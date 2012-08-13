/**
 * 
 */
package deco4mas.distributed.mechanism.pastry;

import java.util.Collection;

import rice.p2p.commonapi.Node;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.scribe.Scribe;
import rice.p2p.scribe.ScribeContent;
import rice.p2p.scribe.ScribeImpl;
import rice.p2p.scribe.ScribeMultiClient;
import rice.p2p.scribe.Topic;
import rice.pastry.commonapi.PastryIdFactory;
import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import deco4mas.distributed.mechanism.CoordinationInfo;

/**
 * @author thomas
 * 
 */
public class ScribeCoordinationClient implements ScribeMultiClient {

	/**
	 * My handle to a scribe impl.
	 */
	private Scribe scribe = null;

	/**
	 * The only topic this appl is subscribing to.
	 */
	private Topic topic = null;

	private CoordinationSpace space = null;

	public ScribeCoordinationClient(Node node, String coordinationContextID, CoordinationSpace space) {
		this.scribe = new ScribeImpl(node, coordinationContextID);

		this.topic = new Topic(new PastryIdFactory(node.getEnvironment()), coordinationContextID);

		this.space = space;
	}

	@Override
	public boolean anycast(Topic topic, ScribeContent content) {
		// TODO Auto-generated method stub
		System.out.println(this.getClass() + " anycast called.");

		return false;
	}

	@Override
	public void deliver(Topic topic, ScribeContent content) {
		if (content instanceof ScribeCoordinationContent) {
			ScribeCoordinationContent scc = (ScribeCoordinationContent) content;
			space.publishCoordinationEvent(scc.getCi());
		}
	}

	@Override
	public void childAdded(Topic topic, NodeHandle child) {
		// TODO Auto-generated method stub
		System.out.println(this.getClass() + " childAdded called.");
	}

	@Override
	public void childRemoved(Topic topic, NodeHandle child) {
		// TODO Auto-generated method stub
		System.out.println(this.getClass() + " childRemoved called.");
	}

	@Override
	public void subscribeFailed(Topic topic) {
		// TODO Auto-generated method stub
		System.out.println(this.getClass() + " subscribeFailed called.");
	}

	@Override
	public void subscribeFailed(Collection<Topic> topics) {
		// TODO Auto-generated method stub
		System.out.println(this.getClass() + " subscribeFailed(Collection) called.");
	}

	@Override
	public void subscribeSuccess(Collection<Topic> topics) {
		// TODO Auto-generated method stub
		System.out.println(this.getClass() + " subscribeSuccess called.");
	}

	public void subscribe() {
		this.scribe.subscribe(topic, this, null, null);
	}

	public void publish(CoordinationInfo ci) {
		ScribeCoordinationContent scc = new ScribeCoordinationContent(ci);
		this.scribe.publish(this.topic, scc);
	}

	public void unsubscribe() {
		this.scribe.unsubscribe(topic, this);
	}
}
