package jadex.bdi.examples.hunterprey.ldahunter.potentialfield;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.future.IFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.commons.transformation.annotations.Classname;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Int;
import jadex.extension.envsupport.math.Vector2Int;

/**
 *  A gui for the LA hunter.
 */
public class PotentialFrame extends JFrame
{
	JointField jf;
	IVector2 myLoc;
	int toX;
	int toY;
	Vector2Int toLoc;

	/**
	 *  Create the LA hunter frame.
	 */
	public PotentialFrame(final IExternalAccess agent, String title) throws HeadlessException
	{
		super(title);

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				agent.killComponent();
			}
		});
		
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("dispose")
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				bia.addComponentListener(new TerminationAdapter()
//				{
//					public void componentTerminated()
//					{
//						SwingUtilities.invokeLater(new Runnable()
//						{
//							public void run()
//							{
//								PotentialFrame.this.dispose();
//							}
//						});
//					}
//				});
				
				ia.getComponentFeature(IMonitoringComponentFeature.class).subscribeToEvents(IMonitoringEvent.TERMINATION_FILTER, false, PublishEventLevel.COARSE)
					.addResultListener(new SwingIntermediateResultListener<IMonitoringEvent>(new IntermediateDefaultResultListener<IMonitoringEvent>()
				{
					public void intermediateResultAvailable(IMonitoringEvent result)
					{
						PotentialFrame.this.dispose();
					}
				}));
				return IFuture.DONE;
			}
		});
		
//		agent.addAgentListener(new IAgentListener()
//		{
//			public void agentTerminating(AgentEvent ae)
//			{
//				SwingUtilities.invokeLater(new Runnable()
//				{
//					public void run()
//					{
//						PotentialFrame.this.dispose();
//					}
//				});
//			}
//			public void agentTerminated(AgentEvent ae)
//			{
//			}
//		});
		
		this.setSize(400, 400);
		this.setBackground(Color.BLACK);
		this.getContentPane().add(new FieldDisplayer());
		this.getContentPane().setBackground(Color.BLACK);
		this.setLocation(SGUI.calculateMiddlePosition(this));
		this.setVisible(true);
		toLoc = null;

	}

	static final Color[] dScale = new Color[256];

	static
	{
		int i = 256;
		while(i-->0)
		{
			dScale[i] = new Color(0, 0, i);
		}
	}

	/**
	 * @param jf
	 * @param myLoc
	 * @param x
	 * @param y
	 */
	public void update(JointField jf, IVector2 myLoc, int x, int y)
	{
		this.jf = jf;
		this.myLoc = myLoc;
		this.toX = x;
		this.toY = y;
		this.repaint();
	}

	final class FieldDisplayer extends JComponent
	{
		/**
		 * @param g
		 * @see javax.swing.JComponent#update(java.awt.Graphics)
		 */
		public void update(Graphics g)
		{
			paint(g);
		}

		/**
		 * @param g
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
		public void paint(Graphics g)
		{
			if(jf!=null)
			{
				int i = jf.desire.length;
				int h = jf.desire[0].length;
				int j = h;

				final int ow = getWidth()/i;
				final int oh = getHeight()/j;
				final int ow2 = ow/2+1;
				final int oh2 = oh/2+1;
				while(i-->0)
				{
					j = h;
					while(j-->0)
					{
						if(jf.field.obstacles[i][j])
						{
							g.setColor(Color.GREEN);
							g.fillOval(ow*i, oh*j, ow, oh);
						}
						else if(jf.desire[i][j]>0.0)
						{
							int d = (int)((jf.desire[i][j]-jf.minDesire)*256.0/jf.maxDesire);
							d = d<0? 0: d;
							d = d>255? 255: d;
							g.setColor(dScale[d]);
							g.fillRect(ow*i, oh*j, ow, oh);
						}
					}
				}

				g.setColor(Color.WHITE);
				g.fillOval(ow*myLoc.getXAsInteger(), oh*myLoc.getYAsInteger(), ow, oh);

				int tx = toX;
				int ty = toY;
				g.setXORMode(Color.BLUE);
				g.drawLine(tx*ow, ty*oh, tx*ow+ow, ty*oh+oh);
				g.drawLine(tx*ow+ow, ty*oh, tx*ow, ty*oh+oh);

				if(toLoc==null)
				{
					toLoc	= new Vector2Int(toX, toY);
				}
				else
				{
					toLoc.setX(new Vector1Int(toX));
					toLoc.setY(new Vector1Int(toY));
				}
				int nx, ny;
				while(jf.getNearerLocation(toLoc))
				{
					nx = toLoc.getXAsInteger();
					ny = toLoc.getYAsInteger();
					if(Math.abs(nx-tx)+Math.abs(ny-ty)==1)
					{
						g.drawLine(tx*ow+ow2, ty*oh+oh2, nx*ow+ow2, ny*oh+oh2);
					}

					tx = nx;
					ty = ny;
				}
				nx = myLoc.getXAsInteger();
				ny = myLoc.getYAsInteger();
				if(Math.abs(nx-tx)+Math.abs(ny-ty)==1)
				{
					g.drawLine(tx*ow+ow2, ty*oh+oh2, nx*ow+ow2, ny*oh+oh2);
				}

			}
		}
	}
}

