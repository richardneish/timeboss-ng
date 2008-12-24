package hu.schmidtsoft.timeboss.standalone;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Standalone dialog for initiating a search on a Wings model. This dialog is
 * popped up when activating <code>WingsSearchAction</code>
 * 
 * @author rizsi
 * 
 */
public class TextDialog extends Dialog {
	private String content;
	public String getContent() {
		return content;
	}

	protected TextDialog(Shell parentShell) {
		super(parentShell, SWT.SYSTEM_MODAL);
	}
	Shell shell;
	public Object open() {
		Shell parent = getParent();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.SYSTEM_MODAL| SWT.RESIZE);
		shell.setText(getText());
		// Your code goes here (widget creation, set result, etc).
		
		doDialogCode(shell);

		shell.pack();
		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return null;
	}

	private void doDialogCode(final Shell shell) {
		shell.setLayout(new GridLayout());
		Text text=new Text(shell, SWT.MULTI|SWT.WRAP|SWT.V_SCROLL);
		text.setEnabled(true);
		text.setEditable(false);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		text.setText(getContent());
	}

	public void setContent(String content) {
		this.content=content;
	}

}
