package hu.schmidtsoft.timeboss.standalone;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.concurrent.Future;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * dialog that shows "processing..." for long time processings
 * 
 * @author rizsi
 * 
 */
public class ProcessingDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	boolean closed=false;
	
	public synchronized boolean isClosed() {
		return closed;
	}
	public synchronized void setClosed(boolean closed) {
		this.closed = closed;
	}
	protected ProcessingDialog(JFrame parentShell) {
		super(parentShell, true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setTitle("Processing...");
		doDialogCode();
		pack();
		setLocationRelativeTo(parentShell);
	}
	int gridX=0;
	int gridY=0;
	private void doDialogCode() {
		JPanel panel=new JPanel();
		panel.setLayout(new GridBagLayout());
		add(panel);
		JPanel ret=panel;
		createLabel(ret, "Processing...");
	}
	private JLabel createLabel(JComponent ret, String string) {
		JLabel l=new JLabel(string);
		createGridConstraint(ret, l);
		ret.add(l);
		return l;
	}
	private void createGridConstraint(JComponent ret, JComponent l) {
		GridBagConstraints c=new GridBagConstraints();
		c.gridx=gridX;
		c.gridy=gridY;
		c.anchor=GridBagConstraints.FIRST_LINE_START;
		c.fill=GridBagConstraints.HORIZONTAL;
		gridX++;
		if(gridX>1)
		{
			gridX=0;
			gridY++;
		}
		((GridBagLayout)ret.getLayout()).setConstraints(l, 
				c);
	}
}
