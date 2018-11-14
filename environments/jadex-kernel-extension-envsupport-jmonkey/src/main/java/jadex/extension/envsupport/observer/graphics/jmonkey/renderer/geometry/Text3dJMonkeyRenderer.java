package jadex.extension.envsupport.observer.graphics.jmonkey.renderer.geometry;

import java.awt.Color;

import com.jme3.bounding.BoundingBox;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Text3d;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.AbstractJMonkeyRenderer;



public class Text3dJMonkeyRenderer extends AbstractJMonkeyRenderer
{
//	/** Text for jMonkey. */
//	private String	text;

	public Spatial draw(DrawableCombiner3d dc, Primitive3d primitive,
			SpaceObject sobj, ViewportJMonkey vp)
	{
		Node textnode = new Node(identifier);
		
		Text3d textP = (Text3d)primitive;
		String text = Text3d.getReplacedText(dc, sobj, textP.getText(), vp);
		 if (text==null)
		 {
			text= ((Text3d)primitive).getText();
		 }
			Color c =  (Color)primitive.getColor();
			float alpha= ((float)c.getAlpha())/255;
			ColorRGBA color = new ColorRGBA(((float)c.getRed())/255,((float)c.getGreen())/255,((float)c.getBlue())/255, alpha);
	    
	    	        BitmapFont fnt = assetManager.loadFont("Interface/Fonts/Default.fnt");
	    	        BitmapText txt = new BitmapText(fnt, false);
	    	        
	    	        //TODO: what is the use of setBox?
	    	        txt.setBox(new Rectangle(-1, -1, 2, 2));
	    	        txt.setQueueBucket(Bucket.Transparent);
	    	        txt.setSize( 0.1f );
	    	        txt.setText(text);
	    	        txt.setColor(color);
	    	        
					

		txt.setModelBound(new BoundingBox());

		textnode.attachChild(txt);
		
		
		return textnode;


	}


}
