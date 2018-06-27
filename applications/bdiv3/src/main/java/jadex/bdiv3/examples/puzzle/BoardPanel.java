package jadex.bdiv3.examples.puzzle;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIDefaults;

import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.gui.SGUI;

/**
 *  Display the board.
 */
public class BoardPanel extends JPanel
{
	//-------- constants --------

	/** The image icons. */
	public static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"white_piece",	SGUI.makeIcon(BoardPanel.class, "/jadex/bdiv3/examples/puzzle/images/white_piece.png"),
		"red_piece",	SGUI.makeIcon(BoardPanel.class, "/jadex/bdiv3/examples/puzzle/images/red_piece.png"),
		"empty_field", SGUI.makeIcon(BoardPanel.class, "/jadex/bdiv3/examples/puzzle/images/empty_field.png")
	});

	//-------- attributes --------

	/** The board to visualize. */
	protected IBoard board;

	/** Indicates if an image rescaling is necessray. */
	protected boolean rescale;

	/** The white piece image. */
	protected Image	wp_image;

	/** The red piece image. */
	protected Image	rp_image;

	/** The empty field image. */
	protected Image	ef_image;

	/** The component to display white pieces. */
	protected JLabel white_piece;

	/** The component to display white pieces. */
	protected JLabel red_piece;

	/** The component to display white pieces. */
	protected JLabel empty_field;

	/** The listeners. */
	protected List<ActionListener> listeners;
	
	/** The move count (to detect takebacks). */
	protected int movecnt;

	/** The last move (if any). */
	protected Move	last;

	//-------- constructors --------

	/**
	 *  Create a new board panel.
	 */
	public BoardPanel(IBoard board)
	{
		this.listeners = new ArrayList<ActionListener>();
		this.board = board;
		this.wp_image	= ((ImageIcon)icons.getIcon("white_piece")).getImage();
		this.rp_image	= ((ImageIcon)icons.getIcon("red_piece")).getImage();
		this.ef_image	= ((ImageIcon)icons.getIcon("empty_field")).getImage();
		this.white_piece	= new JLabel(new ImageIcon(wp_image), JLabel.CENTER);
		this.red_piece	= new JLabel(new ImageIcon(rp_image), JLabel.CENTER);
		this.empty_field	= new JLabel(new ImageIcon(ef_image), JLabel.CENTER);

		// Trigger rescaling of images.
		this.addComponentListener(new ComponentAdapter()
		{
			public void	componentResized(ComponentEvent ce)
			{
				rescale	= true;
			}
		});

		this.addMouseListener(new MouseAdapter()
		{
			/**
			 * Invoked when the mouse has been clicked on a component.
			 */
			public void mouseClicked(MouseEvent e)
			{
				//System.out.println("Mouse clicked: "+e.getX()+" "+e.getY());
				int size = BoardPanel.this.board.getSize();
				Rectangle r = BoardPanel.this.getBounds();
				int x = (int)(e.getX()/(r.getWidth()/(double)size));
				int y = (int)(e.getY()/(r.getHeight()/(double)size));
				int m = size/2;
				if(!(x<m && y>m || x>m && y<m))
				{
					ActionEvent ae = new ActionEvent(new Position(x,y), 0, null); // todo: hack
					for(int i=0; i<listeners.size(); i++)
					{
						((ActionListener)listeners.get(i)).actionPerformed(ae);
					}
					//System.out.println("x: "+x+" y: "+y);
				}
			}
		});
	}

	//-------- methods --------
	
	/**
	 *  Update the gui after a move.
	 */
	public void	update(PropertyChangeEvent evt)
	{
		//System.out.println(board.getLastMove());
		repaint();
	}

	/**
	 *  Overridden paint method.
	 */
	protected void	paintComponent(Graphics g)
	{
		int bsize = board.getSize();
		Rectangle	bounds	= getBounds();
		double cellw = bounds.getWidth()/(double)bsize;
		double cellh = bounds.getHeight()/(double)bsize;

		// Rescale images if necessary.
		if(rescale)
		{
			((ImageIcon)white_piece.getIcon()).setImage(
				wp_image.getScaledInstance((int)cellw, (int)cellh, Image.SCALE_DEFAULT));
			((ImageIcon)red_piece.getIcon()).setImage(
				rp_image.getScaledInstance((int)cellw, (int)cellh, Image.SCALE_DEFAULT));
			((ImageIcon)empty_field.getIcon()).setImage(
				ef_image.getScaledInstance((int)cellw, (int)cellh,Image.SCALE_DEFAULT));

			rescale	= false;
		}

		g.setColor(getBackground());
		g.fillRect(0,0,bounds.width, bounds.height);

		List<Piece> pieces = board.getCurrentPosition();
		for(int y=0; y<bsize; y++)
		{
			for(int x=0; x<bsize; x++)
			{
				Piece piece = pieces.get(y*bsize+x);
				if(piece!=null)
				{
					if(piece.isWhite())
						SGUI.renderObject(g, white_piece, cellw, cellh, x, y, 0);
						//g.setColor(Color.white);
					else
						SGUI.renderObject(g, red_piece, cellw, cellh, x, y, 0);
						//g.setColor(Color.darkGray);
					//g.fillOval((int)(cellw*x), (int)(cellh*y), (int)cellw, (int)cellh);
				}
				else if(board.isFreePosition(new Position(x,y)))
				{
					SGUI.renderObject(g, empty_field, cellw, cellh, x, y, 0);
					//g.setColor(Color.lightGray);
					//g.fillRect((int)(cellw*x), (int)(cellh*y), (int)cellw, (int)cellh);
				}
				else
				{
					//System.out.println("empty: "+x+" "+y);
				}
			}
		}

		if(board.getLastMove()!=null)
		{
			// Display new move.
			if(movecnt<=board.getMoves().size())
			{
				last	= board.getLastMove();
				g.setColor(Color.green);
				drawArrow(g, last, cellw, cellh);
			}
			
			// Take back -> display inverse of old last move.
			else
			{
				g.setColor(Color.red);
				Move tb = new Move(last.getEnd(), last.getStart());
				drawArrow(g, tb, cellw, cellh);
				last	= board.getLastMove();
			}

			movecnt	= board.getMoves().size();
		}
	}

	/**
	 *  Draw an arrow for visulizing the move.
	 */
	protected void drawArrow(Graphics g, Move move, double cellw, double cellh)
	{
		int xs = move.getStart().getX();
		int ys = move.getStart().getY();
		int xe = move.getEnd().getX();
		int ye = move.getEnd().getY();
		int xms = (int)(xs*cellw+cellw/2);
		int yms = (int)(ys*cellh+cellh/2);
		int xme = (int)(xe*cellw+cellw/2);
		int yme = (int)(ye*cellh+cellh/2);
		//g.drawLine(xms, yms, xme, yme);
		int asize = Math.max((int)(Math.min(cellw, cellh)/8), 1);
		int thick = Math.max(asize/4, 1);
		if(xs<xe)
		{
			// Arrow right.
			g.fillRect(xms, yms-thick/2, xme-xms-asize, thick);
			g.fillPolygon(new int[]{xme-asize, xme-asize, xme}, new int[]{yme-asize, yme+asize, yme}, 3);
		}
		else if(xs>xe)
		{
			// Arrow left.
			g.fillRect(xme+asize, yms-thick/2, xms-xme-asize, thick);
			g.fillPolygon(new int[]{xme+asize, xme+asize, xme}, new int[]{yme-asize, yme+asize, yme}, 3);
		}
		else if(ys<ye)
		{
			// Arrow up.
			g.fillRect(xms-thick/2, yms, thick, yme-yms-asize);
			g.fillPolygon(new int[]{xme-asize, xme+asize, xme}, new int[]{yme-asize, yme-asize, yme}, 3);
		}
		else
		{
			// Arrow down.
			g.fillRect(xms-thick/2, yme+asize, thick, yms-yme-asize);
			g.fillPolygon(new int[]{xme-asize, xme+asize, xme}, new int[]{yme+asize, yme+asize, yme}, 3);
		}
	}

	//-------- action listeners --------

	/**
	 *  Add a new action listener.
	 *  @param listener The listener.
	 */
	public void addActionListener(ActionListener listener)
	{
		this.listeners.add(listener);
	}

	/**
	 *  Remove an action listener.
	 *  @param listener The listener.
	 */
	public void removeActionListener(ActionListener listener)
	{
		this.listeners.remove(listener);
	}

	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		Board b = new Board(5);
		BoardPanel bp = new BoardPanel(b);
		bp.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Action event: "+e);
			}
		});
		JFrame f = new JFrame();
		f.getContentPane().add("Center", bp);
		f.setSize(400,400);
		f.setLocation(SGUI.calculateMiddlePosition(f));
		f.setVisible(true);
	}
}
