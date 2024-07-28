package com.mycompany.matdongsan.dto;

import lombok.Data;

@Data
public class AgentSignupData {
	private Agent agent;
	private AgentDetail agentDetail;
	private UserCommonData userEmail;
}
