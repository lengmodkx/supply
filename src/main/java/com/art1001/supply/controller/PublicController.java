package com.art1001.supply.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("/public")
public class PublicController {

    @GetMapping("mypage.html")
    public String my(){
        return "mypage";
    }

    @GetMapping("calendar.html")
    public String calendar(){
        return "tk-calendar";
    }
}
