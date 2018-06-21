package jadex.extension.rs.invoke;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.FileCopyUtils;

/**
 * Reads an {@link HttpInputMessage} and converts it to a {@link RestResponse}.
 */
public class RestResponseHTTPMessageConverter extends AbstractHttpMessageConverter
{

	@Override
	public List getSupportedMediaTypes()
	{
		// accept all types for now
		return Collections.singletonList(MediaType.ALL);
	}

	@Override
	protected boolean supports(Class clazz)
	{
		return true;
	}

	@Override
	protected Object readInternal(Class clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException
	{
		RestResponse response = new RestResponse();
		HttpHeaders headers = inputMessage.getHeaders();
		
		response.setContentType(headers.getContentType().toString());
		response.setContentLength(headers.getContentLength());
		response.setDate(headers.getDate());
		
		long contentLength = headers.getContentLength();
		if (contentLength >= 0) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream((int) contentLength);
			FileCopyUtils.copy(inputMessage.getBody(), bos);
			response.setTargetByteArray(bos.toByteArray());
		}
		else {
			response.setTargetByteArray(FileCopyUtils.copyToByteArray(inputMessage.getBody()));
		}
		return response;
	}

	@Override
	protected void writeInternal(Object t, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException
	{
		if (t instanceof byte[]) {
			outputMessage.getBody().write((byte[])t);
		}
	}

}
