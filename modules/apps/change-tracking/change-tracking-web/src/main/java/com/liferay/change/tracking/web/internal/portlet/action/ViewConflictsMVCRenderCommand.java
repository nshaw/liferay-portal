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

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.conflict.ConflictInfo;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.web.internal.constants.CTWebKeys;
import com.liferay.change.tracking.web.internal.display.CTDisplayRendererRegistry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + CTPortletKeys.CHANGE_LISTS,
		"mvc.command.name=/change_lists/view_conflicts"
	},
	service = MVCRenderCommand.class
)
public class ViewConflictsMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		long ctCollectionId = ParamUtil.getLong(
			renderRequest, "ctCollectionId");

		List<ObjectValuePair<ConflictInfo, CTEntry>> resolvedConflicts =
			new ArrayList<>();
		List<ObjectValuePair<ConflictInfo, CTEntry>> unresolvedConflicts =
			new ArrayList<>();

		CTCollection ctCollection;

		try {
			ctCollection = _ctCollectionLocalService.getCTCollection(
				ctCollectionId);

			Map<Long, List<ConflictInfo>> conflictInfoMap =
				_ctCollectionLocalService.checkConflicts(ctCollection);

			for (Map.Entry<Long, List<ConflictInfo>> entry :
					conflictInfoMap.entrySet()) {

				List<CTEntry> ctEntries = _ctEntryLocalService.getCTEntries(
					ctCollectionId, entry.getKey());

				List<ConflictInfo> conflictInfos = entry.getValue();

				for (ConflictInfo conflictInfo : conflictInfos) {
					for (CTEntry ctEntry : ctEntries) {
						if ((entry.getKey() == ctEntry.getModelClassNameId()) &&
							(ctEntry.getModelClassPK() ==
								conflictInfo.getSourcePrimaryKey())) {

							if (conflictInfo.isResolved()) {
								resolvedConflicts.add(
									new ObjectValuePair<>(
										conflictInfo, ctEntry));
							}
							else {
								unresolvedConflicts.add(
									new ObjectValuePair<>(
										conflictInfo, ctEntry));
							}

							break;
						}
					}
				}
			}

			renderRequest.setAttribute(CTWebKeys.CT_COLLECTION, ctCollection);
			renderRequest.setAttribute(
				CTWebKeys.CT_DISPLAY_RENDERER_REGISTRY,
				_ctDisplayRendererRegistry);
			renderRequest.setAttribute(
				CTWebKeys.RESOLVED_CONFLICTS, resolvedConflicts);
			renderRequest.setAttribute(
				CTWebKeys.UNRESOLVED_CONFLICTS, unresolvedConflicts);
		}
		catch (PortalException pe) {
			throw new PortletException(pe);
		}

		return "/change_lists/conflicts.jsp";
	}

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private CTDisplayRendererRegistry _ctDisplayRendererRegistry;

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference
	private Portal _portal;

}