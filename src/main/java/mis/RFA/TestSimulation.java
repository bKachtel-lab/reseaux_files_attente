package mis.RFA;

public class TestSimulation {

    public static void main(String[] args) {

        // Paramètres communs
    	
        double muC = 100.0;          // Coordinateur : 10 ms
        double p = 0.5;              // Probabilité de sortie
        double dureeSimulation = 3000.0;

        // ==============================
        // TEST 1 : 1 serveur rapide
        // ==============================
        System.out.println("= TEST 1 : 1 serveur rapide ==");

        double lambda1 = 0.002;  // ms

        double[] muServeurs1 = { 0.0083 };   // 120 ms
        double[] q1 = { 0.5 };

        ReseauFilesAttente rfa1 = new ReseauFilesAttente(
                lambda1,
                muC,
                muServeurs1,
                p,
                q1,
                dureeSimulation
        );

        rfa1.simuler();

    }
}
