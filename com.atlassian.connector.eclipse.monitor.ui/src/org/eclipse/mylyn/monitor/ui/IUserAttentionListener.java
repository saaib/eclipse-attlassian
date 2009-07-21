/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.monitor.ui;

/**
 * Notified of user activity and inactivity events.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public interface IUserAttentionListener {

	public void userAttentionGained();

	public void userAttentionLost();

}
