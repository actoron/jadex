package sodekovs.marsworld.microcarry;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector2Int;
import jadex.micro.MicroAgent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import sodekovs.marsworld.RequestCarry;
import sodekovs.marsworld.carry.LoadOreTask;
import sodekovs.marsworld.coordination.CoordinationSpaceData;
import sodekovs.marsworld.movement.MicroMoveTask;
import sodekovs.marsworld.movement.MoveTask;
import sodekovs.marsworld.producer.ProduceOreTask;
import sodekovs.marsworld.sentry.AnalyzeTargetTask;
import deco4mas.distributed.coordinate.annotation.CoordinationParameter;
import deco4mas.distributed.coordinate.interpreter.agent_state.CoordinationComponentStep;

public class MicroCarryAgent extends MicroAgent{

	Space2D environment;
	ISpaceObject myself;
	
	boolean finished = false;
	
	Queue<ISpaceObject> objects = new LinkedList<ISpaceObject>();
	
	Queue<ISpaceObject> targets = new LinkedList<ISpaceObject>();
	
	// Getter and Setter	
	
	public Queue<ISpaceObject> getObjects() {
		return objects;
	}

	public void addObject(ISpaceObject so){
		this.objects.add(so);
	}
	
	public void setObjects(Queue<ISpaceObject> objects) {
		this.objects = objects;
	}
	
	// Methods
	
	// Handling of an incoming Message-Event
	public void messageArrived(Map<String, Object> msg, MessageType mt)
	{
		ISpaceObject ot = ((RequestCarry)msg.get(SFipa.CONTENT)).getTarget();
		if (!targets.contains(ot))
			targets.add(ot);		
	}

	public IFuture<Void> agentCreated()
	{
		scheduleStep(new PerformStep());
		return IFuture.DONE;
	}
	
	public class PerformStep implements IComponentStep<Void>
	{

		@SuppressWarnings("unchecked")
		@Override
		public IFuture<Void> execute(IInternalAccess ia) {
			getParentAccess().getExtension("my2dspace").addResultListener(createResultListener(new DefaultResultListener()
			{

				@Override
				public void resultAvailable(Object result) {
					if(result==null)
						return;
					
					environment = (Space2D)result;
					myself = environment.getAvatar(getComponentDescription(), getModel().getFullName());
					
					getTime().addResultListener(new DefaultResultListener<Long>() {

						@Override
						public void resultAvailable(Long result) {
							if (result >= (Long)environment.getSpaceObjectsByType("homebase")[0].getProperty("missiontime")){
								IResultListener res = new DefaultResultListener() {

									@Override
									public void resultAvailable(Object result) {
										killAgent();								
									}
									
									@Override
									public void exceptionOccurred(Exception exception) {
										exception.printStackTrace();
									}
								};
								ISpaceObject homebase = environment.getSpaceObjectsByType("homebase")[0];
								moveToLocation((IVector2)homebase.getProperty(Space2D.PROPERTY_POSITION), res);
							}
							else{
								while (objects.size() > 0)
									informNewTarget(objects.poll());
								if (targets.size() > 0){
									ISpaceObject target = environment.getSpaceObject(targets.poll().getId());
									carryOre(target);
								}
								else{
									moveRandom();
								}
							}						
						}
						
						@Override
						public void exceptionOccurred(Exception exception) {
							exception.printStackTrace();
						}
					});					
				}				
			}));			
			return IFuture.DONE;
		}	
	}
		
	public void moveRandom(){
		IVector2	dest	= environment.getRandomPosition(Vector2Int.ZERO);
		
		IResultListener res = new DefaultResultListener() {

			@Override
			public void resultAvailable(Object result) {
				getExternalAccess().scheduleStep(new PerformStep());				
			}
			
			@Override
			public void exceptionOccurred(Exception exception) {
				exception.printStackTrace();
			}
		};
		
		moveToLocation(dest, res);
		getLogger().info("Reached point: "+dest);
	}
	
