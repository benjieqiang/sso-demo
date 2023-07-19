package com.ben.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @Author: benjieqiang
 * @CreateTime: 2023-07-19  16:15
 * @Description: 单点登录的服务端
 * @Version: 1.0
 */
@Controller
public class LoginController {

    @Autowired
    StringRedisTemplate redisTemplate;



    /**
     * @param token:
     * @return String
     * @description 根据token获取用户信息
     * @author benjieqiang
     * @date 2023/7/19 5:34 PM
     */
    @ResponseBody
    @GetMapping("/userInfo")
    public String getUserInfo(@RequestParam("token") String token) {
        String s = redisTemplate.opsForValue().get(token);
        return s;
    }


    /**
     * @param :
     * @return String
     * @description 登录页面
     * @author benjieqiang
     * @date 2023/7/19 4:28 PM
     */
    @GetMapping("/login.html")
    public String login(@RequestParam(value = "redirect_url", required = false) String url, Model model,
                        @CookieValue(value= "sso_token", required = false) String sso_token) {
        if (!StringUtils.isEmpty(sso_token)) {
            // 如果不为空，说明之前有人登录过，给浏览器留下了痕迹；
            // 跳转到回调地址；
            return "redirect:" + url + "?token=" + sso_token;
        }

        model.addAttribute("url", url); // 接收重定向传递过来的参数，放到model里面。post请求时直接从页面上拿到要回调的地址
        return "login";
    }

    @PostMapping("/doLogin")
    public String doLogin(String username, String password, String url,
                          HttpServletResponse response) {

        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            // 模拟进行了登录用户和密码的校验，如果校验成功，则跳转回调页面。又重新回到http://127.0.0.1:8081/employees
            // 该页面对应的方法会判断是否登录，即是否有session存在，因为当前回调并没有给redis存数据，所以继续会跳到登录页面，造成死循环问题；
            // 解决方案：带一个token到回调地址，这样在回调地址对应的方法处进行判断，如果带了token则说明是回调回来的，再请求sso服务器获取用户信息；

            String uuid = UUID.randomUUID().toString().replace("-", "");
            // 把用户信息存起来，key是uuid：val是username
            redisTemplate.opsForValue().set(uuid, username);

            // 为了解决跨域登录的问题，如果购物车服务过来登录成功，服务器存一个cookie，返回给客户端
            Cookie cookie = new Cookie("sso_token", uuid);
            response.addCookie(cookie);
            // 重定向到回调页面
            return "redirect:" + url + "?token=" + uuid;
        }
        return "login";
    }
}
