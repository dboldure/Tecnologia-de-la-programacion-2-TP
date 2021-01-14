package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Vector;

public abstract class Builder<T> {
	
	private String _typeTag;
	private String _desc;
	
	//BUILDERS-----------------------------------------
	
	public Builder(String type, String desc)
	{
		this._typeTag = type;
		this._desc = desc;
	}
	
	//FUCNTIONS--------------------------------------------------------------------------------------------
	
	protected abstract T createTheInstance(JSONObject o);
	//protected abstract T createConcrete(JSONObject data);

	public T createInstance(JSONObject info)
	{
		if (this._typeTag.equalsIgnoreCase(info.getString("type"))) return createTheInstance(info);
		else return null;
	}
	
	public JSONObject getBuilderInfo()
	{
		JSONObject o = new JSONObject();

		o.put("type", this._typeTag);
		o.put("data", this.createData());
		o.put("desc", this._desc);
		return o;
	}
	
	protected double[] jasonArrayToDoubleArray(JSONArray a)
	{
		double[] d = new double[a.length()];
		for(int i = 0; i < a.length(); i++)
			d[i] = a.getDouble(i);
		return d;
	}
	
	public JSONObject createData()
	{
		return new JSONObject();
	}
	
	//AUX----------------------------------------------------------------------------------------------------------
	
	
	protected Vector getParseVector(JSONArray a)
	{
		double[] v = new double[a.length()];
		for(int i = 0; i < a.length(); i++)
			v[i] = a.getDouble(i);
		
		return new Vector(v);
	}

}
