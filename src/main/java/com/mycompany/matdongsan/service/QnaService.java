package com.mycompany.matdongsan.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mycompany.matdongsan.dao.AnswerDao;
import com.mycompany.matdongsan.dao.NoticeDao;
import com.mycompany.matdongsan.dao.QuestionDao;
import com.mycompany.matdongsan.dto.Answer;
import com.mycompany.matdongsan.dto.Notice;
import com.mycompany.matdongsan.dto.Pager;
import com.mycompany.matdongsan.dto.Question;

@Service
public class QnaService {
	@Autowired
	private QuestionDao questionDao;
	@Autowired
	private AnswerDao answerDao;
	@Autowired
	private NoticeDao noticeDao;
	
	// Question
	// 고객 문의 DB 저장 
	public int insertQuestion(Question question) {
		return questionDao.insertQuestion(question);
	}
	
	// 고객 문의 읽기(해당하는 고객의 문의만)
	public Question getQuestionByQUnumbers(Map<String, Integer> QUnumbsers) {
		Question question =  questionDao.getQuestionByQUnumbers(QUnumbsers);
		return question;
	}
	
	// 고객 문의 읽기(user정보에 상관없이 읽기)
	public Question getQuestionByQnumber(int qnumber) {
		Question question =  questionDao.getQuestionByQnumber(qnumber);
		return question;
	}
	
	// 고객 문의 수정하기
	public int updateQuestion(Question question) {
		return questionDao.updateQuestion(question);
	}
	
	// 고객 문의 삭제하기
	public int deleteQuestionByQnumber(int qnumber) {
		return questionDao.deleteQuestionByQnumber(qnumber);
	}
	
	// 해당하는 고객의 문의 갯수 가져오기
	public int getQuestionCountByUnumber(int qUnumber) {
		return questionDao.getQuestionCountByUnumber(qUnumber);
	}
	
	// 해당하는 고객의 문의 리스트 가져오기
	public List<Question> getUsersQuestionList(Map<String, Object> usersQuestion){
		return questionDao.getUsersQuestionList(usersQuestion);
	}
	
	// 고객 문의 갯수 가져오기(전체)
	public int getQuestionCount() {
		return questionDao.getQuestionCount();
	}
	
	// 고객 문의 갯수 가져오기(타입별)
	public int getQuestionCountByfilter(Map<String, Object> mapForTotalRows) {
		return questionDao.getQuestionCountByType(mapForTotalRows);
	}
	
	// 고객 문의 리스트 가져오기(전체)
	public List<Question> getQuestionList(Map<String, Object> mapForFilter){
		return questionDao.getQuestionList(mapForFilter);
	}
	
	// 문의 첨부파일 가져오기
	public Question getQuestionImgByQnumber(int qnumber) {
		return questionDao.getQuestionImgByQnumber(qnumber);
	}
	
	// 문의 작성자 가져오기
	public String getWriterByQunumber(int qUnumber) {
		return questionDao.getWriterByQunumber(qUnumber);
	}
	
	// Answer 고객 문의 답변-------------------/////////////////////////////////////////////////////////////////////
	
	// 문의 답변 생성
	public int insertAnswer(Answer answer) {
		return answerDao.insertAnswer(answer);
	}
	
	// 문의 답변 수정
	public int updateAnswer(Answer answer) {
		return answerDao.updateAnswer(answer);
	}
	
	// 문의 답변 가져오기
	public Answer getAnswerByAqnumber(int aQnumber) {
		return answerDao.getAnswerByAqnumber(aQnumber);
	}
	
	// 문의 답변 삭제하기
	public int deleteAnswer(int anumber) {
		return answerDao.deleteAnswer(anumber);
	}
	
	// 문의 답변을 하면 q_isAnswer에 1을 넣기
	public int insertIsAnswer(int qnumber) {
		return questionDao.insertIsAnswer(qnumber);
	}
	
	// 문의 답변을 지우면 q_isanswer값 지우기
	public int updateRemoveAnswer(int qnumber) {
		return questionDao.updateRemoveAnswer(qnumber);
	}
	
	// Notice 공지 사항---------------------//////////////////////////////////////////////////////////////////////
	
	// 공지 사항 DB 저장
	public int insertNotice(Notice notice) {
		return noticeDao.insertNotice(notice);
	}
	
	// 공지사항 갯수 가져오기
	public int getCountNotice() {
		return noticeDao.countNotice();
	}
	
	// 검색 된 공지사항 갯수 가져오기
	public int getCountOfSearchedNotices(String searchKeyword) {
		return noticeDao.getCountOfSearchedNotices(searchKeyword);
	}
	
	// 공지사항 검색 및 정렬하기
	public List<Notice> getSearchedNoticeList(Map<String,Object> mapForSearch){
		return noticeDao.getSearchedNoticeList(mapForSearch);
	}
		
	// 공지사항 디테일 가져오기
	public Notice getNoticeDetail(int nnumber) {
		return noticeDao.getNoticeDetail(nnumber);
	}
	
	// 공지사항 수정하기
	public int updateNotice(Notice notice) {
		return noticeDao.updateNotice(notice);
	}
	
	// 공지사항 삭제하기
	public int deleteNotice(int nnumber) {
		return noticeDao.deleteNotice(nnumber);
	}
	

}
