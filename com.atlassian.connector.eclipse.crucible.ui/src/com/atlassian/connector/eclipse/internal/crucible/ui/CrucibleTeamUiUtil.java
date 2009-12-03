/*******************************************************************************
 * Copyright (c) 2009 Atlassian and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Atlassian - initial API and implementation
 ******************************************************************************/

package com.atlassian.connector.eclipse.internal.crucible.ui;

import com.atlassian.connector.eclipse.team.ui.AtlassianTeamUiPlugin;
import com.atlassian.connector.eclipse.team.ui.CrucibleFile;
import com.atlassian.connector.eclipse.team.ui.ITeamUiResourceConnector;
import com.atlassian.connector.eclipse.team.ui.TeamUiResourceManager;
import com.atlassian.connector.eclipse.team.ui.TeamUiUtils;
import com.atlassian.connector.eclipse.ui.exceptions.UnsupportedTeamProviderException;
import com.atlassian.theplugin.commons.crucible.api.model.Review;

import org.eclipse.ui.IEditorInput;
import org.jetbrains.annotations.Nullable;

public class CrucibleTeamUiUtil {

	private CrucibleTeamUiUtil() {
	}

	@Nullable
	public static CrucibleFile getCorrespondingCrucibleFileFromEditorInput(IEditorInput editorInput, Review activeReview) {
		if (activeReview == null) {
			return null;
		}

		TeamUiResourceManager teamResourceManager = AtlassianTeamUiPlugin.getDefault().getTeamResourceManager();

		for (ITeamUiResourceConnector connector : teamResourceManager.getTeamConnectors()) {
			if (connector.isEnabled() && connector.canHandleEditorInput(editorInput)) {
				CrucibleFile fileInfo;
				try {
					fileInfo = connector.getCorrespondingCrucibleFileFromEditorInput(editorInput, activeReview);
				} catch (UnsupportedTeamProviderException e) {
					return null;
				}
				if (fileInfo != null) {
					return fileInfo;
				}
			}
		}

		try {
			CrucibleFile file = TeamUiUtils.getDefaultConnector().getCorrespondingCrucibleFileFromEditorInput(
					editorInput, activeReview);
			if (file != null) {
				return file;
			}
		} catch (UnsupportedTeamProviderException e) {
			// ignore
		}

		return getCruciblePreCommitFile(editorInput);

	}

	@Nullable
	private static CrucibleFile getCruciblePreCommitFile(IEditorInput editorInput) {

		if (editorInput instanceof CruciblePreCommitFileInput) {
			CruciblePreCommitFileInput input = (CruciblePreCommitFileInput) editorInput;
			return input.getCrucibleFile();
		}
		return null;
	}

}
