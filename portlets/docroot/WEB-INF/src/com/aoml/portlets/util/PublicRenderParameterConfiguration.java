package com.aoml.portlets.util;

import com.liferay.portal.model.PublicRenderParameter;
import com.liferay.portlet.PortletQNameUtil;

/**
 * @author Alberto Montero
 */
public class PublicRenderParameterConfiguration {

	public static final String IGNORE_PREFIX = "lfr-prp-ignore-";

	public static final String MAPPING_PREFIX = "lfr-prp-mapping-";

	public static String getIgnoreKey(
		PublicRenderParameter publicRenderParameter) {

		String publicRenderParameterName =
			PortletQNameUtil.getPublicRenderParameterName(
				publicRenderParameter.getQName());

		return IGNORE_PREFIX.concat(publicRenderParameterName);
	}

	public static String getMappingKey(
		PublicRenderParameter publicRenderParameter) {

		String publicRenderParameterName =
			PortletQNameUtil.getPublicRenderParameterName(
				publicRenderParameter.getQName());

		return MAPPING_PREFIX.concat(publicRenderParameterName);
	}

	public PublicRenderParameterConfiguration(
		PublicRenderParameter publicRenderParameter, String mappingValue,
		boolean ignoreValue) {

		_publicRenderParameter = publicRenderParameter;
		_publicRenderParameterName =
			PortletQNameUtil.getPublicRenderParameterName(
				publicRenderParameter.getQName());
		_mappingValue = mappingValue;
		_ignoreValue = ignoreValue;
	}

	public String getIgnoreKey() {
		return IGNORE_PREFIX.concat(_publicRenderParameterName);
	}

	public boolean getIgnoreValue() {
		return _ignoreValue;
	}

	public String getMappingKey() {
		return MAPPING_PREFIX.concat(_publicRenderParameterName);
	}

	public String getMappingValue() {
		return _mappingValue;
	}

	public PublicRenderParameter getPublicRenderParameter() {
		return _publicRenderParameter;
	}

	public String getPublicRenderParameterName() {
		return _publicRenderParameterName;
	}

	private boolean _ignoreValue;
	private String _mappingValue;
	private PublicRenderParameter _publicRenderParameter;
	private String _publicRenderParameterName;

}