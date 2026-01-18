package mis.RFA;

public class Evenmt implements Comparable<Evenmt> {

	public enum TypeEvenement {
	    ARRIVEE_EXTERNE,
	    FIN_SERVICE_FC,
	    FIN_SERVICE_FI
	}
	
    private final double date;
    private final TypeEvenement type;
    private final int idxServeur; // pour FIN_SERVICE_FI : index de Fi

    public Evenmt(double date, TypeEvenement type, int indexServeur) {
        this.date = date;
        this.type = type;
        this.idxServeur = indexServeur;
    }

    public double getDate() { return date; }
    public TypeEvenement getType() { return type; }
    public int getIndexServeur() { return idxServeur; }

    @Override
    public int compareTo(Evenmt other) {
        return Double.compare(this.date, other.date);
    }
}
