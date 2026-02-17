![Logo Université](images/logoUniv.png)

### Master 1 IWOCS

---
# Compte rendu
## Implémentation d’un réseau de files d’attente pour simuler une base de données distribuée


### Membres du binôme :
- Boussad Hammoum
- Boukhalfa Kachtel 

### Introduction
Ce projet porte sur la modélisation et la simulation d'une base de données distribuée par un réseau de files d'attente. Les requêtes (clients) arrivent selon un processus de Poisson de taux λ au coordinateur (file Fc, service exponentiel de paramètre c). Après service au coordinateur :    

* Avec probabilité p, la requête est terminée et quitte le système.

* Avec probabilité 1-p, elle est envoyée vers un serveur Fi (choisi selon les probabilités qi), où elle subit un service exponentiel de paramètre μi

* Le résultat retourne au coordinateur, qui peut de nouveau router ou terminer la requête.

Chaque file est une M/M/1 L’objectif est d’étudier la stabilité et les performances (temps moyen de présence W, nombre moyen de requêtes L) en régime permanent, via simulation et théorie de Jackson.  
​Le modèle peut être présenté ainsi :  
![schema](images/schema.png)

###### Paramètres du système :
| Serveur | Temps de service (ms) | Taux de service μ (req/s) |
|:-------:|:---------------------:|:--------------------------:|
| Fc      | 10                    | 100                        |
| Rapides | 120                   | 8.33                       |
| Lents   | 240                   | 4.17                       |
| Moyens  | 190                   | 5.26                       |
###### Paramètre de simulation :
| Paramètre | Description | Valeur |
|:-------:|:---------------------:|:--------------------------:|
| qi      | probabilite d'orientation vers un serveur i| 1/n|
| 1-p | probabilité de renvoi vers un serveur après passage sur Fc| 1/2|  

### Conception
#### Modélisation 
On a décidé de repartir à partir du TP3 qui avait pour objectif de **d’etudier et de simuler un système de file d’attente de type
M/M/1,** et donc de récuperer les classes *FileAttente et Client*   

| Classe  | Role | Attributs clés|
|:-------:|:---------------------:|:--------------------------:|
| Client      | Requête individuelle |id, instantArriveeSusteme, instantSortieSystem|
| FileAttente | MM1 générique|Queue<Client>, mu, occupe, finService, getTailleFile()|
| Coordinateur (herite de FileAttente)| File Fc avec routage| p, sortDuSys() |
| Evenmt   | Representation des événements discrets| date, TypeEvenement     |
| ReseauFilesAttente| Simulateur principal| agenda, majAiresEtNT(), calcul L/W, export données
|Main|Execution des tests| Paramètres du système (λ,qi, μ...), dureeSimulation|

#### Architecture
```mermaid
classDiagram
    class Evenement {
        -date: double
        -type: TypeEvenement
        +compareTo()
    }
    
    class PriorityQueue~Evenement~ {
        +add()
        +poll()
        +peek()
    }
    
    class Client {
        -id: int
        -dateArrivee: double
        -dateDepart: double
        +getTempsSejour()
    }
    
    class FileAttente {
        #queue: Queue~Client~
        #μ: double
        #nbClients: int
        +arriver()
        +partir()
        +getStatistiques()
    }
    
    class Coordinateur {
        -p: double
        -probabilitesRouting: double[]
        +traiterRequete()
        +routerVersServeur()
    }
    
    class ReseauFilesAttente {
        -λ: double
        -files: List~FileAttente~
        -events: PriorityQueue~Evenement~
        -clients: List~Client~
        +simuler()
        +initialiser()
        +prochainEvenement()
    }

    PriorityQueue~Evenement~ --> Evenement : contient
    ReseauFilesAttente --> PriorityQueue~Evenement~ : utilise
    ReseauFilesAttente --> FileAttente : gère
    ReseauFilesAttente --> Client : suit
    FileAttente --> Client : contient
    Coordinateur --|> FileAttente : hérite
  ```  
  
