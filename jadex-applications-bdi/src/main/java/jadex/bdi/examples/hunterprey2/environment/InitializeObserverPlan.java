package jadex.bdi.examples.hunterprey2.environment;

import jadex.bdi.examples.hunterprey2.Configuration;
import jadex.bdi.examples.hunterprey2.Creature;
import jadex.bdi.examples.hunterprey2.WorldObject;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.DrawableCombiner;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.Rectangle;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.TexturedRectangle;
import jadex.bdi.planlib.simsupport.common.graphics.layer.GridLayer;
import jadex.bdi.planlib.simsupport.common.graphics.layer.ILayer;
import jadex.bdi.planlib.simsupport.common.graphics.layer.TiledLayer;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;
import jadex.bdi.planlib.simsupport.observer.capability.plugin.IObserverCenterPlugin;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InitializeObserverPlan extends Plan
{
	public void body()
	{
		boolean use_old_gui = false;
		if(getBeliefbase().containsBelief("use_old_gui"))
		{
			use_old_gui = ((Boolean) getBeliefbase().getBelief("use_old_gui").getFact()).booleanValue();
		}
		
		if (use_old_gui)
		{
			createGUI();
		}
		else
		{
			initializeObserver();
		}
	}

	/**
	 * Create a "old-style" Observer GUI 
	 * needed for remote observers.
	 */
	protected void createGUI()
	{
		ObserverGui gui;
		try {
			gui = new ObserverGui(getExternalAccess());
			getBeliefbase().getBelief("gui").setFact(gui);
		} catch (Exception e) {
			fail(e);
		}
	}

	/**
	 * Initialize the "new-style" OpenGL Observer
	 * @return true if the observer goal succeded
	 */
	protected boolean initializeObserver()
	{
		
		if (getBeliefbase().getBelief("environment").getFact() == null)
		{
			waitForFactChanged("environment");
		}
		// insert plugin - this MUST get the agent scope, so don't use the
		// capability belief reference in ADF for creation
		IObserverCenterPlugin plugin = new EnvironmentControlPlugin(getExternalAccess());
		getBeliefbase().getBeliefSet("custom_plugins").addFact(plugin);
		
		getBeliefbase().getBelief("environment_name_obs").setFact(Configuration.ENVIRONMENT_NAME);
		
		Map theme = new HashMap();
		String imgPath = this.getClass().getPackage().getName().replaceAll("environment", "").concat("images.").replaceAll("\\.", "/");

		DrawableCombiner hunterCombiner = new DrawableCombiner();
		String hunterImage = imgPath.concat("hunter.png");
		for (int i=Creature.CREATURE_VISUAL_RANGE.getAsInteger()-1; i > 0 ; i--)
		{
			float alpha = (float) (0.1f + (0.5 * 1 / Creature.CREATURE_VISUAL_RANGE.getAsInteger()));
			hunterCombiner.addDrawable(new Rectangle(new Vector2Double((Creature.CREATURE_VISUAL_RANGE.getAsDouble()-i) * 2.0), false, new Color(1.0f, 1.0f, 0.0f, alpha)), -2);
		}
		hunterCombiner.addDrawable(new TexturedRectangle(Creature.CREATURE_SIZE, false, hunterImage), 0);
		theme.put(Environment.OBJECT_TYPE_HUNTER, hunterCombiner);

		
		DrawableCombiner preyCombiner = new DrawableCombiner();
		String preyImage = imgPath.concat("prey.png");
		for (int i=Creature.CREATURE_VISUAL_RANGE.getAsInteger()-1; i > 0 ; i--)
		{
			float alpha = (float) (0.1f + (0.5 * 1 / Creature.CREATURE_VISUAL_RANGE.getAsInteger()));
			preyCombiner.addDrawable(new Rectangle(new Vector2Double((Creature.CREATURE_VISUAL_RANGE.getAsDouble()-i) * 2.0), false, new Color(1.0f, 1.0f, 0.0f, alpha)), -2);
		}
		preyCombiner.addDrawable(new TexturedRectangle(Creature.CREATURE_SIZE, false, preyImage), 0);
		theme.put(Environment.OBJECT_TYPE_PREY, preyCombiner);
		
		DrawableCombiner foodCombiner = new DrawableCombiner();
		String foodImage = imgPath.concat("food.png");
		foodCombiner.addDrawable(new TexturedRectangle(WorldObject.WORLD_OBJECT_SIZE.copy().multiply(0.7), false, foodImage), -1);
		theme.put(Environment.OBJECT_TYPE_FOOD, foodCombiner);
		
		DrawableCombiner obstacleCombiner = new DrawableCombiner();
		String obstacleImage = imgPath.concat("obstacle.png");
		obstacleCombiner.addDrawable(new TexturedRectangle(WorldObject.WORLD_OBJECT_SIZE.copy().multiply(0.9), false, obstacleImage), 1);
		theme.put(Environment.OBJECT_TYPE_OBSTACLE, obstacleCombiner);
		
		
		Map themes = (Map) getBeliefbase().getBelief("object_themes").getFact();
		themes.put("default",theme);
		
		ILayer background =  new TiledLayer(Configuration.BACKGROUND_TILE_SIZE,
										    Configuration.BACKGROUND_TILE);
		
		List preLayerTheme = new ArrayList();
		preLayerTheme.add(background);
		preLayerTheme.add(new GridLayer(new Vector2Double(1.0), Color.BLACK));
		Map preLayerThemes = (Map) getBeliefbase().getBelief("prelayer_themes").getFact();
		preLayerThemes.put("default", preLayerTheme);
		
		IGoal start = createGoal("simobs_start");
		dispatchSubgoalAndWait(start);
		
		return start.isSucceeded();
	}
	
}
