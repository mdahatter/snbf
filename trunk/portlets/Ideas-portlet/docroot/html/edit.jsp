<%@ include file="/html/init.jsp" %>
<script type="text/javascript">
	function <portlet:namespace />savePreferences() {
		submitForm(document.<portlet:namespace />fm);
	}
</script>
    <div class="contNinos"><img src="/snbf_theme-theme/images/titIdeas.png" width="403" height="77" />
      <div class="contScroll">

<portlet:actionURL var="editPreferencesURL" name="editPreferences">
    <portlet:param name="jspPage" value="/html/view.jsp" />
</portlet:actionURL>
<form action="<%= editPreferencesURL %>" method="post" name="<portlet:namespace />fm">
	<table>
		<tr>
			<td>Group:</td>
			<td><input type="text" name="Group"></td>
		</tr>
		<tr>
			<td>Category:</td>
			<td><input type="text" name="Category"></td>
		</tr>
	</table>
	<input type="button" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />savePreferences();" />

</form>
</div>
</div>