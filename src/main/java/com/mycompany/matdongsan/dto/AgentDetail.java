package com.mycompany.matdongsan.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class AgentDetail {
   private int adnumber;
   private String adattachoname;
   private byte[] adattachdata;
   private String adattachtype;
   private String adbrandnumber;
   private int adAnumber;
   
   private MultipartFile adattach;
}
