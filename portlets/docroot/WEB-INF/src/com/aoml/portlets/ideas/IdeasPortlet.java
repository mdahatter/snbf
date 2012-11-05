package com.aoml.portlets.ideas;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.aoml.portlets.util.WebKeys;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.service.ResourcePermissionServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PortletKeys;
import com.liferay.portlet.PortalPreferences;
import com.liferay.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portlet.messageboards.NoSuchMessageException;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.model.MBMessageConstants;
import com.liferay.portlet.messageboards.model.MBMessageDisplay;
import com.liferay.portlet.messageboards.service.MBMessageServiceUtil;
import com.liferay.portlet.messageboards.service.MBThreadLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

public class IdeasPortlet extends MVCPortlet {
	
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
			//_log.error(path + " is not a valid include");
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

	public void editPreferences(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
		System.out.println("editPreferences");
		String addName = actionRequest.getParameter("Category");
		if (addName != null) {
			PortletPreferences prefs = actionRequest.getPreferences();
			prefs.setValue("Category", actionRequest.getParameter("Category"));
			prefs.store();
			actionResponse.setPortletMode(PortletMode.VIEW);
		}
	}
	
	public void processAction(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
		System.out.println("processAction");
		
		String command = actionRequest.getParameter(Constants.CMD);
		if(command != null) {
			if(command.equals(Constants.ADD) || command.equals(Constants.UPDATE)) {
				updateMessage(actionRequest, actionResponse);
			} else if(command.equals(Constants.VIEW)) {
				viewMessage(actionRequest, actionResponse);
			}
		}
		else {
			String category = actionRequest.getParameter("Category");
			String group = actionRequest.getParameter("Category");
			if (category != null && group != null) {
				PortletPreferences prefs = actionRequest.getPreferences();
				prefs.setValue("Category", category);
				prefs.setValue("Group", group);
				prefs.store();
				actionResponse.setPortletMode(PortletMode.VIEW);
			}
		}
	}
	
