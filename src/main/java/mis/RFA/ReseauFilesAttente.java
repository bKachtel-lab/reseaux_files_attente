package mis.RFA;

import java.util.ArrayList;
import java.util.List;
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

    // =====================
    // Générateurs aléatoires
    // =====================

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

    // =====================
    // Simulation principale
    // =====================

    public void simuler() {
        // première arrivée externe
        double datePremiereArrivee = genererInterArrivee();
        agenda.add(new Evenmt(datePremiereArrivee,
                              Evenmt.TypeEvenement.ARRIVEE_EXTERNE,
                              -1));

        while (!agenda.isEmpty()) {
            Evenmt ev = agenda.poll();
            tempsCourant = ev.getDate();

            if (tempsCourant > dureeSimulation) {
                break;
            }

            switch (ev.getType()) {
                case ARRIVEE_EXTERNE -> traiterArriveeExterne();
                case FIN_SERVICE_FC -> traiterFinServiceFc();
                case FIN_SERVICE_FI -> traiterFinServiceFi(ev.getIndexServeur());
            }
        }

        // Ici tu pourras calculer les stats globales à partir de clientsSortis
        afficherStats();
    }

    // =====================
    // Traitement des événements
    // =====================

    private void traiterArriveeExterne() {
        // Création du client
        compteurClients++;
        nbClientsEntres++;
        Client c = new Client(compteurClients, tempsCourant);

        // Arrivée sur le coordinateur
        fc.arriver(c, tempsCourant);
        // Si Fc vient de devenir occupé, on planifie la fin de service
        if (fc.estOccupe() && fc.getDateFinService() >= tempsCourant) {
            agenda.add(new Evenmt(fc.getDateFinService(),
                                  Evenmt.TypeEvenement.FIN_SERVICE_FC,
                                  -1));
        }

        // Programmer la prochaine arrivée externe
        double prochaine = tempsCourant + genererInterArrivee();
        agenda.add(new Evenmt(prochaine,
                              Evenmt.TypeEvenement.ARRIVEE_EXTERNE,
                              -1));
    }

    private void traiterFinServiceFc() {
        // Le coordinateur termine le service de son client courant
        Client c = fc.terminerService(tempsCourant);
        if (c == null) {
            // plus personne, rien à faire
            return;
        }

        // Tirage : sortie ou renvoi vers un serveur
        if (fc.sortDuSys()) {
            c.setInstantSortie(tempsCourant);
            nbClientsSortis++;
            clientsSortis.add(c);
        } else {
            int idx = tirageServeurSelonQ();
            f[idx].arriver(c, tempsCourant);
            if (f[idx].estOccupe()
                    && f[idx].getDateFinService() >= tempsCourant) {
                agenda.add(new Evenmt(f[idx].getDateFinService(),
                                      Evenmt.TypeEvenement.FIN_SERVICE_FI,
                                      idx));
            }
        }

        // Si Fc a encore un client, planifier la prochaine fin de service
        if (fc.estOccupe()) {
            agenda.add(new Evenmt(fc.getDateFinService(),
                                  Evenmt.TypeEvenement.FIN_SERVICE_FC,
                                  -1));
        }
    }

    private void traiterFinServiceFi(int idxServeur) {
        FileAttente fi = f[idxServeur];

        Client c = fi.terminerService(tempsCourant);
        if (c == null) {
            return;
        }

        // Retour au coordinateur
        fc.arriver(c, tempsCourant);
        if (fc.estOccupe()
                && fc.getDateFinService() >= tempsCourant) {
            agenda.add(new Evenmt(fc.getDateFinService(),
                                  Evenmt.TypeEvenement.FIN_SERVICE_FC,
                                  -1));
        }

        // Si le serveur Fi a encore quelqu'un, planifier sa prochaine fin
        if (fi.estOccupe()) {
            agenda.add(new Evenmt(fi.getDateFinService(),
                                  Evenmt.TypeEvenement.FIN_SERVICE_FI,
                                  idxServeur));
        }
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
            System.out.println("Temps moyen de présence dans le réseau W ≈ " + W);
        }

        for (int i = 0; i < f.length; i++) {
            System.out.println(f[i].getNom()
                    + " a traité " + f[i].getNbClientsTraites() + " clients.");
        }
    }
}
