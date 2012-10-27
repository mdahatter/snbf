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

<%@ include file="/html/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

MBMessage message = (MBMessage)request.getAttribute(WebKeys.MESSAGE_BOARDS_MESSAGE);

long messageId = BeanParamUtil.getLong(message, request, "messageId");

long categoryId = MBUtil.getCategoryId(request, message);
long threadId = BeanParamUtil.getLong(message, request, "threadId");
long parentMessageId = BeanParamUtil.getLong(message, request, "parentMessageId", MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID);

String subject = BeanParamUtil.getString(message, request, "subject");

MBThread thread = null;

MBMessage curParentMessage = null;
String parentAuthor = null;

if (threadId > 0) {
	try {
		curParentMessage = MBMessageLocalServiceUtil.getMessage(parentMessageId);

		if (Validator.isNull(subject)) {
			if (curParentMessage.getSubject().startsWith("RE: ")) {
				subject = curParentMessage.getSubject();
			}
			else {
				subject = "RE: " + curParentMessage.getSubject();
			}
		}

		parentAuthor = curParentMessage.isAnonymous() ? LanguageUtil.get(pageContext, "anonymous") : HtmlUtil.escape(PortalUtil.getUserName(curParentMessage.getUserId(), curParentMessage.getUserName()));
	}
	catch (Exception e) {
	}
}

String body = BeanParamUtil.getString(message, request, "body");
boolean attachments = BeanParamUtil.getBoolean(message, request, "attachments");
boolean preview = ParamUtil.getBoolean(request, "preview");
boolean quote = ParamUtil.getBoolean(request, "quote");
boolean splitThread = ParamUtil.getBoolean(request, "splitThread");

String[] existingAttachments = new String[0];


boolean allowPingbacks = PropsValues.MESSAGE_BOARDS_PINGBACK_ENABLED && BeanParamUtil.getBoolean(message, request, "allowPingbacks", true);

if (Validator.isNull(redirect)) {
	PortletURL viewMessageURL = renderResponse.createRenderURL();

	viewMessageURL.setParameter("struts_action", "/ideas/view_message");
	viewMessageURL.setParameter("messageId", String.valueOf(messageId));

	redirect = viewMessageURL.toString();
}
%>

<liferay-ui:header
	backURL="<%= redirect %>"
	localizeTitle="<%= (message == null) %>"
	title='<%= (message == null) ? "new-message" : message.getSubject() %>'
/>

<c:if test="<%= preview %>">
	<liferay-ui:message key="preview" />:

	<%
	MBMessage temp = null;

	if (message != null) {
		temp = message;

		message = new MBMessageImpl();

		message.setMessageId(temp.getMessageId());
		message.setCompanyId(temp.getCompanyId());
		message.setUserId(temp.getUserId());
		message.setUserName(temp.getUserName());
		message.setCreateDate(temp.getCreateDate());
		message.setModifiedDate(temp.getModifiedDate());
		message.setThreadId(temp.getThreadId());
		message.setSubject(subject);
		message.setBody(body);
		message.setFormat(messageFormat);
		message.setAttachments(temp.isAttachments());
		message.setAnonymous(temp.isAnonymous());
	}
	else {
		message = new MBMessageImpl();

		message.setMessageId(messageId);
		message.setCompanyId(user.getCompanyId());
		message.setUserId(user.getUserId());
		message.setUserName(user.getFullName());
		message.setCreateDate(new Date());
		message.setModifiedDate(new Date());
		message.setThreadId(threadId);
		message.setSubject(subject);
		message.setBody(body);
		message.setFormat(messageFormat);
		message.setAttachments(attachments);
		message.setAnonymous(BeanParamUtil.getBoolean(message, request, "anonymous"));
	}

	boolean editable = false;

	MBCategory category = null;

	int depth = 0;

	String className = "portlet-section-body results-row";
	String classHoverName = "portlet-section-body-hover results-row hover";

	request.setAttribute("edit_message.jsp-assetTagNames", ParamUtil.getString(request, "assetTagNames"));
	%>

	<%@ include file="/html/view_thread_message.jspf" %>

	<%
	request.removeAttribute("edit_message.jsp-assetTagNames");

	message = temp;
	%>

	<br />
</c:if>

<portlet:actionURL var="editMessageURL">
	<portlet:param name="struts_action" value="/ideas/edit_message" />
</portlet:actionURL>

