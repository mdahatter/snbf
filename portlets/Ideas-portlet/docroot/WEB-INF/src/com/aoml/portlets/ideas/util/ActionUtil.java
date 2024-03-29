package com.aoml.portlets.ideas.util;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import com.aoml.portlets.util.WebKeys;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.messageboards.model.MBCategory;
import com.liferay.portlet.messageboards.model.MBCategoryConstants;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.model.MBThread;
import com.liferay.portlet.messageboards.service.MBCategoryServiceUtil;
import com.liferay.portlet.messageboards.service.MBMessageServiceUtil;
import com.liferay.portlet.messageboards.service.MBThreadLocalServiceUtil;

public class ActionUtil {

	public static void getCategory(HttpServletRequest request)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		String topLink = ParamUtil.getString(request, "topLink");

		long categoryId = ParamUtil.getLong(request, "mbCategoryId");

		MBCategory category = null;

		if ((categoryId > 0) &&
			(categoryId != MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID)) {

			category = MBCategoryServiceUtil.getCategory(categoryId);
		}


		request.setAttribute(WebKeys.MESSAGE_BOARDS_CATEGORY, category);
	}

	public static void getCategory(PortletRequest portletRequest)
		throws Exception {

		HttpServletRequest request = PortalUtil.getHttpServletRequest(
			portletRequest);

		getCategory(request);
	}

	public static void getMessage(HttpServletRequest request) throws Exception {
		long messageId = ParamUtil.getLong(request, "messageId");

		MBMessage message = null;

		if (messageId > 0) {
			message = MBMessageServiceUtil.getMessage(messageId);
		}

		request.setAttribute(WebKeys.MESSAGE_BOARDS_MESSAGE, message);
	}

	public static void getMessage(PortletRequest portletRequest)
		throws Exception {

		HttpServletRequest request = PortalUtil.getHttpServletRequest(
			portletRequest);

		getMessage(request);
	}

	public static void getThreadMessage(HttpServletRequest request)
		throws Exception {

		long threadId = ParamUtil.getLong(request, "threadId");

		MBMessage message = null;

		if (threadId > 0) {
			MBThread thread = MBThreadLocalServiceUtil.getThread(threadId);

			message = MBMessageServiceUtil.getMessage(
				thread.getRootMessageId());
		}

		request.setAttribute(WebKeys.MESSAGE_BOARDS_MESSAGE, message);
	}

	public static void getThreadMessage(PortletRequest portletRequest)
		throws Exception {

		HttpServletRequest request = PortalUtil.getHttpServletRequest(
			portletRequest);

		getThreadMessage(request);
	}

}
