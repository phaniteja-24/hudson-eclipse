package dk.contix.eclipse.hudson.preference;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import dk.contix.eclipse.hudson.Activator;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public PreferenceInitializer() {
	}

	public void initializeDefaultPreferences() {
		IPreferenceStore node = Activator.getDefault().getPreferenceStore();
		node.setDefault(Activator.PREF_AUTO_UPDATE, "true");
		node.setDefault(Activator.PREF_UPDATE_INTERVAL, "30");
		node.setDefault(Activator.PREF_POPUP_ON_ERROR, "false");
		node.setDefault(Activator.PREF_POPUP_ON_CONNECTION_ERROR, "false");

		String[] filterprefs = new String[] { Activator.PREF_FILTER_FAIL, Activator.PREF_FILTER_FAIL_TEST, Activator.PREF_FILTER_NO_BUILD, Activator.PREF_FILTER_SUCCESS, Activator.PREF_FILTER_DISABLED };
		for (int i = 0; i < filterprefs.length; i++) {
			node.setDefault(filterprefs[i], true);
		}
	}
}
