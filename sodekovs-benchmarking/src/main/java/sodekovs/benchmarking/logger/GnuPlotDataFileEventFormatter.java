package sodekovs.benchmarking.logger;

import jadex.bridge.service.types.clock.IClockService;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import sodekovs.benchmarking.helper.Constants;

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
}
