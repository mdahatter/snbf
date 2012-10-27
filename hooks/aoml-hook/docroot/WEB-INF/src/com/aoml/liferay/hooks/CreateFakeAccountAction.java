package com.aoml.liferay.hooks;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.liferay.portal.AddressCityException;
import com.liferay.portal.AddressStreetException;
import com.liferay.portal.AddressZipException;
import com.liferay.portal.CompanyMaxUsersException;
import com.liferay.portal.ContactFirstNameException;
import com.liferay.portal.ContactFullNameException;
import com.liferay.portal.ContactLastNameException;
import com.liferay.portal.DuplicateUserEmailAddressException;
import com.liferay.portal.DuplicateUserScreenNameException;
import com.liferay.portal.EmailAddressException;
import com.liferay.portal.GroupFriendlyURLException;
import com.liferay.portal.NoSuchCountryException;
import com.liferay.portal.NoSuchLayoutException;
import com.liferay.portal.NoSuchListTypeException;
import com.liferay.portal.NoSuchOrganizationException;
import com.liferay.portal.NoSuchRegionException;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.OrganizationParentException;
import com.liferay.portal.PhoneNumberException;
import com.liferay.portal.RequiredFieldException;
import com.liferay.portal.RequiredUserException;
import com.liferay.portal.ReservedUserEmailAddressException;
import com.liferay.portal.ReservedUserScreenNameException;
import com.liferay.portal.TermsOfUseException;
import com.liferay.portal.UserEmailAddressException;
import com.liferay.portal.UserIdException;
import com.liferay.portal.UserPasswordException;
import com.liferay.portal.UserScreenNameException;
import com.liferay.portal.UserSmsException;
import com.liferay.portal.WebsiteURLException;
import com.liferay.portal.kernel.captcha.CaptchaMaxChallengesException;
import com.liferay.portal.kernel.captcha.CaptchaTextException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.servlet.BrowserSnifferUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.struts.BaseStrutsPortletAction;
import com.liferay.portal.kernel.struts.StrutsPortletAction;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutTypePortlet;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.service.PortletLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.UserServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PropsUtil;

public class CreateFakeAccountAction extends BaseStrutsPortletAction {

