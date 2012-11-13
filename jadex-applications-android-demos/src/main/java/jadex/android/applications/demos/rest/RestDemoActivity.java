package jadex.android.applications.demos.rest;

import java.util.ArrayList;

import jadex.android.JadexAndroidActivity;
import jadex.android.applications.demos.R;
import jadex.android.service.JadexPlatformManager;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This Activity shows a sample REST Service Call beeing executed using Jadex.
 */
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
	
	private final int maxWidth = 600;
	private final int maxHeight = 800;
	
	private DataItemArrayAdapter dataItemAdapter;
	private ImageView imageView;
	
	/** Constructor */
	public RestDemoActivity()
	{
		super();
		setPlatformAutostart(true);
		setPlatformKernels(JadexPlatformManager.KERNEL_MICRO, JadexPlatformManager.KERNEL_COMPONENT);
		setPlatformName("restDemoPlatform");
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
		imageView = (ImageView) findViewById(R.id.rest_imageView);
		
		getChartButton.setEnabled(false);
		getChartButton.setOnClickListener(buttonClickListener);
		
		heightBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
		widthBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
		
		heightBar.setMax(maxHeight);
		widthBar.setMax(maxWidth);
		
		heightBar.setProgress(100);
		widthBar.setProgress(250);

		dataItemAdapter = new DataItemArrayAdapter(this);
		dataListView.setAdapter(dataItemAdapter);
		
		addDataButton.setOnClickListener(buttonClickListener);
	}
	
	@Override
	protected void onPlatformStarted(IExternalAccess result)
	{
		super.onPlatformStarted(result);
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				startComponent("ChartProviderComponent", "jadex/android/applications/demos/rest/ChartProvider.component.xml")
				.addResultListener(componentCreatedResultListener);
			}
		});
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
			if (v == getChartButton) {
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						setProgressBarIndeterminate(false);
						getChartButton.setEnabled(true);
					}
				});
				final Future<byte[]> fut = new Future<byte[]>();
				SServiceProvider.getService(getPlatformAccess().getServiceProvider(), IChartService.class)
					.addResultListener(new DefaultResultListener<IChartService>()
				{
	
					@Override
					public void resultAvailable(IChartService cs)
					{
						int height = heightBar.getProgress();
						int width = widthBar.getProgress();
						double[][] data = new double[][] {{30, 50, 20, 90}, {55, 88, 11, 14}};
						IFuture<byte[]> lineChart = cs.getLineChart(width, height, data, new String[]{"a", "b", "c", "d"} , new Integer[]{Color.BLACK, Color.BLUE, Color.CYAN, Color.YELLOW});
						lineChart.addResultListener(new DelegationResultListener<byte[]>(fut));
					}
					
				});
				
				fut.addResultListener(new DefaultResultListener<byte[]>()
				{
	
					@Override
					public void resultAvailable(final byte[] result)
					{
						final Bitmap image = BitmapFactory.decodeByteArray(result, 0, result.length);
						final DisplayMetrics dm = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(dm);
						runOnUiThread(new Runnable()
						{
							
							@Override
							public void run()
							{
								setProgressBarIndeterminate(false);
								getChartButton.setEnabled(true);
						        imageView.setMinimumHeight(dm.heightPixels);
						        imageView.setMinimumWidth(dm.widthPixels);
								imageView.setImageBitmap(image);
							}
						});
					}
				});
			} else if (v == addDataButton) {
				dataItemAdapter.add(new DataItem());
			}
		}
	};
	
	// ---------------- Input/Form handling -------------------
	
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
			if (seekBar == heightBar) {
				heightText.setText(""+progress);
			} else if (seekBar == widthBar) {
				widthText.setText(""+progress);
			}
		}
	};

}
