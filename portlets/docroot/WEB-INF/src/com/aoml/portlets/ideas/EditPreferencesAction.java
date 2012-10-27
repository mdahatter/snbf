package com.aoml.portlets.ideas;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class EditPreferencesAction extends PortletAction {

	@Override
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
			throws Exception {

		//String tabs2 = ParamUtil.getString(actionRequest, "tabs2");
		String category = ParamUtil.getString(actionRequest, "Category");
		System.out.println("Category=" + category);
		PortletPreferences prefs = actionRequest.getPreferences();
		prefs.setValue("Category", category);
		prefs.store();

		super.processAction(mapping, form, portletConfig, actionRequest, actionResponse);
	}
	
	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig config,
			RenderRequest req, RenderResponse res)
		throws Exception {

		return mapping.findForward("portlet.ideas.edit");
	}
}