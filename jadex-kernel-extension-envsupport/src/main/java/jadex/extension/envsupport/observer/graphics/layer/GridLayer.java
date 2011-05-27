package jadex.extension.envsupport.observer.graphics.layer;

import jadex.extension.envsupport.math.IVector2;


/**
 * A layer for displaying a grid.
 */
public class GridLayer extends Layer
{
	private IVector2	gridSize;

	/**
	 * Creates a new gridlayer.
	 * 
	 * @param gridSize size of each grid rectangle
	 * @param c color or color binding of the grid
	 */
	public GridLayer(IVector2 gridSize, Object c)
	{
		super(Layer.LAYER_TYPE_GRID, c);
		this.gridSize = gridSize.copy();
	}
	
	public IVector2 getGridSize()
	{
		return gridSize;
	}
}