	public void updateMessage(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {

		System.out.println("updateMessage");
		PortletPreferences preferences = actionRequest.getPreferences();

		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest
				.getAttribute(WebKeys.THEME_DISPLAY);

		long messageId = ParamUtil.getLong(actionRequest, "messageId");
		System.out.println("creando mensaje de nuevo!");
		long groupId = themeDisplay.getScopeGroupId();
		long categoryId = ParamUtil.getLong(actionRequest, "mbCategoryId");
		long threadId = ParamUtil.getLong(actionRequest, "threadId");
		long parentMessageId = ParamUtil.getLong(actionRequest,
				"parentMessageId");
		String subject = ParamUtil.getString(actionRequest, "subject");
		String body = ParamUtil.getString(actionRequest, "body");

		String format = GetterUtil.getString(
				preferences.getValue("messageFormat", null),
				MBMessageConstants.DEFAULT_FORMAT);

		boolean attachments = ParamUtil
				.getBoolean(actionRequest, "attachments");

		List<ObjectValuePair<String, InputStream>> inputStreamOVPs = new ArrayList<ObjectValuePair<String, InputStream>>(
				5);

		try {
			if (attachments) {
				UploadPortletRequest uploadPortletRequest = PortalUtil
						.getUploadPortletRequest(actionRequest);

				for (int i = 1; i <= 5; i++) {
					String fileName = uploadPortletRequest
							.getFileName("msgFile" + i);
					InputStream inputStream = uploadPortletRequest
							.getFileAsStream("msgFile" + i);

					if (inputStream == null) {
						continue;
					}

					ObjectValuePair<String, InputStream> inputStreamOVP = new ObjectValuePair<String, InputStream>(
							fileName, inputStream);

					inputStreamOVPs.add(inputStreamOVP);
				}
			}

			boolean question = ParamUtil.getBoolean(actionRequest, "question");
			boolean anonymous = ParamUtil
					.getBoolean(actionRequest, "anonymous");
			double priority = ParamUtil.getDouble(actionRequest, "priority");
			boolean allowPingbacks = ParamUtil.getBoolean(actionRequest,
					"allowPingbacks");

			ServiceContext serviceContext = ServiceContextFactory.getInstance(actionRequest);
			
			//serviceContext.
			//		MBMessage.class.getName(), );

			boolean preview = ParamUtil.getBoolean(actionRequest, "preview");

			serviceContext.setAttribute("preview", preview);

			MBMessage message = null;

			if (messageId <= 0) {
				System.out.println("creando mensaje nuevo!");

				if (threadId <= 0) {

					// Post new thread

					message = MBMessageServiceUtil
							.addMessage(groupId, categoryId, subject, body,
									format, inputStreamOVPs, anonymous,
									priority, allowPingbacks, serviceContext);

					if (question) {
						MBThreadLocalServiceUtil.updateQuestion(
								message.getThreadId(), true);
					}
				} else {

					// Post reply

					message = MBMessageServiceUtil.addMessage(groupId,
							categoryId, threadId, parentMessageId, subject,
							body, format, inputStreamOVPs, anonymous, priority,
							allowPingbacks, serviceContext);
				}
				
				Role rol2 = RoleLocalServiceUtil.getRole(message.getCompanyId(), RoleConstants.USER);
				Role rol = RoleLocalServiceUtil.getRole(message.getCompanyId(), RoleConstants.GUEST);
				

				//Resource resource = ResourceLocalServiceUtil.getResource(message.getCompanyId(),
				//		MBMessage.class.getName(), ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(message.getMessageId()));
				
				String[] actionIds = new String[] { ActionKeys.VIEW };
				
				Map<Long, String[]> roleIdsToActionIds = new HashMap<Long, String[]>();
				roleIdsToActionIds.put(rol.getRoleId(), actionIds);
				roleIdsToActionIds.put(rol2.getRoleId(), actionIds);
				
				System.out.println("Permiso: " +"-"+ ResourceConstants.SCOPE_INDIVIDUAL+"-"+String.valueOf(message.getMessageId())+"-"+rol.getRoleId()+"-"+ActionKeys.VIEW);
				//PermissionLocalServiceUtil.setRolePermissions(rol.getRoleId(), actionIds, resource.getResourceId());
				
				System.out.println("Permiso: " +"-"+ ResourceConstants.SCOPE_INDIVIDUAL+"-"+String.valueOf(message.getMessageId())+"-"+rol2.getRoleId()+"-"+ActionKeys.VIEW);
				//PermissionLocalServiceUtil.setRolePermissions(rol2.getRoleId(), actionIds, resource.getResourceId());
				
				ResourcePermissionServiceUtil.setIndividualResourcePermissions(message.getGroupId(), message.getCompanyId(), MBMessage.class.getName(), String.valueOf(message.getMessageId()), roleIdsToActionIds);
				//ResourcePermissionServiceUtil.addResourcePermission(message.getGroupId(), message.getCompanyId(), MBMessage.class.getName(), ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(message.getMessageId()), rol2.getRoleId(), ActionKeys.VIEW);

				System.out.println("Permiso añadido!");
			} else {
				List<String> existingFiles = new ArrayList<String>();

				for (int i = 1; i <= 5; i++) {
					String path = ParamUtil.getString(actionRequest,
							"existingPath" + i);

					if (Validator.isNotNull(path)) {
						existingFiles.add(path);
					}
				}

				// Update message

				message = MBMessageServiceUtil.updateMessage(messageId,
						subject, body, inputStreamOVPs, existingFiles,
						priority, allowPingbacks, serviceContext);

				if (message.isRoot()) {
					MBThreadLocalServiceUtil.updateQuestion(
							message.getThreadId(), question);
				}
			}

			//return message;
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if (attachments) {
				for (ObjectValuePair<String, InputStream> inputStreamOVP : inputStreamOVPs) {

					InputStream inputStream = inputStreamOVP.getValue();

					StreamUtil.cleanUp(inputStream);
				}
			}
		}
	}

	public void viewMessage(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
		try {
			long messageId = ParamUtil.getLong(actionRequest, "messageId");

			PortalPreferences preferences = PortletPreferencesFactoryUtil
					.getPortalPreferences(actionRequest);

			String threadView = ParamUtil
					.getString(actionRequest, "threadView");

			if (Validator.isNotNull(threadView)) {
				preferences.setValue(PortletKeys.MESSAGE_BOARDS, "thread-view",
						threadView);
			} 


			boolean includePrevAndNext = true;

			MBMessageDisplay messageDisplay = MBMessageServiceUtil
					.getMessageDisplay(messageId, WorkflowConstants.STATUS_ANY,
							threadView, includePrevAndNext);

			actionRequest.setAttribute(WebKeys.MESSAGE_BOARDS_MESSAGE,
					messageDisplay);
			actionResponse.setRenderParameter("jspPage", "/html/view_message.jsp");

		} catch (Exception e) {
			if (e instanceof NoSuchMessageException
					|| e instanceof PrincipalException) {

				SessionErrors.add(actionRequest, e.getClass());

			} else {
				throw new PortletException(e);
			}
		}

	}
}
