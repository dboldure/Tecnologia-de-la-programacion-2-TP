package simulator.control;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Dictionary;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.factories.Factory;
import simulator.model.Body;
import simulator.model.GravityLaws;
import simulator.model.NewtonUniversalGravitation;
import simulator.model.PhysicSimulator;
import simulator.model.SimulatorObserver;


public class Controller {
	
	private PhysicSimulator sim;
	private Factory<Body> bodyFactory;
	private Factory<GravityLaws> gravityLaws;
	private String[] bodiesTraceForSteps;
	
	
	//BUILDERS--------------------------------------------------
	
	public Controller(PhysicSimulator sim, Factory<Body> bodyFactory,  Factory<GravityLaws> gravityLaws)
	{
		
		this.sim = sim;
		this.bodyFactory = bodyFactory;
		this.gravityLaws=gravityLaws;
	}
	
	//FUNCTIONS-------------------------------------------------
	
	public void loadBodies(InputStream in)	throws IllegalArgumentException
	{
		String result = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
		JSONObject o = new JSONObject(result);
		JSONArray a = new JSONArray();
		if(o.has("bodies"))
			a = o.getJSONArray("bodies");
		else throw new IllegalArgumentException();

		for(int i = 0; i < a.length(); i++)
		{
			Body b = bodyFactory.createInstance(a.getJSONObject(i));
			if(b != null)
				sim.addBody(b);
		}
		sim.load();
	}
	
	public void deleteBody(String id)
	{
		sim.deleteBody(id);
	}
	
	public void run(int n, OutputStream out) throws FileNotFoundException
	{
		bodiesTraceForSteps = sim.loadTrace();
		
		sim.startTrace(bodiesTraceForSteps);
		PrintStream p = new PrintStream(out);
		p.print("{\t\"states\": [");
		p.print(sim.toString());
		p.print(", ");
		for(int i = 0; i < n; i++)
		{
			printBodiesTrace();
			sim.advance();
			p.print(sim.toString());
			if(i < n-1)
				p.print(", ");
		}
		p.print("] }");
		p.println("\n");
		for(int i = 0; i < this.bodiesTraceForSteps.length; i++)
			p.println(bodiesTraceForSteps[i]+"\n");
		p.close();
	}
	
	private void printBodiesTrace()
	{
		String[] s = sim.bodiesTraceToString();
		for(int i = 0 ; i < s.length; i++)
		{
			this.bodiesTraceForSteps[i] += s[i];
			if(i < i-1)
				this.bodiesTraceForSteps[i]+= ", ";
		}
			
	}
	
	public void reset() {
		
		sim.reset();
	}
	
	public void setDeltaTime(double dt) {
		sim.setDeltaTime(dt);
	}
	
	public void addObserver(SimulatorObserver o) {
		sim.addObserver(o);
	}
	
	public void run(int n)
	{
		for(int i = 0; i < n; i++)
			sim.advance();
			
	}
	
	public Factory<GravityLaws>getGravityLawsFactory(){
		return gravityLaws;
	}

	public void setGravityLaws(JSONObject info) {
		GravityLaws gl = this.gravityLaws.createInstance(info);
		if(gl == null) 
			gl = new NewtonUniversalGravitation();
		this.sim.setGravityLaws(gl);
	}
	
	//AUX-------------------------------------------------------------------------------------
	
	public String debugLaw()
	{
		return this.sim.getLaws().toString();
	}
	
	
}
