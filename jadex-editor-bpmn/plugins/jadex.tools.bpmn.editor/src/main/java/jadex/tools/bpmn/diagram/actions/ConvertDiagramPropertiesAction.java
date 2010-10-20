/**
 * 
 */
package jadex.tools.bpmn.diagram.actions;

import jadex.tools.bpmn.editor.properties.JadexBpmnDiagramImportsSection;
import jadex.tools.bpmn.editor.properties.JadexBpmnDiagramParameterSection;
import jadex.tools.bpmn.editor.properties.JadexBpmnDiagramPropertiesTableSection;
import jadex.tools.bpmn.editor.properties.JadexCommonParameterSection;
import jadex.tools.bpmn.editor.properties.JadexIntermediateEventsParameterSection;
import jadex.tools.bpmn.editor.properties.JadexSequenceMappingSection;
import jadex.tools.bpmn.editor.properties.template.AbstractParameterTablePropertySection;
import jadex.tools.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.stp.bpmn.diagram.edit.parts.BpmnDiagramEditPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Claas
 *
 */
public class ConvertDiagramPropertiesAction implements IObjectActionDelegate
{

	public final static String ID = "jadex.tools.bpmn.diagram.actions.ConvertDiagramPropertiesActionID";
	
	private IWorkbenchPart targetPart;
	private EditPart diagramEdidPart;
	private static List<TableAnnotationIdentifier> toConvert;

	static {
		toConvert = new ArrayList<TableAnnotationIdentifier>();
		
		// ---- parameters ----
		toConvert
				.add(new TableAnnotationIdentifier(
						JadexCommonParameterSection.PARAMETER_ANNOTATION_IDENTIFIER,
						JadexCommonParameterSection.PARAMETER_ANNOTATION_DETAIL_IDENTIFIER,
						AbstractParameterTablePropertySection.UNIQUE_PARAMETER_ROW_ATTRIBUTE));
		
		// ---- diagram imports ----
		toConvert.add(new TableAnnotationIdentifier(
						JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION,
						JadexBpmnPropertiesUtil.JADEX_IMPORT_LIST_DETAIL,
						JadexBpmnDiagramImportsSection.UNIQUE_COLUMN_INDEX));
		
		// ---- diagram arguments ----
		toConvert
				.add(new TableAnnotationIdentifier(
						JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION,
						JadexBpmnPropertiesUtil.JADEX_ARGUMENTS_LIST_DETAIL,
						JadexBpmnDiagramParameterSection.UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX));
		
		// ---- sub process ---
		toConvert 
				.add(new TableAnnotationIdentifier(
						// old diagrams uses "subProcess" as source
						JadexBpmnPropertiesUtil.JADEX_SUBPROCESS_ANNOTATION,
						JadexBpmnPropertiesUtil.JADEX_PROPERTIES_LIST_DETAIL,
						JadexBpmnDiagramPropertiesTableSection.UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX));
		toConvert
				.add(new TableAnnotationIdentifier(
						// use "jadex" in newer and converted diagrams
						JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION,
						JadexBpmnPropertiesUtil.JADEX_PROPERTIES_LIST_DETAIL,
						JadexBpmnDiagramPropertiesTableSection.UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX));
		
		// ---- activity ----
		toConvert
				.add(new TableAnnotationIdentifier(
						// old diagrams uses "activity" as source
						JadexBpmnPropertiesUtil.JADEX_ACTIVITY_ANNOTATION,
						JadexBpmnPropertiesUtil.JADEX_PARAMETER_LIST_DETAIL,
						JadexIntermediateEventsParameterSection.UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX));
		toConvert
				.add(new TableAnnotationIdentifier(
						// use "jadex" in newer and converted diagrams
						JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION,
						JadexBpmnPropertiesUtil.JADEX_PARAMETER_LIST_DETAIL,
						JadexIntermediateEventsParameterSection.UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX));
		
		
		// ---- sequence edges ----
		toConvert
				.add(new TableAnnotationIdentifier(
						// old diagrams uses "sequence" as source
						JadexBpmnPropertiesUtil.JADEX_SEQUENCE_ANNOTATION,
						JadexBpmnPropertiesUtil.JADEX_MAPPING_LIST_DETAIL,
						JadexSequenceMappingSection.UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX));
		toConvert
				.add(new TableAnnotationIdentifier(
						// use "jadex" in newer and converted diagrams
						JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION,
						JadexBpmnPropertiesUtil.JADEX_MAPPING_LIST_DETAIL,
						JadexSequenceMappingSection.UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX));
	}
	
	/**
	 * 
	 */
	public ConvertDiagramPropertiesAction()
	{
		super();
		
		

	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection)
	{
		diagramEdidPart = null;
		if (selection instanceof IStructuredSelection)
		{
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			if (structuredSelection.getFirstElement() instanceof EditPart)
			{
				EditPart ep = (EditPart) structuredSelection
					.getFirstElement();
				
				while (ep.getParent() != null && !(ep instanceof BpmnDiagramEditPart))
				{
					ep = ep.getParent();
				}
				diagramEdidPart = ep;
			}
		}
	}

	/**
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) 
	{
		this.targetPart = targetPart;
	}

	@Override
	public void run(IAction action)
	{
		List<EModelElement> elementsToCheck = new ArrayList<EModelElement>();
		EObject diagramEModelElement = ((View) diagramEdidPart.getModel()).getElement();
		
		// add the diagram to the elements to check
		elementsToCheck.add((EModelElement) diagramEModelElement);
		
		// select all elements from diagram and subsequent elements
		TreeIterator<EObject> contents = diagramEModelElement.eAllContents();
		while (contents.hasNext())
		{
			EObject eObject = (EObject) contents.next();
			if (eObject instanceof EModelElement && !(eObject instanceof EAnnotation))
			{
				elementsToCheck.add((EModelElement) eObject);
			}
		}
		
		// check each element for existing annotations to convert
		for (EModelElement eModelElement : elementsToCheck)
		{
			// check annotations and convert to a single jadex annotation
			JadexBpmnPropertiesUtil.checkAnnotationConversion(eModelElement);
			
			// check table conversion
			for (TableAnnotationIdentifier identifier : toConvert)
			{
				JadexBpmnPropertiesUtil.checkAnnotationConversion(
						eModelElement, identifier.annotationID,
						identifier.detailID, identifier.uniqueTableColumn);
			}

		}

	}

}

class TableAnnotationIdentifier
{
	String annotationID;
	String detailID;
	int uniqueTableColumn;
	
	/**
	 * @param annotationID
	 * @param detailID
	 * @param uniqueTableColumn
	 */
	protected TableAnnotationIdentifier(String annotationID,
			String detailID, int uniqueTableColumn)
	{
		super();
		this.annotationID = annotationID;
		this.detailID = detailID;
		this.uniqueTableColumn = uniqueTableColumn;
	}

	/**
	 * @return the annotationID
	 */
	public String getAnnotationID()
	{
		return annotationID;
	}

	/**
	 * @return the detailID
	 */
	public String getDetailID()
	{
		return detailID;
	}

	/**
	 * @return the uniqueTableColumn
	 */
	public int getUniqueTableColumn()
	{
		return uniqueTableColumn;
	}

}
