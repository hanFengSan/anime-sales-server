package tk.mybatis.springboot.business;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import tk.mybatis.springboot.bean.DailySales;
import tk.mybatis.springboot.bean.WeeklySales;

import java.text.Normalizer;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Yakami on 16/12/5.
 * 提供表格html标签解析为可用数据服务
 */
public class SalesTableService {
    private static SalesTableService mTableService = new SalesTableService();

    public static SalesTableService getInstance() {
        return mTableService;
    }

    private SalesTableService() {
    }


    protected Element getTableFromHtml(String html) {
        return Jsoup.parse(html).getElementsByAttributeValue("bgcolor", "#C1C1C1").get(0);
    }

    protected List<List<Element>> getRows(Element table) {
        List<List<Element>> result = new ArrayList<>();
        List<Element> stack = new ArrayList<>();
        String preColor = "";
        for (Element item : table.child(0).children()) {
            if (!preColor.equals("") && !preColor.equals(item.attr("bgcolor"))) {
                result.add(stack);
                stack = new ArrayList<>();
            }
            stack.add(item);
            preColor = item.attr("bgcolor");
        }
        result.add(stack);
        return result;
    }

    protected List<String> deconstructRow(List<Element> row) {
        List<String> result = new ArrayList<>();
        row.forEach(item -> item.children().forEach(td -> result.add(Normalizer.normalize(td.text(), Normalizer.Form.NFKC))));
        return result;
    }

    protected List<List<String>> deconstruct(String html) {
        Element table = getTableFromHtml(html);
        List<List<Element>> rows = getRows(table);
        List<List<String>> result = new ArrayList<>();
        rows.forEach(row -> result.add(deconstructRow(row)));
        result.remove(0);
        return result;
    }

    public long getUpdateTime(String html, boolean isDaily) {
        Pattern pattern = Pattern.compile("<span class=\"ranking\">.*?付</span>");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            String time = matcher.group(0);
            time = time.replace("<span class=\"ranking\">", "");
            time = time.replace("付</span>", "");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDate date = LocalDate.parse(time, formatter);
            LocalDateTime ldt = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), isDaily ? 19 : 17, 0, 0);
            ZonedDateTime zdt = ldt.atZone(ZoneId.of("Japan"));
            return zdt.toInstant().toEpochMilli();
        }
        return 0;
    }

    public long getNextUpdateTime(long time, boolean isDaily) {
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.of("Japan"));
        date = date.plusDays(isDaily ? 2 : 0);
        LocalDateTime ldt = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), isDaily ? 19 : 17, 0, 0);
        ZonedDateTime zdt = ldt.atZone(ZoneId.of("Japan"));
        return zdt.toInstant().toEpochMilli();
    }

    public List<DailySales> getDailySalesList(String html) {
        List<DailySales> result = new ArrayList<>();
        deconstruct(html).forEach(item -> result.add(new DailySales(item)));
        return result;
    }

    public List<WeeklySales> getWeeklySalesList(String html) {
        List<WeeklySales> result = new ArrayList<>();
        deconstruct(html).forEach(item -> result.add(new WeeklySales(item)));
        return result;
    }
}
