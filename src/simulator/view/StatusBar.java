package simulator.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import simulator.control.Controller;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

public class StatusBar extends JPanel implements SimulatorObserver {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String PRESET_LAW = "n/a";
	
	private final String TIME_TEXT = "Time: ";
	private final String BODIES_TEXT = "Bodies: ";
	private final String LAWS_TEXT = "Laws: ";
	
	private JLabel _currTime; // for current time 
	private JLabel _currLaws; // for gravity laws 
	private JLabel _numOfBodies; // for number of bodies
	private JLabel collisionReport;
	
	private double reportCounterTime = 0;
	private double dtCounter;//, timeCounter;

	
	//BUILDERS------------------------------------------------------------------------------
	
	StatusBar(Controller ctrl) 
	{ 
		initGUI();
		ctrl.addObserver(this); 
	}
	
	//FUNCTIONS-----------------------------------------------------------------------------
	
	private void initGUI() 
	{ 
		this.setLayout( new FlowLayout( FlowLayout.LEFT )); 
		this.setBorder( BorderFactory.createBevelBorder( 1 ));
		

		
		//TIME ----------------------------------------------
			_currTime = new JLabel(TIME_TEXT+"0.0");
			_currTime.setPreferredSize(new Dimension(120, 25));
			this.add(_currTime);
			this.add(createSeparator());
		
		//BODIES --------------------------------------------
		
			_numOfBodies = new JLabel(BODIES_TEXT+"0.0");
			_numOfBodies.setPreferredSize(new Dimension(120, 25));
			this.add(_numOfBodies);
			this.add(createSeparator());
			
		//LAWS -----------------------------------------------		
			_currLaws = new JLabel(LAWS_TEXT+PRESET_LAW);
			_currLaws.setPreferredSize(new Dimension(400, 25));
			this.add(_currLaws);
		
	}
	
	
	//AUX ---------------------------------------------------------------------------
	
	private JSeparator createSeparator()
	{
		JSeparator sep = new JSeparator();
		sep.setPreferredSize(new Dimension(2, 25));
		sep.setForeground(new Color (208, 208, 208));
		sep.setOrientation(SwingConstants.VERTICAL);
		return sep;
	}
	
	private void updateBodies(List<Body> bodies)
	{
		_numOfBodies.setText(BODIES_TEXT+bodies.size());
	}


	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String gLawsDesc) {
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run() {
				_currLaws.setText(LAWS_TEXT+gLawsDesc);
				updateBodies(bodies);
				dtCounter = dt;
				//timeCounter = time;
			}
	
		});
		
	}

	@Override
	public void onReset(List<Body> bodies, double time, double dt, String gLawsDesc) {
		
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run() {
				_currLaws.setText(LAWS_TEXT+gLawsDesc);
				_currTime.setText(TIME_TEXT+String.valueOf(time));
				updateBodies(bodies);
				dtCounter = dt;
				//timeCounter = time;
			}
	
		});
	}

	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run() {
				updateBodies(bodies);
			}
	
		});
	
	}
	
	public void onBodyDelete(List<Body> bodies, int i)
	{
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run() {
				updateBodies(bodies);
			}
	
		});
	}

	@Override
	public void onAdvance(List<Body> bodies, double time) {
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run() {
			//timeCounter = time;
			_currTime.setText(TIME_TEXT+String.valueOf(time));
			if(collisionReport != null && time/dtCounter-reportCounterTime == 1000)
				collisionReport.setVisible(false);
			}
	
		});
	
	}

	@Override
	public void onDeltaTimeChanged(double dt) {
		dtCounter = dt;
	}

	@Override
	public void onGravityLawChanged(String gLawsDesc) {
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run() {
				_currLaws.setText(LAWS_TEXT+gLawsDesc);
			}
	
		});
	

	}
	
	public void onBodyCollision(Body a, Body b, double time)
	{
		//collisionReport.setVisible(true);
		reportCounterTime = time;
		collisionReport = new JLabel("Collision between "+a.getID()+", "+b.getID());
		collisionReport.setPreferredSize(new Dimension(300, 25));
		this.add(collisionReport);
	}

}
