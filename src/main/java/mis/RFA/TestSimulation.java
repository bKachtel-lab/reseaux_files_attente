package mis.RFA;

public class TestSimulation {

    public static void main(String[] args) {

        // Paramètres communs
    	
        double muC = 100.0;          // Coordinateur : 10 ms
        double p = 0.5;              // Probabilité de sortie
        double dureeSimulation = 30000.0;

        // ==============================
        // TEST 1 : 1 serveur rapide
        // ==============================
        System.out.println("= TEST 1 : 1 serveur rapide ==");

        double lambda1 = 0.002;

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


        // ==============================
        // TEST 2 : 1 serveur rapide + 1 lent
        // ==============================
        System.out.println("\n= TEST 2 : 1 rapide + 1 lent =");

        double[] muServeurs2 = { 0.00833, 0.00417 };  // 120 ms et 240 ms
        double[] q2 = { 0.25, 0.25 };

        ReseauFilesAttente rfa2 = new ReseauFilesAttente(
                lambda1,
                muC,
                muServeurs2,
                p,
                q2,
                dureeSimulation
        );

        rfa2.simuler();


    }
}
