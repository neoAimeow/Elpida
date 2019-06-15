package com.aimeow.Elpida.entity;

import lombok.Data;

import java.util.Date;

@Data
public class NewsEntity {
    private Date dateTime;
    private String title;
    private String content;
    private String src;
}
