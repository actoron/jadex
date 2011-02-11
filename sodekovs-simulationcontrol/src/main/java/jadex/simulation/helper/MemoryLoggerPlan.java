package jadex.simulation.helper;

import jadex.bdi.runtime.Plan;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;

public class MemoryLoggerPlan extends Plan {

	private String id = null;
	private StringBuffer buffer = new StringBuffer();
	BufferedWriter out = null;

	public void body() {

		if (getBeliefbase().getBelief("MemoryLoggerID").getFact() == null) {
			Random rand = new java.util.Random();
			id = String.valueOf(rand.nextInt());
		} else {
			id = (String) getBeliefbase().getBelief("MemoryLoggerID").getFact();
		}

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("MemoryUsage_" + id + ".txt"));
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		int counter = 0;
		while (true) {
			buffer.append((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024000);
			buffer.append("\n");
			waitFor(5000);
			if (counter > 7) {
				persist();
				counter = 0;
			} else {
				counter++;
			}
		}
	}

	private void persist() {
		try {
			FileOutputStream fout = new FileOutputStream("MemoryUsage_" + id + ".txt", true);
			// Print a line of text
			new PrintStream(fout).print(buffer.toString());
			// Close our output stream
			fout.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		buffer.delete(0, buffer.length());
	}
}
