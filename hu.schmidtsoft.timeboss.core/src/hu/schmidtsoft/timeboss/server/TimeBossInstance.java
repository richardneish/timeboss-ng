package hu.schmidtsoft.timeboss.server;

import hu.schmidtsoft.timeboss.util.UtilEvent;

public class TimeBossInstance {
	private UtilEvent changedEvent=new UtilEvent();
	public UtilEvent getChangedEvent() {
		return changedEvent;
	}
	private ITimeBossServer server;
	private TimeBossConfiguration configuration;
	public TimeBossInstance(ITimeBossServer server,
			TimeBossConfiguration configuration) {
		super();
		this.server = server;
		this.configuration = configuration;
	}
	public ITimeBossServer getServer() {
		return server;
	}
	public TimeBossConfiguration getConfiguration() {
		return configuration;
	}
	public void exit() {
		// TODO Auto-generated method stub
		
	}
}
