package com.aoml.portlets.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brian Wing Shun Chan
 */
public class PortletContextBagPool {

	public static void clear() {
		_instance._portletContextBagPool.clear();
	}

	public static PortletContextBag get(String servletContextName) {
		return _instance._get(servletContextName);
	}

	public static void put(
		String servletContextName, PortletContextBag portletContextBag) {

		_instance._put(servletContextName, portletContextBag);
	}

	public static PortletContextBag remove(String servletContextName) {
		return _instance._remove(servletContextName);
	}

	private PortletContextBagPool() {
		_portletContextBagPool =
			new ConcurrentHashMap<String, PortletContextBag>();
	}

	private PortletContextBag _get(String servletContextName) {
		return _portletContextBagPool.get(servletContextName);
	}

	private void _put(
		String servletContextName, PortletContextBag portletContextBag) {

		_portletContextBagPool.put(servletContextName, portletContextBag);
	}

	private PortletContextBag _remove(String servletContextName) {
		return _portletContextBagPool.remove(servletContextName);
	}

	private static PortletContextBagPool _instance =
		new PortletContextBagPool();

	private Map<String, PortletContextBag> _portletContextBagPool;

}