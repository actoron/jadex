package jadex.commons.future;

import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.*;

public class FutureTest
{

	@Test
	public void thenApply()
	{
		IFuture<String> thenApply = getStringFuture().thenApply(s -> s + "_test");
		assertEquals("hello_test", thenApply.get());
	}
	
	@Test
	public void thenCompose()
	{
		IFuture<String> apply2 = getStringFuture().thenCompose(this::getWorld);
		assertEquals("hello world", apply2.get());
	}
	
	@Test
	public void thenAccept() 
	{
		Future<String> res = new Future<String>();
		getStringFuture().thenAccept(s -> {
			res.setResult(s);
		});
		String value = res.get();
		assertEquals("hello", value);
	}

	@Test
	public void thenCombine() 
	{
		IFuture<String> combined = getStringFuture().thenCombine(getABC(), (s, abc) -> {
			StringBuilder result = new StringBuilder();
			abc.forEach(a -> result.append(a));
			return new String(s + result.toString());
		}, null);
		
		String value = combined.get();
		assertEquals("helloabc", value);
	}
	
//	@Test
//	public void thenCombineAsync() 
//	{
//		IFuture<String> combined = getStringFuture().thenCombineAsync(getABC(), (s, abc) -> {
//			Future<String> future = new Future<String>();
//			StringBuilder result = new StringBuilder();
//			abc.forEach(a -> result.append(a));
//			future.setResult(s + result.toString());
//			return future;
//		}, null);
//		
//		String value = combined.get();
//		assertEquals("helloabc", value);
//	}
	
	
	@Test
	public void applyToEither_firstHasResult() 
	{
		Future<String> s1 = new Future<String>();
		Future<String> s2 = new Future<String>();
		
		IFuture<String> applyToEither = s1.applyToEither(s2, s -> {
			return s;
		}, null);
		
		s1.setResult("Test");
		String string = applyToEither.get();
		
		assertEquals("Test", string);
	}
	
	@Test
	public void applyToEither_secondHasResult() 
	{
		Future<String> s1 = new Future<String>();
		Future<String> s2 = new Future<String>();
		
		IFuture<String> applyToEither = s1.applyToEither(s2, s -> {
			return s;
		}, null);
		
		s2.setResult("Test");
		String string = applyToEither.get();
		
		assertEquals("Test", string);
	}
	
	@Test
	public void applyToEither_FirstIsException() 
	{
		Future<String> s1 = new Future<String>();
		Future<String> s2 = new Future<String>();
		
		IFuture<String> applyToEither = s1.applyToEither(s2, s -> {
			return s;
		}, null);
		
		s1.setException(new Exception("bad"));
		s2.setResult("Test");
		String string = applyToEither.get();
		
		assertEquals("Test", string);
	}
	
	@Test
	public void applyToEither_SecondIsException() 
	{
		Future<String> s1 = new Future<String>();
		Future<String> s2 = new Future<String>();
		
		IFuture<String> applyToEither = s1.applyToEither(s2, s -> {
			return s;
		}, null);
		
		s1.setResult("Test");
		s2.setException(new Exception("bad"));
		String string = applyToEither.get();
		
		assertEquals("Test", string);
	}
	
	@Test
	public void applyToEither_BothAreExceptions() 
	{
		Future<String> s1 = new Future<String>();
		Future<String> s2 = new Future<String>();
		
		IFuture<String> applyToEither = s1.applyToEither(s2, s -> {
			return s;
		}, null);
		
		s1.setException(new Exception("bad"));
		s2.setException(new Exception("bad"));

		try {
			String string = applyToEither.get();
			fail("Exception expected");
		} catch (Exception e) {
			assertEquals("bad", e.getMessage());
		}
	}

    public IFuture<String> getStringFuture()
    {
        return new Future<String>("hello");
    }

    public IFuture<String> getWorld(String hello)
    {
        return new Future<String>(hello+" world");
    }

    public IIntermediateFuture<String> getABC()
    {
        return new IntermediateFuture<>(Arrays.asList("a", "b", "c"));
    }

    public IFuture<String> getD(String arg)
    {
        Future<String> ret = new Future<>();
        ret.setResult(arg+"_1");
        return ret;
    }
    
    public IIntermediateFuture<String> getE(String arg)
    {
        IntermediateFuture<String> ret = new IntermediateFuture<>();
        ret.addIntermediateResult(arg+"_1");
        ret.addIntermediateResult(arg+"_2");
        ret.addIntermediateResult(arg+"_3");
        ret.setFinished();
        return ret;
    }
}
