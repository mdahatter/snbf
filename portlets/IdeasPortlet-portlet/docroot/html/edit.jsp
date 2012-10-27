<%@ include file="/html/init.jsp" %>
<script type="text/javascript">
	function <portlet:namespace />savePreferences() {
		submitForm(document.<portlet:namespace />fm);
	}
</script>

<form action="<portlet:actionURL><portlet:param name="struts_action" value="/ideas/edit" /></portlet:actionURL>" method="post" name="<portlet:namespace />fm">
	<table>
		<tr>
			<td>Category:</td>
			<td><input type="text" name="Category"></td>
		</tr>
	</table>
	<input type="button" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />savePreferences();" />

</form>
