/**
 * 
 */
package jadex.editor.bpmn.runtime.task;

import jadex.editor.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;
import jadex.editor.model.common.properties.table.MultiColumnTable;
import jadex.editor.model.common.properties.table.MultiColumnTable.MultiColumnTableRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.stp.bpmn.BpmnDiagram;

/**
 * @author Claas
 *
 */
public class DiagramImportsTaskProvider extends PackageBasedTaskProvider
{

	/**
	 * The diagram of the current selected task
	 */
	protected BpmnDiagram bpmnDiagram;
	
	/**
	 * 
	 */
	public DiagramImportsTaskProvider()
	{
		setInput(null);
	}
	
	@Override
	public void setInput(EModelElement selectedElement)
	{
		super.setInput(selectedElement);
		updateBpmnDiagram(selectedElement);
	}
	
	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.TaskProviderSupport#refresh()
	 */
	@Override
	public void refresh()
	{
		if (bpmnDiagram != null)
		{
			setInput(bpmnDiagram);
			super.refresh();
		}
	}

	/**
	 * @param selectedElement
	 */
	private void updateBpmnDiagram(EModelElement selectedElement)
	{
		if (selectedElement == null)
			return;
		
		// max recursive search depth
		int maxRecursion = 30;
		int depth = 1;
		
		// each model element is contained in the diagram
		EObject container = selectedElement;
		
		while (!(container instanceof BpmnDiagram)  && depth < maxRecursion)
		{
			container = container.eContainer();
			depth++;
		}
		
		if (container != bpmnDiagram || !container.equals(bpmnDiagram))
		{
			bpmnDiagram = (BpmnDiagram) container;
		}
	}
	
	/**
	 * Override search packages initialization with diagram imports
	 */
	@Override
	protected void initializeSearchPackages()
	{
		searchPackages = parseDiagramImports();
	}
	
	/**
	 * Parse the package imports from BpmnDiagram and return them as String list
	 * @return List of package strings
	 */
	protected List<String> parseDiagramImports()
	{

		MultiColumnTable table =
			JadexBpmnPropertiesUtil.getJadexEAnnotationTable(
				bpmnDiagram,
				JadexBpmnPropertiesUtil.getTableAnnotationIdentifier(
						JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION,
						JadexBpmnPropertiesUtil.JADEX_IMPORT_LIST_DETAIL));
		
		// exit without annotation table
		if (table == null)
		{
			return Collections.emptyList();
		}
		
		int uniqueColumnIndex = table.getUniqueColumn();
		List<String> packageImports = new ArrayList<String>();
		
		for (MultiColumnTableRow row : table.getRowList())
		{
			String importValue = row.getColumnValueAt(uniqueColumnIndex);
			if (importValue.endsWith(".*"))
			{
				packageImports.add(importValue.substring(0, importValue.length() - 2));
			}
		}
		
		return packageImports;
	}

}
