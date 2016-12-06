package tk.mybatis.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import tk.mybatis.springboot.model.User;
import tk.mybatis.springboot.service.UserService;
import tk.mybatis.springboot.util.JsonResponse;
import tk.mybatis.springboot.util.ResponseUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Yakami
 * on 2016/6/16.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping
    public String index() {
        return "login";
    }

    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    public void verify(String userName, String password, HttpServletRequest request,
                         HttpServletResponse response) {
        User user = userService.getByUserName(userName);
        if (user != null && user.getPassword().equals(password))
            ResponseUtils.renderJson(response, JsonResponse.getJson("result", "OK"));
        else
            ResponseUtils.renderJson(response, JsonResponse.getJson("result", "failed"));
    }
}
