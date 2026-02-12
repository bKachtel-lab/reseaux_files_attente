package mis.RFA;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Random;

public class ReseauFilesAttente {

    private static final Random rand = new Random();

    private final double lambda;          // taux d'arrivée dans le systeme
    private final double dureeSimulation; // T
    private final double[] q;             // probas de sortie vers les serveurs Fi

    private final Coordinateur fc;
    private final FileAttente[] f; // serveurs F1..Fn

    private final PriorityQueue<Evenmt> agenda;

    private double tempsCourant;
    private int compteurClients;      // id unique
    private int nbClientsEntres;
    private int nbClientsSortis;
    
    private PrintWriter ntWriter;
    private double lastTime, areaN, areaN_RP, tWarmup;
    // Pour les stats détaillées
    private final List<Client> clientsSortis;

    public ReseauFilesAttente(double lambda,
                              double c,              // paramètre du coordinateur
                              double[] muServeurs,  // paramètres des Fi
                              double pSortie,
                              double[] q,
                              double dureeSimulation) {

        this.lambda = lambda;
        this.dureeSimulation = dureeSimulation;
        this.q = q;

        this.fc = new Coordinateur(c, pSortie);
        this.f = new FileAttente[muServeurs.length];
        for (int i = 0; i < muServeurs.length; i++) {
            f[i] = new FileAttente("F" + (i + 1), muServeurs[i]);
        }

        this.agenda = new PriorityQueue<>();
        this.tempsCourant = 0.0;
        this.compteurClients = 0;
        this.nbClientsEntres = 0;
        this.nbClientsSortis = 0;
        this.clientsSortis = new ArrayList<>();
    }

    // Générateurs aléatoires

    private double genererInterArrivee() {
        return -Math.log(1 - rand.nextDouble()) / lambda;
    }

    private int tirageServeurSelonQ() {
        double u = rand.nextDouble();
        double cum = 0.0;
        for (int i = 0; i < q.length; i++) {
            cum += q[i];
            if (u <= cum) return i;
        }
        return q.length - 1; // sécurité
    }

    // Simulation principale

    public void simuler() {
        tWarmup = dureeSimulation / 2.0;
        lastTime = 0.0;
        areaN = 0.0;
        areaN_RP = 0.0;


        try (PrintWriter nt = new PrintWriter(new FileWriter(nomFichierNT()))) {
            this.ntWriter = nt;
            ntWriter.println("# t N");

            // première arrivée
            double datePremiereArrivee = genererInterArrivee();
            agenda.add(new Evenmt(datePremiereArrivee, Evenmt.TypeEvenement.ARRIVEE_EXTERNE, -1));

            lastTime = 0.0;

            while (!agenda.isEmpty()) {
                Evenmt ev = agenda.poll();
                tempsCourant = ev.getDate();
                if (tempsCourant > dureeSimulation) break;

                majAiresEtNT(tempsCourant);  // <---- ICI

                switch (ev.getType()) {
                    case ARRIVEE_EXTERNE -> traiterArriveeExterne();
                    case FIN_SERVICE_FC -> traiterFinServiceFc();
                    case FIN_SERVICE_FI -> traiterFinServiceFi(ev.getIndexServeur());
                }
            }

            // Flush final : on ferme l’aire jusqu’à T
            majAiresEtNT(dureeSimulation);
            System.out.println("N final ≈ " + nombreClientsDansReseau());

        } catch (IOException e) {
            System.err.println("Erreur NT: " + e.getMessage());
        } finally {
            ntWriter = null;
        }

        afficherStats();
    }


    // Traitement des événements
 
    private void traiterArriveeExterne() {
        compteurClients++;
        nbClientsEntres++;
        Client c = new Client(compteurClients, tempsCourant);

        // Arrivée sur le coordinateur
        if (fc.arriver(c, tempsCourant)) {
            // Fc était libre, on vient de lancer un service -> planifier sa fin
            agenda.add(new Evenmt(fc.getDateFinService(),
                                  Evenmt.TypeEvenement.FIN_SERVICE_FC,
                                  -1));
        }

        // Toujours planifier la prochaine arrivée externe
        double prochaineArrivee = tempsCourant + genererInterArrivee();
        agenda.add(new Evenmt(prochaineArrivee,
                              Evenmt.TypeEvenement.ARRIVEE_EXTERNE,
                              -1));
    }


