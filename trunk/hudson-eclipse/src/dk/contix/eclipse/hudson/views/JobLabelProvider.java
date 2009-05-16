package dk.contix.eclipse.hudson.views;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import dk.contix.eclipse.hudson.Job;

public class JobLabelProvider extends LabelProvider implements ITableLabelProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		Job j = (Job) element;
		if (columnIndex == 2 && j.getStatus() != null) {
			return j.getStatus().getImage();
		}
		if (columnIndex == 3 && j.getHealth() != null) {
			return j.getHealth().getImage();
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		Job j = (Job) element;
		switch (columnIndex) {
			case 0:
				return j.getName();
			case 1:
				if (j.getLastBuild() == null) {
					return "No build";
				}
				return "Build " + j.getLastBuild();
		}
		return null;
	}
}
