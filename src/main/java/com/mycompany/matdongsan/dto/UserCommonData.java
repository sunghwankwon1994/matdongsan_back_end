package com.mycompany.matdongsan.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class UserCommonData {
   private int unumber;
   private String uemail;
   private String urole;
   private String upassword;
   private boolean uremoved;
   private Date ujoindate;
}
