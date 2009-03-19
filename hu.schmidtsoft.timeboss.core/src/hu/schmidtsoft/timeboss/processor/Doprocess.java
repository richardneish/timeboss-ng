package hu.schmidtsoft.timeboss.processor;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import com.csvreader.CsvWriter;

import hu.schmidtsoft.timeboss.server.Activity;
import hu.schmidtsoft.timeboss.server.TimeBossInstance;
import hu.schmidtsoft.timeboss.server.localfile.UtilFile;
import hu.schmidtsoft.timeboss.standalone.TimeBossApplication;
import hu.schmidtsoft.timeboss.util.UtilEvent;
import hu.schmidtsoft.timeboss.util.UtilTime;

public class Doprocess {

	public static void main(String[] args) {
		try {
			new Doprocess().run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void run() throws IOException {
		UtilEvent setTitleEvent = new UtilEvent();
		TimeBossInstance instance = TimeBossApplication
				.createInstance(setTitleEvent);
		String csv = processEntries(instance);
		UtilFile.saveFile(new File("/home/rizsi/mysheet.txt"), csv);
	}

	public String processEntries(TimeBossInstance instance) throws IOException {
		List<Activity> activities = instance.getServer()
				.getAllActivitiesLogged();
		filterActivities(activities);
		// for (Activity activity : activities) {
		// if (activity.processed.getTo() == activity.processed.getFrom()) {
		// System.out.print("DELETED");
		// }
		// System.out.println("" + activity.getProject() + " "
		// + UtilTime.timeToString(activity.processed.getFrom()) + " "
		// + UtilTime.timeToString(activity.processed.getTo()));
		// }
		StringWriter sw = new StringWriter();
		CsvWriter writer = new CsvWriter(sw, ';');
		writer.writeRecord(new String[] { "user", "project", "title", "issue",
				"description", "from (yyyy-mm-dd-hh:mm:ss)", "to",
				"timezone of dates", "length" });
		for (Activity activity : activities) {
			long diff = activity.processed.getTo()
					- activity.processed.getFrom();
			if (activity.getWork() && diff > 0) {
				diff /= 1000;
				if (diff > 10) {
					diff /= 60;
					writer
							.writeRecord(new String[] {
									activity.getUser(),
									activity.getProject(),
									activity.getTitle(),
									activity.getIssue(),
									activity.getDescription(),
									UtilTime.timeToCSVString(activity.processed
											.getFrom()),
									UtilTime.timeToCSVString(activity.processed
											.getTo()),
									""
											+ UtilTime.getDefaultTimeZone()
													.getDisplayName(),
									"" + diff });
				}
			}
		}
		return sw.toString();
	}

	/**
	 * 
	 * @param activities
	 *            all activities are ordered as they were created
	 */
	private void filterActivities(List<Activity> activities) {
		for (int i = 0; i < activities.size(); ++i) {
			Activity current = activities.get(i);
			long from = current.getStartTime()
					- UtilTime.minToMilli(current.getPre());
			long to = current.getStartTime()
					+ UtilTime.minToMilli(current.getLength());
			current.processed.setFrom(from);
			current.processed.setTo(to);

		}
		for (int i = 0; i < activities.size(); ++i) {
			Activity current = activities.get(i);
			for (int prevCtr = i - 1; prevCtr >= 0; --prevCtr) {
				Activity previous = activities.get(prevCtr);
				if (previous.processed.getTo() <= current.processed.getFrom()) {
					break;
				}
				previous.processed.setTo(current.processed.getFrom());
			}
		}
	}
}
