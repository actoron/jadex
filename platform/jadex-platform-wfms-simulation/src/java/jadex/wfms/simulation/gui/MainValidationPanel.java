package jadex.wfms.simulation.gui;

import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.wfms.common.SClassLoaderTools;
import jadex.wfms.simulation.stateset.gui.IStatePanel;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.JComboBox;
import javax.swing.JPanel;

public class MainValidationPanel extends JPanel implements IStatePanel
{
	protected SimulationWindow simWindow;
	
	protected JComboBox classList;
	
	protected static final Pattern DOLLAR_PATTERN = Pattern.compile(".*\\$\\p{Alnum}+\\.class");
	
	public MainValidationPanel(SimulationWindow simWnd)
	{
		this.simWindow = simWnd;
		
		classList = new JComboBox();
		add(classList);
		
		refreshPanel();
	}
	
	/**
	 * Refreshes the contents of the state panel.
	 */
	public void refreshPanel()
	{
		SClassLoaderTools.searchLibraryService(simWindow.getLibService(), new FileFilter()
		{
			public boolean accept(File pathname)
			{
				if (pathname.getPath().endsWith(".class") && (!DOLLAR_PATTERN.matcher(pathname.getPath()).matches()))
				{
					try
					{
						Class clazz = Class.forName(pathname.getPath().replaceAll("/", ".").substring(0, (int) (pathname.getPath().length() - 6)), true, simWindow.getLibService().getClassLoader());
						if (JPanel.class.isAssignableFrom(clazz) &&
							!clazz.isAnonymousClass() &&
							!clazz.isMemberClass() &&
							!Modifier.isAbstract(clazz.getModifiers()) &&
							!Modifier.isInterface(clazz.getModifiers()))
							return true;
					}
					catch (Exception e)
					{
					}
					catch (Error e)
					{
					}
				}
				return false;
			}
		}).addResultListener(new SwingDefaultResultListener(this)
		{
			public void customResultAvailable(Object result)
			{
				Set fileSet = (Set) result;
				classList.addItem("");
				for (Iterator it = fileSet.iterator(); it.hasNext(); )
				{
					String path = (String) it.next();
					try
					{
						classList.addItem(Class.forName(path.replaceAll("/", ".").substring(0, (int) (path.length() - 6)), true, simWindow.getLibService().getClassLoader()));
					}
					catch (ClassNotFoundException e)
					{
					}
				}
			}
		});
	}
}
