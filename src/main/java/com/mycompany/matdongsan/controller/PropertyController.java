package com.mycompany.matdongsan.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mycompany.matdongsan.dto.Favorite;
import com.mycompany.matdongsan.dto.Member;
import com.mycompany.matdongsan.dto.Pager;
import com.mycompany.matdongsan.dto.Property;
import com.mycompany.matdongsan.dto.PropertyDetail;
import com.mycompany.matdongsan.dto.PropertyListing;
import com.mycompany.matdongsan.dto.PropertyPhoto;
import com.mycompany.matdongsan.dto.Report;
import com.mycompany.matdongsan.dto.TotalProperty;
import com.mycompany.matdongsan.dto.UserComment;
import com.mycompany.matdongsan.service.AgentService;
import com.mycompany.matdongsan.service.MemberService;
import com.mycompany.matdongsan.service.PagerService;
import com.mycompany.matdongsan.service.PropertyService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/Property")
public class PropertyController {
	@Autowired
	private PropertyService propertyService;
	@Autowired
	private MemberService memberService;
	@Autowired
	private AgentService agentService;
	@Autowired
	private PagerService pagerService;

//  리스트
	@GetMapping("")
	public Map<String, Object> getPropertyList(@RequestParam(defaultValue = "1") int pageNo,
			@RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String keyword,
			@RequestParam(required = false) String price, @RequestParam(required = false) String date,
			@RequestParam(required = false) String rentType, @RequestParam(required = false) String floorType,
			@RequestParam(required = false) String lat,@RequestParam(required = false) String lng) {
		
		// 검색 내용 찾기 : 주소, 필터 : price, date, rentType, floorType
		int totalPropertyRows;
		Pager pager;
		List<Property> propertyList = new ArrayList<>();
		
		if (keyword != null || price != null || date != null || floorType != null || rentType != null || lat != "" || lng != "") {
			totalPropertyRows = propertyService.getPropertyCountByFilter(keyword, price, date, rentType, floorType, lat, lng);
			pager = new Pager(size, pageNo, totalPropertyRows);
			propertyList = propertyService.getPropertyListByFilter(pager.getStartRowIndex(), pager.getRowsPerPage(),
					keyword, price, date, rentType, floorType, lat, lng);
			log.info("propertyList.size() : " + propertyList.size());

		} else { // 전체 리스트
			totalPropertyRows = propertyService.getAllPropertyCount();
			pager = new Pager(size, pageNo, totalPropertyRows);
			propertyList = propertyService.getAllPropertyList(pager.getStartRowIndex(), pager.getRowsPerPage());
		}
		// 지도 표시를 위한 전체 매물 리스트 ( 페이저 & 페이지네이션 )
		List<Property> propertyTotalList = propertyService.getAllPropertyListWithoutPager();
		log.info(propertyTotalList.size() + "");
		// 여러 객체를 리턴하기 위해 map 객체 생성 (property, pager)
		Map<String, Object> map = new HashMap<>();
		map.put("propertyTotalList", propertyTotalList);
		map.put("property", propertyList);
		map.put("pager", pager);
		return map; // { "property" : {}, "pager" : {}}
	}

//	유저 매물 리스트
	@GetMapping("/Mypage/ManageMyProperty")
	public Map<String, Object> getUserPropertyList(@RequestParam(defaultValue = "1", required = false) String pageNo, @RequestParam(defaultValue = "desc", required = false) String filterKeyword,
			Authentication authentication, HttpSession session) {

		String uemail = authentication.getName();
		int unumber = memberService.getUnumberByUemail(uemail);
		
		log.info("received keyword : " + filterKeyword);
		
		int totalUserPropertyRows = propertyService.getAllUserPropertyCountByFilter(unumber, filterKeyword);
		Pager pager = pagerService.preparePager(session, pageNo, totalUserPropertyRows, 5, 5, "userPropertyList");
		List<Property> userPropertyList = propertyService.getAllUserPropertyListByFilter(unumber, pager, filterKeyword);
		
		log.info("size : " + totalUserPropertyRows);
		
		Map<String, Object> userPropertyMap = new HashMap<>();
		userPropertyMap.put("userPropertyList", userPropertyList);
		userPropertyMap.put("pager", pager);
		
		return userPropertyMap;
	}
	
