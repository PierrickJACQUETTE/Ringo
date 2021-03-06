import java.util.Scanner;

public class VerifEntree {

	private static Entite initMultidiff(Entite entite, Scanner sc, int anneau) {

		String reponse = "";
		boolean correct = false;
		while (!correct) {
			System.out.println("Veuillez entrer le numero du port de multi-diff:");
			reponse = sc.nextLine();
			correct = Annexe.verifNombre(reponse, true);
		}
		entite.setPortMultiDiff(Annexe.entier(reponse), anneau);
		correct = false;
		while (!correct) {
			System.out.println("Veuillez entrer l'adresse multi-diff : ");
			reponse = sc.nextLine();
			correct = Annexe.verifAddress(reponse, true, true);
		}
		entite.setAddrMultiDiff(Annexe.convertIPV4Complete(reponse), anneau);

		return entite;
	}

	protected static Entite nouveauAnneau(Entite entite, Scanner sc) {
		String reponse = "";
		entite = Annexe.initEntite(entite, sc);
		entite.setAddrNext(Annexe.trouveAdress(true), 1);

		entite = initMultidiff(entite, sc, 1);

		printNewEntite(entite);
		return entite;
	}

	protected static Entite rejoindreAnneau(Entite entite, Scanner sc, boolean joindre) {
		String reponse = "";
		entite = Annexe.initEntite(entite, sc);

		boolean correct = false;
		while (!correct) {
			System.out.println("Veuillez entrer l'adresse Ip de l'entité ou se connecter: ");
			reponse = sc.nextLine();
			correct = Annexe.verifAddress(reponse, false, false);
		}
		entite.setAddrNext(Annexe.convertIPV4Complete(reponse), 1);
		correct = false;
		while (!correct) {
			System.out.println("Veuillez entrer le port TCP de l'entité ou se connecter: ");
			reponse = sc.nextLine();
			correct = Annexe.verifNombre(reponse, false);
		}
		entite.setPortTCPOut(Annexe.entier(reponse));

		if (joindre == false) {
			entite = initMultidiff(entite, sc, 1);
		}

		printNewEntite(entite);
		return entite;
	}

	private static void printNewEntite(Entite entite) {
		if (Main.affichage) {
			System.out.println("Vous venez de créer une entité avec ses attributs : ");
			entite.printEntiteSimple();
		}
	}
}
