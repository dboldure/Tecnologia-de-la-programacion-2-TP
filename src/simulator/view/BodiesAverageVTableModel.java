package simulator.view;

import java.util.ArrayList;
import java.util.List;

import simulator.control.Controller;
import simulator.misc.Vector;
import simulator.model.Body;

@SuppressWarnings("serial")
public class BodiesAverageVTableModel extends BodiesTableModel {
	
	private int counter= 0;
	private List<Double> averageVs;
	
	
	//BUILDERS--------------------------------------------------------------------------------------------
	
	public BodiesAverageVTableModel (Controller ctrl)
	{
		super(ctrl);
		this.COLUMN_NAMES = new String[]{
				"Id",
				"Average Velocity"
			};
		this.state_attributes = 2;
	}
	
	
	//FUNCTIONS-----------------------------------------------------------------------------------------------
	
	@Override
	public Object getValueAt(int row, int col) {
		switch(col)
		{
			case 0:
				return _bodies.get(row).getID();
			case 1:
				return averageVs.get(row);
				default:
					return null;
		}
	}
	
	/*
	private Vector calculateAverageSpeedVectorial(Vector v, double simTime, double dt)
	{
		return new Vector
	}
	
	private Double calculateAverageSpeedScalar(Vector v, double simTime, double dt)
	{
		return v.magnitude()
	}*/
	
	private Double calculateAverageSpeedScalar(Vector v, int index)
	{
		return v.magnitude()+averageVs.get(index)/counter;
		//return bodies.get(i).getP().distanceTo(new Vector(bodies.get(i).getP().dim()));
	}
	
	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String gLawsDesc) {
		counter = 0;
		this.averageVs = new ArrayList<Double>();
		for(Body b : bodies)
			averageVs.add(b.getV().magnitude());
		super.updateTable(bodies);
	}

	@Override
	public void onReset(List<Body> bodies, double time, double dt, String gLawsDesc) {
		onRegister(bodies, time, dt, gLawsDesc);
	}
	
	@Override
	public void onAdvance(List<Body> bodies, double time) {
	
		counter++;
		for(int i = 0; i < averageVs.size(); i++)
			averageVs.set(i, calculateAverageSpeedScalar(bodies.get(i).getV(), i));
			//averageDs.set(i, calculateAverageDistanceToCenterScalar(bodies.get(i).getP(), i));
		super.updateTable(bodies);
	}
	
	private Double calculateAverageDistanceToCenterScalar(Vector p, int index)
	{
		return p.distanceTo(new Vector(p.dim()));
	}
	
	public void onBodyDelete(List<Body> bodies, int i)
	{
		updateTable(bodies);
		
		averageVs.remove(i);
	}
		

}
