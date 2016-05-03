public class LengthException extends Exception {
	public LengthException(int lengthAttendu, int lengthReel, String ou, String message) {
		System.out.println("Erreur dans le nombre d'arguments en " + ou + " nombre d'arguments trouve : " + lengthReel
				+ " et " + lengthAttendu + " sont attendus pour le message " + message);
		if (Main.affichage) {
			System.out.println("Je ne transfere pas ce message");
		}
	}

	public LengthException(int lengthReel, String message, String ou) {
		System.out.println("Erreur dans la longueur du message en " + ou + " vaut :" + lengthReel + " et "
				+ Main.SIZEMESSG + " sont attendus pour le message " + message);
		if (Main.affichage) {
			System.out.println("Je ne transfere pas ce message");
		}
	}

}