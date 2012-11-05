package com.aoml.portlets.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.util.servlet.NullSession;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

/**
 * @author Brian Wing Shun Chan
 */
public class SharedSessionWrapper implements HttpSession {

	public SharedSessionWrapper(
		HttpSession portalSession, HttpSession portletSession) {

		if (portalSession == null) {
			_portalSession = new NullSession();

			if (_log.isWarnEnabled()) {
				_log.warn("Wrapped portal session is null");
			}
		}

		_portalSession = portalSession;
		_portletSession = portletSession;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SharedSessionWrapper)) {
			return false;
		}

		SharedSessionWrapper sharedSessionWrapper = (SharedSessionWrapper)obj;

		if (Validator.equals(
				_portalSession, sharedSessionWrapper._portalSession) &&
			Validator.equals(
				_portletSession, sharedSessionWrapper._portletSession)) {

			return true;
		}

		return false;
	}

	public Object getAttribute(String name) {
		HttpSession session = getSessionDelegate(name);

		return session.getAttribute(name);
	}

	public Enumeration<String> getAttributeNames() {
		HttpSession session = getSessionDelegate();

		Enumeration<String> namesEnu = session.getAttributeNames();

		if (session == _portletSession) {
			List<String> namesList = Collections.list(namesEnu);

			Enumeration<String> portalSessionNamesEnu =
				_portalSession.getAttributeNames();

			while (portalSessionNamesEnu.hasMoreElements()) {
				String name = portalSessionNamesEnu.nextElement();

				if (containsSharedAttribute(name)) {
					namesList.add(name);
				}
			}

			namesEnu = Collections.enumeration(namesList);
		}

		return namesEnu;
	}

	public long getCreationTime() {
		HttpSession session = getSessionDelegate();

		return session.getCreationTime();
	}

	public String getId() {
		HttpSession session = getSessionDelegate();

		return session.getId();
	}

	public long getLastAccessedTime() {
		HttpSession session = getSessionDelegate();

		return session.getLastAccessedTime();
	}

	public int getMaxInactiveInterval() {
		HttpSession session = getSessionDelegate();

		return session.getMaxInactiveInterval();
	}

	public ServletContext getServletContext() {
		HttpSession session = getSessionDelegate();

		return session.getServletContext();
	}

	/**
	 * @deprecated
	 */
	public javax.servlet.http.HttpSessionContext getSessionContext() {
		HttpSession session = getSessionDelegate();

		return session.getSessionContext();
	}

	public Object getValue(String name) {
		return getAttribute(name);
	}

	public String[] getValueNames() {
		List<String> names = ListUtil.fromEnumeration(getAttributeNames());

		return names.toArray(new String[names.size()]);
	}

	@Override
	public int hashCode() {
		return _portalSession.hashCode() ^ _portletSession.hashCode();
	}

	public void invalidate() {
		HttpSession session = getSessionDelegate();

		session.invalidate();
	}

	public boolean isNew() {
		HttpSession session = getSessionDelegate();

		return session.isNew();
	}

	public void putValue(String name, Object value) {
		setAttribute(name, value);
	}

	public void removeAttribute(String name) {
		HttpSession session = getSessionDelegate(name);

		session.removeAttribute(name);
	}

	public void removeValue(String name) {
		removeAttribute(name);
	}

	public void setAttribute(String name, Object value) {
		HttpSession session = getSessionDelegate(name);

		session.setAttribute(name, value);
	}

	public void setMaxInactiveInterval(int maxInactiveInterval) {
		HttpSession session = getSessionDelegate();

		session.setMaxInactiveInterval(maxInactiveInterval);
	}

	protected boolean containsSharedAttribute(String name) {


		return false;
	}

	protected HttpSession getSessionDelegate() {
		if (_portletSession != null) {
			return _portletSession;
		}
		else {
			return _portalSession;
		}
	}

	protected HttpSession getSessionDelegate(String name) {
		if (_portletSession == null) {
			return _portalSession;
		}

		if (_sharedSessionAttributesExcludes.containsKey(name)) {
			return _portletSession;
		}
		else if (containsSharedAttribute(name)) {
			return _portalSession;
		}
		else {
			return _portletSession;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(SharedSessionWrapper.class);

	private static Map<String, String> _sharedSessionAttributesExcludes;



	private HttpSession _portalSession;
	private HttpSession _portletSession;

}