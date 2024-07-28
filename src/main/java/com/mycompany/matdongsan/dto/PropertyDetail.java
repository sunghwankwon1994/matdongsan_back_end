package com.mycompany.matdongsan.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class PropertyDetail {
   private int pdnumber;
   private String pdcontent;
   @DateTimeFormat(pattern = "yyyy-MM-dd") // vue -> backend에서 date 보낼 때 string으로 받는 에러 해결
   private Date pdmoveindate;
   private boolean pdbath;
   private boolean pdlift;
   private boolean pdbed;
   private boolean pdlot;
   private boolean pdheating;
   private boolean pdcooling;
   private boolean pdmicrowave;
   private boolean pdburner;
   private boolean pdfridge;
   private boolean pdshoecloset;
   private boolean pdtv;
   private boolean pdcloset;
   private boolean pddinningtable;
   private boolean pdtable;
   private boolean pdwasher;
   private boolean pdinduction;
   private int pdPnumber;
}

