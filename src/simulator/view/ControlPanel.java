package simulator.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.json.JSONObject;

import simulator.control.Controller;
import simulator.control.IllegalArgumentException;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

public class ControlPanel extends JPanel implements SimulatorObserver {

	/**
	 * 
	 */
	private volatile Thread _thread;
	private static final long serialVersionUID = 1L;
	private final String LOAD_BUTTON_IMAGE_DIR = "resources/icons/open.png";
	private final String LAW_BUTTON_IMAGE_DIR = "resources/icons/physics.png";
	private final String PLAY_BUTTON_IMAGE_DIR = "resources/icons/run.png";
	private final String STOP_BUTTON_IMAGE_DIR = "resources/icons/stop.png";
	private final String RESTART_BUTTON_IMAGE_DIR = "resources/icons/restart.png";
	private final String EXIT_BUTTON_IMAGE_DIR = "resources/icons/exit.png";
	//private final String DELETE_BUTTON_IMAGE_DIR = "resources/icons/exit.png";
	//private final String EXIT_DIALOG_IMAGE_DIR = "resources/icons/java.png";
	
	private Controller _ctrl; 
	private int actualSteps;
	
	private JButton loadButton, lawButton, playButton, stopButton, exitButton, restartButton;
	private JMenu deleteMenu = new JMenu();
	private JTextField deltaTimeTextField;
	private JSpinner stepsSpinner,delaySpinner;
	
	//private List<String> idBodies;
	
	
	ControlPanel(Controller ctrl) {
		_ctrl = ctrl;
		//_stopped = true; 
		actualSteps = 0;
		initGUI();
		_ctrl.addObserver(this); 
		
	}
	
	private void initGUI() {

		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		//TOOLS BAR -----------------------------------------
		
		JToolBar tools = new JToolBar();
		tools.setLayout(new BoxLayout(tools, BoxLayout.X_AXIS));
		tools.setOpaque(false);
		
			//LOAD ------------------------------------------
			
			tools.add(createLoadPanel());
			
				tools.addSeparator(new Dimension(3, 40));
				tools.add(createSeparator());
		
			//LAWS ------------------------------------------
			
			tools.add(createLawPanel());
			
				tools.addSeparator(new Dimension(3, 40));
				tools.add(createSeparator());
				
			//DELETE ------------------------------------------
			
			tools.add(createDeletePanel());
			
				tools.addSeparator(new Dimension(3, 40));
				tools.add(createSeparator());	
				
			
			//DATA ------------------------------------------
			
			tools.add(createDataPanel());
	
				tools.add(Box.createHorizontalGlue());
								
					tools.addSeparator(new Dimension(3, 40));
					tools.add(createSeparator());
			
			//EXIT ------------------------------------------
				
			tools.add(createExitPanel(), Box.RIGHT_ALIGNMENT);
			
		this.add(tools);
		
		this.setVisible(true);
	}
	
	private void initButtonPanel(JPanel panel, JButton button, String dir)
	{
	    panel.setMinimumSize(new Dimension(25, 25));
	    panel.setPreferredSize(new Dimension(44, 44));
	    BoxLayout box=new BoxLayout(panel, BoxLayout.X_AXIS);
	    panel.setLayout(box);
	    initButton(button, dir);
	    panel.add(button);
	}
	
	private void initButton(JButton button, String dir)
	{
		button.setIcon(new ImageIcon(dir)); 
	    button.setMinimumSize(new Dimension(40, 40));
	    button.setPreferredSize(new Dimension(40, 40));
	}
	
