package tk.mybatis.springboot.business;

import tk.mybatis.springboot.Application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Yakami on 17/7/4.
 * 销量数据持久化为文件, 并同步到github仓库上
 */
public class SalesFileService {

    protected static HashMap<String, String> mDailyFlag2NameMap = new HashMap<>();
    protected static HashMap<String, String> mWeeklyFlag2NameMap = new HashMap<>();

    private static SalesFileService ourInstance = new SalesFileService();

    protected static String mDestPath = "../mzzb-rank-bottle";

    public static SalesFileService getInstance() {
        return ourInstance;
    }

    private SalesFileService() {
        // 日榜
        mDailyFlag2NameMap.put("106103", "日榜-动画BD");
        mDailyFlag2NameMap.put("106", "日榜-视频BD"); // 疑惑
        mDailyFlag2NameMap.put("104", "日榜-视频DVD");
        mDailyFlag2NameMap.put("106101", "日榜-电影BD");
        mDailyFlag2NameMap.put("101", "日榜-单曲");
        mDailyFlag2NameMap.put("102", "日榜-专辑");
        mDailyFlag2NameMap.put("106102", "日榜-音乐BD");
        mDailyFlag2NameMap.put("104102", "日榜-音乐DVD");
        mDailyFlag2NameMap.put("104102", "日榜-音乐DVD");
        mDailyFlag2NameMap.put("da", "日榜-动画DVD");
        mDailyFlag2NameMap.put("mv", "日榜-电影DVD");
        // 周榜
        mWeeklyFlag2NameMap.put("116103", "周榜-动画BD");
        mWeeklyFlag2NameMap.put("114103", "周榜-动画DVD");
        mWeeklyFlag2NameMap.put("116", "周榜-视频BD");
        mWeeklyFlag2NameMap.put("114", "周榜-视频DVD");
        mWeeklyFlag2NameMap.put("111", "周榜-单曲");
        mWeeklyFlag2NameMap.put("11A", "周榜-专辑");
        mWeeklyFlag2NameMap.put("116102", "周榜-音乐BD");
        mWeeklyFlag2NameMap.put("114102", "周榜-音乐DVD");
        mWeeklyFlag2NameMap.put("116101", "周榜-电影BD");
        mWeeklyFlag2NameMap.put("116104", "周榜-电视剧BD");
    }

    public void save(String flag, String date, String json, String visualTxt) {
        String name = getFileName(date, flag);
        saveAsJson(name, json);
        saveAsVisualData(name, visualTxt);
    }

    protected String getFileName(String date, String flag) {
        if (mDailyFlag2NameMap.containsKey(flag)) {
            return mDestPath + "/日榜/" + date + "/" + date + " " + mDailyFlag2NameMap.get(flag);
        } else if (mWeeklyFlag2NameMap.containsKey(flag)) {
            return mDestPath + "/周榜/" + date + "/" + date + " " + mWeeklyFlag2NameMap.get(flag);
        } else {
            return "null";
        }
    }


    protected void saveAsFile(String fileName, String data) {
        File file = new File(fileName);
        if (file.exists()) {
            Application.logger.info("目标文件已存在! " + fileName + "; 执行覆盖");
            file.delete();
        }
        //判断目标文件所在的目录是否存在
        if (!file.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            if (!file.getParentFile().mkdirs()) {
                Application.logger.error("创建目标文件所在目录失败! " + fileName);
            }
        }
        //创建目标文件
        try {
            if (file.createNewFile()) {
                Application.logger.info("创建文件成功! " + fileName);
            } else {
                Application.logger.error("创建文件失败! " + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Application.logger.error("创建文件错误! " + fileName);
        }
        // 写入文件
        try {
            FileWriter fw = new FileWriter(fileName);
            fw.write(data);
            fw.close();
        } catch (Exception e) {
            Application.logger.error("写入文件错误! " + fileName);
        }

    }

    protected void saveAsJson(String fileName, String json) {
        saveAsFile(fileName + ".json", json);
    }

    protected void saveAsVisualData(String fileName, String visualTxt) {
        String result = String.format("\n%s\n\n", fileName.replace("../rank-bottle", ""));
        result += "--------------------------------------------------------------------------------\n" + visualTxt;
        saveAsFile(fileName + ".txt", result);
    }

    protected void syncGit() {

    }
}
