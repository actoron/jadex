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
 * Prepare content of "*.plt" file for gnuplot. Plot can be created directly from log file. It requires corresponding ".dat" file with the single event.
 * 
 * @author vilenica
 * 
 */
public class GnuPlotMainFileEventFormatter extends Formatter {

	// The start time of the benchmark. Required to compute relative start time which is required
	// to compute various runs of a benchmark.
	private long starttime = -1;
	private IClockService clockService = null;
	//This time denotes the time used for the file name
	private String fileTimestamp = null; 

	public GnuPlotMainFileEventFormatter(long starttime, IClockService clockService, String fileTimestamp) {
		this.starttime = starttime;
		this.clockService = clockService;
		this.fileTimestamp = fileTimestamp;
	}

	public String format(LogRecord rec) {
		StringBuffer buf = new StringBuffer(1000);
		// Bold any levels >= WARNING
		// buf.append("<tr>");
		// buf.append("<td>");

		// if (rec.getLevel().intValue() >= Level.WARNING.intValue())
		// {
		// buf.append("<b>");
		// buf.append(rec.getLevel());
		// buf.append("</b>");
		// } else
		// {
		// buf.append(rec.getLevel());
		// }
		// buf.append("</td>");
		if (rec.getMessage().startsWith(Constants.PREPARE_GNUPLOT_PREFIX)) {
			buf.append(createGnuPlotPrefix());
		} else if (rec.getMessage().startsWith(Constants.PREPARE_GNUPLOT_SUFFIX)) {
			buf.append(createGnuPlotSuffix());
		} else {			
			//500: to align label a little bit left of the value on the graph
			buf.append("set label '"+ rec.getMessage() +"' at " + String.valueOf((clockService.getTime() - starttime)-500) +",2.0");
			buf.append('\n');
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
		buffer.append("plot '" + fileTimestamp + ".dat' u 1:2 w points title \"Observed Events\"");
		buffer.append("\n");

		return buffer.toString();
	}
}
