package jadex.bdi.examples.blackjack.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import jadex.bdi.examples.blackjack.Dealer;
import jadex.bdi.examples.blackjack.GameStatistics;
import jadex.bdi.examples.blackjack.Player;
import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.beans.PropertyChangeListener;

/**
 *  This class represents a JPanel on which a graph is drawn.
 *  To this class belongs a StatisticModel-object containing
 *  all the information shown on the graph.
 */
public class StatisticGraph	extends JPanel	implements ActionListener
{
	/** the model-object containing the information shown on the graph */
	private GameStatistics model;
	
	/** shall the dealer's result be shown ? */
	private JCheckBox showDealer;
	
	/** shall the player-names be shown ? */
	private JCheckBox showPlayerNames;
	
	/** unit of the x-axis */
	private int roundsToShow;
	
	/** unit of the upper part of the y-axis */
	private int heightToShow;
	
	/** unit of the lower part of the y-axis */
	private int depthToShow;

	/**
	 *  The constructor of this class is called with
	 *  the model-object and the panel's height and
	 *  width as parameters.
	 */
	public StatisticGraph(GameStatistics model)
	{
		super();
		super.setBackground(Color.BLACK);
		super.setForeground(Color.WHITE);
		
		this.model = model;

		showDealer = new JCheckBox("show dealer");
		showDealer.setSelected(true);
		showDealer.setBackground(getBackground());
		showDealer.setForeground(getForeground());
		showDealer.addActionListener(this);
		
		showPlayerNames = new JCheckBox("show player-names");
		showPlayerNames.setSelected(true);
		showPlayerNames.setBackground(getBackground());
		showPlayerNames.setForeground(getForeground());
		showPlayerNames.addActionListener(this);
		
		this.add(showDealer);
		this.add(showPlayerNames);

		model.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				repaint();
			}
		});
	}

	/**
	 *  This methods overrides the paintComponent-method in the JPanel-class.
	 *  Here the whole graph is redrawn.
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if (model.isDataAvailable())
		{			
			paintGrid(g);
			paintNames(g);
			paintLines(g);
		}
		else
		{
			g.drawString("Waiting for data ... ", 20,50);
		}
	}

	/**
	 *  Draw the dealer's and player's graph-lines
	 */
	private void paintLines(Graphics g)
	{
		Iterator it = model.getPlayers();
		while (it.hasNext())
		{
			Player player = (Player)it.next();
			if(!(player instanceof Dealer) || showDealer.isSelected())
			{
				int[] xCoords = model.getXArray(player);
				int[] yCoords = model.getYArray(player);
							
				for (int i=0; i < xCoords.length; i++)
				{
					xCoords[i] = calcX(xCoords[i]);
					yCoords[i] = calcY(yCoords[i]);
				}
				
				Color lineColor = player.getColor();
//				Color darkerColor = new Color(lineColor.getRed() - 20, lineColor.getGreen() - 20, lineColor.getBlue() - 20);
				g.setColor(lineColor);
				
				g.drawPolyline(xCoords, yCoords, xCoords.length);
			}
		}		
	}

	/**
	 *  Paint the x- and y-axis of the graph and draw the corresponding
	 *  values beside the axes.
	 */
	private void paintGrid(Graphics g)
	{		
		roundsToShow = ((model.getMaxRound() / 10) * 10) + 10;
		heightToShow = ((model.getMaximum() / 100) * 100) + 100;
		depthToShow = ((Math.max(model.getMinimum(),0) / 100) * 100) - 100;		
		
		// draw the x- and y-axis
		g.drawLine(calcX(0),calcY(heightToShow), calcX(0), calcY(depthToShow)); // y-axis
		g.drawString(""+heightToShow, 0,15);
		g.drawString(""+depthToShow, 0,calcY(depthToShow));
		g.drawString("100", 0,calcY(100));
		
		g.drawLine(calcX(0),calcY(0), calcX(roundsToShow), calcY(0)); // x-axis
		g.drawString("0", 10,calcY(0)+4);
		for (int i=1; i < 10; i++)
		{
			int step = roundsToShow / 10;
			g.drawString("" + (i*step), calcX(i*step), calcY(0)+12);
		}
	}

	/**
	 *  Paint the names of the player-agents in the lower left corner of the graph
	 */
	private void paintNames(Graphics g)
	{
		if (showPlayerNames.isSelected())
		{
			Iterator it = model.getPlayers();
			int yPos = 0;
			while (it.hasNext())
			{
				Player player = (Player)it.next();
				Color lineColor = player.getColor();
//				Color darkerColor = new Color(lineColor.getRed() - 20, lineColor.getGreen() - 20, lineColor.getBlue() - 20);
				g.setColor(lineColor);
				g.drawString(player.getName(), calcX(0)+8, calcY(depthToShow)-yPos);
				yPos = yPos + 12;
			}
		}
	}

	/**
	 *  calculate the pixel's x-position to a given x-value of the graph
	 */
	private int calcX(int xVal)
	{
		double xRet = ((getWidth()*1.0)-30.0) / (roundsToShow*1.0);
		xRet *= xVal;
		xRet += 30; // distance to left panel-border
		return (int)xRet;
	}

	/**
 	*  calculate the pixel's y-position to a given x-value of the graph
 	*/		
	private int calcY(int yVal)
	{
		int hei = (heightToShow / 100);
		int dep = ( (depthToShow * (-1)) / 100 );
		
		int sum = hei + dep;
		int nullPoint = (getHeight() / sum) * hei;
		
		double yStep = (getHeight() * 1.0)/ (heightToShow + (depthToShow * (-1)));  // explicit * 1.0 because of double
				
		double ret = nullPoint - (yVal * yStep);
		
		return (int)ret;
	}
	
	/**
	 * Called when the showDealer-Checkbox is (de-)selected by the user.
	 * For the better scaling of this graph, the model-object is told not
	 * to consider the dealers-result-history, when calculating minimum and maximum
	 */
	public void actionPerformed(ActionEvent e)
	{
		model.setShowDealer(showDealer.isSelected());
	}	
}