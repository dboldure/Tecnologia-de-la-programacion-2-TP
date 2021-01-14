package simulator.model;

import java.util.ArrayList;
import java.util.List;

import simulator.misc.Vector;

public class PhysicSimulator{
	
	private Double timePerStep;
	private GravityLaws laws;
	
	

	private List<Body> bodies;
	private Double simTime;
	
	private List<Body> oldBodies;
	private GravityLaws oldLaws;
	//private Boolean saveBodies = false;
	
	

	private List<SimulatorObserver> observers = new ArrayList<SimulatorObserver>();
	
	//BUILDING-------------------------------------------------
	
	public PhysicSimulator(Double timePerStep, GravityLaws laws)
	{
		
		//saveBodies = false;
		if(timePerStep<0)
			throw new IllegalArgumentException("simTime must be over 0.0");
		if(laws == null)
			throw new IllegalArgumentException("there are any law at the sim");
		this.timePerStep = timePerStep;
		this.setGravityLaws(laws);
		this.bodies = new ArrayList<Body>();
		simTime = 0.0;
	}
	
	//FUNCTIONS-------------------------------------------------
	
	public void addObserver(SimulatorObserver o)
	{
		//this.observers.add(o);
		if(!this.observers.contains(o))
		{
			this.observers.add(o);
			o.onRegister(bodies, this.simTime, this.timePerStep, this.laws.toString());
		}
			
	}
	
	public void reset()
	{
		//this.bodies = new ArrayList<Body>();
		if(oldLaws != null)
			laws = oldLaws.clone();
		if(oldBodies != null || !oldBodies.isEmpty())
			this.bodies = cloneBodies(oldBodies);
		this.simTime = 0.0;
		for(SimulatorObserver o : this.observers)
			o.onReset(bodies, simTime, timePerStep, laws.toString()); 
		//((SimulatorObserver) this.observers.iterator()).onReset(bodies, simTime, timePerStep, laws.toString());
	}
	
	public void setDeltaTime(double dt) throws IllegalArgumentException
	{
		if(dt < 0)
			throw new IllegalArgumentException();
		this.timePerStep = dt;
		for(SimulatorObserver o : this.observers)
			o.onDeltaTimeChanged(timePerStep); 
	}
	
	public void setGravityLaws(GravityLaws gravityLaws)	throws IllegalArgumentException
	{
		if(gravityLaws == null)
			throw new IllegalArgumentException();
		this.laws = gravityLaws;
		for(SimulatorObserver o : this.observers)
			o.onGravityLawChanged(laws.toString()); 
	}
	
	public void advance()
	{
		laws.apply(bodies);
		int i = 0;
		Body b;
		while(i < bodies.size())
		{
			b = bodies.get(i);
			b.move(timePerStep);
			for(Body a : bodies)
				if(b!=a&&a.getP().distanceTo(b.getP())<1)
				{
					for(SimulatorObserver o : observers)
						o.onBodyCollision(a, b, simTime);
					if(b.getMass() > a.getMass())
					{
						b.setMass(b.getMass()+a.getMass());
						deleteBody(a);
					}
					else
					{
						a.setMass(a.getMass()+b.getMass());
						deleteBody(b);
					}	
					
				}
					
			i++;		
		}
		simTime += timePerStep;
		for(SimulatorObserver o : this.observers)
			o.onAdvance(bodies, simTime); 
	}
	
	private void deleteBody(Body b)
	{
		int pos = bodies.lastIndexOf(b);
		for(int i = bodies.size()-1; i < bodies.lastIndexOf(b); i--)
			bodies.set(i-1, bodies.get(i));
		bodies.remove(b);
		for(SimulatorObserver o : observers)
			o.onBodyDelete(bodies, pos);
		
	}
	
	public void addBody(Body b)	throws IllegalArgumentException
	{
		if(bodies.contains(b))
			throw new IllegalArgumentException();
		bodies.add(b);
	}
	
	public String toString()
	{	
		StringBuilder s = new StringBuilder();
		s.append("{ \"time\": ").append(this.simTime).append(", \"bodies\": [ ");
		for(Body b: this.bodies)
		{
			s.append(b.toString());
			if(bodies.indexOf(b) != bodies.size()-1)
				s.append(", ");
		}
		s.append("] }");
			
		return s.toString();
	}
	
	public String[] bodiesTraceToString()
	{
		String[] s = new String[bodies.size()];
		for(int i = 0; i < bodies.size(); i++)
			s[i] = bodies.get(i).getP().toString();
			//s[i] = String.valueOf(bodies.get(i).getP().distanceTo(new Vector(bodies.get(i).getP().dim())));	//distance to center
		return s;
	}
	
	public void startTrace(String[] s)
	{
		for(int i = 0; i < bodies.size(); i++)
			s[i] = bodies.get(i).getID()+": ";
	}
	
	public void load()
	{
		simTime=0.0;
		if(bodies != null || !bodies.isEmpty())
			oldBodies = cloneBodies(this.bodies);
		if(laws != null)
			oldLaws = laws.clone();
		for(SimulatorObserver o : observers)
			o.onRegister(bodies, this.simTime, this.timePerStep, this.laws.toString());
	}
	
	//AUX-----------------------------------------------------------------------
	
	private List<Body> cloneBodies(List<Body> bodies)
	{
		List<Body> newList = new ArrayList<Body>();
		for(Body b : bodies)
			newList.add(b.clone());
		return newList;
	}
	
	public GravityLaws getLaws() {
		return laws;
	}

	public void setLaws(GravityLaws laws) {
		this.laws = laws;
	}

	public void deleteBody(String id) {
		int i = 0;
		while(i < bodies.size() && !bodies.get(i).getID().equalsIgnoreCase(id))
		{
			i++;
		}
		if(i < bodies.size())
		{
			bodies.remove(i);
			for(SimulatorObserver o : observers)
				o.onBodyDelete(bodies, i);
		}
			
	}
	
	public String[] loadTrace()
	{
		return new String[bodies.size()];
	}
	


}
