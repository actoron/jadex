package deco4mas.mechanism.tspaces.server;

import jadex.bdi.runtime.Plan;

import java.util.Properties;

import com.ibm.tspaces.TupleSpaceException;
import com.ibm.tspaces.lock.LMAbortedException;
import com.ibm.tspaces.server.CheckpointException;
import com.ibm.tspaces.server.RollbackException;
import com.ibm.tspaces.server.TSServer;

/**
 * Start a TSpace server instance.
 * 
 * @author Jan Sudeikat
 * 
 */
@SuppressWarnings("serial")
public class StartTSpacePlan extends Plan {

	// -------- methods -------------
	Properties prop = System.getProperties();

	/** The plan body. */
	@Override
	public void body() {

		String space_id = (String) this.getBeliefbase().getBelief("space_id").getFact();
		
		 TSServer ts;
		try {
			ts = new TSServer();
			Thread serverThread = new Thread(ts, space_id);
			serverThread.start();
		} catch (LMAbortedException e) {
			System.err.println("TSPaces server could not be started...");
			e.printStackTrace();
		} catch (RollbackException e) {
			System.err.println("TSPaces server could not be started...");
			e.printStackTrace();
		} catch (CheckpointException e) {
			System.err.println("TSPaces server could not be started...");
			e.printStackTrace();
		} catch (TupleSpaceException e) {
			System.err.println("TSPaces server could not be started...");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println(e);
		 }
	}
}
