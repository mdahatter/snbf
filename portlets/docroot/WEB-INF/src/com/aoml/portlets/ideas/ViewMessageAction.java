package com.aoml.portlets.ideas;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortletKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.util.WebKeys;
import com.liferay.portlet.PortalPreferences;
import com.liferay.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portlet.messageboards.NoSuchMessageException;
import com.liferay.portlet.messageboards.model.MBMessageDisplay;
import com.liferay.portlet.messageboards.service.MBMessageServiceUtil;

public class ViewMessageAction extends PortletAction {
	@Override
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		try {
			long messageId = ParamUtil.getLong(renderRequest, "messageId");

			PortalPreferences preferences = PortletPreferencesFactoryUtil
					.getPortalPreferences(renderRequest);

			String threadView = ParamUtil
					.getString(renderRequest, "threadView");

			if (Validator.isNotNull(threadView)) {
				preferences.setValue(PortletKeys.MESSAGE_BOARDS, "thread-view",
						threadView);
			} else {
				threadView = preferences.getValue(PortletKeys.MESSAGE_BOARDS,
						"thread-view",
						PropsValues.MESSAGE_BOARDS_THREAD_VIEWS_DEFAULT);
			}

			if (!ArrayUtil.contains(PropsValues.MESSAGE_BOARDS_THREAD_VIEWS,
					threadView)) {

				threadView = PropsValues.MESSAGE_BOARDS_THREAD_VIEWS_DEFAULT;

				preferences.setValue(PortletKeys.MESSAGE_BOARDS, "thread-view",
						threadView);
			}

			boolean includePrevAndNext = PropsValues.MESSAGE_BOARDS_THREAD_PREVIOUS_AND_NEXT_NAVIGATION_ENABLED;

			MBMessageDisplay messageDisplay = MBMessageServiceUtil
					.getMessageDisplay(messageId, WorkflowConstants.STATUS_ANY,
							threadView, includePrevAndNext);

			renderRequest.setAttribute(WebKeys.MESSAGE_BOARDS_MESSAGE,
					messageDisplay);

			return mapping.findForward("portlet.ideas.view_message");
		} catch (Exception e) {
			if (e instanceof NoSuchMessageException
					|| e instanceof PrincipalException) {

				SessionErrors.add(renderRequest, e.getClass());

				return mapping.findForward("portlet.ideas.error");
			} else {
				throw e;
			}
		}
	}

}
