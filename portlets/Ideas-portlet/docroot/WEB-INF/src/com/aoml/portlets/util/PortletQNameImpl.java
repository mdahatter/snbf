package com.aoml.portlets.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Namespace;
import com.liferay.portal.kernel.xml.QName;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portlet.PortletQName;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brian Wing Shun Chan
 */
public class PortletQNameImpl implements PortletQName {

	public PortletQNameImpl() {
		_qNames = new ConcurrentHashMap<String, QName>();
		_identifiers = new ConcurrentHashMap<String, String>();
	}

	public String getKey(QName qName) {
		return getKey(qName.getNamespaceURI(), qName.getLocalPart());
	}

	public String getKey(String uri, String localPart) {
		return uri.concat(_KEY_SEPARATOR).concat(localPart);
	}

	public String getPublicRenderParameterIdentifier(
		String publicRenderParameterName) {

		if (!publicRenderParameterName.startsWith(
				PUBLIC_RENDER_PARAMETER_NAMESPACE) &&
			!publicRenderParameterName.startsWith(
				REMOVE_PUBLIC_RENDER_PARAMETER_NAMESPACE)) {

			return null;
		}

		return _identifiers.get(publicRenderParameterName);
	}

	public String getPublicRenderParameterName(QName qName) {
		StringBundler sb = new StringBundler(4);

		sb.append(PUBLIC_RENDER_PARAMETER_NAMESPACE);
		sb.append(qName.getNamespaceURI().hashCode());
		sb.append(StringPool.UNDERLINE);
		sb.append(qName.getLocalPart());

		String publicRenderParameterName = sb.toString();

		if (!_qNames.containsKey(publicRenderParameterName)) {
			_qNames.put(publicRenderParameterName, qName);
		}

		return publicRenderParameterName;
	}

	public QName getQName(
		Element qNameEl, Element nameEl, String defaultNamespace) {

		if ((qNameEl == null) && (nameEl == null)) {
			_log.error("both qname and name elements are null");

			return null;
		}

		if (qNameEl == null) {
			return SAXReaderUtil.createQName(
				nameEl.getTextTrim(),
				SAXReaderUtil.createNamespace(defaultNamespace));
		}

		String localPart = qNameEl.getTextTrim();

		int pos = localPart.indexOf(CharPool.COLON);

		if (pos == -1) {
			if (_log.isDebugEnabled()) {
				_log.debug("qname " + localPart + " does not have a prefix");
			}

			return SAXReaderUtil.createQName(localPart);
		}

		String prefix = localPart.substring(0, pos);

		Namespace namespace = qNameEl.getNamespaceForPrefix(prefix);

		if (namespace == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"qname " + localPart + " does not have a valid namespace");
			}

			return null;
		}

		localPart = localPart.substring(prefix.length() + 1);

		return SAXReaderUtil.createQName(localPart, namespace);
	}

	public QName getQName(String publicRenderParameterName) {
		if (!publicRenderParameterName.startsWith(
				PUBLIC_RENDER_PARAMETER_NAMESPACE) &&
			!publicRenderParameterName.startsWith(
				REMOVE_PUBLIC_RENDER_PARAMETER_NAMESPACE)) {

			return null;
		}

		return _qNames.get(publicRenderParameterName);
	}

	public String getRemovePublicRenderParameterName(QName qName) {
		StringBundler sb = new StringBundler(4);

		sb.append(REMOVE_PUBLIC_RENDER_PARAMETER_NAMESPACE);
		sb.append(qName.getNamespaceURI().hashCode());
		sb.append(StringPool.UNDERLINE);
		sb.append(qName.getLocalPart());

		String removePublicRenderParameterName = sb.toString();

		if (!_qNames.containsKey(removePublicRenderParameterName)) {
			_qNames.put(removePublicRenderParameterName, qName);
		}

		return removePublicRenderParameterName;
	}

	public void setPublicRenderParameterIdentifier(
		String publicRenderParameterName, String identifier) {

		_identifiers.put(publicRenderParameterName, identifier);
	}

	private static final String _KEY_SEPARATOR = "_KEY_";

	private static Log _log = LogFactoryUtil.getLog(PortletQNameImpl.class);

	private Map<String, String> _identifiers;
	private Map<String, QName> _qNames;

}