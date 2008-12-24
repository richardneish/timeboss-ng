package hu.schmidtsoft.timeboss.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that manages the listeners and firing of an event.
 * Features:
 *  - multithread access to listeners is allowed
 *  - listeners and events can be chained. 
 * @author rizsi
 *
 */
public class UtilEvent implements UtilEventListener {
	private List<UtilEventListener> listeners=new ArrayList<UtilEventListener>();
	public void addListener(UtilEventListener l)
	{
		synchronized (listeners) {
			listeners.add(l);
		}
	}
	public void removeListener(UtilEventListener l)
	{
		synchronized (listeners) {
			listeners.remove(l);
		}
	}
	public void eventHappened(Object msg)
	{
		List<UtilEventListener> ls;
		synchronized (listeners) {
			ls=new ArrayList<UtilEventListener>(listeners);
		}
		for(UtilEventListener l:ls)
		{
			l.eventHappened(msg);
		}
	}
}
