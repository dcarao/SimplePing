package com.docler.simpleping;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.docler.simpleping.dto.ConfigDto;
import com.docler.simpleping.service.SimplePingService;

public class SimplePingMain {

	public static void main(String[] args) {
        String file = null;
		if(args.length > 0){
        	file = args[0];
        }
		ConfigDto config = loadProperties(file);

		ScheduledExecutorService execService = Executors.newScheduledThreadPool(0);
		execService.scheduleWithFixedDelay(() -> {

			Callable<String> task = () -> {
				try {
					SimplePingService p = new SimplePingService(config);
					return p.call();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return "";
			};

			Future<String> future = execService.submit(task);
			System.out.println(new Date() + "Checking if there is a task running...");

			if (future.isDone()) {
				System.out.println("Starting a new command");
			} else {
				// System.out.println("Can't run again");
			}

		} , 0, config.getScheduleDelay(), TimeUnit.MILLISECONDS);

		// execService.shutdown();
	}

	/**
	 * 
	 * @return
	 */
	public static ConfigDto loadProperties(String propertyFile) {
		
		
		
		ConfigDto config = new ConfigDto();
		try {
			Properties prop = new Properties();
			InputStream input = null;
			if(propertyFile == null){
				input = SimplePingMain.class.getClassLoader().getResourceAsStream("config.properties");
			}else{
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
			
			System.out.println("************* Properties Configured");
			System.out.println(config);
			System.out.println("************************************");
			
			File file = new File(config.getLogFile());
			if (file.exists()) {
				file.delete();
			}
			

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		return config;
	}


}
