package jadex.bdi.examples.cleanerworld2.observer;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.bdi.examples.cleanerworld2.Configuration;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.DrawableCombiner;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.IDrawable;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.ScalableRegularPolygon;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.ScalableTexturedRectangle;
import jadex.bdi.planlib.simsupport.common.graphics.layer.ILayer;
import jadex.bdi.planlib.simsupport.common.graphics.layer.TiledLayer;
import jadex.bdi.planlib.simsupport.common.graphics.order.YOrder;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

public class InitializeObserverPlan extends Plan
{
	public void body()
	{
		IBeliefbase b = getBeliefbase();
		
		b.getBelief("environment_name").setFact(Configuration.ENVIRONMENT_NAME);
		b.getBelief("preserve_aspect_ratio").setFact(Boolean.TRUE);
		b.getBelief("force_java2d").setFact(Boolean.FALSE);
		b.getBelief("draw_order").setFact(new YOrder());
		
		Map theme = new HashMap();
		
		String imgPath = this.getClass().getPackage().getName().replaceAll("observer", "").concat("images.").replaceAll("\\.", "/");
		
		DrawableCombiner cleanerDrawable = new DrawableCombiner();
		String cleanerImage = imgPath.concat("cleaner.png");
		cleanerDrawable.addDrawable(new ScalableRegularPolygon(new Vector2Double(Configuration.CLEANER_VISUAL_RANGE.getAsDouble() * 2.0), 24, new Color(1.0f, 1.0f, 0.0f, 0.5f)));
		cleanerDrawable.addDrawable(new ScalableTexturedRectangle(new Vector2Double(1.0), cleanerImage));
		//cleanerDrawable.addDrawable(new RotatingColoredTriangle(new Vector2Double(1.0), new Vector2Double(1.0), new Vector2Double(0.0), Color.BLUE));
		theme.put("cleaner", cleanerDrawable);
		
		String wasteImage = imgPath.concat("waste.png");
		IDrawable wasteDrawable = new ScalableTexturedRectangle(new Vector2Double(0.5), wasteImage);
		theme.put("waste", wasteDrawable);
		
		IDrawable wbDrawable = new ScalableTexturedRectangle(Configuration.WASTE_BIN_SIZE, imgPath + "wastebin.png");
		theme.put("waste_bin", wbDrawable);
		
		IDrawable csDrawable = new ScalableTexturedRectangle(Configuration.CHARGING_STATION_SIZE, imgPath + "chargingstation.png");
		theme.put("charging_station", csDrawable);
		
		List themes = (List) b.getBelief("object_themes").getFact();
		themes.add(theme);
		
		ILayer background =  new TiledLayer(Configuration.BACKGROUND_TILE_SIZE,
										    Configuration.BACKGROUND_TILE);
		List preLayers = (List) b.getBelief("prelayers").getFact();
		preLayers.add(background);
		
		IGoal start = createGoal("simobs_start");
		dispatchSubgoalAndWait(start);
	}

}
