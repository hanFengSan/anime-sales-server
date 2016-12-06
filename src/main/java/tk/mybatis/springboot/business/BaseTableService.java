package tk.mybatis.springboot.business;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yakami on 16/12/5.
 * 提供表格html标签解析为可用数据服务
 */
public class BaseTableService {
    private static BaseTableService mBaseTableService = new BaseTableService();

    public static BaseTableService getInstance() {
        return mBaseTableService;
    }

    private BaseTableService() {
    }


    public Element getTableFromHtml(String html) {
        return Jsoup.parse(html).getElementsByAttributeValue("bgcolor", "#C1C1C1").get(0);
    }

    public List<List<Element>> getRows(Element table) {
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

    public List<String> deconstructRow(List<Element> row) {
        List<String> result = new ArrayList<>();
        row.forEach(item -> item.children().forEach(td -> result.add(td.text())));
        return result;
    }
}
