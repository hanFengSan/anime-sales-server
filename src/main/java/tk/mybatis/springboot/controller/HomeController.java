package tk.mybatis.springboot.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import tk.mybatis.springboot.model.Matches;
import tk.mybatis.springboot.model.User;
import tk.mybatis.springboot.service.MatchesService;
import tk.mybatis.springboot.service.UserService;
import tk.mybatis.springboot.util.AppPush;
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
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private MatchesService matchService;

    @Autowired
    private UserService userService;

    @RequestMapping
    public void index(HttpServletRequest request,
                        HttpServletResponse response) {
        ResponseUtils.renderJson(response, JsonResponse.getJson("result", "OK"));
    }

    /**
     * 添加预约信息
     *
     * @param userId
     * @param date
     * @param answer
     * @param orderType
     * @param orderRemark
     * @param request
     * @param response
     */
    @RequestMapping("/add")
    public void add(int userId, String date, String answer, String orderType, String orderRemark, HttpServletRequest request,
                    HttpServletResponse response) {
        Matches match = new Matches();
        match.setUserId(userId);
        match.setTime(date + " " + answer);
        match.setType(orderType);
        match.setRemark(orderRemark);
        match.setOrder(true);
        matchService.save(match);
        ResponseUtils.renderJson(response, JsonResponse.getJson("result", "OK"));
    }

    /**
     * 获取预约信息
     *
     * @param request
     * @param response
     */
    @RequestMapping("/data")
    public void getData(HttpServletRequest request,
                        HttpServletResponse response) {
        List<Matches> list = matchService.getAll();
        List<JSONObject> dataList = new ArrayList<>();

        for (Matches item : list) {
            User user = userService.getById(item.getUserId());
            JSONObject object = new JSONObject();
            object.put("user", user.getUserName());
            object.put("time", item.getTime());
            object.put("type", item.getType());
            object.put("remark", item.getRemark());
            dataList.add(object);
        }
        List<JSONObject> rootList = new ArrayList<>();
        JSONObject tmp = new JSONObject();
        tmp.put("name", "全部预约");
        tmp.put("list", dataList);
        rootList.add(tmp);
        JSONObject tmp2 = new JSONObject();
        tmp2.put("name", "七月预约");
        tmp2.put("list", null);
        rootList.add(tmp2);
        JSONObject tmp3 = new JSONObject();
        tmp3.put("name", "八月预约");
        tmp3.put("list", null);
        rootList.add(tmp3);
        JSONObject tmp4 = new JSONObject();
        tmp4.put("name", "其他预约");
        tmp4.put("list", null);
        rootList.add(tmp4);
        ResponseUtils.renderJson(response, JsonResponse.getJson("list", rootList));
    }

    @RequestMapping("/push")
    public void push(HttpServletRequest request,
                     HttpServletResponse response) {
        AppPush.pushTransmission("sfsdf");
        ResponseUtils.renderJson(response, JsonResponse.getJson("result", "OK"));
    }

//
//    @RequestMapping("/setTimeGap")
//    public void stop(HttpServletRequest request,
//                     HttpServletResponse response,
//                     ) {
//        RankMonitor.setmTimeGap();
//        ResponseUtils.renderJson(response, JsonResponse.getJson("result", "stop"));
//    }
}
