package tk.mybatis.springboot.business;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import tk.mybatis.springboot.bean.DailySales;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yakami on 16/12/20.
 * Emm...
 */
public class OtherDVDTableService {
    private static OtherDVDTableService ourInstance = new OtherDVDTableService();

    public static OtherDVDTableService getInstance() {
        return ourInstance;
    }

    private OtherDVDTableService() {
    }

    protected Elements getItems(String html) {
        return Jsoup.parse(html).getElementsByClass("box-rank-entry");
    }

    protected DailySales getDataByItem(Element row) {
        DailySales sales = new DailySales();
        // get rank
        sales.setRank(Integer.valueOf(row.getElementsByClass("num").get(0).text()));
        // get title
        sales.setTitle(row.getElementsByClass("title").get(0).text());
        // get date
        Elements list = row.getElementsByClass("list").get(0).children();
        String date = list.get(0).text().replaceAll("[(\\n)(\\t)(発売日：)]", "").replaceAll("[年月]", "/");
        sales.setReleaseDate(date.substring(3, date.length() - 1));
        // get publisher
        sales.setPublisher(list.get(1).text());
        // get artist
        sales.setArtist(row.getElementsByClass("name").get(0).text());
        return sales;
    }


    public long getUpdateTime() {
        ZonedDateTime japanTime = ZonedDateTime.now(ZoneId.of("Japan"));
        japanTime = japanTime.minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String dateStr = String.format("%d/%02d/%02d", japanTime.getYear(), japanTime.getMonthValue(), japanTime.getDayOfMonth());
        LocalDate date = LocalDate.parse(dateStr, formatter);
        LocalDateTime ldt = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 19, 0, 0);
        ZonedDateTime zdt = ldt.atZone(ZoneId.of("Japan"));
        return zdt.toInstant().toEpochMilli();
    }

    public long getNextUpdateTime(long time) {
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.of("Japan"));
        date = date.plusDays(1);
        LocalDateTime ldt = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 19, 0, 0);
        ZonedDateTime zdt = ldt.atZone(ZoneId.of("Japan"));
        return zdt.toInstant().toEpochMilli();
    }

    public List<DailySales> getOtherDVDDailySales(String html) {
        List<DailySales> result = new ArrayList<>();
        Elements elements = getItems(html);
        for (Element item : elements) {
            result.add(getDataByItem(item));
        }
        return result;
    }
}