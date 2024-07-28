package com.mycompany.matdongsan.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mycompany.matdongsan.dto.AgentReview;
import com.mycompany.matdongsan.dto.Pager;

@Mapper
public interface AgentReviewDao {

	void createAgentReviewByMember(AgentReview agentReview);

	void deleteAgentReview(int anumber, int arnumber, int userNumber);

	List<AgentReview> getAgentReviewByAnumber(@Param("anumber") int anumber, String sort,@Param("pager") Pager pager);

	void updateAgentReview(AgentReview agentReview);

	int getTotalReviewRows(int anumber);

	int getAgentReviewRateAvgByAnumber(int anumber);

}
