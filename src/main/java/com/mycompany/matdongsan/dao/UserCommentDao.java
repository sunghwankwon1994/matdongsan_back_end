package com.mycompany.matdongsan.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mycompany.matdongsan.dto.Pager;
import com.mycompany.matdongsan.dto.UserComment;

@Mapper
public interface UserCommentDao {
	// 자식 댓글 존재 여부
	public int isChildComment(int ucnumber, int pnumber);
	
	// 삭제
	public void deletePropertyComment(int pnumber, int cnumber, int userNumber);
	
	// 댓글 가져오기
	public UserComment getCommentByCnumber(int cnumber);
	
	// 생성
	public void createPropertyComment(UserComment comment);
	
	// 수정
	public void updatePropertyComment(UserComment userComment);
	
	// 해당 상품에 대한 총 댓글 수
	public int getTotalCommentCount(int pnumber);
	
	// 댓글 리스트
	public List<UserComment> getCommentByPager(int pnumber, String date, Pager pager);
}
