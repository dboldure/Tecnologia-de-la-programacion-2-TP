package simulator.factories;

import org.json.JSONException;
import org.json.JSONObject;

import simulator.misc.Vector;
import simulator.model.Body;
import simulator.model.MassLosingBody;

public class MassLosingBodyBuilder extends Builder<Body> {
		
	public static final String TYPE = "mlb";
	public static final String DESC = "Body with id, speed, aceleration, position and time-decrement mass";
	
	//BUILDERS--------------------------------------------------------------------------
	
	public MassLosingBodyBuilder() {
		super(TYPE, DESC);
	}
	
	//FUNCTIONS-------------------------------------------------------------------------

	

	public MassLosingBody createTheInstance(JSONObject o) throws IllegalArgumentException{
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
		data.put("factor", "the factor");
		data.put("freq",  "the frequency");
		
		return data;
	}
	
	protected MassLosingBody createConcrete(JSONObject data) {
		return new MassLosingBody(data.getString("id"), getParseVector(data.getJSONArray("vel")), 
				 new Vector(getParseVector(data.getJSONArray("pos"))), data.getDouble("mass"), data.getDouble("factor"), data.getDouble("freq"));
	}

}
