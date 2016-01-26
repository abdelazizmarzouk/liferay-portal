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

package com.liferay.portal.ldap.internal;

import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.exportimport.UserImporter;
import com.liferay.portal.kernel.security.ldap.LDAPSettings;
import com.liferay.portal.model.Company;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(immediate = true)
public class LDAPPortalInstanceLifecycleListener
	implements PortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		if (_ldapSettings.isImportOnStartup(company.getCompanyId())) {
			try {
				_userImporter.importUsers(company.getCompanyId());
			}
			catch (Exception e) {
				_log.error(
					"Unable to import users for company " +
						company.getCompanyId(),
					e);
			}
		}
	}

	@Override
	public void portalInstanceUnregistered(Company company) throws Exception {
	}

	@Reference(unbind = "-")
	protected void setLdapSettings(LDAPSettings ldapSettings) {
		_ldapSettings = ldapSettings;
	}

	@Reference(unbind = "-")
	protected void setUserImporter(UserImporter userImporter) {
		_userImporter = userImporter;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LDAPPortalInstanceLifecycleListener.class);

	private LDAPSettings _ldapSettings;
	private UserImporter _userImporter;

}