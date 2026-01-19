package mis.RFA;

public class Coordinateur extends FileAttente{
	private final double p;
	
	public Coordinateur(double mu, double p) {
		super("Fc", mu);
		this.p = p;
	}
	
	public boolean sortDuSys() {
		return Math.random() < p;
	}
}
