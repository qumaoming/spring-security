package com.example.demo.security.entity;

import lombok.Getter;
import lombok.Setter;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

import static java.time.LocalDateTime.*;

@Getter
@Setter
public class ImageCode {

    private BufferedImage image;

    private String code;

    private LocalDateTime expireTime;

    /**
     * 是否过期
     */
    public boolean isExpried(){
        return LocalDateTime.now().isAfter(expireTime);
    }

    public ImageCode(BufferedImage image, String code, int expireIn) {
        this.image = image;
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
    }

    public ImageCode(BufferedImage image, String code, LocalDateTime expireTime) {
        this.image = image;
        this.code = code;
        this.expireTime = expireTime;
    }
}
