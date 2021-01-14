package simulator.model;

import java.util.List;

public interface GravityLaws {
	
	public abstract void apply(List<Body> bodies);
	public GravityLaws clone();

}
