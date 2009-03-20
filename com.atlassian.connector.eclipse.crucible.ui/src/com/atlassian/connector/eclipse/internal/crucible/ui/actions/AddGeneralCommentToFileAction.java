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

package com.atlassian.connector.eclipse.internal.crucible.ui.actions;

import com.atlassian.connector.eclipse.internal.crucible.core.CrucibleUtil;
import com.atlassian.connector.eclipse.internal.crucible.ui.CrucibleUiPlugin;
import com.atlassian.connector.eclipse.internal.crucible.ui.CrucibleUiUtil;
import com.atlassian.connector.eclipse.internal.crucible.ui.IReviewAction;
import com.atlassian.connector.eclipse.internal.crucible.ui.IReviewActionListener;
import com.atlassian.connector.eclipse.ui.team.CrucibleFile;
import com.atlassian.connector.eclipse.ui.team.TeamUiUtils;
import com.atlassian.theplugin.commons.crucible.api.model.Review;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.LineRange;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * Action to add a general file comment to the active review
 * 
 * @author Shawn Minto
 * @author Thomas Ehrnhoefer
 */
public class AddGeneralCommentToFileAction extends AbstractAddCommentAction implements IReviewAction {

	private CrucibleFile crucibleFile;

	private IReviewActionListener actionListener;

	public AddGeneralCommentToFileAction() {
		this(null, null);
	}

	public AddGeneralCommentToFileAction(CrucibleFile file, Review review) {
		super("Create General File Comment...");
		super.review = review;
		this.crucibleFile = file;

		if (review != null && crucibleFile != null) {
			setEnabled(CrucibleUtil.canAddCommentToReview(review));
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		super.selectionChanged(action, selection);
		if (action.isEnabled() && isEnabled()) {
			IEditorPart editorPart = getActiveEditor();
			IEditorInput editorInput = getEditorInputFromSelection(selection);
			if (editorInput != null && editorPart != null) {
				if (crucibleFile == null) {
					crucibleFile = TeamUiUtils.getCorrespondingCrucibleFileFromEditorInput(editorInput,
							CrucibleUiPlugin.getDefault().getActiveReviewManager().getActiveReview());
				}
				if (crucibleFile != null && !crucibleFile.isOldFile()
						&& CrucibleUtil.canAddCommentToReview(getReview())
						&& CrucibleUiUtil.isFilePartOfActiveReview(crucibleFile)) {
					action.setEnabled(true);
					setEnabled(true);
					return;
				}
			}
		}
		action.setEnabled(false);
		setEnabled(false);
		crucibleFile = null;
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		if (crucibleFile != null && !crucibleFile.isOldFile() && CrucibleUtil.canAddCommentToReview(getReview())
				&& CrucibleUiUtil.isFilePartOfActiveReview(crucibleFile)) {
			return true;
		}
		return false;
	}

	@Override
	protected Review getReview() {
		if (review != null) {
			return review;
		} else {
			return CrucibleUiPlugin.getDefault().getActiveReviewManager().getActiveReview();
		}
	}

	@Override
	protected CrucibleFile getCrucibleFile() {
		return crucibleFile;
	}

	@Override
	protected LineRange getSelectedRange() {
		return null;
	}

	@Override
	protected String getDialogTitle() {
		return "Create General File Comment";
	}

	@Override
	public final void run() {
		super.run();
		if (actionListener != null) {
			actionListener.actionRan(this);
		}
	}

	@Override
	public String getToolTipText() {
		return "Add General File Comment...";
	}

	public void setActionListener(IReviewActionListener listener) {
		this.actionListener = listener;
	}

}
