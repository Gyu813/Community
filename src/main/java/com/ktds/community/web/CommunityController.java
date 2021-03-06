package com.ktds.community.web;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.ktds.community.service.CommunityService;
import com.ktds.community.vo.CommunityVO;
import com.ktds.member.constants.Member;
import com.ktds.member.vo.MemberVO;
import com.ktds.util.DownloadUtil;

@Controller
public class CommunityController {
	
	private CommunityService communityService;
	
	public void setCommunityService(CommunityService communityService) {
		this.communityService = communityService;
	}

	@RequestMapping("/")
	public ModelAndView viewListPage(HttpSession session) {
		
		ModelAndView view = new ModelAndView();
		view.setViewName("community/list");
		
		List<CommunityVO> communityList = communityService.getAll();
		view.addObject("communityList", communityList);
		
		return view;
	}
	
	@RequestMapping(value = "/write", method = RequestMethod.GET)
	public String viewWritePage(HttpSession session) {
		
		return "community/write";
	}
	
	@RequestMapping(value = "/write", method = RequestMethod.POST)
	public ModelAndView doWrite(
			@ModelAttribute("writeForm")
			@Valid
			CommunityVO communityVO, Errors errors,
			HttpSession session, HttpServletRequest request) {
		
		if ( errors.hasErrors() ) {
			ModelAndView view = new ModelAndView();
			view.setViewName("community/write");
			view.addObject("communityVO", communityVO);
			return view;
		}
		
		// 작성자의 IP를 얻어오는 코드
		String requestorIp = request.getRemoteAddr();
		communityVO.setRequestIp(requestorIp);
		
		communityVO.save();
		
		boolean isSuccess = communityService.createCommunity(communityVO);
		
		if ( isSuccess ) {
			return new ModelAndView("redirect:/");
		}
		
		return new ModelAndView("redirect:/write");
	}
	
	@RequestMapping("/view/{id}")
	public ModelAndView viewViewPage(HttpSession session, @PathVariable int id) {
		
		ModelAndView view = new ModelAndView();
		view.setViewName("community/view");
		
		// id 게시글 얻어오기
		CommunityVO community = communityService.getOne(id);
		view.addObject("community", community);
		
		return view;
		
	}
	
	@RequestMapping("/read/{id}")
	public String viewReadPage(HttpSession session, @PathVariable int id) {
		
		if ( communityService.incrementViewCount(id) > 0 ) {
			return "redirect:/view/" + id;
		}
		
		return "redirect:/";
	}
	
	@RequestMapping("/recommend/{id}")
	public String viewRecommendPage(HttpSession session, @PathVariable int id) {
		
		if ( communityService.incrementRecommendCount(id) > 0 ) {
			return "redirect:/view/" + id;
		}
		
		return "redirect:/";
	}
	
	@RequestMapping("/get/{id}")
	public void download(@PathVariable int id,
						  HttpServletRequest request,
						  HttpServletResponse response) {
		CommunityVO community = communityService.getOne(id);
		String filename = community.getDisplayFilename();
		
		DownloadUtil download = new DownloadUtil("D:\\uploadFiles/" + filename);
		
		try {
			download.download(request, response, filename);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	@RequestMapping("/delete/{id}")
	public String doDeleteAction(@PathVariable int id, HttpSession session) {
		
		//int memberId = communityService.getOne(id).getMemberVO().getId();
		MemberVO member = (MemberVO) session.getAttribute(Member.USER);
		CommunityVO community = communityService.getOne(id);
		
		boolean isMyCommunity = member.getId() == community.getUserId();
		
		if ( isMyCommunity && communityService.removeOne(id) ) {
			return "redirect:/";
		}	
		
		return "/WEB-INF/view/error/404";
	}
	
	@RequestMapping(value = "/modify/{id}", method = RequestMethod.GET)
	public ModelAndView viewModifyPage(@PathVariable int id, HttpSession session) {
		
		MemberVO member = (MemberVO) session.getAttribute(Member.USER);
		CommunityVO community = communityService.getOne(id);
		
		int userId = member.getId();
		
		if ( userId != community.getUserId() ) {
			return new ModelAndView("WEB-INF/view/error/404");
		}
		
		ModelAndView view = new ModelAndView();
		view.setViewName("community/write");
		view.addObject("communityVO", community);
		view.addObject("mode", "modify");
		
		return view;
	}
	
	@RequestMapping(value = "/modify/{id}", method = RequestMethod.POST)
	public String doModifyAction(@PathVariable int id,
								 HttpSession session,
								 HttpServletRequest request,
								 @ModelAttribute("writeForm") @Valid CommunityVO communityVO,
								 Errors errors) {
		MemberVO member = (MemberVO) session.getAttribute(Member.USER);
		CommunityVO originVO = communityService.getOne(id);
		
		if ( member.getId() != originVO.getUserId() ) {
			return "error/404";
		}
		
		if ( errors.hasErrors() ) {
			return "redirect:/modify/" + id;
		}
		
		CommunityVO newCommunity = new CommunityVO();
		newCommunity.setId(originVO.getId());
		newCommunity.setUserId(member.getId());
		
		boolean isModify = false;
		
		// 1. IP 변경확인
		String ip = request.getRemoteAddr();
		if ( !ip.equals(originVO.getRequestIp()) ) {
			newCommunity.setRequestIp(ip);
			isModify = true;
		}
		
		// 2. 제목 변경확인
		if ( !originVO.getTitle().equals(communityVO.getTitle()) ) {
			newCommunity.setTitle(communityVO.getTitle());
			isModify = true;
		}
		
		// 3. 내용 변경확인
		if ( !originVO.getBody().equals(communityVO.getBody()) ) {
			newCommunity.setBody(communityVO.getBody());
			isModify = true;
		}
		
		// 4. 파일 변경확인
		if ( communityVO.getDisplayFilename().length() > 0 ) {
			File file = new File("D:/uploadFiles/" + communityVO.getDisplayFilename());
			file.delete();
			communityVO.setDisplayFilename("");
		}
		else {
			communityVO.setDisplayFilename(originVO.getDisplayFilename());
			
		}
		
		communityVO.save();
		if ( !originVO.getDisplayFilename().equals(communityVO.getDisplayFilename()) ) {
			newCommunity.setDisplayFilename(communityVO.getDisplayFilename());
			isModify = true;
		}
		
		// 5. 변경 없는지 확인
		if ( isModify ) {
			// 6. UPDATE 하는 Service Code 호출
			communityService.updateCommunity(newCommunity);
		}
		
		return "redirect:/view/" + id;
	}
}
