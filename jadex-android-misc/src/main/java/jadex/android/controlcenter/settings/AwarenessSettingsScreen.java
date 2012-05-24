package jadex.android.controlcenter.settings;

import jadex.android.controlcenter.JadexBooleanPreference;
import jadex.base.service.awareness.management.AwarenessManagementAgent;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.commons.transformation.annotations.Classname;
import jadex.commons.transformation.annotations.IncludeFields;

import java.net.InetAddress;

import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

public class AwarenessSettingsScreen extends AComponentSettings implements OnPreferenceChangeListener {

	private AwarenessSettings settings;
	private JadexBooleanPreference cbautoCreate;
	private JadexBooleanPreference cbautoDelete;

	public AwarenessSettingsScreen(IExternalAccess extAcc) {
		super(extAcc);
	}

	@Override
	protected void createPreferenceHierarchy(PreferenceScreen screen) {
		PreferenceCategory proxyCat = new PreferenceCategory(screen.getContext());
		screen.addPreference(proxyCat);
		proxyCat.setTitle("Proxy Settings");

		
		cbautoCreate = new JadexBooleanPreference(screen.getContext());
		cbautoCreate.setTitle("Create On Discovery");
		cbautoCreate.setKey("autocreate");
		cbautoCreate.setOnPreferenceChangeListener(this);
		proxyCat.addPreference(cbautoCreate);
		
		cbautoDelete = new JadexBooleanPreference(screen.getContext());
		cbautoDelete.setTitle("Delete On Disappearance");
		cbautoDelete.setKey("autodelete");
		cbautoDelete.setOnPreferenceChangeListener(this);
		proxyCat.addPreference(cbautoDelete);
		
		
		ThreadSuspendable sus = new ThreadSuspendable();
		AwarenessSettings awarenessSettings = extAcc.scheduleStep(new IComponentStep<AwarenessSettings>() {
			@Override
			public IFuture<AwarenessSettings> execute(IInternalAccess ia) {
				AwarenessManagementAgent agent = (AwarenessManagementAgent)ia;
				AwarenessSettings	ret	= new AwarenessSettings();
				ret.delay	= agent.getDelay();
				ret.fast	= agent.isFastAwareness();
				ret.autocreate	= agent.isAutoCreateProxy();
				ret.autodelete	= agent.isAutoDeleteProxy();
				ret.includes	= agent.getIncludes();
				ret.excludes	= agent.getExcludes();
				return new Future<AwarenessSettings>(ret);
			}
		}).get(sus);
		updateSettings(awarenessSettings);
	}
	
	/**
	 *  Apply settings to GUI.
	 */
	protected void updateSettings(AwarenessSettings settings)
	{
		this.settings = settings;
		cbautoCreate.setValue(settings.autocreate);
		cbautoDelete.setValue(settings.autodelete);
		
//		spdelay.setValue(new Long(settings.delay/1000));
//		cbfast.setSelected(settings.fast);
//		includes.setEntries(settings.includes);
//		excludes.setEntries(settings.excludes);		
	}

	
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		final AwarenessSettings	settings	= new AwarenessSettings();	// local variable for XML transfer
//		settings.delay = ((Number)spdelay.getValue()).longValue()*1000;
//		settings.fast = cbfast.isSelected();
		settings.autocreate = cbautoCreate.isChecked();
		settings.autodelete = cbautoDelete.isChecked();
		
		// TODO: make editable
		settings.delay = this.settings.delay;
		settings.fast = this.settings.fast;
		settings.includes = this.settings.includes;
		settings.excludes = this.settings.excludes;
		
//		settings.includes	= includes.getEntries();
//		settings.excludes	= excludes.getEntries();
		this.settings	= settings;	// todo: wait for step before setting?
		extAcc.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("applySettings")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				AwarenessManagementAgent agent	= (AwarenessManagementAgent)ia;
//				agent.setAddressInfo(settings.address, settings.port);
				agent.setDelay(settings.delay);
				agent.setFastAwareness(settings.fast);
				agent.setAutoCreateProxy(settings.autocreate);
				agent.setAutoDeleteProxy(settings.autodelete);
				agent.setIncludes(settings.includes);
				agent.setExcludes(settings.excludes);
				return IFuture.DONE;
			}
		});
		return true;
	}
	
	
	/**
	 *  The awareness settings transferred between GUI and agent.
	 */
	@IncludeFields
	public static class AwarenessSettings
	{
		/** The inet address. */
		public InetAddress address;
		
		/** The port. */
		public int port;
		
		/** The delay. */
		public long delay;
		
		/** The fast awareness flag. */
		public boolean fast;
		
		/** The autocreate flag. */
		public boolean autocreate;
		
		/** The autocreate flag. */
		public boolean autodelete;
		
		/** The includes list. */
		public String[]	includes;
		
		/** The excludes list. */
		public String[]	excludes;
	}
}
