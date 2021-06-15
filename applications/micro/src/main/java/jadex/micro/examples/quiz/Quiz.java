package jadex.micro.examples.quiz;

import java.util.ArrayList;
import java.util.List;

/**
 *  The quiz data class.
 */
public class Quiz
{
	/** The questions. */
	protected List<Question> questions;

	/**
	 *  Create a new quiz.
	 */
	public Quiz()
	{
		this.questions = new ArrayList<Question>();
	}
	
	/**
	 *  Create a new quiz.
	 */
	public Quiz(List<Question> questions)
	{
		this.questions = questions;
	}

	/**
	 * @return the questions
	 */
	public List<Question> getQuestions()
	{
		return questions;
	}

	/**
	 * @param questions the questions to set
	 */
	public void setQuestions(List<Question> questions)
	{
		this.questions = questions;
	}
	
	/**
	 *  Get a question per index.
	 */
	public Question getQuestion(int no)
	{
		return questions.get(no);
	}
	
	/**
	 *  Get the number of questions.
	 */
	public int getNumberOfQuestions()
	{
		return questions.size();
	}
	
	/**
	 *  Add a question.
	 */
	public void addQuestion(Question q)
	{
		this.questions.add(q);
	}
}