	//pnumber에 따른 데이터 가져오기
	@GetMapping("/DetailData/{pnumber}")
	public Property getPropertyDataByPnumber(@PathVariable int pnumber) {
		log.info("실행");
		log.info("pnumber: "+pnumber+"");
		Property propertyData = propertyService.getPropertyDataByPnumber(pnumber);
		log.info(propertyData.getPtitle());
		return propertyData;
	}
	// 좌표에 따른 pnumber 가져오기
	@GetMapping("/Position")
	public int getAgentNumberByAgentPosition(@RequestParam String lat, @RequestParam String lng) {
		int pnumber = propertyService.getPnumberByPropertyPosition(lat, lng);
		return pnumber;
	}

//	읽기
	@GetMapping("/{pnumber}")
	public Map<String, Object> readProperty(@PathVariable int pnumber, @ModelAttribute TotalProperty totalProperty,
			@RequestParam(defaultValue = "1", required = false) String pageNo,
			@RequestParam(defaultValue = "desc", required = false) String date, HttpSession session) {

		// property 정보
		totalProperty.setProperty(propertyService.getPropertyDataByPnumber(pnumber));
		totalProperty.setPropertyDetail(propertyService.getPropertyDetailByPdPnumber(pnumber));
		
		// photos는 여러 개라서 따로 리스트 설정
		List<Integer> propertyPhotos = propertyService.getPropertyPhotoByPpPnumber(pnumber);
	
		// property Comment
		int totalPropertyCommentRows = propertyService.getAllPropertyCommentCount(pnumber);
		Pager pager = pagerService.preparePager(session, pageNo, totalPropertyCommentRows, 5, 5, "propertyComment");
		List<UserComment> propertyCommentList = propertyService.getCommentByPnumber(pnumber, date, pager);

		Map<String, Object> propertyMap = new HashMap<>();
		propertyMap.put("totalProperty", totalProperty);
		propertyMap.put("propertyCommentList", propertyCommentList);
		propertyMap.put("propertyPhotos", propertyPhotos);
		propertyMap.put("pager", pager);
		
		return propertyMap;
	}

//	등록
//	@PreAuthorize("hasAuthority('ROLE_USER')")
	@Transactional
	@PostMapping("/PropertyForm")
	public boolean createProperty(@ModelAttribute TotalProperty totalProperty, Authentication authentication)
			throws IOException {
		
		log.info("totalProperty 받아온 data in 등록 : " + totalProperty.toString());
		
		String userEmail = authentication.getName();
		int userNumber = memberService.getUnumberByUemail(userEmail);

		// 유저가 이전에 결제한 적 있는지, 있다면 남아있는 개수가 있는지
		boolean hasPropertyListing = propertyService.checkPropertyCondition(userNumber);

		if (hasPropertyListing) {

			Property property = totalProperty.getProperty();
			PropertyDetail propertyDetail = totalProperty.getPropertyDetail();
			PropertyPhoto propertyPhoto = totalProperty.getPropertyPhoto();
			// 사용자 설정
			// 추후 authentication 설정하기
			property.setPUnumber(userNumber);
			property.setPstatus("활성화");

			// property 파일 첨부 여부
			if (property.getPthumbnail() != null && !property.getPthumbnail().isEmpty()) {
				MultipartFile mf = property.getPthumbnail();
				property.setPthumbnailoname(mf.getOriginalFilename());
				property.setPthumbnailtype(mf.getContentType());
				property.setPthumbnaildata(mf.getBytes());
			}

			propertyService.createProperty(property, propertyDetail);

			// propertyPhoto 파일 첨부 여부
			if (propertyPhoto.getPpattach() != null && !propertyPhoto.getPpattach().isEmpty()) {
				List<MultipartFile> files = propertyPhoto.getPpattach();
				if (files != null && !files.isEmpty()) {
					for (MultipartFile file : files) {
						log.info(file.getOriginalFilename());
						propertyPhoto.setPpattachoname(file.getOriginalFilename());
						propertyPhoto.setPpattachtype(file.getContentType());
						propertyPhoto.setPpattachdata(file.getBytes());
						propertyPhoto.setPpPnumber(property.getPnumber()); // FK 값 주기
						propertyService.createPropertyByPropertyPhoto(propertyPhoto);
					}
				}
			}

			// JSON으로 변환되지 않는 필드는 null 처리
			property.setPthumbnail(null);
			property.setPthumbnaildata(null);
			propertyPhoto.setPpattach(null);
			propertyPhoto.setPpattachdata(null);

			propertyService.updateRemainPropertyListing(userNumber);

			return true;
		} else { // 등록권 없음
			// 등록권 소개 페이지로 이동
			return false;
		}

	}

//	수정
//	@PreAuthorize("hasAuthority('ROLE_USER')")
	@PutMapping("/PropertyForm/{pnumber}")
	public TotalProperty updateProperty(@PathVariable int pnumber, @ModelAttribute TotalProperty totalProperty)
			throws IOException {

		Property property = totalProperty.getProperty();
		PropertyDetail propertyDetail = totalProperty.getPropertyDetail();
		

		// PK 값 가져오기
		propertyDetail.setPdnumber(propertyService.getPdnumber(pnumber));

		// property 파일 첨부 여부
		if (property.getPthumbnail() != null && !property.getPthumbnail().isEmpty()) {
			MultipartFile mf = property.getPthumbnail();
			property.setPthumbnailoname(mf.getOriginalFilename());
			property.setPthumbnailtype(mf.getContentType());
			property.setPthumbnaildata(mf.getBytes());
		}
		
		propertyService.updateProperty(property, propertyDetail);

		// propertyPhoto 파일 첨부 여부
		if (totalProperty.getPropertyPhoto() != null) {
			PropertyPhoto propertyPhoto = totalProperty.getPropertyPhoto();
			List<Integer> ppnumbers = propertyService.getPpnumbers(pnumber); // pk 값 가져오기
			List<MultipartFile> files = propertyPhoto.getPpattach();
			log.info("files.size() : " + files.size());
			int existingPhotosCount = ppnumbers.size();
			int newFilesCount = files.size();

			for (int i = 0; i < newFilesCount; i++) {
				MultipartFile file = files.get(i);
				propertyPhoto.setPpattachoname(file.getOriginalFilename());
				propertyPhoto.setPpattachtype(file.getContentType());
				propertyPhoto.setPpattachdata(file.getBytes());
				if (i < existingPhotosCount) {
					// 기존 사진을 업데이트하는 경우
					propertyPhoto.setPpnumber(ppnumbers.get(i));
					propertyService.updatePropertyByPropertyPhoto(propertyPhoto);
				} else {
					// 새로운 사진을 추가하는 경우
					propertyPhoto.setPpnumber(0); // 새로운 사진의 경우 ppnumber는 0(null)로 설정하고, DB에서 자동 생성되도록 처리
					propertyPhoto.setPpPnumber(pnumber); // FK 값 주기
					propertyService.createPropertyByPropertyPhoto(propertyPhoto);
				}
			}

			// 기존 사진 중 남은 사진은 삭제 처리 (newFilesCount < existingPhotosCount 인 경우)
			for (int i = newFilesCount; i < existingPhotosCount; i++) {
				propertyService.deletePropertyPhoto(ppnumbers.get(i));
			}
			totalProperty.setPropertyPhoto(propertyService.getPropertyPhoto(propertyPhoto.getPpnumber()));
			propertyPhoto.setPpattach(null);
			propertyPhoto.setPpattachdata(null);
		}

		// totalProperty 객체에 수정된 내용 다시 설정
		totalProperty.setProperty(propertyService.getPropertyDataByPnumber(pnumber));
		totalProperty.setPropertyDetail(propertyService.getPropertyDetail(propertyDetail.getPdnumber()));


		// JSON으로 변환되지 않는 필드는 null 처리
		property.setPthumbnail(null);
		property.setPthumbnaildata(null);


		return totalProperty;
	}
	

//	삭제
//	@PreAuthorize("hasAuthority('ROLE_USER')")	
	@Transactional
	@DeleteMapping("/deleteProperty/{pnumber}")
	public void deleteProperty(@PathVariable int pnumber) {
		propertyService.deleteProperty(pnumber);
	}

//	상태 변경 (비활성화, 거래완료)
	@PutMapping("/updatePropertyStatus/{pnumber}")
	public void updatePropertyStatus(@PathVariable int pnumber, @RequestParam String pstatus) {
		log.info("pnumber : " + pnumber);
		log.info("pstatus + pnumber : " + pstatus);

		propertyService.updatePropertyStatus(pnumber, pstatus);
	}

//	댓글 생성
	@PostMapping("/{pnumber}")
	public UserComment createPropertyComment(@PathVariable int pnumber, @ModelAttribute UserComment userComment,
			Authentication authentication) {
		log.info("pnumber in boot : " + pnumber);
		String userEmail = authentication.getName();
		String userRole = memberService.getUserRole(userEmail);
		int userNumber = memberService.getUnumberByUemail(userEmail);
		boolean isPropertyOwner = propertyService.isPropertyOwner(pnumber, userNumber); // 매물 주인 여부
		log.info(isPropertyOwner + "");
		if (userComment.getUcparentnumber() == 0) { // 부모 댓글 없음
			if (!userRole.equals("MEMBER") || isPropertyOwner) {
				// agent 또는 매물 주인이면 댓글 못달게 처리하기
			} else {
				userComment.setUcUnumber(userNumber);
			}
			userComment.setUcparentnumber(0);
		} else { // 부모 댓글 있음
			if (userRole.equals("MEMBER")) { // 기존 댓글 주인 여부 파악하기 위해 member / agent 나눠서 처리
				boolean isFirstCommentOwner = propertyService.isFirstCommentOwner(userComment.getUcUnumber(), pnumber);
				if (!isFirstCommentOwner) {
					// 댓글 주인 아닌 경우 못달게 처리
				} else {
					userComment.setUcUnumber(userNumber);
				}
			} else { // agent인 경우
				if (isPropertyOwner) {
					userComment.setUcUnumber(userNumber);
				} else {
					// 올린 사람 아니면 댓글 못달게 하기
				}
			}
		}
		// 유저 넘버 없음
		userComment.setUcUnumber(userNumber);
		userComment.setUcPnumber(pnumber);
		userComment.setUcremoved(false);
		log.info(userComment.toString() + "userComment 실행 중");
		propertyService.createPropertyComment(userComment);
		log.info(userComment.toString() + "userComment 실행 끝");

		return userComment;
	}

//	댓글 수정
	@PutMapping("/{pnumber}/{ucnumber}")
	public UserComment updatePropertyComment(@PathVariable int pnumber, @PathVariable int ucnumber,
			@ModelAttribute UserComment userComment, Authentication authentication) {

		String userEmail = authentication.getName();
		int userNumber = memberService.getUnumberByUemail(userEmail);

		userComment.setUcnumber(ucnumber);
		userComment.setUcUnumber(userNumber);
		userComment.setUcPnumber(pnumber);
		propertyService.updatePropertyComment(userComment);
		return userComment;
	}

//	댓글 삭제
	@DeleteMapping("/{pnumber}/{ucnumber}")
	public void deletePropertyComment(@PathVariable int pnumber, @PathVariable int ucnumber,
			Authentication authentication) {

		String userEmail = authentication.getName();
		int userNumber = memberService.getUnumberByUemail(userEmail);
		UserComment comment = propertyService.getCommentByCnumber(ucnumber);

		// 자식 댓글 존재 여부
		boolean isComment = propertyService.isComment(ucnumber, pnumber);
		if (isComment) {
			comment.setUcremoved(true);
			propertyService.updatePropertyComment(comment);
		} else {
			propertyService.deletePropertyComment(pnumber, ucnumber, userNumber);
		}
	}

//	상품 좋아요 추가
	@PostMapping("/likeProperty/{pnumber}")
	public boolean addLikeButton(@PathVariable int pnumber, @ModelAttribute Favorite favorite,
			Authentication authentication) {

		String userEmail = authentication.getName();
		int memberNumber = memberService.getMemberNumberByMemberEmail(userEmail);

		boolean existsFavorite = propertyService.existsFavorite(pnumber, memberNumber); // 이미 좋아요 눌렀는지

		if (!existsFavorite) { // 좋아요 존재하지 않아서 추가하기
			favorite.setFPnumber(pnumber);
			favorite.setFMnumber(memberNumber);
			propertyService.addLikeButton(favorite);
			log.info("좋아요 추가 완료");
			return true;
		} else {
			return false;
		}

	}

//	상품 좋아요 리스트
	@GetMapping("/FavoriteProperty")
	public Map<String, Object> favoriteList(@RequestParam(defaultValue = "1") int pageNo,
			@RequestParam(defaultValue = "10") int size, Authentication authentication) {
		log.info("관심 실행");
		String uemail = authentication.getName();
		int unumber = memberService.getUnumberByUemail(uemail);
		Member member = memberService.getMemberDataFullyByUserNumber(unumber);
		int mnumber =member.getMnumber();
		int totalFavoriteRows = propertyService.getAllFavoriteCount(mnumber);
		Pager pager = new Pager(size, pageNo, totalFavoriteRows);
		List<Favorite> favoritePropertyList = propertyService.getAllUserFavoriteList(mnumber,pager.getStartRowIndex(),
				pager.getRowsPerPage());
		Map<String, Object> map = new HashMap<>();
		map.put("favorite", favoritePropertyList);
		map.put("pager", pager);
		return map; // { "favorite" : {}, "pager" : {}}
	}

//	상품 좋아요 취소
	@DeleteMapping("/cancelLikeProperty/{pnumber}")
	public void cancelLikeButton(@PathVariable int pnumber, @ModelAttribute Favorite favorite,
			Authentication authentication) {

		String userEmail = authentication.getName();
		int memberNumber = memberService.getMemberNumberByMemberEmail(userEmail);

		propertyService.cancelLikeButton(pnumber, memberNumber);
		log.info("좋아요 취소 완료");
	}

