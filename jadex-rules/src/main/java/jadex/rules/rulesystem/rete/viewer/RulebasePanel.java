package jadex.rules.rulesystem.rete.viewer;

import jadex.rules.rulesystem.IRule;
import jadex.rules.rulesystem.IRulebase;
import jadex.rules.rulesystem.IRulebaseListener;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

/**
 *  A panel for viewing the content of the rulebase.
 */
public class RulebasePanel extends JPanel
{
	//-------- attributes --------
	
	/** The rulebase. */
//	protected IRulebase rulebase;
	protected List rules;
	
	/** The list. */
	protected JList list;
	
	//-------- constructors --------
	
	/**
	 *  Create a new rulebase panel.
	 */
	public RulebasePanel(final IRulebase rulebase)
	{
//		this.rulebase = rulebase;
		this.rules = new ArrayList();
		for(Iterator it=rulebase.getRules().iterator(); it.hasNext(); )
			rules.add(it.next());
		
		ListModel lm = new AbstractListModel()
		{
			public Object getElementAt(int index)
			{
				return rules.get(index);
			}
			
			public int getSize()
			{
				return rules.size();
			}
		};
		
		rulebase.addRulebaseListener(new IRulebaseListener()
		{
			public void ruleAdded(IRule rule)
			{
				rules.add(rule);
			}
			
			public void ruleRemoved(IRule rule)
			{
				rules.remove(rule);
			}
		});
		
		this.list = new JList(lm);
		this.setLayout(new BorderLayout());
		JScrollPane sp = new JScrollPane(list);
		this.add(sp, BorderLayout.CENTER);
		this.setBorder(BorderFactory.createTitledBorder("Rulebase"));
	
		// todo: cleanup, remove rulebase listener
	}
	
	/**
	 *  Get the underlying list.
	 *  @return The list.
	 */
	public JList getList()
	{
		return list;
	}
}
