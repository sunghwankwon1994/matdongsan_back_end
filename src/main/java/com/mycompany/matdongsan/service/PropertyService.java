package com.mycompany.matdongsan.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mycompany.matdongsan.dao.FavoriteDao;
import com.mycompany.matdongsan.dao.PropertyDao;
import com.mycompany.matdongsan.dao.PropertyDetailDao;
import com.mycompany.matdongsan.dao.PropertyListingDao;
import com.mycompany.matdongsan.dao.PropertyPhotoDao;
import com.mycompany.matdongsan.dao.ReportDao;
import com.mycompany.matdongsan.dao.UserCommentDao;
import com.mycompany.matdongsan.dto.Favorite;
import com.mycompany.matdongsan.dto.Pager;
import com.mycompany.matdongsan.dto.Property;
import com.mycompany.matdongsan.dto.PropertyDetail;
import com.mycompany.matdongsan.dto.PropertyListing;
import com.mycompany.matdongsan.dto.PropertyPhoto;
import com.mycompany.matdongsan.dto.Report;
import com.mycompany.matdongsan.dto.UserComment;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PropertyService {
	@Autowired
	private PropertyDao propertyDao;
	@Autowired
	private PropertyDetailDao propertyDetailDao;
	@Autowired
	private PropertyPhotoDao propertyPhotoDao;
	@Autowired
	private PropertyListingDao propertyListingDao;
	@Autowired
	private UserCommentDao commentDao;
	@Autowired
	private FavoriteDao favoriteDao;
	@Autowired
	private ReportDao reportDao;

	// 생성 - property and propertyDetail
	public void createProperty(Property property, PropertyDetail propertyDetail) {
		// property
		propertyDao.createPropertyByProperty(property);

		// propertyDetail
		propertyDetail.setPdPnumber(property.getPnumber()); // FK 값 주기
		propertyDetailDao.createPropertyByPropertyDetail(propertyDetail);
	}

	// 생성 - propertyPhoto
	public void createPropertyByPropertyPhoto(PropertyPhoto propertyPhoto) {
		propertyPhotoDao.createPropertyByPropertyPhoto(propertyPhoto);
	}

	public void updateProperty(Property property) {
		propertyDao.updatePropertyByProperty(property);
	}

	// 수정
	public void updateProperty(Property property, PropertyDetail propertyDetail) {
		// property
		propertyDao.updatePropertyByProperty(property);

		// propertyDetail
		propertyDetailDao.updatePropertyByPropertyDetail(propertyDetail);
	}

	// 수정 - propertyPhoto
	public void updatePropertyByPropertyPhoto(PropertyPhoto propertyPhoto) {
		if (propertyPhoto.getPpattach() != null && !propertyPhoto.getPpattach().isEmpty()) {
			propertyPhotoDao.updatePropertyByPropertyPhoto(propertyPhoto);
		}

	}

	// 읽기 - property
	public Property getPropertyDataByPnumber(int pnumber) {
		Property property = propertyDao.selectByPnumber(pnumber);
		propertyDao.updatePhitcount(pnumber);
		return property;
	}

	// 읽기 - propertyDetail
	public PropertyDetail getPropertyDetail(int pdnumber) {
		PropertyDetail propertyDetail = propertyDetailDao.selectByPdnumber(pdnumber);
		return propertyDetail;
	}

	// 읽기 - propertyPhoto
	public PropertyPhoto getPropertyPhoto(int ppnumber) {
		PropertyPhoto propertyPhoto = propertyPhotoDao.selectByPpnumber(ppnumber);
		return propertyPhoto;
	}

	// pk 값 가져오기 - propertyDetail
	public int getPdnumber(int pdPnumber) {
		int pdnumber = propertyDetailDao.selectPdnumberByPnumber(pdPnumber);
		return pdnumber;
	}

	// pk 값 가져오기 - propertyPhoto
	public List<Integer> getPpnumbers(int ppPnumber) {
		List<Integer> ppnumbers = propertyPhotoDao.selectPpnumbersByPnumber(ppPnumber);
		return ppnumbers;
	}

	// 수정 시 propertyPhoto 삭제
	public int deletePropertyPhoto(int ppnumber) {
		return propertyPhotoDao.deleteByPpnumber(ppnumber);
	}

	// property 전체 개수
	public int getAllPropertyCount() {
		int totalPropertyRows = propertyDao.getAllPropertyCount();
		return totalPropertyRows;
	}

	// property 개수 by filter and keyword
	public int getPropertyCountByFilter(String keyword, String price, String date, String rentType,  String floorType, String lat,
			String lng) {
		int totalPropertyRows = propertyDao.getPropertyCountByFilter(keyword, price, date, rentType, floorType, lat, lng);
		return totalPropertyRows;
	}

	// property 전체 리스트
	public List<Property> getAllPropertyList(int offset, int limit) {
		List<Property> propertyList = propertyDao.getAllPropertyList(offset, limit);
		return propertyList;
	}

	// property 리스트 by filter and keyword
	public List<Property> getPropertyListByFilter(int offset, int limit, String keyword, String price, String date,
			String rentType,  String floorType, String lat, String lng) {
		String latPrefix = (lat != null && !lat.isEmpty())
				? lat.substring(0, Math.min(lat.length(), lat.indexOf('.') + 4))
				: null;
		String lngPrefix = (lng != null && !lng.isEmpty())
				? lng.substring(0, Math.min(lng.length(), lng.indexOf('.') + 5))
				: null;
		log.info(latPrefix);
		log.info(lngPrefix);
		log.info("rentType in service" + rentType);
		List<Property> propertyList = propertyDao.getPropertyListByFilter(offset, limit, keyword, price, date, rentType,
				 floorType, latPrefix, lngPrefix);

		return propertyList;
	}

	// 삭제
	public void deleteProperty(int pnumber) {
		// propertyDetail
		propertyDetailDao.deletePropertyDetailByPdPnumber(pnumber);

		// propertyPhoto
		propertyPhotoDao.deletePropertyPhotoByPpPnumber(pnumber);

		// property
		propertyDao.deletePropertyByPnumber(pnumber);
	}

	public void readProperty(int pnumber) {
		// TODO Auto-generated method stub

	}

	// 읽기 - propertyDetail
	public PropertyDetail getPropertyDetailByPdPnumber(int pnumber) {
		PropertyDetail propertyDetail = propertyDetailDao.selectPropertyDetailByPdPnumber(pnumber);
		return propertyDetail;
	}

	// 읽기 - propertyDetail
	public List<Integer> getPropertyPhotoByPpPnumber(int pnumber) {
		List<Integer> propertyPhoto = propertyPhotoDao.selectPropertyPhotoByPpPnumber(pnumber);
		return propertyPhoto;
	}

	// 상품 구매
	public void purchasePropertyListing(PropertyListing propertyListing) {
		propertyListingDao.createPropertyListing(propertyListing);

	}

	public boolean checkPropertyCondition(int userNumber) {
		return propertyListingDao.checkUserDataInPropertyListing(userNumber) > 0 ? true : false;
	}

	// 댓글 작성 시 매물 주인 여부
	public boolean isPropertyOwner(int pnumber, int userNumber) {
		int propertyCount = propertyDao.isPropertyOwnerByComment(pnumber, userNumber);
		if (propertyCount == 0) {
			return false;
		} else {
			return true;
		}
	}

	// 댓글 생성
	public void createPropertyComment(UserComment comment) {

		log.info(comment.toString());
		commentDao.createPropertyComment(comment);

	}

	public boolean isFirstCommentOwner(int cUnumber, int pnumber) {
		// TODO Auto-generated method stub
		return false;
	}

	// 댓글 삭제
	public void deletePropertyComment(int pnumber, int cnumber, int userNumber) {
		commentDao.deletePropertyComment(pnumber, cnumber, userNumber);
	}

	// 자식 댓글 존재 여부
	public boolean isComment(int ucnumber, int pnumber) {
		return commentDao.isChildComment(ucnumber, pnumber) != 0 ? true : false;
	}

	// 댓글 정보 가져오기
	public UserComment getCommentByCnumber(int cnumber) {
		return commentDao.getCommentByCnumber(cnumber);
	}

	// 댓글 수정
	public void updatePropertyComment(UserComment userComment) {
		commentDao.updatePropertyComment(userComment);

	}

	// 등록권 개수 수정
	public void updateRemainPropertyListing(int userNumber) {
		propertyListingDao.updateRemainPropertyListing(userNumber);

	}

	// 해당 상품에 대한 댓글 총 개수
	public int getAllPropertyCommentCount(int pnumber) {
		return commentDao.getTotalCommentCount(pnumber);
	}

	// 댓글 리스트
	public List<UserComment> getCommentByPnumber(int pnumber, String date, Pager pager) {
		return commentDao.getCommentByPager(pnumber, date, pager);
	}

	// 좋아요 추가
	public void addLikeButton(Favorite favorite) {
		favoriteDao.addLikeButton(favorite);
	}

	// 좋아요 여부
	public boolean existsFavorite(int pnumber, int mnumber) {
		return favoriteDao.existsFavorite(pnumber, mnumber) != 0 ? true : false;
	}

	// 좋아요 취소
	public void cancelLikeButton(int pnumber, int userNumber) {
		favoriteDao.cancelLikeButton(pnumber, userNumber);
	}

	// 매물 신고
	public void createPropertyReport(Report report) {
		reportDao.createPropertyReport(report);
	}

	// 유저 매물 리스트
	public List<Property> getAllUserPropertyList(int unumber, Pager pager) {
		return propertyDao.getUserPropertyListByUnumber(unumber, pager);
	}

	// 유저 매물 총 개수
	public int getAllUserPropertyCount(int unumber) {
		return propertyDao.getAllUserPropertyCountByUnumber(unumber);
	}

	// 인기있는 매물
	public List<Property> getPopularPropertyList() {
		return propertyDao.getPopularPropertyListByHitcount();
	}

	public boolean checkPropertyListingHistory(int userNumber) {
		return propertyListingDao.checkUserDataInPropertyListing(userNumber) > 0 ? true : false;
	}

	// 유저 허위 신고 총 개수
	public int getAllUserReportCount(int unumber) {
		return reportDao.getAllUserReportCountByUnumber(unumber);
	}

	// 유저 허위 매물 리스트
	public List<Report> getAllUserReportList(int unumber, Pager pager,String filterKeyword) {
		return reportDao.getUserReportListByUnumber(unumber, pager,filterKeyword);
	}

	// 허위 매물 신고 삭제
	public void deletePropertyReport(int pnumber, int unumber) {
		reportDao.deleteUserReport(pnumber, unumber);
	}

	// 허위 매물 신고 여부
	public boolean checkPropertyReport(int unumber, int pnumber) {
		return reportDao.checkUserPropertyReport(unumber, pnumber) > 0 ? true : false;
	}

	public int getUserPropertyListingQuantity(int userNumber) {
		boolean hasUser = propertyListingDao.checkPropertyListingByUserNumber(userNumber) > 0 ? true : false;
		if (hasUser) {
			int quantity = propertyListingDao.getQuantityByUserNumber(userNumber);
			return quantity;
		}
		return 0;
	}

	// 매물 등록권 잔여 개수
	public int getUserPropertyListingRemain(int userNumber) {
		boolean hasUser = propertyListingDao.checkPropertyListingByUserNumber(userNumber) > 0 ? true : false;
		if (hasUser) {
			int Remain = propertyListingDao.getRemainByUserNumber(userNumber);
			return Remain;
		}
		return 0;
	}

	public int getTradeCountByAgentWithUserNumber(int userNumber) {
		int count = propertyDao.getTradeCountByUserNumber(userNumber);
		return count;
	}

	// 좋아요 리스트
	public List<Favorite> getAllUserFavoriteList(int mnumber,int offset, int limit) {
		return favoriteDao.getUserFavoriteList(mnumber,offset, limit);
	}

	// 좋아요 총 개수
	public int getAllFavoriteCount(int mnumber) {
		return favoriteDao.getUserFavoriteCountByUnumber(mnumber);
	}

	// 매물 상태 수정
	public void updatePropertyStatus(int pnumber, String pstatus) {
		propertyDao.updatePropertyStatus(pnumber, pstatus);

	}
	// 매물 전체 데이터 (페이저X)
	public List<Property> getAllPropertyListWithoutPager() {
		// TODO Auto-generated method stub
		return propertyDao.getAllPropertyListWithoutPager();
	}

	public int getPnumberByPropertyPosition(String lat, String lng) {
		int pnumber = propertyDao.getPnumberByPropertyPosition(lat,lng);
		log.info("pnumber in service: "+pnumber);
		return pnumber;
	}

	public List<Property> getAllUserPropertyListByFilter(int unumber, Pager pager, String filterKeyword) {
		return propertyDao.getUserPropertyListByUnumberAndFilter(unumber, pager, filterKeyword);
	}

	public int getAllUserPropertyCountByFilter(int unumber, String filterKeyword) {
		return propertyDao.getAllUserPropertyCountByUnumberAndFilter(unumber, filterKeyword);
	}

}
