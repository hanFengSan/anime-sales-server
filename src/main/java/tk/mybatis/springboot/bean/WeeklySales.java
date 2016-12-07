package tk.mybatis.springboot.bean;

import tk.mybatis.springboot.bean.base.Entity;

import java.util.List;

/**
 * Created by Yakami on 16/12/7.
 * Emm...
 */
public class WeeklySales extends Entity {
    private int rank;
    private int preRank;
    private String title;
    private int weeklySales;
    private int sales;
    private String releaseDate;
    private String publisher;
    private String artist;
    private int topRank;
    private int times;

    public WeeklySales() {}

    public WeeklySales(List<String> list) {
        if (list.size() == 11) {  // 音乐相关
            list.remove(2); // 移除"期待作品"列, 和其他周榜的格式统一
        }
        rank = Integer.valueOf(list.get(0).replace(",", ""));
        if (list.get(1).equals("-")) {
            preRank = -1;
        } else {
            preRank = Integer.valueOf(list.get(1).replace(",", ""));
        }
        title = list.get(2);
        weeklySales = Integer.valueOf(list.get(3).replace(",", ""));
        sales = Integer.valueOf(list.get(4).replace(",", ""));
        releaseDate = list.get(5);
        publisher = list.get(6);
        artist = list.get(7);
        topRank = Integer.valueOf(list.get(8).replace(",", ""));
        times = Integer.valueOf(list.get(9).replace(",", ""));
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getPreRank() {
        return preRank;
    }

    public void setPreRank(int preRank) {
        this.preRank = preRank;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getWeeklySales() {
        return weeklySales;
    }

    public void setWeeklySales(int weeklySales) {
        this.weeklySales = weeklySales;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getTopRank() {
        return topRank;
    }

    public void setTopRank(int topRank) {
        this.topRank = topRank;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }


}
