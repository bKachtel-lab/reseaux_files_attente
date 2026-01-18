package mis.RFA;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

public class FileAttente {
	private static final Random rand = new Random();

	private final String nom;
	private final double mu; 
	private final Queue<Client> file; 
	private boolean occupe;
	private double finService; 
	private int nbClientsTraites;

	public FileAttente(String nom, double mu) {
		this.nom = nom;
		this.mu = mu;
		this.file = new ArrayDeque<>();
		this.occupe = false;
		this.finService = Double.POSITIVE_INFINITY;
	}

	public String getNom() {
		return nom;
	}

	public double getMu() {
		return mu;
	}

	public boolean estOccupe() {
		return occupe;
	}

	public double getDateFinService() {
		return finService;
	}

	public int getNbClientsTraites() {
		return nbClientsTraites;
	}

	private double genererTempsService() { // 1/mu
		return -Math.log(1 - rand.nextDouble()) / mu;
	}

	// entrée d’un client dans cette file à la date t
	public void arriver(Client c, double tCourant) {
		file.add(c);
		if (!occupe) {
			demarrerService(tCourant);
		}
	}

	// Termine le service courant, renvoie le client qui sort de cette file
	public Client terminerService(double tCourant) {
		if (!occupe)
			return null;
		Client client = file.poll();
		nbClientsTraites++;
		// Lancer le suivant si file non vide
		if (file.isEmpty()) {
			occupe = false;
			finService = Double.POSITIVE_INFINITY;
		} else {
			demarrerService(tCourant);
		}
		return client;
	}

	private void demarrerService(double tCourant) {
		Client courant = file.peek();
		if (courant == null) {
			occupe = false;
			finService = Double.POSITIVE_INFINITY;
			return;
		}
		occupe = true;
		double tempsService = genererTempsService();
		finService = tCourant + tempsService;
	}

	public int getTailleFile() {
		return file.size(); 
	}
}
