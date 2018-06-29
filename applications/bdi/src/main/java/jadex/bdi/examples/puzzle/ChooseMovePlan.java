package jadex.bdi.examples.puzzle;

import java.util.ArrayList;
import java.util.List;

import jadex.bdiv3x.runtime.ICandidateInfo;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.SUtil;

/**
 *  Meta-level reasoning plan for choosing between applicable plans.
 */
public class ChooseMovePlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		//System.out.println("Meta");
		ICandidateInfo[] apps = (ICandidateInfo[])getParameterSet("applicables").getValues();

		assert apps.length>0;

		ICandidateInfo sel = null;
		IBoard board = (IBoard)getBeliefbase().getBelief("board").getFact();
		String ml = (String)getBeliefbase().getBelief("ml").getFact();
		if(ml.equals("none"))
			sel = apps[0];
		else if(ml.equals("short"))
			sel = selectPlan(apps, board, true, false, false, true);
		else if(ml.equals("long"))
			sel = selectPlan(apps, board, true, true, false, true);
		else if(ml.equals("same_long"))
			sel = selectPlan(apps, board, true, true, true, true);
		else if(ml.equals("alter_long"))
			sel = selectPlan(apps, board, false, true, true, true);
		else
			throw new RuntimeException("Wrong meta-level strategy.");

		getParameterSet("result").addValue(sel);
	}

	/**
	 *  Select a move with respect to color resp. move kind (jump vs. normal).
	 *  @param apps The list of applicables.
	 *  @param board The board.
	 *  @param same Prefer moves of same color.
	 *  @param jump Prefer jump moves.
	 *  @param consider_color Consider the color.
	 *  @param consider_jump Consider the move kind.
	 */
	protected ICandidateInfo selectPlan(ICandidateInfo[] apps, IBoard board, boolean same, boolean jump,
		boolean consider_color, boolean consider_jump)
	{
		List<ICandidateInfo> sel_col = new ArrayList<ICandidateInfo>();
		if(consider_color)
		{
			for(int i=0; i<apps.length; i++)
			{
				Move tmpmove = null;
				tmpmove = (Move)apps[i].getPlan().getParameter("move").getValue();
				if(matchColor(board, tmpmove, same))
				{
					sel_col.add(apps[i]);
				}
			}
		}
		else
		{
			sel_col = SUtil.arrayToList(apps);
		}

		List<ICandidateInfo> sel_jump = new ArrayList<ICandidateInfo>();
		if(consider_jump)
		{
			for(int i=0; i<sel_col.size(); i++)
			{
				ICandidateInfo tmp = (ICandidateInfo)sel_col.get(i);
				Move tmpmove = (Move)tmp.getPlan().getParameter("move").getValue();
				if(matchJump(board, tmpmove, jump))
				{
					sel_jump.add(tmp);
				}
			}
		}
		else
		{
			sel_jump = sel_col;
		}

		assert sel_col.size()>0 || sel_jump.size()>0 || apps.length>0;

		ICandidateInfo ret = null;
		if(sel_jump.size()>0)
			ret = (ICandidateInfo)sel_jump.get(0);
		else if(sel_col.size()>0)
			ret = (ICandidateInfo)sel_col.get(0);
		else
			ret = apps[0];

		return ret;
	}

	/**
	 *  Match move with color constraint.
	 */
	protected boolean matchColor(IBoard board, Move move, boolean prefer_samecolor)
	{
		Piece piece = board.getPiece(move.getStart());
		if(piece==null)
			throw new RuntimeException("Impossible move: "+move);
		boolean same = board.wasLastMoveWhite()==board.getPiece(move.getStart()).isWhite();
		return prefer_samecolor==same;
	}

	/**
	 *  Match move with jump constraint.
	 */
	protected boolean matchJump(IBoard board, Move move, boolean prefer_jump)
	{
		return prefer_jump==move.isJumpMove();
	}
}
