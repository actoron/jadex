package sodekovs.mapscollision;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.micro.MicroAgent;

import java.util.HashMap;

import javax.swing.AbstractAction;

/**
 * Used for spawning an Agent
 * 
 * @author wolf.posdorfer
 * 
 */
public class Spawn
{
	/**
	 * Different Types of Agents to Spawn
	 * 
	 * @author wolf
	 * 
	 */
	public enum Type
	{
		Zombie("Zombie"), Soldier("Soldier"), Zivi("Zivi"), Scout("Scout"), Medic("Medic"), Hospital(
				"Hospital"), Goku("Goku"), Poison("Poison"), Helicopter("Helicopter");

		private String _s;

		Type(String s)
		{
			_s = s;
		}

		public String toString()
		{
			return _s;
		}
	}

	/**
	 * Different Types of SpaceObjects to Spawn
	 */
	public enum ObjType
	{
		Beam_Vertical("beam_vertical"), Beam_Horizontal("beam_horizontal"), House("house"), Cloud(
				"cloud");
		private String _s;

		ObjType(String s)
		{
			_s = s;
		}

		public String toString()
		{
			return _s;
		}
	}

	/**
	 * Spawn an Agent
	 * 
	 * @param environment
	 * @param microagent
	 *            , preferably the Agent calling this
	 * @param agenttype
	 *            of {@link Type}
	 * @param position
	 *            Position where to Spawn
	 * @param executeAfterCreation
	 *            if !=null will be invoked after the Agent is created, this can
	 *            also be used as a confirmation of creation
	 */
	public static IFuture<IComponentIdentifier> spawnAgent(final Grid2D environment,
			final MicroAgent microagent, Type agenttype, final IVector2 position,
			final AbstractAction executeAfterCreation)
	{
		final String clazz;

		final Future<IComponentIdentifier> fut = new Future<IComponentIdentifier>();

		switch (agenttype)
			{
			case Zombie:
				clazz = "de/agent/zombie/ZombieAgent.class";
				break;
			case Soldier:
				clazz = "de/agent/soldier/SoldierAgent.class";
				break;
			case Zivi:
				clazz = "de/agent/men/MenAgent.class";
				break;
			case Medic:
				clazz = "de/agent/medic/MedicAgent.class";
				break;
			case Scout:
				clazz = "de/agent/scout/ScoutAgent.class";
				break;
			case Hospital:
				clazz = "de/agent/medic/hospital/HospitalAgent.class";
				break;
			case Goku:
				clazz = "de/agent/goku/GokuAgent.class";
				break;
			case Poison:
				clazz = "de/agent/poison/PoisonAgent.class";
				break;
			case Helicopter:
				clazz = "de/agent/helicopter/HelicopterAgent.class";
				break;
			default:
				clazz = "de/agent/men/MenAgent.class";
				break;
			}

		IFuture<IComponentManagementService> service = SServiceProvider.getService(
				microagent.getServiceProvider(), IComponentManagementService.class,
				RequiredServiceInfo.SCOPE_GLOBAL);
		service.addResultListener(new DefaultResultListener<IComponentManagementService>()
		{

			@Override
			public void resultAvailable(final IComponentManagementService cms)
			{

				IFuture<IComponentIdentifier> futurecompidentif = cms.createComponent(null, clazz,
						new CreationInfo(microagent.getParentAccess().getComponentIdentifier()), null);
				futurecompidentif
						.addResultListener(new DefaultResultListener<IComponentIdentifier>()
						{

							public void exceptionOccurred(Exception e)
							{
								e.printStackTrace();
								fut.setException(e);
							}

							@Override
							public void resultAvailable(final IComponentIdentifier compident)
							{
								IFuture<IComponentDescription> compdes = cms
										.getComponentDescription(compident);
								compdes.addResultListener(new DefaultResultListener<IComponentDescription>()
								{

									public void exceptionOccurred(Exception e)
									{
										e.printStackTrace();
										fut.setException(e);
									}

									@Override
									public void resultAvailable(
											IComponentDescription compdescription)
									{
										ISpaceObject spaceobj = environment
												.getAvatar(compdescription);
										spaceobj.setProperty(Space2D.PROPERTY_POSITION, position);

										fut.setResult(compident);

										if (executeAfterCreation != null)
										{
											executeAfterCreation.actionPerformed(null);
										}

									}
								});
							}
						});
			}
		});
		return fut;
	}

	public static ISpaceObject spawnObject(final Grid2D environment, ObjType type, IVector2 position)
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(Space2D.PROPERTY_POSITION, position);
		ISpaceObject result = environment.createSpaceObject(type.toString(), map, null);
		return result;
	}

}
