<%
String randomNamespace = PortalUtil.generateRandomKey(request, "taglib_ui_ratings_page") + StringPool.UNDERLINE;

RatingsStats ratingsStats = RatingsStatsLocalServiceUtil.getStats(MBMessage.class.getName(), message.getMessageId());
	
RatingsEntry ratingsEntry =  RatingsEntryLocalServiceUtil.fetchEntry(themeDisplay.getUserId(), MBMessage.class.getName(), message.getMessageId());

double yourScore = 0.0;

if (ratingsEntry != null) {
	yourScore = ratingsEntry.getScore();
}

int numberOfStars = 1;
String url = themeDisplay.getPathMain() + "/ratings/rate_entry";

%>
<div id="<%= randomNamespace %>linkVotar" class="btVotar">
	<span id="<%= randomNamespace %>numVotos"><%=ratingsStats.getTotalEntries()%><br/>votos</span>
	<c:choose>
		<c:when test="<%=themeDisplay.isSignedIn()%>">
			<portlet:renderURL var="replyURL">
				<portlet:param name="jspPage"
					value="/html/edit_message.jsp" />
				<portlet:param name="redirect" value="<%=currentURL%>" />
				<portlet:param name="mbCategoryId"
					value="<%=String.valueOf(message.getCategoryId())%>" />
				<portlet:param name="threadId"
					value="<%=String.valueOf(message.getThreadId())%>" />
				<portlet:param name="parentMessageId"
					value="<%=String.valueOf(message.getMessageId())%>" />
			</portlet:renderURL>
			<c:choose>
				<c:when test="<%=yourScore == 0%>">
					<a href="javascript:callVotation('<%=String.valueOf(MBMessage.class.getName())%>','<%=String.valueOf(message.getMessageId())%>', '<%=(ratingsStats.getTotalEntries() + 1)%>', '<%= randomNamespace %>');"></a>
				</c:when>
				<c:otherwise>
					&nbsp;
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<a href="<%=PortalUtil.getCreateAccountURL(request, themeDisplay)+"&redirect="+currentURL%>"></a>
		</c:otherwise>
	</c:choose>
</div>
<div class="btComentar">
	<span><%=String.valueOf(thread.getMessageCount())%><br/>comentarios</span>
	<c:choose>
		<c:when test="<%=themeDisplay.isSignedIn()%>">
			<portlet:renderURL var="replyURL">
				<portlet:param name="jspPage" value="/html/edit_message.jsp" />
				<portlet:param name="redirect" value="<%= currentURL %>" />
				<portlet:param name="mbCategoryId" value="<%= String.valueOf(message.getCategoryId()) %>" />
				<portlet:param name="threadId" value="<%= String.valueOf(message.getThreadId()) %>" />
				<portlet:param name="parentMessageId" value="<%= String.valueOf(message.getMessageId()) %>" />
			</portlet:renderURL>
			<a href="<%=replyURL%>"></a>
		</c:when>
		<c:otherwise>
			<a href="<%=PortalUtil.getCreateAccountURL(request, themeDisplay)+"&redirect="+currentURL%>"></a>
		</c:otherwise>
	</c:choose>
</div>
<div id="comments"></div>
<script>
function callVotation(className, classPK, votes, namespace) {

var A = AUI(); 
A.use('aui-io-request', function(aui) {
	A.io.request('<%=url%>', { 
		method : 'POST', 
		data: {
			className: className,
			classPK: classPK,
			score: '1',
			p_auth: Liferay.authToken,
			p_l_id: <%=themeDisplay.getPlid()%>
		},
		dataType : 'json', 
		on : { 
			success : function() { 
				
			} 
		} 
	});
});

//A.one('#'+namespace+'numVotos').setContent(votes+'<br/>votos');
A.one('#'+namespace+'linkVotar').setContent('<span>'+votes+'<br/>votos</span>&nbsp;');
}
</script>