package tk.mybatis.springboot.business;

import com.google.gson.Gson;
import okhttp3.ResponseBody;
import org.joda.time.DateTime;
import rx.Observable;
import rx.schedulers.Schedulers;
import tk.mybatis.springboot.Application;
import tk.mybatis.springboot.ServerAPI;
import tk.mybatis.springboot.bean.DailySales;
import tk.mybatis.springboot.bean.SalesContainer;
import tk.mybatis.springboot.bean.WeeklySales;

import java.nio.charset.Charset;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * Created by Yakami on 16/12/1.
 * 提供oricon数据获取及缓存服务
 */
public class SalesService {
    private static SalesService ourInstance = new SalesService();

    public static final int STATUS_STOPPED = 0; // 已停止服务
    public static final int STATUS_INITING = 1; // 初始化中, 暂不能提供服务
    public static final int STATUS_RUNNING = 2; // 运行中, 可提供服务

    private static int mTimeGapOfBigReconnection = 60 * 2; //sec
    private static int mTimeGapOfReconnection = 2; //sec
    private static int mReconnectedTimes = 10;

    private static final String OTHER_DAILY_DVD_ANIME = "da"; // daily anime dvd
    private static final String OTHER_DAILY_DVD_MOVIE = "mv"; // daily movie dvd

    private static final String[] DAILY = {
            "106103", // daily anime BD
            "106", // daily video BD
            "104", // daily video DVD
            "106101", // daily film BD
            "101", // daily single
            "102", // daily ablum
            "106102", // daily music BD
            "104102", // daily music DVD
    };

    private static final String[] WEEKLY = {
            "116103", // weekly anime BD
            "114103", // weekly anime DVD
            "114103", // weekly anime dvd
            "116", // weekly video BD
            "114", // weekly video DVD
            "111", // weekly single
            "11A", // weekly ablum
            "116102", // weekly music bd
            "114102", // weekly music dvd
            "116101", // weekly movie bd
            "116104", // weekly tv bd
    };

    private HashMap<String, String> mRomanNumConvertedMap = new HashMap<>(); //shift_jis解码时,罗马数字会乱码, 采用map替换

    private int mStatus;

    private HashMap<String, String> mDailySales = new HashMap<>(); // daily data cache
    private HashMap<String, String> mWeeklySales = new HashMap<>();

    private static String[] mUpdateTime = {"17:00", "17:05", "17:15", "19:00", "19:05", "19:15", "20:00", "21:00"}; //JapanTime, 需要更新的时间点
    private Gson gson = new Gson();

    public static SalesService getInstance() {
        return ourInstance;
    }

    private SalesService() {
        // init RomanNumConvertedMap
        mRomanNumConvertedMap.put("�T", "Ⅰ");
        mRomanNumConvertedMap.put("�U", "Ⅱ");
        mRomanNumConvertedMap.put("�V", "Ⅲ");
        mRomanNumConvertedMap.put("�W", "Ⅳ");
        mRomanNumConvertedMap.put("�X", "Ⅴ");
        mRomanNumConvertedMap.put("�Y", "Ⅵ");
        mRomanNumConvertedMap.put("�Z", "Ⅶ");
        mRomanNumConvertedMap.put("�[", "Ⅷ");
        mRomanNumConvertedMap.put("�\\", "Ⅸ");
        mRomanNumConvertedMap.put("�]", "Ⅹ");
    }

