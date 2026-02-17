package mis.RFA;

import java.util.Locale;

public class Main {

	private static void lancerTest(String nom, double[] lambdas, double muC, double[] muServeurs, double p, double[] q,
			double dureeSimulation, String dossierSortie) {

		for (double lambda : lambdas) {

			System.out.println("\n===== " + nom + " | lambda = " + lambda + " ===========================================");

			ReseauFilesAttente rfa = new ReseauFilesAttente(lambda, muC, muServeurs, p, q, dureeSimulation, dossierSortie);

			rfa.simuler();
		}
	}

	public static void main(String[] args) {
		Locale.setDefault(Locale.US);

		// Paramètres communs

		double muC = 1 / 10.0; // Coordinateur : 10 ms
		double p = 0.5; // Probabilité de sortie
		double dureeSimulation = 100000.0;

		double lambdas[] = {0.002, 0.008, 0.01};

// TEST CAS 1
		double[] muServeurs1 = { 1 / 120.0 }; // 120 ms
		double[] q1 = { 1.0 };
		lancerTest("TEST 1 : 1 serveur rapide",
		           lambdas,
		           muC,
		           muServeurs1,
		           p,
		           q1,
		           dureeSimulation, "test1");
		
// TEST CAS 2		
		double[] muServeurs2 = { 1/120.0, 1/240.0 }; // 120 ms et 240 ms 
		double[] q2 = { 0.5, 0.5 };
		lancerTest("Test 2 : 2 serveurs (rapide + lent)", 
					lambdas, 
					muC,	
					muServeurs2, 
					p,
					q2,
					dureeSimulation, "test2");
		
// TEST CAS 3		
		double[] muServeurs3 = { 1/120.0, 1/240.0, 1/240.0};
		double[] q3 = { 1.0/3, 1.0/3, 1.0/3 };
		lancerTest("Test 3 : 3 serveurs (rapide + 2 lents)", 
				    lambdas, 
				    muC, 
				    muServeurs3,
				    p,
				    q3,
				    dureeSimulation, "test3");
		
// TEST CAS 4		
		double[] muServeurs4 = { 1.0/120, 1.0/190 }; // 120 ms et 190 ms
		double[] q4 = { 1.0/2.0, 1.0/2.0 };
		lancerTest("Test 4 : 2 serveurs (rapide + moyen (190ms))", 
			    lambdas, 
			    muC, 
			    muServeurs4,
			    p,
			    q4,
			    dureeSimulation, "test4");
		
// TEST CAS 5		
		double[] muServeurs5 = { 1.0 / 120, 1.0 / 240, 1.0 / 240 };
		double[] q5 = { 1.0 / 3, 1.0 / 3, 1.0 / 3 };
		double[] ps = {0.2, 0.5, 0.8};
		for (double pTest : ps) {

		    for (double lambda : lambdas) {

		        System.out.println("\n===== TEST 5 | lambda = " + lambda + " | p = " + pTest + " =====");

		        ReseauFilesAttente rfa = new ReseauFilesAttente(
		                lambda,
		                muC,
		                muServeurs5,
		                pTest,
		                q5,
		                dureeSimulation,
		                "test5");

		        rfa.simuler();
		    }
		}
	}
}