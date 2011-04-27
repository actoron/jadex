package jadex.benchmarking.logger;

import jadex.benchmarking.helper.Constants;
import jadex.bridge.service.clock.IClockService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import sodekovs.util.misc.GlobalConstants;

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
		
		if (rec.getMessage().startsWith(Constants.PREPARE_GNUPLOT_PREFIX)) {
			buf.append(createGnuPlotPrefix());
		} else if (rec.getMessage().startsWith(Constants.PREPARE_GNUPLOT_SUFFIX)) {
			buf.append(createGnuPlotSuffix());
		} else {			
			//500: to align label a little bit left of the value on the graph
			buf.append("set label '"+ rec.getMessage() +"' at " + String.valueOf((clockService.getTime() - starttime)-500) +",1.25");
			buf.append('\n');
		}
		return buf.toString();
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
		buffer.append("set yrange [0:2]");
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

		buffer.append("set term png transparent");
		buffer.append("\n");
		
		//This has to be done when reading file from db, in order to have appropriate filenames for the system where is should be plotted.
		
//		buffer.append("set output '" + GlobalConstants.LOGGING_DIRECTORY + "\\" + fileTimestamp + ".png'");
//		buffer.append("\n");
//		buffer.append("plot '" + GlobalConstants.LOGGING_DIRECTORY + "\\" + fileTimestamp + ".dat' u 1:2 w impulse title \"Observed Events\"");
//		buffer.append("\n");
//		buffer.append("pause -1");

		return buffer.toString();
	}
}