    public void run() {
        if (mStatus != STATUS_STOPPED) {
            Application.logger.info("is running");
            return;
        }
        Application.logger.info("init...");
        mStatus = STATUS_INITING;

        Observable.just("")
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(tmp -> {
                    Boolean isFirst = true;
                    while (mStatus != STATUS_STOPPED) {
                        try {
                            getData(mReconnectedTimes);
                            // get updateTimeStr
                            ZonedDateTime japanTime = ZonedDateTime.now(ZoneId.of("Japan"));
                            int timeInt = japanTime.getHour() * 100 + japanTime.getMinute();
                            String updateTimeStr = "";
                            for (int i = 0; i < mUpdateTime.length; i++) {
                                if (timeInt < Integer.valueOf(mUpdateTime[i].replace(":", ""))) {
                                    updateTimeStr = mUpdateTime[i];
                                    break;
                                } else if (i == mUpdateTime.length - 1) {
                                    updateTimeStr = mUpdateTime[0];
                                }
                            }
                            // get updateTime
                            int hourOfUpdateTime = Integer.valueOf(updateTimeStr.split(":")[0]);
                            int minOfUpdateTime = Integer.valueOf(updateTimeStr.split(":")[1]);
                            int minsOfUpdateTime = hourOfUpdateTime * 60 + minOfUpdateTime;
                            int minsOfNow = japanTime.getHour() * 60 + japanTime.getMinute();
                            int minsOfSleep = minsOfNow < minsOfUpdateTime ? (minsOfUpdateTime - minsOfNow) :
                                    24 * 60 - minsOfNow + minsOfUpdateTime;
                            // sleep
                            DateTime now = DateTime.now();
                            DateTime updateTime = now.plusMinutes(minsOfSleep);
                            updateTime = new DateTime(updateTime.getMillis() - updateTime.getMillis() % (60 * 1000)); //获得整数分钟时间
                            Application.logger.info(String.format("wait JapanTime: %s, sleep: %d mins", updateTimeStr, (updateTime.getMillis() - now.getMillis()) / 1000 / 60));
                            Thread.sleep(updateTime.getMillis() - now.getMillis());
                        } catch (Throwable e) {
                            Application.logger.error("had a error in outer");
                            Application.logger.error(e.toString());
                        }
                    }
                });
    }

    public void stop() {
        mStatus = STATUS_STOPPED;
        Application.logger.info("stopped");
    }

