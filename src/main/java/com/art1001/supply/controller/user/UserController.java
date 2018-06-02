package com.art1001.supply.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/login.html")
    public String login() {
        return "login";
    }
}
