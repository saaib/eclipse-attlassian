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

package com.atlassian.connector.eclipse.internal.crucible.ui.editor.parts;

import com.atlassian.connector.eclipse.internal.crucible.ui.CrucibleImages;
import com.atlassian.theplugin.commons.crucible.api.model.Reviewer;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

import java.util.Set;

/**
 * Form part that displays the details of a reviewer
 * 
 * @author Thomas Ehrnhoefer
 */
public class CrucibleReviewersPart {

	private final Set<Reviewer> reviewers;

	public CrucibleReviewersPart(Set<Reviewer> reviewers) {
		super();
		this.reviewers = reviewers;
	}

	public void createControl(FormToolkit toolkit, Composite parent) {
		createControl(toolkit, parent, "Reviewers:");
	}

	public void createControl(FormToolkit toolkit, Composite parent, String labelText) {
		//CHECKSTYLE:MAGIC:OFF

		Label label = createLabelControl(toolkit, parent, labelText);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).applyTo(label);

		Composite reviewersComposite = toolkit.createComposite(parent);
		// use indent to make up for forms border gap
		GridDataFactory.fillDefaults().indent(0, -2).grab(true, false).span(3, 1).hint(300, SWT.DEFAULT).applyTo(
				reviewersComposite);
		RowLayout layout = new RowLayout();
		layout.marginBottom = 0;
		layout.marginTop = 0;
		layout.marginRight = 0;
		layout.marginLeft = 0;
		layout.fill = true;
		reviewersComposite.setLayout(layout);
		if (reviewers.isEmpty()) {
			// avoid blank gap on Linux
			toolkit.createLabel(reviewersComposite, " ");
		} else {
			for (Reviewer reviewer : reviewers) {
				Text text = createReadOnlyText(toolkit, reviewersComposite, reviewer.getDisplayName(), null, false);

				if (reviewer.isCompleted()) {
					Label imageLabel = toolkit.createLabel(reviewersComposite, "");
					imageLabel.setImage(CrucibleImages.getImage(CrucibleImages.REVIEWER_COMPLETE));
				}

				// XXX padding
				toolkit.createLabel(reviewersComposite, " ");
			}
		}
		//CHECKSTYLE:MAGIC:ON
	}

	private Text createReadOnlyText(FormToolkit toolkit, Composite composite, String value, String labelString,
			boolean isMultiline) {

		if (labelString != null) {
			createLabelControl(toolkit, composite, labelString);
		}
		int style = SWT.FLAT | SWT.READ_ONLY;
		if (isMultiline) {
			style |= SWT.MULTI | SWT.WRAP;
		}
		Text text = new Text(composite, style);
		text.setFont(EditorUtil.TEXT_FONT);
		text.setData(FormToolkit.KEY_DRAW_BORDER, Boolean.FALSE);
		text.setText(value);
		toolkit.adapt(text, true, true);

		return text;
	}

	private Label createLabelControl(FormToolkit toolkit, Composite composite, String labelString) {
		Label labelControl = toolkit.createLabel(composite, labelString);
		labelControl.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		return labelControl;
	}
}
