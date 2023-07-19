package com.ben.member.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
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

    @Value("${sso.member.url}")
    String bossUrl;

    @GetMapping("/boss")
    public String boss(Model model, HttpSession session,
                            @RequestParam(value = "token", required = false) String token) {

        if (!StringUtils.isEmpty(token)) {
            // 从sso服务器回调回来的
            // 去ssoserver获取当前token真正的用户信息
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> forEntity = restTemplate.getForEntity("http://127.0.0.1:8080/userInfo?token=" + token, String.class);
            String body = forEntity.getBody();
            session.setAttribute("loginUser", body);
//            session.setAttribute("loginUser", "哈哈");
        }
        System.out.println(session);
        Object loginUser = session.getAttribute("loginUser");

        if (loginUser == null) {
            // 未登录，跳转至服务端的登录页面；
            return "redirect:" + ssoServerUrl + "?redirect_url=" + bossUrl;
        }

        // 否则，显示登录状态，比如去数据库查询一些数据展示到前端；
        List<String> emps = new ArrayList<>();
        emps.add("张三");
        emps.add("李四");
        model.addAttribute("emps", emps);

        return "boss";
    }

}
