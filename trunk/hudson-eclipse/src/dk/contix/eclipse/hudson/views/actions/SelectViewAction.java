package dk.contix.eclipse.hudson.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TableViewer;

import dk.contix.eclipse.hudson.Activator;
import dk.contix.eclipse.hudson.JobContentProvider;
import dk.contix.eclipse.hudson.JobView;

public class SelectViewAction extends Action{

	private final IPreferenceStore store;
	private final JobView view;
	private final JobContentProvider jobContentProvider;
	private final TableViewer viewer;

	public SelectViewAction(TableViewer viewer, JobView view, JobContentProvider jobContentProvider) {
		super(view.getName(), AS_RADIO_BUTTON);
		this.viewer = viewer;
		this.view = view;
		this.jobContentProvider = jobContentProvider;
		setToolTipText(view.getName());
		
		store = Activator.getDefault().getPreferenceStore();
		
		setChecked(view.getName().equals(store.getString(Activator.PREF_SELECTED_VIEW)));
	}
	
	public void run() {
		store.setValue(Activator.PREF_SELECTED_VIEW, view.getName());

		jobContentProvider.setView(view.getUrl());
		viewer.refresh();
	}
}
