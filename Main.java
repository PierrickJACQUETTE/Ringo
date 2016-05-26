import java.util.Scanner;

//ip addr show
//ifconfig

public class Main {

	private static String option;
	protected static boolean affichage;
	protected static final long TIMEMAX = 20000;
	protected static final int SIZEMESSG = 512;
	protected static final String TYPECONN = "eth0"; // bond0 ou wlan0

	public static void main(String[] args) {
		affichage = false;
		if (args.length >= 1) {
			option = args[0];
			if (option.equals("--debeug")) {
				affichage = true;
			}
		}
		Scanner sc = new Scanner(System.in);
		boolean correctAction = false;
		Entite entite = new Entite();
		while (!correctAction) {
			System.out.println("Voulez un Nouveau anneau ou Joindre ou Duppliquer un anneau existant ?");
			System.out.println("Taper N ou J ou D");
			String reponse = sc.nextLine();
			if (reponse.equals("N")) {
				correctAction = true;
				entite = VerifEntree.nouveauAnneau(entite, sc);
			} else if (reponse.equals("J") || reponse.equals("D")) {
				correctAction = true;
				boolean joindre = false;
				if (reponse.equals("J")) {
					joindre = true;
				}

				entite = VerifEntree.rejoindreAnneau(entite, sc, joindre);
				entite = MssgTCP.insertNouveauTCP(entite, joindre);
			} else {
				System.out.println("Erreur de frappe, recommencez");
			}
		}
		Anneau anneau = new Anneau(entite);
		anneau.anneau();
	}
}
