package tk.mybatis.springboot.bean;

import tk.mybatis.springboot.bean.base.Entity;

import java.util.List;

/**
 * Created by Yakami on 16/12/6.
 * 日榜单泛用
 */
public class DailySales extends Entity {
    private int rank;
    private int sales = -1;
    private String title;
    private String artist;
    private String releaseDate;
    private String publisher;

    public DailySales() {
    }

    public DailySales(List<String> list) {
        this.rank = Integer.valueOf(list.get(0));
        if (list.size() == 5) { // normal
            this.title = list.get(1);
            this.publisher = list.get(2);
            this.releaseDate = list.get(3);
            this.artist = list.get(4);
        } else if (list.size() == 6) {
            if (list.get(1).equals("-")) {
                this.sales = 0;
            } else {
                this.sales = Integer.valueOf(list.get(1).replace(",", ""));
            }
            this.title = list.get(2);
            this.publisher = list.get(3);
            this.releaseDate = list.get(4);
            this.artist = list.get(5);
        }
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
