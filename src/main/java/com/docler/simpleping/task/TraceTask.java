package com.docler.simpleping.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class TraceTask implements Callable<String> {

	private final String os = System.getProperty("os.name").toLowerCase();
	private String host;

	public TraceTask(String host) {
		this.host = host;
	}

	@Override
	public String call() {
		System.out.println("Running trace for: " + host);
		String route = "";
		String errors = "";
		try {
			InetAddress inet = InetAddress.getByName(host);
			Process traceRt;
			if (os.contains("win"))
				traceRt = Runtime.getRuntime().exec("tracert " + inet.getHostAddress());
			else
				traceRt = Runtime.getRuntime().exec("traceroute " + inet.getHostAddress());
			route = convertStreamToString(traceRt.getInputStream());

			errors = convertStreamToString(traceRt.getErrorStream());
		} catch (IOException e) {
			System.out.println(errors);
		}
		System.out.println("END trace for: " + host);
		return  "\"trace\":\"" + route +"\"";
	}

	private String convertStreamToString(InputStream inputStream) {
		String result = new BufferedReader(new InputStreamReader(inputStream)).lines()
				.collect(Collectors.joining());
		return result;
	}

}
