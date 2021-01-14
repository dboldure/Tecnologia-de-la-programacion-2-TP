package simulator.model;

import java.util.List;

public class NoGravity extends NewtonUniversalGravitation implements GravityLaws {
	
	

	//BUILDERS-----------------------------------------------------------------------------------------------------
	
	
	//FUNCTIONS-----------------------------------------------------------------------------------------------------
	@Override
	public void apply(List<Body> bodies) {
		
	}
	
	public String toString()
	{
		return "No gravity, simply";
	}
	
	public NoGravity clone()
	{
		return new NoGravity();
	}

}
