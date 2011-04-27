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
			
			//calculate lenght of array: split + 3 for the constants at the end
			String[] newMainFile = new String[data.getGnuPlotMainFileContent().split("\n").length+3];
			// split file by separator "\n" : get the separate line of the main file
			String[] tmpFile = data.getGnuPlotMainFileContent().split("\n");
			System.arraycopy(tmpFile, 0, newMainFile, 0, tmpFile.length );
			
			//Edit main file in order to add current user directory to output file
			newMainFile[newMainFile.length-3] = "set output '" + GlobalConstants.LOGGING_DIRECTORY + "\\" + data.getTimestamp() + ".png'";
			newMainFile[newMainFile.length-2] = "plot '" + GlobalConstants.LOGGING_DIRECTORY + "\\" + data.getTimestamp() + ".dat' u 1:2 w impulse title \"Observed Events\"";
			newMainFile[newMainFile.length-1] = "pause -1";
									
			//Write content of data log file to hard disk. this file is needed in order to create the png-file in the next step.
			FileHandler.writeToFile(GlobalConstants.LOGGING_DIRECTORY, data.getTimestamp()+ ".dat", data.getLogEntries());
			
			GnuPlotHandler.exec(newMainFile);			
		}
		
	}

}
