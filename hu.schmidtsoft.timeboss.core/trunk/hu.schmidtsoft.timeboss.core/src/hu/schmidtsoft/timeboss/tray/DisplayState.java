package hu.schmidtsoft.timeboss.tray;

import hu.schmidtsoft.timeboss.standalone.TimeBossApplication;

public class DisplayState {
	public enum State
	{
		what,
		work,
		rest
	}
	public DisplayState() {
		
	}
	
	public DisplayState(State state, String toolTipText) {
		super();
		this.state = state;
		this.toolTipText = toolTipText;
	}
	public State state=State.what;
	public String toolTipText = TimeBossApplication.applicatonName+" application";
}
