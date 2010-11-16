/**
 * 
 */
package jadex.editor.bpmn.diagram.edit.parts;

import jadex.editor.bpmn.editor.properties.JadexSequencePropertiesSection;
import jadex.editor.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.stp.bpmn.SequenceEdge;
import org.eclipse.stp.bpmn.diagram.edit.parts.SequenceEdgeEditPart;
import org.eclipse.swt.graphics.Color;


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
		EdgeFigureWithCondition edgeFigure = new EdgeFigureWithCondition();
		
		String condition = getCondition();
		WrappingLabel conditionFigure = new WrappingLabel();
		conditionFigure.setText(condition);
		
		setConditionLabelStyles(conditionFigure);
		
		ConnectionLocator locator = new ConnectionLocator(edgeFigure, ConnectionLocator.MIDDLE);
		locator.setRelativePosition(PositionConstants.SOUTH_EAST);
		locator.setGap(10);
		edgeFigure.add(conditionFigure, locator);
		edgeFigure.setFigureSequenceEdgeConditionFigure(conditionFigure);

		return edgeFigure;
	}

	/**
	 * Get the condition from annotation
	 * @return
	 */
	private String getCondition()
	{
		String condition = null;
		Object edgeModel = getPrimaryView().getElement();
		if (edgeModel != null && edgeModel instanceof SequenceEdge)
		{
			SequenceEdge edge = (SequenceEdge) edgeModel;
			condition = JadexBpmnPropertiesUtil.getJadexEAnnotationDetail(edge, JadexSequencePropertiesSection.SEQUENCE_PROPERTIES_ANNOTATION_IDENTIFIER, JadexSequencePropertiesSection.SEQUENCE_PROPERTIES_CONDITION_DETAIL_IDENTIFIER);
		}
		return condition;
	}
	
	private void setConditionLabelStyles(WrappingLabel label)
	{
		if (label.getText() != null && !label.getText().isEmpty())
		{
			label.setForegroundColor(ColorConstants.darkBlue);
			//label.setBorder(new LineBorder());
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionEditPart#refresh()
	 */
	@Override
	public void refresh()
	{
		super.refresh();
		EdgeFigureWithCondition edgeFigure = (EdgeFigureWithCondition) getFigure();
		if (edgeFigure != null)
		{
			WrappingLabel label = edgeFigure.getFigureSequenceEdgeConditionFigure();
			label.setText(getCondition());
			label.repaint();
		}
	}




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
			this.createContents();
		}
		
		
		/**
		 * @generated NOT
		 */
		private void createContents()
		{

		}

		/**
		 * @return the fFigureSequenceEdgeConditionFigure
		 * @generated NOT
		 */
		public WrappingLabel getFigureSequenceEdgeConditionFigure()
		{
			return fFigureSequenceEdgeConditionFigure;
		}


		/**
		 * @param fFigureSequenceEdgeConditionFigure the fFigureSequenceEdgeConditionFigure to set
		 * @generated NOT
		 */
		public void setFigureSequenceEdgeConditionFigure(WrappingLabel sequenceEdgeConditionFigure)
		{
			this.fFigureSequenceEdgeConditionFigure = sequenceEdgeConditionFigure;
		}
		
		
	}

}
