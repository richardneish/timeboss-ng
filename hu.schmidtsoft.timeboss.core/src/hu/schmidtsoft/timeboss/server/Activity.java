package hu.schmidtsoft.timeboss.server;

import hu.schmidtsoft.timeboss.server.localfile.UtilDom4j;

import java.io.IOException;
import java.net.MalformedURLException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class Activity {
	private String user;
	private String issue;
	private String project;
	private boolean work;
	private long pre;
	private String title;
	private String description;
	private long startTime;
	private long length;
	public class Processed
	{
		private long from;
		public long getFrom() {
			return from;
		}
		public void setFrom(long from) {
			this.from = from;
		}
		public long getTo() {
			return to;
		}
		public void setTo(long to) {
			this.to = to;
			if(to<from)
			{
				from=to;
			}
		}
		private long to;
	}
	public final Processed processed=new Processed();
	public String getIssue() {
		return issue;
	}
	public String getProject() {
		return project;
	}
	public String getUser() {
		return user;
	}
	public Activity(boolean work,
			String project,
			String title,
			String description,
			String issue,
			long startTime,
			long length, long pre) {
		super();
		this.user=System.getProperty("user.name");
		this.project=project;
		this.work=work;
		this.title = title;
		this.description = description;
		this.startTime = startTime;
		this.length = length;
		this.pre=pre;
		this.issue=issue;
	}
	public Activity(byte[] bytes) throws MalformedURLException, DocumentException {
		fromXml(bytes);
	}
	public long getPre() {
		return pre;
	}
	public boolean getWork() {
		return work;
	}
	public String getTitle() {
		return title;
	}
	public String getDescription() {
		return description;
	}
	public long getStartTime() {
		return startTime;
	}
	public long getLength() {
		return length;
	}
	public byte[] toXml() throws IOException {
		Document doc=DocumentHelper.createDocument();
		Element root=doc.addElement("activity");
		root.addElement("user").setText(user);
		root.addElement("project").setText(project);
		root.addElement("work").setText(""+work);
		root.addElement("title").setText(title);
		root.addElement("description").setText(description);
		root.addElement("start").setText(""+startTime);
		root.addElement("length").setText(""+length);
		root.addElement("pre").setText(""+pre);
		root.addElement("issue").setText(issue);
		return UtilDom4j.write(doc);
	}
	private void fromXml(byte[] bytes) throws MalformedURLException, DocumentException {
		Document doc=UtilDom4j.read(bytes);
		user=UtilDom4j.getString(doc, "//user");
		project=UtilDom4j.getString(doc, "//project");
		work=UtilDom4j.getBoolean(doc, "//work");
		title=UtilDom4j.getString(doc, "//title");
		description=UtilDom4j.getString(doc, "//description");
		startTime=UtilDom4j.getLong(doc, "//start");
		length=UtilDom4j.getLong(doc, "//length");
		pre=UtilDom4j.getLong(doc, "//pre");
		issue=UtilDom4j.getString(doc, "//issue");
	}
}
