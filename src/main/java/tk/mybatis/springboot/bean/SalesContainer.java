package tk.mybatis.springboot.bean;

import tk.mybatis.springboot.bean.base.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yakami on 16/12/17.
 * Emm...
 */
public class SalesContainer<T extends Entity> {
    private List<T> list = new ArrayList<T>();
    private long updateTime;
    private long nextUpdateTime;
    private long CNUpdateTime;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
        this.setCNUpdateTime(updateTime + 60 * 60 * 1000 * 24);
    }

    public long getNextUpdateTime() {
        return nextUpdateTime;
    }

    public void setNextUpdateTime(long nextUpdateTime) {
        this.nextUpdateTime = nextUpdateTime;
    }

    public long getCNUpdateTime() {
        return CNUpdateTime;
    }

    public void setCNUpdateTime(long CNUpdateTime) {
        this.CNUpdateTime = CNUpdateTime;
    }

    // mode 1: 日榜
    // mode 2: 周榜
    public String toVisualTxt(int mode) {
        String result = "";
        switch (mode) {
            case 1:
                for (T item : list) {
                    DailySales i = (DailySales) item;
                    result += "排名: " + i.getRank() + "\n";
                    result += "标题: " + i.getTitle() + "\n";
                    result += "艺术家: " + i.getArtist() + "\n";
                    result += "发售日期: " + i.getReleaseDate() + "\n";
                    result += "发行方: " + i.getPublisher() + "\n";
                    result += "--------------------------------------------------------------------------------\n";
                }
                return result;
            case 2:
                for (T item : list) {
                    WeeklySales i = (WeeklySales) item;
                    result += "排名: " + i.getRank() + "\n";
                    result += "标题: " + i.getTitle() + "\n";
                    result += "周销量: " + i.getWeeklySales() + "\n";
                    result += "销量: " + i.getSales() + "\n";
                    result += "艺术家: " + i.getArtist() + "\n";
                    result += "发售日期: " + i.getReleaseDate() + "\n";
                    result += "发行方: " + i.getPublisher() + "\n";
                    result += "最高排名: " + i.getTopRank() + "\n";
                    result += "上榜次数: " + i.getTimes() + "\n";
                    result += "--------------------------------------------------------------------------------\n";
                }
                return result;
            default:
                return result;
        }
    }
}
