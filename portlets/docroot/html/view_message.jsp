<%@ include file="/html/init.jsp" %>

<%
MBMessageDisplay messageDisplay = (MBMessageDisplay)request.getAttribute(WebKeys.MESSAGE_BOARDS_MESSAGE);

MBCategory category = messageDisplay.getCategory();

String displayStyle = BeanPropertiesUtil.getString(category, "displayStyle", MBCategoryConstants.DEFAULT_DISPLAY_STYLE);

if (Validator.isNull(displayStyle)) {
	displayStyle = MBCategoryConstants.DEFAULT_DISPLAY_STYLE;
}
%>
    <div class="contNinos"><img src="/snbf_theme-theme/images/titIdeas.png" width="403" height="77" />
      <div class="contScroll">
<%
themeDisplay.setIncludeServiceJs(true);

MBMessage message = messageDisplay.getMessage();

MBThread thread = messageDisplay.getThread();
%>


		<liferay-util:include page="/html/top_links.jsp" />


<%
String redirect = ParamUtil.getString(request, "redirect");

MBThread previousThread = messageDisplay.getPreviousThread();
MBThread nextThread = messageDisplay.getNextThread();

String threadView = messageDisplay.getThreadView();

MBThreadFlag threadFlag = MBThreadFlagLocalServiceUtil.getThreadFlag(themeDisplay.getUserId(), thread);
%>

<c:choose>
	<c:when test="<%= Validator.isNull(redirect) %>">
		<portlet:renderURL var="backURL">
			<portlet:param name="jspPage" value="/html/view.jsp" />
			<portlet:param name="mbCategoryId" value="<%= (category != null) ? String.valueOf(category.getCategoryId()) : String.valueOf(MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) %>" />
		</portlet:renderURL>

		<liferay-ui:header
			backLabel='<%= (category != null) ? category.getName() : "message-boards-home" %>'
			backURL="<%= backURL.toString() %>"
			localizeTitle="<%= false %>"
			title="<%= message.getSubject() %>"
		/>
	</c:when>
	<c:otherwise>
		<liferay-ui:header
			backURL="<%= redirect %>"
			localizeTitle="<%= false %>"
			title="<%= message.getSubject() %>"
		/>
	</c:otherwise>
</c:choose>



<div>
	<%
	MBTreeWalker treeWalker = messageDisplay.getTreeWalker();

	List<MBMessage> messages = null;

	if (treeWalker != null) {
		messages = new ArrayList<MBMessage>();

		messages.addAll(treeWalker.getMessages());

		messages = ListUtil.sort(messages, new MessageCreateDateComparator(true));
	}

	//AssetUtil.addLayoutTags(request, AssetTagLocalServiceUtil.getTags(MBMessage.class.getName(), thread.getRootMessageId()));
	%>

	<div class="message-scroll" id="<portlet:namespace />message_0"></div>

		<liferay-ui:toggle-area id="toggle_id_message_boards_view_message_thread">
			<table class="toggle_id_message_boards_view_message_thread">

			<%
			request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER, treeWalker);
			request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_SEL_MESSAGE, message);
			request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_CUR_MESSAGE, treeWalker.getRoot());
			request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_CATEGORY, category);
			request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_THREAD, thread);
			request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_THREAD_FLAG, threadFlag);
			request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_LAST_NODE, Boolean.valueOf(false));
			request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_DEPTH, new Integer(0));
			%>

			<liferay-util:include page="/ideas/view_thread_shortcut.jsp" />

			</table>
		</liferay-ui:toggle-area>

	<%
	boolean viewableThread = false;
	%>

	<c:choose>
		<c:when test="<%= threadView.equals(MBThreadConstants.THREAD_VIEW_TREE) %>">

			<%
			request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER, treeWalker);
			request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_SEL_MESSAGE, message);
			request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_CUR_MESSAGE, treeWalker.getRoot());
			request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_CATEGORY, category);
			request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_THREAD, thread);
			request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_LAST_NODE, Boolean.valueOf(false));
			request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_DEPTH, new Integer(0));
			request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_VIEWABLE_THREAD, Boolean.FALSE.toString());
			%>

			<liferay-util:include page="/ideas/view_thread_tree.jsp" />

			<%
			viewableThread = GetterUtil.getBoolean((String)request.getAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_VIEWABLE_THREAD));
			%>

		</c:when>
		<c:otherwise>
			<%@ include file="/html/view_thread_flat.jspf" %>
		</c:otherwise>
	</c:choose>

	<c:if test="<%= !viewableThread %>">
		<div class="portlet-msg-error">
			<liferay-ui:message key="you-do-not-have-permission-to-access-the-requested-resource" />
		</div>
	</c:if>
