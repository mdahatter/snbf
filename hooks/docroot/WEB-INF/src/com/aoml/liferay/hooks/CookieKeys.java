package com.aoml.liferay.hooks;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Hex;

import com.liferay.portal.CookieNotSupportedException;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsUtil;
import com.liferay.util.CookieUtil;

public class CookieKeys  implements com.liferay.portal.kernel.util.CookieKeys {
	public static final int MAX_AGE = 31536000;

	public static final int VERSION = 0;

	public static void addCookie(
		HttpServletRequest request, HttpServletResponse response,
		Cookie cookie) {

		addCookie(request, response, cookie, request.isSecure());
	}

	public static void addCookie(
		HttpServletRequest request, HttpServletResponse response, Cookie cookie,
		boolean secure) {

		if (!GetterUtil.getBoolean(com.liferay.portal.kernel.util.PropsUtil.get(com.liferay.portal.util.PropsKeys.SESSION_ENABLE_PERSISTENT_COOKIES)) ||
				GetterUtil.getBoolean(com.liferay.portal.kernel.util.PropsUtil.get(com.liferay.portal.util.PropsKeys.TCK_URL))) {

			return;
		}

		// LEP-5175

		String name = cookie.getName();

		String originalValue = cookie.getValue();
		String encodedValue = originalValue;

		if (isEncodedCookie(name)) {
			encodedValue = new String(Hex.encodeHex(originalValue.getBytes()));


		}

		cookie.setSecure(secure);
		cookie.setValue(encodedValue);
		cookie.setVersion(VERSION);

		// Setting a cookie will cause the TCK to lose its ability to track
		// sessions

		response.addCookie(cookie);
	}

	public static void addSupportCookie(
		HttpServletRequest request, HttpServletResponse response) {

		Cookie cookieSupportCookie = new Cookie(COOKIE_SUPPORT, "true");

		cookieSupportCookie.setPath(StringPool.SLASH);
		cookieSupportCookie.setMaxAge(MAX_AGE);

		addCookie(request, response, cookieSupportCookie);
	}

	public static String getCookie(HttpServletRequest request, String name) {
		return getCookie(request, name, true);
	}

	public static String getCookie(
		HttpServletRequest request, String name, boolean toUpperCase) {

		String value = CookieUtil.get(request, name, toUpperCase);

		if ((value != null) && isEncodedCookie(name)) {
			try {
				String encodedValue = value;
				String originalValue = new String(
					Hex.decodeHex(encodedValue.toCharArray()));



				return originalValue;
			}
			catch (Exception e) {

				return value;
			}
		}

		return value;
	}

	public static String getDomain(HttpServletRequest request) {

		// See LEP-4602 and	LEP-4618.

		if (Validator.isNotNull(com.liferay.portal.kernel.util.PropsUtil.get(com.liferay.portal.util.PropsKeys.SESSION_COOKIE_DOMAIN))) {
			return com.liferay.portal.kernel.util.PropsUtil.get(com.liferay.portal.util.PropsKeys.SESSION_COOKIE_DOMAIN);
		}

		String host = request.getServerName();

		return getDomain(host);
	}

	public static String getDomain(String host) {

		// See LEP-4602 and LEP-4645.

		if (host == null) {
			return null;
		}

		// See LEP-5595.

		if (Validator.isIPAddress(host)) {
			return host;
		}

		int x = host.lastIndexOf(CharPool.PERIOD);

		if (x <= 0) {
			return null;
		}

		int y = host.lastIndexOf(CharPool.PERIOD, x - 1);

		if (y <= 0) {
			return StringPool.PERIOD + host;
		}

		int z = host.lastIndexOf(CharPool.PERIOD, y - 1);

		String domain = null;

		if (z <= 0) {
			domain = host.substring(y);
		}
		else {
			domain = host.substring(z);
		}

		return domain;
	}

	public static boolean hasSessionId(HttpServletRequest request) {
		String jsessionid = getCookie(request, JSESSIONID, false);

		if (jsessionid != null) {
			return true;
		}
		else {
			return false;
		}
	}

	public static boolean isEncodedCookie(String name) {
		if (name.equals(ID) || name.equals(LOGIN) || name.equals(PASSWORD) ||
			name.equals(SCREEN_NAME)) {

			return true;
		}
		else {
			return false;
		}
	}

	public static void validateSupportCookie(HttpServletRequest request)
		throws CookieNotSupportedException {

		if (GetterUtil.getBoolean(com.liferay.portal.kernel.util.PropsUtil.get(com.liferay.portal.util.PropsKeys.SESSION_ENABLE_PERSISTENT_COOKIES)) &&
				GetterUtil.getBoolean(com.liferay.portal.kernel.util.PropsUtil.get(com.liferay.portal.util.PropsKeys.SESSION_TEST_COOKIE_SUPPORT))) {

			String cookieSupport = getCookie(request, COOKIE_SUPPORT, false);

			if (Validator.isNull(cookieSupport)) {
				throw new CookieNotSupportedException();
			}
		}
	}
}