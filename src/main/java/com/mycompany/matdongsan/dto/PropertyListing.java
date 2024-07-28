package com.mycompany.matdongsan.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class PropertyListing {
   private int plnumber;
   private int plprice;
   private Date pldate;
   private int plquantity;
   private int plUnumber;
   private int plremain;
}

