package com.javalord.MyPhotoAppApiUsers.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/users")
public class UsersController {

    @GetMapping(value = "/status/check")
    public String status() {

        return "Working...";
    }
}
