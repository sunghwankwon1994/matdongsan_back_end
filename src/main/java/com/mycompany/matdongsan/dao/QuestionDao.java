package com.mycompany.matdongsan.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.matdongsan.dto.Pager;
import com.mycompany.matdongsan.dto.Question;

@Mapper
public interface QuestionDao {
	
	public int insertQuestion(Question question);
	public Question getQuestionByQUnumbers(Map<String, Integer> QUnumbers);
	public Question getQuestionByQnumber(int qnumber);
	public int updateQuestion(Question question);
	public int deleteQuestionByQnumber(int qnumber);
	public int getQuestionCountByUnumber(int qUnumber);
	public List<Question> getUsersQuestionList(Map<String, Object> usersQuestion);
	public int getQuestionCount();
	public List<Question> getQuestionList(Map<String, Object> mapForFilter);
	public int insertIsAnswer(int qnumber);
	public int updateRemoveAnswer(int qnumber);
	public Question getQuestionImgByQnumber(int qnumber);
	public String getWriterByQunumber(int qUnumber);
	public int getQuestionCountByType(Map<String, Object> mapForTotalRows);
}
