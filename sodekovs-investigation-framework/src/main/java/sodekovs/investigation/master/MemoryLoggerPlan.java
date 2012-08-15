package sodekovs.investigation.master;

import jadex.bdi.runtime.Plan;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

import sodekovs.investigation.helper.Constants;

public class MemoryLoggerPlan extends Plan {

	public void body() {

		while (true) {

			if (((Integer) getBeliefbase().getBelief("memoryLoggerCounter").getFact()).intValue() == -1) {
				try {
					BufferedWriter out = new BufferedWriter(new FileWriter("MemoryUsage.txt"));
					out.write(String.valueOf((Runtime.getRuntime().totalMemory() / 1024000)));
					out.close();
					getBeliefbase().getBelief("memoryLoggerCounter").setFact(0);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					FileOutputStream fout = new FileOutputStream("MemoryUsage.txt", true);

					// Print a line of text
					new PrintStream(fout).println(getExperimentNumber()  + "\t" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024000);
					// Close our output stream
					fout.close();
					getBeliefbase().getBelief("memoryLoggerCounter").setFact((Integer) getBeliefbase().getBelief("memoryLoggerCounter").getFact() + 1);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			waitFor(5000);
		}

	}

	private int getExperimentNumber() {
		HashMap beliefbaseFacts = (HashMap) getBeliefbase().getBelief("generalSimulationFacts").getFact();
		return ((Integer) beliefbaseFacts.get(Constants.TOTAL_EXPERIMENT_COUNTER)).intValue();
	}
}
