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

package com.liferay.commerce.product.type.grouped.service.base;

import com.liferay.commerce.product.type.grouped.model.CPDefinitionGroupedEntry;
import com.liferay.commerce.product.type.grouped.service.CPDefinitionGroupedEntryService;
import com.liferay.commerce.product.type.grouped.service.persistence.CPDefinitionGroupedEntryPersistence;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdate;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdateFactoryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;
import com.liferay.portal.kernel.service.BaseServiceImpl;
import com.liferay.portal.kernel.service.persistence.ClassNamePersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;

import javax.sql.DataSource;

/**
 * Provides the base implementation for the cp definition grouped entry remote service.
 *
 * <p>
 * This implementation exists only as a container for the default service methods generated by ServiceBuilder. All custom service methods should be put in {@link com.liferay.commerce.product.type.grouped.service.impl.CPDefinitionGroupedEntryServiceImpl}.
 * </p>
 *
 * @author Andrea Di Giorgi
 * @see com.liferay.commerce.product.type.grouped.service.impl.CPDefinitionGroupedEntryServiceImpl
 * @generated
 */
public abstract class CPDefinitionGroupedEntryServiceBaseImpl
	extends BaseServiceImpl
	implements CPDefinitionGroupedEntryService, IdentifiableOSGiService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Use <code>CPDefinitionGroupedEntryService</code> via injection or a <code>org.osgi.util.tracker.ServiceTracker</code> or use <code>com.liferay.commerce.product.type.grouped.service.CPDefinitionGroupedEntryServiceUtil</code>.
	 */

	/**
	 * Returns the cp definition grouped entry local service.
	 *
	 * @return the cp definition grouped entry local service
	 */
	public com.liferay.commerce.product.type.grouped.service.
		CPDefinitionGroupedEntryLocalService
			getCPDefinitionGroupedEntryLocalService() {

		return cpDefinitionGroupedEntryLocalService;
	}

	/**
	 * Sets the cp definition grouped entry local service.
	 *
	 * @param cpDefinitionGroupedEntryLocalService the cp definition grouped entry local service
	 */
	public void setCPDefinitionGroupedEntryLocalService(
		com.liferay.commerce.product.type.grouped.service.
			CPDefinitionGroupedEntryLocalService
				cpDefinitionGroupedEntryLocalService) {

		this.cpDefinitionGroupedEntryLocalService =
			cpDefinitionGroupedEntryLocalService;
	}

	/**
	 * Returns the cp definition grouped entry remote service.
	 *
	 * @return the cp definition grouped entry remote service
	 */
	public CPDefinitionGroupedEntryService
		getCPDefinitionGroupedEntryService() {

		return cpDefinitionGroupedEntryService;
	}

	/**
	 * Sets the cp definition grouped entry remote service.
	 *
	 * @param cpDefinitionGroupedEntryService the cp definition grouped entry remote service
	 */
	public void setCPDefinitionGroupedEntryService(
		CPDefinitionGroupedEntryService cpDefinitionGroupedEntryService) {

		this.cpDefinitionGroupedEntryService = cpDefinitionGroupedEntryService;
	}

	/**
	 * Returns the cp definition grouped entry persistence.
	 *
	 * @return the cp definition grouped entry persistence
	 */
	public CPDefinitionGroupedEntryPersistence
		getCPDefinitionGroupedEntryPersistence() {

		return cpDefinitionGroupedEntryPersistence;
	}

	/**
	 * Sets the cp definition grouped entry persistence.
	 *
	 * @param cpDefinitionGroupedEntryPersistence the cp definition grouped entry persistence
	 */
	public void setCPDefinitionGroupedEntryPersistence(
		CPDefinitionGroupedEntryPersistence
			cpDefinitionGroupedEntryPersistence) {

		this.cpDefinitionGroupedEntryPersistence =
			cpDefinitionGroupedEntryPersistence;
	}

	/**
	 * Returns the counter local service.
	 *
	 * @return the counter local service
	 */
	public com.liferay.counter.kernel.service.CounterLocalService
		getCounterLocalService() {

		return counterLocalService;
	}

	/**
	 * Sets the counter local service.
	 *
	 * @param counterLocalService the counter local service
	 */
	public void setCounterLocalService(
		com.liferay.counter.kernel.service.CounterLocalService
			counterLocalService) {

		this.counterLocalService = counterLocalService;
	}

	/**
	 * Returns the class name local service.
	 *
	 * @return the class name local service
	 */
	public com.liferay.portal.kernel.service.ClassNameLocalService
		getClassNameLocalService() {

		return classNameLocalService;
	}

	/**
	 * Sets the class name local service.
	 *
	 * @param classNameLocalService the class name local service
	 */
	public void setClassNameLocalService(
		com.liferay.portal.kernel.service.ClassNameLocalService
			classNameLocalService) {

		this.classNameLocalService = classNameLocalService;
	}

	/**
	 * Returns the class name remote service.
	 *
	 * @return the class name remote service
	 */
	public com.liferay.portal.kernel.service.ClassNameService
		getClassNameService() {

		return classNameService;
	}

	/**
	 * Sets the class name remote service.
	 *
	 * @param classNameService the class name remote service
	 */
	public void setClassNameService(
		com.liferay.portal.kernel.service.ClassNameService classNameService) {

		this.classNameService = classNameService;
	}

	/**
	 * Returns the class name persistence.
	 *
	 * @return the class name persistence
	 */
	public ClassNamePersistence getClassNamePersistence() {
		return classNamePersistence;
	}

	/**
	 * Sets the class name persistence.
	 *
	 * @param classNamePersistence the class name persistence
	 */
	public void setClassNamePersistence(
		ClassNamePersistence classNamePersistence) {

		this.classNamePersistence = classNamePersistence;
	}

	/**
	 * Returns the resource local service.
	 *
	 * @return the resource local service
	 */
	public com.liferay.portal.kernel.service.ResourceLocalService
		getResourceLocalService() {

		return resourceLocalService;
	}

	/**
	 * Sets the resource local service.
	 *
	 * @param resourceLocalService the resource local service
	 */
	public void setResourceLocalService(
		com.liferay.portal.kernel.service.ResourceLocalService
			resourceLocalService) {

		this.resourceLocalService = resourceLocalService;
	}

	/**
	 * Returns the user local service.
	 *
	 * @return the user local service
	 */
	public com.liferay.portal.kernel.service.UserLocalService
		getUserLocalService() {

		return userLocalService;
	}

	/**
	 * Sets the user local service.
	 *
	 * @param userLocalService the user local service
	 */
	public void setUserLocalService(
		com.liferay.portal.kernel.service.UserLocalService userLocalService) {

		this.userLocalService = userLocalService;
	}

	/**
	 * Returns the user remote service.
	 *
	 * @return the user remote service
	 */
	public com.liferay.portal.kernel.service.UserService getUserService() {
		return userService;
	}

	/**
	 * Sets the user remote service.
	 *
	 * @param userService the user remote service
	 */
	public void setUserService(
		com.liferay.portal.kernel.service.UserService userService) {

		this.userService = userService;
	}

	/**
	 * Returns the user persistence.
	 *
	 * @return the user persistence
	 */
	public UserPersistence getUserPersistence() {
		return userPersistence;
	}

	/**
	 * Sets the user persistence.
	 *
	 * @param userPersistence the user persistence
	 */
	public void setUserPersistence(UserPersistence userPersistence) {
		this.userPersistence = userPersistence;
	}

	public void afterPropertiesSet() {
	}

	public void destroy() {
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return CPDefinitionGroupedEntryService.class.getName();
	}

	protected Class<?> getModelClass() {
		return CPDefinitionGroupedEntry.class;
	}

	protected String getModelClassName() {
		return CPDefinitionGroupedEntry.class.getName();
	}

	/**
	 * Performs a SQL query.
	 *
	 * @param sql the sql query
	 */
	protected void runSQL(String sql) {
		try {
			DataSource dataSource =
				cpDefinitionGroupedEntryPersistence.getDataSource();

			DB db = DBManagerUtil.getDB();

			sql = db.buildSQL(sql);
			sql = PortalUtil.transformSQL(sql);

			SqlUpdate sqlUpdate = SqlUpdateFactoryUtil.getSqlUpdate(
				dataSource, sql);

			sqlUpdate.update();
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	@BeanReference(
		type = com.liferay.commerce.product.type.grouped.service.CPDefinitionGroupedEntryLocalService.class
	)
	protected com.liferay.commerce.product.type.grouped.service.
		CPDefinitionGroupedEntryLocalService
			cpDefinitionGroupedEntryLocalService;

	@BeanReference(type = CPDefinitionGroupedEntryService.class)
	protected CPDefinitionGroupedEntryService cpDefinitionGroupedEntryService;

	@BeanReference(type = CPDefinitionGroupedEntryPersistence.class)
	protected CPDefinitionGroupedEntryPersistence
		cpDefinitionGroupedEntryPersistence;

	@ServiceReference(
		type = com.liferay.counter.kernel.service.CounterLocalService.class
	)
	protected com.liferay.counter.kernel.service.CounterLocalService
		counterLocalService;

	@ServiceReference(
		type = com.liferay.portal.kernel.service.ClassNameLocalService.class
	)
	protected com.liferay.portal.kernel.service.ClassNameLocalService
		classNameLocalService;

	@ServiceReference(
		type = com.liferay.portal.kernel.service.ClassNameService.class
	)
	protected com.liferay.portal.kernel.service.ClassNameService
		classNameService;

	@ServiceReference(type = ClassNamePersistence.class)
	protected ClassNamePersistence classNamePersistence;

	@ServiceReference(
		type = com.liferay.portal.kernel.service.ResourceLocalService.class
	)
	protected com.liferay.portal.kernel.service.ResourceLocalService
		resourceLocalService;

	@ServiceReference(
		type = com.liferay.portal.kernel.service.UserLocalService.class
	)
	protected com.liferay.portal.kernel.service.UserLocalService
		userLocalService;

	@ServiceReference(
		type = com.liferay.portal.kernel.service.UserService.class
	)
	protected com.liferay.portal.kernel.service.UserService userService;

	@ServiceReference(type = UserPersistence.class)
	protected UserPersistence userPersistence;

}