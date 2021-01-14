package simulator.model;

import simulator.misc.Vector;

public class MassLosingBody extends Body {

	private Double lossFactor;
	private Double lossFrequency;
	
	private Double timeCounter = 0.0;
	
	
	
	//BUILDERS----------------------------------------------------------------------------------------------
	
/*	public MassLosingBody() {
		
	}
	*/

	public MassLosingBody(String id, Vector v, Vector p, Double mass, Double lFac, Double lFreq) {
		super(id, v, p, mass);
		this.lossFactor = lFac;
		this.lossFrequency = lFreq;
	}
	

	//FUNCTIONS----------------------------------------------------------------------------------------------
	
	public void move(Double t)
	{
		super.move(t);
		massLose(t);
	}
	
	private void massLose(Double t)
	{
		while(t >= this.lossFrequency)
		{
			this.mass *= (1-this.lossFactor);
			t -= lossFrequency;
		}
		timeCounter += t;
	}
	
	public String toString()
	{
		return super.toString().replace(this.mass.toString(), (this.mass+",\n\t\t\"freq\": "+this.lossFrequency+",\n\t\t\"factor\": "+this.lossFactor));
	}

	
	//AUX----------------------------------------------------------------------------------------------------
	
	public MassLosingBody clone()
	{
		return new MassLosingBody(ID, v, p,mass, lossFactor, lossFrequency);
	}
	
	public Double getLossFactor() {
		return lossFactor;
	}

	public void setLossFactor(Double lossFactor) {
		this.lossFactor = lossFactor;
	}

	public Double getLossFrequency() {
		return lossFrequency;
	}

	public void setLossFrequency(Double lossFrequency) {
		this.lossFrequency = lossFrequency;
	}

	public Double getTimeCounter() {
		return timeCounter;
	}

	public void setTimeCounter(Double timeCounter) {
		this.timeCounter = timeCounter;
	}

}
