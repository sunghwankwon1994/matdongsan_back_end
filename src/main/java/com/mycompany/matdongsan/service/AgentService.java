package com.mycompany.matdongsan.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mycompany.matdongsan.dao.AgentDao;
import com.mycompany.matdongsan.dao.AgentDetailDao;
import com.mycompany.matdongsan.dao.AgentReviewDao;
import com.mycompany.matdongsan.dao.UserCommonDataDao;
import com.mycompany.matdongsan.dto.Agent;
import com.mycompany.matdongsan.dto.AgentDetail;
import com.mycompany.matdongsan.dto.AgentReview;
import com.mycompany.matdongsan.dto.AgentSignupData;
import com.mycompany.matdongsan.dto.Pager;
import com.mycompany.matdongsan.dto.UserCommonData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AgentService {
	@Autowired
	private AgentDao agentDao;
	@Autowired
	private AgentDetailDao agentDetailDao;
	@Autowired
	private UserCommonDataDao userEmailDao;
	@Autowired
	private AgentReviewDao agentReviewDao;
	// agent 데이터 카운트
	public int getCount() {
		int totalAgentCount = agentDao.getAgentCount();
		return totalAgentCount;
	}

	// Agent 데이터 리스트 가져오기
	public List<Agent> getAgentList(int offset, int limit) {
		List<Agent> agentList = agentDao.getAgentList(offset, limit);
		return agentList;
	}

	public List<Agent> getAgentList(int offset, int limit, String keyword,String byRate,String byComment,String byDate) {
		List<Agent> agentList = agentDao.getAgentListByKeyword(offset, limit, keyword,byRate,byComment,byDate);
		return agentList;
	}

	// 중개인 상세 데이터 추가
	public void insertAgentData(AgentDetail agentDetail) {
		agentDetailDao.insertNewAgentDetailData(agentDetail);
	}

	// 중개인 데이터 추가 (회원가입)
	public void joinByAgent(Agent agent) {
		agentDao.joinByAgent(agent);
	}

	// 공통 회원가입 데이터부분 저장
	public void joinByUserEmail(UserCommonData userEmail) {
		userEmailDao.insertUserDataByUser(userEmail);

	}

	public int getAllAgentCount() {
		int totalAgentRows = agentDao.getAllAgentCount();
		return totalAgentRows;
	}

	public int getUserIdByUserName(String username) {
		int userId = userEmailDao.getUserIdByUsername(username);
		return userId;
	}

	public AgentSignupData getAgentDataFullyByUserNumber(int userNumber) {
		AgentSignupData agentSignupData = new AgentSignupData();
		log.info("check");
		agentSignupData.setAgent(agentDao.getAgentDataByUserNumber(userNumber));
		log.info("check1");
		agentSignupData.setAgentDetail(
				agentDetailDao.getAgentDetailDataByAgentNumber(agentSignupData.getAgent().getAnumber()));
		return agentSignupData;
	}

	public void updateAgentData(Agent agent, AgentDetail agentDetail) {
		// TODO Auto-generated method stub
		agentDao.updateAgentData(agent);
		agentDetailDao.updateAgentDetailData(agentDetail);
	}

	public int getAgentNumberByUserNumber(int userNum) {
		return agentDao.getAgentNumberByUserNumber(userNum);
	}

	public Agent getAgentDataByAnumber(int anumber) {
		Agent agent = agentDao.getAgentDataByAgentNumber(anumber);
		return agent;
	}

	public AgentDetail getAgentDetailByAgentNumber(int anumber) {
		AgentDetail agentDetail = agentDetailDao.getAgentDetailDataByAgentNumber(anumber);
		return agentDetail;
	}

	public void createAgentReview(AgentReview agentReview) {
		agentReviewDao.createAgentReviewByMember(agentReview);
		
	}

	public void deleteAgentReview(int anumber, int arnumber, int userNumber) {
		agentReviewDao.deleteAgentReview(anumber,arnumber,userNumber);
		
	}

	public List<AgentReview> getAgentReviewListByAnumber(int anumber,String sort,Pager pager) {
		List<AgentReview> agentReviewList = agentReviewDao.getAgentReviewByAnumber(anumber,sort,pager);
		return agentReviewList;
	}

	public void updateAgentReview(AgentReview agentReview) {
		agentReviewDao.updateAgentReview(agentReview);
		
	}

	public int getTotalReviews(int anumber) {
		int totalRows = agentReviewDao.getTotalReviewRows(anumber);
		return totalRows;
	}

	public String getReviewAvgByanumber(int anumber) {
		String avg = Integer.toString(agentReviewDao.getAgentReviewRateAvgByAnumber(anumber));
		return avg;
	}

	public String getReviewCountByAnumber(int anumber) {
		String count = Integer.toString(agentReviewDao.getTotalReviewRows(anumber));
		return count;
	}

	public int getUserNumberByAnumber(int anumber) {
		int userNumber = agentDao.getUserNumberByAnumber(anumber);
		return userNumber;
	}
	
	// 아이디 찾기
	public String findEmail(Agent agent) {
		return agentDao.findEmail(agent);
	}
	
	// 해당하는 회원이 있는지 찾기
	public int checkAgent(Agent agent) {
		return agentDao.checkAgent(agent);
	}

	public int getAnumberByAgentPosition(String lat, String lng) {
		int anumber = agentDao.getAgentNumberByPosition(lat,lng);
		return anumber;
	}
	
	// unumber로 agent 정보 가져오기
	public Agent getAgentDataByUnumber(int unumber) {
		return agentDao.getAgentDataByUserNumber(unumber);
	}

}

