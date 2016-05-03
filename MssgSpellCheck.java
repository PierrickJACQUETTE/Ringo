public class MssgSpellCheck extends Exception {
	public MssgSpellCheck(String message, String ou) {
		System.out.println("Erreur type de message inconnue en " + ou + ": " + message);
		if (Main.affichage) {
			System.out.println("Je ne transfere pas ce message");
		}
	}
}
