package hu.schmidtsoft.timeboss.standalone;

import hu.schmidtsoft.timeboss.server.ITimeBossServer;
import hu.schmidtsoft.timeboss.server.TimeBossConfiguration;
import hu.schmidtsoft.timeboss.server.TimeBossInstance;
import hu.schmidtsoft.timeboss.server.localfile.LocalFileTimeBossServer;
import hu.schmidtsoft.timeboss.server.localfile.UtilFile;
import hu.schmidtsoft.timeboss.tray.DisplayState;
import hu.schmidtsoft.timeboss.tray.TrayIcon;
import hu.schmidtsoft.timeboss.util.UtilEvent;
import hu.schmidtsoft.timeboss.util.UtilEventListener;
import hu.schmidtsoft.timeboss.util.UtilTime;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class TimeBossApplication implements MyRunnable {
	public static final String applicatonName="Timeboss";
	public static final String applicatonDir=".timeboss";
	Shell shell;
	TimeBossInstance instance;
	TimerThread timerThread;
	long epsilon=1000;
	DisplayState state=new DisplayState();
	UtilEvent setTitleEvent;
	public TimeBossApplication(final Shell shell,
			UtilEvent setTitleEvent) {
		this.shell=shell;
		this.setTitleEvent=setTitleEvent;
		setTitleEvent.addListener(new UtilEventListener(){

			@Override
			public void eventHappened(Object msg) {
				state=(DisplayState)msg;
					shell.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							updateLabel(shell);
						}
					});
				}
			});
		updateLabel(shell);
		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				shell.setVisible(false);
				event.doit = false;
			}
		});
		createMenu(shell);
		File dir=new File(System.getProperty("user.home"));
		dir=new File(dir, TimeBossApplication.applicatonDir);
		dir.mkdirs();
		ITimeBossServer server=new LocalFileTimeBossServer(dir, setTitleEvent);
		TimeBossConfiguration config=new TimeBossConfiguration();
		try {
			config.fromXml(server.getPreferences());
		} catch (Exception e) {
			Logger.getLogger(TimeBossApplication.class.getName()).log(Level.WARNING, "Cannot load configuration file", e);
			byte[] bs;
			try {
				bs = config.toXml();
				server.setPreferences(bs);
			} catch (IOException e1) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Saving preferences", e);
			}
		}
		instance=new TimeBossInstance(server,config);
		createContents();
		initTimer();
		instance.getChangedEvent().addListener(new UtilEventListener(){

			@Override
			public void eventHappened(Object msg) {
				timerThread.breakSleep();
			}});
	}
	private void updateLabel(Shell shell) {
	    Image image = TrayIcon.getIcon(shell.getDisplay(), state.state);
		shell.setImage(image);
		shell.setText(""+state.toolTipText);
	}

	private void createContents() {
		shell.setLayout(new FillLayout());
		new TimeBossPage(instance).createControl(shell);
	}
	public static void initApplication(final Shell shell, UtilEvent setTitleEvent) {
		new TimeBossApplication(shell, setTitleEvent);
	}

	ITimeBossServer getServer()
	{
		return instance.getServer();
	}
	TimeBossConfiguration getConfiguration()
	{
		return instance.getConfiguration();
	}
	void initTimer()
	{
		timerThread=new TimerThread(this);
		timerThread.start();
	}
	Shell dialogOpen=null;
	protected void showDialog() {
		shell.getDisplay().asyncExec(new Runnable()
		{

			@Override
			public void run() {
				if(dialogOpen!=null)
				{
					getServer().alertOpened();
					dialogOpen.setVisible(false);
					dialogOpen.setVisible(true);
					dialogOpen.setFocus();
					dialogOpen.setActive();
					dialogOpen.forceFocus();
					return;
				}
				try
				{
					QueryDialog qd=new QueryDialog(shell);
					dialogOpen=qd.create();
					getServer().alertOpened();
					qd.open();
					getServer().alertOpened();
				}finally
				{
					dialogOpen=null;
					timerThread.breakSleep();
				}
			}
		});
	}

	private void createMenu(final Shell shell) {
	    Menu menuBar = new Menu(shell, SWT.BAR);
	    MenuItem fileMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
	    fileMenuHeader.setText("&File");

	    Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
	    fileMenuHeader.setMenu(fileMenu);

//	    MenuItem showDialogItem = new MenuItem(fileMenu, SWT.PUSH);
//	    showDialogItem.setText("Show alert dialog!");
//
//	    showDialogItem.addSelectionListener(new SelectionAdapter(){
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				showDialog();
//			}});
	    
	    MenuItem configure = new MenuItem(fileMenu, SWT.PUSH);
	    configure.setText("Configure");
	    configure.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				openConfigureDialog();
				
			}});

	    MenuItem close = new MenuItem(fileMenu, SWT.PUSH);
	    close.setText("close");
	    close.addSelectionListener(new SelectionAdapter(){
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		shell.setVisible(false);
	    	}
	    });
	    new MenuItem(fileMenu, SWT.SEPARATOR);
	    MenuItem fileExitItem = new MenuItem(fileMenu, SWT.PUSH);
	    fileExitItem.setText("E&xit");

	    fileExitItem.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				timerThread.exit();
				shell.dispose();
			}});
	    
	    MenuItem helpMenuHeader = new MenuItem(menuBar, SWT.CASCADE|SWT.RIGHT);
	    helpMenuHeader.setText("&Help");

	    Menu helpMenu = new Menu(shell, SWT.DROP_DOWN);
	    helpMenuHeader.setMenu(helpMenu);

	    MenuItem about = new MenuItem(helpMenu, SWT.PUSH);
	    about.setText("about");
	    
	    about.addSelectionListener(new SelectionAdapter(){
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		TextDialog td=new TextDialog(shell);
	    		td.setText("About "+TimeBossApplication.applicatonName);
	    		try {
					td.setContent(UtilFile.loadAsString(getClass().getResourceAsStream("about.txt")));
				} catch (IOException e1) {
					Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Loading about box", e);
				}
	    		td.open();
	    	}
	    });

	    MenuItem helpGetHelpItem = new MenuItem(helpMenu, SWT.PUSH);
	    helpGetHelpItem.setText("Show help");
	    
	    helpGetHelpItem.addSelectionListener(new SelectionAdapter(){
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		TextDialog td=new TextDialog(shell);
	    		td.setText("Help "+TimeBossApplication.applicatonName);
	    		try {
					td.setContent(UtilFile.loadAsString(getClass().getResourceAsStream("help.txt")));
				} catch (IOException e1) {
					Logger.getLogger(getClass().getName()).log(Level.SEVERE, "loading help box", e);
				}
	    		td.open();
	    	}
	    });
	    
	    shell.setMenuBar(menuBar);
	}
	
	protected void openConfigureDialog() {
		new ConfigureDialog(shell, instance).open();
	}
	/**
	 * Do alert if necessary or initialize a timer for next alert.
	 * If there is no need for alert, then just don't do anything.
	 */
	@Override
	public long myRun() {
		long lastAlertTime=getServer().getLastAlertTime();
		long lastTime;
		long timeout;
		if(lastAlertTime>0)
		{
			lastTime=lastAlertTime;
			timeout=UtilTime.minToMilli(getConfiguration().getSleepTime());
		}else
		{
			lastTime=getServer().getLastActivity().getStartTime();
			timeout=
				UtilTime.minToMilli(getServer().getLastActivity().getLength());
		}
		if(timeout>0)
		{
			long elapsedTime=(System.currentTimeMillis()-lastTime);
			long remainingTime=timeout-elapsedTime;
			if(remainingTime<epsilon)
			{
				DisplayState state=new DisplayState(DisplayState.State.what, "Please fill your "+TimeBossApplication.applicatonName+" entry!");
				setTitleEvent.eventHappened(state);
				showDialog();
			}else
			{
				return timeout;
			}
		}
		return 10000;
	}
}
