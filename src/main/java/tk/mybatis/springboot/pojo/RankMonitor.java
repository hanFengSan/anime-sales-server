package tk.mybatis.springboot.pojo;

import com.google.gson.Gson;
import rx.Observable;
import rx.schedulers.Schedulers;
import tk.mybatis.springboot.Application;
import tk.mybatis.springboot.ServerAPI;
import tk.mybatis.springboot.bean.DiscRank;
import tk.mybatis.springboot.bean.ServerResponse;
import tk.mybatis.springboot.util.AppPush;
import tk.mybatis.springboot.util.NumFlagBitUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yakami
 * on 2016/8/3.
 */
public class RankMonitor {

    private static RankMonitor ourInstance = new RankMonitor();

    private DiscRankContainer mContainer = new DiscRankContainer();

    private static int mTimeGap = 60 * 2; //sec

    private static int mCheckTime = 0;

    private boolean runningEnabled;

    public static RankMonitor getInstance() {
        return ourInstance;
    }

    private RankMonitor() {
    }

    public void run() {
        if (runningEnabled) {
            Application.logger.info("已经在运行");
            return;
        }
        Application.logger.info("开始运行");
        runningEnabled = true;
        Observable.just("")
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(tmp -> {
                    while (runningEnabled) {
                        try {
                            Application.logger.info(String.format("第%04d次check", mCheckTime++));
                            checkData();
                            Application.logger.info(String.format("开始睡眠，时长: %s秒", mTimeGap));
                            Thread.sleep(mTimeGap * 1000);
                            Application.logger.info(String.format("睡眠结束，时长: %s秒", mTimeGap));
                        } catch (Throwable e) {
                            Application.logger.error("循环里错误");
                            Application.logger.error(e.toString());
                        }
                    }
                }, throwable -> {
                    Application.logger.error("run第一层错误");
                    Application.logger.error(throwable.toString());
                });
    }

    public void stop() {
        runningEnabled = false;
        Application.logger.info("停止了运行");
    }

    public static RankMonitor getOurInstance() {
        return ourInstance;
    }

    public static void setOurInstance(RankMonitor ourInstance) {
        RankMonitor.ourInstance = ourInstance;
    }

    public DiscRankContainer getmContainer() {
        return mContainer;
    }

    public void setmContainer(DiscRankContainer mContainer) {
        this.mContainer = mContainer;
    }

    public boolean isRunningEnabled() {
        return runningEnabled;
    }

    public void setRunningEnabled(boolean runningEnabled) {
        this.runningEnabled = runningEnabled;
    }

    private void checkData() {
        ServerAPI.getSakuraAPI()
                .getData()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(container -> {
                    DiscRankContainer newContainer = new DiscRankContainer(container);
                    List<DiscRank> list = mContainer.updateList(newContainer);
                    if (list.size() != 0) {
                        Application.logger.info("结果列表里有" + list.size() + "项是新的");
                        pushMessage(list);
                    } else
                        Application.logger.info("结果列表里没有新项");
                }, throwable -> {
                    Application.logger.error("网络或处理错误");
                    Application.logger.error(throwable.toString());
                });
    }

    private List<String> getTagList(List<DiscRank> list) {
        List<String> result = new ArrayList<>();
        for (DiscRank item : list)
            result.add(String.valueOf(item.getId()));
        return result;
    }

    private List<Integer> getIdList(List<DiscRank> list) {
        List<Integer> result = new ArrayList<>();
        for (DiscRank item : list) {
            result.add(item.getId());
        }
        return result;
    }

    private void pushMessage(List<DiscRank> list) {
        ServerResponse response = new ServerResponse();
        String tmp = NumFlagBitUtils.toFlagBit(getIdList(list));
        response.setCode(1);
        response.setMessage("update");
        response.setData(tmp);
        Gson gson = new Gson();
        String json = gson.toJson(response);
        if (json.length() < 2000) {
            AppPush.pushTransmission(json);
            Application.logger.info("发送了一条update消息");
        } else {
            ServerResponse response2 = new ServerResponse();
            response2.setCode(2);
            response2.setMessage("all_update");
            response2.setData("");
            String json2 = gson.toJson(response2);
            AppPush.pushTransmission(json2);
            Application.logger.info("发送了一条all_update消息");
        }
    }

    public static int getmTimeGap() {
        return mTimeGap;
    }

    public static void setmTimeGap(int mTimeGap) {
        RankMonitor.mTimeGap = mTimeGap;
    }

    public static int getmCheckTime() {
        return mCheckTime;
    }

    public static void setmCheckTime(int mCheckTime) {
        RankMonitor.mCheckTime = mCheckTime;
    }
}
