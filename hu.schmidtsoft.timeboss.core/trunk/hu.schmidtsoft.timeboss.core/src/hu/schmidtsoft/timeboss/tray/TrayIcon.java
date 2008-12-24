package hu.schmidtsoft.timeboss.tray;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import hu.schmidtsoft.timeboss.standalone.TimeBossApplication;
import hu.schmidtsoft.timeboss.tray.DisplayState.State;
import hu.schmidtsoft.timeboss.util.UtilEvent;
import hu.schmidtsoft.timeboss.util.UtilEventListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

public class TrayIcon {

	private static final String ICON_IMAGE_WORK = "icon-work.png";
	private static final String ICON_IMAGE_REST = "icon-rest.png";
	private static final String ICON_IMAGE_WHAT = "icon-what.png";
	static Logger log = Logger.getLogger(TrayIcon.class.getName());

	private static Image imageWork;
	private static Image imageRest;
	private static Image imageWhat;

	public synchronized static Image getIconWork(Display display) {
		if (imageWork == null) {
			imageWork = new Image(display, TrayIcon.class
					.getResourceAsStream(ICON_IMAGE_WORK));
		}
		return imageWork;
	}

	public synchronized static Image getIconWhat(Display display) {
		if (imageWhat == null) {
			imageWhat = new Image(display, TrayIcon.class
					.getResourceAsStream(ICON_IMAGE_WHAT));
		}
		return imageWhat;
	}

	public synchronized static Image getIconRest(Display display) {
		if (imageRest == null) {
			imageRest = new Image(display, TrayIcon.class
					.getResourceAsStream(ICON_IMAGE_REST));
		}
		return imageRest;
	}

	public static void main(String[] args) {
		try {
			try {
				boolean append = true;

				File dir = new File(System.getProperty("user.home"));
				dir = new File(dir, TimeBossApplication.applicatonDir);
				dir.mkdirs();

				FileHandler handler = new FileHandler("%h/"
						+ TimeBossApplication.applicatonDir + "/javaLog%g.log",
						1000000, 10, append);
				handler.setFormatter(new SimpleFormatter());
				Logger logger = Logger.getLogger("");
				logger.addHandler(handler);
				log.log(Level.INFO, TimeBossApplication.applicatonName
						+ " started");
			} catch (IOException e) {
				System.err.println("Cannot create log file");
				e.printStackTrace();
			}

			TrayIcon test = new TrayIcon();
			test.showTray();
		} catch (Throwable t) {
			Logger.getLogger(TrayIcon.class.getName()).log(Level.SEVERE,
					"Main thread exited by throwable", t);
		}
	}

	Shell shell;

	private void updateLabel(TrayItem item) {
		item.setImage(getIcon(item.getDisplay(), state.state));
		item.setToolTipText(state.toolTipText);
	}

	public static Image getIcon(Display display, State state) {
		switch (state) {
		case rest:
			return getIconRest(display);
		case what:
			return getIconWhat(display);
		case work:
			return getIconWork(display);
		}
		return null;
	}

	DisplayState state = new DisplayState();

	public void showTray() {
		final Display display = new Display();
		shell = new Shell(display);
		UtilEvent setTitleEvent = new UtilEvent();
		setTitleEvent.addListener(new UtilEventListener() {
			@Override
			public void eventHappened(Object msg) {
				state = (DisplayState) msg;
				if (item != null) {
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							if (item != null) {
								updateLabel(item);
							}
						}
					});
				}
			}
		});
		TimeBossApplication.initApplication(shell, setTitleEvent);
		initTray(display, shell, setTitleEvent);
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
		if (trayThread != null) {
			trayThread.interrupt();
		}
	}

	TrayItem item;

	Thread trayThread;

	private void initTray(final Display display, final Shell shell,
			UtilEvent setTitleEvent) {
		// Thread that re-opens tray icon every minute is started
		// as a workaround for bug:
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=259457
		trayThread = new Thread(new Runnable() {
			int sleepTime = 1000;

			@Override
			public void run() {
				while (!shell.isDisposed()) {
					shell.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							try {
								if (item != null) {
									TrayItem old = item;
									item = null;
									old.dispose();
								}
								if (!display.isDisposed()) {
									Tray tray = display.getSystemTray();
									if (tray != null) {
										item = new TrayItem(tray, SWT.NONE);
										updateLabel(item);
										item
												.addSelectionListener(getSelectionListener(display));
										sleepTime = 60000;
									}
								}
							} catch (Throwable t) {
								log.log(Level.SEVERE,
										"Error restarting tray item", t);
							}
						}
					});
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
				}

			}
		}, "Tray icon restarter");
		trayThread.start();
	}

	private SelectionListener getSelectionListener(final Display display) {
		return new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent event) {
				widgetSelected(event);
			}

			public void widgetSelected(SelectionEvent event) {
				shell.setVisible(!shell.getVisible());
			}
		};
	}
}
