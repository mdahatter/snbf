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
	MBCategory category = (MBCategory) request
			.getAttribute(WebKeys.MESSAGE_BOARDS_CATEGORY);

	long categoryId = MBUtil.getCategoryId(request, category);
	System.out.println("categoryId" + categoryId);
	MBCategoryDisplay categoryDisplay = (MBCategoryDisplay) request
			.getAttribute("view.jsp-categoryDisplay");

	PortletURL portletURL = (PortletURL) request
			.getAttribute("view.jsp-portletURL");
%>

<liferay-ui:panel-container cssClass="message-boards-panels"
	extended="<%=false%>" id="messageBoardsPanelContainer"
	persistState="<%=true%>">

	<%
		int categoriesCount = MBCategoryServiceUtil.getCategoriesCount(
					scopeGroupId, categoryId);
	System.out.println("categoriesCount" + categoriesCount);
	%>
 					<portlet:renderURL var="editMessageURL">
						<portlet:param name="struts_action" value="/message_boards/edit_message" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
						<portlet:param name="mbCategoryId" value="<%= String.valueOf(categoryId) %>" />
					</portlet:renderURL>

					<aui:button href="<%= editMessageURL %>" value="post-new-thread" />
					
	<liferay-ui:panel collapsible="<%=threadsPanelCollapsible%>"
		cssClass="threads-panel" extended="<%=threadsPanelExtended%>"
		id="messageBoardsThreadsPanel" persistState="<%=true%>"
		title='<%=LanguageUtil.get(pageContext,
										"threads")%>'>
		<liferay-ui:search-container curParam="cur2"
			emptyResultsMessage="there-are-no-threads-in-this-category"
			headerNames="thread,flag,started-by,posts,views,last-post"
			iteratorURL="<%=portletURL%>">
			<liferay-ui:search-container-results
				results="<%=MBThreadServiceUtil.getThreads(
											scopeGroupId, categoryId,
											WorkflowConstants.STATUS_APPROVED,
											searchContainer.getStart(),
											searchContainer.getEnd())%>"
				total="<%=MBThreadServiceUtil
											.getThreadsCount(
													scopeGroupId,
													categoryId,
													WorkflowConstants.STATUS_APPROVED)%>" />

			<liferay-ui:search-container-row
				className="com.liferay.portlet.messageboards.model.MBThread"
				keyProperty="threadId" modelVar="thread">

				<%
					MBMessage message = null;

												try {
													message = MBMessageLocalServiceUtil
															.getMessage(thread
																	.getRootMessageId());
												} catch (NoSuchMessageException nsme) {

													message = new MBMessageImpl();

													row.setSkip(true);
												}

												message = message.toEscapedModel();

//												row.setBold(!MBThreadFlagLocalServiceUtil
//														.hasThreadFlag(themeDisplay
//																.getUserId(), thread));
//												row.setObject(new Object[] { message,
//														threadSubscriptionClassPKs });
//												row.setRestricted(!MBMessagePermission
//														.contains(permissionChecker,
//																message,
//																ActionKeys.VIEW));
				%>

				<liferay-portlet:renderURL varImpl="rowURL">
					<portlet:param name="struts_action"
						value="/ideas/view_message" />
					<portlet:param name="messageId"
						value="<%=String.valueOf(message
													.getMessageId())%>" />
				</liferay-portlet:renderURL>

				<!-- Inicio hook AM -->

				<liferay-ui:search-container-column-text buffer="buffer"
					name="thread">

					<%
						buffer.append("<b>"+message.getSubject()
																+ "</b>&nbsp;&nbsp;&nbsp;(" + HtmlUtil.escape(PortalUtil.getUserName(
																		message.getUserId(),
																		message.getUserName())) + ")<br/>");
					%>
					<%
						buffer.append( dateFormatDateTime.format(message.getModifiedDate())
																+ "<br/>");
					%>
					<%
						String msgBody = StringPool.BLANK;

						if (message.isFormatBBCode()) {
							msgBody = BBCodeTranslatorUtil
									.getHTML(message
											.getBody());
							msgBody = StringUtil
									.replace(
											msgBody,
											"@theme_images_path@/emoticons",
											themeDisplay
													.getPathThemeImages()
													+ "/emoticons");
						} else {
							msgBody = message.getBody();
						}
						buffer.append(msgBody);
						
						buffer.append("&nbsp;<a href=\"");
						buffer.append(rowURL);
						buffer.append("\">");
						buffer.append(LanguageUtil.get(pageContext, "more"));
						buffer.append(" &raquo;");
						buffer.append("</a>");
					%>
					
					
				</liferay-ui:search-container-column-text>


				<liferay-ui:search-container-column-text name="comments"
					value="<%=String.valueOf(thread
													.getMessageCount())%>">

				</liferay-ui:search-container-column-text>
				<liferay-ui:search-container-column-jsp cssClass="question"
					 path="/html/ratings.jsp" />

				<!-- Fin hook AM -->
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator />
		</liferay-ui:search-container>
	</liferay-ui:panel>
</liferay-ui:panel-container>
