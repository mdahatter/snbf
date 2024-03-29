package com.aoml.portlets.util;

import java.util.Enumeration;
import java.util.ResourceBundle;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ResourceBundleThreadLocal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringPool;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Brian Wing Shun Chan
 * @author Eduardo Lundgren
 */
public class StrutsResourceBundle extends ResourceBundle {

	public StrutsResourceBundle(String portletName, Locale locale) {
		_portletName = portletName;
		_locale = locale;
	}

	@Override
	public Enumeration<String> getKeys() {
		return null;
	}

	@Override
	public Locale getLocale() {
		return _locale;
	}

	@Override
	protected Object handleGetObject(String key) {
		if (key == null) {
			throw new NullPointerException();
		}

		if (key.equals(JavaConstants.JAVAX_PORTLET_DESCRIPTION) ||
			key.equals(JavaConstants.JAVAX_PORTLET_KEYWORDS) ||
			key.equals(JavaConstants.JAVAX_PORTLET_LONG_TITLE) ||
			key.equals(JavaConstants.JAVAX_PORTLET_SHORT_TITLE) ||
			key.equals(JavaConstants.JAVAX_PORTLET_TITLE)) {

			key = key.concat(StringPool.PERIOD).concat(_portletName);
		}

		String value = LanguageUtil.get(_locale, key);

		if ((value == null) && ResourceBundleThreadLocal.isReplace()) {
			value = ResourceBundleUtil.NULL_VALUE;
		}

		return value;
	}

	private Locale _locale;
	private String _portletName;

}