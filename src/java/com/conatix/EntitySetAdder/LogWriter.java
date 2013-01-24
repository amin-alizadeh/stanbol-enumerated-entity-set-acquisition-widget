package com.conatix.EntitySetAdder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * LogWriter opens a log file and stores all the logs in a file. Each line
 * contains the time when the log happens.
 * 
 * @param logFileName
 *            change on command line using the argument - LogFile <logFileName>
 *            or in the configs.xml file <LogFile>log.log</LogFile>.
 * 
 * @see {@link #openLogFile()}, {@link #writeLog(String)}, {@link #closeLogFile()}
 * 
 */
public class LogWriter {
	private static String logFileName;
	private static BufferedWriter logWriter;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"dd.MM.yyyy HH:mm:ss.SSS");

	/**
	 * Constructor of the class. It gets the logFileName as the input and
	 * assigns it to the static variable so it can be opened and ready to use.
	 * 
	 * @param logFileName
	 *            change on command line using the argument - LogFile
	 *            <logFileName> or in the configs.xml file
	 *            <LogFile>log</LogFile>.
	 */
	@SuppressWarnings("static-access")
	public LogWriter(String logFileName) {
		this.logFileName = logFileName + ".log";
	}

	/**
	 * Opens the LogFile and makes it ready to start writing the logs.
	 * 
	 * @throws IOException
	 */
	public void openLogFile() {
		FileWriter fStream;
		try {
			fStream = new FileWriter(logFileName, true);
			logWriter = new BufferedWriter(fStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param logString
	 *            the log which should be stored in the logFileName.log. The
	 *            date and time when the log happened is also stored at the
	 *            beginning of each line.
	 * @throws IOException
	 */
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

	/**
	 * Closes the LogFile when there is no need of storing.
	 * @throws IOException
	 */
	public void closeLogFile() {
		try {
			logWriter.close();
		} catch (IOException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}

}
