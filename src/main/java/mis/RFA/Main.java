package mis.RFA;

import java.util.Locale;

public class Main {

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        // Paramètres communs

        double muC = 1/10.0;          // Coordinateur : 10 ms
        double p = 0.5;              // Probabilité de sortie
        double dureeSimulation = 100000.0;
        
        double lambda1 = 0.002;
        double lambda2 = 0.008;
        double lambda3 = 0.01;
        
        // TEST 1 : 1 serveur rapide
       /* System.out.println("= TEST 1 : 1 serveur rapide ==");

        

        double[] muServeurs1 = { 1/120.0 };   // 120 ms
        double[] q1 = { 1.0 };

        ReseauFilesAttente rfa1 = new ReseauFilesAttente(
                lambda3,
                muC,
                muServeurs1,
                p,
                q1,
                dureeSimulation
        );

        rfa1.simuler();


        // TEST 2 : 1 serveur rapide + 1 lent
        System.out.println("\n= TEST 2 : 1 rapide + 1 lent =");

        double[] muServeurs2 = { 1/120.0, 1/240.0 };  // 120 ms et 240 ms
        double[] q2 = { 0.5, 0.5 };

        ReseauFilesAttente rfa2 = new ReseauFilesAttente(
                lambda3,
                muC,
                muServeurs2,
                p,
                q2,
                dureeSimulation
        );

        rfa2.simuler();

        // TEST 3 : 1 serveur rapide + 2 serveurs lents
        System.out.println("\n=== TEST 3 : 1 rapide + 2 serveurs lents ===");

        double[] muServeurs3 = { 1/120.0, 1/240.0, 1/240.0};
        double[] q3 = { 1.0/3, 1.0/3, 1.0/3 };

        ReseauFilesAttente rfa3 = new ReseauFilesAttente(
                lambda3,
                muC,
                muServeurs3,
                p,
                q3,
                dureeSimulation
        );

        rfa3.simuler();*/
        
     // TEST 4 : 2 serveurs dont un plus rapide (190 ms)
        System.out.println("\n== TEST 4 : 2 serveurs (120 ms + 190 ms) =");

        double[] muServeurs4 = { 1/120, 1/190 }; // 120 ms et 190 ms
        double[] q4 = { 0.25, 0.25 };

        ReseauFilesAttente rfa4 = new ReseauFilesAttente(
                lambda3,
                muC,
                muServeurs4,
                p,
                q4,
                dureeSimulation
        );

        rfa4.simuler();

    }
}