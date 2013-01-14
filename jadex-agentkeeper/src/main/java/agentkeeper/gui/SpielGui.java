package agentkeeper.gui;

import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.observer.gui.ObserverCenter;
import jadex.extension.envsupport.observer.gui.plugin.AbstractInteractionPlugin;
import jadex.commons.IPropertyObject;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

/**
 * Das Spielinterface f�r Spieleraktionen
 * 
 * 
 * @author 7willuwe
 * 
 */

public class SpielGui extends AbstractInteractionPlugin implements Listener, IPropertyObject {
	
	private JPanel _mainpanel;

	private UserEingabenManager _usermanager;
	private ObserverCenter _obsCenter;
	private IEnvironmentSpace _space;
	
	private IVector2 _zielpos;
	private ImageIcon _spielicons[];
	

	private int _typ;
	
	private JLabel _title;
	private JLabel _logo;
	private JLabel _statusAllgemein;
	private JLabel _forschung_status;

	private JSeparator _pane;
	private JLabel _koord;
	private JLabel _koordExpl;
	private JLabel _buttonExpl;
	private JLabel _buttonExpl2;
	private JLabel _buttonExpl3;
	private JLabel _infos;
//	private JButton _abrBut;
	private JButton _bauTraining;
	private JButton _bauEssen;
	private JButton _bauSchlafen;
	private JButton _bauLernen;
	private JButton _bauFolter;
	private JButton _zauberImp;
//	private JButton _zauberBlitz;


	public SpielGui() {
		
		_typ = 0;
		
		this.initWindow( );

	}

	protected void initWindow() {
		
		_mainpanel = new JPanel();
		_mainpanel.setLayout(new GridBagLayout());
		_mainpanel.setBackground(Color.gray);

		_pane = new JSeparator();

		_statusAllgemein = new JLabel("<html><u>Status</u>Gold: 0</html>" );
		_statusAllgemein.setForeground(Color.orange);

		_forschung_status = new JLabel("Forschung: 0");
		_forschung_status.setForeground(Color.cyan);
		_koordExpl = new JLabel("Koordinaten: ");
		_koord = new JLabel("x & y");
		_koord.setForeground(Color.magenta);
		_buttonExpl = new JLabel("Aktion waehlen:");
		_buttonExpl.setForeground(Color.darkGray);
		_buttonExpl2 = new JLabel("Bauen:");

//		_abrBut = new JButton("Abreissen");
		_bauTraining = new JButton("Training (50)");
		_bauEssen = new JButton("Essen (35)");
		_bauSchlafen = new JButton("Schlafen (25)");
		_bauLernen = new JButton("Lernen (100)");
		_bauFolter = new JButton("Folter (150)");

		_buttonExpl3 = new JLabel("Zauber:");
		_zauberImp = new JButton("(F:15) (M: 500)");
//		_zauberBlitz = new JButton("(F: 25) (M: 250)");
		
		_infos = new JLabel("");
		
		ImageIcon title = new ImageIcon(SpielGui.class.getResource("bilder/title.jpg"));

		_title = new JLabel(title);

		ImageIcon logo = new ImageIcon(SpielGui.class.getResource("bilder/logo.png"));

		_logo = new JLabel(logo);
		_spielicons = new ImageIcon[7];
		
		_spielicons[UserEingabenManager.ZAUBERIMP] = new ImageIcon(SpielGui.class.getResource("bilder/imp.jpg"));
		_spielicons[UserEingabenManager.BAUTRAINING] = new ImageIcon(SpielGui.class.getResource("bilder/trainingroom.jpg"));
		_spielicons[UserEingabenManager.BAULAIR] = new ImageIcon(SpielGui.class.getResource("bilder/lair.jpg"));
		_spielicons[UserEingabenManager.BAUHATCHERY] = new ImageIcon(SpielGui.class.getResource("bilder/hatchery.jpg"));
		_spielicons[UserEingabenManager.BAULIBRARY] = new ImageIcon(SpielGui.class.getResource("bilder/library.jpg"));
		_spielicons[UserEingabenManager.BAUTORTURE] = new ImageIcon(SpielGui.class.getResource("bilder/torture.jpg"));

		ImageIcon imp = new ImageIcon(SpielGui.class.getResource("bilder/imp.jpg"));
		_bauTraining.setIcon( _spielicons[UserEingabenManager.BAUTRAINING] );
		_bauSchlafen.setIcon( _spielicons[UserEingabenManager.BAULAIR]);
		_bauEssen.setIcon( _spielicons[UserEingabenManager.BAUHATCHERY]);
		_bauLernen.setIcon(_spielicons[UserEingabenManager.BAULIBRARY]);
		_bauFolter.setIcon(_spielicons[UserEingabenManager.BAUTORTURE]);

		_zauberImp.setIcon(imp);
//		_zauberBlitz.setIcon(blitz);

//		_abrBut.addActionListener(new ActionListener() {
//
//			public void actionPerformed(final ActionEvent arg0) {
//				buttonAktion( UserEingabenManager.ABREISSEN );
//			}
//
//		});

		_bauTraining.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				buttonAktion( UserEingabenManager.BAUTRAINING );
			}
		});
		_bauSchlafen.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				buttonAktion( UserEingabenManager.BAULAIR );
			}
		});
		_bauEssen.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				buttonAktion( UserEingabenManager.BAUHATCHERY );
			}
		});
		_bauLernen.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				buttonAktion( UserEingabenManager.BAULIBRARY );
			}
		});
		_bauFolter.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				buttonAktion( UserEingabenManager.BAUTORTURE );
			}
		});

		_zauberImp.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				buttonAktion( UserEingabenManager.ZAUBERIMP );
			}
		});

		_mainpanel.add(_title, machConstraints(0, 0));
		_mainpanel.add(_statusAllgemein, machConstraints(0, 2));
		_mainpanel.add(_pane, machConstraints(0, 4));

		_mainpanel.add(_koordExpl, machConstraints(0, 8));
		_mainpanel.add(_koord, machConstraints(0, 9));
		_mainpanel.add(_buttonExpl, machConstraints(0, 11));
