import java.util.Random;
import java.util.Arrays;
import java.util.Scanner;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Classe interface permettant à l'agent/(ou autre) d'inscrire/vérifier/mettre à jour des membres/services
 * à travers des commandes définies dans le manuel afin de protéger les données.
 */
public class MenuAgent {
	
	private static Clients clients;
	private static Services services;
	private static TimeChecker timeChecker;
	
	public MenuAgent() {
		
		this.clients = new Clients();
		this.services = new Services();
		this.timeChecker = new TimeChecker();
		this.timeChecker.addData(this.clients,this.services);
		this.timeChecker.start(); 
		
	}
	
	
	// Tests pour ajouterCient
	@Test
	public void testAjouterClient() {
		
		// Test pour un membre
		String nom="The Hedgehog",prenom="Sonic";
		int num = this.ajouterClient(nom, prenom, false, "59 Rue Caulaincourt", "Paris", "75018", "TheBlueBlurr@gmail.com");
		Client returned = this.rechercherClient(num);
		assertNotNull("TEST ERROR: Cannot find client with number "+Integer.toString(num)+" Despite it being added", returned);
		assertEquals("TEST ERROR: Name of found client doesn't match the one specified when added", "The Hedgehog", returned.getNom());
		assertEquals("TEST ERROR: Normal client marks itself as a professional", false, returned.getIsPro());
		
		// Supprimer le membre ajout� pour les tests
		this.supprimerClient(returned);
		
		// Test pour un professionel
		nom="Robotnik";prenom="Dr. Ivo";
		num = this.ajouterClient(nom, prenom, true, "49 Rue Bourgelat", "Maison-Alfort", "94700", "EvilScientist@yahoo.fr");
		returned = this.rechercherClient(num);
		assertNotNull("TEST ERROR: Cannot find client with number "+Integer.toString(num)+" Despite it being added", returned);
		assertEquals("TEST ERROR: Name of found client doesn't match the one specified when added", "Robotnik", returned.getNom());
		assertEquals("TEST ERROR: Professional client doesn't mark itself as professional", true, returned.getIsPro());
		
		// Supprimer le professionel ajout� pour les tests
		this.supprimerClient(returned);
		
	}
	
	@Test
	public void testAddService() {
		// Le professionel auquel sera li� le service test�
		String nom="Robotnik",prenom="Dr. Ivo";
		int numPro = this.ajouterClient(nom, prenom, true, "49 Rue Bourgelat", "Maison-Alfort", "94700", "EvilScientist@yahoo.fr");
		int numService = this.addService("Entrainement � la course d'h�risson", "2019-12-13-15-30", 5.5, "Ceci est un commentaire", 10, numPro);
		Service returned = this.rechercherService(numService);
		assertNotNull("TEST ERROR: Cannot find service with number"+Integer.toString(numService)+" Despite it being added",returned);
		assertEquals("TEST ERROR: Name of found service doesn't match the one specified when added", "Entrainement � la course d'h�risson", returned.getName());
		assertEquals("TEST ERROR: Service isn't attributed to the right professional", numPro, returned.getNumProfessionel());
		assertTrue("TEST ERROR: Service isn't added to the professional's list of services", this.rechercherClient(numPro).getListService().contains(this.rechercherService(numService)));

	}

	
	// Tests pour Mise � jour Client
	@Test
	public void testmaJourClient() {
		
		// Test pour un membre
		String nom="The Hedgehog",prenom="Sonic";
		int num = this.ajouterClient(nom, prenom, false, "59 Rue Caulaincourt", "Paris", "75018", "TheBlueBlurr@gmail.com");
		Client returned = this.rechercherClient(num);
		this.maJourClient(returned, "Hedgehog", "Sonnic" , "30 Avenue" , "Marseille", "99900", "TheBlueBlurr@gmail.com");
		
		assertEquals("TEST ERROR: Name of found client doesn't match the one specified when uptaded", "Hedgehog", returned.getNom());
		assertEquals("TEST ERROR: Forename of found client doesn't match the one specified when uptaded", "Sonnic", returned.getPrenom());
		assertEquals("TEST ERROR: Address of found client doesn't match the one specified when uptaded", "30 Avenue 99900, Marseille", returned.getFullAddress());

		
		// Supprimer le membre ajout� pour les tests
		this.supprimerClient(returned);
		
		// Test pour un professionel
		nom="Robotnik";prenom="Dr. Ivo";
		num = this.ajouterClient(nom, prenom, true, "49 Rue Bourgelat", "Maison-Alfort", "94700", "EvilScientist@yahoo.fr");
		returned = this.rechercherClient(num);
		this.maJourClient(returned, "Robotnikk", "Mr. Ivo" , "30 Avenue" , "Marseille", "99900", "EvilScientist@yahoo.fr");
		assertEquals("TEST ERROR: Name of found client doesn't match the one specified when uptaded", "Robotnikk", returned.getNom());
		assertEquals("TEST ERROR: Forename of found client doesn't match the one specified when uptaded", "Mr. Ivo", returned.getPrenom());
		assertEquals("TEST ERROR: Address of found client doesn't match the one specified when uptaded", "30 Avenue 99900, Marseille", returned.getFullAddress());
		
		// Supprimer le professionel ajout� pour les tests
		this.supprimerClient(returned);
		
	}
	
