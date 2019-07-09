package com.aimeow.Elpida.entity.joinQuant;

import lombok.Data;

import java.util.Date;

@Data
public class JoinQuantSecurityEntity {
    private String stockCode;
    private String displayName;
    private String type;
    private Date startDate;
    private Date endDate;
}
