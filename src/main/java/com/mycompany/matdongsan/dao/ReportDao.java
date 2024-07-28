package com.mycompany.matdongsan.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.matdongsan.dto.Pager;
import com.mycompany.matdongsan.dto.Report;

@Mapper
public interface ReportDao {
	
	// 매물 신고
	public void createPropertyReport(Report report);
	
	// 유저 허위 신고 총 개수
	public int getAllUserReportCountByUnumber(int unumber);
	
	// 유저 허위 매물 리스트
	public List<Report> getUserReportListByUnumber(int unumber, Pager pager,String filterKeyword);
	
	// 허위 매물 신고 삭제
	public int deleteUserReport(int pnumber, int unumber);
	
	// 허위 매물 신고 여부
	public int checkUserPropertyReport(int unumber, int pnumber);

}
