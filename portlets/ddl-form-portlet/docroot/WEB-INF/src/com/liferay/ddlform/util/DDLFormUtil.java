/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ddlform.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.dynamicdatalists.model.DDLRecord;
import com.liferay.portlet.dynamicdatalists.model.DDLRecordSet;
import com.liferay.portlet.dynamicdatalists.service.DDLRecordLocalServiceUtil;

import java.util.List;

import javax.portlet.ActionRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Marcellus Tavares
 */
public class DDLFormUtil {

	public static boolean hasSubmitted(
			ActionRequest actionRequest, long recordSetId)
		throws SystemException {

		HttpServletRequest request = PortalUtil.getHttpServletRequest(
			actionRequest);

		return hasSubmitted(request, recordSetId);
	}

	public static boolean hasSubmitted(
			HttpServletRequest request, long recordSetId)
		throws SystemException {

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		List<DDLRecord> records = DDLRecordLocalServiceUtil.getRecords(
			recordSetId, themeDisplay.getUserId());
		
		if (records.isEmpty()) {
			return false;
		}

		return true;
	}
	
	public static boolean hasSubmittedSurvey(
			HttpServletRequest request, List<DDLRecordSet> newListaDDL)
		throws SystemException {
		
		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
				WebKeys.THEME_DISPLAY);
		
		boolean submitted = false;
		for (int i = 0; i < newListaDDL.size() && !submitted; i++)
		{
			
			List<DDLRecord> records = DDLRecordLocalServiceUtil.getRecords(
					newListaDDL.get(i).getRecordSetId(), themeDisplay.getUserId());
			
			if (!records.isEmpty()) {
				submitted = true;
			}
		}
		
		return submitted; 
		
	}
}