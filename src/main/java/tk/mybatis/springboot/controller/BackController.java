package tk.mybatis.springboot.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import tk.mybatis.springboot.model.Matches;
import tk.mybatis.springboot.service.MatchesService;
import tk.mybatis.springboot.service.UserService;
import tk.mybatis.springboot.util.JsonResponse;
import tk.mybatis.springboot.util.ResponseUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yakami
 * on 2016/6/16.
 */
@Controller
@RequestMapping("/back")
public class BackController {

    @Autowired
    private MatchesService matchService;

    @Autowired
    private UserService userService;

    @RequestMapping
    public String index() {
        return "back";
    }

    /**
     * 获取预约信息
     * @param request
     * @param response
     */
    @RequestMapping("/order")
    public void getOrder(HttpServletRequest request,
                         HttpServletResponse response) {
        List<JSONObject> list = new ArrayList<>();
        List<Matches> orderes = matchService.getAll();
        for (Matches item : orderes) {
            if (!item.isOrder())
                continue;
            JSONObject tmp = new JSONObject();
            tmp.put("id", item.getId());
            tmp.put("userName", userService.getById(item.getUserId()).getUserName());
            tmp.put("phone", userService.getById(item.getUserId()).getPhone());
            tmp.put("time", item.getTime());
            tmp.put("type", item.getType());
            tmp.put("remark", item.getRemark());
            list.add(tmp);
        }
        ResponseUtils.renderJson(response, JsonResponse.getJson("list", list));
    }

    /**
     * 获取赛程表
     * @param request
     * @param response
     */
    @RequestMapping("/matches")
    public void getMatches(HttpServletRequest request,
                         HttpServletResponse response) {
        List<JSONObject> list = new ArrayList<>();
        List<Matches> matches = matchService.getAll();
        for (Matches item : matches) {
            if (item.isOrder())
                continue;
            JSONObject tmp = new JSONObject();
            tmp.put("id", item.getId());
            tmp.put("userName", userService.getById(item.getUserId()).getUserName());
            tmp.put("phone", userService.getById(item.getUserId()).getPhone());
            tmp.put("time", item.getTime());
            tmp.put("type", item.getType());
            tmp.put("remark", item.getRemark());
            list.add(tmp);
        }
        ResponseUtils.renderJson(response, JsonResponse.getJson("list", list));
    }

    @RequestMapping("/add")
    public void add(int id, HttpServletRequest request,
                    HttpServletResponse response) {
        Matches matches = matchService.getById(id);
        matches.setOrder(false);
        matchService.save(matches);
        ResponseUtils.renderJson(response, JsonResponse.getJson("result", "OK"));
    }

    @RequestMapping("/delete")
    public void delete(int id, HttpServletRequest request,
                    HttpServletResponse response) {
        matchService.deleteById(id);
        ResponseUtils.renderJson(response, JsonResponse.getJson("result", "OK"));
    }

    @RequestMapping("/fix")
    public void fix(int id, String time, String type, String remark, HttpServletRequest request,
                       HttpServletResponse response) {
        Matches matches = matchService.getById(id);
        matches.setTime(time);
        matches.setType(type);
        matches.setRemark(remark);
        matchService.save(matches);
        ResponseUtils.renderJson(response, JsonResponse.getJson("result", "OK"));
    }
}
