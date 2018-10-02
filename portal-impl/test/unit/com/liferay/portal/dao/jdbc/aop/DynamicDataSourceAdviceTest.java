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

package com.liferay.portal.dao.jdbc.aop;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.aop.DynamicDataSourceTargetSource;
import com.liferay.portal.kernel.dao.jdbc.aop.MasterDataSource;
import com.liferay.portal.kernel.dao.jdbc.aop.Operation;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.aop.AnnotationChainableMethodAdvice;
import com.liferay.portal.spring.aop.ServiceBeanAopCacheManager;
import com.liferay.portal.spring.aop.ServiceBeanMethodInvocation;
import com.liferay.portal.spring.transaction.TransactionInterceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.Map;
import java.util.Stack;

import javax.sql.DataSource;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class DynamicDataSourceAdviceTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@Before
	public void setUp() {
		_dynamicDataSourceAdvice = new DynamicDataSourceAdvice();

		_dynamicDataSourceTargetSource =
			new DefaultDynamicDataSourceTargetSource();

		ClassLoader classLoader =
			DynamicDataSourceAdviceTest.class.getClassLoader();

		InvocationHandler invocationHandler = new InvocationHandler() {

			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {

				throw new UnsupportedOperationException();
			}

		};

		_readDataSource = (DataSource)ProxyUtil.newProxyInstance(
			classLoader, new Class<?>[] {DataSource.class}, invocationHandler);

		_dynamicDataSourceTargetSource.setReadDataSource(_readDataSource);

		_writeDataSource = (DataSource)ProxyUtil.newProxyInstance(
			classLoader, new Class<?>[] {DataSource.class}, invocationHandler);

		_dynamicDataSourceTargetSource.setWriteDataSource(_writeDataSource);

		_dynamicDataSourceAdvice.setDynamicDataSourceTargetSource(
			_dynamicDataSourceTargetSource);

		_serviceBeanAopCacheManager = new ServiceBeanAopCacheManager(
			_dynamicDataSourceAdvice);

		Map<Class<? extends Annotation>, AnnotationChainableMethodAdvice<?>[]>
			registeredAnnotationChainableMethodAdvices =
				ReflectionTestUtil.getFieldValue(
					_serviceBeanAopCacheManager,
					"_annotationChainableMethodAdvices");

		AnnotationChainableMethodAdvice<?>[] annotationChainableMethodAdvices =
			registeredAnnotationChainableMethodAdvices.get(
				MasterDataSource.class);

		Assert.assertEquals(
			Arrays.toString(annotationChainableMethodAdvices), 1,
			annotationChainableMethodAdvices.length);
		Assert.assertSame(
			_dynamicDataSourceAdvice, annotationChainableMethodAdvices[0]);
		Assert.assertSame(
			annotationChainableMethodAdvices,
			registeredAnnotationChainableMethodAdvices.get(
				MasterDataSource.class));

		_dynamicDataSourceAdvice.setTransactionInterceptor(
			new TransactionInterceptor());
	}

	@Test
	public void testAnnotationType() {
		MasterDataSource masterDataSource = ReflectionTestUtil.getFieldValue(
			DynamicDataSourceAdvice.class, "_nullMasterDataSource");

		Assert.assertSame(
			MasterDataSource.class, masterDataSource.annotationType());
	}

	@Test
	public void testDeprecatedMethods() {
		_dynamicDataSourceAdvice.setTransactionAttributeSource(null);
	}

	@Test
	public void testDynamicDataSourceAdvice() throws Throwable {
		TestClass testClass = new TestClass();

		for (int i = 1; i <= 6; i++) {
			MethodInvocation methodInvocation = createMethodInvocation(
				testClass, "method" + i);

			methodInvocation.proceed();
		}

		testClass.assertExecutions();
	}

	protected MethodInvocation createMethodInvocation(
			TestClass testClass, String methodName)
		throws Exception {

		Method method = TestClass.class.getMethod(methodName);

		ServiceBeanMethodInvocation serviceBeanMethodInvocation =
			new ServiceBeanMethodInvocation(testClass, method, new Object[0]);

		serviceBeanMethodInvocation.setMethodInterceptors(
			new MethodInterceptor[] {_dynamicDataSourceAdvice});

		return serviceBeanMethodInvocation;
	}

	private DynamicDataSourceAdvice _dynamicDataSourceAdvice;
	private DynamicDataSourceTargetSource _dynamicDataSourceTargetSource;
	private DataSource _readDataSource;
	private ServiceBeanAopCacheManager _serviceBeanAopCacheManager;
	private DataSource _writeDataSource;

	private class TestClass {

		public void assertExecutions() {
			Assert.assertTrue(_testMethod1);
			Assert.assertTrue(_testMethod2);
			Assert.assertTrue(_testMethod3);
			Assert.assertTrue(_testMethod4);
			Assert.assertTrue(_testMethod5);
			Assert.assertTrue(_testMethod6);
		}

		@SuppressWarnings("unused")
		public void method1() throws Exception {
			Assert.assertEquals(
				Operation.WRITE, _dynamicDataSourceTargetSource.getOperation());
			Assert.assertSame(
				_writeDataSource, _dynamicDataSourceTargetSource.getTarget());
			Assert.assertEquals(
				TestClass.class.getName() + StringPool.PERIOD + "method1",
				_getCurrentMethod());

			_testMethod1 = true;
		}

		@Transactional
		public void method2() throws Exception {
			Assert.assertEquals(
				Operation.WRITE, _dynamicDataSourceTargetSource.getOperation());
			Assert.assertSame(
				_writeDataSource, _dynamicDataSourceTargetSource.getTarget());
			Assert.assertEquals(
				TestClass.class.getName() + StringPool.PERIOD + "method2",
				_getCurrentMethod());

			_testMethod2 = true;
		}

		@Transactional(readOnly = true)
		public void method3() throws Exception {
			Assert.assertEquals(
				Operation.READ, _dynamicDataSourceTargetSource.getOperation());
			Assert.assertSame(
				_readDataSource, _dynamicDataSourceTargetSource.getTarget());
			Assert.assertEquals(
				TestClass.class.getName() + StringPool.PERIOD + "method3",
				_getCurrentMethod());

			_testMethod3 = true;
		}

		@MasterDataSource
		@Transactional
		public void method4() throws Exception {
			Assert.assertEquals(
				Operation.WRITE, _dynamicDataSourceTargetSource.getOperation());
			Assert.assertSame(
				_writeDataSource, _dynamicDataSourceTargetSource.getTarget());
			Assert.assertEquals(
				TestClass.class.getName() + StringPool.PERIOD + "method4",
				_getCurrentMethod());

			_testMethod4 = true;
		}

		@MasterDataSource
		@Transactional(readOnly = true)
		public void method5() throws Exception {
			Assert.assertEquals(
				Operation.WRITE, _dynamicDataSourceTargetSource.getOperation());
			Assert.assertSame(
				_writeDataSource, _dynamicDataSourceTargetSource.getTarget());
			Assert.assertEquals(
				TestClass.class.getName() + StringPool.PERIOD + "method5",
				_getCurrentMethod());

			_testMethod5 = true;
		}

		@Transactional(readOnly = true)
		public void method6() throws Throwable {
			MethodInvocation methodInvocation = createMethodInvocation(
				this, "method3");

			methodInvocation.proceed();

			Assert.assertEquals(
				Operation.READ, _dynamicDataSourceTargetSource.getOperation());
			Assert.assertSame(
				_readDataSource, _dynamicDataSourceTargetSource.getTarget());
			Assert.assertEquals(
				TestClass.class.getName() + StringPool.PERIOD + "method6",
				_getCurrentMethod());

			methodInvocation = createMethodInvocation(this, "method1");

			methodInvocation.proceed();

			Assert.assertEquals(
				Operation.WRITE, _dynamicDataSourceTargetSource.getOperation());
			Assert.assertSame(
				_writeDataSource, _dynamicDataSourceTargetSource.getTarget());
			Assert.assertEquals(
				TestClass.class.getName() + StringPool.PERIOD + "method6",
				_getCurrentMethod());

			_testMethod6 = true;
		}

		private String _getCurrentMethod() {
			Stack<String> stack =
				_dynamicDataSourceTargetSource.getMethodStack();

			return stack.peek();
		}

		private boolean _testMethod1;
		private boolean _testMethod2;
		private boolean _testMethod3;
		private boolean _testMethod4;
		private boolean _testMethod5;
		private boolean _testMethod6;

	}

}