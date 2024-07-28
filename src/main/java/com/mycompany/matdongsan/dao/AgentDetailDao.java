package com.mycompany.matdongsan.dao;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.matdongsan.dto.AgentDetail;

@Mapper
public interface AgentDetailDao {
	
	//중개인 디테일 정보 추가
	void insertNewAgentDetailData(AgentDetail agentDetail);

	AgentDetail getAgentDetailDataByAgentNumber(int anumber);

	void updateAgentDetailData(AgentDetail agentDetail);
	
}
