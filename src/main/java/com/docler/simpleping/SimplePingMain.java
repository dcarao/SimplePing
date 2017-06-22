package com.docler.simpleping;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.docler.simpleping.dto.ConfigDto;
import com.docler.simpleping.service.SimplePingService;
/**
 * Entry point for the SimplePing application
 * 
 * @author dcarao
 *
 */
public class SimplePingMain {

	/**
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String file = null;
		boolean isDefault = false;
		if (args.length > 0) {
			file = args[0];
		} else {
			file = "config.properties";
			isDefault = true;
		}
		ConfigDto config = loadProperties(file, isDefault);

		String[] hosts = config.getHosts().split(",");

		ScheduledExecutorService execService = Executors.newScheduledThreadPool(hosts.length);
		execService.scheduleWithFixedDelay(() -> {

			System.out.println("\n\nSTARTING SimpleMain PROCESS...............");
			for (String host : hosts) {
				execService.execute(new SimplePingService(config, host));
			}
		} , 0, config.getScheduleDelay(), TimeUnit.MILLISECONDS);
	}

	/**
	 * Load configuration.
	 * 
	 * @param propertyFile
	 * @param isDefault
	 * @return
	 * @throws Exception 
	 */
	public static ConfigDto loadProperties(String propertyFile, boolean isDefault) throws Exception {

		ConfigDto config = new ConfigDto();
		try {
			Properties prop = new Properties();
			InputStream input = null;
			if (isDefault) {
				input = SimplePingMain.class.getClassLoader().getResourceAsStream(propertyFile);
			} else {
				File file = new File(propertyFile);
				input = new FileInputStream(file);

			}
			prop.load(input);

			config.setTimeout(Integer.parseInt(prop.getProperty("tcp.timeout")));
			config.setHosts(prop.getProperty("hosts"));
			config.setLogFile(prop.getProperty("log.file"));
			config.setTimes(Integer.parseInt(prop.getProperty("ping.times")));
			config.setUrl(prop.getProperty("url"));
			config.setScheduleDelay(Long.parseLong(prop.getProperty("delay")));

			System.out.println("************* Properties Configured: " + propertyFile);
			System.out.println(config);
			System.out.println("************************************");

			File file = new File(config.getLogFile());
			if (file.exists()) {
				file.delete();
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			throw new Exception(e);
		}

		return config;
	}

}
