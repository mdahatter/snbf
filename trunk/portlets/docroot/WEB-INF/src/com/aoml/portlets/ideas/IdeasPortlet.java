package com.aoml.portlets.ideas;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IdeasPortlet extends GenericPortlet {
	private static Log _log = LogFactory.getLog(IdeasPortlet.class);

	protected String editJSP;
	protected String viewJSP;

	public void init() throws PortletException {
		editJSP = getInitParameter("edit-jsp");
		viewJSP = getInitParameter("view-jsp");
	}

	public void doView(RenderRequest renderRequest,
			RenderResponse renderResponse) throws IOException, PortletException {
		PortletPreferences prefs = renderRequest.getPreferences();
		String categoryId = (String) prefs.getValue("categoryId", "no");
		if (categoryId.equalsIgnoreCase("no")) {
			categoryId = "";
		}
		renderRequest.setAttribute("categoryId", categoryId);
		include(viewJSP, renderRequest, renderResponse);
	}

	protected void include(String path, RenderRequest renderRequest,
			RenderResponse renderResponse) throws IOException, PortletException {
		PortletRequestDispatcher portletRequestDispatcher = getPortletContext()
				.getRequestDispatcher(path);
		if (portletRequestDispatcher == null) {
			_log.error(path + " is not a valid include");
		} else {
			portletRequestDispatcher.include(renderRequest, renderResponse);
		}
	}

	public void doEdit(RenderRequest renderRequest,
			RenderResponse renderResponse) throws IOException, PortletException {
		renderResponse.setContentType("text/html");
		PortletURL addNameURL = renderResponse.createActionURL();
		addNameURL.setParameter("addCategoryId", "addCategoryId");
		renderRequest.setAttribute("addCategoryIdURL", addNameURL.toString());
		include(editJSP, renderRequest, renderResponse);
	}

	public void processAction(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
		String addName = actionRequest.getParameter("addCategoryId");
		if (addName != null) {
			PortletPreferences prefs = actionRequest.getPreferences();
			prefs.setValue("categoryId", actionRequest.getParameter("categoryId"));
			prefs.store();
			actionResponse.setPortletMode(PortletMode.VIEW);
		}
	}
}
