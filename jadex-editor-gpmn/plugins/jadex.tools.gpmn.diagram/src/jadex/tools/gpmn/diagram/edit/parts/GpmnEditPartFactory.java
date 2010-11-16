/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.edit.parts;

import jadex.tools.gpmn.diagram.part.GpmnVisualIDRegistry;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ITextAwareEditPart;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

/**
 * @generated
 */
public class GpmnEditPartFactory implements EditPartFactory
{
	
	/**
	 * @generated
	 */
	public EditPart createEditPart(EditPart context, Object model)
	{
		if (model instanceof View)
		{
			View view = (View) model;
			switch (GpmnVisualIDRegistry.getVisualID(view))
			{
				
				case GpmnDiagramEditPart.VISUAL_ID:
					return new GpmnDiagramEditPart(view);
					
				case ActivationPlanEditPart.VISUAL_ID:
					return new ActivationPlanEditPart(view);
					
				case ActivationPlanNameEditPart.VISUAL_ID:
					return new ActivationPlanNameEditPart(view);
					
				case SubProcessEditPart.VISUAL_ID:
					return new SubProcessEditPart(view);
					
				case SubProcessNameEditPart.VISUAL_ID:
					return new SubProcessNameEditPart(view);
					
				case BpmnPlanEditPart.VISUAL_ID:
					return new BpmnPlanEditPart(view);
					
				case BpmnPlanNameEditPart.VISUAL_ID:
					return new BpmnPlanNameEditPart(view);
					
				case GoalEditPart.VISUAL_ID:
					return new GoalEditPart(view);
					
				case GoalNameEditPart.VISUAL_ID:
					return new GoalNameEditPart(view);
					
				case ActivationEdgeEditPart.VISUAL_ID:
					return new ActivationEdgeEditPart(view);
					
				case ActivationEdgeOrderEditPart.VISUAL_ID:
					return new ActivationEdgeOrderEditPart(view);
					
				case PlanEdgeEditPart.VISUAL_ID:
					return new PlanEdgeEditPart(view);
					
				case SuppressionEdgeEditPart.VISUAL_ID:
					return new SuppressionEdgeEditPart(view);
					
				case VirtualActivationEdgeEditPart.VISUAL_ID:
					return new VirtualActivationEdgeEditPart(view);
					
				case VirtualActivationOrderEditPart.VISUAL_ID:
					return new VirtualActivationOrderEditPart(view);
					
			}
		}
		return createUnrecognizedEditPart(context, model);
	}
	
	/**
	 * @generated
	 */
	private EditPart createUnrecognizedEditPart(EditPart context, Object model)
	{
		// Handle creation of unrecognized child node EditParts here
		return null;
	}
	
	/**
	 * @generated
	 */
	public static CellEditorLocator getTextCellEditorLocator(
			ITextAwareEditPart source)
	{
		if (source.getFigure() instanceof WrappingLabel)
			return new TextCellEditorLocator((WrappingLabel) source.getFigure());
		else
		{
			return new LabelCellEditorLocator((Label) source.getFigure());
		}
	}
	
	/**
	 * @generated
	 */
	static private class TextCellEditorLocator implements CellEditorLocator
	{
		
		/**
		 * @generated
		 */
		private WrappingLabel wrapLabel;
		
		/**
		 * @generated
		 */
		public TextCellEditorLocator(WrappingLabel wrapLabel)
		{
			this.wrapLabel = wrapLabel;
		}
		
		/**
		 * @generated
		 */
		public WrappingLabel getWrapLabel()
		{
			return wrapLabel;
		}
		
		/**
		 * @generated
		 */
		public void relocate(CellEditor celleditor)
		{
			Text text = (Text) celleditor.getControl();
			Rectangle rect = getWrapLabel().getTextBounds().getCopy();
			getWrapLabel().translateToAbsolute(rect);
			if (getWrapLabel().isTextWrapOn()
					&& getWrapLabel().getText().length() > 0)
			{
				rect.setSize(new Dimension(text.computeSize(rect.width,
						SWT.DEFAULT)));
			}
			else
			{
				int avr = FigureUtilities.getFontMetrics(text.getFont())
						.getAverageCharWidth();
				rect.setSize(new Dimension(text.computeSize(SWT.DEFAULT,
						SWT.DEFAULT)).expand(avr * 2, 0));
			}
			if (!rect.equals(new Rectangle(text.getBounds())))
			{
				text.setBounds(rect.x, rect.y, rect.width, rect.height);
			}
		}
	}
	
	/**
	 * @generated
	 */
	private static class LabelCellEditorLocator implements CellEditorLocator
	{
		
		/**
		 * @generated
		 */
		private Label label;
		
		/**
		 * @generated
		 */
		public LabelCellEditorLocator(Label label)
		{
			this.label = label;
		}
		
		/**
		 * @generated
		 */
		public Label getLabel()
		{
			return label;
		}
		
		/**
		 * @generated
		 */
		public void relocate(CellEditor celleditor)
		{
			Text text = (Text) celleditor.getControl();
			Rectangle rect = getLabel().getTextBounds().getCopy();
			getLabel().translateToAbsolute(rect);
			int avr = FigureUtilities.getFontMetrics(text.getFont())
					.getAverageCharWidth();
			rect.setSize(new Dimension(text.computeSize(SWT.DEFAULT,
					SWT.DEFAULT)).expand(avr * 2, 0));
			if (!rect.equals(new Rectangle(text.getBounds())))
			{
				text.setBounds(rect.x, rect.y, rect.width, rect.height);
			}
		}
	}
}
