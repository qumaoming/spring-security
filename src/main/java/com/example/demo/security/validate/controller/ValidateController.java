package com.example.demo.security.validate.controller;

import com.example.demo.security.entity.ImageCode;
import com.example.demo.security.validate.imageCode.AuthImageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class ValidateController {

    @Autowired
    private AuthImageBuilder authImageBuilder;

    @GetMapping("/image")
    public void getCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ImageCode imageCode = authImageBuilder.generateVerifyCode(new ServletWebRequest(request));
        request.getSession().setAttribute(request.getSession().getId() + "IMAGE" , imageCode);
        ImageIO.write(imageCode.getImage(), "JPEG", response.getOutputStream());
    }

}
