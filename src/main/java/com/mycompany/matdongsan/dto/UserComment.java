package com.mycompany.matdongsan.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class UserComment {
   private int ucnumber;
   private String uccomment;
   private Date ucdate;
   private int ucUnumber;
   private int ucparentnumber;
   private int ucPnumber;
   private boolean ucremoved;
}

