package jadex.simulation.analysis.application.desmoJ.models.varncarrier;

import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;
import desmoj.core.simulator.SimTime;
/**
 * This class is part of the "Vancarrier_1st_p_model".
 * See the description() method of the model class for
 * further documentation of the basis model.
 *
 * This class represents the vancarrier in the above 
 * mentioned model.
 * The vc is on service and waits for a truck to come
 * requesting service. He will head for the container and
 * deliver it to the truck.
 * If there is another truck waiting he services it.
 * If not he waits for the next truck to arrive.
 *
 * Creation date: (29.03.00 14:13:13)
 * @author: Olaf Neidhardt
 */
public class VC extends SimProcess {

	/**
	* Keeps a reference to the model this actor is a part of 
	* usefull shortcut to access the model infrastructure
	*/
	private VancarrierModel myModel;
	/**
	 * This method constructs a new VC
	 *
	 * @param owner desmoj.Model     the associated model
	 * @param name java.lang.String  of the VC
	 * @param showInTrace boolean    show in trace file or not show in trace 
	 */
	public VC(Model owner, String name, boolean showInTrace) {
		super(owner, name, showInTrace);

		myModel = (VancarrierModel) owner;

	}
	/**
	 * This lifeCycle() describes what the vancarrier (VC) does when it
	 * becomes activated by DESMO-J,
	 * 
	 * It will cycle through a process like this:
	 * Check if there is a customer waiting.
	 * If there is someone waiting 
	 *   a) remove customer from queue
	 *   b) service customer
	 *   c) return to top
	 * If there is no one waiting
	 *   a) wait until you get activated again
	 *	 b) then return to top
	 *   
	 * The eventRoutine()/lifeCycle() methods are one of the most import
	 * methods within DESMO-J based simulations. This is where the real
	 * action happens.
	 * 
	 */
	public void lifeCycle() {

		//the servicer is always on duty and will never stop working
		while (true) {
			//check if there is someone waiting
			if (myModel.truckQueue.isEmpty()) { // NO,there is no one waiting

				// insert yourself into the idle VC queue
				myModel.idleVCQueue.insert(this);

				// and wait for things to happen
				passivate();
			} else { //YES,there is a customer (truck) waiting

				//get the next truck to service station
				Truck nextTruck = myModel.truckQueue.first();
				//again first() does not mean it is removed yet, so...
				myModel.truckQueue.remove(nextTruck);
				// myModel.queueData.update(myModel.truckQueue.length());
				nextTruck.endWait();

				//now service it
				//service time is represented by a hold to the VC process
				hold(new SimTime(myModel.getServiceTime()));
				//from inside to outside...
				//...draw a new period of service time
				//...make a SimTime object out of it
				//...and hold for this amount of time

				//now the truck has received its container and can leave
				//we will reactivate iot though, to allow him to do some
				//more message sending
				nextTruck.activate(new SimTime(0.0));
				//the VC can return to top and check for a new customer
			}
		}
	}
}
