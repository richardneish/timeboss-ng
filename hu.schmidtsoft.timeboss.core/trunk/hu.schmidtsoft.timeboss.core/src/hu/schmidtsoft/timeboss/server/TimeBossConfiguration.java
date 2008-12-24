package hu.schmidtsoft.timeboss.server;

import hu.schmidtsoft.timeboss.server.localfile.UtilDom4j;

import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class TimeBossConfiguration {
	private long timeoutForActivityCheck=15;
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
	private long sleepTime=1;
	private String serverType="local";
	public byte[] toXml() throws IOException
	{
		Document doc=DocumentHelper.createDocument();
		Element root=doc.addElement("timeboss-configuration");
		root.addElement("sleepTime").setText(""+sleepTime);
		root.addElement("timeout").setText(""+timeoutForActivityCheck);
		return UtilDom4j.write(doc);
	}
	public void fromXml(byte[] bytes) throws IOException, DocumentException
	{
		Document doc=UtilDom4j.read(bytes);
		sleepTime=UtilDom4j.getLong(doc, "//sleepTime" );
		timeoutForActivityCheck=UtilDom4j.getLong(doc, "//timeout" );
	}
}
