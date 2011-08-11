package jadex.simulation.analysis.application.desmoJ.models.varncarrier;

import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;
import desmoj.core.simulator.SimTime;
/**
 * This class is part of the "Vancarrier_1st_p_model".
 * See the description() method of the model class for
 * further documentation of the basis model.
 *
 * This class represents the truck in the above mentioned
 * model.
 * A truck arrives at the container terminal, and requests
 * loading of a container.  If possible he gets serviced
 * by the vancarrier immediatly. If not he waits on the
 * parking area for his turn.
 * After service he leaves the system.
 *
 * Creation date: (29.03.00 14:13:40)
 * @author: Olaf Neidhardt
 */
public class Truck extends SimProcess {

	/**
	* Keeps a reference to the model this actor is a part of 
	* usefull shortcut to access the model infrastructure
	*/
	private VancarrierModel myModel;
	/**
	 * Constructor of the truck process
	 *
	 * Used to create a new truck to be serviced by the vancarrier.
	 *
	 * @param owner desmoj.Model      of this model
	 * @param name java.lang.String   of this truck
	 * @param showInTrace boolean     show it in trace or not show it
	 */
	public Truck(Model owner, String name, boolean showInTrace) {

		super(owner, name, showInTrace);

		//memorize the model this truck is associated with
		myModel = (VancarrierModel) owner;

	}
	
	private SimTime startWait;
	
	private SimTime endWait;
	
	/**
	 * This lifeCycle() describes what the truck does when it
	 * becomes activated by DESMO-J, thus when it enters the parking-lot.
	 * 
	 * It will check if the vancarrier is available.
	 * If it will activate the vancarrier to get serviced and
	 * transfer the control to the VC.
	 * Otherwise it inserts itself into the queue of waiting trucks
	 * and passivates. (it parks and waits)
	 * It leaves the system after service.
	 *
	 * The eventRoutine()/lifeCycle() methods are one of the most import
	 * methods within DESMO-J based simulations. This is where the real
	 * action happens.
	 */
	public void lifeCycle() {

		// Truck enters parking-lot
		myModel.truckQueue.insert(this);
		startWait = currentTime();
		sendTraceNote("TruckQueuelength: " + myModel.truckQueue.length());

		// is the VC available ?
		if (!myModel.idleVCQueue.isEmpty()) { // it is available

			//get the first (and only) VC from the idle VC queue
			VC vancarrier = myModel.idleVCQueue.first();
			//to get it does not mean it is removed yet (?!), so do it now:
			myModel.idleVCQueue.remove(vancarrier);

			//place the VC on the eventlist right after me,
			//to ensure that I will be the next customer to get serviced
			vancarrier.activateAfter(this);

			//thats all, I can sit back and wait
			passivate();

		} else { // it is NOT available

			// thus I do nothing and wait for service
			passivate();
		}

		// Ok, I am back online again, which means I was serviced
		// by the VC. I can leave the systems now.
		// Luckily I don't have to do anything more than sending
		// a message to the trace file, because the
		// JAVA VM garbagge collector will get the job done.
		// Bye!
		sendTraceNote("Truck was serviced and leaves system.");
		myModel.trucksServiced.update(++myModel.servicedTrucks);
		myModel.waitTimeHistogram.update(getWaitTime());
	}
	
	public void endWait() {
		endWait = currentTime();
	}
	
	public double getWaitTime() {
		if (startWait != null && endWait != null) 
			return endWait.getTimeValue() - startWait.getTimeValue();
		else
			return Double.NaN;
	}
}
