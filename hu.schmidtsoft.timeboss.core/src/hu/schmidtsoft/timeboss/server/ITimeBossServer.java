package hu.schmidtsoft.timeboss.server;

import java.io.IOException;
import java.util.List;

public interface ITimeBossServer {
	byte[] getPreferences() throws IOException;
	void setPreferences(byte[] prefs);
	long getLastAlertTime();
	void alertOpened();
	Activity getLastActivity();
	Activity getLastRealActivity();
	void saveActivity(Activity activity) throws IOException;
	List<Activity> getAllActivitiesLogged();
}
