package sodekovs.util.gnuplot;

import sodekovs.util.misc.FileHandler;
import sodekovs.util.misc.GlobalConstants;
import sodekovs.util.model.benchmarking.description.IHistoricDataDescription;

public class CreateImagesThread implements Runnable {

	private IHistoricDataDescription[] desc = null;

	public CreateImagesThread(IHistoricDataDescription[] desc) {
		this.desc = desc;
	}

	/**
	 * Create PNG-Images from descriptions using gnuplot
	 */
	public void run() {

		for (IHistoricDataDescription data : desc) {
			CreateImage.createImage(data);
		}
	}

}
