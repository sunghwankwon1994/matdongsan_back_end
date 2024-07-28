package com.mycompany.matdongsan.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mycompany.matdongsan.dto.Answer;
import com.mycompany.matdongsan.dto.Notice;
import com.mycompany.matdongsan.dto.Pager;
import com.mycompany.matdongsan.dto.Question;
import com.mycompany.matdongsan.service.MemberService;
import com.mycompany.matdongsan.service.PagerService;
import com.mycompany.matdongsan.service.QnaService;

import lombok.extern.slf4j.Slf4j;


@RestController
@Slf4j
@RequestMapping("/api/Qna")
public class QnaController {
	@Autowired 
	private QnaService qnaService;
	
	@Autowired
	private PagerService pagerService;
	
	@Autowired
	private MemberService memberService;
	
	// Question
	// 고객 문의 생성
	//@PreAuthorize("hasAuthority('ROLE_USER')")
	@PostMapping("/CustomerInquiryForm")
	public Question createQuestion(Question question, Authentication authentication) {
		
		// 로그인 정보가 있는 사람만 작성 가능
		if(!authentication.getName().equals("")) {
			if(question.getQattach() != null && !question.getQattach().isEmpty()) {
				log.info("첨부 파일 있음");
				
				MultipartFile mf = question.getQattach();
				
				question.setQattachoname(mf.getOriginalFilename());
				question.setQattachtype(mf.getContentType());
				try {
					question.setQattachdata(mf.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
							
			} else {
				log.info("첨부 파일 없음");
			}
			
			question.setQUnumber(memberService.getUnumberByUemail(authentication.getName()));
			log.info(question.toString()); // postman으로 볼 수 없는 multipartfile이나 byte배열을 확인 
			
			qnaService.insertQuestion(question);
			
			// json 반환할 때 json으로 변환할 수 없는 multipartfile이나 byte배열같은 타입은 빼야 한다.
			question.setQattach(null);
			question.setQattachdata(null);
			return question;
		}
		return question;
	}
	
	// Read
	// 고객 문의 사항 디테일 읽기 
	//@PreAuthorize("hasAuthority('ROLE_USER')")
	@GetMapping("/ReadCustomerInquiry")
	public Question readQuestion(int qnumber, @RequestParam(required = false) int qUnumber, Authentication authentication) {
		// 매개변수에 qUnumber는 문의 작성자이다.
		Question question = new Question();
		
		// 로그인 여부 확인
		if(authentication.getName() == null || authentication.getName() == "") {
			log.info("로그인 정보가 없습니다.");
			return question; // 비어있는 객체
		}else {
			// 로그인 정보가 있다면
			// admin 권한 확인
			if(memberService.getUserRole(authentication.getName()).equals("ADMIN")){
				log.info("ADMIN 권한 입니다.");
				// admin의 경우 qnumber만으로 게시물을 읽을 수 있다.
				question = qnaService.getQuestionByQnumber(qnumber);
				return question;
				
			// Role이 관리자가 아니면서 고객문의를 요청한 사용자와 문의를 작성한 사용자가 같을 경우에 
			// qUnumber와 qnumber 모두를 가지고 고객 문의 정보를 가져온다.
			} else if(qUnumber == memberService.getUnumberByUemail(authentication.getName())) {
				log.info("USER 권한 입니다.");
				Map<String, Integer> QUnumber = new HashMap<>();
				QUnumber.put("qnumber", qnumber);
				QUnumber.put("qUnumber", qUnumber);
				question = qnaService.getQuestionByQUnumbers(QUnumber);
				
				return question;
				
			}
			return question;// 비어있는 객체
		}
		
	}
	
	// Update
	// 고객 문의 사항 수정 (고객만 할 수 있다.)
	//@PreAuthorize("hasAuthority('ROLE_USER')")
	@PutMapping("/MyCustomerInquiryUpdate")
	public String updateQuestion(Question question, Authentication authentication) {
		
		log.info("받아온 문의 확인: "+question.toString());
		// 수정할 게시물의 unumber랑 로그인 한 사용자의 unumber랑 같아야 수정 할 수 있음
		if(question.getQUnumber() == memberService.getUnumberByUemail(authentication.getName())) {
			log.info("사용자 같음");
			
			if(question.getQattach() != null && !question.getQattach().isEmpty()) {
				log.info("첨부 파일 있음");
				
				MultipartFile mf = question.getQattach();
				
				question.setQattachoname(mf.getOriginalFilename());
				question.setQattachtype(mf.getContentType());
				try {
					question.setQattachdata(mf.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
							
			} else {
				log.info("첨부 파일 없음");
			}
			
			qnaService.updateQuestion(question);

			// 수정 후 변경 된 문의사항 가져오기
			question = qnaService.getQuestionByQnumber(question.getQnumber());
			return question.toString();
		}
		
		return "잘못된 접근입니다.";
		
	}
	
	// Delete
	// 고객 문의 사항 삭제 -> 고객만 할 수 있다.
	//@PreAuthorize("hasAuthority('ROLE_USER')")
	@DeleteMapping("/MyCustomerInquiryDelete")
	public int deleteQuestion(int qnumber, int qUnumber, Authentication authentication) {
		// 삭제할 게시물의 unumber랑 로그인 한 사용자의 unumber랑 같아야 삭제 할 수 있음
		if(qUnumber == memberService.getUnumberByUemail(authentication.getName())) {
			int result = qnaService.deleteQuestionByQnumber(qnumber); // 정상적으로 삭제가 되면 1을 반환, 없는 게시물을 삭제하려고 하면 0을 반환
			log.info("결과 값", result);
			return result;
		}
		return 0;
	} 
	
	// getList - user
	// 특정 고객이 문의한 고객 문의 사항 리스트 가져오기(특정 회원이 문의한 문의사항-마이페이지에서 사용할 예정)
	@GetMapping("/MyCustomerInquiryList")
	public Map<String, Object> customerQuestionList(@RequestParam(defaultValue = "1") String pageNo, HttpSession session, Authentication authentication){
		// 로그인 정보로 리스트 가져오기
		
		// 해당하는 회원이 작성한 공지사항 갯수
		int totalRows = qnaService.getQuestionCountByUnumber(memberService.getUnumberByUemail(authentication.getName()));
		
		// 페이저 객체 생성(페이지 당 행 수, 그룹 당 페이지 수, 전체 행 수, 현재 페이지 번호)
		Pager pager= pagerService.preparePager(session, pageNo, totalRows, 3, 3, "myCustomerInquiry");
		
		
		// 해당하는 유저의 페이지 불러오기 위한 Map 생성 (Mybatis는 parameter을 하나의 타입만 보낼 수 있어서 map에 담아서 매개변수를 넣는다.)
		Map<String, Object> usersQuestion = new HashMap<>();
		usersQuestion.put("pager", pager);
		usersQuestion.put("qUnumber", memberService.getUnumberByUemail(authentication.getName()));
		
		// 해당 페이지의 게시물 목록 가져오기
		List<Question> list = qnaService.getUsersQuestionList(usersQuestion);
		
		// 여러 객체를 리턴하기 위해 Map 객체 생성		
		Map<String, Object> map = new HashMap<>();
		
		// map에 데이터 넣기
		map.put("question", list);
		map.put("pager", pager);

		
		return map; // return 값은 JSON으로 변환되어 응답 본문에 들어간다. {"pager":{}, "notice":[]};
	}
	
	// getList - all
	// 고객 문의 사항 리스트 전부 가져오기(전체 문의 사항 - 관리자페이지)
	@GetMapping("/CustomerInquiryList")
	public Map<String, Object> CustomerInquiryList(@RequestParam(defaultValue = "1") String pageNo, 
			@RequestParam(required = false) String filter, 
			@RequestParam(required = false) String isAnswer, 
			@RequestParam(defaultValue = "desc", required = false) String sort, HttpSession session, Authentication authentication){
		
		// 유저 정보가 admin이면 리스트 가져오기
//		if(memberService.getUserRole(authentication.getName()).equals("ADMIN")) {
			// 검색 조건이 아무것도 없을 때 
			if((filter == null || filter.equals(""))&&(isAnswer == null || isAnswer.equals(""))&&(sort.equals(""))) {
				// 공지사항 갯수
				int totalRows = qnaService.getQuestionCount(); 
				
				// 페이저 객체 생성(페이지 당 행 수, 그룹 당 페이지 수, 전체 행 수, 현재 페이지 번호)
				Pager pager= pagerService.preparePager(session, pageNo, totalRows, 10, 5, "adminCustomerInquiry");
				
				Map<String, Object> mapForFilter = new HashMap<>();
				
				mapForFilter.put("pager",pager);
				
				// 해당 페이지의 게시물 목록 가져오기
				List<Question> list = qnaService.getQuestionList(mapForFilter);
				
				// 여러 객체를 리턴하기 위해 Map 객체 생성		
				Map<String, Object> map = new HashMap<>();
							
				// map에 데이터 넣기
				map.put("question", list);
				map.put("pager", pager);

				
				return map;
				
				
			} 	// 타입별 공지사항 갯수
				Map<String, Object> mapForTotalRows = new HashMap<>();
				mapForTotalRows.put("filter", filter);
				mapForTotalRows.put("isAnswer", isAnswer);
				
				int totalRows = qnaService.getQuestionCountByfilter(mapForTotalRows); 
				
				// 페이저 객체 생성(페이지 당 행 수, 그룹 당 페이지 수, 전체 행 수, 현재 페이지 번호)
				Pager pager= pagerService.preparePager(session, pageNo, totalRows, 10, 5, "adminCustomerInquiry");
				
				Map<String, Object> mapForList = new HashMap<>();
				mapForList.put("pager",pager);
				mapForList.put("filter", filter);
				mapForList.put("sort", sort);
				mapForList.put("isAnswer", isAnswer);
				
				// 해당 페이지의 게시물 목록 가져오기
				List<Question> list = qnaService.getQuestionList(mapForList);
				
				// 여러 객체를 리턴하기 위해 Map 객체 생성		
				Map<String, Object> map = new HashMap<>();
							
				// map에 데이터 넣기
				map.put("question", list);
				map.put("pager", pager);

				
				return map;
			
			 // return 값은 JSON으로 변환되어 응답 본문에 들어간다. {"pager":{}, "notice":[]};
//		} return "관리자 권한이 없습니다.";
	}
	
	//@PreAuthorize("hasAuthority('ROLE_USER')") // 메소드를 실행하기 전에 권한을 체크한다.
	@GetMapping("/qattach/{qnumber}")
	public void download(@PathVariable int qnumber, HttpServletResponse response) {
		// 해당 게시물 가져오기
		Question question = qnaService.getQuestionImgByQnumber(qnumber);
		log.info("가져왔니?"+question.toString());
		
		// 파일 이름이 한글일 경우, 브라우저에서 한글 이름으로 다운로드 받기 위한 코드
		try {
			String fileName = new String(question.getQattachoname().getBytes("UTF-8"), "ISO-8859-1");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			
			// 파일 타입을 헤더에 추가
			response.setContentType(question.getQattachtype());
			
			// 응답 바디에 파일 데이터를 출력
			OutputStream os = response.getOutputStream();
			os.write(question.getQattachdata());
			os.flush();
			os.close();
			
		} catch (IOException e) {
			log.error(e.toString());
		}
	}
	
	// 문의 작성자 이름 가져오기
	@GetMapping("/getQuestionWriter")
	public String getQuestionWriter(int qUnumber) {
		String qwriter = qnaService.getWriterByQunumber(qUnumber);
		return qwriter;
	}

	
//	-------------------------------------------------------------------------------------------------------------------------------------
	// Answer 문의사항 답변
	
	// 문의 답변 생성
	@PostMapping("/AdminInquiryAnswer")
	public Answer createAnswer(Answer answer) {
		qnaService.insertAnswer(answer);
		qnaService.insertIsAnswer(answer.getAQnumber()); // 답변을 생성하면 question 테이블에 답변 여부를 넣어준다.
		return answer;
	}
	
	// 문의 답변 수정
	@PutMapping("/AdminInquiryAnswer")
	public Answer updateAnswer(Answer answer) {
		qnaService.updateAnswer(answer);
		// 읽기 메소드 만들어서 수정한거 DB에서 새로 불러오기
		return answer;
	}
	
	// 문의 답변 읽기
	@GetMapping("/AdminInquiryAnswerDetail")
	public Answer readAnswer(int aQnumber) {
		return qnaService.getAnswerByAqnumber(aQnumber);
	}
	
	// 문의 답변 삭제
	@DeleteMapping("/AdminInquiryAnswerDelete")
	public int deleteAnswer(int anumber, int qnumber) {
		qnaService.updateRemoveAnswer(qnumber);
		return qnaService.deleteAnswer(anumber);
	}
	
//	-------------------------------------------------------------------------------------------------------------------------------------
	// Notice 공지 사항
	
	// 공지 사항 생성
	@PostMapping("/NoticeForm")
	public Notice createNotice(Notice notice) {
		qnaService.insertNotice(notice);
		return notice;
	}
	
	// 공지 사항 읽기
	@GetMapping("/NoticeDetail")
	public Notice getNotice(int nnumber) {
		return qnaService.getNoticeDetail(nnumber);
	}
	
	// 공지 사항 수정
	@PutMapping("NoticeForm")
	public Notice updateNotice(Notice notice) {
		
		// 공지 사항 수정
		qnaService.updateNotice(notice);
		
		// 수정 후 수정 된 공지사항 가져오기
		notice =  qnaService.getNoticeDetail(notice.getNnumber());
		
		return notice;
	}
	
	// 공지 사항 삭제
	@DeleteMapping("/NoticeDetailDelete")
	public int deleteNotice(int nnumber) {
		//log.info("nnumber: "+nnumber);
		return qnaService.deleteNotice(nnumber);
	}
		
	// 공지 사항 검색 및 정렬
	@GetMapping("/NoticeList")
	public Map<String, Object> searchNoticeList(@RequestParam(defaultValue = "1") String pageNo
			, @RequestParam(required = false) String searchKeyword
			, @RequestParam(required = false) String sort, HttpSession session)
		{
		// 받아온 값 확인
		log.info("매개변수: "+pageNo+ searchKeyword+ sort);
		
		// 검색 된 공지사항 갯수
		int totalNoticeRows;
		
		// 검색어가 없으면 전체 공지사항의 갯수를 반환하고 아니면 검색된 공지사항의 갯수를 반환
		if(searchKeyword.equals("") || searchKeyword == null) {
			totalNoticeRows = qnaService.getCountNotice();
		}else {
			totalNoticeRows = qnaService.getCountOfSearchedNotices(searchKeyword);
		}
		
		log.info(totalNoticeRows+"총 갯수");
		
		// 페이저 객체 생성(페이지 당 행 수, 그룹 당 페이지 수, 전체 행 수, 현재 페이지 번호)
		Pager pager= pagerService.preparePager(session, pageNo, totalNoticeRows, 10, 5, "Notice");
		
		log.info(pager.getTotalPageNo()+"총 페이지 넘버");
		
		// 검색된 페이지 불러오기 위한 Map 생성 (Mybatis는 parameter을 하나의 타입만 보낼 수 있어서 map에 담아서 매개변수를 넣는다.)
		Map<String, Object> mapForSearch = new HashMap<>();
		mapForSearch.put("pager", pager);
		mapForSearch.put("searchKeyword", searchKeyword);
		mapForSearch.put("sort", sort);
		
		// 해당 페이지의 게시물 목록 가져오기
		List<Notice> list = qnaService.getSearchedNoticeList(mapForSearch);
		
		// 여러 객체를 리턴하기 위해 Map 객체 생성		
		Map<String, Object> map = new HashMap<>();
		
		// map에 데이터 넣기
		map.put("notice", list);
		map.put("pager", pager);
		
		return map; // return 값은 JSON으로 변환되어 응답 본문에 들어간다. {"pager":{}, "notice":[]};
	}
	
	
}
