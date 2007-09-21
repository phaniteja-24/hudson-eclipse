package dk.contix.eclipse.hudson.preference;


import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import dk.contix.eclipse.hudson.Activator;


public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public PreferenceInitializer() {
	}

	public void initializeDefaultPreferences() {
		IEclipsePreferences node = new DefaultScope().getNode(Activator.PLUGIN_ID);
		node.put(Activator.PREF_AUTO_UPDATE, "true");
		node.put(Activator.PREF_UPDATE_INTERVAL, "30");
		node.put(Activator.PREF_POPUP_ON_ERROR, "false");

		String[] filterprefs = new String[] { Activator.PREF_FILTER_FAIL, Activator.PREF_FILTER_FAIL_TEST, Activator.PREF_FILTER_NO_BUILD, Activator.PREF_FILTER_SUCCESS };
		for (int i = 0; i < filterprefs.length; i++) {
			node.putBoolean(filterprefs[i], true);
		}
	}
}
