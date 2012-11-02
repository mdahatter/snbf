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
<span>			
	<table>
		<tr>
			<td align="center" width="75px" class="bordeComentar">
				<div id="<%= randomNamespace %>numVotos"><strong><%=ratingsStats.getTotalEntries()%></strong></div>
				<strong>Votos</strong>
			</td>
			<td align="center" >
				<div><strong><%=String.valueOf(thread.getMessageCount())%></strong></div>
				<strong>Comentarios</strong>
			</td>
		</tr>
		<tr>
			<td align="center" width="75px">
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
						<c:choose>
							<c:when test="<%=yourScore == 0%>">
								<div id="<%= randomNamespace %>linkVotar">
								<a href="javascript:callVotation('<%=String.valueOf(MBMessage.class.getName())%>','<%=String.valueOf(message.getMessageId())%>', '<%=(ratingsStats.getTotalEntries() + 1)%>', '<%= randomNamespace %>');"><input type"button" value="Votar" class="btStandard"/></a>
								</div>
							</c:when>
							<c:otherwise>
								Gracias por tu voto!
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
							<a href="<%=PortalUtil.getCreateAccountURL(request, themeDisplay)%>"><input type"button" value="Votar" class="btStandard"/></a>
						</div>
					</c:otherwise>
				</c:choose>
			</td>
			<td align="center">					
				<c:choose>
					<c:when test="<%=themeDisplay.isSignedIn()%>">
						<portlet:renderURL var="replyURL">
							<portlet:param name="struts_action" value="/ideas/edit_message" />
							<portlet:param name="redirect" value="<%= currentURL %>" />
							<portlet:param name="mbCategoryId" value="<%= String.valueOf(message.getCategoryId()) %>" />
							<portlet:param name="threadId" value="<%= String.valueOf(message.getThreadId()) %>" />
							<portlet:param name="parentMessageId" value="<%= String.valueOf(message.getMessageId()) %>" />
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
			</td>
		</tr>
	</table>
</span>

<script>
function callVotation(className, classPK, votes, namespace) {

var A = AUI(); 
A.use('aui-io-request', function(aui) {
	A.io.request('<%=url%>', { 
		method : 'POST', 
		data: {
			className: className,
			classPK: classPK,
			score: '1'
		},
		dataType : 'json', 
		on : { 
			success : function() { 
				
			} 
		} 
	});
});

A.one('#'+namespace+'numVotos').setContent('<strong>'+votes+'<strong>');
A.one('#'+namespace+'linkVotar').setContent('Gracias por tu voto!');
}
</script>