package com.docler.simpleping.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Implementation of the trace command 
 * 
 * @author dcarao
 *
 */
public class TraceTask implements Callable<String> {

	private final String os = System.getProperty("os.name").toLowerCase();
	private String host;

	/**
	 * 
	 * @param host
	 */
	public TraceTask(String host) {
		this.host = host;
	}

	@Override
	public String call() {
		System.out.println("Command: tracert " + host);
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
		System.out.println("End trace for: " + host);
		return  "\"trace\":\"" + route +"\"";
	}

	/**
	 * 
	 * @param inputStream
	 * @return
	 */
	private String convertStreamToString(InputStream inputStream) {
		String result = new BufferedReader(new InputStreamReader(inputStream)).lines()
				.collect(Collectors.joining());
		return result;
	}

}
