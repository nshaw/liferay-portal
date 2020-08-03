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

package com.liferay.blogs.web.internal.portlet.action;

import com.liferay.blogs.exception.NoSuchEntryException;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.web.internal.trackback.Trackback;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.Collections;
import java.util.function.Function;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.stubbing.answers.CallsRealMethods;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author André de Oliveira
 */
@PrepareForTest(ActionUtil.class)
@RunWith(PowerMockRunner.class)
public class TrackbackMVCActionCommandTest extends PowerMockito {

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		setUpActionRequest();
		setUpActionUtil();
		setUpBlogsEntry();
		setUpPortalUtil();
		setUpPortletPreferencesFactoryUtil();
		setUpPropsUtil();
	}

	@Test
	public void testDisabledComments() throws Exception {
		whenGetEntryThenReturn(_blogsEntry);

		when(
			_portletPreferences.getValue("enableComments", null)
		).thenReturn(
			"false"
		);

		addTrackback();

		assertError("Comments are disabled");
	}

	@Test
	public void testMismatchedIPAddress() throws Exception {
		whenGetEntryThenReturn(_blogsEntry);

		initURL("123");

		addTrackback();

		assertError("Remote IP does not match the trackback URL's IP");
	}

	@Test
	public void testMissingURL() throws Exception {
		whenGetEntryThenReturn(_blogsEntry);

		addTrackback();

		assertError("Trackback requires a valid permanent URL");
	}

	@Test(expected = NoSuchEntryException.class)
	public void testNoSuchEntryException() throws Exception {
		whenGetEntryThenThrow(new NoSuchEntryException());

		initValidURL();

		addTrackback();
	}

	@Test
	public void testPrincipalException() throws Exception {
		whenGetEntryThenThrow(new PrincipalException());

		initValidURL();

		addTrackback();

		assertError(
			"Blog entry must have guest view permissions to enable trackbacks");
	}

	@Test
	public void testSuccess() throws Exception {
		whenGetEntryThenReturn(_blogsEntry);

		_mockOriginalServletRequest.setParameter("blog_name", "__blogName__");
		_mockOriginalServletRequest.setParameter("excerpt", "__excerpt__");
		_mockOriginalServletRequest.setParameter("title", "__title__");

		initValidURL();

		addTrackback();

		assertSuccess();

		Mockito.verify(
			_trackback
		).addTrackback(
			Matchers.same(_blogsEntry), Matchers.same(_themeDisplay),
			Matchers.eq("__excerpt__"), Matchers.eq("__url__"),
			Matchers.eq("__blogName__"), Matchers.eq("__title__"),
			(Function<String, ServiceContext>)Matchers.any()
		);
	}

	@Test
	public void testTrackbacksNotEnabled() throws Exception {
		whenGetEntryThenReturn(_blogsEntry);

		when(
			_blogsEntry.isAllowTrackbacks()
		).thenReturn(
			false
		);

		initValidURL();

		addTrackback();

		assertError("Trackbacks are not enabled");
	}

	protected void addTrackback() throws Exception {
		TrackbackMVCActionCommand trackbackMVCActionCommand =
			new TrackbackMVCActionCommand();

		ReflectionTestUtil.setFieldValue(
			trackbackMVCActionCommand, "_http", _http);
		ReflectionTestUtil.setFieldValue(
			trackbackMVCActionCommand, "_portal", PortalUtil.getPortal());
		ReflectionTestUtil.setFieldValue(
			trackbackMVCActionCommand, "_trackback", _trackback);

		trackbackMVCActionCommand.addTrackback(_actionRequest, _actionResponse);
	}

	protected void assertError(String msg) throws Exception {
		assertResponseContent(
			StringBundler.concat(
				"<?xml version=\"1.0\" encoding=\"utf-8\"?><response><error>1",
				"</error><message>", msg, "</message></response>"));
	}

	protected void assertResponseContent(String expected) throws Exception {
		Assert.assertEquals(
			expected, _mockHttpServletResponse.getContentAsString());
	}

	protected void assertSuccess() throws Exception {
		assertResponseContent(
			"<?xml version=\"1.0\" encoding=\"utf-8\"?><response><error>0" +
				"</error></response>");
	}

	protected void initURL(String remoteIP) {
		String url = "__url__";

		when(
			_http.getIpAddress(url)
		).thenReturn(
			remoteIP
		);

		_mockOriginalServletRequest.addParameter("url", url);
	}

	protected void initValidURL() {
		initURL(_mockHttpServletRequest.getRemoteAddr());
	}

	protected void setUpActionRequest() {
		when(
			_actionRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);

		when(
			_actionRequest.getPreferences()
		).thenReturn(
			_portletPreferences
		);

		when(
			_actionRequest.getAttribute(WebKeys.BLOGS_ENTRY)
		).thenReturn(
			_blogsEntry
		);
	}

	protected void setUpActionUtil() {
		mockStatic(ActionUtil.class, new CallsRealMethods());
	}

	protected void setUpBlogsEntry() {
		when(
			_blogsEntry.isAllowTrackbacks()
		).thenReturn(
			true
		);
	}

	protected void setUpPortalUtil() throws Exception {
		PortalUtil portalUtil = new PortalUtil();

		Portal portal = mock(Portal.class);

		when(
			portal.getOriginalServletRequest((HttpServletRequest)Matchers.any())
		).thenReturn(
			_mockOriginalServletRequest
		);

		when(
			portal.getHttpServletRequest((PortletRequest)Matchers.any())
		).thenReturn(
			_mockHttpServletRequest
		);

		when(
			portal.getHttpServletResponse((PortletResponse)Matchers.any())
		).thenReturn(
			_mockHttpServletResponse
		);

		portalUtil.setPortal(portal);
	}

	protected void setUpPortletPreferencesFactoryUtil() throws Exception {
		PortletPreferencesFactoryUtil portletPreferencesFactoryUtil =
			new PortletPreferencesFactoryUtil();

		PortletPreferencesFactory portletPreferencesFactory = mock(
			PortletPreferencesFactory.class);

		when(
			portletPreferencesFactory.getExistingPortletSetup(
				Mockito.any(PortletRequest.class))
		).thenReturn(
			_portletPreferences
		);

		portletPreferencesFactoryUtil.setPortletPreferencesFactory(
			portletPreferencesFactory);
	}

	protected void setUpPropsUtil() throws Exception {
		PropsTestUtil.setProps(Collections.emptyMap());
	}

	protected void whenGetEntryThenReturn(BlogsEntry blogsEntry) {
		stub(
			method(ActionUtil.class, "getEntry", PortletRequest.class)
		).toReturn(
			blogsEntry
		);
	}

	protected void whenGetEntryThenThrow(Throwable throwable) throws Exception {
		stub(
			method(ActionUtil.class, "getEntry", PortletRequest.class)
		).toThrow(
			throwable
		);
	}

	@Mock
	private ActionRequest _actionRequest;

	@Mock
	private ActionResponse _actionResponse;

	@Mock
	private BlogsEntry _blogsEntry;

	@Mock
	private Http _http;

	private final MockHttpServletRequest _mockHttpServletRequest =
		new MockHttpServletRequest();
	private final MockHttpServletResponse _mockHttpServletResponse =
		new MockHttpServletResponse();
	private final MockHttpServletRequest _mockOriginalServletRequest =
		new MockHttpServletRequest();

	@Mock
	private PortletPreferences _portletPreferences;

	private final ThemeDisplay _themeDisplay = new ThemeDisplay();

	@Mock
	private Trackback _trackback;

}