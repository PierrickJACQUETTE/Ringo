
public class LengthException extends Exception {
	public LengthException(int lengthAttendu, int lengthReel, String ou, String message) {
		System.out.println("Erreur dans le nombre d'arguments en " + ou + " nombre d'arguments trouve : " + lengthReel
				+ " et " + lengthAttendu + " sont attendus pour le message " + message);
	}

}