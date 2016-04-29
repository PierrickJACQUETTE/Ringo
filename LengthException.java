
public class LengthException extends Exception {
	public LengthException(String message, int lengthAttendu, int lengthReel, String ou) {
		System.out.println("Erreur dans le nombre d'arguments en " + ou + " nombre d'arguments trouve : " + lengthReel
				+ " et " + lengthAttendu + " sont attendus pour le type de message " + message);
	}

}