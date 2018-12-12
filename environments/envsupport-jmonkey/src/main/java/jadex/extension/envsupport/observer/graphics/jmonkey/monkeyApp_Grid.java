package jadex.extension.envsupport.observer.graphics.jmonkey;


import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Line;

import jme3tools.optimize.GeometryBatchFactory;


public class monkeyApp_Grid
{
	private Node			_gridNode;

	private AssetManager	_assetManager;

	private float			_areaSize;

	private boolean			_isGrid;

	private float			_gridGaps;

	private int				_gridNumber;

	private float			_spaceSize;

	public monkeyApp_Grid(final float areaSize, final float spaceSize, final AssetManager assetManager, final boolean isGrid)
	{
		_assetManager = assetManager;
		_areaSize = areaSize;
		_spaceSize = spaceSize;
		_gridNode = new Node("gridNode");
		_isGrid = isGrid;
		_gridNumber = Math.max((int)_spaceSize, 10);
		_gridGaps = (float)_areaSize / _gridNumber;
		createGrid();

		if(_isGrid)
			shiftGrid();
		
		GeometryBatchFactory.optimize(_gridNode);
	}


	private void shiftGrid()
	{
		System.out.println("SHIFT!");
		float test = (float)100 / 30 / 2;
		Vector3f trans = new Vector3f(-test, -test, -test);
		_gridNode.setLocalTranslation(trans);
	}


	public Node getGrid()
	{
		return _gridNode;
	}


	private void createGrid()
	{


		// the three basic Directions
		putArrow(Vector3f.ZERO, new Vector3f(_areaSize, 0, 0), ColorRGBA.Red);
		putArrow(Vector3f.ZERO, new Vector3f(0, _areaSize, 0), ColorRGBA.Green);
		putArrow(Vector3f.ZERO, new Vector3f(0, 0, _areaSize), ColorRGBA.Blue);

		// the inner grid

		for(int i = 0; i <= _gridNumber; i++)
		{
			float punkt = _gridGaps * i;
			// the two sides
			putLine(new Vector3f(punkt, 0, 0), new Vector3f(punkt, _areaSize, 0), ColorRGBA.Gray);
			putLine(new Vector3f(0, 0, punkt), new Vector3f(0, _areaSize, punkt), ColorRGBA.Gray);
			putLine(new Vector3f(0, punkt, 0), new Vector3f(0, punkt, _areaSize), ColorRGBA.Gray);
			putLine(new Vector3f(0, punkt, 0), new Vector3f(_areaSize, punkt, 0), ColorRGBA.Gray);
			for(int j = 0; j <= _gridNumber; j++)
			{

				float punktj = _gridGaps * j;
				// x-line and y-line. The Basement
				putLine(new Vector3f(punkt, 0, 0), new Vector3f(punkt, 0, _areaSize), ColorRGBA.Gray);
				putLine(new Vector3f(0, 0, punktj), new Vector3f(_areaSize, 0, punktj), ColorRGBA.Gray);


			}
		}
	}


	private void putLine(Vector3f start, Vector3f end, ColorRGBA color)
	{
		Line line = new Line(start, end);
		putShape(line, color);
	}

	private void putArrow(Vector3f pos, Vector3f dir, ColorRGBA color)
	{
		Arrow arrow = new Arrow(dir);
		arrow.setArrowExtent(dir);
		arrow.setPointSize(0.2f);
		arrow.setLineWidth(5); // make arrow thicker
		putShape(arrow, color).setLocalTranslation(pos);
	}

	private Geometry putShape(Mesh shape, ColorRGBA color)
	{
		Geometry g = new Geometry("shape", shape);
		Material mat = new Material(_assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.getAdditionalRenderState().setWireframe(true);
		mat.setColor("Color", color);
		g.setMaterial(mat);
		_gridNode.attachChild(g);
		return g;
	}

}
