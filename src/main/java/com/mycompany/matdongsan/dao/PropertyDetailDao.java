package com.mycompany.matdongsan.dao;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.matdongsan.dto.PropertyDetail;

@Mapper
public interface PropertyDetailDao {
	// 생성
	public int createPropertyByPropertyDetail(PropertyDetail propertyDetail);
	
	// 수정
	public int updatePropertyByPropertyDetail(PropertyDetail propertyDetail);
	
	// 읽기
	public PropertyDetail selectByPdnumber(int pdnumber);
	
	// pk 값 가져오기
	public int selectPdnumberByPnumber(int pdPnumber);
	
	// 삭제
	public int deletePropertyDetailByPdPnumber(int pdPnumber);
	
	// 읽기 
	public PropertyDetail selectPropertyDetailByPdPnumber(int pdPnumber);

}
