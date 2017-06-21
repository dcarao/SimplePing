package com.docler.simpleping.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class PingIcmpTask implements Callable<String> {

	private String host;
	private int times;

	public PingIcmpTask(String host, int times) {
		this.host = host;
		this.times = times;
	}

	@Override
	public String call() {
		System.out.println("Running Ping (ICMP) for: " + host);
		String command = "";
		String errors = "";
		try {
			InetAddress inet = InetAddress.getByName(host);
			Process process;
			
				process = Runtime.getRuntime().exec("ping -n " + times + " " +inet.getHostAddress());
			
			command = convertStreamToString(process.getInputStream());

			errors = convertStreamToString(process.getErrorStream());
		} catch (IOException e) {
			System.out.println(errors);
		}
		System.out.println("END Ping (ICMP) for: " + host);
		return  "\"icmp_ping\":\"" + command +"\"";
	}

	private String convertStreamToString(InputStream inputStream) {
		String result = new BufferedReader(new InputStreamReader(inputStream)).lines()
				.collect(Collectors.joining());
		return result;
	}

}
