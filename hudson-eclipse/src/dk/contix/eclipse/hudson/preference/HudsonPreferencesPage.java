package dk.contix.eclipse.hudson.preference;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.views.IViewDescriptor;

import dk.contix.eclipse.hudson.Activator;
import dk.contix.eclipse.hudson.HudsonClient;

public class HudsonPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private IWorkbench workbench;

	private IntegerFieldEditor interval;

	public HudsonPreferencesPage() {
		super(FieldEditorPreferencePage.GRID);
	}

	protected void createFieldEditors() {
		addField(new HudsonUrlField(getFieldEditorParent()));

		final BooleanFieldEditor enabled = new BooleanFieldEditor(Activator.PREF_AUTO_UPDATE, "Update periodically?", getFieldEditorParent()) {
			protected Button getChangeControl(Composite parent) {
				final Button c = super.getChangeControl(parent);

				c.addSelectionListener(new SelectionListener() {
					public void widgetDefaultSelected(SelectionEvent arg0) {
					}

					public void widgetSelected(SelectionEvent e) {
						interval.setEnabled(c.getSelection(), getFieldEditorParent());
					}

				});
				return c;
			}
		};
		interval = new IntegerFieldEditor(Activator.PREF_UPDATE_INTERVAL, "Update interval (seconds)", getFieldEditorParent());

		addField(enabled);
		addField(interval);

		addField(new BooleanFieldEditor(Activator.PREF_POPUP_ON_ERROR, "Popup window when state changes to error?", getFieldEditorParent()));
		addField(new BooleanFieldEditor(Activator.PREF_POPUP_ON_CONNECTION_ERROR, "Popup error when connection to Hudson fails?", getFieldEditorParent()));

		interval.setEnabled(getPreferenceStore().getBoolean(Activator.PREF_AUTO_UPDATE), getFieldEditorParent());
	}

	public void init(IWorkbench workbench) {
		this.workbench = workbench;
	}

	protected IPreferenceStore doGetPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}

	public void dispose() {
		super.dispose();
	}

	public boolean performOk() {

		IViewDescriptor desc = workbench.getViewRegistry().find(Activator.PLUGIN_ID + ".views.HudsonView");
		if (desc != null) {
			try {
				IViewPart part = desc.createView();
				System.out.println(part);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return super.performOk();
	}

	private class HudsonUrlField extends StringButtonFieldEditor {
		public HudsonUrlField(Composite parent) {
			init(Activator.PREF_BASE_URL, "Hudson base url");

			setChangeButtonText("Check url");
			setValidateStrategy(StringButtonFieldEditor.VALIDATE_ON_FOCUS_LOST);
			setEmptyStringAllowed(true);
			setErrorMessage("Invalid url");

			createControl(parent);

			Button button = getChangeControl(parent);
			button.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
				}

				public void focusLost(FocusEvent e) {
					if (isValid()) {
						getPage().setMessage(null);
					}
				}

			});
		}

		protected boolean checkState() {
			try {
				check();
				clearErrorMessage();
				return true;
			} catch (Exception e) {
				showErrorMessage();
				return false;
			}
		}

		private void check() throws Exception {
			if (getStringValue() != null && !"".equals(getStringValue().trim())) {
				new HudsonClient().checkValidUrl(getStringValue());
			}
		}

		protected String changePressed() {
			try {
				check();
				getPage().setMessage("Valid url", FieldEditorPreferencePage.INFORMATION);
				setValid(true);
			} catch (Exception e) {
				showErrorMessage(e.getMessage());
				setValid(false);
			}
			return null;
		}
	}
}
