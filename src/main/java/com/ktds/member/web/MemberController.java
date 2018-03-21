package com.ktds.member.web;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ktds.community.service.CommunityService;
import com.ktds.member.constants.Member;
import com.ktds.member.service.MemberService;
import com.ktds.member.vo.MemberVO;

@Controller
public class MemberController {
	
	private MemberService memberService;
	private CommunityService communityService;

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	public void setCommunityService(CommunityService communityService) {
		this.communityService = communityService;
	}
	
	@RequestMapping("/api/exists/email")
	@ResponseBody
	public Map<String, Boolean> apiIsExistsEmail(@RequestParam String email) {
		
		boolean isExists = memberService.readCountMemberEmail(email);
		
		Map<String, Boolean> response = new HashMap<String, Boolean>();
		response.put("response", isExists);
		
		return response;
	}
	
	@RequestMapping("/api/exists/nickname")
	@ResponseBody
	public Map<String, Boolean> apiIsExistsNickname(@RequestParam String nickname) {
		
		boolean isExists = memberService.readCountMemberNickname(nickname);
		
		Map<String, Boolean> response = new HashMap<String, Boolean>();
		response.put("response", isExists);
		
		return response;
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String viewLoginPage(HttpSession session) {
		
		if ( session.getAttribute(Member.USER) != null ) {
			return "redirect:/";
		}
		
		return "member/login";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView doLoginAction(MemberVO memberVO, Errors errors, HttpServletRequest request) {
		
		HttpSession session = request.getSession();
		
		MemberVO loginMember = memberService.readMember(memberVO);
		
		
		// FIXME DB에 계정이 존재하지 않을 경우로 변경
		if ( loginMember != null ) {
			session.setAttribute(Member.USER, loginMember);
			return new ModelAndView("redirect:/");
		}
		
		session.setAttribute("status", "fail");
		
		return new ModelAndView("redirect:/login");
	}
	
	@RequestMapping(value = "/regist", method = RequestMethod.GET)
	public String viewRegistPage() {
		return "member/regist";
	}
	
	@RequestMapping(value = "/regist", method = RequestMethod.POST)
	public ModelAndView doRegistAction(@ModelAttribute("registForm")
								  @Valid MemberVO memberVO, Errors errors) {
		if ( errors.hasErrors() ) {
//			ModelAndView view = new ModelAndView();
//			view.setViewName("member/regist");
//			view.addObject("memberVO", memberVO);
//			return view;
			return new ModelAndView("member/regist");
			
		}
		
		if ( memberService.createMember(memberVO) ) {
			return new ModelAndView("redirect:/login");			
		}
		
		return new ModelAndView("member/regist");
	}
	
	@RequestMapping("/logout")
	public String doLogoutAction(HttpSession session) {
		
		// 세션 소멸
		session.invalidate();
		return "redirect:/login";
	}
	
	@RequestMapping("/delete/process1")
	public String viewVerifyPage() {
		return "member/delete/process1";
	}
	
	@RequestMapping("/delete/process2")
	public ModelAndView viewDeleteMyCommunitiesPage(
			@RequestParam(required = false, defaultValue = "") String password,
			HttpSession session) {
		
		if ( password.length() == 0 ) {
			return new ModelAndView("error/404");
		}
		
		MemberVO member = (MemberVO) session.getAttribute(Member.USER);
		member.setPassword(password);
		
		MemberVO verifyMember = memberService.readMember(member);
		if ( verifyMember == null ) {
			return new ModelAndView("redirect:/delete/process1");
		}
		
		// 내가 작성한 게시글의 개수 가져오기
		int myCommunitiesCount = communityService.readMyCommunitiesCount(verifyMember.getId());
		
		ModelAndView view = new ModelAndView();
		view.setViewName("member/delete/process2");
		view.addObject("myCommunitiesCount", myCommunitiesCount);
		String uuid = UUID.randomUUID().toString();
		session.setAttribute("__TOKEN__", uuid);
		view.addObject("token", uuid);
		
		return view;
	}
	
	@RequestMapping("/account/delete/{deleteFlag}")
	public String doDropAction(HttpSession session,
				@RequestParam(required = false, defaultValue = "") String token,
				@PathVariable String deleteFlag) {
		
		String sessionToken = (String) session.getAttribute("__TOKEN__");
		if ( sessionToken == null || !sessionToken.equals(token) ) {
			return "error/404";
		}
		
		MemberVO member = (MemberVO) session.getAttribute(Member.USER);
		if ( member == null ) {
			return "redirect:/login";
		}
		
		int id = member.getId();
		
		if ( memberService.dropMember(id, deleteFlag) ) {
			session.invalidate();
		}
		return "member/delete/delete";
		
	}
	
}
