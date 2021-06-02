package jadex.webservice.examples.rs.chart;

import java.awt.Color;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import jadex.bridge.service.annotation.Value;
import jadex.commons.future.IFuture;
import jadex.extension.rs.invoke.annotation.ParameterMapper;
import jadex.extension.rs.publish.annotation.ResultMapper;

/**
 *  The mapping information for the rest service.
 *  Describes how the Java service call information is used
 *  to generate the rest service call.
 */
//@BaseURI("https://chart.googleapis.com/chart")
@Path("https://chart.googleapis.com")
public interface IRSChartService
{
	/**
	 *  Get a bar chart.
	 */
	@GET
	@Path("chart")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@ParameterMapper(value="cht", mapper=@Value("new ConstantStringMapper(\"bhs\")"))
	public @ResultMapper(@Value(clazz=ChartResultMapper.class)) IFuture<byte[]> getBarChart(
            @ParameterMapper(value = "chs", mapper = @Value(clazz = SizeStringMapper.class), source = {0, 1}) int width, int height,
            @ParameterMapper(value = "chd", mapper = @Value("new IterableStringMapper(\"t:\",\"|\", null, new IterableStringMapper(\",\"))")) double[][] data,
            @ParameterMapper(value = "chl", mapper = @Value("new IterableStringMapper(\"|\")")) String[] labels,
            @ParameterMapper(value = "chco", mapper = @Value("new IterableStringMapper(\",\", new ColorStringMapper())")) Color[] colors);

	/**
	 *  Get a line chart.
	 */
	@GET
	@Path("chart")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@ParameterMapper(value="cht", mapper=@Value("new ConstantStringMapper(\"lc\")"))
	public @ResultMapper(@Value(clazz=ChartResultMapper.class)) IFuture<byte[]> getLineChart(
            @ParameterMapper(value = "chs", mapper = @Value(clazz = SizeStringMapper.class), source = {0, 1}) int width, int height,
            @ParameterMapper(value = "chd", mapper = @Value("new IterableStringMapper(\"t:\",\"|\", null, new IterableStringMapper(\",\"))")) double[][] data,
//		@QueryParamMapper(value="chd", mapper=@Value("new IterableStringMapper(\"t:\",\",\")")) double[] data, 
            @ParameterMapper(value = "chl", mapper = @Value("new IterableStringMapper(\"|\")")) String[] labels,
            @ParameterMapper(value = "chco", mapper = @Value("new IterableStringMapper(\",\", new ColorStringMapper())")) Color[] colors);
	
	/**
	 *  Get a pie chart.
	 */
	@GET
	@Path("chart")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@ParameterMapper(value="cht", mapper=@Value("new ConstantStringMapper(\"pc\")"))
	public @ResultMapper(@Value(clazz=ChartResultMapper.class)) IFuture<byte[]> getPieChart(
            @ParameterMapper(value = "chs", mapper = @Value(clazz = SizeStringMapper.class), source = {0, 1}) int width, int height,
            @ParameterMapper(value = "chd", mapper = @Value("new IterableStringMapper(\"t:\",\"|\", null, new IterableStringMapper(\",\"))")) double[][] data,
//		@QueryParamMapper(value="chd", mapper=@Value("new IterableStringMapper(\"t:\",\",\")")) double[] data, 
            @ParameterMapper(value = "chl", mapper = @Value("new IterableStringMapper(\"|\")")) String[] labels,
            @ParameterMapper(value = "chco", mapper = @Value("new IterableStringMapper(\",\", new ColorStringMapper())")) Color[] colors);

	
	//-------- alternatives --------
	
//	/**
//	 *  Get a chart.
//	 */
//	@GET
//	@Path("chart")
//	@Produces(MediaType.APPLICATION_OCTET_STREAM)
//	@ParametersMapper(@Value(clazz=ChartParameterMapper.class))
//	@ResultMapper(@Value(clazz=ChartResultMapper.class))
//	public IFuture<byte[]> getPieChart(int width, int height, double[] data, String[] labels);

//	/**
//	 *  Get a bar chart.
//	 */
//	@POST
//	@Path("chart")
//	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//	@Produces(MediaType.APPLICATION_OCTET_STREAM)
//	@ParamMapper(value="cht", mapper=@Value("new ConstantStringMapper(\"bhs\")"))
//	public @ResultMapper(@Value(clazz=ChartResultMapper.class)) IFuture<byte[]> getBarChart(
//		@ParamMapper(value="chs", mapper=@Value(clazz=SizeStringMapper.class), source={0,1}) int width, int height, 
//		@ParamMapper(value="chd", mapper=@Value("new IterableStringMapper(\"t:\",\"|\", null, new IterableStringMapper(\",\"))")) double[][] data, 
//		@ParamMapper(value="chl", mapper=@Value("new IterableStringMapper(\"|\")")) String[] labels,
//		@ParamMapper(value="chco", mapper=@Value("new IterableStringMapper(\",\", new ColorStringMapper())")) Color[] colors);
	
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
