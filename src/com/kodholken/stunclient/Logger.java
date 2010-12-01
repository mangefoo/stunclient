package com.kodholken.stunclient;

public class Logger {
	public interface Observer {
		public void onLogEntry(String logEntry);
	}
	
	private String className;
	
	public Logger(String className) {
		this.className = className;
	}
	
	public void debug(String message) {
		LoggerFactory.getObserver().onLogEntry(message);
		System.out.println(className + ": " + message);
	}
}
