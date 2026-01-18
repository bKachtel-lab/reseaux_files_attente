package mis.RFA;

public class Client {
	private final int id;
	
	private final double instantArrivee;
	private double instantSortie;
	
	public Client(int id, double instantArriveeSysteme) {
		this.id = id;
		this.instantArrivee = instantArriveeSysteme;
	}
	
	public int getId() {
		return id;
	}
	
	public double getInstantArrivee() {
		return instantArrivee;
	}
	
	public double getInstantSortie() {
		return instantSortie;
	}
	
	public void setInstantSortie(double t) {
		this.instantSortie = t;
	}
	
	public double getTempsPresence() {
		return instantSortie - instantArrivee;
	}
	
	
}
