package jadex.micro.examples.quiz;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.OnStart;
import jadex.bridge.service.annotation.Security;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  The quiz master agent.
 */
@Agent
@Imports("jadex.bridge.service.annotation.Security")
@Arguments(
	@Argument(name="scope", clazz=ServiceScope.class))
@ProvidedServices(@ProvidedService(name="quiz", type=IQuizService.class,
	scope=ServiceScope.EXPRESSION, scopeexpression="$args.scope",
	security=@Security(roles="%{$args.scope.isGlobal() ? Security.UNRESTRICTED : Security.TRUSTED}")))
public class QuizMasterAgent implements IQuizService
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The current participants. */
	protected Map<IComponentIdentifier, SubscriptionIntermediateFuture<QuizEvent>> subscriptions = new HashMap<>();
	
	/** The quiz. */
	protected Quiz quiz;
	
	/** The results. */
	protected Map<IComponentIdentifier, QuizResults> results = new HashMap<IComponentIdentifier, QuizResults>();
	
	/** The delay between questions. */
	@AgentArgument
	protected long delay = 15000;
	
	/** The current question no. */
	protected int questioncnt = 0;
	
	@OnStart
	public void start()
	{
		this.quiz = createQuiz();
		
		agent.scheduleStep(s ->
		{
			while(true)
			{
				//System.out.println("master working");
				if(questioncnt<quiz.getNumberOfQuestions())
				{
					Question question = quiz.getQuestion(questioncnt);
					publishQuestion(question, questioncnt);
					questioncnt++;
					agent.waitForDelay(delay).get();
				}
				else
				{
					questioncnt = 0;
				}
			}
		});
	}
	
	/**
	 *  Publish a question to all subscribers.
	 *  @param question The question.
	 */
	public void publishQuestion(Question question, int questioncnt)
	{
		for(SubscriptionIntermediateFuture<QuizEvent> subscription: subscriptions.values())
		{
			subscription.addIntermediateResult(new QuestionEvent(question, questioncnt));
		}
	}
	
	/**
	 *  Create a quiz.
	 */
	protected Quiz createQuiz()
	{
		Quiz quiz = new Quiz();
		quiz.addQuestion(new Question("Which software pattern can be used to exchange algorithms?", Arrays.asList(new String[]{"Visitor", "Observer", "Memento", "Strategy"}), 3));
		quiz.addQuestion(new Question("What does the L in SOLID stands for?", Arrays.asList(new String[]{"Lavrow", "Liskov", "Low", "Lightweight"}), 1));
		quiz.addQuestion(new Question("In the SOA triangle there are the entities service provider, user and ...?", Arrays.asList(new String[]{"ESB", "Bus", "Registry", "Mesh"}), 2));
		quiz.addQuestion(new Question("An agent often is reactive and ...?", Arrays.asList(new String[]{"emotional", "fast", "proactive", "scalable"}), 2));
		return quiz;
	}
	
	/**
	 *  Method to participate in the quiz.
	 *  @return The subscription for receiving quiz events.
	 */
	public ISubscriptionIntermediateFuture<QuizEvent> participate()
	{
		//SubscriptionIntermediateFuture<QuizEvent> ret = new SubscriptionIntermediateFuture<QuizEvent>();
		SubscriptionIntermediateFuture<QuizEvent> ret = (SubscriptionIntermediateFuture<QuizEvent>)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, agent);
		IComponentIdentifier caller = ServiceCall.getCurrentInvocation().getCaller();
		subscriptions.put(caller, ret);
		ret.setTerminationCommand(new ITerminationCommand()
		{
			public void terminated(Exception reason)
			{
				subscriptions.remove(caller);
			}
			
			public boolean checkTermination(Exception reason)
			{
				return true;
			}
		});
		return ret;
	}

	/**
	 *  Send an answer.
	 *  @param answer The answer.
	 */
	public IFuture<Void> sendAnswer(int answer, int questioncnt)
	{
		IComponentIdentifier caller = ServiceCall.getCurrentInvocation().getCaller();
		
		if(questioncnt!=this.questioncnt && questioncnt+1!=this.questioncnt)
			return new Future<Void>(new RuntimeException("Answer only to current questions allowed: "+questioncnt+" "+this.questioncnt));
		
		QuizResults res = results.get(caller);
		if(res==null)
		{
			res = new QuizResults();
			results.put(caller, res);
		}
		System.out.println("answer: "+answer+" "+questioncnt+" "+(quiz.getQuestion(questioncnt).getSolution()==answer));
		//System.out.println("antwort: "+(quiz.getQuestion(questioncnt).getSolution()==answer)+" "+answer);
		res.addResult(questioncnt, quiz.getQuestion(questioncnt).getSolution()==answer);
		//System.out.println("res: "+res.size()+" "+quiz.getNumberOfQuestions());
		if(res.size()==quiz.getNumberOfQuestions())
		{
			SubscriptionIntermediateFuture<QuizEvent> s = subscriptions.get(caller);
			if(s!=null)
			{
				s.addIntermediateResult(new ResultEvent(res));
				s.setFinished();
				subscriptions.remove(caller);
			}
			else
			{
				System.out.println("not found: "+caller+" "+results+" "+subscriptions);
			}
		}
		
		return IFuture.DONE;
	}
}
