package com.example.stomp.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "/home";
    }

    @GetMapping("/chat-room/{roomId}")
    public String chatRoom(@PathVariable("roomId") String roomId, Model model) {

        model.addAttribute("roomId", roomId);

        return "/chat_room";
    }

}
