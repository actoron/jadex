package jadex.tools.gpmn.diagram.ui;

import jadex.tools.gpmn.ModeType;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class PlanSemanticsChooser extends Composite
{
	/** Mapping for plan mode types */
	protected static final Map<String, ModeType> PLAN_SEMANTICS_MAP = new HashMap<String, ModeType>();
	static
	{
		PLAN_SEMANTICS_MAP.put("Parallel", ModeType.PARALLEL);
		PLAN_SEMANTICS_MAP.put("Sequential", ModeType.SEQUENTIAL);
		PLAN_SEMANTICS_MAP.put("Mixed/None", null);
	}
	
	/** The combobox. */
	protected Combo chooserCombo;
	
	/** Reverse mapping for plan mode types */
	protected Map<ModeType, Integer> invPlanSemanticsMap;
	
	public PlanSemanticsChooser(Composite parent)
	{
		super(parent, SWT.NULL);
		setLayout(new GridLayout());
		
		chooserCombo = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		
		invPlanSemanticsMap = new HashMap<ModeType, Integer>(PLAN_SEMANTICS_MAP
				.size());
		int index = 0;
		for (Map.Entry<String, ModeType> entry : PLAN_SEMANTICS_MAP.entrySet())
		{
			chooserCombo.add(entry.getKey());
			invPlanSemanticsMap.put(entry.getValue(), index++);
		}
	}
	
	public void select(ModeType mode)
	{
		select(invPlanSemanticsMap.get(mode));
	}
	
	public void select(int index)
	{
		chooserCombo.select(index);
	}
	
	public ModeType getMode()
	{
		return PLAN_SEMANTICS_MAP.get(getText());
	}
	
	public String getText()
	{
		
		return chooserCombo.getText();
	}
	
	public void addSelectionListener(SelectionListener listener)
	{
		chooserCombo.addSelectionListener(listener);
	}
	
	public void removeSelectionListener(SelectionListener listener)
	{
		chooserCombo.removeSelectionListener(listener);
	}
}
