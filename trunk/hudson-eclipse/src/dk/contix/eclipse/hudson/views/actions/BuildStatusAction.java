package dk.contix.eclipse.hudson.views.actions;


import org.eclipse.jface.action.Action;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.StatusLineContributionItem;

import dk.contix.eclipse.hudson.Activator;
import dk.contix.eclipse.hudson.Job;


public class BuildStatusAction extends StatusLineContributionItem {
	
	public BuildStatusAction() {
		super(Activator.PLUGIN_ID + ".statusline");

		setImage(Activator.getImage("icons/hudson.png"));
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
		setImage(Activator.getImage("icons/blue.png"));
		setToolTipText("All builds ok");
	}
	
	public void setUnknown() {
		setImage(Activator.getImage("icons/yellow.png"));
		setToolTipText("Unknown status");
	}
	
	public void setError(Job job) {
		setImage(Activator.getImage("icons/red.png"));
		setToolTipText("Build error in " + job.getName());
	}
}
