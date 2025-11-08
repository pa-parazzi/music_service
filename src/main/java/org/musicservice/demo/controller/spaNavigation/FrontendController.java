package org.musicservice.demo.controller.spaNavigation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping("/album/{id}")
    public String forward(){
        return "forward:/music/index.html";
    }
}
