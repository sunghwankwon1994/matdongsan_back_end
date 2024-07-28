package com.mycompany.matdongsan.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class Agent {
	private int anumber;
	private String aname;
	private String abrand;
	private String aphone;
	private String aaddress;
	private byte[] aprofiledata;
	private String aprofileoname;
	private String aprofiletype;
	private String alongitude;
	private String alatitude;
	private String apostcode;
	private String aaddressdetail;
	private int aUnumber;

	private MultipartFile aprofile;
}
