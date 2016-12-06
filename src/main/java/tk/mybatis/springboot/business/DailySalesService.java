package tk.mybatis.springboot.business;

import org.jsoup.nodes.Element;
import rx.schedulers.Schedulers;
import tk.mybatis.springboot.Application;
import tk.mybatis.springboot.ServerAPI;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by Yakami on 16/12/1.
 */
public class DailySalesService {
    private static DailySalesService ourInstance = new DailySalesService();
    private boolean runningEnabled;
    private static int mTimeGap = 60 * 2; //sec
    private static int mCheckTime = 0;

    public static DailySalesService getInstance() {
        return ourInstance;
    }

    private DailySalesService() {
    }

    public void run() {
        if (runningEnabled) {
            Application.logger.info("已经在运行");
            return;
        }
        Application.logger.info("开始运行");
        runningEnabled = true;

        getHTML();

//        Observable.just("")
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.newThread())
//                .subscribe(tmp -> {
//                    while (runningEnabled) {
//                        try {
//                            Application.logger.info(String.format("第%04d次check", mCheckTime++));
//                            checkData();
//                            Application.logger.info(String.format("开始睡眠，时长: %s秒", mTimeGap));
//                            Thread.sleep(mTimeGap * 1000);
//                            Application.logger.info(String.format("睡眠结束，时长: %s秒", mTimeGap));
//                        } catch (Throwable e) {
//                            Application.logger.error("循环里错误");
//                            Application.logger.error(e.toString());
//                        }
//                    }
//                }, throwable -> {
//                    Application.logger.error("run第一层错误");
//                    Application.logger.error(throwable.toString());
//                });
    }

    public void stop() {
        runningEnabled = false;
        Application.logger.info("停止了运行");
    }

    private void getHTML() {
        ServerAPI.withPermission()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(Void -> {
                    getDailyAnimeBDRank();
                }, Throwable::printStackTrace);
    }

    private void getDailyAnimeBDRank() {
        ServerAPI.getOriconAPI()
                .getWeeklyAnimeBDRank()
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.computation())
                .subscribe(body -> {
                    try {
                        String str = body.source().readString(Charset.forName("Shift_JIS"));
                        Element table = BaseTableService.getInstance().getTableFromHtml(str);
                        List<List<Element>> tmp = BaseTableService.getInstance().getRows(table);
                        tmp.forEach(BaseTableService.getInstance()::deconstructRow);
                        Application.logger.info(str);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
    }

//    private void login() {
//        ServerAPI.getOriconAPI()
//                .login(ServerAPI.oriconID, ServerAPI.oriconPW)
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.computation())
//                .subscribe(Void -> {
//                    Application.logger.info("登录失败");
//                }, throwable -> {
//                    Application.logger.info(throwable.toString());
//                });
//
//        ServerAPI.isLoggedIn()
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.computation())
//                .subscribe(isLoggedIn -> {
//                    if (isLoggedIn)
//                        Application.logger.info("已登录");
//                    else
//                        Application.logger.info("未登录");
//                }, Throwable::printStackTrace);
//
//    }

//    private void checkData() {
//        ServerAPI.getSakuraAPI()
//                .getData()
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.computation())
//                .subscribe(container -> {
//                    DiscRankContainer newContainer = new DiscRankContainer(container);
//                    List<DiscRank> list = mContainer.updateList(newContainer);
//                    if (list.size() != 0) {
//                        Application.logger.info("结果列表里有" + list.size() + "项是新的");
//                        pushMessage(list);
//                    } else
//                        Application.logger.info("结果列表里没有新项");
//                }, throwable -> {
//                    Application.logger.error("网络或处理错误");
//                    Application.logger.error(throwable.toString());
//                });
//    }

}
