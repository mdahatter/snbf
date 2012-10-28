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

	MBCategoryDisplay categoryDisplay = new MBCategoryDisplayImpl(19, catId);

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

<c:choose>
	<c:when test='<%=topLink.equals("ideas-home")%>'>


		<%
			System.out.println("categoryId" + categoryId);
		%>
		<div class="contNinos">
			<img src="/IdeasPortlet-portlet/images/titIdeasNinos.png" width="453"
				height="77" />
			<div class="btMasVotadas">
				<a href=""></a>
			</div>
			<div class="btRecientes">
				<a href=""></a>
			</div>
			<div class="contScroll">
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

															//												row.setBold(!MBThreadFlagLocalServiceUtil
															//														.hasThreadFlag(themeDisplay
															//																.getUserId(), thread));
															row.setObject(new Object[] { message });
															//												row.setRestricted(!MBMessagePermission
															//														.contains(permissionChecker,
															//																message,
															//																ActionKeys.VIEW));
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
										<%=dateFormatDateTime.format(message.getModifiedDate())%></h2>
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
									<c:choose>
										<c:when test="<%=themeDisplay.isSignedIn()%>">
											<portlet:renderURL var="replyURL">
												<portlet:param name="struts_action"
													value="/ideas/edit_message" />
												<portlet:param name="redirect" value="<%=currentURL%>" />
												<portlet:param name="mbCategoryId"
													value="<%=String.valueOf(message.getCategoryId())%>" />
												<portlet:param name="threadId"
													value="<%=String.valueOf(message.getThreadId())%>" />
												<portlet:param name="parentMessageId"
													value="<%=String.valueOf(message.getMessageId())%>" />
											</portlet:renderURL>
											<div class="btComentar">
												<a href="<%=replyURL%>"></a>
											</div>
										</c:when>
										<c:otherwise>
											<div class="btComentar">
												<a
													href="<%=PortalUtil.getCreateAccountURL(request, themeDisplay)%>"></a>
											</div>
										</c:otherwise>
									</c:choose>
									Comentarios:
									<%=String.valueOf(thread.getMessageCount())%>
									<br />
									<liferay-ui:ratings className="<%=MBMessage.class.getName()%>"
										classPK="<%=message.getMessageId()%>" type="thumbs" />
								</div>
							</liferay-ui:search-container-row>

							<liferay-ui:search-iterator />
						</liferay-ui:search-container>
					</liferay-ui:panel>



				</liferay-ui:panel-container>
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
		<%
			System.out.println(category);

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

									row.setBold(!MBThreadFlagLocalServiceUtil.hasThreadFlag(themeDisplay.getUserId(), thread));
									row.setObject(new Object[] { message, threadSubscriptionClassPKs });
									row.setRestricted(!MBMessagePermission.contains(permissionChecker, message, ActionKeys.VIEW));
				%>

				<liferay-portlet:renderURL varImpl="rowURL">
					<portlet:param name="struts_action" value="/ideas/view_message" />
					<portlet:param name="messageId"
						value="<%=String.valueOf(message.getMessageId())%>" />
				</liferay-portlet:renderURL>

				<liferay-ui:search-container-column-text buffer="buffer"
					href="<%=rowURL%>" name="thread">

					<%
						buffer.append(message.getSubject());
					%>

				</liferay-ui:search-container-column-text>

				<!-- 
<%--@ include file="/html/portlet/message_boards/user_thread_columns_last_post.jspf" %>

<%@ include file="/html/portlet/message_boards/user_thread_columns_action.jspf" --%>			
 -->
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
