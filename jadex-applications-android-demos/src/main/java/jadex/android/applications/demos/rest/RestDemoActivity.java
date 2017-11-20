package jadex.android.applications.demos.rest;

import jadex.android.JadexAndroidActivity;
import jadex.android.applications.demos.R;
import jadex.android.applications.demos.rest.view.ChartDataRowAdapter;
import jadex.android.applications.demos.rest.view.RestImageActivity;
import jadex.android.commons.JadexPlatformOptions;
import jadex.base.IPlatformConfiguration;
import jadex.base.IRootComponentConfiguration;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This Activity shows a sample REST Service Call beeing executed using Jadex.
 */
@Reference
public class RestDemoActivity extends JadexAndroidActivity
{

	private Button getChartButton;
	private Button addDataButton;
	private Spinner chartTypeSpinner;
	private ListView dataListView;
	private SeekBar heightBar;
	private SeekBar widthBar;
	private TextView heightText;
	private TextView widthText;

	private ChartDataRowAdapter dataItemAdapter;

	/** Google Limits. **/
	private static final int MAX_RESOLUTION = 300000;
	private static final int MAX_LENGTH_WIDTH = 1000;

	/** Constructor */
	public RestDemoActivity()
	{
		super();
		setPlatformAutostart(true);
		IPlatformConfiguration config = getPlatformConfiguration();
		config.setPlatformName("restDemoPlatform");
		config.getRootConfig().setKernels(IRootComponentConfiguration.KERNEL_MICRO, IRootComponentConfiguration.KERNEL_COMPONENT);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rest_demo);
		getChartButton = (Button) findViewById(R.id.rest_getChartButton);
		addDataButton = (Button) findViewById(R.id.rest_addDataItem);
		chartTypeSpinner = (Spinner) findViewById(R.id.rest_chartTypeSpinner);
		dataListView = (ListView) findViewById(R.id.rest_dataList);
		heightBar = (SeekBar) findViewById(R.id.rest_height);
		widthBar = (SeekBar) findViewById(R.id.rest_width);
		heightText = (TextView) findViewById(R.id.rest_height_text);
		widthText = (TextView) findViewById(R.id.rest_width_text);
		labelText = (EditText) findViewById(R.id.rest_labelText);
		
		getChartButton.setEnabled(false);
		getChartButton.setOnClickListener(buttonClickListener);

		heightBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
		widthBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

		final DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		heightBar.setMax(Math.max(dm.heightPixels, MAX_LENGTH_WIDTH));
		widthBar.setMax(Math.max(dm.widthPixels, MAX_LENGTH_WIDTH));

		heightBar.setProgress(600);
		widthBar.setProgress(500);

		dataItemAdapter = new ChartDataRowAdapter(this);
		dataListView.setAdapter(dataItemAdapter);

		// add initial data line
		ChartDataRow item = new ChartDataRow();
		labelText.setText("a,b,c,d,e");
		item.setColor(Color.BLUE);
		item.setData(new double[]
		{ 1, 8, 13, 5, 20 });
		dataItemAdapter.add(item);

		addDataButton.setOnClickListener(buttonClickListener);
	}

	private void showImage(byte[] imageArr)
	{
		Intent i = new Intent(this, RestImageActivity.class);
		i.putExtra(RestImageActivity.EXTRA_IMAGE, imageArr);
		startActivity(i);
	}

	@Override
	protected void onPlatformStarted(IExternalAccess result)
	{
		super.onPlatformStarted(result);
		startComponent("ChartProviderComponent", "jadex/android/applications/demos/rest/ChartProvider.component.xml").addResultListener(
				componentCreatedResultListener);
	}

	private IResultListener<IComponentIdentifier> componentCreatedResultListener = new DefaultResultListener<IComponentIdentifier>()
	{
		public void resultAvailable(final IComponentIdentifier bdiId)
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					getChartButton.setEnabled(true);
				}
			});
		}
	};

	private OnClickListener buttonClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			if (v == getChartButton)
			{
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						setProgressBarIndeterminate(true);
						getChartButton.setEnabled(false);
					}
				});
				final Future<byte[]> fut = new Future<byte[]>();
				SServiceProvider.getService(getPlatformAccess(), IChartService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(
						new DefaultResultListener<IChartService>()
						{

							@Override
							public void resultAvailable(IChartService cs)
							{
								int count = dataItemAdapter.getCount();

								int height = heightBar.getProgress();
								int width = widthBar.getProgress();
								double[][] data = new double[count][];
								String[] labels = labelText.getText().toString().replaceAll("[^\\w,]*", "").split(",");
								Integer[] colors = new Integer[count];

								for (int i = 0; i < count; i++)
								{
									ChartDataRow item = dataItemAdapter.getItem(i);
									data[i] = item.getData();
									colors[i] = item.getColor();
								}
								IFuture<byte[]> chart;

								switch (chartTypeSpinner.getSelectedItemPosition())
								{
								case 0: // bar
									chart = cs.getBarChart(width, height, data, labels, colors);
									break;
								case 1: // line
									chart = cs.getLineChart(width, height, data, labels, colors);
									break;
								default: // pie
									chart = cs.getPieChart(width, height, data, labels, colors);
									break;
								}

								chart.addResultListener(new DelegationResultListener<byte[]>(fut));
							}

						});

				fut.addResultListener(new DefaultResultListener<byte[]>()
				{

					@Override
					public void resultAvailable(final byte[] result)
					{
						runOnUiThread(new Runnable()
						{

							@Override
							public void run()
							{
								setProgressBarIndeterminate(false);
								showImage(result);
								getChartButton.setEnabled(true);
							}

						});
					}

					public void exceptionOccurred(final Exception exception)
					{
						runOnUiThread(new Runnable()
						{

							@Override
							public void run()
							{
								Toast.makeText(RestDemoActivity.this, exception.toString(), Toast.LENGTH_LONG).show();
							}
						});
					};
				});

			} else if (v == addDataButton)
			{
				ChartDataRow item = new ChartDataRow();
				item.setColor(Color.RED);
				dataItemAdapter.add(item);
			}
		}
	};

	// ---------------- Input handling for seekBars -------------------

	private OnSeekBarChangeListener onSeekBarChangeListener = new OnSeekBarChangeListener()
	{

		@Override
		public void onStopTrackingTouch(SeekBar seekBar)
		{
			seekBar.setSecondaryProgress(seekBar.getProgress());
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar)
		{
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
		{
			if (seekBar == heightBar)
			{
				if (progress * widthBar.getProgress() > MAX_RESOLUTION)
				{
					widthBar.setProgress(MAX_RESOLUTION / progress);
				}
				heightText.setText("" + progress);
			} else if (seekBar == widthBar)
			{
				if (progress * heightBar.getProgress() > MAX_RESOLUTION)
				{
					heightBar.setProgress(MAX_RESOLUTION / progress);
				}
				widthText.setText("" + progress);
			}
		}
	};
	private EditText labelText;

}
