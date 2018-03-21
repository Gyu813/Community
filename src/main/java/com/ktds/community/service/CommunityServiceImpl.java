package com.ktds.community.service;

import java.util.ArrayList;
import java.util.List;

import com.ktds.community.dao.CommunityDao;
import com.ktds.community.vo.CommunityVO;

public class CommunityServiceImpl implements CommunityService {

	private CommunityDao communityDao;
	
	public void setCommunityDao(CommunityDao communityDao) {
		this.communityDao = communityDao;
	}
	
	@Override
	public List<CommunityVO> getAll() {
		return communityDao.selectAll();
	}

	@Override
	public boolean createCommunity(CommunityVO communityVO) {
		
		String body = communityVO.getBody();
		// \n --> <br/>
		body = body.replace("\n", "<br/>");
		communityVO.setBody(body);
		
		if ( filter(body) || filter(communityVO.getTitle()) ) {
			return false;
		}
		
		int insertCount = communityDao.insertCommunity(communityVO);
		
		return insertCount > 0;
	}

	@Override
	public CommunityVO getOne(int id) {
		
		return communityDao.selectOne(id);
	}
	
	@Override
	public int readMyCommunitiesCount(int userId) {
		return communityDao.selectMyCommunitiesCount(userId);
	}

	@Override
	public List<CommunityVO> readMyCommunities(int userId) {
		return communityDao.selectMyCommunities(userId);
	}

	
	@Override
	public int incrementViewCount(int id) {
		
		if ( communityDao.incrementViewCount(id) > 0 ) {
			return 1;
		}
		
		return 0;
	}

	@Override
	public int incrementRecommendCount(int id) {
		
		if ( communityDao.incrementRecommendCount(id) > 0 ) {
			return 1;
		}
		
		return 0;
	}

	@Override
	public boolean filter(String str) {
		
		List<String> blackList = new ArrayList<String>();
		blackList.add("욕");
		blackList.add("시");
		blackList.add("발");
		blackList.add("1식");
		blackList.add("종간나세끼");
		blackList.add("2식");
		
		// str ==> 남편은 2식이에요.
		String[] splitString = str.split(" ");
		
		for ( String word : splitString ) {
			for ( String blackString : blackList ) {
				if ( word.contains(blackString) ) {
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public boolean removeOne(int id) {
		if ( communityDao.deleteOne(id) > 0 ) {
			return true;
		}
		return false;
	}

	@Override
	public boolean updateCommunity(CommunityVO communityVO) {
		return communityDao.updateCommunity(communityVO) > 0;
	}

	@Override
	public boolean deleteCommunities(List<Integer> ids, int userId) {
		// TODO Auto-generated method stub
		return false;
	}

}
