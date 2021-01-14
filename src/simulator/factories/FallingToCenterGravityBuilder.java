package simulator.factories;

import org.json.JSONObject;

import simulator.model.FallingToCenterGravity;
import simulator.model.GravityLaws;

public class FallingToCenterGravityBuilder extends NewtonUniversalGravitationBuilder {
	
	public static final String TYPE = "ftcg";
	public static final String DESC = "Falling to center gravity"+" ("+TYPE+")";
	
	//BUILDERS-------------------------------------------------------------------------------------------------
	
	public FallingToCenterGravityBuilder()
	{
		super(TYPE, DESC);
	}
	
	//FUNCTIONS------------------------------------------------------------------------------------------------
	

	protected GravityLaws createConcrete(JSONObject data) {
		return new FallingToCenterGravity();
	}

	@Override
	public JSONObject createData() {
		return null;
	}
}
