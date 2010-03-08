/**
 * 
 */
package deco4mas.mechanism.v2.tspaces;

import com.ibm.tspaces.TupleSpaceException;
import com.ibm.tspaces.lock.LMAbortedException;
import com.ibm.tspaces.server.CheckpointException;
import com.ibm.tspaces.server.RollbackException;
import com.ibm.tspaces.server.TSServer;

/**
 * Start a TSpace server instance.
 * 
 * @author Ante Vilenica & Jan Sudeikat
 * 
 */
public class StartTSpacesServer {

	public static void startServer(String spaceId) {
		if(spaceId == null)
			spaceId= "default_space";	
		
		TSServer ts;
		try {
			ts = new TSServer();
			Thread serverThread = new Thread(ts, spaceId);
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
