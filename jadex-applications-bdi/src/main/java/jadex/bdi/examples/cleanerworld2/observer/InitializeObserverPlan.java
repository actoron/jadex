package jadex.bdi.examples.cleanerworld2.observer;

import jadex.bdi.examples.cleanerworld2.Configuration;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.DrawableCombiner;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.IDrawable;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.Rectangle;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.RegularPolygon;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.TexturedRectangle;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.Triangle;
import jadex.bdi.planlib.simsupport.common.graphics.layer.ILayer;
import jadex.bdi.planlib.simsupport.common.graphics.layer.TiledLayer;
import jadex.bdi.planlib.simsupport.common.graphics.order.YOrder;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;
import jadex.bdi.runtime.IBeliefbase;
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
		IBeliefbase b = getBeliefbase();
		
		b.getBelief("environment_name").setFact(Configuration.ENVIRONMENT_NAME);
		b.getBelief("preserve_aspect_ratio").setFact(Boolean.TRUE);
		b.getBelief("force_java2d").setFact(Boolean.FALSE);
		//b.getBelief("obscap.invert_y_axis").setFact(Boolean.TRUE);
		b.getBelief("draw_order").setFact(new YOrder());
		
		Map theme = new HashMap();
		
		String imgPath = this.getClass().getPackage().getName().replaceAll("observer", "").concat("images.").replaceAll("\\.", "/");
		
		DrawableCombiner combiner = new DrawableCombiner();
		String cleanerImage = imgPath.concat("cleaner.png");
		combiner.addDrawable(new RegularPolygon(new Vector2Double(Configuration.CLEANER_VISUAL_RANGE.getAsDouble() * 2.0), new Vector2Double(0.0), false, new Color(1.0f, 1.0f, 0.0f, 0.5f), 24), -1);
		combiner.addDrawable(new TexturedRectangle(new Vector2Double(1.0), new Vector2Double(0.0), false, cleanerImage), 0);
		//cleanerDrawable.addDrawable(new RotatingColoredTriangle(new Vector2Double(1.0), new Vector2Double(1.0), new Vector2Double(0.0), Color.BLUE));
		theme.put("cleaner", combiner);
		
		combiner = new DrawableCombiner(new Vector2Double(0.5));
		String wasteImage = imgPath.concat("waste.png");
		IDrawable wasteDrawable = new TexturedRectangle(new Vector2Double(0.5), new Vector2Double(0.0), false, wasteImage);
		combiner.addDrawable(wasteDrawable);
		theme.put("waste", combiner);
		
		combiner = new DrawableCombiner(Configuration.WASTE_BIN_SIZE);
		IDrawable wbDrawable = new TexturedRectangle(Configuration.WASTE_BIN_SIZE, new Vector2Double(0.0), false, imgPath + "wastebin.png");
		combiner.addDrawable(wbDrawable);
		theme.put("waste_bin", combiner);
		
		combiner = new DrawableCombiner(Configuration.CHARGING_STATION_SIZE);
		IDrawable csDrawable = new TexturedRectangle(Configuration.CHARGING_STATION_SIZE, new Vector2Double(0.0), false, imgPath + "chargingstation.png");
		combiner.addDrawable(csDrawable);
		theme.put("charging_station", combiner);
		
		Map themes = (Map) b.getBelief("object_themes").getFact();
		themes.put("default", theme);
		
		theme = new HashMap();
		combiner = new DrawableCombiner();
		combiner.addDrawable(new RegularPolygon(new Vector2Double(Configuration.CLEANER_VISUAL_RANGE.getAsDouble() * 2.0), new Vector2Double(0.0), false, new Color(1.0f, 1.0f, 0.0f, 0.5f), 24), -1);
		combiner.addDrawable(new Triangle(new Vector2Double(1.0), new Vector2Double(0.0), true, Color.BLUE), 0);
		theme.put("cleaner", combiner);
		
		combiner = new DrawableCombiner(new Vector2Double(0.5));
		wasteDrawable = new RegularPolygon(new Vector2Double(0.5), new Vector2Double(0.0), false, Color.RED, 24);
		combiner.addDrawable(wasteDrawable);
		theme.put("waste", combiner);
		
		combiner = new DrawableCombiner(Configuration.WASTE_BIN_SIZE);
		wbDrawable = new Rectangle(new Vector2Double(1.0), new Vector2Double(0.0), false, Color.GREEN);
		combiner.addDrawable(wbDrawable);
		theme.put("waste_bin", combiner);
		
		combiner = new DrawableCombiner(Configuration.CHARGING_STATION_SIZE);
		csDrawable = wbDrawable = new Rectangle(new Vector2Double(1.0), new Vector2Double(0.0), false, Color.YELLOW);
		combiner.addDrawable(csDrawable);
		theme.put("charging_station", combiner);
		
		themes.put("abstract", theme);
		
		
		ILayer background =  new TiledLayer(Configuration.BACKGROUND_TILE_SIZE,
										    Configuration.BACKGROUND_TILE);
		List preLayerTheme = new ArrayList();
		preLayerTheme.add(background);
		Map preLayerThemes = (Map) b.getBelief("prelayer_themes").getFact();
		preLayerThemes.put("default", preLayerTheme);
		
		b.getBelief("selected_theme").setFact("default");
		
		IGoal start = createGoal("simobs_start");
		dispatchSubgoalAndWait(start);
	}

}
