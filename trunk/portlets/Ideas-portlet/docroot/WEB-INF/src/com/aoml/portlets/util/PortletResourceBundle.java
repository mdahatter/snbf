package com.aoml.portlets.util;

import java.util.Enumeration;
import java.util.ResourceBundle;

import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ResourceBundleThreadLocal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.model.PortletInfo;

import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Brian Wing Shun Chan
 * @author Eduardo Lundgren
 * @author Shuyang Zhou
 */
public class PortletResourceBundle extends ResourceBundle {

	public PortletResourceBundle(PortletInfo portletInfo) {
		this(null, portletInfo);
	}

	public PortletResourceBundle(
		ResourceBundle parentResourceBundle, PortletInfo portletInfo) {

		parent = parentResourceBundle;

		_portletInfo = portletInfo;
	}

	@Override
	public Enumeration<String> getKeys() {
		return parent.getKeys();
	}

	@Override
	public Locale getLocale() {
		return parent.getLocale();
	}

	@Override
	protected Object handleGetObject(String key) {
		if (key == null) {
			throw new NullPointerException();
		}

		String value = null;

		if (parent != null) {
			try {
				value = parent.getString(key);
			}
			catch (MissingResourceException mre) {
			}
		}

		if ((value == null) || (value == ResourceBundleUtil.NULL_VALUE)) {
			value = _getJavaxPortletString(key);
		}

		if ((value == null) && ResourceBundleThreadLocal.isReplace()) {
			value = ResourceBundleUtil.NULL_VALUE;
		}

		return value;
	}

	private String _getJavaxPortletString(String key) {
		if (key.equals(JavaConstants.JAVAX_PORTLET_TITLE)) {
			return _portletInfo.getTitle();
		}
		else if (key.equals(JavaConstants.JAVAX_PORTLET_SHORT_TITLE)) {
			return _portletInfo.getShortTitle();
		}
		else if (key.equals(JavaConstants.JAVAX_PORTLET_KEYWORDS)) {
			return _portletInfo.getKeywords();
		}
		else if (key.equals(JavaConstants.JAVAX_PORTLET_DESCRIPTION)) {
			return _portletInfo.getDescription();
		}

		return null;
	}

	private PortletInfo _portletInfo;

}