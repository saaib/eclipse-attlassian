/*******************************************************************************
 * Copyright (c) 2008 Atlassian and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Atlassian - initial API and implementation
 ******************************************************************************/

package com.atlassian.connector.eclipse.internal.bamboo.core.configuration;

import com.atlassian.theplugin.commons.cfg.BambooServerCfg;
import com.atlassian.theplugin.commons.cfg.ServerId;

/**
 * Server Config for Eclipse connector so that we can key on just the url
 * 
 * @author Shawn Minto
 */
public class EclipseBambooServerCfg extends BambooServerCfg {

	private static final int HASCODE_MAGIC_NOT_TEMPORARY = 1237;

	private static final int HASCODE_MAGIC_TEMPORARY = 1231;

	private final boolean isTemporary;

	public EclipseBambooServerCfg(String name, String url, boolean isTemporary) {
		super(name, new ServerId());
		setUrl(url);
		this.isTemporary = isTemporary;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isTemporary ? HASCODE_MAGIC_TEMPORARY : HASCODE_MAGIC_NOT_TEMPORARY);
		result = prime * result + ((getUrl() == null) ? 0 : getUrl().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof EclipseBambooServerCfg)) {
			return false;
		}
		final EclipseBambooServerCfg other = (EclipseBambooServerCfg) obj;
		if (isTemporary != other.isTemporary) {
			return false;
		}
		if (getUrl() == null) {
			if (other.getUrl() != null) {
				return false;
			}
		} else if (!getUrl().equals(other.getUrl())) {
			return false;
		}
		return true;
	}

}
