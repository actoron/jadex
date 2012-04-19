package jadex.extension.envsupport.observer.graphics.jmonkey.renderer;

import jadex.extension.envsupport.observer.graphics.drawable3d.Text3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;

import com.jme3.bounding.BoundingBox;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Spatial;



public class Text3dJMonkeyRenderer extends AbstractJMonkeyRenderer
{
	/** Text for jMonkey. */
	private String	text;

	public Spatial draw(DrawableCombiner3d dc, Primitive3d primitive,
			Object obj, ViewportJMonkey vp)
	{
		 text = (String)((Text3d)primitive).getText();
	    
	    	        BitmapFont fnt = assetManager.loadFont("Interface/Fonts/Default.fnt");
	    	        BitmapText txt = new BitmapText(fnt, false);
	    	        txt.setBox(new Rectangle(0, 0, 6, 3));
	    	        txt.setQueueBucket(Bucket.Transparent);
	    	        txt.setSize( 1.5f );
	    	        txt.setText(text);


		txt.setModelBound(new BoundingBox());

		return txt;


	}


}
