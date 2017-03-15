package com.hammersoft.coiner.post;

public class Logger {
	
	private static final Logger instance = new Logger(); 

	public static Logger getLogger(Class class1) {
		return instance;
	}

	public void info(String string) {
		System.out.println(string);
	}
}
