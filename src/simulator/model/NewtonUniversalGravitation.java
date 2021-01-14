package simulator.model;

import java.util.List;

import simulator.control.DivByZeroException;
import simulator.misc.Vector;

public class NewtonUniversalGravitation implements GravityLaws{
	
	private final Double NEWTON_GRAVITY_CONSTANT = 6.67E-11;
	
	
	
	//BUILDERS------------------------------------------------------------------------------------------------------
	
	
	//FUNCTIONS-----------------------------------------------------------------------------------------------------
	
	public void apply(List<Body> bodies) {
		try
		{
			Vector f;
			
			for(Body a : bodies)
			{
				f = new Vector(a.getA().dim());
				
				if(a.getMass()==0)
				{
					a.setA(new Vector(a.getA().dim()));
					a.setV(new Vector(a.getV().dim()));
				}
				else
				{
					for(Body b : bodies)
					{
						f = f.plus(calculateGForce(a, b));
					}
					Vector acceleration = f.scale(1/a.getMass());
						
					a.setA(acceleration);
				}
					
			}
		}
		catch(ArithmeticException e)
		{
			
		}
	}
	
	private Vector calculateGForce(Body a, Body b)
	{
		try
		{
			Double s1 = NEWTON_GRAVITY_CONSTANT*a.getMass()*b.getMass(), s2 = Math.pow(a.getP().distanceTo(b.getP()), 2);
			Vector dir = (b.getP().minus(a.getP())).direction();
			if(a.getP().distanceTo(b.getP())!=0)
				return dir.scale(s1/s2);
		
			throw new DivByZeroException();
		}
		catch(ArithmeticException e)
		{
			return new Vector(a.getA().dim());
		}
	}
	
	public String toString()
	{
		return "This is the basic Newtons´s laws";
	}
	
	//AUX----------------------------------------------------------------------------------------------------
	
	public NewtonUniversalGravitation clone()
	{
		return new NewtonUniversalGravitation();
	}
	
}
