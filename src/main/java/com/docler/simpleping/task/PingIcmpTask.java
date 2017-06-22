package com.docler.simpleping.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Implementation of the ping command via ICMP
 * 
 * @author dcarao
 *
 */
public class PingIcmpTask implements Callable<String> {

	private String host;
	private int times;

	/**
	 * 
	 * @param host
	 * @param times
	 */
	public PingIcmpTask(String host, int times) {
		this.host = host;
		this.times = times;
	}

	/**
	 * 
	 */
	@Override
	public String call() {
		System.out.println("Command: ping -n " + times + " " + host);
		String command = "";
		String errors = "";
		try {
			InetAddress inet = InetAddress.getByName(host);
			Process process;

			process = Runtime.getRuntime().exec("ping -n " + times + " " + inet.getHostAddress());

			command = convertStreamToString(process.getInputStream());

			errors = convertStreamToString(process.getErrorStream());
		} catch (IOException e) {
			System.out.println(errors);
		}
		System.out.println("End Command: ping (ICMP) for: " + host);
		return "\"icmp_ping\":\"" + command + "\"";
	}

	/**
	 * 
	 * @param inputStream
	 * @return
	 */
	private String convertStreamToString(InputStream inputStream) {
		String result = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());
		return result;
	}

}
