<%--
/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/html/init.jsp"%>

<%
	String topLink = ParamUtil.getString(request, "topLink", "ideas-home");

	String redirect = ParamUtil.getString(request, "redirect");

	MBCategory category = (MBCategory) request.getAttribute(WebKeys.MESSAGE_BOARDS_CATEGORY);
	String categoryId = preferences.getValue("Category", StringPool.BLANK);

	long catId = Long.parseLong(categoryId);

	String displayStyle = BeanPropertiesUtil.getString(category, "displayStyle", MBCategoryConstants.DEFAULT_DISPLAY_STYLE);

	MBCategoryDisplay categoryDisplay = new MBCategoryDisplayImpl(10856, catId);

	System.out.println(categoryDisplay.getAllCategories());
	System.out.println(topLink);

	Set<Long> categorySubscriptionClassPKs = null;
	Set<Long> threadSubscriptionClassPKs = null;

	PortletURL portletURL = renderResponse.createRenderURL();

	portletURL.setParameter("struts_action", "/ideas/view");
	portletURL.setParameter("topLink", topLink);
	portletURL.setParameter("mbCategoryId", categoryId);

	request.setAttribute("view.jsp-categoryDisplay", categoryDisplay);

	request.setAttribute("view.jsp-viewCategory", Boolean.FALSE.toString());

	request.setAttribute("view.jsp-portletURL", portletURL);
%>

<liferay-util:include page="/top_links.jsp" />

