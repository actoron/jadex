package jadex.bdibpmn.examples.puzzle;

import jadex.bdi.runtime.ICandidateInfo;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;
import jadex.bpmn.task.info.ParameterMetaInfo;
import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.bridge.IInternalAccess;
import jadex.commons.SUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *  Meta-level reasoning plan for choosing between applicable plans.
 */
public class ChooseMoveTask extends AbstractTask
{
	/**
	 *  Get the meta information about the task.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "Write our print used memory for benchmark agent.";
		
		ParameterMetaInfo pmi1 = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			ICandidateInfo[].class, "applicables", null, "The available move plans.");
		ParameterMetaInfo pmi2 = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			IBoard.class, "board", null, "The game board.");
		ParameterMetaInfo pmi3 = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "ml", null, "The meta-level reasoning strategy.");
		ParameterMetaInfo pmi4 = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_OUT, 
			ICandidateInfo.class, "result", null, "The selected plan.");

		return new TaskMetaInfo(desc, new ParameterMetaInfo[]{pmi1, pmi2, pmi3, pmi4}); 
	}
	
	//-------- methods --------
	
	public void doExecute(ITaskContext context, IInternalAccess instance) throws Exception
	{
		//System.out.println("Meta");
		ICandidateInfo[] apps = (ICandidateInfo[])context.getParameterValue("applicables");
		IBoard board = (IBoard)context.getParameterValue("board");
		String ml = (String)context.getParameterValue("ml");

		assert apps.length>0;

		ICandidateInfo sel = null;
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

		context.setParameterValue("result", sel);
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
		List sel_col = new ArrayList();
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

		List sel_jump = new ArrayList();
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
