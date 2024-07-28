package com.mycompany.matdongsan.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class Member {
   private int mnumber;
   private String mname;
   private byte[] mprofiledata;
   private String mprofileoname;
   private String mprofiletype;
   private String mphone;
   private int mUnumber;
   
   private MultipartFile mprofile;

}
