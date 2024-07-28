package com.mycompany.matdongsan.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class Notice {
   private int nnumber;
   private String ntitle;
   private String ncontent;
   private Date ndate;
}

