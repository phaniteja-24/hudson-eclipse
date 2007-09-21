package dk.contix.eclipse.hudson;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import dk.contix.eclipse.hudson.views.actions.BuildStatusAction;

/**
 * Content Provider for job listing.
 * 
 * @author Joakim Recht
 * 
 */
public class JobContentProvider implements IStructuredContentProvider {

	private org.eclipse.core.runtime.jobs.Job updateJob;

	private final HudsonClient client = new HudsonClient();

	private Preferences prefs;

	private final TableViewer viewer;

	private Job[] jobs;

	private boolean updating = false;

	private boolean error = false;

	private final BuildStatusAction action;

	public JobContentProvider(final TableViewer viewer, BuildStatusAction action) {
		this.viewer = viewer;
		this.action = action;
		prefs = Activator.getDefault().getPluginPreferences();
		prefs.addPropertyChangeListener(new Preferences.IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				error = false;
				reloadUpdateJob();
			}
		});
		reloadUpdateJob();
	}

	private void reloadUpdateJob() {
		if (updateJob != null) {
			updateJob.cancel();
		}

		if (prefs.getBoolean(Activator.PREF_AUTO_UPDATE)) {
			updateJob = new org.eclipse.core.runtime.jobs.Job("Fetch Hudson status") {
				protected IStatus run(IProgressMonitor monitor) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							viewer.refresh();
						}
					});
					updateJob.schedule(prefs.getInt(Activator.PREF_UPDATE_INTERVAL) * 1000);
					return Status.OK_STATUS;
				}
			};
			updateJob.setPriority(org.eclipse.core.runtime.jobs.Job.DECORATE);
			updateJob.schedule(1500);
		}

	}

	public Object[] getElements(Object inputElement) {
		if (updating)
			return jobs;

		try {
			updating = true;
			Job[] newJobs = client.getJobs();

			if (jobs != null && prefs.getBoolean(Activator.PREF_POPUP_ON_ERROR)) {
				for (int i = 0; i < newJobs.length; i++) {
					if (hasFailed(newJobs[i])) {
						MessageDialog.openWarning(viewer.getControl().getShell(), "Hudson build failed", "Hudson build failed for " + newJobs[i].getName());
						break;
					}
				}
			}
			check: {
				for (int i = 0; i < newJobs.length; i++) {
					if (newJobs[i].getColor().toLowerCase().equals(Job.BUILD_FAIL) || newJobs[i].getColor().toLowerCase().equals(Job.BUILD_TEST_FAIL)) {
						action.setError(newJobs[i]);
						break check;
					}
				}
				action.setOk();
			}

			jobs = newJobs;
			return jobs;
		} catch (Exception e) {
			action.setUnknown();
			if (!error) {
				ErrorDialog.openError(viewer.getControl().getShell(), "Unable to get Hudson status",
						"Unable to get status from Hudson. Check that the base url is configured correctly under preferences.", new Status(Status.ERROR, Activator.PLUGIN_ID, 0,
								"Unable to communicate with Hudson", e));
				error = true;
			}
			return new Object[0];
		} finally {
			updating = false;
		}
	}

	private boolean hasFailed(Job job) {
		for (int i = 0; i < jobs.length; i++) {
			if (jobs[i].getName().equals(job.getName())) {
				return job.getColor().toLowerCase().equals("red") && (jobs[i].getColor().toLowerCase().equals("blue") || jobs[i].getColor().toLowerCase().equals("blue_anime"));
			}
		}
		return false;
	}

	public void dispose() {
		if (updateJob != null) {
			updateJob.cancel();
		}
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
