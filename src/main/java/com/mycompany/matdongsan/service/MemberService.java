package com.mycompany.matdongsan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mycompany.matdongsan.dao.MemberDao;
import com.mycompany.matdongsan.dao.UserCommonDataDao;
import com.mycompany.matdongsan.dto.Member;
import com.mycompany.matdongsan.dto.UserCommonData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MemberService {
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private UserCommonDataDao userEmailDao;

	
	public void joinUserByMember(UserCommonData userEmail) {
		userEmailDao.insertUserDataByUser(userEmail);
	}

	public void joinByMember(Member member) {
		memberDao.insertMemberData(member);
	}

	public String getUserRole(String name) {
		return userEmailDao.getUserRoleByUserName(name);
	}

	public void deleteAccount(String uemail, Boolean isDeactivate) {
		userEmailDao.deleteUser(uemail, isDeactivate);
	}

	public int getMemberNumberByMemberEmail(String name) {
		int userNumber = userEmailDao.getUserIdByUsername(name);
		log.info(userNumber+"");
		int memberNumber = memberDao.getMemberNumberByMemberEmail(userNumber);
		return memberNumber;

	}

	public Member getMemberDataFullyByUserNumber(int userNumber) {
		Member member = memberDao.getMemberDataByUserNumber(userNumber);
		return member;
	}

	public int getUnumberByUemail(String userEmail) {
		int userNumber = userEmailDao.getUserIdByUsername(userEmail); // userId = userNumber
		return userNumber;
	}

	//map타입으로 리턴하도록 바꿔야함
	public UserCommonData getUserDataByUemail(String uemail) {
		UserCommonData userData = userEmailDao.getUserDataByUser(uemail);
		return userData;
	}

	public UserCommonData getUserDataFullyByUemail(String uemail) {
		return userEmailDao.getUserDataByUemail(uemail);
	}

	public boolean checkPassword(String currPw, String upassword) {
		PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		return passwordEncoder.matches(currPw, upassword);
	}

	public String getUserEmailByMemberNumber(int arMnumber) {
		String memberName= memberDao.getMemberNameByarMnumber(arMnumber);
		return memberName;
		
	}

	public Member getMemberDataByMemberNumber(int mnumber) {
		Member member = memberDao.getMemberDataByMemberNumber(mnumber);
		return member;
	}

	public void updateMemberData(Member member) {
		memberDao.updateMemberData(member);
		
	}
	
	// 아이디(이메일) 찾기
	public String findEmail(Member member) {
		return memberDao.findEmail(member);
	}
	
	// 해당하는 회원이 있는지 찾기
	public int checkMember(Member member) {
		return memberDao.checkMember(member);
	}

	public UserCommonData getUserDataByUnumber(int unumber) {
		return userEmailDao.getUserDataByUnumber(unumber);
	}

	public int updatePassword(UserCommonData userData) {
		return userEmailDao.updatePassword(userData);
	}
	
	public int getEmailUniqueCheck(String uemail) {
		return memberDao.getEmailUniqueCheck(uemail);
	}

}
