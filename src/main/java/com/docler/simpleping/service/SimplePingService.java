package com.docler.simpleping.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.docler.simpleping.dto.ConfigDto;
import com.docler.simpleping.task.PingIcmpTask;
import com.docler.simpleping.task.PingTcpTask;
import com.docler.simpleping.task.TraceTask;

/**
 * Service logic to coordinate the command calls
 * 
 * @author dcarao
 *
 */
public class SimplePingService implements Runnable {

	private ConfigDto config;
	private String host;

	/**
	 * Constructor
	 * 
	 * @param config
	 * @param host
	 */
	public SimplePingService(ConfigDto config, String host) {
		this.config = config;
		this.host = host;
	}

	@Override
	public void run() {
		System.out.println("Starting a new Thread for: " + host);
		ExecutorService executor = Executors.newWorkStealingPool();

		List<Callable<String>> callables = new ArrayList<>();
		callables.add(new PingIcmpTask(host, config.getTimes()));
		callables.add(new PingTcpTask(host, config.getTimes(), config.getTimeout()));
		callables.add(new TraceTask(host));

		List<Future<String>> futures = null;
		try {
			futures = executor.invokeAll(callables);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		List<String> result = new ArrayList<>();
		for (Future<String> future : futures) {
			try {
				result.add(future.get());
			} catch (CancellationException ce) {
				ce.printStackTrace();
			} catch (ExecutionException ee) {
				ee.printStackTrace();
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}
		}

		executor.shutdown();
		report(result);
		System.out.println("******************* Completed commands for: " + host);
	}

	/**
	 * Report the result. It will write to a file and it will send via POST to a
	 * http address
	 * 
	 * @param result
	 */
	public void report(List<String> result) {
		StringBuilder sb = new StringBuilder();
		sb.append("{").append("\"host\":\"").append(host).append("\", ");
		sb.append(result.stream().collect(Collectors.joining(", ")));
		sb.append("}");
		writeToFile(sb.toString());
		System.out.println("Results stored in file for host : " + host);
		int responseCode = sendReport(sb.toString());
		System.out.println("Results sent for host : " + host + " - Response Code: " + responseCode);

	}

	/**
	 * Storing the result
	 * 
	 * @param content
	 */
	private synchronized void writeToFile(String content) {
		try {
			File file = new File(config.getLogFile());
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.write("\n");
			bw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Calling a http
	 * 
	 * @param payload
	 */
	private int sendReport(String payload) {
		int responseCode = 0;
		try {
			URL url = new URL(config.getUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setDoInput(true);
			conn.setDoOutput(true);

			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

			// Send post request
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			writer.write(payload);
			writer.close();
			responseCode = conn.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseCode;
	}

}
