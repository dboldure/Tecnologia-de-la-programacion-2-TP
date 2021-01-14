package simulator.factories;

import org.json.JSONException;
import org.json.JSONObject;

import simulator.misc.Vector;
import simulator.model.Body;


public class BasicBodyBuilder extends Builder<Body> {
	
	public static final String TYPE = "basic";
	public static final String DESC = "Basic body with id, speed, aceleration, position and mass";
	
	//BUILDERS--------------------------------------------------------------------------
	
	public BasicBodyBuilder()
	{
		super(TYPE,DESC);
	}
	
	//FUNCTIONS-------------------------------------------------------------------------

	public Body createTheInstance(JSONObject o) throws IllegalArgumentException{
		try
		{
			
				JSONObject data = o.getJSONObject("data");
				return createConcrete(data);
				
			
		}
		catch(JSONException e)
		{
			throw new IllegalArgumentException();
		}
			
	}
	
	public JSONObject createData()
	{
		JSONObject data = new JSONObject();

		data.put("id", "the identifier");
		data.put("pos", "the position");
		data.put("vel", "the speed");
		data.put("acc", "the acceleration");
		data.put("mass", "the mass");
		
		return data;
	}
	
	
	protected Body createConcrete(JSONObject data) {
		return new Body(data.getString("id"), 
				        getParseVector(data.getJSONArray("vel")), 
				        new Vector(getParseVector(data.getJSONArray("pos"))), 
				        data.getDouble("mass"));
	}
}