	//Test pour Mise � jour Service
	@Test
	public void testmaJourService() {
		// Le professionel auquel sera li� le service test�
		String nom="Robotnik",prenom="Dr. Ivo";
		int numPro = this.ajouterClient(nom, prenom, true, "49 Rue Bourgelat", "Maison-Alfort", "94700", "EvilScientist@yahoo.fr");
		int numService = this.addService("Entrainement � la course d'h�risson", "2019-12-13-15-30", 5.5, "Ceci est un commentaire", 10, numPro);
		Service returned = this.rechercherService(numService);
		this.maJourService(returned, "Entrainement � la nage dauphin" , "6.0" , "20" , "2020-12-13-15-30", "Ceci n'est pas un commentaire");
		assertEquals("TEST ERROR: Name of found service doesn't match the one specified when uptaded", "Entrainement � la nage dauphin", returned.getName());
		assertEquals("TEST ERROR: Price of found service doesn't match the one specified when uptaded", 6.0 , returned.getFrais(), 0);
		assertEquals("TEST ERROR: Capacity of found service doesn't match the one specified when uptaded", 20 , returned.getCapacite());
		assertEquals("TEST ERROR: LocalDate of found service doesn't match the one specified when uptaded", "2020-12-13T15:30", returned.getDateService().toString());
		assertEquals("TEST ERROR: Comment of found service doesn't match the one specified when uptaded", "Ceci n'est pas un commentaire", returned.getCommentaire());
	}

	/**
	 * Fonction permettant d'ajouter un nouveau client(membre ou pro) dans la base de données avec les informations
	 * fournies par le client
	 * @param nom
	 * @param prenom
	 * @param isPro : veut-t-il s'inscrire en tant que pro ou membre
	 * @param address
	 * @param town
	 * @param postCode
	 * @param courriel : utilisé pour pouvoir entrer dans le gym sans devoir parler à l'agent
	 * @return : un code client unique qui va servir à identifier le client quand il veut un service ou autre
	 */
	// M�thode appel�e pour ajouter un client 
	public int ajouterClient(String nom, String prenom, boolean isPro, String address, String town, String postCode, String courriel) {
		
		// Cr�er l'objet date
		LocalDateTime dateInscription = LocalDateTime.now();
		
		int num = this.clients.addClient(nom, prenom, isPro, dateInscription,false, address, town, postCode, courriel);
		
		return num;
		
	}

