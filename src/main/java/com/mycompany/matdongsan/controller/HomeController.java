package com.mycompany.matdongsan.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mycompany.matdongsan.dao.UserCommonDataDao;
import com.mycompany.matdongsan.dto.Agent;
import com.mycompany.matdongsan.dto.AgentSignupData;
import com.mycompany.matdongsan.dto.Member;
import com.mycompany.matdongsan.dto.UserCommonData;
import com.mycompany.matdongsan.security.AppUserDetails;
import com.mycompany.matdongsan.security.AppUserDetailsService;
import com.mycompany.matdongsan.security.JwtProvider;
import com.mycompany.matdongsan.service.AgentService;
import com.mycompany.matdongsan.service.MemberService;
import com.mycompany.matdongsan.service.PropertyService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class HomeController {
	@Autowired
	private MemberService memberService;
	@Autowired
	private AgentService agentService;
	@Autowired
	private JwtProvider jwtProvider;
	@Autowired
	private AppUserDetailsService userDetailsService;
	@Autowired
	private UserCommonDataDao userCommonDataDao;
	@Autowired
	private PropertyService propertyService;

	@GetMapping("/api/")
	public String home() {
		log.info("실행");
		return "restapi";
	}

	@PostMapping("/Home/login")
	public Map<String, String> userLogin(String uemail, String upassword) {
		// 사용자 상세 정보 얻기
		AppUserDetails userDetails = (AppUserDetails) userDetailsService.loadUserByUsername(uemail);
		// 비밀번호 체크
		PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		boolean checkResult = passwordEncoder.matches(upassword, userDetails.getUser().getUpassword());
		// 비활성화 되었는지 확인해야함
		boolean checkActivation = userDetails.isEnabled();

		// Spring security 인증 처리
		if (checkResult != false && !checkActivation) {
			Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
					userDetails.getAuthorities());

			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		// 응답 생성 비밀번호 일치 && 계정 활성화 확인
		Map<String, String> map = new HashMap<>();
		if (checkResult && !checkActivation) {
			// AccessToken을 생성
			String accessToken = jwtProvider.createAccessToken(uemail, userDetails.getUser().getUrole());
			String userRole = memberService.getUserRole(uemail);
			if (userRole.equals("AGENT")) {
				int userNum = agentService.getUserIdByUserName(uemail);

				log.info("유저넘버: " + agentService.getAgentNumberByUserNumber(userNum) + "");
				map.put("userRoleNumber", agentService.getAgentNumberByUserNumber(userNum) + "");
			} else if (userRole.equals("MEMBER")) {
				log.info("멤버입니다.");
				log.info(memberService.getMemberNumberByMemberEmail(uemail) + "");
				map.put("userRoleNumber", memberService.getMemberNumberByMemberEmail(uemail) + "");
			}
			// JSON 응답
			map.put("result", "success");
			map.put("uemail", uemail);
			map.put("userRole", userRole);
			map.put("accessToken", accessToken);

		} else if (checkActivation) { // 비활성화(삭제된) 유저의 경우 removed라고 map에 값을 넣음
			map.put("result", "removed");

		} else { // 로그인에 실패한 경우(비밀번호 문제) fail 표시
			map.put("result", "fail");
		}
		log.info(map + "");
		return map;
	}

	// 탈퇴
	@PutMapping("/Home/MyPage/DeleteAccount")
	public void activateAccount(@RequestBody Map<String, String> payload, Authentication authentication) {
		String currPw = payload.get("currPw");
		log.info("탈퇴 currPw : " + currPw);
		String uemail = authentication.getName();
		log.info("탈퇴 uemail : " + uemail);
		UserCommonData user = memberService.getUserDataFullyByUemail(uemail);
		boolean isDeactivate = true; // 비활성화 여부

		// 비밀번호 일치 여부
		if (memberService.checkPassword(currPw, user.getUpassword())) {
			memberService.deleteAccount(uemail, isDeactivate);
		}
	}

	// 유저정보 불러오기
	@GetMapping("/Home/Mypage/MyInfomation/{uemail}")
	public UserCommonData getUserDataByUemail(@PathVariable String uemail) {
		log.info(uemail);
		UserCommonData userData = memberService.getUserDataByUemail(uemail);
		return userData;
	}

	// 유저정보 불러오기 by unumber
	@GetMapping("/Home/getUserData/{unumber}")
	public Map<String, Object> getUserDataByUnumber(@PathVariable int unumber) {

		Map<String, Object> userTotalInfo = new HashMap<>();
		UserCommonData userCommonData = memberService.getUserDataByUnumber(unumber);
		userTotalInfo.put("userCommonData", userCommonData);

		if (userCommonData.getUrole().equals("MEMBER")) {
			Member member = memberService.getMemberDataFullyByUserNumber(unumber);
			userTotalInfo.put("member", member);
		} else if (userCommonData.getUrole().equals("AGENT")) {
			// 중개인일 경우
			Agent agent = agentService.getAgentDataByUnumber(unumber);
			log.info("agent: " + agent.toString());
			userTotalInfo.put("agent", agent);
		}
		return userTotalInfo;
	}

	// 중개인 아이디 찾기
	@PostMapping("/Home/login/findAgentEmail")
	public Map<String, String> getAgentEmail(Agent agent) {
		// 이메일 가져오기
		String aemail = agentService.findEmail(agent);

		// 통신 결과를 map에 담아 보내기
		Map<String, String> result = new HashMap<>();

		if (aemail != null) {
			result.put("success", aemail);
		} else {
			result.put("fail", "이메일을 찾을 수 없습니다.");
		}
		return result;
	}

	// 일반인 아이디 찾기 -> 개인 정보는 form데이터로 받기 위해 post매핑 함
	@PostMapping("/Home/login/findMemberEmail")
	public Map<String, String> getMemberEmail(Member member) {
		log.info("받아온 매개변수 없다고 뜬다." + member.toString());

		// 이메일 가져오기
		String memail = memberService.findEmail(member);

		// 통신 결과를 map에 담아 보내기
		Map<String, String> result = new HashMap<>();

		// memail이 빈값이 아닐 경우 memail데이터를 반환
		if (memail != null) {
			result.put("success", memail);
		} else {
			result.put("fail", "이메일을 찾을 수 없습니다.");
		}

		return result;

	}

	// 비밀번호 찾기 위한 회원 인증
	@PostMapping("/Home/canResetPassword")
	public Map<String, String> canResetPassword(@RequestParam("name") String name, @RequestParam("phone") String phone,
			@RequestParam("email") String email) {
		// 리턴 값 반환 할 map
		Map<String, String> result = new HashMap<>();

		// 아이디가 존재 하는지 확인
		UserCommonData userData = userCommonDataDao.getUserDataByUser(email);

		if (userData == null) {
			result.put("noUser", "해당 회원을 찾을 수 없습니다");
			return result;
		}

		if (userData.getUemail() != null) {
			// 존재 한다면 입력한 정보가 맞는지 확인
			if (userData.getUrole().equals("MEMBER")) {
				// 일반 회원이면 member에서 찾기
				Member member = new Member();
				member.setMname(name);
				member.setMphone(phone);
				// 회원이 있다면
				log.info("회원 존재 여부" + memberService.checkMember(member) + "");
				if (memberService.checkMember(member) > 0) {
					result.put("success", userData.getUemail());
				} else {
					result.put("notFoundUser", "아이디와 정보가 일치하지 않습니다.");
				}
			} else if (userData.getUrole().equals("AGENT")) {
				// 중개인 회원이면 agent에서 찾기
				Agent agent = new Agent();
				agent.setAname(name);
				agent.setAphone(phone);
				// 회원이 있다면
				if (agentService.checkAgent(agent) > 0) {
					result.put("success", userData.getUemail());
				} else {
					result.put("notFoundUser", "아이디와 정보가 일치하지 않습니다.");
				}
			}
		}

		return result;
	}

	// 비밀번호 변경
	@PutMapping("/Home/updatePassword")
	public Map<String, String> updatePassword(UserCommonData userData) {
		Map<String, String> map = new HashMap<>();

		// 비밀번호 암호화
		PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		userData.setUpassword(passwordEncoder.encode(userData.getUpassword()));

		// 비밀번호 변경
		int result = userCommonDataDao.updatePassword(userData);

		// result가 0이면 수정 안됨, 1이면 된 것이다.
		if (result > 0) {
			map.put("success", "변경이 완료되었습니다.");
		} else {
			map.put("fail", "변경을 실패하였습니다.");
		}

		return map;
	}

	// 비밀번호 맞는지 확인
	@PostMapping("/Home/checkOldPassword")
	public Map<String, String> checkOldPassword(UserCommonData userData) {
		// 사용자 상세 정보 얻기
		AppUserDetails userDetails = (AppUserDetails) userDetailsService.loadUserByUsername(userData.getUemail());
		// 비밀번호 체크
		PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		boolean checkResult = passwordEncoder.matches(userData.getUpassword(), userDetails.getUser().getUpassword());

		Map<String, String> map = new HashMap<>();
		if (checkResult) {
			map.put("result", "맞음");
		} else {
			map.put("result", "틀림");
		}

		return map;
	}

	@GetMapping("/Home/getListingRemain")
	public Map<String, Object> getListingRemain(Authentication authentication) {
		Map<String, Object> map = new HashMap<>();
		String userName = authentication.getName(); // 로그인 정보에서 이름 가져오기
		if (userName == null || userName.equals("")) {
			map.put("result", "noUser");
		} else {
			int userNumber = userCommonDataDao.getUserIdByUsername(userName); // 가져온 이름으로 userNumber값 가져오기
			boolean hasPropertyListing = propertyService.checkPropertyCondition(userNumber); // 유저가 이전에 결제한 적 있는지
			if (hasPropertyListing) {
				// 있다면 수량 정보 가져오기
				int propertyListing = propertyService.getUserPropertyListingRemain(userNumber); // userNumber로 수량 가져오기
				log.info("구매 한 적이 없으면");
				map.put("remain", propertyListing);
			} else {
				map.put("result", "noRemain");
			}

		}

		return map;
	}

}