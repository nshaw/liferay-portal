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

package com.liferay.layout.page.template.internal.upgrade.v3_1_1;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Pavel Savinov
 */
public class UpgradeLayoutPageTemplateEntryIndex extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		upgradeIndex();
	}

	protected void upgradeIndex() throws Exception {
		runSQL("drop index IX_A075DAA4 on LayoutPageTemplateEntry");

		runSQL(
			"create unique index IX_C3960EB1 on LayoutPageTemplateEntry (" +
				"groupId, name, type_)");
	}

}