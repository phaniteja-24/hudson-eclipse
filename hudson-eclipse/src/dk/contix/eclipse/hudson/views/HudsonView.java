package dk.contix.eclipse.hudson.views;

import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.SubStatusLineManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import dk.contix.eclipse.hudson.Activator;
import dk.contix.eclipse.hudson.HudsonClient;
import dk.contix.eclipse.hudson.Job;
import dk.contix.eclipse.hudson.JobContentProvider;
import dk.contix.eclipse.hudson.views.actions.BuildStatusAction;
import dk.contix.eclipse.hudson.views.actions.FilterAction;
import dk.contix.eclipse.hudson.views.actions.FilterJobAction;
import dk.contix.eclipse.hudson.views.actions.OpenPreferencesAction;
import dk.contix.eclipse.hudson.views.actions.StatusFilter;

public class HudsonView extends ViewPart {
	private TableViewer viewer;

	private Action scheduleAction;

	private Action refreshAction;

	private Action openBrowserAction;

	private Action viewConsoleAction;

	private String baseUrl;

	private Action securityTokenAction;

	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		configurePreferences();
	}

	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.VERTICAL));

		viewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

		JobSorter sorter = new JobSorter(viewer);
		BuildStatusAction buildStatusAction = new BuildStatusAction();

		Table t = viewer.getTable();
		t.setLayout(new GridLayout());
		t.setLayoutData(new GridData(GridData.FILL_BOTH));
		TableColumn col = new TableColumn(t, SWT.NONE);
		col.setText("Project");
		col.addListener(SWT.Selection, sorter);

		col = new TableColumn(t, SWT.LEFT);
		col.setText("Build");
		col.addListener(SWT.Selection, sorter);

		col = new TableColumn(t, SWT.LEFT);
		col.setText("Status");
		col.setWidth(20);
		col.addListener(SWT.Selection, sorter);
		t.setHeaderVisible(true);

		viewer.setColumnProperties(new String[] { "Project", "Status", "" });
		viewer.setContentProvider(new JobContentProvider(viewer, buildStatusAction));
		viewer.setLabelProvider(new JobLabelProvider());

		viewer.setSorter(sorter);
		viewer.setInput(getViewSite());

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		SubStatusLineManager slm = (SubStatusLineManager) getViewSite().getActionBars().getStatusLineManager();
		slm.getParent().add(buildStatusAction);

		for (int i = 0; i < 2; i++) {
			t.getColumn(i).pack();
		}

	}

	private void configurePreferences() {
		final Preferences prefs = Activator.getDefault().getPluginPreferences();
		prefs.addPropertyChangeListener(new Preferences.IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				baseUrl = prefs.getString(Activator.PREF_BASE_URL);
				viewer.refresh();
			}
		});
		baseUrl = prefs.getString(Activator.PREF_BASE_URL);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				HudsonView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
		fillLocalPullDown(bars.getMenuManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(refreshAction);
		manager.add(new Separator());
		manager.add(new OpenPreferencesAction(getSite().getShell()));

		MenuManager filtermenu = new MenuManager("Filters");

		StatusFilter filter = new StatusFilter();
		filtermenu.add(new FilterAction(viewer, "Successful builds", "Show successful builds", Activator.PREF_FILTER_SUCCESS, filter));
		filtermenu.add(new FilterAction(viewer, "Failed builds", "Show failed builds", Activator.PREF_FILTER_FAIL, filter));
		filtermenu.add(new FilterAction(viewer, "Test failures", "Show builds with test failures", Activator.PREF_FILTER_FAIL_TEST, filter));
		filtermenu.add(new FilterAction(viewer, "Unbuilt projects", "Show projects which have not been built yet", Activator.PREF_FILTER_NO_BUILD, filter));
		manager.add(filtermenu);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(openBrowserAction);
		manager.add(scheduleAction);
		manager.add(viewConsoleAction);
		manager.add(securityTokenAction);
		manager.add(new Separator());
		manager.add(refreshAction);

		makeFilterAction(manager);

		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void makeFilterAction(IMenuManager manager) {
		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
		if (sel.size() == 1) {
			Job j = (Job) sel.getFirstElement();
			manager.add(new FilterJobAction("Ignore failed builds", "Do not report build errors for this project", j.getName()));
		}
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(scheduleAction);
		manager.add(refreshAction);
	}

	private void makeActions() {
		scheduleAction = new Action() {
			public void run() {
				IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
				final Job j = (Job) sel.getFirstElement();

				org.eclipse.core.runtime.jobs.Job sj = new org.eclipse.core.runtime.jobs.Job("Scheduling Hudson build") {
					protected IStatus run(IProgressMonitor monitor) {
						try {
							new HudsonClient().scheduleJob(j.getName());
						} catch (IOException e1) {
							return new Status(Status.ERROR, Activator.PLUGIN_ID, 0, "Unable to schedule job", e1);
						}

						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								viewer.refresh();
							}
						});
						return Status.OK_STATUS;
					}

				};
				sj.setPriority(org.eclipse.core.runtime.jobs.Job.INTERACTIVE);
				sj.schedule();
			}
		};
		scheduleAction.setText("Schedule new build");
		scheduleAction.setToolTipText("Schedule new build for project");
		scheduleAction.setImageDescriptor(Activator.getImageDescriptor("icons/schedule.png"));
		scheduleAction.setEnabled(false);

		openBrowserAction = new Action() {
			public void run() {
				IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
				openBrowser(sel);
			}
		};
		openBrowserAction.setText("Open in browser");
		openBrowserAction.setToolTipText("Open job status in browser");
		openBrowserAction.setEnabled(false);

		viewConsoleAction = new Action() {
			public void run() {
				IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
				Job j = (Job) sel.getFirstElement();

				String url = baseUrl + "/job/" + j.getName() + "/lastBuild/consoleText";
				openBrowser(url, "Console output");
			}
		};
		viewConsoleAction.setText("View console output");
		viewConsoleAction.setToolTipText("Open the console output for the latest build");
		viewConsoleAction.setEnabled(false);

		securityTokenAction = new Action() {
			public void run() {
				IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
				Job j = (Job) sel.getFirstElement();

				showSecurityTokenDialog(j);
			}
		};
		securityTokenAction.setText("Set security token...");
		securityTokenAction.setToolTipText("Configure the security token used to schedule builds");
		securityTokenAction.setEnabled(false);

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				scheduleAction.setEnabled(sel.size() == 1);
				openBrowserAction.setEnabled(sel.size() == 1);
				securityTokenAction.setEnabled(sel.size() == 1);

				Job j = (Job) sel.getFirstElement();
				viewConsoleAction.setEnabled(sel.size() == 1 && j.getLastBuild() != null);
			}
		});

		refreshAction = new Action() {
			public void run() {
				org.eclipse.core.runtime.jobs.Job refresh = new org.eclipse.core.runtime.jobs.Job("Refreshing Hudson status") {
					protected IStatus run(IProgressMonitor monitor) {
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								viewer.refresh();
							}
						});
						return Status.OK_STATUS;
					}
				};
				refresh.setPriority(org.eclipse.core.runtime.jobs.Job.INTERACTIVE);
				refresh.schedule();
			}
		};
		refreshAction.setText("Refresh status");
		refreshAction.setToolTipText("Refresh status for all projects");
		refreshAction.setImageDescriptor(Activator.getImageDescriptor("icons/refresh.png"));

	}

	private void showSecurityTokenDialog(Job j) {
		Preferences prefs = Activator.getDefault().getPluginPreferences();
		String sc = prefs.getString(Activator.PREF_SECURITY_TOKEN + "_" + j.getName());
		InputDialog dialog = new InputDialog(getSite().getShell(), "Enter security token for " + j.getName(), "Enter the security token for job " + j.getName(), sc, null);
		if (dialog.open() == InputDialog.OK) {
			prefs.setValue(Activator.PREF_SECURITY_TOKEN + "_" + j.getName(), dialog.getValue());
		}
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {

				final IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				org.eclipse.core.runtime.jobs.Job open = new org.eclipse.core.runtime.jobs.Job("Opening Hudson browser") {
					protected IStatus run(IProgressMonitor monitor) {
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								openBrowser(sel);
							}
						});
						return Status.OK_STATUS;
					}
				};
				open.setPriority(org.eclipse.core.runtime.jobs.Job.INTERACTIVE);
				open.schedule();
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		if (viewer != null) {
			viewer.getControl().setFocus();
		}
	}

	private void openBrowser(IStructuredSelection selection) {
		final Job j = (Job) selection.getFirstElement();
		final String url = baseUrl + "/job/" + j.getName();
		final String name = j.getName();
		openBrowser(url, name);
	}

	private void openBrowser(final String url, final String name) {
		try {
			IEditorReference[] refs = getSite().getPage().getEditorReferences();

			IEditorPart editor = null;
			for (int i = 0; i < refs.length; i++) {
				if (refs[i].getId().equals(Activator.PLUGIN_ID + ".browser")) {
					editor = refs[i].getEditor(true);
					break;
				}
			}
			if (editor != null) {
				((HudsonBrowser) editor).openUrl(url, name);
			}

			if (editor == null) {
				getSite().getPage().openEditor(new IPathEditorInput() {
					public IPath getPath() {
						return new Path(url);
					}

					public boolean exists() {
						return false;
					}

					public ImageDescriptor getImageDescriptor() {
						return null;
					}

					public String getName() {
						return name;
					}

					public IPersistableElement getPersistable() {
						return null;
					}

					public String getToolTipText() {
						return "Hudson browser";
					}

					@SuppressWarnings("unchecked")
					public Object getAdapter(Class adapter) {
						return null;
					}
				}, Activator.PLUGIN_ID + ".browser");
			}
		} catch (PartInitException e) {
			showError("Unable to launch browser", e);
		}
	}

	private void showError(String msg, Exception e) {
		ErrorDialog.openError(getSite().getShell(), "Hudson Error", null, new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, msg, e));
	}
}