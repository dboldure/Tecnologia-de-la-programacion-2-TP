package simulator.factories;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class BuilderBasedFactory<T> implements Factory<T> {
	
	private List<Builder<T>> builders;
	private List<JSONObject> info;

	//BUILDERS---------------------------------------------------------------------------------------------
	
	public BuilderBasedFactory(List<Builder<T>> builders)
	{
		this.builders = builders;
		updateInfo();
	}
	
	//FUCNTIONS--------------------------------------------------------------------------------------------
	

	@Override
	public T createInstance(JSONObject info) throws IllegalArgumentException{
		T o;
		for(Builder<T> builder : builders)
		{
			o = builder.createInstance(info);
			if(o != null)
				return o;
		}
			
		return null;

	}
	
	private void updateInfo()
	{
		List<JSONObject> result = new ArrayList<JSONObject>();
		for(Builder<T> builder : builders)
			result.add(builder.getBuilderInfo());
		this.info = result;
	}

	@Override
	public List<JSONObject> getInfo() {
		return this.info;
	}

}
