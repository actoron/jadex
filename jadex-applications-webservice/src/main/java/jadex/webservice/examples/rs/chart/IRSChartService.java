package jadex.webservice.examples.rs.chart;

import jadex.commons.future.IFuture;
import jadex.extension.rs.invoke.annotation.QueryParamMapper;
import jadex.extension.rs.publish.annotation.ParameterMapper;
import jadex.extension.rs.publish.annotation.ResultMapper;
import jadex.micro.annotation.Value;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

//@BaseURI("https://chart.googleapis.com/chart")
@Path("https://chart.googleapis.com")
public interface IRSChartService
{
	/**
	 *  Get a chart.
	 */
	@GET
	@Path("chart")
	@Consumes("text/plain")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@QueryParamMapper(value="cht", mapper=@Value("new ConstantStringMapper(\"p3\")"))
//	@ParameterMapper(@Value(clazz=ChartParameterMapper.class))
//	@ResultMapper(@Value(clazz=ChartResultMapper.class))
	public @ResultMapper(@Value(clazz=ChartResultMapper.class)) IFuture<byte[]> getPieChart(
		@QueryParamMapper(value="chs", mapper=@Value(clazz=SizeStringMapper.class), source={0,1}) int width, int height, 
		@QueryParamMapper(value="chd", mapper=@Value("new IterableStringMapper(\"t:\",\",\")")) double[] data, 
		@QueryParamMapper(value="chl", mapper=@Value("new IterableStringMapper(\"|\")")) String[] labels);

//	/**
//	 *  Get a chart.
//	 */
//	@GET
//	@Path("chart")
//	@Consumes("text/plain")
//	@Produces(MediaType.APPLICATION_OCTET_STREAM)
//	@ParameterMapper(@Value(clazz=ChartParameterMapper.class))
//	@ResultMapper(@Value(clazz=ChartResultMapper.class))
//	public IFuture<byte[]> getPieChart(int width, int height, double[] data, String[] labels);

	
//	https://chart.googleapis.com/chart?
//	    This is the base URL for all chart requests. (However, see Improving Performance on Pages with Many Charts below for an optional variation for pages with multiple charts.)
//	cht=p3
//	    The chart type: here, a 3D pie chart.
//	chs=250x100
//	    The chart size (width x height), in pixels. See the maximum values here.
//	chd=t:60,40
//	    The chart data. This data is in simple text format, but there are other formats.
//	chl=Hello|World
//	    The slice labels.
//	&
}
