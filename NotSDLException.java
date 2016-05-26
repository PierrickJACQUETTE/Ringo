public class NotSDLException extends Exception {

	public NotSDLException(String message, String ou) {
		System.out.println(
				"Erreur dans la synthaxe du message en " + ou + " il manque le \\n : dans le message " + message);
	}
}