</div>
</div>
</div>


<aui:script>
	Liferay.provide(
		window,
		'<portlet:namespace />addAnswerFlag',
		function(messageId) {
			var A = AUI();

			Liferay.Service.MB.MBMessage.updateAnswer(
				{
					messageId: messageId,
					answer: true,
					cascade: false
				}
			);

			var addAnswerFlagDiv = A.one('#<portlet:namespace />addAnswerFlagDiv').clone();

			var html = addAnswerFlagDiv.html();

			html = '<div class="answer" id="<portlet:namespace />deleteAnswerFlag_' + messageId + '">' + html + '</div>';
			html = html.replace(/@MESSAGE_ID@/g, messageId);

			var tags = A.one('#<portlet:namespace />message_' + messageId).one('div.tags');

			if (tags) {
				tags.html(html);
			}

			A.one('#<portlet:namespace />addAnswerFlag_' + messageId).hide();
			A.one('#<portlet:namespace />deleteAnswerFlag_' + messageId).show();
		},
		['aui-base']
	);

	Liferay.provide(
		window,
		'<portlet:namespace />addQuickReply',
		function(cmd, messageId) {
			var A = AUI();

			var addQuickReplyDiv = A.one('#<portlet:namespace />addQuickReplyDiv');

			if (cmd == 'reply') {
				addQuickReplyDiv.show();

				addQuickReplyDiv.one('#<portlet:namespace />parentMessageId').val(messageId);

				var editorInput = addQuickReplyDiv.one('textarea');

				if (editorInput) {
					var editorId = editorInput.get('id');

					var editorInstance = window[editorId];

					if (editorInstance) {
						A.setTimeout(editorInstance.focus, 50, editorInstance);
					}
				}
			}
			else {
				addQuickReplyDiv.hide();
			}
		},
		['aui-base']
	);

	Liferay.provide(
		window,
		'<portlet:namespace />deleteAnswerFlag',
		function(messageId) {
			var A = AUI();

			Liferay.Service.MB.MBMessage.updateAnswer(
				{
					messageId: messageId,
					answer: false,
					cascade: false
				}
			);

			var deleteAnswerFlagDiv = A.one('#<portlet:namespace />deleteAnswerFlagDiv').clone();

			var html = deleteAnswerFlagDiv.html();

			html = '<li id="<portlet:namespace />addAnswerFlag_' + messageId + '">' + html + '</li>';
			html = html.replace(/@MESSAGE_ID@/g, messageId);

			var editControls = A.one('#<portlet:namespace />message_' + messageId).one('ul.edit-controls');

			if (editControls) {
				editControls.prepend(html);
			}

			A.one('#<portlet:namespace />deleteAnswerFlag_' + messageId).hide();

			A.one('#<portlet:namespace />addAnswerFlag_' + messageId).show();
		},
		['aui-base']
	);

	<c:if test="<%= thread.getRootMessageId() != message.getMessageId() %>">
		document.getElementById("<portlet:namespace />message_" + <%= message.getMessageId() %>).scrollIntoView(true);
	</c:if>
</aui:script>

<%
MBThreadFlagLocalServiceUtil.addThreadFlag(themeDisplay.getUserId(), thread);

message = messageDisplay.getMessage();

PortalUtil.setPageSubtitle(message.getSubject(), request);
PortalUtil.setPageDescription(message.getSubject(), request);

List<AssetTag> assetTags = AssetTagLocalServiceUtil.getTags(MBMessage.class.getName(), message.getMessageId());

PortalUtil.setPageKeywords(ListUtil.toString(assetTags, AssetTag.NAME_ACCESSOR), request);

MBUtil.addPortletBreadcrumbEntries(message, request, renderResponse);
%>
</div>