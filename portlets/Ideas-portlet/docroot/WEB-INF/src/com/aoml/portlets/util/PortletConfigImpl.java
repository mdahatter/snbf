package com.aoml.portlets.util;

import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.PortletBag;
import com.liferay.portal.kernel.portlet.PortletBagPool;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.model.PortletApp;
import com.liferay.portal.model.PortletConstants;
import com.liferay.portal.model.PublicRenderParameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.portlet.PortletContext;

import javax.xml.namespace.QName;

/**
 * @author Brian Wing Shun Chan
 * @author Eduardo Lundgren
 * @author Shuyang Zhou
 */
public class PortletConfigImpl implements LiferayPortletConfig {

	public PortletConfigImpl(Portlet portlet, PortletContext portletContext) {
		_portletApp = portlet.getPortletApp();
		_portlet = portlet;
		_portletName = portlet.getRootPortletId();

		int pos = _portletName.indexOf(PortletConstants.WAR_SEPARATOR);

		if (pos != -1) {
			_portletName = _portletName.substring(0, pos);
		}

		_portletContext = portletContext;
		_resourceBundles = new ConcurrentHashMap<String, ResourceBundle>();
	}

	public Map<String, String[]> getContainerRuntimeOptions() {
		return _portletApp.getContainerRuntimeOptions();
	}

	public String getDefaultNamespace() {
		return _portletApp.getDefaultNamespace();
	}

	public String getInitParameter(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		return _portlet.getInitParams().get(name);
	}

	public Enumeration<String> getInitParameterNames() {
		return Collections.enumeration(_portlet.getInitParams().keySet());
	}

	public Portlet getPortlet() {
		return _portlet;
	}

	public PortletContext getPortletContext() {
		return _portletContext;
	}

	public String getPortletId() {
		return _portlet.getPortletId();
	}

	public String getPortletName() {
		return _portletName;
	}

	public Enumeration<QName> getProcessingEventQNames() {
		return Collections.enumeration(
			toJavaxQNames(_portlet.getProcessingEvents()));
	}

	public Enumeration<String> getPublicRenderParameterNames() {
		List<String> publicRenderParameterNames = new ArrayList<String>();

		for (PublicRenderParameter publicRenderParameter :
				_portlet.getPublicRenderParameters()) {

			publicRenderParameterNames.add(
				publicRenderParameter.getIdentifier());
		}

		return Collections.enumeration(publicRenderParameterNames);
	}

	public Enumeration<QName> getPublishingEventQNames() {
		return Collections.enumeration(
			toJavaxQNames(_portlet.getPublishingEvents()));
	}

	public ResourceBundle getResourceBundle(Locale locale) {
		String resourceBundleClassName = _portlet.getResourceBundle();

		ResourceBundle resourceBundle = null;

		if (Validator.isNull(resourceBundleClassName)) {
			String resourceBundleId = _portlet.getPortletId();

			resourceBundle = _resourceBundles.get(resourceBundleId);

			if (resourceBundle == null) {
				resourceBundle = new PortletResourceBundle(
					_portlet.getPortletInfo());

				_resourceBundles.put(resourceBundleId, resourceBundle);
			}

			return resourceBundle;
		}
		else {
			StringBundler sb = new StringBundler(4);

			sb.append(_portlet.getPortletId());
			sb.append(locale.getLanguage());
			sb.append(locale.getCountry());
			sb.append(locale.getVariant());

			String resourceBundleId = sb.toString();

			resourceBundle = _resourceBundles.get(resourceBundleId);

			if (resourceBundle == null) {
				if (!_portletApp.isWARFile() &&
					resourceBundleClassName.equals(
						StrutsResourceBundle.class.getName())) {

					resourceBundle = new StrutsResourceBundle(
						_portletName, locale);
				}
				else {
					PortletBag portletBag = PortletBagPool.get(
						_portlet.getRootPortletId());

					resourceBundle = portletBag.getResourceBundle(locale);
				}

				resourceBundle = new PortletResourceBundle(
					resourceBundle, _portlet.getPortletInfo());

				_resourceBundles.put(resourceBundleId, resourceBundle);
			}

			return resourceBundle;
		}
	}

	public Enumeration<Locale> getSupportedLocales() {
		List<Locale> supportedLocales = new ArrayList<Locale>();

		for (String languageId : _portlet.getSupportedLocales()) {
			supportedLocales.add(LocaleUtil.fromLanguageId(languageId));
		}

		return Collections.enumeration(supportedLocales);
	}

	public boolean isWARFile() {
		return _portletApp.isWARFile();
	}

	protected Set<javax.xml.namespace.QName> toJavaxQNames(
		Set<com.liferay.portal.kernel.xml.QName> liferayQNames) {

		Set<QName> javaxQNames = new HashSet<QName>(liferayQNames.size());

		for (com.liferay.portal.kernel.xml.QName liferayQName : liferayQNames) {
			QName javaxQName = new QName(
				liferayQName.getNamespaceURI(), liferayQName.getLocalPart(),
				liferayQName.getNamespacePrefix());

			javaxQNames.add(javaxQName);
		}

		return javaxQNames;
	}

	private Portlet _portlet;
	private PortletApp _portletApp;
	private PortletContext _portletContext;
	private String _portletName;
	private Map<String, ResourceBundle> _resourceBundles;

}