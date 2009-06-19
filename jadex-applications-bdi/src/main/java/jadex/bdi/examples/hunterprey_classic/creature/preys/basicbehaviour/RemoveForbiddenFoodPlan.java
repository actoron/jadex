package jadex.bdi.examples.hunterprey_classic.creature.preys.basicbehaviour;

import jadex.bdi.examples.hunterprey_classic.Food;
import jadex.bdi.examples.hunterprey_classic.Vision;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.Plan;

/**
 *  Remove forbidden food when out of sight.
 */
public class RemoveForbiddenFoodPlan extends Plan
{
	/**
	 *  The body.
	 */
	public void body()
	{
		IBeliefSet forb = getBeliefbase().getBeliefSet("forbidden_food");
		Food[] food = (Food[])forb.getFacts();
		Vision vision = (Vision)getBeliefbase().getBelief("vision").getFact();
		for(int i=0; i<food.length; i++)
		{
			if(!vision.contains(food[i]))
			{
				// avoid running back directly to forbidden food
				if (getBeliefbase().getBeliefSet("food").containsFact(food[i]))
					getBeliefbase().getBeliefSet("food").removeFact(food[i]);
				forb.removeFact(food[i]);
				//System.out.println("REMOVING forb");
			}
		}
	}
}
