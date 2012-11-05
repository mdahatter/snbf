package com.aoml.portlets.util;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletURLGenerationListener;
import javax.portlet.filter.PortletFilter;

import com.liferay.portlet.CustomUserAttributes;

/**
 * @author Brian Wing Shun Chan
 */
public class PortletContextBag {

	public PortletContextBag(String servletContextName) {
		_servletContextName = servletContextName;
	}

	public Map<String, CustomUserAttributes> getCustomUserAttributes() {
		return _customUserAttributes;
	}

	public Map<String, PortletFilter> getPortletFilters() {
		return _portletFilters;
	}

	public Map<String, PortletURLGenerationListener> getPortletURLListeners() {
		return _urlListeners;
	}

	public String getServletContextName() {
		return _servletContextName;
	}

	private Map<String, CustomUserAttributes> _customUserAttributes =
		new HashMap<String, CustomUserAttributes>();
	private Map<String, PortletFilter> _portletFilters =
		new HashMap<String, PortletFilter>();
	private String _servletContextName;
	private Map<String, PortletURLGenerationListener> _urlListeners =
		new HashMap<String, PortletURLGenerationListener>();

}