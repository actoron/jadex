/**
 * 
 */
package jadex.tools.bpmn.diagram.edit.parts;

import org.eclipse.draw2d.BendpointLocator;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.MidpointLocator;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.LabelEditPart;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.stp.bpmn.diagram.edit.parts.SequenceEdgeEditPart;


/**
 * @author Claas
 *
 */
public class SequenceEdgeEditPartWithCondition extends SequenceEdgeEditPart
{

	/**
	 * @param view
	 */
	public SequenceEdgeEditPartWithCondition(View view)
	{
		super(view);
	}
	
	/**
	 * Creates figure for this edit part.
	 * 
	 * @generated NOT
	 */
	@Override
	protected Connection createConnectionFigure()
	{
		return new EdgeFigureWithCondition();
	}
	
//	/**
//	 * @generated NOT
//	 */
//	protected boolean addFixedChildGen(EditPart childEditPart)
//	{
//		if (childEditPart instanceof WrappingLabelEditPart)
//		{
//			((WrappingLabelEditPart) childEditPart).setLabel(((EdgeFigureWithCondition)getConnectionFigure())
//					.getFigureSequenceEdgeConditionFigure());
//			return true;
//		}
//		return false;
//	}
//	
//
//	/**
//	 * @generated NOT
//	 */
//	protected boolean removeFixedChild(EditPart childEditPart)
//	{
//		if (childEditPart instanceof WrappingLabelEditPart)
//		{
//			return true;
//		}
//		return false;
//	}

	
	
	/**
	 * Extend the BPMN EdgeFigure to support customization
	 * 
	 * @generated NOT
	 * @author Claas
	 */
	public class EdgeFigureWithCondition extends EdgeFigure {
		
		/**
		 * @generated NOT
		 */
		private WrappingLabel fFigureSequenceEdgeConditionFigure;
		
		/**
		 * @generated NOT 
		 */
		public EdgeFigureWithCondition()
		{
			super();
			//this.setForegroundColor(org.eclipse.draw2d.ColorConstants.red);
			this.createContents();
		}
		
		
		/**
		 * @generated NOT
		 */
		private void createContents()
		{

			fFigureSequenceEdgeConditionFigure = new WrappingLabel();
//			fFigureSequenceEdgeConditionFigure.setText("SOME TEXT");

			//new BendpointLocator(this, 0);
			//new MidpointLocator(this, 0)
			this.add(fFigureSequenceEdgeConditionFigure, new ConnectionLocator(this, ConnectionLocator.SOURCE));

		}


		/**
		 * @generated NOT
		 */
		public WrappingLabel getFigureSequenceEdgeConditionFigure()
		{
			return fFigureSequenceEdgeConditionFigure;
		}
	}

}
