package hu.schmidtsoft.timeboss.tray;

import hu.schmidtsoft.timeboss.standalone.TimeBossApplication;
import hu.schmidtsoft.timeboss.tray.DisplayState.State;
import hu.schmidtsoft.timeboss.util.UtilEvent;
import hu.schmidtsoft.timeboss.util.UtilEventListener;

import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.JFrame;

public class TrayIcon {

	private static final String ICON_IMAGE_WORK = "icon-work.png";
	private static final String ICON_IMAGE_REST = "icon-rest.png";
	private static final String ICON_IMAGE_WHAT = "icon-what.png";
	static Logger log = Logger.getLogger(TrayIcon.class.getName());

	private static Image imageWork;
	private static Image imageRest;
	private static Image imageWhat;

	public synchronized static Image getIconWork() {
		if (imageWork == null) {
			imageWork = 
				Toolkit.getDefaultToolkit()
			.getImage(TrayIcon.class.getResource(ICON_IMAGE_WORK)); 
		}
		return imageWork;
	}

	public synchronized static Image getIconWhat() {
		if (imageWhat == null) {
			imageWhat =
				Toolkit.getDefaultToolkit()
				.getImage(TrayIcon.class.getResource(ICON_IMAGE_WHAT)); 
		}
		return imageWhat;
	}

	public synchronized static Image getIconRest() {
		if (imageRest == null) {
			imageRest =
				Toolkit.getDefaultToolkit()
				.getImage(TrayIcon.class.getResource(ICON_IMAGE_REST)); 
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

	private void updateLabel(java.awt.TrayIcon item) {
		item.setImage(getIcon(state.state));
		item.setToolTip(state.toolTipText);
	}

	public static Image getIcon(State state) {
		switch (state) {
		case rest:
			return getIconRest();
		case what:
			return getIconWhat();
		case work:
			return getIconWork();
		}
		return null;
	}

	DisplayState state = new DisplayState();

	public void showTray() {
		UtilEvent setTitleEvent = new UtilEvent();
		setTitleEvent.addListener(new UtilEventListener() {
			@Override
			public void eventHappened(Object msg) {
				state = (DisplayState) msg;
				if (item != null) {
					updateLabel(item);
				}
			}
		});
		JFrame frame=TimeBossApplication.initApplication(setTitleEvent);
		mainThread(frame, setTitleEvent);
	}

	java.awt.TrayIcon item;

	Thread trayThread;
	private boolean exit=false;
	Thread th;
	boolean listenerAdded=false;
	boolean iconAdded=false;

	private void mainThread(final JFrame frame, UtilEvent setTitleEvent) {
		th=Thread.currentThread();
		final Image image = Toolkit.getDefaultToolkit()
		.getImage(TrayIcon.class.getResource("icon-work.png"));

		
		if (SystemTray.isSupported()) {
			// SystemTray tray = SystemTray.getSystemTray();
			while (!exit) {
				SystemTray tray = SystemTray.getSystemTray();
				if (tray != null) {
					if(!listenerAdded)
					{
						tray.addPropertyChangeListener("trayIcons", new PropertyChangeListener(){
							@Override
							public void propertyChange(PropertyChangeEvent evt) {
								th.interrupt();
							}});
						listenerAdded=true;
					}
					java.awt.TrayIcon[] icons = tray.getTrayIcons();
					if (icons == null || icons.length < 1) {
						iconAdded=false;
						try {
							java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image, "Tester2");
							trayIcon.setImageAutoSize(true);
//							trayIcon.addActionListener(new Li(trayIcon));
							trayIcon.addMouseListener(new MouseAdapter(){

								@Override
								public void mouseClicked(MouseEvent e) {
									frame.setVisible(!frame.isVisible());
								}
							});
							tray.add(trayIcon);
							item=trayIcon;
							updateLabel(trayIcon);
							iconAdded=true;
						} catch (Exception e) {
							log.log(Level.SEVERE,
									"Error restarting tray item", e);
						}
					}
				}
				try
				{
					if(iconAdded)
					{
						Thread.sleep(60000);
					}else
					{
						Thread.sleep(4000);
					}
				}catch(InterruptedException e){}
			}
		}
	}
}
