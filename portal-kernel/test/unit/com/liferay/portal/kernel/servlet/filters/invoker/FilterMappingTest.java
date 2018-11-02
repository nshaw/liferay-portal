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

package com.liferay.portal.kernel.servlet.filters.invoker;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.util.ProxyFactory;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterConfig;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Mika Koivisto
 */
public class FilterMappingTest {

	@Before
	public void setUp() {
		_dispatchers = new ArrayList<>();

		for (Dispatcher dispatcher : Dispatcher.values()) {
			_dispatchers.add(dispatcher.name());
		}

		_filterConfig = ProxyFactory.newDummyInstance(FilterConfig.class);
	}

	@Test
	public void testIsMatchURLPattern() {
		List<String> urlPatterns = new ArrayList<>();

		urlPatterns.add("/c/portal/login");

		FilterMapping filterMapping = new FilterMapping(
			StringPool.BLANK, null, _filterConfig, urlPatterns, _dispatchers);

		String uri = "/c/portal/login";

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest(HttpMethods.GET, uri);

		Assert.assertEquals(
			true,
			filterMapping.isMatch(
				mockHttpServletRequest, Dispatcher.REQUEST, uri));
	}

	private List<String> _dispatchers;
	private FilterConfig _filterConfig;

}