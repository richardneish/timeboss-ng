package hu.schmidtsoft.timeboss.standalone;

import hu.schmidtsoft.timeboss.processor.Doprocess;
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

import java.awt.FileDialog;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class TimeBossApplication extends JFrame implements MyRunnable {
	private static final long serialVersionUID = 1L;
	public static final String applicatonName = "Timeboss";
	public static final String applicatonDir = ".timeboss";
	TimeBossInstance instance;
	TimerThread timerThread;
	long epsilon = 1000;
	DisplayState state = new DisplayState();
	UtilEvent setTitleEvent;

	public static TimeBossInstance createInstance(UtilEvent setTitleEvent) {
		File dir = new File(System.getProperty("user.home"));
		dir = new File(dir, TimeBossApplication.applicatonDir);
		dir.mkdirs();
		ITimeBossServer server = new LocalFileTimeBossServer(dir, setTitleEvent);
		TimeBossConfiguration config = new TimeBossConfiguration();
		try {
			config.fromXml(server.getPreferences());
		} catch (Exception e) {
			Logger.getLogger(TimeBossApplication.class.getName()).log(
					Level.WARNING, "Cannot load configuration file", e);
			byte[] bs;
			try {
				bs = config.toXml();
				server.setPreferences(bs);
			} catch (IOException e1) {
				Logger.getLogger(TimeBossApplication.class.getName()).log(
						Level.SEVERE, "Saving preferences", e);
			}
		}
		TimeBossInstance instance = new TimeBossInstance(server, config);
		return instance;
	}

	// QueryDialog dialogOpen;
	public TimeBossApplication(UtilEvent setTitleEvent) {
		this.setTitleEvent = setTitleEvent;
		setTitleEvent.addListener(new UtilEventListener() {

			@Override
			public void eventHappened(Object msg) {
				state = (DisplayState) msg;
				updateLabel();
			}
		});
		updateLabel();
		createMenu();
		instance = createInstance(setTitleEvent);
		createContents();
		pack();
		setLocationRelativeTo(null);
		initTimer();
		instance.getChangedEvent().addListener(new UtilEventListener() {
			@Override
			public void eventHappened(Object msg) {
				timerThread.breakSleep();
			}
		});
	}

	private void updateLabel() {
		Image image = TrayIcon.getIcon(state.state);
		setIconImage(image);
		setTitle("" + state.toolTipText);
		if (timerThread != null) {
			timerThread.interrupt();
		}
	}

	private void createContents() {
		new TimeBossPage(instance).createControl(this);
	}

	public static JFrame initApplication(UtilEvent setTitleEvent) {
		return new TimeBossApplication(setTitleEvent);
	}

	ITimeBossServer getServer() {
		return instance.getServer();
	}

	TimeBossConfiguration getConfiguration() {
		return instance.getConfiguration();
	}

	void initTimer() {
		timerThread = new TimerThread(this);
		timerThread.start();
	}

	static Logger log = Logger.getLogger(TimeBossApplication.class.getName());

	QueryDialog dialogShown;

	public synchronized QueryDialog getDialogShown() {
		if (dialogShown == null) {
			dialogShown = new QueryDialog(this, instance);
		}
		return dialogShown;
	}

	protected void showDialog() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					instance.getServer().alertOpened();
					getDialogShown().setVisible(false);
					getDialogShown().setVisible(true);
					return;
				} catch (Throwable t) {
					log.log(Level.SEVERE, "showing warining", t);
				}
			}
		}).start();
	}

	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenuHeader = new JMenu("File");
		menuBar.add(fileMenuHeader);

		JMenuItem configure = new JMenuItem("Configure");
		fileMenuHeader.add(configure);
		configure.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openConfigureDialog();
			}
		});

		JMenuItem export = new JMenuItem("Export...");
		fileMenuHeader.add(export);
		export.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openExportDialog();
			}
		});

		JMenuItem close = new JMenuItem("close");
		fileMenuHeader.add(close);
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		JMenuItem fileExitItem = new JMenuItem("Exit");
		fileMenuHeader.add(fileExitItem);

		fileExitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				instance.exit();
				System.exit(0);
			}
		});
		JMenu helpMenuHeader = new JMenu("Help");
		menuBar.add(helpMenuHeader);

		JMenuItem about = new JMenuItem("about");
		helpMenuHeader.add(about);

		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TextDialog td = new TextDialog(TimeBossApplication.this);
				td.setText("About " + TimeBossApplication.applicatonName);
				try {
					td.setContent(UtilFile.loadAsString(getClass()
							.getResourceAsStream("about.txt")));
				} catch (IOException e1) {
					Logger.getLogger(getClass().getName()).log(Level.SEVERE,
							"Loading about box", e);
				}
				td.setVisible(true);
			}
		});

		JMenuItem helpGetHelpItem = new JMenuItem("Show help");
		helpMenuHeader.add(helpGetHelpItem);
		helpGetHelpItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TextDialog td = new TextDialog(TimeBossApplication.this);
				td.setText("Help " + TimeBossApplication.applicatonName);
				try {
					td.setContent(UtilFile.loadAsString(getClass()
							.getResourceAsStream("help.txt")));
				} catch (IOException e1) {
					Logger.getLogger(getClass().getName()).log(Level.SEVERE,
							"loading help box", e);
				}
				td.setVisible(true);
			}
		});

		setJMenuBar(menuBar);
	}

	protected void openConfigureDialog() {
		new ConfigureDialog(this, instance).setVisible(true);
	}

	protected void openExportDialog() {
		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			final File f = fc.getSelectedFile();
			try {
				ExecutorService service = Executors.newSingleThreadExecutor();
				try {
					final ProcessingDialog pd = new ProcessingDialog(this);
					Future<?> future = service.submit(new Callable<Object>() {
						@Override
						public Object call() throws Exception {
							String csv = new Doprocess()
									.processEntries(instance);
							UtilFile.saveFile(f, csv);
							while(!pd.isVisible()&&!pd.isClosed())
							{
								Thread.sleep(100);
							}
							pd.setVisible(false);
							return null;
						}
					});
					pd.setVisible(true);
					pd.setClosed(true);
				} finally {
					service.shutdown();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Do alert if necessary or initialize a timer for next alert. If there is
	 * no need for alert, then just don't do anything.
	 */
	@Override
	public long myRun() {
		long lastAlertTime = getServer().getLastAlertTime();
		long lastTime;
		long timeout;
		if (lastAlertTime > 0) {
			lastTime = lastAlertTime;
			timeout = UtilTime.minToMilli(getConfiguration().getSleepTime());
		} else {
			lastTime = getServer().getLastActivity().getStartTime();
			timeout = UtilTime.minToMilli(getServer().getLastActivity()
					.getLength());
		}
		if (timeout >= 0) {
			long elapsedTime = (System.currentTimeMillis() - lastTime);
			long remainingTime = timeout - elapsedTime;
			if (remainingTime < epsilon) {
				DisplayState state = new DisplayState(DisplayState.State.what,
						"Please fill your "
								+ TimeBossApplication.applicatonName
								+ " entry!");
				setTitleEvent.eventHappened(state);
				showDialog();
			} else {
				return timeout;
			}
		}
		return 10000;
	}
}
