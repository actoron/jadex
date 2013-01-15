package sodekovs.swing.jcc.plugins.benchmarking.helper;


import java.util.TimerTask;

import sodekovs.swing.jcc.plugins.benchmarking.BenchmarkingPanel;
import sodekovs.util.misc.FileHandler;
import sodekovs.util.model.benchmarking.description.IHistoricDataDescription;

/**
 * Check when file is created and update PNG on panel.
 * @author vilenica
 *
 */
public class CheckFileThread extends TimerTask {
	
	private String path;
	private BenchmarkingPanel panel;
	private IHistoricDataDescription dataDesc;
	
	public CheckFileThread(String path, BenchmarkingPanel panel, IHistoricDataDescription dataDesc) {
		this.path = path;
		this.panel = panel;
		this.dataDesc = dataDesc;
	}

	public void run() {
		if (isFileModified()) {
			// do whatever needs doing if file has been modified
//			System.out.println("PATH THERE!");
			panel.updateHistoryPNG(dataDesc);
			this.cancel();
		}
	}

	private boolean isFileModified(){
			return FileHandler.fileExists(path);	
	}
}