	@Override
	public void processAction(StrutsPortletAction originalStrutsPortletAction,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest
				.getAttribute(WebKeys.THEME_DISPLAY);

		Company company = themeDisplay.getCompany();

		if (!company.isStrangers()) {
			throw new PrincipalException();
		}

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD)) {

				addUser(actionRequest, actionResponse);
			}
		} catch (Exception e) {
			if (e instanceof DuplicateUserEmailAddressException
					|| e instanceof DuplicateUserScreenNameException) {

				String emailAddress = ParamUtil.getString(actionRequest,
						"emailAddress");

				try {
					User user = UserLocalServiceUtil.getUserByEmailAddress(
							themeDisplay.getCompanyId(), emailAddress);

					if (user.getStatus() != WorkflowConstants.STATUS_INCOMPLETE) {

						SessionErrors.add(actionRequest, e.getClass(), e);
					} else {
						setForward(actionRequest,
								"portlet.login.update_account");
					}
				} catch (NoSuchUserException nsue) {
					SessionErrors.add(actionRequest, e.getClass(), e);
				}
			} else if (e instanceof AddressCityException
					|| e instanceof AddressStreetException
					|| e instanceof AddressZipException
					|| e instanceof CaptchaMaxChallengesException
					|| e instanceof CaptchaTextException
					|| e instanceof CompanyMaxUsersException
					|| e instanceof ContactFirstNameException
					|| e instanceof ContactFullNameException
					|| e instanceof ContactLastNameException
					|| e instanceof EmailAddressException
					|| e instanceof GroupFriendlyURLException
					|| e instanceof NoSuchCountryException
					|| e instanceof NoSuchListTypeException
					|| e instanceof NoSuchOrganizationException
					|| e instanceof NoSuchRegionException
					|| e instanceof OrganizationParentException
					|| e instanceof PhoneNumberException
					|| e instanceof RequiredFieldException
					|| e instanceof RequiredUserException
					|| e instanceof ReservedUserEmailAddressException
					|| e instanceof ReservedUserScreenNameException
					|| e instanceof TermsOfUseException
					|| e instanceof UserEmailAddressException
					|| e instanceof UserIdException
					|| e instanceof UserPasswordException
					|| e instanceof UserScreenNameException
					|| e instanceof UserSmsException
					|| e instanceof WebsiteURLException) {

				SessionErrors.add(actionRequest, e.getClass(), e);
			} else {
				throw e;
			}
		}


		try {
			Layout layout = LayoutLocalServiceUtil.getFriendlyURLLayout(
					themeDisplay.getScopeGroupId(), false,
					PropsUtil.get(PropsKeys.COMPANY_SECURITY_STRANGERS_URL));

			String redirect = PortalUtil.getLayoutURL(layout, themeDisplay);

			sendRedirect(actionRequest, actionResponse, redirect);
		} catch (NoSuchLayoutException nsle) {
		}

	}

	public String render(StrutsPortletAction originalStrutsPortletAction,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		System.out.println("Wrapped por Andrés Mantilla");
		return originalStrutsPortletAction.render(null, portletConfig,
				renderRequest, renderResponse);
	}

	public void serveResource(StrutsPortletAction originalStrutsPortletAction,
			PortletConfig portletConfig, ResourceRequest resourceRequest,
			ResourceResponse resourceResponse) throws Exception {
		originalStrutsPortletAction.serveResource(originalStrutsPortletAction,
				portletConfig, resourceRequest, resourceResponse);
	}

	protected void addUser(ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(actionRequest);
		HttpSession session = request.getSession();

		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest
				.getAttribute(WebKeys.THEME_DISPLAY);

		Company company = themeDisplay.getCompany();

		boolean autoPassword = true;
		String password1 = null;
		String password2 = null;
		boolean autoScreenName = isAutoScreenName();
		String screenName = ParamUtil.getString(actionRequest, "screenName");
		String emailAddress = ParamUtil
				.getString(actionRequest, "emailAddress");
		long facebookId = ParamUtil.getLong(actionRequest, "facebookId");
		String openId = ParamUtil.getString(actionRequest, "openId");
		String firstName = ParamUtil.getString(actionRequest, "firstName");
		String middleName = ParamUtil.getString(actionRequest, "middleName");
		String lastName = ParamUtil.getString(actionRequest, "lastName");
		int prefixId = ParamUtil.getInteger(actionRequest, "prefixId");
		int suffixId = ParamUtil.getInteger(actionRequest, "suffixId");
		boolean male = ParamUtil.getBoolean(actionRequest, "male", true);
		int birthdayMonth = ParamUtil
				.getInteger(actionRequest, "birthdayMonth");
		int birthdayDay = ParamUtil.getInteger(actionRequest, "birthdayDay");
		int birthdayYear = ParamUtil.getInteger(actionRequest, "birthdayYear");
		String jobTitle = ParamUtil.getString(actionRequest, "jobTitle");
		long[] groupIds = null;
		long[] organizationIds = null;
		long[] roleIds = null;
		long[] userGroupIds = null;
		boolean sendEmail = true;

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
				User.class.getName(), actionRequest);

		User user = UserServiceUtil.addUserWithWorkflow(company.getCompanyId(),
				autoPassword, password1, password2, autoScreenName, screenName,
				emailAddress, facebookId, openId, themeDisplay.getLocale(),
				firstName, middleName, lastName, prefixId, suffixId, male,
				birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds,
				organizationIds, roleIds, userGroupIds, sendEmail,
				serviceContext);
		user.setPasswordReset(false);
		user.setReminderQueryQuestion("@new@");
        user.setReminderQueryAnswer("@new@");
		UserLocalServiceUtil.updateUser(user);

		// Session messages

		if (user.getStatus() == WorkflowConstants.STATUS_APPROVED) {
			SessionMessages.add(request, "user_added", user.getEmailAddress());
			SessionMessages.add(request, "user_added_password",
					user.getPasswordUnencrypted());
		} else {
			SessionMessages
					.add(request, "user_pending", user.getEmailAddress());
		}

		// Send redirect

		String login = null;

		if (company.getAuthType().equals(CompanyConstants.AUTH_TYPE_ID)) {
			login = String.valueOf(user.getUserId());
		} else if (company.getAuthType().equals(CompanyConstants.AUTH_TYPE_SN)) {
			login = user.getScreenName();
		} else {
			login = user.getEmailAddress();
		}

		sendRedirect(actionRequest, actionResponse, themeDisplay, login,
				user.getPasswordUnencrypted());
	}

	protected boolean isAutoScreenName() {
		return _AUTO_SCREEN_NAME;
	}

	protected void sendRedirect(ActionRequest actionRequest,
			ActionResponse actionResponse, ThemeDisplay themeDisplay,
			String login, String password) throws Exception {

		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(actionRequest);

		String redirect = PortalUtil.escapeRedirect(ParamUtil.getString(
				actionRequest, "redirect"));

		// if (Validator.isNotNull(redirect)) {
		HttpServletResponse response = PortalUtil
				.getHttpServletResponse(actionResponse);

		com.aoml.liferay.hooks.LoginUtil.login(request, response, login, password, false, null);
		// }
		// else {
		// PortletURL loginURL = LoginUtil.getLoginURL(
		// request, themeDisplay.getPlid());
		//
		// loginURL.setParameter("login", login);
		//
		// redirect = loginURL.toString();
		// }
		
		redirect = "/web/guest/home";
		System.out.println(redirect);

		actionResponse.sendRedirect(redirect);
	}

	protected void sendRedirect(ActionRequest actionRequest,
			ActionResponse actionResponse, String redirect) throws IOException,
			SystemException {

		if (SessionErrors.isEmpty(actionRequest)) {
			ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest
					.getAttribute(WebKeys.THEME_DISPLAY);

			LayoutTypePortlet layoutTypePortlet = themeDisplay
					.getLayoutTypePortlet();

			boolean hasPortletId = false;

			String portletId = (String) actionRequest
					.getAttribute(WebKeys.PORTLET_ID);

			try {
				hasPortletId = layoutTypePortlet.hasPortletId(portletId);
			} catch (Exception e) {
			}

			Portlet portlet = PortletLocalServiceUtil.getPortletById(
					themeDisplay.getCompanyId(), portletId);

			if (hasPortletId || portlet.isAddDefaultResource()) {
				addSuccessMessage(actionRequest, actionResponse);
			}
		}

		if (Validator.isNull(redirect)) {
			redirect = (String) actionRequest.getAttribute(WebKeys.REDIRECT);
		}

		if (Validator.isNull(redirect)) {
			redirect = ParamUtil.getString(actionRequest, "redirect");
		}

		if (Validator.isNotNull(redirect)) {

			// LPS-1928

			HttpServletRequest request = PortalUtil
					.getHttpServletRequest(actionRequest);

			if (BrowserSnifferUtil.isIe(request)
					&& (BrowserSnifferUtil.getMajorVersion(request) == 6.0)
					&& redirect.contains(StringPool.POUND)) {

				String redirectToken = "&#";

				if (!redirect.contains(StringPool.QUESTION)) {
					redirectToken = StringPool.QUESTION + redirectToken;
				}

				redirect = StringUtil.replace(redirect, StringPool.POUND,
						redirectToken);
			}

			redirect = PortalUtil.escapeRedirect(redirect);

			if (Validator.isNotNull(redirect)) {
				actionResponse.sendRedirect(redirect);
			}
		}
	}

	protected void addSuccessMessage(ActionRequest actionRequest,
			ActionResponse actionResponse) {

		PortletConfig portletConfig = (PortletConfig) actionRequest
				.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);

		boolean addProcessActionSuccessMessage = GetterUtil.getBoolean(
				portletConfig
						.getInitParameter("add-process-action-success-action"),
				true);

		if (!addProcessActionSuccessMessage) {
			return;
		}

		String successMessage = ParamUtil.getString(actionRequest,
				"successMessage");

		SessionMessages.add(actionRequest, "request_processed", successMessage);
	}

	protected void setForward(PortletRequest portletRequest, String forward) {
		portletRequest.setAttribute(getForwardKey(portletRequest), forward);
	}

	public static String getForwardKey(PortletRequest portletRequest) {
		String portletId = (String) portletRequest
				.getAttribute(WebKeys.PORTLET_ID);

		String portletNamespace = PortalUtil.getPortletNamespace(portletId);

		return portletNamespace
				.concat(com.liferay.portal.util.WebKeys.PORTLET_STRUTS_FORWARD);
	}

	private static final boolean _AUTO_SCREEN_NAME = false;
}
