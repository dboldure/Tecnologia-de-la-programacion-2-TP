package simulator.model;

import java.util.List;

public class FallingToCenterGravity extends NewtonUniversalGravitation implements GravityLaws{
	
private final static Double FALLING_ACELERATION = -9.81;
	
	
	//BUILDERS----------------------------------------------------------------------------------------------------
	
	
	//FUNCTIONS-----------------------------------------------------------------------------------------------------
	

	@Override
	public void apply(List<Body> bodies) {
		
		for(Body a : bodies)
		{
			a.setA(a.getP().direction().scale(FALLING_ACELERATION));
		}
		
	}
	
	public String toString()
	{
		return "This produce a acceleration to all bodies to the center";
	}
	
	public FallingToCenterGravity clone()
	{
		return new FallingToCenterGravity();
	}

}
