package sodekovs.mapscollision;

import jadex.extension.envsupport.math.IVector2;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Agent
@Description(value = "A Simple Agent")
/**
 * @author wolf.posdorfer
 */
public class CollisionAgent extends Humanoid
{

	Queue<MoveAction.Direction> _moveQueue = new ConcurrentLinkedQueue<MoveAction.Direction>();
	private boolean firstmove = true;

	private IVector2 _positionToGoTo = null;

	@AgentBody
	public void executebody()
	{
		super.inheritedExecuteBody();
	}

	@Override
	protected void act()
	{

		if (firstmove)
		{
			moveTo(MoveAction.getTotallyRandomDirection());
			firstmove = false;
		}
		else
		{
			if (_moveQueue.isEmpty())
			{
				MapService mapserv = MapService.getInstance();
				int x = mapserv.getValidPositions().size();
				IVector2 randomPosition = mapserv.getValidPositions().get(
						(int) (Math.random() * 10000 % x));
				_positionToGoTo = randomPosition;
				_moveQueue = Astar.calculateRoute(getMyPosition(), randomPosition);
			}

			moveTo(_moveQueue.poll());

			if (_positionToGoTo.getDistance(getMyPosition()).getAsInteger() < 4)
			{
				_myself.setProperty("reached", new Boolean(true));
			}
			else
			{
				_myself.setProperty("reached", new Boolean(false));
			}
		}
	}
}