//		_mainpanel.add(_abrBut, machConstraints(0, 12));
		_mainpanel.add(_pane, machConstraints(0, 13));

		_mainpanel.add(_buttonExpl2, machConstraints(0, 14));
		_mainpanel.add(_bauSchlafen, machConstraints(0, 15));
		_mainpanel.add(_bauEssen, machConstraints(0, 16));
		_mainpanel.add(_bauTraining, machConstraints(0, 17));
		_mainpanel.add(_bauLernen, machConstraints(0, 18));
		_mainpanel.add(_bauFolter, machConstraints(0, 19));
		_mainpanel.add(_forschung_status, machConstraints(0, 20));
		_mainpanel.add(_buttonExpl3, machConstraints(0, 21));
		_mainpanel.add(_zauberImp, machConstraints(0, 22));
//		_mainpanel.add(_zauberBlitz, machConstraints(0, 23));
		_mainpanel.add(_logo, machConstraints(0, 24));
		_mainpanel.add(_infos, machConstraints(0, 25));

		_mainpanel.doLayout();
	}
	
	@Override
	public void initialize(ObserverCenter arg0) {
		
		System.out.println("initialize SpielGui");

		_space = arg0.getSpace();
		_usermanager =  (UserEingabenManager) _space.getProperty("uem");
		GUIInformierer.addListener(this);
		
		

		aktuallisiereStatus();
		_forschung_status.setText("Forschung: " + _usermanager.gibForschung() );

		_obsCenter = arg0;

		addMouseListener(new MouseAdapter()
		{
			
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(e.getButton() == 1)
				{
				//System.out.println("Button: " + e.getButton());
				//int i = e.getClickCount();
				IVector2 koord = getWorldCoordinates(e.getPoint());
				int x = koord.copy().getXAsInteger();
				int y = koord.copy().getYAsInteger();

				_koord.setText("X: " + x + " & " + " Y: " + y);
				
				String info = _usermanager.userAktion(x, y, _typ);
				addInfo( info );
				}
				else
				{
					_typ= 0;
					Component comp = _obsCenter.getSelectedPerspective().getView();
					comp.setCursor( null );
					
				}
		
				super.mouseClicked(e);
			}
			
			@Override
			public void mouseEntered( MouseEvent e )
			{
				if ( _spielicons[_typ] != null )
				{
					Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(
							_spielicons[_typ].getImage(), 
							new Point(5,5), 
							"Cursor");
					
					Component comp = _obsCenter.getSelectedPerspective().getView();
					comp.setCursor( cursor );
				}	
			}
			
			@Override
			public void mouseExited( MouseEvent e )
			{
				Component comp = _obsCenter.getSelectedPerspective().getView();
				comp.setCursor( null );
			}
			
		});
	}
	
	/**
	 * Sends a User-Information
	 * TODO: Change to MessageWindow / Textfield
	 * @param info
	 */
	void addInfo( String info )
	{
		if ( info.matches("[ ]*" ) ) 
			return;
		
		if ( !(_infos.getText().matches("[ ]*" ) ) )
		{
			String text = (_infos.getText()).substring( 6, _infos.getText().length()-6 );
			if ( text.length() > 100 )
			{
				text = text.substring(0, text.lastIndexOf("<br>"));
			}
			text = "<html>"+info + "<br>"+ text+"</html>";
			_infos.setText( text );
		}
		else
		{
			String text = "<html>"+info+"</html>";
			_infos.setText( text );
		}
		
		
//		System.out.println("Stacktrace 1: ");
//		for ( StackTraceElement e : Thread.currentThread().getStackTrace() )
//		{
//			System.out.println( e );
//		}
		
	}

	private GridBagConstraints machConstraints(int x, int y) {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.gridx = x;
		c.gridy = y;
		return c;
	}

	/**
	 * @param i
	 *            : 0 = Wandabbau, 1 = Goldabbau
	 * 
	 */
	private void buttonAktion( int modus ) 
	{
		_typ = modus;
	}

	private void aktuallisiereStatus( ) {
		String status = "<html>";
		status = status + "<u>Status:</u><br/> ";
		status = status + "Gold: " + _usermanager.gibGold()+"<br>";
		status = status + "Mana: " + _usermanager.gibMana() + "<br>";
		status = status + "Geclaimt: "+_usermanager.gibGeclaimt() +"<br>";
		status = status + "Imps: " + _usermanager.gibImps() + "<br>";
		status = status + "Goblins: " + _usermanager.gibGoblins() + "<br>";
		status = status + "Warlocks: " + _usermanager.gibWarlocks() + "<br>";
		status = status + "</html>";
		_statusAllgemein.setText( status );
		
		_forschung_status.setText("Forschung: " + _usermanager.gibForschung() );
	}
	
	public void aktualisierung()
	{
		_usermanager.aktuallisiere();
		aktuallisiereStatus();
	}


	@Override
	public String getIconPath() {
		return "agentkeeper/bilder/dklogo.png";
	}

	public Component getView() {
		return _mainpanel;
	}

	@Override
	public void refresh() {
		if (_obsCenter.getSelectedPerspective().getSelectedObject() != null) {

			_zielpos = ((IVector2) ((ISpaceObject) _obsCenter.getSelectedPerspective().getSelectedObject()).getProperty("position"));
			int x = _zielpos.copy().getXAsInteger();
			int y = _zielpos.copy().getYAsInteger();
			_koord.setText("X: " + x + " & " + " Y: " + y);

		}

	}

	public Object getProperty(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	public Set<?> getPropertyNames() {
		// TODO Auto-generated method stub
		return null;
	}


	public void setProperty(String arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	public boolean hasProperty(String arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public String getName()
	{
		return "DkGui";
	}

}
