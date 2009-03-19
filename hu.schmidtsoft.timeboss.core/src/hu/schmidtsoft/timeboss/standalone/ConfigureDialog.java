package hu.schmidtsoft.timeboss.standalone;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import hu.schmidtsoft.timeboss.server.TimeBossConfiguration;
import hu.schmidtsoft.timeboss.server.TimeBossInstance;

/**
 * 
 * @author rizsi
 * 
 */
public class ConfigureDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	TimeBossInstance instance;
	protected ConfigureDialog(JFrame parentShell, TimeBossInstance instance) {
		super(parentShell, true);
		this.instance=instance;
		setTitle("Set parameters of "+TimeBossApplication.applicatonName);
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
		createLabel(ret, "Default interval (min):");
		final JTextField interval=createText(ret,""+instance.getConfiguration().getTimeoutForActivityCheck());
		createLabel(ret, "sleep time (min):");
		final JTextField sleepTime=createText(ret,""+instance.getConfiguration().getSleepTime());
		JButton b=createButton(ret, "Save");
		b.setText("Save");
		b.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
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
				ConfigureDialog.this.dispose();
			}});
		b=createButton(ret, "Cancel");
		b.setSelected(true);
		b.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				ConfigureDialog.this.dispose();
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
	private JTextField createText(JComponent c, String string) {
		JTextField ret=new JTextField(string);
		createGridConstraint(c, ret);
		c.add(ret);
		return ret;
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
	private JButton createButton(JComponent ret, String string) {
		JButton b=new JButton(string);
		createGridConstraint(ret, b);
		ret.add(b);
		return b;
	}

}
