package hu.schmidtsoft.timeboss.standalone;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Standalone dialog for initiating a search on a Wings model. This dialog is
 * popped up when activating <code>WingsSearchAction</code>
 * 
 * @author rizsi
 * 
 */
public class TextDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	public void setText(String text) {
		setTitle(text);
	}

	JTextArea area;
	protected TextDialog(JFrame parentShell) {
		super(parentShell, true);
		setLocationRelativeTo(parentShell);
		area=createTextBig(this, "");
		pack();
	}
	private JTextArea createTextBig(JDialog c, String string) {

		JTextArea ret=new JTextArea(string);
		ret.setColumns(80);
		ret.setLineWrap(true);
		ret.setWrapStyleWord(true);
		ret.setEditable(false);
		JScrollPane scroller = new JScrollPane(ret);
		c.add(scroller);
		return ret;
	}

	public void setContent(String content) {
		area.setText(content);
		pack();
	}
}
