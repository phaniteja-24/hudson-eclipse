package dk.contix.eclipse.hudson.views.actions;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import dk.contix.eclipse.hudson.Activator;
import dk.contix.eclipse.hudson.Job;

public class StatusFilter extends ViewerFilter {

	public boolean select(Viewer viewer, Object parentElement, Object element) {
		Job j = (Job) element;
		IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();

		switch (j.getStatus().getStatus()) {
			case SUCCESS:
				return prefs.getBoolean(Activator.PREF_FILTER_SUCCESS);
			case FAIL:
				return prefs.getBoolean(Activator.PREF_FILTER_FAIL);
			case TEST_FAIL:
				return prefs.getBoolean(Activator.PREF_FILTER_FAIL_TEST);
			case NO_BUILD:
				return prefs.getBoolean(Activator.PREF_FILTER_NO_BUILD);
			case DISABLED:
				return prefs.getBoolean(Activator.PREF_FILTER_DISABLED);
		}

		return true;
	}

}
