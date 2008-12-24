package hu.schmidtsoft.timeboss.standalone;

import hu.schmidtsoft.timeboss.server.Activity;
import hu.schmidtsoft.timeboss.server.ITimeBossServer;
import hu.schmidtsoft.timeboss.server.TimeBossInstance;
import hu.schmidtsoft.timeboss.util.UtilTime;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

public class TimeBossPage {
	TimeBossInstance instance;
	public TimeBossPage(TimeBossInstance instance) {
		super();
		this.instance = instance;
	}
	class SaveActivity extends SelectionAdapter
	{
		@Override
		public void widgetSelected(SelectionEvent e) {
			try
			{
				if(title.getText().length()<1)
				{
					throw new RuntimeException("title must be set!");
				}
				Activity toSave=new Activity(true,
						project.getText(),
						title.getText(),
						description.getText(),
						issue.getText(),
						System.currentTimeMillis(),
						Long.parseLong(length.getText()),
						Long.parseLong(pre.getText())
						);
				instance.getServer().saveActivity(toSave);
			}catch(Exception ex)
			{
				MessageBox mb=new MessageBox(title.getShell());
				String msg="Invalid data, cannot save activity: "+ex.getMessage();
				mb.setText("Error");
				mb.setMessage(msg);
				mb.open();
			}
			updateControls();
		}
	}
	class Reload extends SelectionAdapter
	{
		@Override
		public void widgetSelected(SelectionEvent e) {
			updateControls();
		}
	}
	class SaveActivityNothing extends SelectionAdapter
	{
		@Override
		public void widgetSelected(SelectionEvent e) {
			try
			{
				long time=Long.parseLong(notWorkingTime.getText());
				instance.getServer().saveActivity(new Activity(
						false,
						"",
						"","",
						"",System.currentTimeMillis(), time,
						0));
			}catch(Exception ex)
			{
				MessageBox mb=new MessageBox(title.getShell());
				String msg="Invalid data, cannot save activity: "+ex.getMessage();
				mb.setText("Error");
				mb.setMessage(msg);
				mb.open();
			}
			updateControls();
		}
	}
	Label time;
	Label finishTime;
	Label lastActivity;
	Text notWorkingTime;
	Text title;
	Text description;
	Text length;
	Text pre;
	Text project;
	Text issue;
	public Control createControl(Composite parent)
	{
		ScrolledComposite sret = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		sret.setExpandHorizontal(true);
	    sret.setExpandVertical(true);

		Composite ret=new Composite(sret, SWT.NO);
		GridLayout gl=new GridLayout();
		gl.numColumns=2;
		ret.setLayout(gl);
		createLabel(ret, "Last activity title:");
		lastActivity=createLabel(ret, "");
		createLabel(ret, "Last activity saved at:");
		time=createLabel(ret, "");
		createLabel(ret, "Last activity lasts until:");
		finishTime=createLabel(ret, "");
		createLabel(ret, "project:");
		project=createText(ret, "");
		createLabel(ret, "Activity Title:");
		title=createText(ret, "");
		createLabel(ret, "Issue:");
		issue=createText(ret, "");
		createLabel(ret, "Elapsed Time (up to now in minutes):");
		pre=createText(ret, "0");
		createLabel(ret, "Remaining Time (from now in minutes):");
		length=createText(ret, "");
		createLabel(ret, "Activity Description:");
		description=createTextBig(ret, "");
		createEmpty(ret);
		createButton(ret, "Save activity log").addSelectionListener(new SaveActivity());
		createEmpty(ret);
		createButton(ret, "Reload last activity's data").addSelectionListener(new Reload());
		createButton(ret, "Not working. Don't bother me for minutes: ").addSelectionListener(new SaveActivityNothing());
		notWorkingTime=createText(ret, ""+instance.getConfiguration().getTimeoutForActivityCheck());
		Point p=ret.computeSize(0, 0);
		sret.setMinSize(p.x,p.y);
		sret.setContent(ret);
		updateControls();
		return sret;
	}
	public void updateControls() {
		ITimeBossServer server=instance.getServer();
		Activity last=server.getLastRealActivity();
		Activity lastAny=server.getLastActivity();
		time.setText(""+UtilTime.timeToString(
				lastAny.getStartTime()));
		lastActivity.setText(""+lastAny.getTitle());
		if(!lastAny.getWork())
		{
			lastActivity.setText("REST");
		}
		finishTime.setText(""+UtilTime.timeToString(
				lastAny.getStartTime()<0?-1:
				(lastAny.getStartTime()+
				UtilTime.minToMilli(lastAny.getLength()))));
		description.setText(last.getDescription());
		title.setText(last.getTitle());
		project.setText(last.getProject());
		issue.setText(last.getIssue());
		pre.setText("0");
		if(last.getLength()<0)
		{
			length.setText(""+instance.getConfiguration().getTimeoutForActivityCheck());
		}else
		{
			length.setText(""+last.getLength());
		}
	}
	private void createEmpty(Composite ret)
	{
		new Label(ret, SWT.NO);
	}

	private Text createText(Composite c, String string) {
		Text ret=new Text(c, SWT.NO);
		ret.setText(string);
		ret.setLayoutData(new GridData(
				SWT.FILL, SWT.NO, true, false));
		return ret;
	}
	private Text createTextBig(Composite c, String string) {
		Text ret=new Text(c, SWT.MULTI);
		ret.setText(string);
		ret.setLayoutData(new GridData(
				SWT.FILL, SWT.FILL, true, true));
		return ret;
	}

	private Label createLabel(Composite ret, String string) {
		Label l=new Label(ret, SWT.NO);
		l.setText(string);
		l.setLayoutData(new GridData(
				SWT.FILL, SWT.NO, true, false));
		return l;
	}
	private Button createButton(Composite ret, String string) {
		Button b=new Button(ret, SWT.NO);
		b.setText(string);
		return b;
	}
	
}
