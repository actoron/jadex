/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.edit.parts;

import jadex.tools.gpmn.diagram.part.GpmnVisualIDRegistry;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ITextAwareEditPart;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.gmf.runtime.gef.ui.internal.parts.TextCellEditorEx;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.stp.bpmn.diagram.edit.parts.WrapTextCellEditorEx;
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

				case ProcessEditPart.VISUAL_ID:
					return new ProcessEditPart(view);

				case ProcessNameEditPart.VISUAL_ID:
					return new ProcessNameEditPart(view);

				case AchieveGoalEditPart.VISUAL_ID:
					return new AchieveGoalEditPart(view);

				case AchieveGoalNameEditPart.VISUAL_ID:
					return new AchieveGoalNameEditPart(view);

				case MaintainGoalEditPart.VISUAL_ID:
					return new MaintainGoalEditPart(view);

				case MaintainGoalNameEditPart.VISUAL_ID:
					return new MaintainGoalNameEditPart(view);

				case PerformGoalEditPart.VISUAL_ID:
					return new PerformGoalEditPart(view);

				case PerformGoalNameEditPart.VISUAL_ID:
					return new PerformGoalNameEditPart(view);

				case QueryGoalEditPart.VISUAL_ID:
					return new QueryGoalEditPart(view);

				case QueryGoalNameEditPart.VISUAL_ID:
					return new QueryGoalNameEditPart(view);

				case SequentialGoalEditPart.VISUAL_ID:
					return new SequentialGoalEditPart(view);

				case SequentialGoalNameEditPart.VISUAL_ID:
					return new SequentialGoalNameEditPart(view);

				case ParallelGoalEditPart.VISUAL_ID:
					return new ParallelGoalEditPart(view);

				case ParallelGoalNameEditPart.VISUAL_ID:
					return new ParallelGoalNameEditPart(view);

				case MessageGoalEditPart.VISUAL_ID:
					return new MessageGoalEditPart(view);

				case MessageGoalNameEditPart.VISUAL_ID:
					return new MessageGoalNameEditPart(view);

				case SubProcessGoalEditPart.VISUAL_ID:
					return new SubProcessGoalEditPart(view);

				case SubProcessGoalNameEditPart.VISUAL_ID:
					return new SubProcessGoalNameEditPart(view);

				case PlanEditPart.VISUAL_ID:
					return new PlanEditPart(view);

				case PlanNameEditPart.VISUAL_ID:
					return new PlanNameEditPart(view);

				case ContextEditPart.VISUAL_ID:
					return new ContextEditPart(view);

				case TextAnnotationEditPart.VISUAL_ID:
					return new TextAnnotationEditPart(view);

				case TextAnnotationNameEditPart.VISUAL_ID:
					return new TextAnnotationNameEditPart(view);

				case DataObjectEditPart.VISUAL_ID:
					return new DataObjectEditPart(view);

				case DataObjectNameEditPart.VISUAL_ID:
					return new DataObjectNameEditPart(view);

				case GenericGpmnElementEditPart.VISUAL_ID:
					return new GenericGpmnElementEditPart(view);

				case GenericGpmnElementNameEditPart.VISUAL_ID:
					return new GenericGpmnElementNameEditPart(view);

				case AssociationEditPart.VISUAL_ID:
					return new AssociationEditPart(view);

				case SubGoalEdgeEditPart.VISUAL_ID:
					return new SubGoalEdgeEditPart(view);

				case SubGoalEdgeSequentialOrderEditPart.VISUAL_ID:
					return new SubGoalEdgeSequentialOrderEditPart(view);

				case PlanEdgeEditPart.VISUAL_ID:
					return new PlanEdgeEditPart(view);

				case MessagingEdgeEditPart.VISUAL_ID:
					return new MessagingEdgeEditPart(view);

				case MessagingEdgeNameEditPart.VISUAL_ID:
					return new MessagingEdgeNameEditPart(view);

				case GenericGpmnEdgeEditPart.VISUAL_ID:
					return new GenericGpmnEdgeEditPart(view);

				case GenericGpmnEdgeNameEditPart.VISUAL_ID:
					return new GenericGpmnEdgeNameEditPart(view);

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
	 * Copied from TextDirectEditManager and applied to use WrapppingLabel for
	 * wrapping labels. The original code is buggy due to use of WrapLabel it
	 * always returns TextCellEditorEx instead of WrapTextCellEditor.
	 * <p>
	 * Also use and the BPMN WrapTextCellEditorExNeeded to support
	 * <code>shift+enter</code> and <code>alt+enter</code> in labels.
	 * </p>
	 * <p>
	 * <strong>THIS IS A HACK</strong> WrapTextCellEditor is not intended to be
	 * subclassed!
	 * </p>
	 * 
	 * @param source
	 *            the <code>GraphicalEditPart</code> that is used to determine
	 *            which <code>CellEditor</code> class to use.
	 * @return the <code>Class</code> of the <code>CellEditor</code> to use for
	 *         the text editing.
	 * 
	 * @generated NOT
	 */
	@SuppressWarnings("restriction")
	public static Class getTextCellEditorClass(GraphicalEditPart source)
	{
		IFigure figure = source.getFigure();

		if (figure instanceof WrappingLabel
				&& ((WrappingLabel) figure).isTextWrapOn())
		{
			// return WrapTextCellEditor.class;
			// use BPMN extended class to support shift+enter and alt+enter
			return WrapTextCellEditorEx.class;
		}

		return TextCellEditorEx.class;
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
