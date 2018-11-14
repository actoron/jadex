package jadex.android.puzzle.ui;

import jadex.android.puzzle.R;
import jadex.bdiv3.examples.puzzle.IBoard;
import jadex.bdiv3.examples.puzzle.Move;
import jadex.bdiv3.examples.puzzle.Piece;
import jadex.bdiv3.examples.puzzle.Position;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

public class SokratesView extends TileView 
{

	/**
	 * Labels for the drawables that will be loaded into the TileView class
	 */
	private static final int EMPTY_FIELD = 1;
	private static final int RED_PIECE = 2;
	private static final int WHITE_PIECE = 3;

	private IBoard board;

	/**
	 * Create a simple handler that we can use to cause animation to happen. We
	 * set ourselves as a target and we can use the sleep() function to cause an
	 * update/invalidate to occur at a later date.
	 */
	private RefreshHandler mRedrawHandler = new RefreshHandler();

	class RefreshHandler extends Handler
	{

		@Override
		public void handleMessage(Message msg)
		{
			SokratesView.this.invalidate();
		}

		public void sleep(long delayMillis)
		{
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};

	public SokratesView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		resetTiles(4);
		Resources r = this.getContext().getResources();
		loadTile(EMPTY_FIELD, r.getDrawable(R.drawable.empty_field));
		loadTile(RED_PIECE, r.getDrawable(R.drawable.red_piece));
		loadTile(WHITE_PIECE, r.getDrawable(R.drawable.white_piece));
	}

	public void updatePos(Position pos)
	{
		Piece piece = board.getPiece(pos);
		if (piece != null)
		{
			if (piece.isWhite())
			{
				setTile(WHITE_PIECE, pos.getX(), pos.getY());
			}
			else
			{
				setTile(RED_PIECE, pos.getX(), pos.getY());
			}
		}
		else
		{
			setTile(EMPTY_FIELD, pos.getX(), pos.getY());
		}
	}

	public void initBoard()
	{
		Position pos = new Position(0,0);
		for (int x = 0; x < mTileCount; x++)
		{
			for (int y = 0; y < mTileCount; y++)
			{
					pos.setX(x);
					pos.setY(y);
					if (board.getPiece(pos) != null) {
						updatePos(pos);
					}
			}
		}
		updatePos(new Position(2, 2));
//		updatePos(((Board)board).hole_pos);
		mRedrawHandler.obtainMessage().sendToTarget();
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
	}

	public void performMove(Move move)
	{
		
		Position start = move.getStart();
		Position end = move.getEnd();

		updatePos(start);
		updatePos(end);

		mRedrawHandler.obtainMessage().sendToTarget();
	}

	public void setBoard(IBoard board)
	{
		this.board = board;
		initBoard();
	}

}
