package simulator.model;

import simulator.misc.Vector;

public class Body implements Cloneable{
	protected String ID;
	protected Vector v, a, p;
	protected Double mass;

	//BUILDER---------------------------------------------------------------------------------------------------------------

	/*public Body() {
		new Body("", new Vector(0), new Vector(0), new Vector(0), 0.0);
	}
	
	
	public Body(Body b)
	{
		new Body(b.getID(), b.getV(), b.getA(), b.getP(), b.getMass());
	}*/
	
	public Body(String id, Vector v, Vector p, Double mass) {
		this.ID = id;
		this.v = v;
		this.a = new Vector(this.v.dim());
		this.p = p;
		this.mass = mass;
	}
	
	
	
	//FUNCTIONS--------------------------------------------------------------------------------------------------------------
	
	public void move(Double t)
	{
		p = p.plus(v.scale(t)).plus(a.scale(Math.pow(t, 2)*0.5));
		v = v.plus(a.scale(t));
	}
	
	
	
	public String toString()
	{
		return "{\"id\": \""+this.ID+"\",\"pos\": "+this.p.toString()+",\"vel\": "+this.v.toString() + ",\"acc\": "+this.a.toString()+ ",\"mass\": "+this.mass + "}";
	}
	
	//AUX--------------------------------------------------------------------------------------------------------------------

	public Body clone()
	{
		return new Body(this.ID, this.v, this.p, this.mass);
	}
	
	public String getID() {
		return ID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ID == null) ? 0 : ID.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Body other = (Body) obj;
		if (ID == null) {
			if (other.ID != null)
				return false;
		} else if (!ID.equals(other.ID))
			return false;
		return true;
	}


	public void setID(String iD) {
		ID = iD;
	}

	public Vector getV() {
		return new Vector(v);
	}

	public void setV(Vector v) {
		this.v = new Vector(v);
	}

	public Vector getA() {
		return new Vector(a);
	}

	public void setA(Vector a) {
		this.a = new Vector(a);
	}

	public Vector getP() {
		return new Vector(p);
	}

	public void setP(Vector p) {
		this.p = new Vector(p);
	}

	public Double getMass() {
		return mass;
	}

	public void setMass(Double mass) {
		this.mass = mass;
	}
	
}
