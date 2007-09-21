package dk.contix.eclipse.hudson.views.actions;


import org.eclipse.jface.action.Action;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.StatusLineContributionItem;

import dk.contix.eclipse.hudson.Activator;
import dk.contix.eclipse.hudson.Job;


public class BuildStatusAction extends StatusLineContributionItem {
	
	public BuildStatusAction() {
		super(Activator.PLUGIN_ID + ".statusline");

		setImage(Activator.getImageDescriptor("icons/hudson.png").createImage());
		setToolTipText("Hudson status");
		
		setActionHandler(new Action() {
			public void run() {
				try {
					Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(Activator.PLUGIN_ID + ".views.HudsonView");
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void setOk() {
		setImage(Activator.getImageDescriptor("icons/blue.png").createImage());
		setToolTipText("All builds ok");
	}
	
	public void setUnknown() {
		setImage(Activator.getImageDescriptor("icons/yellow.png").createImage());
		setToolTipText("Unknown status");
	}
	
	public void setError(Job job) {
		setImage(Activator.getImageDescriptor("icons/red.png").createImage());
		setToolTipText("Build error in " + job.getName());
	}
}
