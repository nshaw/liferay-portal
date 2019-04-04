/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portlet.tck.bridge;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.tck.bridge.configuration.PortletTCKBridgeConfiguration;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Tambara
 * @author Vernon Singleton
 * @author Kyle Stiemann
 */
@Component(
	configurationPid = "com.liferay.portlet.tck.bridge.configuration.PortletTCKBridgeConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE, service = {}
)
public class PortletTCKBridge {

	@Activate
	protected void activate(Map<String, String> properties) throws Exception {
		Company company = CompanyLocalServiceUtil.getCompanyByWebId(
			PropsValues.COMPANY_DEFAULT_WEB_ID);

		Group group = GroupLocalServiceUtil.fetchGroup(
			company.getCompanyId(), _TCK_SITE_GROUP_NAME);

		if (group != null) {
			return;
		}

		PortletTCKBridgeConfiguration portletTCKBridgeConfiguration =
			ConfigurableUtil.createConfigurable(
				PortletTCKBridgeConfiguration.class, properties);

		String tckDeployFilesDir =
			portletTCKBridgeConfiguration.tckDeployFilesDir();

		_setupPortletTCKSite(company, tckDeployFilesDir);

		if (_log.isInfoEnabled()) {
			_log.info("Portlet TCK Bridge is ready");
		}
	}

	private void _setupPortletTCKSite(Company company, String tckDeployFilesDir)
		throws Exception {

		User user = UserLocalServiceUtil.getUserByEmailAddress(
			company.getCompanyId(),
			PropsValues.DEFAULT_ADMIN_EMAIL_ADDRESS_PREFIX + "@" +
				company.getMx());

		long userId = user.getUserId();

		Map<Locale, String> nameMap = Collections.singletonMap(
			Locale.US, _TCK_SITE_GROUP_NAME);

		Group group = GroupLocalServiceUtil.addGroup(
			userId, GroupConstants.DEFAULT_PARENT_GROUP_ID, null, 0L,
			GroupConstants.DEFAULT_LIVE_GROUP_ID, nameMap, nameMap,
			GroupConstants.TYPE_SITE_OPEN, false,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, "/portlet-tck", true,
			false, true, new ServiceContext());

		File configFile = new File(
			tckDeployFilesDir + "/pluto-portal-driver-config.xml");

		Document document = SAXReaderUtil.read(configFile);

		Element rootElement = document.getRootElement();

		Element renderConfigElement = rootElement.element("render-config");

		for (Element pageElement : renderConfigElement.elements("page")) {
			List<String> portletIds = new ArrayList<>();

			for (Element portletElement : pageElement.elements("portlet")) {
				String context = portletElement.attributeValue("context");

				Matcher matcher = _portletContextPattern.matcher(context);

				if (matcher.find()) {
					portletIds.add(
						portletElement.attributeValue("name") + "_WAR_" +
							matcher.group(1));
				}
			}

			if (portletIds.isEmpty()) {
				continue;
			}

			String pageName = pageElement.attributeValue("name");

			Layout portalPageLayout = LayoutLocalServiceUtil.addLayout(
				userId, group.getGroupId(), true,
				LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, pageName, pageName,
				pageName, LayoutConstants.TYPE_PORTLET, false,
				"/" + StringUtil.toLowerCase(pageName), new ServiceContext());

			LayoutTypePortlet layoutTypePortlet =
				(LayoutTypePortlet)portalPageLayout.getLayoutType();

			layoutTypePortlet.setLayoutTemplateId(userId, "1_column", false);

			for (String portletId : portletIds) {
				layoutTypePortlet.addPortletId(
					userId, portletId, "column-1", -1, false);
			}

			LayoutLocalServiceUtil.updateLayout(portalPageLayout);
		}
	}

	private static final String _TCK_SITE_GROUP_NAME = "Portlet TCK";

	private static final Log _log = LogFactoryUtil.getLog(
		PortletTCKBridge.class);

	private static final Pattern _portletContextPattern = Pattern.compile(
		"/(tck-.*)(-[0-9.]+)-SNAPSHOT");

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED, unbind = "-")
	private ModuleServiceLifecycle _moduleServiceLifecycle;

}