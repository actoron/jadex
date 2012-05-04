package sodekovs.benchmarking.logger;

import jadex.bridge.service.types.clock.IClockService;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import sodekovs.util.misc.GlobalConstants;

/**
 * Logs events of the schedule.
 * 
 * @author vilenica
 * 
 */
public class ScheduleLogger {

	private static Logger scheduleLogger = Logger.getLogger(ScheduleLogger.class.getName());
	// The start time of the benchmark. Required to compute relative start time which is required
	// to compute various runs of a benchmark.
	private long starttime = -1;
	private IClockService clockService = null;
	// Not identical with starttime: this time denotes the time used for the name of the file
	private String timestamp = null;

	public ScheduleLogger() {
	}

//	public ScheduleLogger(long starttime, IClockService clockService) {
//		this.starttime = starttime;
//		this.clockService = clockService;
//		init();
//	}

	public void log(String log) {
		scheduleLogger.info(log);
	}

	public void init() {

		timestamp = calculateTimestamp();

		// Create file handler for plt-file
		FileHandler fh = null;
		try {
			String dir = GlobalConstants.LOGGING_DIRECTORY;
			File f = new File(dir);
			if (!f.isDirectory())
				f.mkdir();
			fh = new FileHandler(dir + "\\" + timestamp + ".plt", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		fh.setFormatter(new GnuPlotMainFileEventFormatter(starttime, clockService, timestamp));
		scheduleLogger.addHandler(fh);
		scheduleLogger.setLevel(Level.ALL);
		scheduleLogger.setUseParentHandlers(false);

		// Create file handler for dat-file
		fh = null;
		try {
			String dir = GlobalConstants.LOGGING_DIRECTORY;
			File f = new File(dir);
			if (!f.isDirectory())
				f.mkdir();
			fh = new FileHandler(dir + "\\" + timestamp + ".dat", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		fh.setFormatter(new GnuPlotDataFileEventFormatter(starttime, clockService));
		scheduleLogger.addHandler(fh);
		scheduleLogger.setLevel(Level.ALL);
		scheduleLogger.setUseParentHandlers(false);

	}

	private String calculateTimestamp() {
		GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();
		String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
		return String.valueOf(cal.get(Calendar.HOUR_OF_DAY)) + "-" + String.valueOf(cal.get(Calendar.MINUTE)) + "-"
				+ String.valueOf(cal.get(Calendar.SECOND) + " -- " + cal.get(Calendar.DAY_OF_MONTH) + "-" + month + "-" + cal.get(Calendar.YEAR));
	}

	/**
	 * Get the time when the logger file is created. it serves as kind of id.
	 * 
	 * @return
	 */
	public String getTimestamp() {
		return this.timestamp;
	}

	public long getStarttime() {
		return starttime;
	}

	public void setStarttime(long starttime) {
		this.starttime = starttime;
	}

	public IClockService getClockService() {
		return clockService;
	}

	public void setClockService(IClockService clockService) {
		this.clockService = clockService;
	}

	/**
	 * Return the name of the file where the gnuPlot log is stored.
	 * 
	 * @return
	 */
	public String getFileName() {
		return GlobalConstants.LOGGING_DIRECTORY + "\\" + timestamp + ".";
	}

}
