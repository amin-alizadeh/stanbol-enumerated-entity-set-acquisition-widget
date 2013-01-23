package com.conatix.EntitySetAdder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogWriter {
	private static String logFileName;
	private static BufferedWriter logWriter;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"dd.MM.yyyy HH:mm:ss.SSS");

	public LogWriter(String logFileName) {
		this.logFileName = logFileName + ".log";
		System.out.println("Opened log file");
	}

	public void openLogFile() {
		FileWriter fStream;
//		System.out.println(logFileName);
		try {
			fStream = new FileWriter(logFileName, true);
			logWriter = new BufferedWriter(fStream);
		} catch (IOException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}

	public void writeLog(String logString) {
		Date date = new Date();
		String current_date = dateFormat.format(date);
		try {
			logWriter.write(current_date + ": " + logString);
			logWriter.newLine();
			logWriter.flush();
		} catch (IOException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}

	public void closeLogFile() {
		try {
			logWriter.close();
		} catch (IOException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}

}
