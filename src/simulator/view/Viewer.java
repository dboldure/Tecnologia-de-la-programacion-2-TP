package simulator.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import simulator.control.Controller;
import simulator.misc.Vector;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

public class Viewer extends JComponent implements SimulatorObserver {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int _centerX; 
	private int _centerY;
	private double _scale; 
	private List<Body> _bodies; 
	private boolean _showHelp;
	private String helpText = "h: toggle help, +:zoom-in, -:zoom-out, =fit";
	private String scaleText = "Scaling ratio: ";
	private final int BODIES_DIAMETER = 12;

	
	//BUILDERS--------------------------------------------------------------------------
	
	public Viewer(Controller ctrl) 
	{ 
		initGUI(); 
		ctrl.addObserver(this); 
	}

	
	//FUNCTIONS-------------------------------------------------------------------------
	
	private void initGUI()
	{
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder( BorderFactory.createLineBorder(Color.black, 2), "Viewer", TitledBorder.LEFT, TitledBorder.TOP));
		this.setPreferredSize(new Dimension(1020, 621));
		this.setSize(new Dimension(1020, 621));
		_bodies = new ArrayList<>();
		_scale = 1.0;
		_showHelp = true;
		addKeyListener(new KeyListener() { 
			@Override
			public void keyPressed(KeyEvent e) { 
				switch (e.getKeyChar()) { 
				case '-': 
					_scale = _scale * 1.1; 
					break; 
				case '+':
					_scale = Math.max(1000.0, _scale / 1.1); 
				break; 
				case '=':
					autoScale(); 
				break; 
				case 'h':
					_showHelp = !_showHelp; 
					break; 
				default:
	
				}
				repaint();

			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				
			}
		});
		
		addMouseListener(new MouseListener() 
		{ 
			// ... 
			@Override 
			public void mouseEntered(MouseEvent e) 
			{ 
				requestFocus(); 
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				
			} 
		});

	}
	
	@Override 
	protected void paintComponent(Graphics g) 
	{ 
		super.paintComponent(g); 
		Graphics2D gr = (Graphics2D) g; 
		gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
		gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		// use ’gr’ to draw not ’g’
		// calculate the center 
		_centerX = getWidth() / 2; 
		_centerY = getHeight() / 2;
		// draw a cross at center 
		gr.setColor(new Color(255, 0, 0));
		gr.drawLine(_centerX-6, _centerY, _centerX+6, _centerY);
		gr.drawLine(_centerX, _centerY-6, _centerX, _centerY+6);
		// draw bodies 
		
		for(Body b : this._bodies)
		{
			gr.setColor(new Color(0, 0, 255));
			gr.fillOval( _centerX + (int) (b.getP().coordinate(0)/_scale)-BODIES_DIAMETER/2,  _centerY - (int) (b.getP().coordinate(1)/_scale)-BODIES_DIAMETER/2 , BODIES_DIAMETER, BODIES_DIAMETER);
			gr.setColor(new Color(0, 0, 0));
			gr.drawString(b.getID(), _centerX + (int) ((b.getP().coordinate(0)/_scale)-BODIES_DIAMETER/2), _centerY - (int) ((b.getP().coordinate(1)/_scale)+BODIES_DIAMETER));
		}
			
		// draw help if _showHelp is true
		gr.setColor(new Color(255, 0, 0));
		Font f = new Font ("", Font.BOLD , 12);
		this.setFont(f);
		if(this._showHelp)
		{
			gr.drawString(helpText, 15, 25);
			gr.drawString(scaleText+this._scale, 15, 40);
		}
		
		
		
	}
	
	private void autoScale() 
	{ 
		double max = 1.0;
		for (Body b : _bodies) 
		{ 
			Vector p = b.getP(); 
			for (int i = 0; i < p.dim(); i++) 
				max = Math.max(max, Math.abs(b.getP().coordinate(i))); 
		}
		double size = Math.max(1.0, Math.min((double) getWidth(), (double) getHeight()));
		_scale = max > size ? 4.0 * max / size : 1.0;
	}
	
	//AUX---------------------------------------------------------------------

	private void updateView(List<Body> bodies)
	{
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run() {
				_bodies = bodies;
				autoScale();
				repaint();}
	
		});
		
	}

	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String gLawsDesc) {
		updateView(bodies);
	}

	@Override
	public void onReset(List<Body> bodies, double time, double dt, String gLawsDesc) {
		updateView(bodies);
	}

	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {
		updateView(bodies);
	}
	
	public void onBodyDelete(List<Body> bodies, int i)
	{
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run() {
				updateView(bodies);
			}
	
		});
	}

	@Override
	public void onAdvance(List<Body> bodies, double time) {
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run() {
				repaint();}
	
		});

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
