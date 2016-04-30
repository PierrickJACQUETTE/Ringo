
public class LengthException extends Exception {
	public LengthException(int lengthAttendu, int lengthReel, String ou, String message) {
		System.out.println("Erreur dans le nombre d'arguments en " + ou + " nombre d'arguments trouve : " + lengthReel
				+ " et " + lengthAttendu + " sont attendus pour le message " + message);
	}

	public LengthException(int lengthAttendu, int lengthReel, String message) {
		System.out.println("Erreur dans la longueur du message en UPD vaut :" + lengthReel + " et " + lengthAttendu
				+ " sont attendus pour le message " + message);
	}

}