package gui2d;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;

import aos.jack.util.cursor.Action;

public class GameBoard extends Canvas {

    //      x       x
    //    x   x   x   x
    //  x   x   x   x   x
    //    x   x   x   x
    //      x       x

    static final int BASE = 10;
    static final int BW = 3;
    static final int SIDE = 40 + BW + BASE;
    static final int SZ = 36;
    static final int GAP = 40;
    static final int PEG = SZ - 4;
    static final int MAP = SIDE + 2;
    static final int LEFT = BASE + BW;
    static final int RIGHT = 2*SIDE + 8*GAP + SZ;
    static final int TOP = BASE + BW;
    static final int BOTTOM = 2*SIDE + 4*GAP + SZ;

    static int [] holes = {
	SIDE + 2*GAP, 		SIDE,
	SIDE + 6*GAP, 		SIDE,
	SIDE + GAP,		SIDE + GAP,
	SIDE + 3*GAP,		SIDE + GAP,
	SIDE + 5*GAP,		SIDE + GAP,
	SIDE + 7*GAP,		SIDE + GAP,
	SIDE,			SIDE + 2*GAP,
	SIDE + 2*GAP,		SIDE + 2*GAP,
	SIDE + 4*GAP,		SIDE + 2*GAP,
	SIDE + 6*GAP,		SIDE + 2*GAP,
	SIDE + 8*GAP,		SIDE + 2*GAP,
	SIDE + GAP,		SIDE + 3*GAP,
	SIDE + 3*GAP,		SIDE + 3*GAP,
	SIDE + 5*GAP,		SIDE + 3*GAP,
	SIDE + 7*GAP,		SIDE + 3*GAP,
	SIDE + 2*GAP, 		SIDE + 4*GAP,
	SIDE + 6*GAP, 		SIDE + 4*GAP
    };

    public GameBoard()
    {
	setSize( RIGHT + BASE + BW, BOTTOM + BASE + BW );
    }

    public Dimension getPreferredSize()
    {
	return new Dimension( RIGHT + BASE + BW, BOTTOM + BASE + BW );
    }

    public void paint(Graphics g)
    {
	Rectangle r = (Rectangle) g.getClip();
	g.setColor( Color.black );
	paintEdge( g );
	paintHoles( g );
	paintPegs( g );
    }

    private void paintEdge(Graphics g)
    {
	g.fillRect( LEFT - BW, TOP - BW, RIGHT - LEFT + BW, BW );
	g.fillRect( LEFT - BW, TOP, BW, BOTTOM - TOP + BW );
	g.fillRect( LEFT, BOTTOM, RIGHT - LEFT + BW, BW );
	g.fillRect( RIGHT, TOP - BW, BW, BOTTOM - TOP + BW );
    }

    private void paintHoles(Graphics g)
    {
	for ( int i=0; i<holes.length; i += 2 ) {
	    g.drawOval( holes[i], holes[i+1], SZ, SZ );
	}
    }

    void paintPegXY(Graphics g,Color c,int x,int y)
    {
	g.setColor( c );
	g.fillOval( x, y, PEG, PEG );
    }


    void redrawPegXY(int x,int y)
    {
	repaint( 10L, x, y, PEG+1, PEG+1 );
    }

    public int visualX(int i,int j)
    {
	return ( i + j ) * GAP + MAP;
    }

    public int visualY(int i,int j)
    {
        return ( i - j ) * GAP + MAP + 2*GAP;
    }

    private void paintPeg(Graphics g,Color c,int i,int j)
    {
	if ( c != null )
	    paintPegXY( g, c, visualX( i, j ), visualY( i, j ) );
    }

    Color colorOf(int c)
    {
	if ( c == 1 )
	    return Color.yellow;
	if ( c == -1 )
	    return Color.red;
	return null;
    }

    int [][] pegs = {
	{ 1, 1, 1, 4, 4 },
	{ 1, 1, 1, 4, 4 },
	{ 1, 1, 0, -1, -1 },
	{ 4, 4, -1, -1, -1 },
	{ 4, 4, -1, -1, -1 }
    };

    private void paintPegs(Graphics g)
    {
	for ( int i=0; i<5; i++ )
	    for ( int j=0; j<5; j++ )
		paintPeg( g, colorOf( pegs[i][j] ), i, j );
	if ( moving != null ) {
	    moving.paint( g );
	}
    }
    
    PegMoving moving = null;

    public Action move(int i0,int j0,int i1, int j1,boolean back)
    {
	return new PegMoving( i0, j0, i1, j1, back );
    }

    class PegMoving extends Action {

	int x;
	int y;

	int c;
	int x0;
	int y0;
	int i1;
	int j1;
	
	boolean back;

	PegMoving(int i0,int j0,int i1, int j1,boolean b)
	{
	    c = pegs[i0][j0];
	    x = x0 = visualX( i0, j0 );
	    y = y0 = visualY( i0, j0 );
	    this.i1 = i1;
	    this.j1 = j1;
	    pegs[i0][j0] = 0;
	    back = b;
	    moving = this;
	}

	synchronized void move(int i)
	{
	    redrawPegXY( x, y );
	    x = ( visualX( i1, j1 ) - x0 ) / 10 * i +  x0;
	    y = ( visualY( i1, j1 ) - y0 ) / 10 * i +  y0;
	    redrawPegXY( x, y );
	    if ( i == 10 ) {
		pegs[i1][j1] = c;
		back = false;
	    }
	}

	synchronized void paint(Graphics g)
	{
	    paintPegXY( g, colorOf( c ), x, y );
	    if ( back ) {
		g.setColor( Color.black );
	        g.drawString( "back", x, y + PEG );
	    }
	}

	protected void action()
	{
	    for ( int i=0; i<=10; i++ ) {
		delay();
		move( i );
	    }
	    moving = null;
	}

	void delay()
	{
	    try { Thread.sleep( 40 ); } catch (InterruptedException e) { }
	}

    }
}

