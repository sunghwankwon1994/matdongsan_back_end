package com.mycompany.matdongsan.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mycompany.matdongsan.dto.Agent;
import com.mycompany.matdongsan.dto.AgentDetail;
import com.mycompany.matdongsan.dto.AgentSignupData;
import com.mycompany.matdongsan.dto.Member;
import com.mycompany.matdongsan.dto.UserCommonData;
import com.mycompany.matdongsan.security.AppUserDetails;
import com.mycompany.matdongsan.security.AppUserDetailsService;
import com.mycompany.matdongsan.security.JwtProvider;
import com.mycompany.matdongsan.service.AgentService;
import com.mycompany.matdongsan.service.MemberService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/Member")
public class MemberController {
	@Autowired
	private MemberService memberService;
	@Autowired
	private AgentService agentService;
	@Autowired
	private JwtProvider jwtProvider;
	@Autowired
	private AppUserDetailsService userDetailsService;
	
	// 로그인

	// 회원가입
	// 참고 블로그
	// https://velog.io/@tjddnths0223/%ED%8C%81-RequestBody%EB%A1%9C-%EC%97%AC%EB%9F%AC-%EA%B0%9D%EC%B2%B4-%EB%B0%9B%EA%B8%B0
	@Transactional
	@PostMapping("/Signup/MemberSignup")
	public UserCommonData joinByMember(@RequestParam("uemail") String uemail, @RequestParam("urole") String urole,
			@RequestParam("upassword") String upassword, @RequestParam("mname") String mname,
			@RequestParam("mphone") String mphone,
			@RequestParam(value = "mprofile", required = false) MultipartFile mprofile) throws IOException {

		UserCommonData userEmail = new UserCommonData();
		userEmail.setUemail(uemail);
		userEmail.setUrole(urole);
		userEmail.setUpassword(upassword);
		userEmail.setUremoved(false);

		// 비밀번호 암호화
		PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		userEmail.setUpassword(passwordEncoder.encode(userEmail.getUpassword()));

		// 권한 설정

		Member member = new Member();
		member.setMname(mname);
		member.setMphone(mphone);

		memberService.joinUserByMember(userEmail);
		member.setMUnumber(userEmail.getUnumber()); // FK 값 주기

		// 프로필 사진 첨부 여부
		if (mprofile != null && !mprofile.isEmpty()) {
			member.setMprofileoname(mprofile.getOriginalFilename());
			member.setMprofiletype(mprofile.getContentType());
			member.setMprofiledata(mprofile.getBytes());
		}
		memberService.joinByMember(member);

		// JSON으로 변환되지 않는 필드는 null 처리
		userEmail.setUpassword(null);
		member.setMprofile(null);
		member.setMprofiledata(null);

		return userEmail;
	}

	//일반 유저 프로필 사진
	@GetMapping("/mattach/{mnumber}")
	public void downloadMemberProfile(@PathVariable int mnumber, HttpServletResponse response) {
		
		// 해당 게시물 가져오기
		Member memeber = memberService.getMemberDataByMemberNumber(mnumber);
		// 파일 이름이 한글일 경우, 브라우저에서 한글 이름으로 다운로드 받기 위한 코드
		if(memeber.getMprofileoname() !=null) {
			try {
				String fileName = new String(memeber.getMprofileoname().getBytes("UTF-8"), "ISO-8859-1");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
				// 파일 타입을 헤더에 추가
				response.setContentType(memeber.getMprofiletype());
				// 응답 바디에 파일 데이터를 출력
				OutputStream os = response.getOutputStream();
				os.write(memeber.getMprofiledata());
				os.flush();
				os.close();
			} catch (IOException e) {
				log.error(e.toString());
			}
		}
	
	}
	
