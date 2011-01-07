package hu.schmidtsoft.timeboss.server;

import hu.schmidtsoft.timeboss.server.localfile.UtilDom4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class TimeBossConfiguration {
	private static final int RECENT_PROJECT_COUNT = 5;
	private static final int RECENT_TITLE_COUNT = 5;
	private static final int RECENT_ISSUE_COUNT = 5;

	private long timeoutForActivityCheck=15;
	private long sleepTime=1;
	private String serverType="local";
	private List<String> projects=new ArrayList<String>();
	private List<String> titles=new ArrayList<String>();
	private List<String> issues=new ArrayList<String>();

	public long getTimeoutForActivityCheck() {
		return timeoutForActivityCheck;
	}
	public void setTimeoutForActivityCheck(long timeoutForActivityCheck) {
		this.timeoutForActivityCheck = timeoutForActivityCheck;
	}

	public long getSleepTime() {
		return sleepTime;
	}
	public void setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
	}

	public String getServerType() {
		return serverType;
	}
	public void setServerType(String serverType) {
		this.serverType = serverType;
	}

	public List<String> getProjects() {
		return projects;
	}

	public final void addProject(final String project)
	{
		addToRecentList(projects, project, RECENT_PROJECT_COUNT);
	}

	public List<String> getActivityTitles() {
		return titles;
	}

	public final void addActivityTitle(final String title)
	{
		addToRecentList(titles, title, RECENT_TITLE_COUNT);
	}

	public List<String> getIssues() {
		return issues;
	}

	public final void addIssue(final String issue)
	{
		addToRecentList(issues, issue, RECENT_ISSUE_COUNT);
	}

	private final void addToRecentList(final List<String> list, final String item, final int maxItems)
	{
		// Remove and re-add the item, so it bumps to the front of the list.
		list.remove(item);
		list.add(0, item);
		
		// Trim recent list if needed.
		if (list.size() > maxItems) {
			final List<String> recentItems = new ArrayList<String>(maxItems);
			int i = 0;
			for (final String recentItem : list)
			{
				recentItems.add(recentItem);
				if (++i >= maxItems)
				{
					break;
				}
			}
			list.retainAll(recentItems);
		}
	}

	public byte[] toXml() throws IOException
	{
		Document doc=DocumentHelper.createDocument();
		Element root=doc.addElement("timeboss-configuration");
		root.addElement("sleepTime").setText(""+sleepTime);
		root.addElement("timeout").setText(""+timeoutForActivityCheck);
		Element recentProjects = root.addElement("recentProjects");
		for (String project : projects)
		{
			recentProjects.addElement("project").addAttribute("name", project);
		}
		Element recentActivityTitles = root.addElement("recentActivityTitles");
		for (String title : titles)
		{
			recentActivityTitles.addElement("title").addAttribute("name", title);
		}
		Element recentIssues = root.addElement("recentIssues");
		for (String issue : issues)
		{
			recentIssues.addElement("issue").addAttribute("name", issue);
		}
		return UtilDom4j.write(doc);
	}

	public void fromXml(byte[] bytes) throws IOException, DocumentException
	{
		Document doc=UtilDom4j.read(bytes);
		sleepTime=UtilDom4j.getLong(doc, "//sleepTime" );
		timeoutForActivityCheck=UtilDom4j.getLong(doc, "//timeout" );
		projects.clear();
		for (String project : UtilDom4j.getStrings(doc, "//recentProjects/project/@name" ))
		{
			projects.add(project);
		}
		titles.clear();
		for (String title : UtilDom4j.getStrings(doc, "//recentActivityTitles/title/@name" ))
		{
			titles.add(title);
		}
		issues.clear();
		for (String issue : UtilDom4j.getStrings(doc, "//recentIssues/issue/@name" ))
		{
			issues.add(issue);
		}
	}
}
