package com.ben.cart.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: benjieqiang
 * @CreateTime: 2023-07-19  15:34
 * @Description: TODO
 * @Version: 1.0
 */

@Controller
public class HelloController {

    @Value("${sso.server.url}")
    String ssoServerUrl;

    @Value("${sso.cart.url}")
    String cartUrl;

    @GetMapping("/employees")
    public String employees(Model model, HttpSession session,
                            @RequestParam(value = "token", required = false) String token) {

        // 这里简化了，按道理是要从redis中查询一个token和接收到的token进行对比
        if (!StringUtils.isEmpty(token)) {
            // 从sso服务器回调回来的
            // TODO： 去ssoserver获取当前token真正的用户信息
            session.setAttribute("loginUser", "zhangsan");
        }
        Object loginUser = session.getAttribute("loginUser");

        if (loginUser == null) {
            // 未登录，跳转至服务端的登录页面；
            return "redirect:" + ssoServerUrl + "?redirect_url=" + cartUrl;
        }

        // 否则，显示登录状态，比如去数据库查询一些数据展示到前端；
        List<String> emps = new ArrayList<>();
        emps.add("张三");
        emps.add("李四");
        model.addAttribute("emps", emps);

        return "employees";
    }

}
