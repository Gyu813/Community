package com.ktds.community.service;

import java.util.List;

import com.ktds.community.vo.CommunityVO;

public interface CommunityService {
	
	public List<CommunityVO> getAll();
	
	public CommunityVO getOne(int id);
	
	public int readMyCommunitiesCount(int userId);
	
	public List<CommunityVO> readMyCommunities(int userId);
	
	public boolean createCommunity(CommunityVO communityVO);
	
	public int incrementViewCount(int id);
	
	public int incrementRecommendCount(int id);
	
	public boolean filter(String str);
	
	public boolean removeOne(int id);
	
	public boolean updateCommunity(CommunityVO communityVO);
	
	public boolean deleteCommunities(List<Integer> ids, int userId);

}
