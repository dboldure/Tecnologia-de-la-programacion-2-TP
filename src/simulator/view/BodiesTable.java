package simulator.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

import simulator.control.Controller;

public class BodiesTable extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	//BUILDERS-----------------------------------------------------------------------------
	
	public BodiesTable(Controller ctrl, BodiesTableModel m) 
	{
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder( BorderFactory.createLineBorder(Color.black, 2), "Bodies", TitledBorder.LEFT, TitledBorder.TOP));
		this.setPreferredSize(new Dimension(1020, 310));
		
		

		JTable bodiesTable = new JTable(m);
		bodiesTable.getTableHeader().setBorder(null);
		this.add(new JScrollPane(bodiesTable));
	}
	
	//FUNCTIONS-----------------------------------------------------------------------------
	
	public void printGUI()
	{
		
	}

}
