package jadex.benchmarking.logger;

import jadex.benchmarking.helper.Constants;
import jadex.bridge.service.clock.IClockService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Prepare content of "*.dat" file for gnuplot. Plot can be created directly from log file. It requires corresponding ".plt" file with instructions for gnuplot.
 * 
 * @author vilenica
 * 
 */
public class GnuPlotDataFileEventFormatter extends Formatter {

	// The start time of the benchmark. Required to compute relative start time which is required
	// to compute various runs of a benchmark.
	private long starttime = -1;
	private IClockService clockService = null;

	public GnuPlotDataFileEventFormatter(long starttime, IClockService clockService) {
		this.starttime = starttime;
		this.clockService = clockService;
	}

	public String format(LogRecord rec) {
		StringBuffer buf = new StringBuffer(1000);
		if (rec.getMessage().startsWith(Constants.PREPARE_GNUPLOT_PREFIX) || rec.getMessage().startsWith(Constants.PREPARE_GNUPLOT_SUFFIX)) {
			// do nothing: this LogRecord has to be processed by the GnuPlotMainFileEventFormatter
		} else {
			buf.append(clockService.getTime() - starttime);
			buf.append("\t");
			// Hack: default y-value is 1. To create sort of history, e.g. y-value is not important.
			buf.append("1");
			buf.append("\t");
			buf.append(formatMessage(rec));
			buf.append("\n");
		}

		return buf.toString();
	}

	private String calcDate(long millisecs) {
		SimpleDateFormat date_format = new SimpleDateFormat("MMM dd,yyyy HH:mm");
		Date resultdate = new Date(millisecs);
		return date_format.format(resultdate);
	}

	private String getTimestamp() {
		GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();
		return String.valueOf(cal.get(Calendar.HOUR_OF_DAY)) + "-" + String.valueOf(cal.get(Calendar.MINUTE)) + "-" + String.valueOf(cal.get(Calendar.SECOND));
	}

	/**
	 * Create prefix of the *.plt file
	 * 
	 * @return
	 */
	private String createGnuPlotPrefix() {
		StringBuffer buffer = new StringBuffer(1000);
		buffer.append("set title \"History of CRUD events of the benchmark.\"");
		buffer.append("\n");
		buffer.append("set xlab \"Relative time\"");
		buffer.append("\n");
		buffer.append("set grid");
		buffer.append("\n");
		buffer.append("set yrange [0:3]");
		buffer.append("\n");

		return buffer.toString();
	}

	/**
	 * Create suffix of the *.plt file
	 * 
	 * @return
	 */
	private String createGnuPlotSuffix() {
		StringBuffer buffer = new StringBuffer(1000);

		buffer.append("\n");
		buffer.append("plot 'data.txt' u 1:2 w points");
		buffer.append("\n");

		return buffer.toString();
	}
}
