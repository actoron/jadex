package jadex.tools.bpmn.runtime.task;

public interface IRuntimeTaskProvider
{

	public abstract String[] getAvailableTaskImplementations();

	public abstract TaskMetaInfo getTaskMetaInfoFor(String implementationClass);

}
