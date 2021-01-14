package simulator.factories;

import org.json.JSONObject;

import simulator.model.GravityLaws;
import simulator.model.NoGravity;

public class NoGravityBuilder extends NewtonUniversalGravitationBuilder {
	
	public static final String TYPE = "ng";
	public static final String DESC = "No gravity "+" ("+TYPE+")";
	
	//BUILDERS-------------------------------------------------------------------------------------------------
	
	public NoGravityBuilder()
	{
		super(TYPE, DESC);
	}
	
	//FUNCTIONS------------------------------------------------------------------------------------------------
	
	protected GravityLaws createConcrete(JSONObject data) {
		return new NoGravity();
	}

	@Override
	public JSONObject createData() {
		return null;
	}

}