    private void traiterFinServiceFc() {

        Client c = fc.terminerService(tempsCourant);
        if (c == null) return;

        if (fc.sortDuSys()) {
            c.setInstantSortie(tempsCourant);
            nbClientsSortis++;
            clientsSortis.add(c);
        } else {
            int idx = tirageServeurSelonQ();
            if (f[idx].arriver(c, tempsCourant)) {
                agenda.add(new Evenmt(f[idx].getDateFinService(),
                        Evenmt.TypeEvenement.FIN_SERVICE_FI, idx));
            }
        }

        if (fc.occupe) {
            agenda.add(new Evenmt(fc.getDateFinService(),
                    Evenmt.TypeEvenement.FIN_SERVICE_FC, -1));
        }
    }

    private void traiterFinServiceFi(int idx) {

        FileAttente fi = f[idx];
        Client c = fi.terminerService(tempsCourant);
        if (c == null) return;

        if (fc.arriver(c, tempsCourant)) {
            agenda.add(new Evenmt(fc.getDateFinService(),
                    Evenmt.TypeEvenement.FIN_SERVICE_FC, -1));
        }

        if (fi.occupe) {
            agenda.add(new Evenmt(fi.getDateFinService(),
                    Evenmt.TypeEvenement.FIN_SERVICE_FI, idx));
        }
    }
    
    private void majAiresEtNT(double t) {
        int N = nombreClientsDansReseau();
        double dt = t - lastTime;
        if (dt < 0) dt = 0;

        // aire totale
        areaN += N * dt;

        // aire régime permanent (sur [T/2, T])
        if (t > tWarmup) {
            double start = Math.max(lastTime, tWarmup);
            double dtRP = t - start;
            if (dtRP > 0) areaN_RP += N * dtRP;
        }

        // log N(t)
        if (ntWriter != null) {
            ntWriter.printf(Locale.US, "%.6f %d%n", t, N);
        }

        lastTime = t;
    }


    private int nombreClientsDansReseau() {
        int N = fc.getTailleFile();
        for (FileAttente fi : f) N += fi.getTailleFile();
        return N;
    }

    private String nomFichierNT() {
        return String.format(Locale.US,
            "src/main/resources/test_5_nt_lambda%.3f_n%d_p%.1f_T%.0f.dat",
            lambda, f.length, fc.getP(), dureeSimulation);
    }

    
    private void afficherStats() {
        System.out.println("Temps de simulation: " + dureeSimulation);
        System.out.println("Clients entrés : " + nbClientsEntres);
        System.out.println("Clients sortis : " + nbClientsSortis);

        if (!clientsSortis.isEmpty()) {
            double sommeTempsPresence = 0.0;
            for (Client c : clientsSortis) {
                sommeTempsPresence += c.getTempsPresence();
            }
            double W = sommeTempsPresence / clientsSortis.size();
            System.out.println("Temps moyen de présence dans le réseau W ≈ " + W + " ms");
        }
        double L = areaN / dureeSimulation;
        double Lrp = areaN_RP / (dureeSimulation - tWarmup);

        System.out.printf(Locale.US, "L moyen ≈ %.4f clients%n", L);
        System.out.printf(Locale.US, "L (regime permanent) ≈ %.4f clients%n", Lrp);

        System.out.printf(Locale.US, "L (régime permanent, [%.0f, %.0f]) ≈ %.4f clients%n",
                tWarmup, dureeSimulation, Lrp);

        for (int i = 0; i < f.length; i++) 
            System.out.println(f[i].getNom()
                    + " a traité " + f[i].getNbClientsTraites() + " clients.");
        
        
        String nomFichier = String.format(
        	    "src/main/resources/test_5_reseau_lambda%.3f_n%d_p%.1f_T%.0f.dat",
        	    lambda, f.length, fc.getP(), dureeSimulation
        	);
        sauvegarderClientsSortis(nomFichier);
    }
    
    public void sauvegarderClientsSortis(String nomFichier) {
        try (PrintWriter w = new PrintWriter(new FileWriter(nomFichier))) {

            // En-tête (commentaire pour Gnuplot)
            w.println("# ID ArriveeSysteme TempsPresence SortieSysteme");

            for (Client c : clientsSortis) {
                w.printf("%d %.6f %.6f %.6f%n",
                        c.getId(),
                        c.getInstantArrivee(),
                        c.getTempsPresence(),
                        c.getInstantSortie());
            }

            System.out.println("Données sauvegardées dans: " + nomFichier +
                               " (" + clientsSortis.size() + " clients)");

        } catch (IOException e) {
            System.err.println("Erreur sauvegarde " + nomFichier + ": " + e.getMessage());
        }
    }

}