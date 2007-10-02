package dk.contix.eclipse.hudson.views.actions;

import org.eclipse.jface.preference.BooleanPropertyAction;
import org.eclipse.swt.widgets.Event;

import dk.contix.eclipse.hudson.Activator;

public class FilterJobAction extends BooleanPropertyAction {

	public FilterJobAction(String title, String tooltip, String job) throws IllegalArgumentException {
		super(title, Activator.getDefault().getPreferenceStore(), Activator.PREF_FILTER_IGNORE_PROJECT + "_" + job);

		setToolTipText(tooltip);
	}

	public void runWithEvent(Event event) {
		super.runWithEvent(event);

	}

}
