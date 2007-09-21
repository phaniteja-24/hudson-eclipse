package dk.contix.eclipse.hudson.views.actions;


import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.PlatformUI;

import dk.contix.eclipse.hudson.Activator;
import dk.contix.eclipse.hudson.Job;


public class StatusFilter extends ViewerFilter {

	public boolean select(Viewer viewer, Object parentElement, Object element) {
		Job j = (Job) element;
		String r = j.getColor();
		IPreferenceStore prefs = PlatformUI.getPreferenceStore();
		
		if (r.startsWith(Job.BUILD_SUCCESS)) {
			return prefs.getBoolean(Activator.PREF_FILTER_SUCCESS);
		} else if (r.startsWith(Job.BUILD_FAIL)) {
			return prefs.getBoolean(Activator.PREF_FILTER_FAIL);
		} else if (r.startsWith(Job.BUILD_TEST_FAIL)) {
			return prefs.getBoolean(Activator.PREF_FILTER_FAIL_TEST);
		} else if (r.startsWith(Job.BUILD_NO_BUILD)) {
			return prefs.getBoolean(Activator.PREF_FILTER_NO_BUILD);
		}
		
		return false;
	}

}