	public void carryOre(final ISpaceObject target)
		{
			finished = false;					
				
			// Move to the target.			
			IResultListener res = new DefaultResultListener() {

				@Override
				public void resultAvailable(Object result) {
					// Load ore at the target.					
					IResultListener res2 = new DefaultResultListener() {

						@Override
						public void resultAvailable(Object result) {
							finished	= ((Number)target.getProperty(ProduceOreTask.PROPERTY_CAPACITY)).intValue()==0;
							if(((Number)myself.getProperty(AnalyzeTargetTask.PROPERTY_ORE)).intValue()==0) {
								getExternalAccess().scheduleStep(new PerformStep());
							}
							else {
								// Move to the homebase.
								final ISpaceObject	homebase	= environment.getSpaceObjectsByType("homebase")[0];									
								IResultListener res3 = new DefaultResultListener() {

									@Override
									public void resultAvailable(Object result) {
										// Unload ore at the homebase.										
										IResultListener res4 = new DefaultResultListener() {

											@Override
											public void resultAvailable(Object result) {
												if (!finished)
													carryOre(target);
												else
													getExternalAccess().scheduleStep(new PerformStep());												
											}
											
											@Override
											public void exceptionOccurred(Exception exception) {
												exception.printStackTrace();
											}
										};
										loadOre(homebase, false, res4);										
									}
									
									@Override
									public void exceptionOccurred(Exception exception) {
										exception.printStackTrace();
									}
								};
								moveToLocation((IVector2)homebase.getProperty(Space2D.PROPERTY_POSITION), res3);
							}
							
						}
						
						@Override
						public void exceptionOccurred(Exception exception) {
							exception.printStackTrace();
						}
					};
					loadOre(target, true, res2);							
				}
				
				@Override
				public void exceptionOccurred(Exception exception) {
					exception.printStackTrace();
				}
			};
			moveToLocation((IVector2)target.getProperty(Space2D.PROPERTY_POSITION), res);
	}	
	
	public void loadOre(ISpaceObject target, boolean load, IResultListener res){		
		Map props = new HashMap();
		props.put(LoadOreTask.PROPERTY_TARGET, target);
		props.put(LoadOreTask.PROPERTY_LOAD, load);
		IEnvironmentSpace space = (IEnvironmentSpace)environment;
		Object	taskid	= space.createObjectTask(LoadOreTask.PROPERTY_TYPENAME, props, myself.getId());
		space.addTaskListener(taskid, myself.getId(), res);
	}
	
	public void moveToLocation(IVector2 dest, IResultListener res){		
		Map props = new HashMap();
		props.put(MoveTask.PROPERTY_DESTINATION, dest);
		props.put(MoveTask.PROPERTY_SCOPE, getExternalAccess());
		IEnvironmentSpace space = (IEnvironmentSpace)environment;		
		Object mtaskid = space.createObjectTask(MicroMoveTask.PROPERTY_TYPENAME, props, myself.getId());
		space.addTaskListener(mtaskid, myself.getId(), res);
	}
	
	public void informNewTarget(ISpaceObject target){
		this.scheduleStep(new CallSentryStep(target));
	}
	
	public class CallSentryStep extends CoordinationComponentStep {
		
		@CoordinationParameter
		public CoordinationSpaceData latest_target = null;
		
		public CallSentryStep(ISpaceObject target) {
			IVector2 position = (IVector2) target.getProperty("position");
			this.latest_target = new CoordinationSpaceData(position.getXAsDouble(), position.getYAsDouble());
		}
		
		@Override
		public IFuture<Void> execute(IInternalAccess ia) {
			System.out.println("#CallSentryStep# called in " + getAgentName());
			return IFuture.DONE;
		}		
	}
	
	public class LatestProducedTargetStep extends CoordinationComponentStep {
		
		@CoordinationParameter
		public CoordinationSpaceData latest_produced_target = null;

		@Override
		public IFuture<Void> execute(IInternalAccess ia) {
			System.out.println("#LatestProducedTargetStep# Received latest produced target: " + latest_produced_target);
			IVector2 position = new Vector2Double(latest_produced_target.getX(), latest_produced_target.getY());
			ISpaceObject latestTarget = environment.getNearestObject(position, null, "target");
						
			if (!targets.contains(latestTarget))
				targets.add(latestTarget);	
			
			return IFuture.DONE;
		}		
	}
}