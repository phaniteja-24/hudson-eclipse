package dk.contix.eclipse.hudson;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "dk.contix.eclipse.hudson";
	
	public static final String JOB_FAMILY_UPDATE = PLUGIN_ID + ".update";
	
	public static final String PREF_BASE_URL = "base_url";
	public static final String PREF_AUTO_UPDATE = "auto_update";
	public static final String PREF_UPDATE_INTERVAL = "update_interval";
	public static final String PREF_POPUP_ON_ERROR = "popup_error";
	
	public static final String PREF_FILTER_SUCCESS = "filter_success";
	public static final String PREF_FILTER_FAIL = "filter_fail";
	public static final String PREF_FILTER_FAIL_TEST = "filter_fail_test";
	public static final String PREF_FILTER_NO_BUILD = "filter_no_build";

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
}
