package jadex.base.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import jadex.base.SRemoteGui;
import jadex.base.gui.filechooser.RemoteFileSystemView;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.filetransfer.FileData;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDelegationResultListener;
import jadex.commons.gui.future.SwingExceptionDelegationResultListener;

/**
 *  Helper class that uses a JFileChooser for 
 *  selecting a file at a remote platform.
 *  Methods of this class should only be called from the swing thread.
 */
public class RemoteFileChooser
{
	//-------- attributes --------
	
	/** The remote platform access. */
	protected IExternalAccess	remote;
	
	/** The file chooser (asynchronously created on first access). */
	protected IFuture<JFileChooser>	filechooser;
	
	//-------- constructors --------
	
	/**
	 *  Create a remote file chooser.
	 *  @param remote	The remote platform access.
	 */
	public RemoteFileChooser(IExternalAccess remote)
	{
		this.remote	= remote;
	}
	
	//-------- methods --------
	
	/**
	 *  Choose a remote file.
	 *  @param title	The file chooser title.
	 *  @param def The default file (if any).
	 *  @param filename	A file name (without path) to use as default (if any).
	 *  @param parent	The parent component.
	 *  @param mode	The file selection mode.
	 *  @param filter	A file filter to use (if any).
	 *  @return The absolute path of the file from the remote file system or null if no file was selected.
	 */
	public IFuture<FileData>	chooseFile(final String title, final String def, final JComponent parent, final int mode, final FileFilter filter)
	{
		final Future<FileData>	ret	= new Future<FileData>();
		getFileChooser().addResultListener(new SwingExceptionDelegationResultListener<JFileChooser, FileData>(ret)
		{
			public void customResultAvailable(JFileChooser filechooser)
			{
				if(def!=null)
				{
					filechooser.setSelectedFile(new File(def));
				}
				filechooser.setFileSelectionMode(mode);
				if(filter!=null)
				{
					filechooser.addChoosableFileFilter(filter);
				}
				
				if(filechooser.showDialog(SGUI.getWindowParent(parent), title)==JFileChooser.APPROVE_OPTION)
				{
					File file = filechooser.getSelectedFile();
					if(file!=null)
					{
//						final String	path	= file.getAbsolutePath();
						final String	path	= file.getPath();
						SRemoteGui.getFileData(remote, path)
							.addResultListener(new SwingDelegationResultListener<FileData>(ret));
					}
					else
					{
						ret.setResult(null);
					}
					
					if(filter!=null)
					{
						filechooser.removeChoosableFileFilter(filter);
					}
				}
				else
				{
					ret.setResult(null);
				}
			}
		});
		return ret;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Get or create the file chooser.
	 */
	public IFuture<JFileChooser>	getFileChooser()
	{
		assert SwingUtilities.isEventDispatchThread();
		
		if(filechooser==null)
		{
			final Future<JFileChooser>	fut	= new Future<JFileChooser>();
			filechooser	= fut;
			final RemoteFileSystemView view = new RemoteFileSystemView(remote);
			view.init().addResultListener(new SwingExceptionDelegationResultListener<Void, JFileChooser>(fut)
			{
				public void customResultAvailable(Void result)
				{
					final JFileChooser	filechooser = new JFileChooser(view.getCurrentDirectory(), view);
					view.setFileChooser(filechooser);
					JPanel pan = new JPanel(new GridBagLayout());
					JButton refresh = new JButton("Refresh");
					pan.add(refresh, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHEAST,
						GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
					filechooser.setAccessory(pan);
					refresh.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							view.clearCache();
							filechooser.rescanCurrentDirectory();
						}
					});
					fut.setResult(filechooser);
				}
			});
		}
		
		return filechooser;
	}
}
