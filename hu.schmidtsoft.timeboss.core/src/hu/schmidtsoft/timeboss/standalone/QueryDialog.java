package hu.schmidtsoft.timeboss.standalone;

import hu.schmidtsoft.timeboss.server.TimeBossInstance;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * Standalone dialog for initiating a search on a Wings model. This dialog is
 * popped up when activating <code>WingsSearchAction</code>
 * 
 * @author rizsi
 * 
 */
public class QueryDialog extends JDialog {
	TimeBossInstance instance;
	private static final long serialVersionUID = 1L;
	protected QueryDialog(JFrame parentShell, TimeBossInstance instance) {
		super(parentShell, true);
		this.instance=instance;
		setTitle("Please fill your next "+TimeBossApplication.applicatonName+" entry!");
		doDialogCode();
		pack();
	}
	private void doDialogCode() {
		setLayout(new GridBagLayout());
		JButton b=new JButton("Ok");
		add(b);
		b.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				QueryDialog.this.setVisible(false);
				getParent().setVisible(true);
				instance.getServer().alertOpened();
			}});
		b=new JButton("Don't bother me now, Later!");
		add(b);
		b.setSelected(true);
		b.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				QueryDialog.this.setVisible(false);
				instance.getServer().alertOpened();
			}});
	}
}
