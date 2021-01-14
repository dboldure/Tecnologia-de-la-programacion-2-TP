package simulator.factories;

import org.json.JSONException;
import org.json.JSONObject;

import simulator.model.GravityLaws;
import simulator.model.NewtonUniversalGravitation;

public class NewtonUniversalGravitationBuilder extends Builder<GravityLaws> {

	public static final String TYPE = "nlug";
	public static final String DESC = "Newton’s law of universal gravitation"+" ("+TYPE+")";
	
	//BUILDERS-----------------------------------------------------------------
	
	public NewtonUniversalGravitationBuilder() {
		super(TYPE, DESC);
	}
	
	public NewtonUniversalGravitationBuilder(String type, String desc) {
		super(type, desc);
	}
	
	//FUNCTIONS-------------------------------------------------------------------

	public GravityLaws createTheInstance(JSONObject o) throws IllegalArgumentException{
		try
		{

				return createConcrete(null);
		
		}
		catch(JSONException e)
		{
			throw new IllegalArgumentException();
		}
			
	}


	protected GravityLaws createConcrete(JSONObject data) {
		return new NewtonUniversalGravitation();
	}

	@Override
	public JSONObject createData() {
		return null;
	}

}
