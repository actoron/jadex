/**
 * 
 */
package sodekovs.bikesharing.bikestation;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.math.Vector2Double;
import sodekovs.bikesharing.coordination.StateCoordinationStationData;

/**
 * Plan is called when a decentralized polling request was received over DeCoMAS and answer with the current occupancy in this station.
 * 
 * @author Thomas Preisler
 */
public class DecentralizedPollingRequestPlan extends Plan {

	private static final long serialVersionUID = 6066646584562717799L;

	@Override
	public void body() {
		while (true) {
			waitForTick();
			Object[] elements = getWaitqueue().getElements();
			// IInternalEvent event = (IInternalEvent) getWaitqueue().removeNextElement();

			if (elements != null) {
				for (Object element : elements) {
					IInternalEvent event = (IInternalEvent) element;
					StateCoordinationStationData data = (StateCoordinationStationData) event.getParameter("data").getValue();

					Integer stock = (Integer) getBeliefbase().getBelief("stock").getFact();
					Integer capacity = (Integer) getBeliefbase().getBelief("capacity").getFact();
					String stationID = (String) getBeliefbase().getBelief("stationID").getFact();
					Vector2Double position = (Vector2Double) getBeliefbase().getBelief("position").getFact();

					StateCoordinationStationData reply = new StateCoordinationStationData(stationID, capacity, stock, position, StateCoordinationStationData.REPLY, data.getOriginatorID());
					IInternalEvent replyEvent = createInternalEvent("decentralized_polling_reply");
					replyEvent.getParameter("data").setValue(reply);
					dispatchInternalEvent(replyEvent);
				}
			}
		}
	}
}