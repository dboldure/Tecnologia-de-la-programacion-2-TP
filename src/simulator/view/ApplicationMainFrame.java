package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import simulator.control.Controller;

public class ApplicationMainFrame extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ControlPanel controlPanel;
	private BodiesTable bodiesTable;
	private BodiesTable bodiesTable2;
	private JComponent viewer;
	private StatusBar statusBar;
	
	
	public ApplicationMainFrame(Controller c)
	{
		super("Physics Simulator");	
		controlPanel = new ControlPanel(c);
		bodiesTable = new BodiesTable(c, new BodiesTableModel(c));
		bodiesTable2 = new BodiesTable(c, new BodiesAverageVTableModel(c));
		viewer = new Viewer(c);
		
		statusBar = new StatusBar(c);
		initComponents();
	}
	
	
	
	
	public void initComponents()
	{
		setMaximumSize(new Dimension(1020, 1000));
        setMinimumSize(new Dimension(1020, 1000));
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setPreferredSize(new Dimension(1020, 1000));
		this.addWindowListener((WindowListener) controlPanel.exitActionListener());
		getContentPane().setLayout(new BorderLayout());
        this.setVisible(true);
        
        //Control Panel --------------------
        getContentPane().add(controlPanel, BorderLayout.PAGE_START);
        
        //Center 
        	JPanel centerPanel = new JPanel();
        	centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
	        //Bodies Table ---------------------
        	centerPanel.add(bodiesTable);
        	//Bodies Table 2 -------------------
        	centerPanel.add(bodiesTable2);
        	
	        //Bodies Viewer --------------------
        	centerPanel.add(viewer);
        	
	        getContentPane().add(centerPanel, BorderLayout.CENTER);
        //Status Bar -----------------------
        getContentPane().add(statusBar, BorderLayout.PAGE_END);
        
        
	}

}
