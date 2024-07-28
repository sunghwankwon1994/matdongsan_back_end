package com.mycompany.matdongsan.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.matdongsan.dto.Notice;
import com.mycompany.matdongsan.dto.Pager;

@Mapper
public interface NoticeDao {
	
	public int insertNotice(Notice notice);
	public int countNotice();
	public Notice getNoticeDetail(int nnumber);
	public int updateNotice(Notice notice);
	public int deleteNotice(int nnumber);
	public List<Notice> getSearchedNoticeList(Map<String, Object> mapForSearch); 
	public int getCountOfSearchedNotices(String searchKeyword);

}
