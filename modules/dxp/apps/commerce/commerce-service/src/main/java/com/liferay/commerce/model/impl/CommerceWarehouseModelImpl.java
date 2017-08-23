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

package com.liferay.commerce.model.impl;

import aQute.bnd.annotation.ProviderType;

import com.liferay.commerce.model.CommerceWarehouse;
import com.liferay.commerce.model.CommerceWarehouseModel;
import com.liferay.commerce.model.CommerceWarehouseSoap;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.util.ExpandoBridgeFactoryUtil;

import com.liferay.portal.kernel.bean.AutoEscapeBeanHandler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSON;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.impl.BaseModelImpl;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;

import java.io.Serializable;

import java.sql.Types;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The base model implementation for the CommerceWarehouse service. Represents a row in the &quot;CommerceWarehouse&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This implementation and its corresponding interface {@link CommerceWarehouseModel} exist only as a container for the default property accessors generated by ServiceBuilder. Helper methods and all application logic should be put in {@link CommerceWarehouseImpl}.
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @see CommerceWarehouseImpl
 * @see CommerceWarehouse
 * @see CommerceWarehouseModel
 * @generated
 */
@JSON(strict = true)
@ProviderType
public class CommerceWarehouseModelImpl extends BaseModelImpl<CommerceWarehouse>
	implements CommerceWarehouseModel {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. All methods that expect a commerce warehouse model instance should use the {@link CommerceWarehouse} interface instead.
	 */
	public static final String TABLE_NAME = "CommerceWarehouse";
	public static final Object[][] TABLE_COLUMNS = {
			{ "commerceWarehouseId", Types.BIGINT },
			{ "groupId", Types.BIGINT },
			{ "companyId", Types.BIGINT },
			{ "userId", Types.BIGINT },
			{ "userName", Types.VARCHAR },
			{ "createDate", Types.TIMESTAMP },
			{ "modifiedDate", Types.TIMESTAMP },
			{ "name", Types.VARCHAR },
			{ "description", Types.VARCHAR },
			{ "street1", Types.VARCHAR },
			{ "street2", Types.VARCHAR },
			{ "street3", Types.VARCHAR },
			{ "city", Types.VARCHAR },
			{ "zip", Types.VARCHAR },
			{ "commerceRegionId", Types.BIGINT },
			{ "commerceCountryId", Types.BIGINT }
		};
	public static final Map<String, Integer> TABLE_COLUMNS_MAP = new HashMap<String, Integer>();

	static {
		TABLE_COLUMNS_MAP.put("commerceWarehouseId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("groupId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("companyId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("userId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("userName", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("createDate", Types.TIMESTAMP);
		TABLE_COLUMNS_MAP.put("modifiedDate", Types.TIMESTAMP);
		TABLE_COLUMNS_MAP.put("name", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("description", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("street1", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("street2", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("street3", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("city", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("zip", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("commerceRegionId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("commerceCountryId", Types.BIGINT);
	}

	public static final String TABLE_SQL_CREATE = "create table CommerceWarehouse (commerceWarehouseId LONG not null primary key,groupId LONG,companyId LONG,userId LONG,userName VARCHAR(75) null,createDate DATE null,modifiedDate DATE null,name VARCHAR(75) null,description STRING null,street1 VARCHAR(75) null,street2 VARCHAR(75) null,street3 VARCHAR(75) null,city VARCHAR(75) null,zip VARCHAR(75) null,commerceRegionId LONG,commerceCountryId LONG)";
	public static final String TABLE_SQL_DROP = "drop table CommerceWarehouse";
	public static final String ORDER_BY_JPQL = " ORDER BY commerceWarehouse.name ASC";
	public static final String ORDER_BY_SQL = " ORDER BY CommerceWarehouse.name ASC";
	public static final String DATA_SOURCE = "liferayDataSource";
	public static final String SESSION_FACTORY = "liferaySessionFactory";
	public static final String TX_MANAGER = "liferayTransactionManager";
	public static final boolean ENTITY_CACHE_ENABLED = GetterUtil.getBoolean(com.liferay.commerce.service.util.ServiceProps.get(
				"value.object.entity.cache.enabled.com.liferay.commerce.model.CommerceWarehouse"),
			true);
	public static final boolean FINDER_CACHE_ENABLED = GetterUtil.getBoolean(com.liferay.commerce.service.util.ServiceProps.get(
				"value.object.finder.cache.enabled.com.liferay.commerce.model.CommerceWarehouse"),
			true);
	public static final boolean COLUMN_BITMASK_ENABLED = GetterUtil.getBoolean(com.liferay.commerce.service.util.ServiceProps.get(
				"value.object.column.bitmask.enabled.com.liferay.commerce.model.CommerceWarehouse"),
			true);
	public static final long COMMERCECOUNTRYID_COLUMN_BITMASK = 1L;
	public static final long GROUPID_COLUMN_BITMASK = 2L;
	public static final long NAME_COLUMN_BITMASK = 4L;

	/**
	 * Converts the soap model instance into a normal model instance.
	 *
	 * @param soapModel the soap model instance to convert
	 * @return the normal model instance
	 */
	public static CommerceWarehouse toModel(CommerceWarehouseSoap soapModel) {
		if (soapModel == null) {
			return null;
		}

		CommerceWarehouse model = new CommerceWarehouseImpl();

		model.setCommerceWarehouseId(soapModel.getCommerceWarehouseId());
		model.setGroupId(soapModel.getGroupId());
		model.setCompanyId(soapModel.getCompanyId());
		model.setUserId(soapModel.getUserId());
		model.setUserName(soapModel.getUserName());
		model.setCreateDate(soapModel.getCreateDate());
		model.setModifiedDate(soapModel.getModifiedDate());
		model.setName(soapModel.getName());
		model.setDescription(soapModel.getDescription());
		model.setStreet1(soapModel.getStreet1());
		model.setStreet2(soapModel.getStreet2());
		model.setStreet3(soapModel.getStreet3());
		model.setCity(soapModel.getCity());
		model.setZip(soapModel.getZip());
		model.setCommerceRegionId(soapModel.getCommerceRegionId());
		model.setCommerceCountryId(soapModel.getCommerceCountryId());

		return model;
	}

	/**
	 * Converts the soap model instances into normal model instances.
	 *
	 * @param soapModels the soap model instances to convert
	 * @return the normal model instances
	 */
	public static List<CommerceWarehouse> toModels(
		CommerceWarehouseSoap[] soapModels) {
		if (soapModels == null) {
			return null;
		}

		List<CommerceWarehouse> models = new ArrayList<CommerceWarehouse>(soapModels.length);

		for (CommerceWarehouseSoap soapModel : soapModels) {
			models.add(toModel(soapModel));
		}

		return models;
	}

	public static final long LOCK_EXPIRATION_TIME = GetterUtil.getLong(com.liferay.commerce.service.util.ServiceProps.get(
				"lock.expiration.time.com.liferay.commerce.model.CommerceWarehouse"));

	public CommerceWarehouseModelImpl() {
	}

	@Override
	public long getPrimaryKey() {
		return _commerceWarehouseId;
	}

	@Override
	public void setPrimaryKey(long primaryKey) {
		setCommerceWarehouseId(primaryKey);
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _commerceWarehouseId;
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		setPrimaryKey(((Long)primaryKeyObj).longValue());
	}

	@Override
	public Class<?> getModelClass() {
		return CommerceWarehouse.class;
	}

	@Override
	public String getModelClassName() {
		return CommerceWarehouse.class.getName();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("commerceWarehouseId", getCommerceWarehouseId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("name", getName());
		attributes.put("description", getDescription());
		attributes.put("street1", getStreet1());
		attributes.put("street2", getStreet2());
		attributes.put("street3", getStreet3());
		attributes.put("city", getCity());
		attributes.put("zip", getZip());
		attributes.put("commerceRegionId", getCommerceRegionId());
		attributes.put("commerceCountryId", getCommerceCountryId());

		attributes.put("entityCacheEnabled", isEntityCacheEnabled());
		attributes.put("finderCacheEnabled", isFinderCacheEnabled());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long commerceWarehouseId = (Long)attributes.get("commerceWarehouseId");

		if (commerceWarehouseId != null) {
			setCommerceWarehouseId(commerceWarehouseId);
		}

		Long groupId = (Long)attributes.get("groupId");

		if (groupId != null) {
			setGroupId(groupId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		String userName = (String)attributes.get("userName");

		if (userName != null) {
			setUserName(userName);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String description = (String)attributes.get("description");

		if (description != null) {
			setDescription(description);
		}

		String street1 = (String)attributes.get("street1");

		if (street1 != null) {
			setStreet1(street1);
		}

		String street2 = (String)attributes.get("street2");

		if (street2 != null) {
			setStreet2(street2);
		}

		String street3 = (String)attributes.get("street3");

		if (street3 != null) {
			setStreet3(street3);
		}

		String city = (String)attributes.get("city");

		if (city != null) {
			setCity(city);
		}

		String zip = (String)attributes.get("zip");

		if (zip != null) {
			setZip(zip);
		}

		Long commerceRegionId = (Long)attributes.get("commerceRegionId");

		if (commerceRegionId != null) {
			setCommerceRegionId(commerceRegionId);
		}

		Long commerceCountryId = (Long)attributes.get("commerceCountryId");

		if (commerceCountryId != null) {
			setCommerceCountryId(commerceCountryId);
		}
	}

	@JSON
	@Override
	public long getCommerceWarehouseId() {
		return _commerceWarehouseId;
	}

	@Override
	public void setCommerceWarehouseId(long commerceWarehouseId) {
		_commerceWarehouseId = commerceWarehouseId;
	}

	@JSON
	@Override
	public long getGroupId() {
		return _groupId;
	}

	@Override
	public void setGroupId(long groupId) {
		_columnBitmask |= GROUPID_COLUMN_BITMASK;

		if (!_setOriginalGroupId) {
			_setOriginalGroupId = true;

			_originalGroupId = _groupId;
		}

		_groupId = groupId;
	}

	public long getOriginalGroupId() {
		return _originalGroupId;
	}

	@JSON
	@Override
	public long getCompanyId() {
		return _companyId;
	}

	@Override
	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	@JSON
	@Override
	public long getUserId() {
		return _userId;
	}

	@Override
	public void setUserId(long userId) {
		_userId = userId;
	}

	@Override
	public String getUserUuid() {
		try {
			User user = UserLocalServiceUtil.getUserById(getUserId());

			return user.getUuid();
		}
		catch (PortalException pe) {
			return StringPool.BLANK;
		}
	}

	@Override
	public void setUserUuid(String userUuid) {
	}

	@JSON
	@Override
	public String getUserName() {
		if (_userName == null) {
			return StringPool.BLANK;
		}
		else {
			return _userName;
		}
	}

	@Override
	public void setUserName(String userName) {
		_userName = userName;
	}

	@JSON
	@Override
	public Date getCreateDate() {
		return _createDate;
	}

	@Override
	public void setCreateDate(Date createDate) {
		_createDate = createDate;
	}

	@JSON
	@Override
	public Date getModifiedDate() {
		return _modifiedDate;
	}

	public boolean hasSetModifiedDate() {
		return _setModifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		_setModifiedDate = true;

		_modifiedDate = modifiedDate;
	}

	@JSON
	@Override
	public String getName() {
		if (_name == null) {
			return StringPool.BLANK;
		}
		else {
			return _name;
		}
	}

	@Override
	public void setName(String name) {
		_columnBitmask = -1L;

		_name = name;
	}

	@JSON
	@Override
	public String getDescription() {
		if (_description == null) {
			return StringPool.BLANK;
		}
		else {
			return _description;
		}
	}

	@Override
	public void setDescription(String description) {
		_description = description;
	}

	@JSON
	@Override
	public String getStreet1() {
		if (_street1 == null) {
			return StringPool.BLANK;
		}
		else {
			return _street1;
		}
	}

	@Override
	public void setStreet1(String street1) {
		_street1 = street1;
	}

	@JSON
	@Override
	public String getStreet2() {
		if (_street2 == null) {
			return StringPool.BLANK;
		}
		else {
			return _street2;
		}
	}

	@Override
	public void setStreet2(String street2) {
		_street2 = street2;
	}

	@JSON
	@Override
	public String getStreet3() {
		if (_street3 == null) {
			return StringPool.BLANK;
		}
		else {
			return _street3;
		}
	}

	@Override
	public void setStreet3(String street3) {
		_street3 = street3;
	}

	@JSON
	@Override
	public String getCity() {
		if (_city == null) {
			return StringPool.BLANK;
		}
		else {
			return _city;
		}
	}

	@Override
	public void setCity(String city) {
		_city = city;
	}

	@JSON
	@Override
	public String getZip() {
		if (_zip == null) {
			return StringPool.BLANK;
		}
		else {
			return _zip;
		}
	}

	@Override
	public void setZip(String zip) {
		_zip = zip;
	}

	@JSON
	@Override
	public long getCommerceRegionId() {
		return _commerceRegionId;
	}

	@Override
	public void setCommerceRegionId(long commerceRegionId) {
		_commerceRegionId = commerceRegionId;
	}

	@JSON
	@Override
	public long getCommerceCountryId() {
		return _commerceCountryId;
	}

	@Override
	public void setCommerceCountryId(long commerceCountryId) {
		_columnBitmask |= COMMERCECOUNTRYID_COLUMN_BITMASK;

		if (!_setOriginalCommerceCountryId) {
			_setOriginalCommerceCountryId = true;

			_originalCommerceCountryId = _commerceCountryId;
		}

		_commerceCountryId = commerceCountryId;
	}

	public long getOriginalCommerceCountryId() {
		return _originalCommerceCountryId;
	}

	public long getColumnBitmask() {
		return _columnBitmask;
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		return ExpandoBridgeFactoryUtil.getExpandoBridge(getCompanyId(),
			CommerceWarehouse.class.getName(), getPrimaryKey());
	}

	@Override
	public void setExpandoBridgeAttributes(ServiceContext serviceContext) {
		ExpandoBridge expandoBridge = getExpandoBridge();

		expandoBridge.setAttributes(serviceContext);
	}

	@Override
	public CommerceWarehouse toEscapedModel() {
		if (_escapedModel == null) {
			_escapedModel = (CommerceWarehouse)ProxyUtil.newProxyInstance(_classLoader,
					_escapedModelInterfaces, new AutoEscapeBeanHandler(this));
		}

		return _escapedModel;
	}

	@Override
	public Object clone() {
		CommerceWarehouseImpl commerceWarehouseImpl = new CommerceWarehouseImpl();

		commerceWarehouseImpl.setCommerceWarehouseId(getCommerceWarehouseId());
		commerceWarehouseImpl.setGroupId(getGroupId());
		commerceWarehouseImpl.setCompanyId(getCompanyId());
		commerceWarehouseImpl.setUserId(getUserId());
		commerceWarehouseImpl.setUserName(getUserName());
		commerceWarehouseImpl.setCreateDate(getCreateDate());
		commerceWarehouseImpl.setModifiedDate(getModifiedDate());
		commerceWarehouseImpl.setName(getName());
		commerceWarehouseImpl.setDescription(getDescription());
		commerceWarehouseImpl.setStreet1(getStreet1());
		commerceWarehouseImpl.setStreet2(getStreet2());
		commerceWarehouseImpl.setStreet3(getStreet3());
		commerceWarehouseImpl.setCity(getCity());
		commerceWarehouseImpl.setZip(getZip());
		commerceWarehouseImpl.setCommerceRegionId(getCommerceRegionId());
		commerceWarehouseImpl.setCommerceCountryId(getCommerceCountryId());

		commerceWarehouseImpl.resetOriginalValues();

		return commerceWarehouseImpl;
	}

	@Override
	public int compareTo(CommerceWarehouse commerceWarehouse) {
		int value = 0;

		value = getName().compareTo(commerceWarehouse.getName());

		if (value != 0) {
			return value;
		}

		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof CommerceWarehouse)) {
			return false;
		}

		CommerceWarehouse commerceWarehouse = (CommerceWarehouse)obj;

		long primaryKey = commerceWarehouse.getPrimaryKey();

		if (getPrimaryKey() == primaryKey) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return (int)getPrimaryKey();
	}

	@Override
	public boolean isEntityCacheEnabled() {
		return ENTITY_CACHE_ENABLED;
	}

	@Override
	public boolean isFinderCacheEnabled() {
		return FINDER_CACHE_ENABLED;
	}

	@Override
	public void resetOriginalValues() {
		CommerceWarehouseModelImpl commerceWarehouseModelImpl = this;

		commerceWarehouseModelImpl._originalGroupId = commerceWarehouseModelImpl._groupId;

		commerceWarehouseModelImpl._setOriginalGroupId = false;

		commerceWarehouseModelImpl._setModifiedDate = false;

		commerceWarehouseModelImpl._originalCommerceCountryId = commerceWarehouseModelImpl._commerceCountryId;

		commerceWarehouseModelImpl._setOriginalCommerceCountryId = false;

		commerceWarehouseModelImpl._columnBitmask = 0;
	}

	@Override
	public CacheModel<CommerceWarehouse> toCacheModel() {
		CommerceWarehouseCacheModel commerceWarehouseCacheModel = new CommerceWarehouseCacheModel();

		commerceWarehouseCacheModel.commerceWarehouseId = getCommerceWarehouseId();

		commerceWarehouseCacheModel.groupId = getGroupId();

		commerceWarehouseCacheModel.companyId = getCompanyId();

		commerceWarehouseCacheModel.userId = getUserId();

		commerceWarehouseCacheModel.userName = getUserName();

		String userName = commerceWarehouseCacheModel.userName;

		if ((userName != null) && (userName.length() == 0)) {
			commerceWarehouseCacheModel.userName = null;
		}

		Date createDate = getCreateDate();

		if (createDate != null) {
			commerceWarehouseCacheModel.createDate = createDate.getTime();
		}
		else {
			commerceWarehouseCacheModel.createDate = Long.MIN_VALUE;
		}

		Date modifiedDate = getModifiedDate();

		if (modifiedDate != null) {
			commerceWarehouseCacheModel.modifiedDate = modifiedDate.getTime();
		}
		else {
			commerceWarehouseCacheModel.modifiedDate = Long.MIN_VALUE;
		}

		commerceWarehouseCacheModel.name = getName();

		String name = commerceWarehouseCacheModel.name;

		if ((name != null) && (name.length() == 0)) {
			commerceWarehouseCacheModel.name = null;
		}

		commerceWarehouseCacheModel.description = getDescription();

		String description = commerceWarehouseCacheModel.description;

		if ((description != null) && (description.length() == 0)) {
			commerceWarehouseCacheModel.description = null;
		}

		commerceWarehouseCacheModel.street1 = getStreet1();

		String street1 = commerceWarehouseCacheModel.street1;

		if ((street1 != null) && (street1.length() == 0)) {
			commerceWarehouseCacheModel.street1 = null;
		}

		commerceWarehouseCacheModel.street2 = getStreet2();

		String street2 = commerceWarehouseCacheModel.street2;

		if ((street2 != null) && (street2.length() == 0)) {
			commerceWarehouseCacheModel.street2 = null;
		}

		commerceWarehouseCacheModel.street3 = getStreet3();

		String street3 = commerceWarehouseCacheModel.street3;

		if ((street3 != null) && (street3.length() == 0)) {
			commerceWarehouseCacheModel.street3 = null;
		}

		commerceWarehouseCacheModel.city = getCity();

		String city = commerceWarehouseCacheModel.city;

		if ((city != null) && (city.length() == 0)) {
			commerceWarehouseCacheModel.city = null;
		}

		commerceWarehouseCacheModel.zip = getZip();

		String zip = commerceWarehouseCacheModel.zip;

		if ((zip != null) && (zip.length() == 0)) {
			commerceWarehouseCacheModel.zip = null;
		}

		commerceWarehouseCacheModel.commerceRegionId = getCommerceRegionId();

		commerceWarehouseCacheModel.commerceCountryId = getCommerceCountryId();

		return commerceWarehouseCacheModel;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(33);

		sb.append("{commerceWarehouseId=");
		sb.append(getCommerceWarehouseId());
		sb.append(", groupId=");
		sb.append(getGroupId());
		sb.append(", companyId=");
		sb.append(getCompanyId());
		sb.append(", userId=");
		sb.append(getUserId());
		sb.append(", userName=");
		sb.append(getUserName());
		sb.append(", createDate=");
		sb.append(getCreateDate());
		sb.append(", modifiedDate=");
		sb.append(getModifiedDate());
		sb.append(", name=");
		sb.append(getName());
		sb.append(", description=");
		sb.append(getDescription());
		sb.append(", street1=");
		sb.append(getStreet1());
		sb.append(", street2=");
		sb.append(getStreet2());
		sb.append(", street3=");
		sb.append(getStreet3());
		sb.append(", city=");
		sb.append(getCity());
		sb.append(", zip=");
		sb.append(getZip());
		sb.append(", commerceRegionId=");
		sb.append(getCommerceRegionId());
		sb.append(", commerceCountryId=");
		sb.append(getCommerceCountryId());
		sb.append("}");

		return sb.toString();
	}

	@Override
	public String toXmlString() {
		StringBundler sb = new StringBundler(52);

		sb.append("<model><model-name>");
		sb.append("com.liferay.commerce.model.CommerceWarehouse");
		sb.append("</model-name>");

		sb.append(
			"<column><column-name>commerceWarehouseId</column-name><column-value><![CDATA[");
		sb.append(getCommerceWarehouseId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>groupId</column-name><column-value><![CDATA[");
		sb.append(getGroupId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>companyId</column-name><column-value><![CDATA[");
		sb.append(getCompanyId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>userId</column-name><column-value><![CDATA[");
		sb.append(getUserId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>userName</column-name><column-value><![CDATA[");
		sb.append(getUserName());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>createDate</column-name><column-value><![CDATA[");
		sb.append(getCreateDate());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>modifiedDate</column-name><column-value><![CDATA[");
		sb.append(getModifiedDate());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>name</column-name><column-value><![CDATA[");
		sb.append(getName());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>description</column-name><column-value><![CDATA[");
		sb.append(getDescription());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>street1</column-name><column-value><![CDATA[");
		sb.append(getStreet1());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>street2</column-name><column-value><![CDATA[");
		sb.append(getStreet2());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>street3</column-name><column-value><![CDATA[");
		sb.append(getStreet3());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>city</column-name><column-value><![CDATA[");
		sb.append(getCity());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>zip</column-name><column-value><![CDATA[");
		sb.append(getZip());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>commerceRegionId</column-name><column-value><![CDATA[");
		sb.append(getCommerceRegionId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>commerceCountryId</column-name><column-value><![CDATA[");
		sb.append(getCommerceCountryId());
		sb.append("]]></column-value></column>");

		sb.append("</model>");

		return sb.toString();
	}

	private static final ClassLoader _classLoader = CommerceWarehouse.class.getClassLoader();
	private static final Class<?>[] _escapedModelInterfaces = new Class[] {
			CommerceWarehouse.class
		};
	private long _commerceWarehouseId;
	private long _groupId;
	private long _originalGroupId;
	private boolean _setOriginalGroupId;
	private long _companyId;
	private long _userId;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private boolean _setModifiedDate;
	private String _name;
	private String _description;
	private String _street1;
	private String _street2;
	private String _street3;
	private String _city;
	private String _zip;
	private long _commerceRegionId;
	private long _commerceCountryId;
	private long _originalCommerceCountryId;
	private boolean _setOriginalCommerceCountryId;
	private long _columnBitmask;
	private CommerceWarehouse _escapedModel;
}