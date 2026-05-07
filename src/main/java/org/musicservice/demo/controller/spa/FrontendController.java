package org.musicservice.demo.controller.spa;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping({
            "/admin",
            "/main",
            "/auth/**",
            "/album/**",
            "/artist/**",
            "/sound/**",
            "/search/**",
            "/collection/**",
            "/genre/**"
    })
    public String forward(){
        return "forward:/index.html";
    }
}
