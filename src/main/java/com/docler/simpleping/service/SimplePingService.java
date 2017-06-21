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
import java.util.Date;
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

public class SimplePingService implements Callable<String> {

	private ConfigDto config;

	public SimplePingService(ConfigDto config) {
		this.config = config;
	}

	@Override
	public String call() throws Exception {
		ExecutorService executor = Executors.newWorkStealingPool();

		List<Callable<String>> callables = new ArrayList<>();
		callables.add(new PingIcmpTask(config.getHosts(), config.getTimes()));
		callables.add(new PingTcpTask(config.getHosts(), config.getTimes(), config.getTimeout()));
		callables.add(new TraceTask(config.getHosts()));

		List<Future<String>> futures = executor.invokeAll(callables);

		List<String> result = new ArrayList<>();
		for (Future<String> future : futures) {
			try {
				result.add(future.get());
			} catch (CancellationException ce) {
				ce.printStackTrace();
			} catch (ExecutionException ee) {
				ee.printStackTrace();
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt(); // ignore/reset
			}
		}
		System.out.println(new Date() + "*********** Completed");
		executor.shutdown();

		report(result);

		return "Completed";

	}

	/**
	 * 
	 * @param result
	 */
	public void report(List<String> result) {
		StringBuilder sb = new StringBuilder();
		sb.append("{").append("\"host\":\"").append(config.getHosts()).append("\", ");
		sb.append(result.stream().collect(Collectors.joining(", ")));
		sb.append("}");
		System.out.println(sb.toString());
		writeToFile(sb.toString());
		sendReport(sb.toString());

	}

	/**
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

			System.out.println("Done");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param payload
	 */
	private void sendReport(String payload) {
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
		       


			int responseCode = conn.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + payload);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			conn.disconnect();

			//print result
			System.out.println(response.toString());



		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
