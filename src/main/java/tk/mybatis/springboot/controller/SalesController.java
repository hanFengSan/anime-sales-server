package tk.mybatis.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tk.mybatis.springboot.business.SalesService;
import tk.mybatis.springboot.util.JsonResponse;
import tk.mybatis.springboot.util.ResponseUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Yakami on 16/12/6.
 * Emm...
 */
@Controller
@RequestMapping("/sales")
public class SalesController {

    @RequestMapping
    public void index(HttpServletRequest request,
                      HttpServletResponse response) {
        ResponseUtils.renderJson(response, JsonResponse.getJson("result", "OK"));
    }

    @RequestMapping(path="/daily", params = {"flag"})
    public void getDaily(HttpServletRequest request,
                        HttpServletResponse response,
                        @RequestParam(value = "flag") String flag) {
        ResponseUtils.renderJson(response, SalesService.getInstance().getDaily(flag));
    }

    @RequestMapping(path="/weekly", params = {"flag"})
    public void getWeekly(HttpServletRequest request,
                         HttpServletResponse response,
                         @RequestParam(value = "flag") String flag) {
        ResponseUtils.renderJson(response, SalesService.getInstance().getWeekly(flag));
    }
}