	/**
	 * Fonction qui permet d'ajouter un service lié à un pro à la base de données
	 * @param name
	 * @param dateToSplit
	 * @param frais
	 * @param commentaire
	 * @param capacite
	 * @param numProfessionel
	 * @return : code unique du service qui va servir à identifier le service pour le modifier ou le supprimer
	 */
	public int addService(String name, String dateToSplit, Double frais, String commentaire, int capacite, int numProfessionel) {
		
		String[] tmp = dateToSplit.split("-");
		String heureStr = (tmp[3]+"."+tmp[4]);
		double heure = Double.parseDouble(heureStr);
		LocalDateTime dateService = LocalDateTime.of(Integer.parseInt(tmp[0]),Integer.parseInt(tmp[1]),Integer.parseInt(tmp[2]),(int)heure, (int)((heure - (int)heure) *100));
		
		LocalDateTime dateActuelle = LocalDateTime.now();
		
		int num = this.services.addService(name, dateService, dateActuelle, frais, commentaire, capacite, numProfessionel);
		
		// Ajouter ce service � la liste des services de ce professionel
		this.rechercherClient(numProfessionel).addListService(this.rechercherService(num));
		return num;
	}

	/**
	 * Permet de trouver tous les services donnés dans une journée donnée en utilisant la date de la journée
	 * @param jour
	 * @param mois
	 * @param ans
	 * @return liste de services (ou rien)
	 */
	public ArrayList<Service> trouverService(int jour, int mois, int ans) {
		
		ArrayList<Service> toReturn = services.trouverService(jour,mois,ans);
		return toReturn;
	}

	/**
	 * Retorune un objet client en utilisant son code client
	 * @param num
	 * @return objet client
	 */
	public Client rechercherClient(int num) {
		
		return this.clients.getClient(num);
	}

	/**
	 * Retourne un objet service en utilisant son code service
	 * @param num
	 * @return objet service
	 */
	public Service rechercherService(int num) {
		
		return this.services.getService(num);
	}

	/**
	 * Fonction qui met à jour les information de n'importe quel client dans la base de données. Pour ne pas modifier
	 * une certain donnée , donner un argument vide comme suit ""
	 * @param toChange client à mettre à jour
	 * @param nom
	 * @param prenom
	 * @param address
	 * @param town
	 * @param postCode
	 * @param courriel
	 */
	public void maJourClient(Client toChange, String nom, String prenom, String address, String town, String postCode, String courriel) {

		this.clients.updateClient(toChange,nom,prenom, address, town, postCode, courriel);
	}

	/**
	 * Fonction qui met à jour les information de n'importe quel service dans la base de données. Pour ne pas modifier
	 * une certain donnée, donner un argument vide comme suit """
	 * @param toChange :Service à changer
	 * @param newName
	 * @param newFrais
	 * @param newCap
	 * @param newLocalDate
	 * @param newComment
	 */
	public void maJourService(Service toChange, String newName, String newFrais, String newCap, String newLocalDate, String newComment) {
		
		this.services.updateService(toChange,newName, newFrais,newCap,newLocalDate,newComment);
	}

	/**
	 * Fonction qui supprime de la base de données n'importe quel client
	 * @param toDelete : objet client à supprimer
	 */
	public void supprimerClient(Client toDelete) {
		
		this.clients.deleteClient(toDelete);
		
	}

