package com.aoml.portlets.util;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Brian Wing Shun Chan
 */
public class RenderParametersPool {

	public static void clear(HttpServletRequest request, long plid) {
		Map<String, Map<String, String[]>> plidPool = get(request, plid);

		plidPool.clear();
	}

	public static void clear(
		HttpServletRequest request, long plid, String portletId) {

		Map<String, String[]> params = get(request, plid, portletId);

		params.clear();
	}

	public static Map<String, Map<String, String[]>> get(
		HttpServletRequest request, long plid) {

		HttpSession session = request.getSession();

		if (plid <= 0) {
			return new ConcurrentHashMap<String, Map<String, String[]>>();
		}

		Map<Long, Map<String, Map<String, String[]>>> pool =
			_getRenderParametersPool(session);

		Map<String, Map<String, String[]>> plidPool = pool.get(plid);

		if (plidPool == null) {
			plidPool = new ConcurrentHashMap<String, Map<String, String[]>>();

			pool.put(plid, plidPool);
		}

		return plidPool;
	}

	public static Map<String, String[]> get(
		HttpServletRequest request, long plid, String portletId) {

		Map<String, Map<String, String[]>> plidPool = get(request, plid);

		Map<String, String[]> params = plidPool.get(portletId);

		if (params == null) {
			params = new HashMap<String, String[]>();

			plidPool.put(portletId, params);
		}

		return params;
	}

	public static void put(
		HttpServletRequest request, long plid, String portletId,
		Map<String, String[]> params) {

		Map<String, Map<String, String[]>> plidPool = get(request, plid);

		plidPool.put(portletId, params);
	}

	private static Map<Long, Map<String, Map<String, String[]>>>
		_getRenderParametersPool(HttpSession session) {

		Map<Long, Map<String, Map<String, String[]>>> renderParametersPool =
			(Map<Long, Map<String, Map<String, String[]>>>)session.getAttribute(
				WebKeys.PORTLET_RENDER_PARAMETERS);

		if (renderParametersPool == null) {
			renderParametersPool = new ConcurrentHashMap
				<Long, Map<String, Map<String, String[]>>>();

			session.setAttribute(
				WebKeys.PORTLET_RENDER_PARAMETERS, renderParametersPool);
		}

		return renderParametersPool;
	}

}