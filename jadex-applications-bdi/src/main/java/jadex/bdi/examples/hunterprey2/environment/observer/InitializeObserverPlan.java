package jadex.bdi.examples.hunterprey2.environment.observer;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.bdi.examples.hunterprey2.Configuration;
import jadex.bdi.examples.hunterprey2.Creature;
import jadex.bdi.examples.hunterprey2.Environment;
import jadex.bdi.examples.hunterprey2.WorldObject;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.DrawableCombiner;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.IDrawable;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.RegularPolygon;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.TexturedRectangle;
import jadex.bdi.planlib.simsupport.common.graphics.layer.ILayer;
import jadex.bdi.planlib.simsupport.common.graphics.layer.TiledLayer;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

public class InitializeObserverPlan extends Plan
{
	public void body()
	{
		IBeliefbase b = getBeliefbase();
		
		b.getBelief("environment_name_obs").setFact(Configuration.ENVIRONMENT_NAME);
		b.getBelief("preserve_aspect_ratio").setFact(Boolean.TRUE);
		b.getBelief("force_java2d").setFact(Boolean.FALSE);
		
		Map theme = new HashMap();
		
		String imgPath = this.getClass().getPackage().getName().replaceAll("environment\\.observer", "").concat("images.").replaceAll("\\.", "/");
		
		DrawableCombiner hunterDrawable = new DrawableCombiner();
		String hunterImage = imgPath.concat("hunter.png");
		hunterDrawable.addDrawable(new RegularPolygon(new Vector2Double(Creature.CREATURE_VISUAL_RANGE.getAsDouble() * 2.0), 24, new Color(1.0f, 1.0f, 0.0f, 0.5f), false));
		hunterDrawable.addDrawable(new TexturedRectangle(Creature.CREATURE_SIZE, hunterImage, false));
		theme.put(Environment.OBJECT_TYPE_HUNTER, hunterDrawable);
		
		DrawableCombiner preyDrawable = new DrawableCombiner();
		String preyImage = imgPath.concat("prey.png");
		preyDrawable.addDrawable(new RegularPolygon(new Vector2Double(Creature.CREATURE_VISUAL_RANGE.getAsDouble() * 2.0), 24, new Color(1.0f, 1.0f, 0.0f, 0.5f), false));
		preyDrawable.addDrawable(new TexturedRectangle(Creature.CREATURE_SIZE, preyImage, false));
		theme.put(Environment.OBJECT_TYPE_PREY, preyDrawable);
		
		DrawableCombiner foodDrawable = new DrawableCombiner();
		String foodImage = imgPath.concat("food.png");
		//IDrawable foodDrawable = new ScalableTexturedRectangle(new Vector2Double(0.5), foodImage);
		foodDrawable.addDrawable(new TexturedRectangle(WorldObject.WORLD_OBJECT_SIZE, foodImage, false));
		theme.put(Environment.OBJECT_TYPE_FOOD, foodDrawable);
		
		DrawableCombiner obstacleDrawable = new DrawableCombiner();
		obstacleDrawable.addDrawable(new TexturedRectangle(WorldObject.WORLD_OBJECT_SIZE, imgPath + "obstacle.png", false));
		theme.put(Environment.OBJECT_TYPE_OBSTACLE, obstacleDrawable);
		
		
		List themes = (List) b.getBelief("object_themes").getFact();
		themes.add(theme);
		
		ILayer background =  new TiledLayer(Configuration.BACKGROUND_TILE_SIZE,
										    Configuration.BACKGROUND_TILE);
		List preLayers = (List) b.getBelief("prelayers").getFact();
		preLayers.add(background);
		
		System.out.println("Starting Observer");
		IGoal start = createGoal("simobs_start");
		dispatchSubgoalAndWait(start);
	}

}