	/**
	 * Fonction main : la plus importante de cette classe. S'occupe d'intéragir avec l'agent à travers la console avec
	 * des commandes précises. Chaque input donné par l'agent (ou autre) dans la console est lu et le commande entrée
	 * est éxcutée. Ces méthodes ont déjà été définies dans cette classe. Elles sont excutées de manière à respecter
	 * les spécification de #gym. On utilise un scanner avec une boucle while infinie pour que le système soit toujours
	 * en fonction et prêt à recevoir de nouvelles commandes. On utilise des cases pour savoir quelle commande est entrée
	 * @param args
	 */
	public static void main(String args[]) {
		
		String helpMsg = "Commandes disponibles:\nVerifier: V�rifier la validit� d'un num�ro.\nAjouter: Ajouter un nouveau client ou une s�ance.\nModifier: Modifier les informations d'un client ou d'un service.\nSupprimer: Supprimer un client ou un service.\nTrouver: Trouver les services � une date pr�cise.\nInscrire: Inscrire un client � une s�ance.\nConfirmer: Confirmer sa pr�sence � une s�ance.\nConsulter: Consulter le nombre de personnes inscrites et/ou confirm�es � une s�ance.\nRechercher: Trouver un client ou un service � partir de son num�ro.\nSynthese: G�n�re la synth�se.\nAide: Affiche ce message.";
		System.out.println(helpMsg);
		MenuAgent menu = new MenuAgent();
		
		while(true) {  // Main Loop
			
			System.out.println("\nCommande:");
			Scanner input = new Scanner(System.in);
			String user = input.nextLine();
			int num;
			Client found;			
			
			// En fonction de la commande, on execute la m�thode correspondante.
			switch(user.toLowerCase()) {
				
				case "verifier":
					
					System.out.println("Entrez le num�ro du client:");
					num = Integer.parseInt(input.nextLine());
					found = menu.rechercherClient(num);
					
					if(found != null) {
						
						if(found.getIsSuspended()) {
							System.out.println("Membre suspendu");
							
						} else {
							System.out.println("Valid�");
						}
					} else {
						System.out.println("Num�ro invalide");
					}
					
					break;
					
				case "ajouter":
				
					System.out.println("Ajouter un client ou un service? (client/service)");
					user = input.nextLine();
					switch(user.toLowerCase()) {
						
						case "client":
							System.out.println("Entrez le nom:");
							String nom = input.nextLine();
							System.out.println("Entrez le pr�nom:");
							String prenom = input.nextLine();
							System.out.println("Entrez l'addresse du client:");
							String address = input.nextLine();
							System.out.println("Entrez la ville du client:");
							String town = input.nextLine();
							System.out.println("Entrez le code postal du client:");
							String postCode = input.nextLine();
							System.out.println("Entrez le courriel du client:");
							String courriel = input.nextLine();
							System.out.println("Le membre est-il un professionel? (oui/non)");
							String answer = input.nextLine();
							boolean isPro;
							switch(answer.toLowerCase()) {
								case "oui":
									isPro = true;
									break;
								case "non":
									isPro = false;
									break;
								default:
									System.out.println("R�ponse invalide. On assume qu'il ne s'agit pas d'un professionel.");
									isPro = false;
									break;
							}
							System.out.println("Cr�ation en cours...");
							num = menu.ajouterClient(nom,prenom,isPro,address,town,postCode,courriel);
							System.out.println("Cr�ation termin�e. Le num�ro est: "+ num);
							break;
						
						case "service":
							System.out.println("Entrez le nom du service:");
							String name = input.nextLine();
							System.out.println("Entrez la date � laquel le service aura lieu (aaaa-mm-jj-heure-minute):");
							String dateService = input.nextLine();
							System.out.println("Entrez le co�t du service (e.g: 5.5):");
							double frais = Double.parseDouble(input.nextLine());
							System.out.println("Entrez la capacit� maximale:");
							int capacite = Integer.parseInt(input.nextLine());
							System.out.println("Entrez le num�ro du professionel:");
							int numProfessionel = Integer.parseInt(input.nextLine());
							
							Client pro = menu.rechercherClient(numProfessionel);
							if(pro == null) {
								System.out.println("Num�ro inconnu. Annulation de l'op�ration.");
								break;
							}
							
							if(!pro.getIsPro()) {
								System.out.println("Ce client n'est pas un professionel. Annulation de l'op�ration.");
								break;
							}
							
							System.out.println("Entrez un commentaire (optionel):");
							String commentaire = input.nextLine();
							num = menu.addService(name, dateService, frais, commentaire, capacite, numProfessionel);
							System.out.println("Cr�ation termin�e. Le num�ro est: "+ num);
							break;
						
						default:
							System.out.println("R�ponse inconnue. Annulation.");
					}
					break;
				
				case "modifier":
					
					System.out.println("Modifier les informations d'un client ou d'un service? (client/service)");
					user = input.nextLine();
					switch(user.toLowerCase()) {
						
						case "client":
							
							System.out.println("Entrez le num�ro du client:");
							num = Integer.parseInt(input.nextLine());
							found = menu.rechercherClient(num);
							
							if(found != null) { 
								System.out.println("Client trouv�!");
								System.out.println("Entrez le nouveau nom (laissez blanc pour ne pas changer):");
								String nom = input.nextLine();
								System.out.println("Entrez le nouveau pr�nom (laissez blanc pour ne pas changer):");
								String prenom = input.nextLine();
								System.out.println("Entrez la nouvelle adresse (laissez blanc pour ne pas changer):");
								String address = input.nextLine();
								System.out.println("Entrez la nouvelle ville (laissez blanc pour ne pas changer):");
								String town = input.nextLine();
								System.out.println("Entrez le nouveau code postale (laissez blanc pour ne pas changer):");
								String postCode = input.nextLine();
								System.out.println("Entrez le nouveau courriel (laissez blanc pour ne pas changer):");
								String courriel = input.nextLine();
								
								menu.maJourClient(found, nom, prenom, address, town, postCode, courriel);
							
							} else {
								System.out.println("Client introuvable...");

							}
							
							break;
						
						case "service":
							System.out.println("Entrez le num�ro du service:");
							num = Integer.parseInt(input.nextLine());
							Service found2 = menu.services.getService(num);
							
							if(found2 != null) {
								System.out.println("Service trouv�! ("+ found2.getName() + ")");
								System.out.println("Entrez le nouveau nom du service (laissez blanc pour ne pas changer):");
								String name = input.nextLine();
								System.out.println("Entrez les nouveaux frais du service (laissez blanc pour ne pas changer):");
								String frais = input.nextLine();
								System.out.println("Entrez la nouvelle capacit� maximale (laissez blanc pour ne pas changer):");
								String capacite = input.nextLine();
								System.out.println("Entrez la nouvelle date de service (aaaa-mm-jj-heure-minute ou laissez blanc pour ne pas changer):");
								String newLocalDate = input.nextLine();
								System.out.println("Entrez un nouveau commentaire (laissez blanc pour ne pas changer):");
								String newComment = input.nextLine();
								
								menu.maJourService(found2,name,frais,capacite,newLocalDate, newComment);
								
							} else {
								System.out.println("Service introuvable...");

							}
							
							break;
					}
					break;
				
				case "supprimer":
				
					System.out.println("Supprimer un client ou un service? (client/service)");
					user = input.nextLine();
					switch(user.toLowerCase()) {
						case "client":
							
							System.out.println("Entrez le num�ro du client:");
							num = Integer.parseInt(input.nextLine());
							found = menu.rechercherClient(num);
							
							if(found != null) {
								System.out.println("Client trouv�!");
								System.out.println("Confirmer la suppression? (oui/non)");
								user = input.nextLine();
								
								switch(user.toLowerCase()) {
									case "oui":
										menu.supprimerClient(found);
										break;
									
									case "non":
										break;
										
									default:
										break;
								}
								
							} else {
								System.out.println("Client introuvable...");
							}
							break;
						
						case "service":
							
							System.out.println("Entrez le num�ro du client:");
							num = Integer.parseInt(input.nextLine());
							Service found2 = menu.services.getService(num);
							if(found2 != null) {
								System.out.println("Confirmer la suppression? (oui/non)");
								user = input.nextLine();
								
								switch(user.toLowerCase()) {
									case "oui":
										services.deleteService(found2);
										break;
									
									case "non":
										break;
										
									default:
										break;
								}
							} else {
								System.out.println("Service introuvable...");
							}
							break;
					}
					break;
				
				case "trouver":
					System.out.println("Entrez le jour (jj): ");
					int jour = Integer.parseInt(input.nextLine());
					System.out.println("Entrez le mois (mm): ");
					int mois = Integer.parseInt(input.nextLine());
					System.out.println("Entrez l'ann�e (aaaa): ");
					int ans = Integer.parseInt(input.nextLine());
					
					ArrayList<Service> toShow = menu.trouverService(jour,mois,ans);
					
					if(toShow.size() == 0) {
						System.out.println("Aucun service ce jour.");
						break;
						
					}
					
					for (Service service : toShow) {
						System.out.println("Service num�ro "+service.getCodeService()+" � lieu le "+service.getDateService().toString());
						
					}
					
					break;
				
				case "inscrire":
					
					System.out.println("Entrez le num�ro du client: ");
					int numClient = Integer.parseInt(input.nextLine());
					Client toIns = menu.rechercherClient(numClient);
					if(toIns == null) {
						System.out.println("Client introuvable");
						break;
					}
					System.out.println("Client trouv�!");
					System.out.println("Entrez le num�ro du service: ");
					int numService = Integer.parseInt(input.nextLine());
					Service service = menu.rechercherService(numService);
					if(service == null) {
						System.out.println("Service introuvable");
						break;
					}
					
					service.addList(toIns);
					toIns.addListService(service);
					System.out.println("Inscription termin�e!");
					
					break;
				
				case "confirmer":
					System.out.println("Entrez le num�ro du client: ");
					numClient = Integer.parseInt(input.nextLine());
					toIns = menu.rechercherClient(numClient);
					if(toIns == null) {
						System.out.println("Client introuvable");
						break;
					}
					System.out.println("Client trouv�!");
					System.out.println("Entrez le num�ro du service: ");
					numService = Integer.parseInt(input.nextLine());
					service = menu.rechercherService(numService);
					if(service == null) {
						System.out.println("Service introuvable");
						break;
					}
					
					service.addListConf(toIns);
					System.out.println("Pr�sence confirm�e!");
					
					break;
				
				case "consulter":
					
					System.out.println("Entrez le num�ro du service: ");
					numService = Integer.parseInt(input.nextLine());
					service = menu.rechercherService(numService);
					if(service == null) {
						System.out.println("Service introuvable");
						break;
					}
					System.out.println("Service trouv�!");
					System.out.println("Nombre de personnes inscrites: " + service.getListeMembre().size());
					System.out.println("Nombre de personnes confirm�es: " + service.getListMembreConf().size());
					
					break;
				
				case "rechercher":
					System.out.println("Chercher un client ou un service? (client/service)");
					user = input.nextLine();
					switch(user.toLowerCase()) {
						
						case "client":
							System.out.println("Entrez le num�ro du client:");
							num = Integer.parseInt(input.nextLine());
							found = menu.rechercherClient(num);
							
							if(found != null) {
								System.out.println("Client trouv�!");
								System.out.println(found.getPrenom()+" "+found.getNom());
								System.out.println("Inscrit depuis: "+found.getDateInscription().toString());
								if(found.getIsPro()) {
									System.out.println("Le client est enregistr� comme professionel");
								}
								
							} else {
								System.out.println("Client introuvable..."); 
							}
							break;
							
						
						case "service":
							System.out.println("Entrez le num�ro du service:");
							num = Integer.parseInt(input.nextLine());
							Service found2 = menu.rechercherService(num);
							
							if(found2 != null) {
								System.out.println("Service trouv�!");
								System.out.println("Frais: $" + found2.getFrais());
								System.out.println("LocalDate: " + found2.getDateService().toString());
								System.out.println("Capacit� maximale: " + found2.getCapacite());
								System.out.println("Nombre d'inscrits actuels: " + found2.getListeMembre().size());
								System.out.println("Nombre de clients ayant confirm� leur pr�sence: " + found2.getListMembreConf().size());
								
								if(!(found2.getCommentaire().equals(""))) {
									System.out.println("Commentaire: " + found2.getCommentaire());
									
								}
								
							}
							
							break;
					}
					break;
				
				case "synthese":
					AutoProc.genererSynthese(menu.clients, menu.services);
					break;
				
				case "aide":
					System.out.println(helpMsg);
					break;
				
				case "proc_comptable": // Cette commande n'est pas r�f�renc�e dans l'aide, et sert � d�clencher procComptable() pour debug
					AutoProc.procComptable(menu.clients, menu.services);
					break;
				
				default:
					System.out.println("Commande inconnue. Entrez \"Aide\" pour afficher les commandes disponibles.");
					break;
			}
		}
	}
}