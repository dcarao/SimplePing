package com.docler.simpleping.dto;
/**
 * DTO to store the properties used in the application
 * 
 * @author dcarao
 *
 */
public class ConfigDto {

	private String hosts;
	private int times;
	private long scheduleDelay;
	private int timeout;
	private String url;
	private String logFile;

	public String getHosts() {
		return hosts;
	}

	public void setHosts(String hosts) {
		this.hosts = hosts;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public long getScheduleDelay() {
		return scheduleDelay;
	}

	public void setScheduleDelay(long scheduleDelay) {
		this.scheduleDelay = scheduleDelay;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLogFile() {
		return logFile;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("hosts=").append(hosts).append("\n");
		sb.append("times=").append(times).append("\n");
		sb.append("scheduleDelay=").append(scheduleDelay).append("\n");
		sb.append("timeout=").append(timeout).append("\n");
		sb.append("url=").append(url).append("\n");
		sb.append("logFile=").append(logFile);
		return sb.toString();
	}

}
