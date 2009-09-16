package communicationexample;

import java.util.LinkedList;

import eis.EnvironmentInterfaceStandard;
import eis.exceptions.EntityException;
import eis.exceptions.EnvironmentInterfaceException;
import eis.exceptions.ManagementException;
import eis.iilang.ActionResult;
import eis.iilang.Percept;
import eis.iilang.EnvironmentCommand;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import eis.iilang.Percept;


public class EnvironmentInterface extends EnvironmentInterfaceStandard {

	public EnvironmentInterface() {
		
		try {

			this.addEntity("en1");
			this.addEntity("en2");
			this.addEntity("en3");

		} catch (EntityException e) {
			e.printStackTrace();
		}
		
	}
	
	public ActionResult actiontellall(String entity, Identifier message) {
		
		for( String e : this.getEntities() ) {
			
			if( e.equals(entity) )
				continue;
			
			try {
				this.notifyAgentsViaEntity(
						new Percept("message", (Parameter)message), 
						e);
			} catch (EnvironmentInterfaceException e1) {
				e1.printStackTrace();
			}
			
		}
		
		return new ActionResult("success");
		
	}

	@Override
	public LinkedList<Percept> getAllPerceptsFromEntity(String entity) {
		return null;
	}

	@Override
	public void manageEnvironment(EnvironmentCommand command, String... args)
			throws ManagementException {
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isConnected() {

		return true;
	
	}
	

}
