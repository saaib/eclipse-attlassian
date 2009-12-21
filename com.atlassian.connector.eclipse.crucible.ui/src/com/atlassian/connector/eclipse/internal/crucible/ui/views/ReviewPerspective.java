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

package com.atlassian.connector.eclipse.internal.crucible.ui.views;

import com.atlassian.connector.eclipse.internal.crucible.ui.CrucibleUiPlugin;

import org.eclipse.mylyn.tasks.ui.ITasksUiConstants;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import java.lang.reflect.Field;

public class ReviewPerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		defineActions(layout);
		defineLayout(layout);
	}

	private void defineLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();

		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.20f, editorArea);
		left.addView(CrucibleUiPlugin.EXPLORER_VIEW_ID);

		boolean hasProjectExplorer = false;
		try {
			// this field is on e3.5 and following
			Field id = IPageLayout.class.getField("ID_PROJECT_EXPLORER");
			left.addView((String) id.get(null));
			hasProjectExplorer = true;
		} catch (Exception e) {
			// ignore
		}

		if (!hasProjectExplorer) {
			try {
				Class<?> javaUiClz = Class.forName("org.eclipse.jdt.ui.JavaUI");
				Field id = javaUiClz.getField("ID_PACKAGES");
				left.addView((String) id.get(null));
			} catch (Exception e) {
				// ignore
			}
		}

		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, 0.6f, editorArea);
		right.addView(CrucibleUiPlugin.COMMENTS_VIEW_ID);
		right.addView(ITasksUiConstants.ID_VIEW_TASKS);
	}

	private void defineActions(IPageLayout layout) {
		layout.addShowViewShortcut(CrucibleUiPlugin.EXPLORER_VIEW_ID);
		layout.addShowViewShortcut(CrucibleUiPlugin.COMMENTS_VIEW_ID);
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		layout.addShowViewShortcut(ITasksUiConstants.ID_VIEW_TASKS);
		layout.addShowViewShortcut(ITasksUiConstants.ID_VIEW_REPOSITORIES);
	}

}
