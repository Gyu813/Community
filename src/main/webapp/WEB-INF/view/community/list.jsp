<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

	<div id="wrapper">
		<jsp:include page="/WEB-INF/view/template/menu.jsp"/>
		<table>
			<tr>
				<th>ID</th>
				<th>제목</th>
				<th>작성자</th>
				<th>작성일</th>
				<th>조회수</th>
			</tr>
			<c:forEach items="${communityList}" var="community">
				<tr>
					<td>${community.id}</td>
					<td>
						<a href="<c:url value="/read/${community.id}"/>">
							${community.title}
						</a>
						<c:if test="${not empty community.displayFilename}">
							<img	src="<c:url value="/static/img/file.png"/>"
									alt="${comuunity.displayFilename}"
									style="width: 10px;"/>
						</c:if>
					</td>
					<td>
						<c:choose>
							<c:when test="${not empty community.memberVO}">
								<!-- NICKNAME(EMAIL) -->
								${community.memberVO.nickname}(${community.memberVO.email})
							</c:when>
							<c:otherwise>
								탈퇴한 회원
							</c:otherwise>
						</c:choose>
					</td>
					<td>${community.writeDate}</td>
					<td>${community.viewCount}</td>
				</tr>
			</c:forEach>
			<c:if test="${empty communityList}">
				<tr>
					<td colspan="5">등록된 게시글이 없습니다.</td>
				</tr>
			</c:if>
		</table>
		
		<div>
			<a href="/write">글쓰기</a>
		</div>
	</div>

</body>
</html>