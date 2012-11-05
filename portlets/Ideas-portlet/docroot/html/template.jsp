<%@ include file="/html/init.jsp" %>

<portlet:defineObjects />

<tiles:useAttribute classname="java.lang.String" id="tilesPortletContent" ignore="true" name="portlet_content" />
<tiles:useAttribute classname="java.lang.String" id="tilesPortletDecorate" ignore="true" name="portlet_decorate" />
<tiles:useAttribute classname="java.lang.String" id="tilesPortletPadding" ignore="true" name="portlet_padding" />

<tiles:insert attribute="portlet_content"/>