<aui:form action="<%= editMessageURL %>" enctype='<%= attachments ? "multipart/form-data" : "" %>' method="post" name="fm" onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "saveMessage(false);" %>'>
	<aui:input name="<%= Constants.CMD %>" type="hidden" />
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="messageId" type="hidden" value="<%= messageId %>" />
	<aui:input name="mbCategoryId" type="hidden" value="<%= categoryId %>" />
	<aui:input name="threadId" type="hidden" value="<%= threadId %>" />
	<aui:input name="parentMessageId" type="hidden" value="<%= parentMessageId %>" />
	<aui:input name="attachments" type="hidden" value="<%= attachments %>" />
	<aui:input name="preview" type="hidden" />
	<aui:input name="workflowAction" type="hidden" value="<%= String.valueOf(WorkflowConstants.ACTION_SAVE_DRAFT) %>" />

	<liferay-ui:error exception="<%= LockedThreadException.class %>" message="thread-is-locked" />
	<liferay-ui:error exception="<%= MessageBodyException.class %>" message="please-enter-a-valid-message" />
	<liferay-ui:error exception="<%= MessageSubjectException.class %>" message="please-enter-a-valid-subject" />


	<liferay-ui:asset-categories-error />

	<liferay-ui:asset-tags-error />

	<aui:model-context bean="<%= message %>" model="<%= MBMessage.class %>" />

	<aui:fieldset>
		<c:if test="<%= message != null %>">
			<aui:workflow-status status="<%= message.getStatus() %>" />
		</c:if>

		<aui:input name="subject" value="<%= subject %>" />

		<aui:field-wrapper label="body">
			<c:choose>
				<c:when test='<%= ((messageId != 0) && message.isFormatBBCode()) || ((messageId == 0) && messageFormat.equals("bbcode")) %>'>
					<%@ include file="/html/bbcode_editor.jspf" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/html/html_editor.jspf" %>
				</c:otherwise>
			</c:choose>
			<aui:input name="body" type="hidden" />
		</aui:field-wrapper>

		<liferay-ui:custom-attributes-available className="<%= MBMessage.class.getName() %>">
			<liferay-ui:custom-attribute-list
				className="<%= MBMessage.class.getName() %>"
				classPK="<%= messageId %>"
				editable="<%= true %>"
				label="<%= true %>"
			/>
		</liferay-ui:custom-attributes-available>

	</aui:fieldset>


	<%
	boolean pending = false;

	if (message != null) {
		pending = message.isPending();
	}
	%>

	<aui:button-row>

		<%
		String saveButtonLabel = "save";

		if ((message == null) || message.isDraft() || message.isApproved()) {
			saveButtonLabel = "save-as-draft";
		}

		String publishButtonLabel = "publish";

		%>

		<aui:button disabled="<%= pending %>" name="publishButton" type="submit" value="<%= publishButtonLabel %>" />

		<aui:button href="<%= redirect %>" type="cancel" />
	</aui:button-row>
</aui:form>

<aui:script>
	function <portlet:namespace />getSuggestionsContent() {
		var content = '';

		content += document.<portlet:namespace />fm.<portlet:namespace />subject.value + ' ';
		content += <portlet:namespace />getHTML();

		return content;
	}

	function <portlet:namespace />manageAttachments(removeAttachments) {
		document.<portlet:namespace />fm.<portlet:namespace />body.value = <portlet:namespace />getHTML();
		document.<portlet:namespace />fm.<portlet:namespace />attachments.value = removeAttachments;

		submitForm(document.<portlet:namespace />fm);
	}

	function <portlet:namespace />previewMessage() {
		document.<portlet:namespace />fm.<portlet:namespace />body.value = <portlet:namespace />getHTML();
		document.<portlet:namespace />fm.<portlet:namespace />preview.value = 'true';

		<portlet:namespace />saveMessage(true);
	}

	function <portlet:namespace />saveMessage(draft) {
		document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = "<%= (message == null) ? Constants.ADD : Constants.UPDATE %>";
		document.<portlet:namespace />fm.<portlet:namespace />body.value = <portlet:namespace />getHTML();

		if (!draft) {
			document.<portlet:namespace />fm.<portlet:namespace />preview.value = <%= preview %>;
			document.<portlet:namespace />fm.<portlet:namespace />workflowAction.value = <%= WorkflowConstants.ACTION_PUBLISH %>;
		}

		submitForm(document.<portlet:namespace />fm);
	}

	function <portlet:namespace />selectCategory(categoryId, categoryName) {
		document.<portlet:namespace />fm.<portlet:namespace />mbCategoryId.value = categoryId;

		var nameEl = document.getElementById("<portlet:namespace />categoryName");

		nameEl.href = "<portlet:renderURL><portlet:param name="struts_action" value="/ideas/view" /></portlet:renderURL>&<portlet:namespace />mbCategoryId=" + categoryId;
		nameEl.innerHTML = categoryName + "&nbsp;";
	}

	<c:if test="<%= windowState.equals(WindowState.MAXIMIZED) && !themeDisplay.isFacebook() %>">
		Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace />subject);
	</c:if>
</aui:script>

<aui:script use="aui-base">

	<%
	for (int i = 1; i <= existingAttachments.length; i++) {
	%>

		var removeExisting = A.one("#<portlet:namespace />removeExisting" + <%= i %>);

		if (removeExisting) {
			removeExisting.on(
				'click',
				function(event) {
					var button = event.target;
					var span = A.one("#<portlet:namespace />existingFile" + <%= i %>);
					var file = A.one("#<portlet:namespace />msgFile" + <%= i %>);

					if (button) {
						button.remove();
					}

					if (span) {
						span.remove();
					}

					if (file) {
						file.ancestor('.aui-field').show();
					}
				}
			);
		}

	<%
	}
	%>

</aui:script>

<%
if (curParentMessage != null) {
	MBUtil.addPortletBreadcrumbEntries(curParentMessage, request, renderResponse);

	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(pageContext, "reply"), currentURL);
}
else if (message != null) {
	MBUtil.addPortletBreadcrumbEntries(message, request, renderResponse);

	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(pageContext, "edit"), currentURL);
}
else {
	MBUtil.addPortletBreadcrumbEntries(categoryId, request, renderResponse);

	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(pageContext, "add-message"), currentURL);
}
%>