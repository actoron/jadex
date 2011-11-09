package de.unihamburg.vsis.jadexAndroid_test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import jadex.android.bluetooth.JadexBluetoothActivity;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.rules.rulesystem.rules.Operator.InstanceOf;
import jadex.xml.annotation.XMLClassname;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class BaseActivity extends JadexBluetoothActivity {

	protected Button findButtonById(int id) {
		View v = findViewById(id);
		if (v instanceof Button) {
			return (Button) v;
		} else {
			throw new ClassCastException(
					"findButtonById called for a non-button id!");
		}
	}

	protected TextView findTextViewById(int id) {
		View v = findViewById(id);
		if (v instanceof TextView) {
			return (TextView) v;
		} else {
			throw new ClassCastException(
					"findTextViewById called for a non-textView id!");
		}
	}

	protected ListView findListViewById(int id) {
		View v = findViewById(id);
		if (v instanceof ListView) {
			return (ListView) v;
		} else {
			throw new ClassCastException(
					"findListViewById called for a non-listView id!");
		}
	}

	protected EditText findEditTextById(int id) {
		View v = findViewById(id);
		if (v instanceof EditText) {
			return (EditText) v;
		} else {
			throw new ClassCastException(
					"findEditTextById called for a non-listView id!");
		}
	}

	protected IFuture<IComponentIdentifier> startComponent(
			final Class component, final String name,
			IExternalAccess extPlattformAcc) {

		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		IFuture<IComponentManagementService> scheduleStep = extPlattformAcc
				.scheduleStep(new IComponentStep<IComponentManagementService>() {
					@XMLClassname("find-cms")
					public IFuture<IComponentManagementService> execute(
							IInternalAccess ia) {
						Future<IComponentManagementService> ret = new Future<IComponentManagementService>();
						SServiceProvider.getService(ia.getServiceContainer(),
								IComponentManagementService.class,
								RequiredServiceInfo.SCOPE_PLATFORM)
								// ia.getRequiredService("cms")
								.addResultListener(
										ia.createResultListener(new DelegationResultListener<IComponentManagementService>(
												ret)));
						return ret;
					}
				});

		scheduleStep
				.addResultListener(new DefaultResultListener<IComponentManagementService>() {
					@XMLClassname("create-component")
					public void resultAvailable(IComponentManagementService arg0) {
						IComponentManagementService cms = (IComponentManagementService) arg0;
						HashMap<String, Object> args = new HashMap<String, Object>();
						Log.i(Helper.LOG_TAG,
								"Starting " + component.getSimpleName());
						cms.createComponent(
								name,
								component.getName().replaceAll("\\.", "/")
										+ ".class", new CreationInfo(args),
								null)
								.addResultListener(
										new DefaultResultListener<IComponentIdentifier>() {
											@Override
											public void resultAvailable(
													IComponentIdentifier result) {
												// component created!
												Log.i(Helper.LOG_TAG,
														component
																.getSimpleName()
																+ " created!");
												ret.setResult(result);
											}
										});
					}
				});

		return ret;
	}
	
	protected String createRandomPlattformID() {
		UUID randomUUID = UUID.randomUUID();
		return randomUUID.toString().substring(0, 5);
	}
}
