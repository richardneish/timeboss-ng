package hu.schmidtsoft.timeboss.standalone;

import java.util.logging.Level;
import java.util.logging.Logger;

import hu.schmidtsoft.timeboss.server.TimeBossConfiguration;
import hu.schmidtsoft.timeboss.server.TimeBossInstance;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Standalone dialog for initiating a search on a Wings model. This dialog is
 * popped up when activating <code>WingsSearchAction</code>
 * 
 * @author rizsi
 * 
 */
public class ConfigureDialog extends Dialog {
	TimeBossInstance instance;
	protected ConfigureDialog(Shell parentShell, TimeBossInstance instance) {
		super(parentShell, SWT.SYSTEM_MODAL);
		this.instance=instance;
		setText("Set parameters of "+TimeBossApplication.applicatonName);
	}
	Shell shell;
	public Object open() {
		Shell parent = getParent();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.SYSTEM_MODAL);
		shell.setText(getText());
		// Your code goes here (widget creation, set result, etc).
		
		doDialogCode(shell);

		shell.pack();
		shell.open();
		shell.forceActive();
		shell.forceFocus();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return null;
	}

	private void doDialogCode(final Shell shell) {
		shell.setLayout(new GridLayout());
		Label l=new Label(shell, SWT.NO);
		l.setText(getText());
		l=new Label(shell, SWT.NO);
		l.setText("Default interval (min):");
		final Text interval=new Text(shell, SWT.NO);
		interval.setLayoutData(new GridData(SWT.FILL, SWT.NO, true, false));
		interval.setText(""+instance.getConfiguration().getTimeoutForActivityCheck());
		l=new Label(shell, SWT.NO);
		l.setText("sleep time (min):");
		final Text sleepTime=new Text(shell, SWT.NO);
		sleepTime.setLayoutData(new GridData(SWT.FILL, SWT.NO, true, false));
		sleepTime.setText(""+instance.getConfiguration().getSleepTime());
		Button b=new Button(shell, SWT.NO);
		b.setText("Save");
		b.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			@Override
			public void widgetSelected(SelectionEvent e) {
				TimeBossConfiguration conf=instance.getConfiguration();
				conf.setSleepTime(
						parseLong(sleepTime.getText(), conf.getSleepTime()));
				conf.setTimeoutForActivityCheck(
						parseLong(interval.getText(), conf.getTimeoutForActivityCheck()));
				try {
					instance.getServer().setPreferences(conf.toXml());
					instance.getChangedEvent().eventHappened(null);
				} catch (Exception e1) {
					Logger.getLogger(ConfigureDialog.class.getName()).log(Level.SEVERE, "Setting preferences", e1);
				}
				shell.dispose();
			}});
		b=new Button(shell, SWT.NO);
		b.setText("Cancel");
		b.setFocus();
		b.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}});
	}
	long parseLong(String txt, long def)
	{
		try
		{
			return Long.parseLong(txt);
		}
		catch(Exception e)
		{
			return def;
		}
	}

}
