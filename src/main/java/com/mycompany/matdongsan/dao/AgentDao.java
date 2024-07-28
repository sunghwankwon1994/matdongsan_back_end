package com.mycompany.matdongsan.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mycompany.matdongsan.dto.Agent;

@Mapper
public interface AgentDao {
	//총 중개인 수
	int getAgentCount();
	//전체 중개인 데이터 리스트
	List<Agent> getAgentList(@Param("offset") int offset, @Param("limit") int limit);
	List<Agent> getAgentListByKeyword(int offset, int limit, String keyword,String byRate,String byComment,String byDate);
	void insertNewAgentData(Agent agent);
	void joinByAgent(Agent agent);
	int getAllAgentCount();
	int getAgentNumberByUserNumber(int userNumber);
	Agent getAgentDataByUserNumber(int userNumber);
	Agent getAgentDataByAgentNumber(int agentNumber);
	void updateAgentData(Agent agent);
	int getUserNumberByAnumber(int anumber);
	public String findEmail(Agent agent);
	public int checkAgent(Agent agent);
	int getAgentNumberByPosition(String lat, String lng);
}