	// 일반 멤버 정보 업데이트
		@Transactional
		@PutMapping("/Mypage/MyInfomation")
		public void updateMypagePropertyInfo(@ModelAttribute Member memberData,
				Authentication authentication) {
			Member member =  memberData;
			// 프로필사진 & 등록증 사진
			if (member.getMprofile() != null) {
				MultipartFile memberProfile = member.getMprofile();

				// 파일 이름을 설정
				member.setMprofileoname(memberProfile.getOriginalFilename());
				// 파일 종류를 설정
				member.setMprofiletype(memberProfile.getContentType());
				try {
					// 파일 데이터를 설정
					member.setMprofiledata(memberProfile.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			int memberNum = memberService.getMemberNumberByMemberEmail(authentication.getName()); // 멤버 넘버
			member.setMnumber(memberNum);
			// 수정하기
			memberService.updateMemberData(member);
		}
		
		
		// 유저정보 불러오기
		@GetMapping("/Mypage/MyInfomation/{uemail}")
		public UserCommonData getUserDataByUemail(@PathVariable String uemail) {
			log.info(uemail);
			UserCommonData userData = memberService.getUserDataByUemail(uemail);
			return userData;
		}	
		
		
		
		// 유저정보 불러오기 by unumber
		@GetMapping("/getUserData/{unumber}")
		public Map<String, Object> getUserDataByUnumber(@PathVariable int unumber) {
			
			Map<String, Object> userTotalInfo = new HashMap<>();
			UserCommonData userCommonData = memberService.getUserDataByUnumber(unumber);
			userTotalInfo.put("userCommonData", userCommonData);
			
			if(userCommonData.getUrole().equals("MEMBER")) {
				Member member = memberService.getMemberDataFullyByUserNumber(unumber);
				userTotalInfo.put("member", member);
			} else if(userCommonData.getUrole().equals("AGENT")) {
				// 중개인일 경우
				Agent agent = agentService.getAgentDataByUnumber(unumber);
				log.info("agent: "+ agent.toString());
				userTotalInfo.put("agent", agent);
			}
			return userTotalInfo;
		}	
		
		
		// 부동산 등록
		// 리턴값과 파라미터 값으로 agent와 agentDetail이 합쳐진 dto를 받아야함
		// agent관련 DTO를 만들어서 코드 바꿀것
		@Transactional
		@PostMapping("/Signup/AgentSignup")
		public void createAgentAccount(@ModelAttribute AgentSignupData agentSignupData) throws IOException {
			// 객체 생성 및 데이터 설정
			Agent agent = agentSignupData.getAgent();
			AgentDetail agentDetail = agentSignupData.getAgentDetail();
			UserCommonData userEmail = agentSignupData.getUserEmail();

			// 비밀번호 암호화
			PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
			userEmail.setUpassword(passwordEncoder.encode(userEmail.getUpassword()));

			MultipartFile profileImg = agent.getAprofile();

			// 중개인 프로필 사진 이미지 처리
			if (profileImg != null && !profileImg.isEmpty()) {
				agent.setAprofileoname(profileImg.getOriginalFilename());
				agent.setAprofiletype(profileImg.getContentType());
				agent.setAprofiledata(profileImg.getBytes());
				log.info(agent.getAprofileoname());
			}

			// 사업자 등록증 이미지
			// 사업자 등록증 첨부 파일 처리
			if (agentDetail.getAdattach() != null && !agentDetail.getAdattach().isEmpty()) {
				agentDetail.setAdattachoname(agentDetail.getAdattach().getOriginalFilename());
				agentDetail.setAdattachtype(agentDetail.getAdattach().getContentType());
				agentDetail.setAdattachdata(agentDetail.getAdattach().getBytes());
				log.info(agentDetail.getAdattachoname());
			}

			// 데이터베이스에 저장
			agentService.joinByUserEmail(userEmail);
			agent.setAUnumber(userEmail.getUnumber());
			agentService.joinByAgent(agent);
			agentDetail.setAdAnumber(agent.getAnumber());
			agentService.insertAgentData(agentDetail);
			// 출력시 데이터 부분은 출력 길이가 길어서 null로 처리
			userEmail.setUpassword(null);
			agentDetail.setAdattachdata(null);
			agent.setAprofiledata(null);
//			return agentSignupData;
		}
		
		
		@PostMapping("/login")
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
		@PutMapping("/MyPage/DeleteAccount")
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
		
		
		// 일반인 아이디 찾기 -> 개인 정보는 form데이터로 받기 위해 post매핑 함
		@PostMapping("/login/findMemberEmail")
		public Map<String, String> getMemberEmail(Member member){
			log.info("받아온 매개변수 없다고 뜬다."+member.toString());
			
			// 이메일 가져오기
			String memail = memberService.findEmail(member);
			
			// 통신 결과를 map에 담아 보내기
			Map<String, String> result = new HashMap<>();
			
			// memail이 빈값이 아닐 경우 memail데이터를 반환
			if(memail != null) {
				result.put("success", memail);
			} else {
				result.put("fail", "이메일을 찾을 수 없습니다.");
			}
			
			return result;
			
		}
		

		// 비밀번호 찾기 위한 회원 인증
		@PostMapping("/canResetPassword")
		public Map<String, String> canResetPassword(
				@RequestParam("name") String name
				, @RequestParam("phone") String phone
				, @RequestParam("email") String email){
			// 리턴 값 반환 할 map
			Map<String, String> result = new HashMap<>();
			
			// 아이디가 존재 하는지 확인
			UserCommonData userData = memberService.getUserDataByUemail(email);
			
			if(userData == null) {
				result.put("noUser", "해당 회원을 찾을 수 없습니다");
				return result;
			} 
			
			if(userData.getUemail() != null) {
				// 존재 한다면 입력한 정보가 맞는지 확인
				if(userData.getUrole().equals("MEMBER")) {
					// 일반 회원이면 member에서 찾기
					Member member = new Member();
					member.setMname(name);
					member.setMphone(phone);
					// 회원이 있다면
					log.info("회원 존재 여부"+memberService.checkMember(member)+"");
					if(memberService.checkMember(member) > 0) {
						result.put("success", userData.getUemail());
					} else {
						result.put("notFoundUser", "아이디와 정보가 일치하지 않습니다.");
					}
				} else if(userData.getUrole().equals("AGENT")) {
					// 중개인 회원이면 agent에서 찾기
					Agent agent = new Agent();
					agent.setAname(name);
					agent.setAphone(phone);
					// 회원이 있다면
					if(agentService.checkAgent(agent) > 0) {
						result.put("success", userData.getUemail());
					} else {
						result.put("notFoundUser", "아이디와 정보가 일치하지 않습니다.");
					}
				} 
			} 
			
			return result;
		}		
		
		// 비밀번호 변경
		@PutMapping("/updatePassword")
		public Map<String, String> updatePassword(UserCommonData userData) {
			Map<String, String> map = new HashMap<>();
			
			// 비밀번호 암호화
			PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
			userData.setUpassword(passwordEncoder.encode(userData.getUpassword()));
			
			// 비밀번호 변경
			int result = memberService.updatePassword(userData);
			// result가 0이면 수정 안됨, 1이면 된 것이다.
			if(result>0) {
				map.put("success", "변경이 완료되었습니다.");
			}else {
				map.put("fail", "변경을 실패하였습니다.");
			}
			
			return map;
		}
		
		
		// 비밀번호 맞는지 확인
		@PostMapping("/checkOldPassword")
		public Map<String, String> checkOldPassword(UserCommonData userData) {
			// 사용자 상세 정보 얻기
			AppUserDetails userDetails = (AppUserDetails) userDetailsService.loadUserByUsername(userData.getUemail());
			// 비밀번호 체크
			PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
			boolean checkResult = passwordEncoder.matches(userData.getUpassword(), userDetails.getUser().getUpassword());
			
			Map<String, String> map = new HashMap<>();
			if(checkResult) {
				map.put("result", "맞음");
			} else {
				map.put("result", "틀림");
			}
			
			return map;
		}
		
	
		// 중개인 아이디 찾기
		@PostMapping("/login/findAgentEmail")
		public Map<String, String> getAgentEmail(Agent agent){
			// 이메일 가져오기
			String aemail = agentService.findEmail(agent);
			
			// 통신 결과를 map에 담아 보내기 
			Map<String, String> result = new HashMap<>();
			
			if(aemail != null) {
				result.put("success", aemail);
			} else {
				result.put("fail", "이메일을 찾을 수 없습니다.");
			}
			return result;
		}
		
		// 아이디 중복 확인
		@GetMapping("/signup/emailUniqueCheck")
		public int getEmailUniqueCheck(String uemail) {
			log.info("이메일나와"+uemail);
			if(memberService.getEmailUniqueCheck(uemail) > 0) {
				return 1; // 중복이 있으면 1
			} else {
				return 0; // 없으면 0
			}
			
		}
		
		
		
	// 비밀번호 수정

	//
}
