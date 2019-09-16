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

package com.liferay.segments.model.impl;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.util.ExpandoBridgeFactoryUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.bean.AutoEscapeBeanHandler;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSON;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.impl.BaseModelImpl;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsEntryModel;
import com.liferay.segments.model.SegmentsEntrySoap;

import java.io.Serializable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;

import java.sql.Types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * The base model implementation for the SegmentsEntry service. Represents a row in the &quot;SegmentsEntry&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This implementation and its corresponding interface </code>SegmentsEntryModel</code> exist only as a container for the default property accessors generated by ServiceBuilder. Helper methods and all application logic should be put in {@link SegmentsEntryImpl}.
 * </p>
 *
 * @author Eduardo Garcia
 * @see SegmentsEntryImpl
 * @generated
 */
@JSON(strict = true)
public class SegmentsEntryModelImpl
	extends BaseModelImpl<SegmentsEntry> implements SegmentsEntryModel {

	/**
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. All methods that expect a segments entry model instance should use the <code>SegmentsEntry</code> interface instead.
	 */
	public static final String TABLE_NAME = "SegmentsEntry";

	public static final Object[][] TABLE_COLUMNS = {
		{"mvccVersion", Types.BIGINT}, {"uuid_", Types.VARCHAR},
		{"segmentsEntryId", Types.BIGINT}, {"groupId", Types.BIGINT},
		{"companyId", Types.BIGINT}, {"userId", Types.BIGINT},
		{"userName", Types.VARCHAR}, {"createDate", Types.TIMESTAMP},
		{"modifiedDate", Types.TIMESTAMP}, {"segmentsEntryKey", Types.VARCHAR},
		{"name", Types.VARCHAR}, {"description", Types.VARCHAR},
		{"active_", Types.BOOLEAN}, {"criteria", Types.CLOB},
		{"source", Types.VARCHAR}, {"type_", Types.VARCHAR},
		{"lastPublishDate", Types.TIMESTAMP}
	};

	public static final Map<String, Integer> TABLE_COLUMNS_MAP =
		new HashMap<String, Integer>();

	static {
		TABLE_COLUMNS_MAP.put("mvccVersion", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("uuid_", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("segmentsEntryId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("groupId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("companyId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("userId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("userName", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("createDate", Types.TIMESTAMP);
		TABLE_COLUMNS_MAP.put("modifiedDate", Types.TIMESTAMP);
		TABLE_COLUMNS_MAP.put("segmentsEntryKey", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("name", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("description", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("active_", Types.BOOLEAN);
		TABLE_COLUMNS_MAP.put("criteria", Types.CLOB);
		TABLE_COLUMNS_MAP.put("source", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("type_", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("lastPublishDate", Types.TIMESTAMP);
	}

	public static final String TABLE_SQL_CREATE =
		"create table SegmentsEntry (mvccVersion LONG default 0 not null,uuid_ VARCHAR(75) null,segmentsEntryId LONG not null primary key,groupId LONG,companyId LONG,userId LONG,userName VARCHAR(75) null,createDate DATE null,modifiedDate DATE null,segmentsEntryKey VARCHAR(75) null,name STRING null,description STRING null,active_ BOOLEAN,criteria TEXT null,source VARCHAR(75) null,type_ VARCHAR(75) null,lastPublishDate DATE null)";

	public static final String TABLE_SQL_DROP = "drop table SegmentsEntry";

	public static final String ORDER_BY_JPQL =
		" ORDER BY segmentsEntry.modifiedDate DESC";

	public static final String ORDER_BY_SQL =
		" ORDER BY SegmentsEntry.modifiedDate DESC";

	public static final String DATA_SOURCE = "liferayDataSource";

	public static final String SESSION_FACTORY = "liferaySessionFactory";

	public static final String TX_MANAGER = "liferayTransactionManager";

	public static final long ACTIVE_COLUMN_BITMASK = 1L;

	public static final long COMPANYID_COLUMN_BITMASK = 2L;

	public static final long GROUPID_COLUMN_BITMASK = 4L;

	public static final long SEGMENTSENTRYKEY_COLUMN_BITMASK = 8L;

	public static final long SOURCE_COLUMN_BITMASK = 16L;

	public static final long TYPE_COLUMN_BITMASK = 32L;

	public static final long UUID_COLUMN_BITMASK = 64L;

	public static final long MODIFIEDDATE_COLUMN_BITMASK = 128L;

	public static void setEntityCacheEnabled(boolean entityCacheEnabled) {
		_entityCacheEnabled = entityCacheEnabled;
	}

	public static void setFinderCacheEnabled(boolean finderCacheEnabled) {
		_finderCacheEnabled = finderCacheEnabled;
	}

	/**
	 * Converts the soap model instance into a normal model instance.
	 *
	 * @param soapModel the soap model instance to convert
	 * @return the normal model instance
	 */
	public static SegmentsEntry toModel(SegmentsEntrySoap soapModel) {
		if (soapModel == null) {
			return null;
		}

		SegmentsEntry model = new SegmentsEntryImpl();

		model.setMvccVersion(soapModel.getMvccVersion());
		model.setUuid(soapModel.getUuid());
		model.setSegmentsEntryId(soapModel.getSegmentsEntryId());
		model.setGroupId(soapModel.getGroupId());
		model.setCompanyId(soapModel.getCompanyId());
		model.setUserId(soapModel.getUserId());
		model.setUserName(soapModel.getUserName());
		model.setCreateDate(soapModel.getCreateDate());
		model.setModifiedDate(soapModel.getModifiedDate());
		model.setSegmentsEntryKey(soapModel.getSegmentsEntryKey());
		model.setName(soapModel.getName());
		model.setDescription(soapModel.getDescription());
		model.setActive(soapModel.isActive());
		model.setCriteria(soapModel.getCriteria());
		model.setSource(soapModel.getSource());
		model.setType(soapModel.getType());
		model.setLastPublishDate(soapModel.getLastPublishDate());

		return model;
	}

	/**
	 * Converts the soap model instances into normal model instances.
	 *
	 * @param soapModels the soap model instances to convert
	 * @return the normal model instances
	 */
	public static List<SegmentsEntry> toModels(SegmentsEntrySoap[] soapModels) {
		if (soapModels == null) {
			return null;
		}

		List<SegmentsEntry> models = new ArrayList<SegmentsEntry>(
			soapModels.length);

		for (SegmentsEntrySoap soapModel : soapModels) {
			models.add(toModel(soapModel));
		}

		return models;
	}

	public SegmentsEntryModelImpl() {
	}

	@Override
	public long getPrimaryKey() {
		return _segmentsEntryId;
	}

	@Override
	public void setPrimaryKey(long primaryKey) {
		setSegmentsEntryId(primaryKey);
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _segmentsEntryId;
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		setPrimaryKey(((Long)primaryKeyObj).longValue());
	}

	@Override
	public Class<?> getModelClass() {
		return SegmentsEntry.class;
	}

	@Override
	public String getModelClassName() {
		return SegmentsEntry.class.getName();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		Map<String, Function<SegmentsEntry, Object>> attributeGetterFunctions =
			getAttributeGetterFunctions();

		for (Map.Entry<String, Function<SegmentsEntry, Object>> entry :
				attributeGetterFunctions.entrySet()) {

			String attributeName = entry.getKey();
			Function<SegmentsEntry, Object> attributeGetterFunction =
				entry.getValue();

			attributes.put(
				attributeName,
				attributeGetterFunction.apply((SegmentsEntry)this));
		}

		attributes.put("entityCacheEnabled", isEntityCacheEnabled());
		attributes.put("finderCacheEnabled", isFinderCacheEnabled());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Map<String, BiConsumer<SegmentsEntry, Object>>
			attributeSetterBiConsumers = getAttributeSetterBiConsumers();

		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			String attributeName = entry.getKey();

			BiConsumer<SegmentsEntry, Object> attributeSetterBiConsumer =
				attributeSetterBiConsumers.get(attributeName);

			if (attributeSetterBiConsumer != null) {
				attributeSetterBiConsumer.accept(
					(SegmentsEntry)this, entry.getValue());
			}
		}
	}

	public Map<String, Function<SegmentsEntry, Object>>
		getAttributeGetterFunctions() {

		return _attributeGetterFunctions;
	}

	public Map<String, BiConsumer<SegmentsEntry, Object>>
		getAttributeSetterBiConsumers() {

		return _attributeSetterBiConsumers;
	}

	private static Function<InvocationHandler, SegmentsEntry>
		_getProxyProviderFunction() {

		Class<?> proxyClass = ProxyUtil.getProxyClass(
			SegmentsEntry.class.getClassLoader(), SegmentsEntry.class,
			ModelWrapper.class);

		try {
			Constructor<SegmentsEntry> constructor =
				(Constructor<SegmentsEntry>)proxyClass.getConstructor(
					InvocationHandler.class);

			return invocationHandler -> {
				try {
					return constructor.newInstance(invocationHandler);
				}
				catch (ReflectiveOperationException roe) {
					throw new InternalError(roe);
				}
			};
		}
		catch (NoSuchMethodException nsme) {
			throw new InternalError(nsme);
		}
	}

	private static final Map<String, Function<SegmentsEntry, Object>>
		_attributeGetterFunctions;
	private static final Map<String, BiConsumer<SegmentsEntry, Object>>
		_attributeSetterBiConsumers;

	static {
		Map<String, Function<SegmentsEntry, Object>> attributeGetterFunctions =
			new LinkedHashMap<String, Function<SegmentsEntry, Object>>();
		Map<String, BiConsumer<SegmentsEntry, ?>> attributeSetterBiConsumers =
			new LinkedHashMap<String, BiConsumer<SegmentsEntry, ?>>();

		attributeGetterFunctions.put(
			"mvccVersion", SegmentsEntry::getMvccVersion);
		attributeSetterBiConsumers.put(
			"mvccVersion",
			(BiConsumer<SegmentsEntry, Long>)SegmentsEntry::setMvccVersion);
		attributeGetterFunctions.put("uuid", SegmentsEntry::getUuid);
		attributeSetterBiConsumers.put(
			"uuid", (BiConsumer<SegmentsEntry, String>)SegmentsEntry::setUuid);
		attributeGetterFunctions.put(
			"segmentsEntryId", SegmentsEntry::getSegmentsEntryId);
		attributeSetterBiConsumers.put(
			"segmentsEntryId",
			(BiConsumer<SegmentsEntry, Long>)SegmentsEntry::setSegmentsEntryId);
		attributeGetterFunctions.put("groupId", SegmentsEntry::getGroupId);
		attributeSetterBiConsumers.put(
			"groupId",
			(BiConsumer<SegmentsEntry, Long>)SegmentsEntry::setGroupId);
		attributeGetterFunctions.put("companyId", SegmentsEntry::getCompanyId);
		attributeSetterBiConsumers.put(
			"companyId",
			(BiConsumer<SegmentsEntry, Long>)SegmentsEntry::setCompanyId);
		attributeGetterFunctions.put("userId", SegmentsEntry::getUserId);
		attributeSetterBiConsumers.put(
			"userId",
			(BiConsumer<SegmentsEntry, Long>)SegmentsEntry::setUserId);
		attributeGetterFunctions.put("userName", SegmentsEntry::getUserName);
		attributeSetterBiConsumers.put(
			"userName",
			(BiConsumer<SegmentsEntry, String>)SegmentsEntry::setUserName);
		attributeGetterFunctions.put(
			"createDate", SegmentsEntry::getCreateDate);
		attributeSetterBiConsumers.put(
			"createDate",
			(BiConsumer<SegmentsEntry, Date>)SegmentsEntry::setCreateDate);
		attributeGetterFunctions.put(
			"modifiedDate", SegmentsEntry::getModifiedDate);
		attributeSetterBiConsumers.put(
			"modifiedDate",
			(BiConsumer<SegmentsEntry, Date>)SegmentsEntry::setModifiedDate);
		attributeGetterFunctions.put(
			"segmentsEntryKey", SegmentsEntry::getSegmentsEntryKey);
		attributeSetterBiConsumers.put(
			"segmentsEntryKey",
			(BiConsumer<SegmentsEntry, String>)
				SegmentsEntry::setSegmentsEntryKey);
		attributeGetterFunctions.put("name", SegmentsEntry::getName);
		attributeSetterBiConsumers.put(
			"name", (BiConsumer<SegmentsEntry, String>)SegmentsEntry::setName);
		attributeGetterFunctions.put(
			"description", SegmentsEntry::getDescription);
		attributeSetterBiConsumers.put(
			"description",
			(BiConsumer<SegmentsEntry, String>)SegmentsEntry::setDescription);
		attributeGetterFunctions.put("active", SegmentsEntry::getActive);
		attributeSetterBiConsumers.put(
			"active",
			(BiConsumer<SegmentsEntry, Boolean>)SegmentsEntry::setActive);
		attributeGetterFunctions.put("criteria", SegmentsEntry::getCriteria);
		attributeSetterBiConsumers.put(
			"criteria",
			(BiConsumer<SegmentsEntry, String>)SegmentsEntry::setCriteria);
		attributeGetterFunctions.put("source", SegmentsEntry::getSource);
		attributeSetterBiConsumers.put(
			"source",
			(BiConsumer<SegmentsEntry, String>)SegmentsEntry::setSource);
		attributeGetterFunctions.put("type", SegmentsEntry::getType);
		attributeSetterBiConsumers.put(
			"type", (BiConsumer<SegmentsEntry, String>)SegmentsEntry::setType);
		attributeGetterFunctions.put(
			"lastPublishDate", SegmentsEntry::getLastPublishDate);
		attributeSetterBiConsumers.put(
			"lastPublishDate",
			(BiConsumer<SegmentsEntry, Date>)SegmentsEntry::setLastPublishDate);

		_attributeGetterFunctions = Collections.unmodifiableMap(
			attributeGetterFunctions);
		_attributeSetterBiConsumers = Collections.unmodifiableMap(
			(Map)attributeSetterBiConsumers);
	}

	@JSON
	@Override
	public long getMvccVersion() {
		return _mvccVersion;
	}

	@Override
	public void setMvccVersion(long mvccVersion) {
		_mvccVersion = mvccVersion;
	}

	@JSON
	@Override
	public String getUuid() {
		if (_uuid == null) {
			return "";
		}
		else {
			return _uuid;
		}
	}

	@Override
	public void setUuid(String uuid) {
		_columnBitmask |= UUID_COLUMN_BITMASK;

		if (_originalUuid == null) {
			_originalUuid = _uuid;
		}

		_uuid = uuid;
	}

	public String getOriginalUuid() {
		return GetterUtil.getString(_originalUuid);
	}

	@JSON
	@Override
	public long getSegmentsEntryId() {
		return _segmentsEntryId;
	}

	@Override
	public void setSegmentsEntryId(long segmentsEntryId) {
		_segmentsEntryId = segmentsEntryId;
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
		_columnBitmask |= COMPANYID_COLUMN_BITMASK;

		if (!_setOriginalCompanyId) {
			_setOriginalCompanyId = true;

			_originalCompanyId = _companyId;
		}

		_companyId = companyId;
	}

	public long getOriginalCompanyId() {
		return _originalCompanyId;
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
			return "";
		}
	}

	@Override
	public void setUserUuid(String userUuid) {
	}

	@JSON
	@Override
	public String getUserName() {
		if (_userName == null) {
			return "";
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

		_columnBitmask = -1L;

		_modifiedDate = modifiedDate;
	}

	@JSON
	@Override
	public String getSegmentsEntryKey() {
		if (_segmentsEntryKey == null) {
			return "";
		}
		else {
			return _segmentsEntryKey;
		}
	}

	@Override
	public void setSegmentsEntryKey(String segmentsEntryKey) {
		_columnBitmask |= SEGMENTSENTRYKEY_COLUMN_BITMASK;

		if (_originalSegmentsEntryKey == null) {
			_originalSegmentsEntryKey = _segmentsEntryKey;
		}

		_segmentsEntryKey = segmentsEntryKey;
	}

	public String getOriginalSegmentsEntryKey() {
		return GetterUtil.getString(_originalSegmentsEntryKey);
	}

	@JSON
	@Override
	public String getName() {
		if (_name == null) {
			return "";
		}
		else {
			return _name;
		}
	}

	@Override
	public String getName(Locale locale) {
		String languageId = LocaleUtil.toLanguageId(locale);

		return getName(languageId);
	}

	@Override
	public String getName(Locale locale, boolean useDefault) {
		String languageId = LocaleUtil.toLanguageId(locale);

		return getName(languageId, useDefault);
	}

	@Override
	public String getName(String languageId) {
		return LocalizationUtil.getLocalization(getName(), languageId);
	}

	@Override
	public String getName(String languageId, boolean useDefault) {
		return LocalizationUtil.getLocalization(
			getName(), languageId, useDefault);
	}

	@Override
	public String getNameCurrentLanguageId() {
		return _nameCurrentLanguageId;
	}

	@JSON
	@Override
	public String getNameCurrentValue() {
		Locale locale = getLocale(_nameCurrentLanguageId);

		return getName(locale);
	}

	@Override
	public Map<Locale, String> getNameMap() {
		return LocalizationUtil.getLocalizationMap(getName());
	}

	@Override
	public void setName(String name) {
		_name = name;
	}

	@Override
	public void setName(String name, Locale locale) {
		setName(name, locale, LocaleUtil.getSiteDefault());
	}

	@Override
	public void setName(String name, Locale locale, Locale defaultLocale) {
		String languageId = LocaleUtil.toLanguageId(locale);
		String defaultLanguageId = LocaleUtil.toLanguageId(defaultLocale);

		if (Validator.isNotNull(name)) {
			setName(
				LocalizationUtil.updateLocalization(
					getName(), "Name", name, languageId, defaultLanguageId));
		}
		else {
			setName(
				LocalizationUtil.removeLocalization(
					getName(), "Name", languageId));
		}
	}

	@Override
	public void setNameCurrentLanguageId(String languageId) {
		_nameCurrentLanguageId = languageId;
	}

	@Override
	public void setNameMap(Map<Locale, String> nameMap) {
		setNameMap(nameMap, LocaleUtil.getSiteDefault());
	}

	@Override
	public void setNameMap(Map<Locale, String> nameMap, Locale defaultLocale) {
		if (nameMap == null) {
			return;
		}

		setName(
			LocalizationUtil.updateLocalization(
				nameMap, getName(), "Name",
				LocaleUtil.toLanguageId(defaultLocale)));
	}

	@JSON
	@Override
	public String getDescription() {
		if (_description == null) {
			return "";
		}
		else {
			return _description;
		}
	}

	@Override
	public String getDescription(Locale locale) {
		String languageId = LocaleUtil.toLanguageId(locale);

		return getDescription(languageId);
	}

	@Override
	public String getDescription(Locale locale, boolean useDefault) {
		String languageId = LocaleUtil.toLanguageId(locale);

		return getDescription(languageId, useDefault);
	}

	@Override
	public String getDescription(String languageId) {
		return LocalizationUtil.getLocalization(getDescription(), languageId);
	}

	@Override
	public String getDescription(String languageId, boolean useDefault) {
		return LocalizationUtil.getLocalization(
			getDescription(), languageId, useDefault);
	}

	@Override
	public String getDescriptionCurrentLanguageId() {
		return _descriptionCurrentLanguageId;
	}

	@JSON
	@Override
	public String getDescriptionCurrentValue() {
		Locale locale = getLocale(_descriptionCurrentLanguageId);

		return getDescription(locale);
	}

	@Override
	public Map<Locale, String> getDescriptionMap() {
		return LocalizationUtil.getLocalizationMap(getDescription());
	}

	@Override
	public void setDescription(String description) {
		_description = description;
	}

	@Override
	public void setDescription(String description, Locale locale) {
		setDescription(description, locale, LocaleUtil.getSiteDefault());
	}

	@Override
	public void setDescription(
		String description, Locale locale, Locale defaultLocale) {

		String languageId = LocaleUtil.toLanguageId(locale);
		String defaultLanguageId = LocaleUtil.toLanguageId(defaultLocale);

		if (Validator.isNotNull(description)) {
			setDescription(
				LocalizationUtil.updateLocalization(
					getDescription(), "Description", description, languageId,
					defaultLanguageId));
		}
		else {
			setDescription(
				LocalizationUtil.removeLocalization(
					getDescription(), "Description", languageId));
		}
	}

	@Override
	public void setDescriptionCurrentLanguageId(String languageId) {
		_descriptionCurrentLanguageId = languageId;
	}

	@Override
	public void setDescriptionMap(Map<Locale, String> descriptionMap) {
		setDescriptionMap(descriptionMap, LocaleUtil.getSiteDefault());
	}

	@Override
	public void setDescriptionMap(
		Map<Locale, String> descriptionMap, Locale defaultLocale) {

		if (descriptionMap == null) {
			return;
		}

		setDescription(
			LocalizationUtil.updateLocalization(
				descriptionMap, getDescription(), "Description",
				LocaleUtil.toLanguageId(defaultLocale)));
	}

	@JSON
	@Override
	public boolean getActive() {
		return _active;
	}

	@JSON
	@Override
	public boolean isActive() {
		return _active;
	}

	@Override
	public void setActive(boolean active) {
		_columnBitmask |= ACTIVE_COLUMN_BITMASK;

		if (!_setOriginalActive) {
			_setOriginalActive = true;

			_originalActive = _active;
		}

		_active = active;
	}

	public boolean getOriginalActive() {
		return _originalActive;
	}

	@JSON
	@Override
	public String getCriteria() {
		if (_criteria == null) {
			return "";
		}
		else {
			return _criteria;
		}
	}

	@Override
	public void setCriteria(String criteria) {
		_criteria = criteria;
	}

	@JSON
	@Override
	public String getSource() {
		if (_source == null) {
			return "";
		}
		else {
			return _source;
		}
	}

	@Override
	public void setSource(String source) {
		_columnBitmask |= SOURCE_COLUMN_BITMASK;

		if (_originalSource == null) {
			_originalSource = _source;
		}

		_source = source;
	}

	public String getOriginalSource() {
		return GetterUtil.getString(_originalSource);
	}

	@JSON
	@Override
	public String getType() {
		if (_type == null) {
			return "";
		}
		else {
			return _type;
		}
	}

	@Override
	public void setType(String type) {
		_columnBitmask |= TYPE_COLUMN_BITMASK;

		if (_originalType == null) {
			_originalType = _type;
		}

		_type = type;
	}

	public String getOriginalType() {
		return GetterUtil.getString(_originalType);
	}

	@JSON
	@Override
	public Date getLastPublishDate() {
		return _lastPublishDate;
	}

	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		_lastPublishDate = lastPublishDate;
	}

	@Override
	public StagedModelType getStagedModelType() {
		return new StagedModelType(
			PortalUtil.getClassNameId(SegmentsEntry.class.getName()));
	}

	public long getColumnBitmask() {
		return _columnBitmask;
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		return ExpandoBridgeFactoryUtil.getExpandoBridge(
			getCompanyId(), SegmentsEntry.class.getName(), getPrimaryKey());
	}

	@Override
	public void setExpandoBridgeAttributes(ServiceContext serviceContext) {
		ExpandoBridge expandoBridge = getExpandoBridge();

		expandoBridge.setAttributes(serviceContext);
	}

	@Override
	public String[] getAvailableLanguageIds() {
		Set<String> availableLanguageIds = new TreeSet<String>();

		Map<Locale, String> nameMap = getNameMap();

		for (Map.Entry<Locale, String> entry : nameMap.entrySet()) {
			Locale locale = entry.getKey();
			String value = entry.getValue();

			if (Validator.isNotNull(value)) {
				availableLanguageIds.add(LocaleUtil.toLanguageId(locale));
			}
		}

		Map<Locale, String> descriptionMap = getDescriptionMap();

		for (Map.Entry<Locale, String> entry : descriptionMap.entrySet()) {
			Locale locale = entry.getKey();
			String value = entry.getValue();

			if (Validator.isNotNull(value)) {
				availableLanguageIds.add(LocaleUtil.toLanguageId(locale));
			}
		}

		return availableLanguageIds.toArray(
			new String[availableLanguageIds.size()]);
	}

	@Override
	public String getDefaultLanguageId() {
		String xml = getName();

		if (xml == null) {
			return "";
		}

		Locale defaultLocale = LocaleUtil.getSiteDefault();

		return LocalizationUtil.getDefaultLanguageId(xml, defaultLocale);
	}

	@Override
	public void prepareLocalizedFieldsForImport() throws LocaleException {
		Locale defaultLocale = LocaleUtil.fromLanguageId(
			getDefaultLanguageId());

		Locale[] availableLocales = LocaleUtil.fromLanguageIds(
			getAvailableLanguageIds());

		Locale defaultImportLocale = LocalizationUtil.getDefaultImportLocale(
			SegmentsEntry.class.getName(), getPrimaryKey(), defaultLocale,
			availableLocales);

		prepareLocalizedFieldsForImport(defaultImportLocale);
	}

	@Override
	@SuppressWarnings("unused")
	public void prepareLocalizedFieldsForImport(Locale defaultImportLocale)
		throws LocaleException {

		Locale defaultLocale = LocaleUtil.getSiteDefault();

		String modelDefaultLanguageId = getDefaultLanguageId();

		String name = getName(defaultLocale);

		if (Validator.isNull(name)) {
			setName(getName(modelDefaultLanguageId), defaultLocale);
		}
		else {
			setName(getName(defaultLocale), defaultLocale, defaultLocale);
		}

		String description = getDescription(defaultLocale);

		if (Validator.isNull(description)) {
			setDescription(
				getDescription(modelDefaultLanguageId), defaultLocale);
		}
		else {
			setDescription(
				getDescription(defaultLocale), defaultLocale, defaultLocale);
		}
	}

	@Override
	public SegmentsEntry toEscapedModel() {
		if (_escapedModel == null) {
			Function<InvocationHandler, SegmentsEntry>
				escapedModelProxyProviderFunction =
					EscapedModelProxyProviderFunctionHolder.
						_escapedModelProxyProviderFunction;

			_escapedModel = escapedModelProxyProviderFunction.apply(
				new AutoEscapeBeanHandler(this));
		}

		return _escapedModel;
	}

	@Override
	public Object clone() {
		SegmentsEntryImpl segmentsEntryImpl = new SegmentsEntryImpl();

		segmentsEntryImpl.setMvccVersion(getMvccVersion());
		segmentsEntryImpl.setUuid(getUuid());
		segmentsEntryImpl.setSegmentsEntryId(getSegmentsEntryId());
		segmentsEntryImpl.setGroupId(getGroupId());
		segmentsEntryImpl.setCompanyId(getCompanyId());
		segmentsEntryImpl.setUserId(getUserId());
		segmentsEntryImpl.setUserName(getUserName());
		segmentsEntryImpl.setCreateDate(getCreateDate());
		segmentsEntryImpl.setModifiedDate(getModifiedDate());
		segmentsEntryImpl.setSegmentsEntryKey(getSegmentsEntryKey());
		segmentsEntryImpl.setName(getName());
		segmentsEntryImpl.setDescription(getDescription());
		segmentsEntryImpl.setActive(isActive());
		segmentsEntryImpl.setCriteria(getCriteria());
		segmentsEntryImpl.setSource(getSource());
		segmentsEntryImpl.setType(getType());
		segmentsEntryImpl.setLastPublishDate(getLastPublishDate());

		segmentsEntryImpl.resetOriginalValues();

		return segmentsEntryImpl;
	}

	@Override
	public int compareTo(SegmentsEntry segmentsEntry) {
		int value = 0;

		value = DateUtil.compareTo(
			getModifiedDate(), segmentsEntry.getModifiedDate());

		value = value * -1;

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

		if (!(obj instanceof SegmentsEntry)) {
			return false;
		}

		SegmentsEntry segmentsEntry = (SegmentsEntry)obj;

		long primaryKey = segmentsEntry.getPrimaryKey();

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
		return _entityCacheEnabled;
	}

	@Override
	public boolean isFinderCacheEnabled() {
		return _finderCacheEnabled;
	}

	@Override
	public void resetOriginalValues() {
		SegmentsEntryModelImpl segmentsEntryModelImpl = this;

		segmentsEntryModelImpl._originalUuid = segmentsEntryModelImpl._uuid;

		segmentsEntryModelImpl._originalGroupId =
			segmentsEntryModelImpl._groupId;

		segmentsEntryModelImpl._setOriginalGroupId = false;

		segmentsEntryModelImpl._originalCompanyId =
			segmentsEntryModelImpl._companyId;

		segmentsEntryModelImpl._setOriginalCompanyId = false;

		segmentsEntryModelImpl._setModifiedDate = false;

		segmentsEntryModelImpl._originalSegmentsEntryKey =
			segmentsEntryModelImpl._segmentsEntryKey;

		segmentsEntryModelImpl._originalActive = segmentsEntryModelImpl._active;

		segmentsEntryModelImpl._setOriginalActive = false;

		segmentsEntryModelImpl._originalSource = segmentsEntryModelImpl._source;

		segmentsEntryModelImpl._originalType = segmentsEntryModelImpl._type;

		segmentsEntryModelImpl._columnBitmask = 0;
	}

	@Override
	public CacheModel<SegmentsEntry> toCacheModel() {
		SegmentsEntryCacheModel segmentsEntryCacheModel =
			new SegmentsEntryCacheModel();

		segmentsEntryCacheModel.mvccVersion = getMvccVersion();

		segmentsEntryCacheModel.uuid = getUuid();

		String uuid = segmentsEntryCacheModel.uuid;

		if ((uuid != null) && (uuid.length() == 0)) {
			segmentsEntryCacheModel.uuid = null;
		}

		segmentsEntryCacheModel.segmentsEntryId = getSegmentsEntryId();

		segmentsEntryCacheModel.groupId = getGroupId();

		segmentsEntryCacheModel.companyId = getCompanyId();

		segmentsEntryCacheModel.userId = getUserId();

		segmentsEntryCacheModel.userName = getUserName();

		String userName = segmentsEntryCacheModel.userName;

		if ((userName != null) && (userName.length() == 0)) {
			segmentsEntryCacheModel.userName = null;
		}

		Date createDate = getCreateDate();

		if (createDate != null) {
			segmentsEntryCacheModel.createDate = createDate.getTime();
		}
		else {
			segmentsEntryCacheModel.createDate = Long.MIN_VALUE;
		}

		Date modifiedDate = getModifiedDate();

		if (modifiedDate != null) {
			segmentsEntryCacheModel.modifiedDate = modifiedDate.getTime();
		}
		else {
			segmentsEntryCacheModel.modifiedDate = Long.MIN_VALUE;
		}

		segmentsEntryCacheModel.segmentsEntryKey = getSegmentsEntryKey();

		String segmentsEntryKey = segmentsEntryCacheModel.segmentsEntryKey;

		if ((segmentsEntryKey != null) && (segmentsEntryKey.length() == 0)) {
			segmentsEntryCacheModel.segmentsEntryKey = null;
		}

		segmentsEntryCacheModel.name = getName();

		String name = segmentsEntryCacheModel.name;

		if ((name != null) && (name.length() == 0)) {
			segmentsEntryCacheModel.name = null;
		}

		segmentsEntryCacheModel.description = getDescription();

		String description = segmentsEntryCacheModel.description;

		if ((description != null) && (description.length() == 0)) {
			segmentsEntryCacheModel.description = null;
		}

		segmentsEntryCacheModel.active = isActive();

		segmentsEntryCacheModel.criteria = getCriteria();

		String criteria = segmentsEntryCacheModel.criteria;

		if ((criteria != null) && (criteria.length() == 0)) {
			segmentsEntryCacheModel.criteria = null;
		}

		segmentsEntryCacheModel.source = getSource();

		String source = segmentsEntryCacheModel.source;

		if ((source != null) && (source.length() == 0)) {
			segmentsEntryCacheModel.source = null;
		}

		segmentsEntryCacheModel.type = getType();

		String type = segmentsEntryCacheModel.type;

		if ((type != null) && (type.length() == 0)) {
			segmentsEntryCacheModel.type = null;
		}

		Date lastPublishDate = getLastPublishDate();

		if (lastPublishDate != null) {
			segmentsEntryCacheModel.lastPublishDate = lastPublishDate.getTime();
		}
		else {
			segmentsEntryCacheModel.lastPublishDate = Long.MIN_VALUE;
		}

		return segmentsEntryCacheModel;
	}

	@Override
	public String toString() {
		Map<String, Function<SegmentsEntry, Object>> attributeGetterFunctions =
			getAttributeGetterFunctions();

		StringBundler sb = new StringBundler(
			4 * attributeGetterFunctions.size() + 2);

		sb.append("{");

		for (Map.Entry<String, Function<SegmentsEntry, Object>> entry :
				attributeGetterFunctions.entrySet()) {

			String attributeName = entry.getKey();
			Function<SegmentsEntry, Object> attributeGetterFunction =
				entry.getValue();

			sb.append(attributeName);
			sb.append("=");
			sb.append(attributeGetterFunction.apply((SegmentsEntry)this));
			sb.append(", ");
		}

		if (sb.index() > 1) {
			sb.setIndex(sb.index() - 1);
		}

		sb.append("}");

		return sb.toString();
	}

	@Override
	public String toXmlString() {
		Map<String, Function<SegmentsEntry, Object>> attributeGetterFunctions =
			getAttributeGetterFunctions();

		StringBundler sb = new StringBundler(
			5 * attributeGetterFunctions.size() + 4);

		sb.append("<model><model-name>");
		sb.append(getModelClassName());
		sb.append("</model-name>");

		for (Map.Entry<String, Function<SegmentsEntry, Object>> entry :
				attributeGetterFunctions.entrySet()) {

			String attributeName = entry.getKey();
			Function<SegmentsEntry, Object> attributeGetterFunction =
				entry.getValue();

			sb.append("<column><column-name>");
			sb.append(attributeName);
			sb.append("</column-name><column-value><![CDATA[");
			sb.append(attributeGetterFunction.apply((SegmentsEntry)this));
			sb.append("]]></column-value></column>");
		}

		sb.append("</model>");

		return sb.toString();
	}

	private static class EscapedModelProxyProviderFunctionHolder {

		private static final Function<InvocationHandler, SegmentsEntry>
			_escapedModelProxyProviderFunction = _getProxyProviderFunction();

	}

	private static boolean _entityCacheEnabled;
	private static boolean _finderCacheEnabled;

	private long _mvccVersion;
	private String _uuid;
	private String _originalUuid;
	private long _segmentsEntryId;
	private long _groupId;
	private long _originalGroupId;
	private boolean _setOriginalGroupId;
	private long _companyId;
	private long _originalCompanyId;
	private boolean _setOriginalCompanyId;
	private long _userId;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private boolean _setModifiedDate;
	private String _segmentsEntryKey;
	private String _originalSegmentsEntryKey;
	private String _name;
	private String _nameCurrentLanguageId;
	private String _description;
	private String _descriptionCurrentLanguageId;
	private boolean _active;
	private boolean _originalActive;
	private boolean _setOriginalActive;
	private String _criteria;
	private String _source;
	private String _originalSource;
	private String _type;
	private String _originalType;
	private Date _lastPublishDate;
	private long _columnBitmask;
	private SegmentsEntry _escapedModel;

}