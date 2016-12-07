package tk.mybatis.springboot.business;

import com.google.gson.Gson;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.schedulers.Schedulers;
import tk.mybatis.springboot.Application;
import tk.mybatis.springboot.ServerAPI;
import tk.mybatis.springboot.bean.DailySales;
import tk.mybatis.springboot.bean.ListContainer;
import tk.mybatis.springboot.bean.WeeklySales;

import java.nio.charset.Charset;
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

    private static int mTimeGapOfUpdate = 60 * 2; //sec
    private static int mTimeGapOfReconnect = 2; //sec
    private static int mCheckTime = 0;
    private static int mReconnectTimes = 5;

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
            "116", // weekly video BD
            "114", // weekly video DVD
            "111", // weekly single
            "11A", // weekly ablum
    };

    private int mStatus;

    private HashMap<String, String> mDailySales = new HashMap<>(); // daily data cache
    private HashMap<String, String> mWeeklySales = new HashMap<>(); // 数据缓存

    private Gson gson = new Gson();

    public static SalesService getInstance() {
        return ourInstance;
    }

    private SalesService() {
    }

    public void run() {
        if (mStatus != STATUS_STOPPED) {
            Application.logger.info("已经在运行");
            return;
        }
        Application.logger.info("初始化...");
        mStatus = STATUS_INITING;

        getData();
    }

    public void stop() {
        mStatus = STATUS_STOPPED;
        Application.logger.info("停止了运行");
    }

    private void getData() {
        ServerAPI.withPermission()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(Void -> {
                    updateSales(false);
                    updateSales(true);
                }, Throwable::printStackTrace);
    }

    private void saveDailySalesCache(String flag, String html) {
        ListContainer<DailySales> container = new ListContainer<>();
        container.list = (SalesTableService.getInstance().getDailySalesList(html));
        if (mDailySales.containsKey(flag)) {
            mDailySales.replace(flag, gson.toJson(container));
        } else {
            mDailySales.put(flag, gson.toJson(container));
        }
    }

    private void saveWeeklySalesCache(String flag, String html) {
        ListContainer<WeeklySales> container = new ListContainer<>();
        container.list = (SalesTableService.getInstance().getWeeklySalesList(html));
        if (mWeeklySales.containsKey(flag)) {
            mWeeklySales.replace(flag, gson.toJson(container));
        } else {
            mWeeklySales.put(flag, gson.toJson(container));
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
                        String str = body.source().readString(Charset.forName("Shift_JIS"));
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
                    Application.logger.error(String.format("Daily %s update failed, error: %s, reconnetTimes: %d", flag,
                            throwable.toString(), reconnectTimes));
                    int reconnect = reconnectTimes - 1;
                    try {
                        TimeUnit.SECONDS.sleep(mTimeGapOfReconnect + new Random().nextInt(5));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    updateSalesByFlags(flag, isDaily, reconnect);
                });
    }

    private void updateSales(boolean isDaily) {
        Observable.from(isDaily ? DAILY : WEEKLY)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(flag -> {
                    updateSalesByFlags(flag, isDaily, mReconnectTimes);
                }, Throwable::printStackTrace);
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