    private void getData(final int reconnectTimes) {
        ServerAPI.withPermission()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(Void -> {
                    updateSales(false);
                    updateSales(true);
                }, throwable -> {
                    Application.logger.error(String.format("getData failed, error: %s, reconnectedTimes: %d",
                            throwable.toString(), reconnectTimes));
                    int reconnect = reconnectTimes - 1;
                    if (reconnect != 0) {
                        try {
                            TimeUnit.SECONDS.sleep(reconnect < 5 ? mTimeGapOfBigReconnection : mTimeGapOfReconnection +
                                    new Random().nextInt(5));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        getData(reconnect);
                    }

                });
    }

    private String getStrFromShiftJIS(ResponseBody body) {
        try {
            String result = body.source().readString(Charset.forName("Shift_JIS"));
            for (String key : mRomanNumConvertedMap.keySet()) {
                if (result.contains(key)) {
                    result = result.replaceAll(key, mRomanNumConvertedMap.get(key));
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void saveDailySalesCache(String flag, String html) {
        SalesContainer<DailySales> container = new SalesContainer<>();
        container.setList((SalesTableService.getInstance().getDailySalesList(html)));
        container.setUpdateTime(SalesTableService.getInstance().getUpdateTime(html, true));
        container.setNextUpdateTime(SalesTableService.getInstance().getNextUpdateTime(container.getUpdateTime(), true));
        if (mDailySales.containsKey(flag)) {
            mDailySales.replace(flag, gson.toJson(container));
        } else {
            mDailySales.put(flag, gson.toJson(container));
        }
    }

    private void saveWeeklySalesCache(String flag, String html) {
        SalesContainer<WeeklySales> container = new SalesContainer<>();
        container.setList((SalesTableService.getInstance().getWeeklySalesList(html)));
        container.setUpdateTime(SalesTableService.getInstance().getUpdateTime(html, false));
        container.setNextUpdateTime(SalesTableService.getInstance().getNextUpdateTime(container.getUpdateTime(), false));
        if (mWeeklySales.containsKey(flag)) {
            mWeeklySales.replace(flag, gson.toJson(container));
        } else {
            mWeeklySales.put(flag, gson.toJson(container));
        }
    }

    private void saveOtherDVDSalesCache(String type, String[] pages) {
        SalesContainer<DailySales> container = new SalesContainer<>();
        for (String html : pages) {
            container.getList().addAll(OtherDVDTableService.getInstance().getOtherDVDDailySales(html));
        }
        container.setUpdateTime(OtherDVDTableService.getInstance().getUpdateTime());
        container.setNextUpdateTime(OtherDVDTableService.getInstance().getNextUpdateTime(container.getUpdateTime()));
        if (mDailySales.containsKey(type)) {
            mDailySales.replace(type, gson.toJson(container));
        } else {
            mDailySales.put(type, gson.toJson(container));
        }
    }

    private void updateSalesByFlags(String flag, boolean isDaily, final int reconnectTimes) {
        Observable<ResponseBody> observable = isDaily ? ServerAPI.getOriconAPI().getDailySales(flag) :
                ServerAPI.getOriconAPI().getWeeklySales(flag);

        observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.computation())
                .subscribe(body -> {
                    try {
                        String str = getStrFromShiftJIS(body);
                        if (isDaily) {
                            saveDailySalesCache(flag, str);
                        } else {
                            saveWeeklySalesCache(flag, str);
                        }
                        Application.logger.info((isDaily ? "Daily " : "Weekly ") + flag + " updated");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, throwable -> {
                    Application.logger.error(String.format("Daily %s update failed, error: %s, reconnectedTimes: %d", flag,
                            throwable.toString(), reconnectTimes));
                    int reconnect = reconnectTimes - 1;
                    if (reconnect != 0) {
                        try {
                            TimeUnit.SECONDS.sleep(reconnect < 5 ? mTimeGapOfBigReconnection : mTimeGapOfReconnection +
                                    new Random().nextInt(5));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        updateSalesByFlags(flag, isDaily, reconnect);
                    }

                });
    }

    private void updateOtherDVD(String type) {
        ZonedDateTime japanTime = (ZonedDateTime.now(ZoneId.of("Japan"))).minusDays(1);
        japanTime = japanTime.minusDays(1);
        String date = String.format("%d-%2d-%2d", japanTime.getYear(), japanTime.getMonthValue(), japanTime.getDayOfMonth());
        String p1 = "http://www.oricon.co.jp/rank/" + type + "/d/" + date + "/";
        String p2 = "http://www.oricon.co.jp/rank/" + type + "/d/" + date + "/p/2/";
        String[] htmlArr = {"", ""};
        Observable.just(p1, p2)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(url -> {
                    ServerAPI.getOriconAPI()
                            .getCutomUrl(url)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(Schedulers.computation())
                            .subscribe(body -> {
                                try {
                                    String str = getStrFromShiftJIS(body);
                                    htmlArr[url.equals(p1) ? 0 : 1] = str;
                                    if (!htmlArr[0].equals("") && !htmlArr[1].equals("")) {
                                        saveOtherDVDSalesCache(type, htmlArr);
                                        Application.logger.info("daily " + type + " updated");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }, Throwable::printStackTrace);
                }, Throwable::printStackTrace);

    }

    private void updateSales(boolean isDaily) {
        Observable.from(isDaily ? DAILY : WEEKLY)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(flag -> {
                    updateSalesByFlags(flag, isDaily, mReconnectedTimes);
                }, Throwable::printStackTrace);

        if (isDaily) {
            updateOtherDVD(OTHER_DAILY_DVD_ANIME);
            updateOtherDVD(OTHER_DAILY_DVD_MOVIE);
        }
    }

    public String getDaily(String flag) {
        if (mDailySales.containsKey(flag))
            return mDailySales.get(flag);
        else
            return "";
    }

    public String getWeekly(String flag) {
        if (mWeeklySales.containsKey(flag))
            return mWeeklySales.get(flag);
        else
            return "";
    }

    public int getStatus() {
        return mStatus;
    }

}
