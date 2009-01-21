package jadex.bdi.examples.hunterprey2.environment;

import jadex.bdi.examples.hunterprey2.Configuration;
import jadex.bdi.examples.hunterprey2.Creature;
import jadex.bdi.examples.hunterprey2.Food;
import jadex.bdi.examples.hunterprey2.Location;
import jadex.bdi.examples.hunterprey2.Obstacle;
import jadex.bdi.examples.hunterprey2.WorldObject;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.DrawableCombiner;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.Rectangle;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.TexturedRectangle;
import jadex.bdi.planlib.simsupport.common.graphics.layer.GridLayer;
import jadex.bdi.planlib.simsupport.common.graphics.layer.ILayer;
import jadex.bdi.planlib.simsupport.common.graphics.layer.TiledLayer;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;
import jadex.bdi.planlib.simsupport.observer.capability.plugin.IObserverCenterPlugin;
import jadex.bdi.planlib.starter.StartAgentInfo;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

import java.awt.Canvas;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InitializeObserverPlan extends Plan
{
	public void body()
	{
		//createGUI();
		
		
		insertPlugin();
		initializeObserver();
		
	}
	
	
	protected void insertPlugin()
	{
		IObserverCenterPlugin plugin = new EnvironmentObserverPlugin(getExternalAccess());
		getBeliefbase().getBeliefSet("custom_plugins").addFact(plugin);
	}
	
	protected void createGUI()
	{
		Canvas worldmap = null;
		if (initializeObserver() && ((Boolean) getBeliefbase().getBelief("custom_gui").getFact()).booleanValue())
		{
			worldmap = (Canvas) getBeliefbase().getBelief("worldmap").getFact();
		}

		ObserverGui gui;
		try {
			gui = new ObserverGui(getExternalAccess(), worldmap);
			getBeliefbase().getBelief("gui").setFact(gui);
		} catch (Exception e) {
			fail(e);
		}
		
	}
	
	


	
	
	protected boolean initializeObserver()
	{
		
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
