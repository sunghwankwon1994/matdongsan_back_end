package com.mycompany.matdongsan.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class Report {
   private int rnumber;
   private String rcontent;
   private Date rdate;
   private int rPnumber;
   private int rUnumber;
}

