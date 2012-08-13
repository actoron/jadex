/**
 * 
 */
package deco4mas.distributed.mechanism.pastry;

import jadex.kernelbase.StatelessAbstractInterpreter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import rice.environment.Environment;
import rice.pastry.NodeIdFactory;
import rice.pastry.PastryNode;
import rice.pastry.PastryNodeFactory;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;
import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import deco4mas.distributed.mechanism.CoordinationInfo;
import deco4mas.distributed.mechanism.CoordinationMechanism;

/**
 * A coordination mechanism based on Scribe. A group communication platform based on the FreePastry distributed hash table implementation.
 * 
 * @author Thomas Preisler
 */
public class ScribeMechanism extends CoordinationMechanism {

	/** The applications interpreter */
	protected StatelessAbstractInterpreter applicationInterpreter = null;

	private ScribeCoordinationClient scribeClient = null;

	private PastryNode node = null;

	public ScribeMechanism(CoordinationSpace space) {
		super(space);

		// TODO Der Cast ist ein Hack bis Lars und Alex die Schnittstellen von Jadex anpassen
		this.applicationInterpreter = (StatelessAbstractInterpreter) space.getApplicationInternalAccess();

		// If it's a distributed application, then it has a contextID.
		HashMap<String, Object> appArgs = (HashMap<String, Object>) this.applicationInterpreter.getArguments();
		this.coordinationContextID = (String) appArgs.get("CoordinationContextID");
	}

	@Override
	public void start() {
		String bootaddress = mechanismConfiguration.getProperty("bootaddress");
		int bootport = mechanismConfiguration.getIntegerProperty("bootport");
		int bindport = mechanismConfiguration.getIntegerProperty("bindport");

		try {
			InetAddress bootaddr = InetAddress.getByName(bootaddress);
			InetSocketAddress bootSocketaddress = new InetSocketAddress(bootaddr, bootport);

			// Loads pastry configurations
			Environment env = new Environment();

			// Generate the NodeIds Randomly
			NodeIdFactory nidFactory = new RandomNodeIdFactory(env);

			// construct the PastryNodeFactory, this is how we use rice.pastry.socket
			PastryNodeFactory factory = new SocketPastryNodeFactory(nidFactory, bindport, env);

			// construct a new node
			node = factory.newNode();

			this.scribeClient = new ScribeCoordinationClient(node, this.coordinationContextID, this.space);

			node.boot(bootSocketaddress);

			// the node may require sending several messages to fully boot into the ring
			synchronized (node) {
				while (!node.isReady() && !node.joinFailed()) {
					// delay so we don't busy-wait
					node.wait(500);

					// abort if can't join
					if (node.joinFailed()) {
						// throw new IOException("Could not join the FreePastry ring.  Reason:" + node.joinFailedReason());
						System.out.println("Could not join the FreePastry ring.  Reason:" + node.joinFailedReason());
					}
				}
			}

			System.out.println("Finished creating new node: " + node);

			this.scribeClient.subscribe();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void perceiveCoordinationEvent(Object obj) {
		CoordinationInfo ci = (CoordinationInfo) obj;
		this.scribeClient.publish(ci);
	}

	@Override
	public void stop() {
		this.scribeClient.unsubscribe();
		this.node.destroy();
	}
}