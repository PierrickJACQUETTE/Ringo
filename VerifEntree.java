import java.util.Scanner;

public class VerifEntree {

	protected static Entite nouveauAnneau(Entite entite, Scanner sc) {
		String reponse = "";
		entite = Annexe.initEntite(entite, sc);
		entite.setAddrNext(Annexe.trouveAdress(), 1);
		boolean correct = false;

		// while (!correct) {
		// System.out.println("Veuillez entrer le numero du port de multi-diff :
		// ");
		// reponse = sc.nextLine();
		// correct = Annexe.verifNombre(reponse, true);
		// }
		// entite.setPortMultiDiff(Annexe.entier(reponse), 1);
		// correct = false;
		// while (!correct) {
		// System.out.println("Veuillez entrer l'adresse multi-diff : ");
		// reponse = sc.nextLine();
		// correct = Annexe.verifAddress(reponse, true);
		// }
		// entite.setAddrMultiDiff(Annexe.convertIPV4Complete(reponse), 1);

		System.out.println("Vous venez de créer une entité avec ses attributs : ");
		entite.printEntiteSimple();
		return entite;
	}

	protected static Entite rejoindreAnneau(Entite entite, Scanner sc) {
		String reponse = "";
		entite = Annexe.initEntite(entite, sc);
		//
		// boolean correct = false; while (!correct) { System.out.println(
		// "Veuillez entrer l'adresse Ip de l'entité ou se connecter: ");
		// reponse = sc.nextLine(); correct = Annexe.verifAddress(reponse,
		// false); } entite.setAddrNext(Annexe.convertIPV4Complete(reponse),1);
		// correct = false; while (!correct) { System.out.println(
		// "Veuillez entrer le port TCP de l'entité ou se connecter: "); reponse
		// = sc.nextLine(); correct = Annexe.verifNombre(reponse, false); }
		// entite.setPortTCPOut(Annexe.entier(reponse));
		//

		if (Main.affichage) {
			System.out.println("Vous venez de créer une entité avec ses attributs : ");
			entite.printEntiteSimple();
		}
		return entite;
	}
}
