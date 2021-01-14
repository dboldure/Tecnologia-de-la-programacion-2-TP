package simulator.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

public class BodiesTableModel extends AbstractTableModel implements SimulatorObserver{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int state_attributes = 5;
	protected String[] COLUMN_NAMES = {
			"Id",
			"Mass",
			"Position",
			"Velocity",
			"Acceleration"
		};
	
	
	protected List<Body> _bodies;
	
	//BUILDERS--------------------------------------------------------------------------------------------
	
	public BodiesTableModel (Controller ctrl)
	{
		_bodies = new ArrayList<>();
		ctrl.addObserver(this);
	}
	
	//FUNCTIONS-----------------------------------------------------------------------------------------------
	

	@Override
	public int getColumnCount() {
		return state_attributes;
	}

	@Override
	public int getRowCount() {
		return _bodies.size();
	}
	
	@Override 
	public String getColumnName(int column) 
	{ 
		if(column>=0 && column<this.COLUMN_NAMES.length)
			return this.COLUMN_NAMES[column];
		else
			return "";
	}


	@Override
	public Object getValueAt(int row, int col) {
		switch(col)
		{
			case 0:
				return _bodies.get(row).getID();
			case 1:
				return _bodies.get(row).getMass();
			case 2:
				return _bodies.get(row).getP();
			case 3:
				return _bodies.get(row).getV();
			case 4:
				return _bodies.get(row).getA();
				default:
					return null;
		}
	}
	
	protected void updateTable(List<Body> bodies)
	{
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run() {
				_bodies = bodies;
				fireTableStructureChanged();}
	
		});
		
	}

	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String gLawsDesc) {
		updateTable(bodies);
	}

	@Override
	public void onReset(List<Body> bodies, double time, double dt, String gLawsDesc) {
		updateTable(bodies);
	}

	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {
		updateTable(bodies);
	}
	
	public void onBodyDelete(List<Body> bodies, int i)
	{
		updateTable(bodies);
	}

	@Override
	public void onAdvance(List<Body> bodies, double time) {
		updateTable(bodies);
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
	
	//AUX--------------------------------------------------------------------------------------
	
	

}