	// 좋아요 여부
	@GetMapping("/isPropertyLiked/{pnumber}")
	public Boolean isPropertyLiked(@PathVariable int pnumber, Authentication authentication) {
		String userEmail = authentication.getName();
		int memberNumber = memberService.getMemberNumberByMemberEmail(userEmail);

		return propertyService.existsFavorite(pnumber, memberNumber);

//	    Map<String, Boolean> response = new HashMap<>();
//	    response.put("liked", isLiked);
//
//	    return response;
	}

//	매물 신고
	@PostMapping("/createPropertyReport/{pnumber}")
	public Boolean createPropertyReport(@PathVariable int pnumber, @ModelAttribute Report report,
			Authentication authentication) {

		log.info("report : " + report.toString());
		String userEmail = authentication.getName();
		int userNumber = memberService.getUnumberByUemail(userEmail);

		// 유저가 이전에 신고한 적 있는지
		boolean hasPropertyReport = propertyService.checkPropertyReport(userNumber, pnumber);

		if (!hasPropertyReport) {
			report.setRPnumber(pnumber);
			report.setRUnumber(userNumber);
			propertyService.createPropertyReport(report);
			return true;
		} else {
			return false;
		}

	}
	

//	매물 신고 여부
	@GetMapping("/isReported/{pnumber}")
	public Boolean isReported(@PathVariable int pnumber, Authentication authentication) {
		String userEmail = authentication.getName();
		int userNumber = memberService.getUnumberByUemail(userEmail);

		// 유저가 이전에 신고한 적 있는지
		boolean hasPropertyReport = propertyService.checkPropertyReport(userNumber, pnumber);

		if (hasPropertyReport) { 
			return true;
		} else {
			return false;
		}
		
	}

//	매물 신고 삭제
//	@PreAuthorize("hasAuthority('ROLE_USER')")	
	@DeleteMapping("/deletePropertyReport/{pnumber}")
	public void deletePropertyReport(@PathVariable int pnumber, Authentication authentication) {

		log.info("pnumber : " + pnumber);
		String uemail = authentication.getName();
		int unumber = memberService.getUnumberByUemail(uemail);

		propertyService.deletePropertyReport(pnumber, unumber);
	}

//	유저 매물 신고 리스트
	@GetMapping("/Mypage/ReportFalseListing")
	public Map<String, Object> getUserReportList(@RequestParam(defaultValue = "1", required = false) String pageNo,@RequestParam(defaultValue = "desc", required = false) String filterKeyword,
			Authentication authentication, HttpSession session) {

		String uemail = authentication.getName();
		int unumber = memberService.getUnumberByUemail(uemail);

		int totalUserReportRows = propertyService.getAllUserReportCount(unumber);
		Pager pager = pagerService.preparePager(session, pageNo, totalUserReportRows, 4, 5, "userReportList");
		List<Report> userReportList = propertyService.getAllUserReportList(unumber, pager,filterKeyword);
		
		Map<String, Object> userReportMap = new HashMap<>();
		userReportMap.put("userReportList", userReportList);
		userReportMap.put("pager", pager);
		
		return userReportMap;
	}

//	등록권 구매
	@PostMapping("/Payment/PaymentResult/{quantity}")
	public boolean purchasePropertyListing(Authentication authentication, @PathVariable int quantity) {
		int price = 5500;
		PropertyListing propertyListing = new PropertyListing();

		// 유저 번호
		String userName = authentication.getName();
		int userNumber = agentService.getUserIdByUserName(userName);

		boolean hasPropertyListing = propertyService.checkPropertyCondition(userNumber); // 유저가 이전에 결제한 적 있는지, 있다면 남아있는
																							// 개수가 있는지

		log.info(hasPropertyListing + "");
		if (!hasPropertyListing) {
			propertyListing.setPlquantity(quantity);
			propertyListing.setPlremain(quantity);
			propertyListing.setPlUnumber(userNumber);
			if (quantity > 1) {
				price = quantity * 5500 - (500 * quantity);
				propertyListing.setPlprice(price);
			} else {
				propertyListing.setPlprice(price);
			}
			log.info(propertyListing.toString());
			propertyService.purchasePropertyListing(propertyListing);
			return true; // 등록권이 없는 유저나 처음 구매하는 유저라면 true
		} else {
			log.info("이미 등록권이 존재합니다.");
			return false; // 아직 등록권이 존재하는 유저라면 false를 리턴
		}
	}

//	매물 썸네일 사진 다운로드
//	@PreAuthorize("hasAuthority('ROLE_USER')")
	@GetMapping("/pattach/{pnumber}")
	public void downloadPropertyThumbnail(@PathVariable int pnumber, HttpServletResponse response) {

		// 해당 게시물 가져오기
		Property property = propertyService.getPropertyDataByPnumber(pnumber);
		// 파일 이름이 한글일 경우, 브라우저에서 한글 이름으로 다운로드 받기 위한 코드
		try {
			String fileName = new String(property.getPthumbnailoname().getBytes("UTF-8"), "ISO-8859-1");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			// 파일 타입을 헤더에 추가
			response.setContentType(property.getPthumbnailtype());
			// 응답 바디에 파일 데이터를 출력
			OutputStream os = response.getOutputStream();
			os.write(property.getPthumbnaildata());
			os.flush();
			os.close();
		} catch (IOException e) {
			log.error(e.toString());
		}

	}

//	매물 디테일 사진 다운로드
	@GetMapping("/detailPattach/{ppnumber}")
	public void downloadPropertyDetailPattach(@PathVariable int ppnumber, HttpServletResponse response) {
	    PropertyPhoto propertyPhoto = propertyService.getPropertyPhoto(ppnumber);

	    try {
	        String fileName = new String(propertyPhoto.getPpattachoname().getBytes("UTF-8"), "ISO-8859-1");
	        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
	        response.setContentType(propertyPhoto.getPpattachtype());
	        
	        OutputStream os = response.getOutputStream();
	        os.write(propertyPhoto.getPpattachdata());
	        os.flush();
	        os.close();
	    } catch (IOException e) {
	        log.error(e.toString());
	    }
	}


//	메인페이지 인기 상품	
	@GetMapping("/popularProperty")
	public List<Property> getPopularProperty() {
		List<Property> popularPropertyList = propertyService.getPopularPropertyList();
		return popularPropertyList;
	}

	// 구매내역이 존재한다면 true 아니면 false
	@GetMapping("/paymentHistory")
	public boolean checkPaymentHistory(Authentication authentication) {
		String userEmail = authentication.getName();
		int userNumber = memberService.getUnumberByUemail(userEmail);
		boolean hasHistory = false;
		hasHistory = propertyService.checkPropertyListingHistory(userNumber);
		return hasHistory;
	}
	
	
	@GetMapping("/getListingRemain")
	public Map<String, Object> getListingRemain(Authentication authentication) {
		Map<String, Object> map = new HashMap<>();
		String userName = authentication.getName(); // 로그인 정보에서 이름 가져오기
		if(userName == null || userName.equals("")) {
			map.put("result", "noUser");
		} else {
			int userNumber = memberService.getUnumberByUemail(userName); // 가져온 이름으로 userNumber값 가져오기 
			boolean hasPropertyListing = propertyService.checkPropertyCondition(userNumber); // 유저가 이전에 결제한 적 있는지
			if(hasPropertyListing) {
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