<div class="contNinos">
	<img src="/IdeasPortlet-portlet/images/titIdeasNinos.png" width="453"
		height="77" />
	<div class="btMasVotadas">
		<%
		portletURL.setParameter("topLink", "ideas-home");
		%>
		<a href="<%= (topLink.equals("ideas-home") && catId == 0) ? StringPool.BLANK : portletURL.toString() %>"></a>
	</div>
	<div class="btRecientes">
		<%
		portletURL.setParameter("topLink", "recent-posts");
		%>
		<a href="<%= topLink.equals("recent-posts") ? StringPool.BLANK : portletURL.toString() %>"></a>
	</div>
	<div class="contScroll">
		<c:choose>
			<c:when test='<%=topLink.equals("ideas-home")%>'>
				<liferay-ui:panel-container extended="<%=false%>"
					id="messageBoardsPanelContainer" persistState="<%=true%>">

					<%
						int categoriesCount = MBCategoryServiceUtil.getCategoriesCount(scopeGroupId, catId);
									System.out.println("categoriesCount" + categoriesCount);
					%>


					<liferay-ui:panel collapsible="no"
						extended="<%=threadsPanelExtended%>"
						id="messageBoardsThreadsPanel" persistState="<%=true%>" title=''>



						<liferay-ui:search-container curParam="cur2"
							emptyResultsMessage="there-are-no-threads-in-this-category"
							headerNames="thread,views,last-post"
							iteratorURL="<%=portletURL%>">
							<%
								java.util.List<com.liferay.portlet.messageboards.model.MBThread> allThreads = MBThreadServiceUtil.getThreads(
															scopeGroupId, catId, WorkflowConstants.STATUS_APPROVED, searchContainer.getStart(), searchContainer.getEnd());
													allThreads = ListUtil.sort(allThreads, new MessageVotesComparator());
							%>
							<liferay-ui:search-container-results results="<%=allThreads%>"
								total="<%=MBThreadServiceUtil.getThreadsCount(scopeGroupId, catId, WorkflowConstants.STATUS_APPROVED)%>" />

							<liferay-ui:search-container-row
								className="com.liferay.portlet.messageboards.model.MBThread"
								keyProperty="threadId" modelVar="thread">

								<%
									MBMessage message = null;

									try {
										message = MBMessageLocalServiceUtil.getMessage(thread.getRootMessageId());
									} catch (NoSuchMessageException nsme) {

										message = new MBMessageImpl();

										row.setSkip(true);
									}

									message = message.toEscapedModel();
									row.setObject(new Object[] { message });
								%>

								<liferay-portlet:renderURL varImpl="rowURL">
									<portlet:param name="struts_action" value="/ideas/view_message" />
									<portlet:param name="messageId"
										value="<%=String.valueOf(message.getMessageId())%>" />
								</liferay-portlet:renderURL>
								<div class="idea">
									<!-- Inicio hook AM -->
									<h1><%=message.getSubject()%></h1>
									<h2><%=HtmlUtil.escape(PortalUtil.getUserName(message.getUserId(), message.getUserName()))%>
										|
										<%=dateFormatDateTime.format(message.getCreateDate())%></h2>
									<p>
										<%
											String msgBody = StringPool.BLANK;

											if (message.isFormatBBCode()) {
												msgBody = BBCodeTranslatorUtil.getHTML(message.getBody());
												msgBody = StringUtil.replace(msgBody, "@theme_images_path@/emoticons", themeDisplay.getPathThemeImages()
														+ "/emoticons");
											} else {
												msgBody = message.getBody();
											}
										%>
										<%=msgBody%>&nbsp;<a href="<%=rowURL%>"><%=LanguageUtil.get(pageContext, "more")%>&raquo;</a>
									</p>
					<%@ include file="/html/ratings.jsp"%>

								</div>
							</liferay-ui:search-container-row>

							<liferay-ui:search-iterator />
						</liferay-ui:search-container>
					</liferay-ui:panel>
				</liferay-ui:panel-container>

		<%
			if (category != null) {
				PortalUtil.setPageSubtitle(category.getName(), request);
				PortalUtil.setPageDescription(category.getDescription(), request);

				MBUtil.addPortletBreadcrumbEntries(category, request, renderResponse);
			}
		%>

			</c:when>
	<c:when test='<%=topLink.equals("recent-posts")%>'>

		<%
			long groupThreadsUserId = ParamUtil.getLong(request, "groupThreadsUserId");

			if (groupThreadsUserId > 0) {
				portletURL.setParameter("groupThreadsUserId", String.valueOf(groupThreadsUserId));
			}
		%>

		<c:if
			test='<%=topLink.equals("recent-posts") && (groupThreadsUserId > 0)%>'>
			<div class="portlet-msg-info">
				<liferay-ui:message key="filter-by-user" />
				:
				<%=HtmlUtil.escape(PortalUtil.getUserName(groupThreadsUserId, StringPool.BLANK))%>
			</div>
		</c:if>

		<liferay-ui:search-container
			headerNames="thread,started-by,posts,views,last-post"
			iteratorURL="<%=portletURL%>">

			<%
				String emptyResultsMessage = null;

				if (topLink.equals("recent-posts")) {
					emptyResultsMessage = "there-are-no-recent-posts";
				}

				searchContainer.setEmptyResultsMessage(emptyResultsMessage);
			%>

			<liferay-ui:search-container-results>

				<%
					if (topLink.equals("recent-posts")) {
						Calendar calendar = Calendar.getInstance();

						int offset = GetterUtil.getInteger(recentPostsDateOffset);

						calendar.add(Calendar.DATE, -offset);

						results = MBThreadServiceUtil.getGroupThreads(scopeGroupId, groupThreadsUserId, calendar.getTime(),
								WorkflowConstants.STATUS_APPROVED, searchContainer.getStart(), searchContainer.getEnd());
						total = MBThreadServiceUtil.getGroupThreadsCount(scopeGroupId, groupThreadsUserId, calendar.getTime(),
								WorkflowConstants.STATUS_APPROVED);
					}

					pageContext.setAttribute("results", results);
					pageContext.setAttribute("total", total);
				%>

			</liferay-ui:search-container-results>

			<liferay-ui:search-container-row
				className="com.liferay.portlet.messageboards.model.MBThread"
				keyProperty="threadId" modelVar="thread">

				<%
					MBMessage message = null;

					try {
						message = MBMessageLocalServiceUtil.getMessage(thread.getRootMessageId());
					} catch (NoSuchMessageException nsme) {
						//_log.error("Thread requires missing root message id " + thread.getRootMessageId());

						message = new MBMessageImpl();

						row.setSkip(true);
					}

					message = message.toEscapedModel();

					row.setObject(new Object[] { message, threadSubscriptionClassPKs });
				%>

				<liferay-portlet:renderURL varImpl="rowURL">
					<portlet:param name="struts_action" value="/ideas/view_message" />
					<portlet:param name="messageId"
						value="<%=String.valueOf(message.getMessageId())%>" />
				</liferay-portlet:renderURL>
				<div class="idea">
					<!-- Inicio hook AM -->
					<h1><%=message.getSubject()%></h1>
					<h2><%=HtmlUtil.escape(PortalUtil.getUserName(message.getUserId(), message.getUserName()))%>
						|
						<%=dateFormatDateTime.format(message.getCreateDate())%></h2>
					<p>
						<%
							String msgBody = StringPool.BLANK;

													if (message.isFormatBBCode()) {
														msgBody = BBCodeTranslatorUtil.getHTML(message.getBody());
														msgBody = StringUtil.replace(msgBody, "@theme_images_path@/emoticons", themeDisplay.getPathThemeImages()
																+ "/emoticons");
													} else {
														msgBody = message.getBody();
													}
						%>
						<%=msgBody%>&nbsp;<a href="<%=rowURL%>"><%=LanguageUtil.get(pageContext, "more")%>&raquo;</a>
					</p>
					<%@ include file="/html/ratings.jsp"%>
				</div>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator />
		</liferay-ui:search-container>

		<%
			PortalUtil.setPageSubtitle(LanguageUtil.get(pageContext, StringUtil.replace(topLink, StringPool.UNDERLINE, StringPool.DASH)),
							request);
					PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(pageContext, TextFormatter.format(topLink, TextFormatter.O)),
							portletURL.toString());
		%>
	</c:when>
</c:choose>
	</div>
	<c:choose>
		<c:when test="<%=themeDisplay.isSignedIn()%>">
			<portlet:renderURL var="editMessageURL">
				<portlet:param name="struts_action" value="/ideas/edit_message" />
				<portlet:param name="redirect" value="<%=currentURL%>" />
				<portlet:param name="mbCategoryId" value="<%=categoryId%>" />
			</portlet:renderURL>
			<div class="btRegistroNinos">
				<a href="<%=editMessageURL%>"></a>
			</div>
		</c:when>
		<c:otherwise>
			<div class="btRegistroNinos">
				<a
					href="<%=PortalUtil.getCreateAccountURL(request, themeDisplay)%>"></a>
			</div>
		</c:otherwise>
	</c:choose>
</div>