	private void initDataPanel(JPanel dataPanel)
	{

		playButton = new JButton();
		stopButton = new JButton();
		restartButton = new JButton();
		
		JLabel delayLabel = new JLabel();
		delaySpinner = new JSpinner();
		JLabel stepsLabel = new JLabel();
		stepsSpinner = new JSpinner();
		JLabel deltaTimeLabel = new JLabel();
		deltaTimeTextField = new JTextField();
	
        dataPanel.setMaximumSize(new Dimension(600, 44));
        dataPanel.setMinimumSize(new Dimension(600, 44));
        
        dataPanel.setPreferredSize(new Dimension(800, 44));
        dataPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 2));

        //PLAY Button ----------------------------------------------------------------
        initButton(playButton, PLAY_BUTTON_IMAGE_DIR);
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                playButtonActionPerformed(evt);
            }
            
            private void correctRun(Double n)
            {
            	_ctrl.setDeltaTime(n);
            	deactivateControlButtons(getVariableButtons());
				
				_thread = new Thread(new Runnable(){
					public void run(){   
						run_sim((int)stepsSpinner.getValue()-actualSteps, (int)delaySpinner.getValue());
						activateControlButtons(getVariableButtons());
						_thread = null;
					}
				});	
				_thread.start();
            }
            
			private void playButtonActionPerformed(ActionEvent evt) {
				Double n = 1.0;
				if(actualSteps <= (Integer) stepsSpinner.getValue()-1)
				{
					
					try
					{
						n = Double.parseDouble(deltaTimeTextField.getText());
						if(n < 0)
							throw new NumberFormatException();
						correctRun(n);
						
					}
					catch(NumberFormatException e)
					{
						JOptionPane.showMessageDialog(null, "Not valid value!", "Warning", JOptionPane.WARNING_MESSAGE);
					}
					
				}
				else
					JOptionPane.showMessageDialog(null, "You can not press play now!", "Warning", JOptionPane.WARNING_MESSAGE);
	
				
			}
        });
        dataPanel.add(playButton);
        

        //STOP Button -------------------------------------------------------------------
        initButton(stopButton, STOP_BUTTON_IMAGE_DIR);
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
            
			public void stopButtonActionPerformed(ActionEvent evt) {
				if(_thread != null)
				{
					_thread.interrupt();
					activateControlButtons(getVariableButtons());
					
				}
			}
        });
        dataPanel.add(stopButton);
        
        //RESTART Button -----------------------------------------------------------------
        initButton(restartButton, RESTART_BUTTON_IMAGE_DIR);
        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                restartButtonActionPerformed(evt);
            }
            
			private void restartButtonActionPerformed(ActionEvent evt) {
				_ctrl.reset();
				activateControlButtons(getVariableButtons());
				actualSteps = 0;
			}
        });
        dataPanel.add(restartButton);
        
        

        //DATA Viewers -------------------------------------------------------------------
        
        //Delay
        delayLabel.setText("Delay:");
        dataPanel.add(delayLabel);

        delaySpinner.setModel(new SpinnerNumberModel(1, 0, 1000, 1));
        delaySpinner.setPreferredSize(new Dimension(80, 35));
        dataPanel.add(delaySpinner);
        
        stepsLabel.setText("Steps:");
        dataPanel.add(stepsLabel);

        stepsSpinner.setModel(new SpinnerNumberModel(2500, 1000, null, 10));
        stepsSpinner.setPreferredSize(new Dimension(80, 35));
        dataPanel.add(stepsSpinner);

        deltaTimeLabel.setText("Delta-Time:");
        dataPanel.add(deltaTimeLabel);


        deltaTimeTextField.setHorizontalAlignment(JTextField.RIGHT);
        deltaTimeTextField.setText("1");
        deltaTimeTextField.setPreferredSize(new Dimension(80, 35));
        deltaTimeTextField.setRequestFocusEnabled(true);
        deltaTimeTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                actionPerformed(evt);
            }
        });
        dataPanel.add(deltaTimeTextField);
    
	}
	
	@SuppressWarnings("static-access")
	private void run_sim(int n,long d)
	{ 
		while(n>0 && !_thread.isInterrupted())	//TODO check method condition
		{
			try
			{
				_ctrl.run(1);
				actualSteps += 1;
				_thread.sleep(d);
				n--;
			}
			catch(InterruptedException e)
			{
				_thread.interrupt();
				return;
			}
			catch(Exception e)
			{
				// show the error in a dialog box
				JOptionPane.showMessageDialog(null, "Exception in sim execution", "Excetion", JOptionPane.ERROR_MESSAGE, null);
				// enable all buttons
				
				activateControlButtons(getVariableButtons());
				return; 
			}
		}
		if(actualSteps == (Integer) stepsSpinner.getValue()-1){
			stopButton.getActionListeners()[0].actionPerformed(null);
		}
		}
	
	
		

	
	//AUX/No Used -------------------------------------------------------------------------------
	
	private JPanel createDeletePanel()
	{
		JPanel deletePanel = new JPanel();
		JMenuBar deleteBar = new JMenuBar();
		
		deleteBar.setSize(new Dimension(70, 40));
		deleteMenu.setSize(deleteBar.getSize());
		deleteMenu.setText("DELETE");
		
		deleteBar.add(deleteMenu);
		deletePanel.add(deleteBar);
		return deletePanel;
	}
	
	private void deleteBody(String id)
	{
		this._ctrl.deleteBody(id);
	}
	
	private JPanel createLoadPanel()
	{
		JPanel loadPanel = new JPanel();
		loadButton = new JButton();
		initButtonPanel(loadPanel, loadButton, LOAD_BUTTON_IMAGE_DIR);
		loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                loadButtonActionPerformed(evt);
            }

			private void loadButtonActionPerformed(ActionEvent evt) {
				JFileChooser fileC = new JFileChooser();
				fileC.showOpenDialog(null);
				File f = fileC.getSelectedFile();
				if(f != null)
				{
					
					try {
						if(!f.getName().endsWith(".txt"))
							throw new IllegalArgumentException();
						
						_ctrl.loadBodies(new FileInputStream(f));
						_ctrl.reset();
					} 
					catch (FileNotFoundException e) {
						JOptionPane.showMessageDialog(null,
							    "File: "+f.getName()+" can not be loaded!",
							    "Law loader",
							    JOptionPane.ERROR_MESSAGE);
					}
					catch(IllegalArgumentException e)
					{
						JOptionPane.showMessageDialog(null,
							    "File: "+f.getName()+" can not be loaded!",
							    "Law loader",
							    JOptionPane.ERROR_MESSAGE);
					}
				}
					
			}
        });
		return loadPanel;
	}
	
	private JPanel createLawPanel()
	{
		JPanel lawPanel = new JPanel();
		lawButton = new JButton();
		initButtonPanel(lawPanel, lawButton, LAW_BUTTON_IMAGE_DIR);
		lawButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                lawButtonActionPerformed(evt);
            }

			private void lawButtonActionPerformed(ActionEvent evt) {
				
				List<JSONObject> list = _ctrl.getGravityLawsFactory().getInfo();
				
				String[] laws = new String[list.size()];
				
				for(int i = 0; i < list.size(); i++)
					laws[i] = list.get(i).getString("desc");	
				
				JSONObject o = new JSONObject();
				String s = (String) JOptionPane.showInputDialog(
	                    null,
	                    "Select gravity law to be used.",
	                    "Gravity Laws Selector",
	                    JOptionPane.PLAIN_MESSAGE,
	                    null,
	                    laws,
	                    null);
				int counter = 0;
				while(counter < laws.length && s != list.get(counter).getString("desc"))
					counter++;
				if(counter < laws.length)
					o = list.get(counter);
				
				_ctrl.setGravityLaws(o);
			}
        });
		return lawPanel;
	}
	
	private JPanel createDataPanel()
	{
		JPanel dataPanel = new JPanel();
		initDataPanel(dataPanel);
		return dataPanel;
	}
	
	private JPanel createExitPanel()
	{
		JPanel exitPanel = new JPanel();
		
		exitButton = new JButton();
		initButtonPanel(exitPanel, exitButton, EXIT_BUTTON_IMAGE_DIR);
		class ExitActionListener implements ActionListener, WindowListener {
			
			
            public void actionPerformed(ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
            
			private void exitButtonActionPerformed(ActionEvent evt) {
				String[] en_options = {"Yes", "No"};	//For english language
				@SuppressWarnings("unused")
				String[] es_options = {"Si", "No"};		//For spanish language

				if(JOptionPane.showOptionDialog(null,
					    "Are sure you want to quit?",
					    "Quit",
					    JOptionPane.YES_NO_OPTION,
					    JOptionPane.QUESTION_MESSAGE,
					    null,  //new ImageIcon(EXIT_DIALOG_IMAGE_DIR)   
					    en_options,
					    en_options[0]) == 0)
					System.exit(0);
			}

			@Override
			public void windowOpened(WindowEvent e) {
				
			}

			@Override
			public void windowClosing(WindowEvent e) {
				exitButtonActionPerformed(null);
				
			}

			@Override
			public void windowClosed(WindowEvent e) {
				
			}

			@Override
			public void windowIconified(WindowEvent e) {
				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				
			}

			@Override
			public void windowActivated(WindowEvent e) {
				
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				
			}
        }
		exitButton.addActionListener(new ExitActionListener());
		return exitPanel;
	}
	
	private JSeparator createSeparator()
	{
		JSeparator sep = new JSeparator();
		sep.setPreferredSize(new Dimension(4, 40));
		sep.setMaximumSize(new Dimension(4, 40));
		sep.setForeground(new Color (208, 208, 208));
		sep.setOrientation(SwingConstants.VERTICAL);
		return sep;
	}
	
	public EventListener exitActionListener()
	{
		return this.exitButton.getActionListeners()[0];
	}
	
	private List<JButton> getVariableButtons()
	{
		List<JButton> buttons = new ArrayList<JButton>();
		buttons.add(loadButton);
		buttons.add(lawButton);
		buttons.add(playButton);
		buttons.add(exitButton);
		return buttons;
	}
	
	private void deactivateControlButtons(List<JButton> buttons)
	{
		for(JButton b : buttons)
			b.setEnabled(false);
	}
	
	private void activateControlButtons(List<JButton> buttons)
	{
		for(JButton b : buttons)
			b.setEnabled(true);
	}
	
	private void fillBodiesId(List<Body> bodies)
	{
		//deleteMenu = new JMenu("DELETE");
		JMenuItem provItem;
	
		for(Body b : bodies)
		{
			provItem = new JMenuItem(b.getID());
			provItem.addActionListener(new ActionListener()
					{

						@Override
						public void actionPerformed(ActionEvent e) {
							deleteBody(((JMenuItem) e.getSource()).getText());
						}
				
					});
			deleteMenu.add(provItem);
		}
	}
	
	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String gLawsDesc) {
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run() {
				deltaTimeTextField.setText(String.valueOf(dt));
				fillBodiesId(bodies);
			}
	
		});
	}

	@Override
	public void onReset(List<Body> bodies, double time, double dt, String gLawsDesc) {
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run() {
				deleteMenu = new JMenu("DELETE");
				fillBodiesId(bodies);
			}
	
		});
	}

	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run() {
				fillBodiesId(bodies);
			}
	
		});
	}
	
	public void onBodyDelete(List<Body> bodies, int i)
	{
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run() {
				deleteMenu = new JMenu("DELETE");
				fillBodiesId(bodies);
			}
	
		});
	}

	@Override
	public void onAdvance(List<Body> bodies, double time) {
		
	}

	@Override
	public void onDeltaTimeChanged(double dt) {
		
	}

	@Override
	public void onGravityLawChanged(String gLawsDesc) {
		
	}
	
	public void onBodyCollision(Body a, Body b, double time)
	{
		
	}

}
