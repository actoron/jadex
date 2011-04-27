package sodekovs.util.gnuplot;

import sodekovs.util.misc.FileHandler;
import sodekovs.util.misc.GlobalConstants;
import sodekovs.util.model.benchmarking.description.IHistoricDataDescription;

public class CreateImagesThread implements Runnable{
	
	private IHistoricDataDescription[] desc = null;
	
	public CreateImagesThread (IHistoricDataDescription[] desc){
		this.desc = desc;
	}
	
	/**
	 * Create PNG-Images from descriptions using gnuplot
	 */
	public void run() {
		
		for(IHistoricDataDescription data : desc){			
			
			// split file by separator "\t" : get the seperate line of the main file
			String[] mainFile = data.getGnuPlotMainFileContent().split("\n");
			
			//Write content of data log file to hard disk. this file is needed in order to create the png-file in the next step.
			FileHandler.writeToFile(GlobalConstants.LOGGING_DIRECTORY + "\\" + data.getTimestamp()+ ".dat", data.getLogEntries());
			GnuPlotHandler.exec(mainFile);			
		}
		
	}

}
