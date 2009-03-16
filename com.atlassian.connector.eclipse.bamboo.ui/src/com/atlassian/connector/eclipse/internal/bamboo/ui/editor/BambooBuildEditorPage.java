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
import com.atlassian.connector.eclipse.internal.bamboo.core.BambooCorePlugin;
import com.atlassian.connector.eclipse.internal.bamboo.core.client.BambooClient;
import com.atlassian.connector.eclipse.internal.bamboo.ui.BambooUiPlugin;
import com.atlassian.connector.eclipse.internal.bamboo.ui.editor.parts.AbstractBambooEditorFormPart;
import com.atlassian.connector.eclipse.internal.bamboo.ui.editor.parts.BambooBuildLogPart;
import com.atlassian.connector.eclipse.internal.bamboo.ui.editor.parts.BambooCodeChangesPart;
import com.atlassian.connector.eclipse.internal.bamboo.ui.editor.parts.BambooDetailsPart;
import com.atlassian.connector.eclipse.internal.bamboo.ui.editor.parts.BambooSummaryPart;
import com.atlassian.connector.eclipse.internal.bamboo.ui.editor.parts.BambooTestPart;
import com.atlassian.theplugin.commons.bamboo.BambooBuild;
import com.atlassian.theplugin.commons.bamboo.BuildDetails;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.tasks.core.TaskRepository;
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
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import java.util.ArrayList;
import java.util.List;

/**
 * @author thomas
 */
public class BambooBuildEditorPage extends BambooFormPage {

	private class RetrieveFullBuildInfoJob extends Job {

		private IStatus status;

		public RetrieveFullBuildInfoJob(String name) {
			super(name);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			BambooClient client = BambooCorePlugin.getRepositoryConnector().getClientManager().getClient(repository);

			status = new MultiStatus(BambooUiPlugin.PLUGIN_ID, 0, "Retrieval of full build information failed", null);
			try {
				buildLog = client.getBuildLogs(monitor, repository, build);
				buildDetails = client.getBuildDetails(monitor, repository, build);
				status = new Status(IStatus.OK, BambooUiPlugin.PLUGIN_ID, null);
			} catch (CoreException e) {
				status = new Status(IStatus.ERROR, BambooUiPlugin.PLUGIN_ID, "Failed to retrieve build logs for build "
						+ build.getPlanKey(), e);
			}
			return Status.OK_STATUS;
		}

		public IStatus getStatus() {
			return status;
		}

	}

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

	private final BambooBuild build;

	private final TaskRepository repository;

	private String buildLog;

	private BuildDetails buildDetails;

	private boolean reflow;

	private Control highlightedControl;

	private static final int VERTICAL_BAR_WIDTH = 15;

	private Text initLabel;

	private AbstractBambooEditorFormPart focusablePart;

	public BambooBuildEditorPage(BambooEditor parentEditor, String title) {
		super(parentEditor, BambooConstants.BAMBOO_EDITOR_PAGE_ID, title);
		parts = new ArrayList<AbstractBambooEditorFormPart>();
		build = getEditor().getEditorInput().getBambooBuild();
		repository = getEditor().getEditorInput().getRepository();
	}

	@Override
	public void setFocus() {
		if (focusablePart == null) {
			initLabel.forceFocus();
		} else {
			focusablePart.setFocus();
		}
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
			editorLayout.numColumns = 2;
			editorLayout.makeColumnsEqualWidth = true;
			editorComposite.setLayout(editorLayout);
			editorComposite.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL));

			editorComposite.setMenu(getEditor().getMenu());

			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).applyTo(editorComposite);

			Composite createComposite = toolkit.createComposite(editorComposite);
			createComposite.setLayout(new GridLayout());
			GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(createComposite);

			initLabel = toolkit.createText(createComposite, "Initializing...", SWT.FLAT | SWT.READ_ONLY);
			initLabel.setFont(JFaceResources.getBannerFont());

			GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(initLabel);

			createFormContent();
			getEditor().updateHeaderToolBar();
			setFocus();

			downloadAndRefreshBuild(0, false);
		} finally {
			setReflow(true);
		}
	}

	private void downloadAndRefreshBuild(long delay, final boolean force) {
		final RetrieveFullBuildInfoJob job = new RetrieveFullBuildInfoJob("Retrieve full build details");
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						setBusy(false);
						IStatus status = job.getStatus();
						if (editorComposite != null) {
							if (!status.isOK() || buildDetails == null || buildLog == null) {
								getEditor().setMessage(
										"Unable to retrieve full bamboo build details.  Click to try again.",
										IMessageProvider.WARNING, new HyperlinkAdapter() {
											@Override
											public void linkActivated(HyperlinkEvent e) {
												downloadAndRefreshBuild(0, true);
											}
										});
							} else {
								getEditor().setMessage(null, IMessageProvider.NONE, null);
							}
							for (AbstractBambooEditorFormPart part : parts) {
								part.setBuildDetails(buildDetails);
								part.setBuildLog(buildLog);
								part.buildInfoRetrievalDone(status.isOK());
							}
						}
					}
				});
			}
		});
		job.schedule(delay);
		setBusy(true);
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
		focusablePart = new BambooSummaryPart();
		parts.add(focusablePart);
		parts.add(new BambooDetailsPart("Summary"));
		parts.add(new BambooTestPart("Tests"));
		parts.add(new BambooCodeChangesPart("Code Changes"));
		parts.add(new BambooBuildLogPart("Build Log"));
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
				part.initialize(this, build, repository, buildLog, buildDetails);
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

			form.layout(true, true);
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

	private void setBusy(boolean busy) {
		getEditor().showBusy(busy);
	}

	public void retrieveBuildInfo() {
		downloadAndRefreshBuild(0, true);
	}
}
