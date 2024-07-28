package com.mycompany.matdongsan.service;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.mycompany.matdongsan.dto.Pager;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PagerService {
	//각 페이지 서비스 하나의 페이저를 이용하면서 각 페이지의 페이지네이션을 이용하면 페이지 번호를 공유한다는 점을 보완하기 위해
	//sessionAttributeKey라는 key역할을 하는 파라미터를 추가함 
	//예시) preparePager(session, 10, 100, 5, 5,"home")
	public Pager preparePager(HttpSession session, String pageNo, int totalRows, int rowsPerPage, int pagesPerGroup,
			String sessionAttributeKey) {

		Pager pager = (Pager) session.getAttribute(sessionAttributeKey);
		if (pager == null) {
			pager = new Pager(rowsPerPage, pagesPerGroup, totalRows, Integer.parseInt(pageNo)); // 페이지 번호를 1로 초기화
			session.setAttribute(sessionAttributeKey, pager);
			return pager;// 세션에 페이저 객체 저장
		}

		// 페이지 번호가 요청되었을 때 설정
		if (pageNo != null && !pageNo.isEmpty()) {
			pager = new Pager(rowsPerPage, pagesPerGroup, totalRows, Integer.parseInt(pageNo));
			int pageNumber = Integer.parseInt(pageNo);
			if (pageNumber > 0 && pageNumber <= pager.getTotalPageNo()) {
				pager.setPageNo(pageNumber);
			}
		}

		return pager;
	}
}