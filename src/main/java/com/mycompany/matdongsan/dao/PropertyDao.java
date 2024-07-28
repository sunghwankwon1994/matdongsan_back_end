package com.mycompany.matdongsan.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mycompany.matdongsan.dto.Pager;
import com.mycompany.matdongsan.dto.Property;
import com.mycompany.matdongsan.dto.PropertyListing;

@Mapper
public interface PropertyDao {
	// 읽기
	public Property selectByPnumber(int pnumber);
	
	// 생성
	public int createPropertyByProperty(Property property);
	
	// 수정
	public int updatePropertyByProperty(Property property);
	
	// 조회수
	public int updatePhitcount(int pnumber);
	
	// property 총 개수
	public int getAllPropertyCount();
	
	// property 개수 by filter and keyword
	public int getPropertyCountByFilter(@Param("keyword") String keyword, @Param("price") String price, @Param("date") String date, @Param("rentType") String rentType, @Param("floorType") String floorType, String platitude,String plongitude);
	
	// property 전체 리스트
	public List<Property> getAllPropertyList(@Param("offset") int offset, @Param("limit") int limit);
	
	// property 리스트 by filter and keyword
	public List<Property> getPropertyListByFilter(@Param("offset") int offset, @Param("limit") int limit, @Param("keyword") String keyword, 
			@Param("price") String price, @Param("date") String date, @Param("rentType") String rentType, @Param("floorType") String floorType, @Param("platitude")String platitude,@Param("plongitude") String plongitude);
	
	// 삭제
	public int deletePropertyByPnumber(int pnumber);
	
	// 댓글 작성 시 매물 주인 여부
	public int isPropertyOwnerByComment(@Param("pnumber") int pnumber, @Param("userNumber") int userNumber);
	
	// 유저 매물 리스트
	public List<Property> getUserPropertyListByUnumber(int unumber, Pager pager);
	
	// 유저 매물 총 개수
	public int getAllUserPropertyCountByUnumber(int unumber);
	
	// 인기 매물
	public List<Property> getPopularPropertyListByHitcount();

	public int getTradeCountByUserNumber(int userNumber);
	
	// 매물 상태 수정
	public void updatePropertyStatus(@Param("pnumber") int pnumber, @Param("pstatus") String pstatus);
	
	//매물 전체 데이터(페이저X)
	public List<Property> getAllPropertyListWithoutPager();

	public int getPnumberByPropertyPosition(String platitude, String plongitude);

	public List<Property> getUserPropertyListByUnumberAndFilter(int unumber, Pager pager, String filterKeyword);

	public int getAllUserPropertyCountByUnumberAndFilter(int unumber, String filterKeyword);
	



}
