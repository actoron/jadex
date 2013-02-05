package gui2d;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import aos.jack.util.cursor.Action;

public class Visualisation extends Frame {

    GameBoard b;

    public Action move(int x0,int y0,int x1, int y1,boolean back)
    {
	return b.move( x0, y0, x1, y1, back );
    }

    public Visualisation(String title)
    {
	super( title );

	setSize( 478, 332 );
	add( b = new GameBoard(), BorderLayout.CENTER );
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e)
            {
                // Crash and bang
                System.exit(0);
            }
        });
	setVisible( true );
    }

    public static void main(String [] args)
	throws InterruptedException
    {
	new Visualisation( "TESTING" );
	Thread.sleep( 100000 );		// Keep the main thread around...
    }
}
