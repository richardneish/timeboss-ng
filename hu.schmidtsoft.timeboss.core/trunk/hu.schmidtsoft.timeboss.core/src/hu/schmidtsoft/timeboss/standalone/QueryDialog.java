package hu.schmidtsoft.timeboss.standalone;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Standalone dialog for initiating a search on a Wings model. This dialog is
 * popped up when activating <code>WingsSearchAction</code>
 * 
 * @author rizsi
 * 
 */
public class QueryDialog extends Dialog {

	protected QueryDialog(Shell parentShell) {
		super(parentShell, SWT.SYSTEM_MODAL);
		setText("Please fill your next "+TimeBossApplication.applicatonName+" entry!");
	}
	Shell shell;
	public Shell create()
	{
		Shell parent = getParent();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.SYSTEM_MODAL);
		shell.setText(getText());
		return shell;
	}
	public Object open() {
		Shell parent = getParent();
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
		Button b=new Button(shell, SWT.NO);
		b.setText("Ok");
		b.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
				getParent().setVisible(true);
			}});
		b=new Button(shell, SWT.NO);
		b.setText("Don't bother me now, Later!");
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

}
