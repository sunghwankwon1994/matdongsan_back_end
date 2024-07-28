package com.mycompany.matdongsan.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mycompany.matdongsan.dto.UserCommonData;

@Mapper
public interface UserCommonDataDao {
	
	// 회원가입
//	public int joinByUserEmail(UserEmail userEmail);
	
	//회원가입
	public int insertUserDataByUser(UserCommonData userCommonData);
	
	public UserCommonData selectByUnumber(String username);

	public int getUserIdByUsername(String username);
	
	// 탈퇴
	public void deleteUser(String uemail, Boolean isDeactivate);

	public String getUserRoleByUserName(String name);
	
	// 전체 정보 가져오기
	public UserCommonData getUserDataByUemail(String uemail);

	public UserCommonData getUserDataByUser(String uemail);

	public UserCommonData getUserDataByUnumber(int unumber);
	
	public int updatePassword(UserCommonData userCommonData);
}
