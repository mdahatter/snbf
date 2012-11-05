package com.aoml.portlets.util;

import java.io.IOException;
import java.io.Serializable;

import java.util.Enumeration;
import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;

/**
 * @author Brian Wing Shun Chan
 */
public class PortletPreferencesWrapper
	implements PortletPreferences, Serializable {

	public PortletPreferencesWrapper(
		PortletPreferences portletPreferences, String lifecycle) {

		_portletPreferences = portletPreferences;
		_lifecycle = lifecycle;
	}

	@Override
	public boolean equals(Object obj) {
		PortletPreferencesWrapper portletPreferencesWrapper =
			(PortletPreferencesWrapper)obj;

		if (this == portletPreferencesWrapper) {
			return true;
		}

		if (getPortletPreferencesImpl().equals(
				portletPreferencesWrapper.getPortletPreferencesImpl())) {

			return true;
		}
		else {
			return false;
		}
	}

	public Map<String, String[]> getMap() {
		return _portletPreferences.getMap();
	}

	public Enumeration<String> getNames() {
		return _portletPreferences.getNames();
	}

	public PortletPreferencesImpl getPortletPreferencesImpl() {
		return (PortletPreferencesImpl)_portletPreferences;
	}

	/**
	 * @deprecated {@link #getPortletPreferencesImpl}
	 */
	public PortletPreferencesImpl getPreferencesImpl() {
		return getPortletPreferencesImpl();
	}

	public String getValue(String key, String def) {
		return _portletPreferences.getValue(key, def);
	}

	public String[] getValues(String key, String[] def) {
		return _portletPreferences.getValues(key, def);
	}

	@Override
	public int hashCode() {
		return _portletPreferences.hashCode();
	}

	public boolean isReadOnly(String key) {
		return _portletPreferences.isReadOnly(key);
	}

	public void reset(String key) throws ReadOnlyException {
		_portletPreferences.reset(key);
	}

	public void setValue(String key, String value) throws ReadOnlyException {
		_portletPreferences.setValue(key, value);
	}

	public void setValues(String key, String[] values)
		throws ReadOnlyException {

		_portletPreferences.setValues(key, values);
	}

	public void store() throws IOException, ValidatorException {

				_portletPreferences.store();

	}

	private String _lifecycle;
	private PortletPreferences _portletPreferences;

}