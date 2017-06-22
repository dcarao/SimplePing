package com.docler.simpleping.task;

import java.net.InetAddress;
import java.util.concurrent.Callable;

/**
 * Implementation of the Ping via TCP
 *  
 * @author dcarao
 *
 */
public class PingTcpTask implements Callable<String> {
	private String host;
	private int times;
	private int timeout;

	public PingTcpTask(String host, int times, int timeout) {
		this.host = host;
		this.times = times;
		this.timeout = timeout;
	}

	@Override
	public String call() throws Exception {
		System.out.println("Command: ping (tcp) " + host);
		InetAddress inet;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < times; i++) {
			inet = InetAddress.getByName(host);
			sb.append(" Sending Ping Request to " + inet);
			sb.append(" - ").append(inet.isReachable(timeout) ? "Host is reachable" : "Host is NOT reachable \n");
		}
		System.out.println("End Command: ping (tcp) for: " + host);
		return "\"tcp_ping\":\"" + sb.toString() + "\"";
	}

}
