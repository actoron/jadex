package nuggets;

/** IDelayedOperation 
 * @author walczak
 * @since  Jan 19, 2006
 */
public interface IDelayedOperation
{
	/** perform the operation 
	 * @param asm 
	 * @throws Exception 
	 */
	void perform(IAssembler asm) throws Exception;
}