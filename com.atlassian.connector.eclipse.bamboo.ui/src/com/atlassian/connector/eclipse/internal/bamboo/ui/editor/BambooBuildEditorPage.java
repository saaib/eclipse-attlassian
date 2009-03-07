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

package com.atlassian.connector.eclipse.internal.bamboo.ui.editor;

import com.atlassian.connector.eclipse.internal.bamboo.core.BambooConstants;
import com.atlassian.connector.eclipse.internal.bamboo.ui.editor.parts.AbstractBambooEditorFormPart;
import com.atlassian.connector.eclipse.internal.bamboo.ui.editor.parts.BambooDetailsPart;
import com.atlassian.connector.eclipse.internal.bamboo.ui.editor.parts.BambooSummaryPart;
import com.atlassian.theplugin.commons.bamboo.BambooBuild;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import java.util.ArrayList;
import java.util.List;

/**
 * @author thomas
 */
public class BambooBuildEditorPage extends BambooFormPage {

	/**
	 * Causes the form page to reflow on resize.
	 */
	private final class ParentResizeHandler implements Listener {
		private static final int REFLOW_TIMER_DELAY = 300;

		private int generation;

		public void handleEvent(Event event) {
			++generation;

			Display.getCurrent().timerExec(REFLOW_TIMER_DELAY, new Runnable() {
				private final int scheduledGeneration = generation;

				public void run() {
					if (getManagedForm().getForm().isDisposed()) {
						return;
					}

					if (scheduledGeneration == generation) {
						getManagedForm().reflow(true);
					}
				}
			});
		}
	}

	private final List<AbstractBambooEditorFormPart> parts;

	private Composite editorComposite;

	private FormToolkit toolkit;

	private ScrolledForm form;

	private Color selectionColor;

	private BambooBuild build;

	private boolean reflow;

	private Control highlightedControl;

	private static final int VERTICAL_BAR_WIDTH = 15;

	public BambooBuildEditorPage(BambooEditor parentEditor, String title) {
		super(parentEditor, BambooConstants.BAMBOO_EDITOR_PAGE_ID, title);
		parts = new ArrayList<AbstractBambooEditorFormPart>();
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		form = managedForm.getForm();

		toolkit = managedForm.getToolkit();

		selectionColor = new Color(getSite().getShell().getDisplay(), 255, 231, 198);

		EditorUtil.disableScrollingOnFocus(form);

		try {
			setReflow(false);

			editorComposite = form.getBody();

			GridLayout editorLayout = new GridLayout();
			editorComposite.setLayout(editorLayout);
			editorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

			editorComposite.setMenu(getEditor().getMenu());

			Composite createComposite = toolkit.createComposite(editorComposite);
			createComposite.setLayout(new GridLayout());
			GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(createComposite);

			Label createLabel = toolkit.createLabel(createComposite, "Initializing review editor...");
			createLabel.setFont(JFaceResources.getBannerFont());

			GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(createLabel);

			//for now, just display build
			build = getEditor().getEditorInput().getBambooBuild();
			createFormContent();
			getEditor().updateHeaderToolBar();

		} finally {
			setReflow(true);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.addListener(SWT.Resize, new ParentResizeHandler());
		super.createPartControl(parent);
	}

	@Override
	public void dispose() {
		super.dispose();
		editorComposite = null;
		toolkit = null;
		selectionColor.dispose();
		selectionColor = null;
	}

	private void createFormParts() {
		parts.add(new BambooSummaryPart());
		parts.add(new BambooDetailsPart("Summary"));
//		parts.add(new SomeBambooPart());
//		parts.add(new SomeBambooPart());
//		parts.add(new SomeBambooPart());
	}

	private void createFormContent() {
		if (editorComposite == null) {
			return;
		}

		assert (build != null);
		try {

			setReflow(false);
			clearFormContent();

			createFormParts();

			for (AbstractBambooEditorFormPart part : parts) {
				getManagedForm().addPart(part);
				part.initialize(this, build, getEditor().getEditorInput().getRepository());
				part.createControl(editorComposite, toolkit);
			}

			setMenu(editorComposite, editorComposite.getMenu());

		} finally {
			setReflow(true);
			reflow();
		}

	}

	/**
	 * Force a re-layout of entire form.
	 */
	public void reflow() {
		if (reflow) {
			// help the layout managers: ensure that the form width always matches
			// the parent client area width.
			Rectangle parentClientArea = form.getParent().getClientArea();
			Point formSize = form.getSize();
			if (formSize.x != parentClientArea.width) {
				ScrollBar verticalBar = form.getVerticalBar();
				int verticalBarWidth = verticalBar != null ? verticalBar.getSize().x : VERTICAL_BAR_WIDTH;
				form.setSize(parentClientArea.width - verticalBarWidth, formSize.y);
			}

			form.layout(true, false);
			form.reflow(true);
		}
	}

	public void setMenu(Composite composite, Menu menu) {
		if (!composite.isDisposed()) {
			composite.setMenu(menu);
			for (Control child : composite.getChildren()) {
				child.setMenu(menu);
				if (child instanceof Composite) {
					setMenu((Composite) child, menu);
				}
			}
		}
	}

	public void setReflow(boolean reflow) {
		this.reflow = reflow;
		form.setRedraw(reflow);
	}

	private void clearFormContent() {

		for (AbstractBambooEditorFormPart part : parts) {
			getManagedForm().removePart(part);
		}

		parts.clear();

		highlightedControl = null;

		Menu menu = editorComposite.getMenu();
		// preserve context menu
		EditorUtil.setMenu(editorComposite, null);

		// remove all of the old widgets so that we can redraw the editor
		for (Control child : editorComposite.getChildren()) {
			child.dispose();
		}

		editorComposite.setMenu(menu);

		for (AbstractBambooEditorFormPart part : parts) {
			part.dispose();
		}
	}

	@Override
	public BambooEditor getEditor() {
		// ignore
		return (BambooEditor) super.getEditor();
	}
}
