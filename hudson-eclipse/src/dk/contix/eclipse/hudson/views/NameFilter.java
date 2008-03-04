package dk.contix.eclipse.hudson.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import dk.contix.eclipse.hudson.Activator;
import dk.contix.eclipse.hudson.Job;

public class NameFilter extends ViewerFilter implements PropertyChangeListener{

	private String filterStr = null;

	public boolean select(Viewer viewer, Object parentElement, Object element) {
		Job job = (Job) element;
		String name = job.getName();
		IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();           
		if(!prefs.getBoolean(Activator.PREF_FILTER_NAME)) {                     
			return true;
		}
		if (filterStr == null || name.startsWith(filterStr)) {                   
			return true;
		}
		return false;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		filterStr = (String)evt.getNewValue();                  
		((HudsonView)evt.getSource()).refreshTableViewer();
	}

}
