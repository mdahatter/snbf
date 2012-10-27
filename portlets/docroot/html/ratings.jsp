<%@ include file="/html/init.jsp"%>

<%
	ResultRow row = (ResultRow) request
			.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

	Object[] objArray = (Object[]) row.getObject();

	MBMessage message = (MBMessage) objArray[0];

%>

  
<liferay-ui:ratings className="<%=MBMessage.class.getName()%>"
	classPK="<%=message.getMessageId()%>" type="thumbs" />