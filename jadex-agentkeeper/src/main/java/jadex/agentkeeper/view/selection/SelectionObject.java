package jadex.agentkeeper.view.selection;

import java.nio.FloatBuffer;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.Box;
import com.jme3.util.BufferUtils;

public class SelectionObject extends Node
{
	private SimpleApplication app;
	private Material defaultmat;
	private Box box;
	private Geometry gbox;
	private WireBox wireBox;
	private Geometry gwireBox;
	

	public SelectionObject(SimpleApplication app, float x, float y, float z)
	{
		this.setName("SelectionNode");
		this.app = app;
		
		defaultmat = new Material(this.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		defaultmat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);

		defaultmat.setColor("Color", ColorRGBA.Blue.mult(new ColorRGBA(1, 1, 1, 0.15f)));
		defaultmat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		
		createWireBox(x, y, z);
		createTransparentBox(x, y, z);
	}

	private void createTransparentBox(float x, float y, float z)
	{
		box = new Box(new Vector3f(0f, 0.5f, 0f), x, y, z);
		box.setDynamic();
		gbox = new Geometry("box", box);
		gbox.setMaterial(defaultmat);
		gbox.setCullHint(CullHint.Never);
		gbox.setQueueBucket(Bucket.Transparent);
		
		this.attachChild(gbox);
		
		
	}

	private void createWireBox(float x, float y, float z)
	{
		wireBox = new WireBox(x,y,z);
		wireBox.setDynamic();
		gwireBox = new Geometry("wbox", wireBox);
		Material mat2 = defaultmat.clone();
		mat2.setColor("Color", ColorRGBA.Black);
		gwireBox.setMaterial(mat2);
		
		this.attachChild(gwireBox);
		
	}
	
	public void updateSelectionVertices(SelectionArea selectionArea)
	{
		if(selectionArea != null) {
            selectionArea.start.x = selectionArea.start.x - 0;
            selectionArea.start.y = selectionArea.start.y - 0;
            selectionArea.end.x = selectionArea.end.x + 0;
            selectionArea.end.y = selectionArea.end.y + 0;
        }else {
            selectionArea = new SelectionArea(new Vector2f(0, 0), new Vector2f(0, 0));
        }
//		selectionBox.updateGeometry(center, appScaled/2, appScaled, appScaled/2);
		
		float sx = selectionArea.start.x;
		float sy = 1.5f*selectionArea.getScale();
		float sz = selectionArea.start.y;
		
		float ex = selectionArea.end.x;
		float ey = 1.5f*selectionArea.getScale();
		float ez = selectionArea.end.y;
		
		gbox.setLocalTranslation(sx, sy, sz);
		
//		box.updateGeometry(new Vector3f(sx,sy,sz), new Vector3f(ex,ey,ez));


		
	}
	
	
	private void createSelectionBox()
	{
		

	}

}
