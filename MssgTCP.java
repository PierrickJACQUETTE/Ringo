import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;

public class MssgTCP {

	protected static Entite insertNouveauTCP(Entite entite) {
		try {
			Socket socket_tcp = new Socket(entite.getAddrNext(1), entite.getPortTCPOut());
			BufferedReader br = new BufferedReader(new InputStreamReader(socket_tcp.getInputStream()));
			PrintWriter tcp_pw = new PrintWriter(new OutputStreamWriter(socket_tcp.getOutputStream()));

			// WELC
			String message = receiveTCP(br, entite, tcp_pw, socket_tcp);
			analyseMssg(message, tcp_pw);
			message = Annexe.substringLast(message);
			String[] parts = message.split(" ");
			entite.setAddrNext(parts[1], 1);
			entite.setPortOutUDP(Integer.parseInt(parts[2]), 1);
			entite.setAddrMultiDiff(parts[3], 1);
			entite.setPortMultiDiff(Integer.parseInt(parts[4]), 1);

			// newc
			String newc = "NEWC " + Annexe.trouveAdress() + " " + entite.getPortInUDP() + "\n";
			sendTCP(newc, tcp_pw);

			// ackc
			message = receiveTCP(br, entite, tcp_pw, socket_tcp);
			analyseMssg(message, tcp_pw);
			message = Annexe.substringLast(message);

			closeTCP(entite, false, br, tcp_pw, socket_tcp);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LengthException e) {
			e.getMessage();
			System.exit(0);
		} catch (NotSDLException e) {
			e.getMessage();
			System.exit(0);
		}
		return entite;
	}

	protected static Entite insertAnneauTCP(Entite entite, ServerSocketChannel tcp_in_ssc) throws IOException {
		if (Main.affichage) {
			System.out.println("Evenement sur TCP");
		}
		Socket sock_tcp = tcp_in_ssc.socket().accept();
		if (Main.affichage) {
			System.out.println("Acceptation TCP");
		}
		BufferedReader tcp_br = new BufferedReader(new InputStreamReader(sock_tcp.getInputStream()));
		PrintWriter tcp_pw = new PrintWriter(new OutputStreamWriter(sock_tcp.getOutputStream()));

		// WELC
		String welc = "WELC " + entite.getAddrNext(1) + " " + entite.getPortOutUDP(1) + " " + entite.getAddrMultiDiff(1)
				+ " " + entite.getPortMultiDiff(1) + "\n";
		sendTCP(welc, tcp_pw);

		// newc
		String lu = receiveTCP(tcp_br, entite, tcp_pw, sock_tcp);
		lu = Annexe.substringLast(lu);
		String parts[] = lu.split(" ");
		String futurAddrUDPOut = parts[1];
		String futurPortUDPOut = parts[2];

		// ackc
		String ackc = "ACKC\n";
		sendTCP(ackc, tcp_pw);

		entite.setAddrNext(futurAddrUDPOut, 1);
		entite.setPortOutUDP(Integer.parseInt(futurPortUDPOut), 1);

		closeTCP(entite, false, tcp_br, tcp_pw, sock_tcp);
		return entite;
	}

	private static void closeTCP(Entite entite, boolean erreur, BufferedReader tcp_br, PrintWriter tcp_pw,
			Socket sock_tcp) {
		try {
			tcp_br.close();
			tcp_pw.close();
			sock_tcp.close();
			if (Main.affichage) {
				System.out.println("Fin de connection TCP");
				if (erreur) {
					System.out.println("Erreur lors de l'insertion donc pas d'insertion -- entite inchange\n");
				} else {
					entite.printEntiteSimple();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void sendTCP(String envoi, PrintWriter tcp_pw) {
		tcp_pw.print(envoi);
		tcp_pw.flush();
		if (Main.affichage) {
			System.out.println("Envoi de : " + envoi);
		}
	}

	private static String receiveTCP(BufferedReader br, Entite entite, PrintWriter tcp_pw, Socket sc) {
		try {

			char[] data = new char[512];
			int mssg;
			mssg = br.read(data);
			boolean erreur = false;
			if (mssg != -1) {
				String message = new String(data, 0, mssg);
				if (Main.affichage) {
					System.out.println("Recu de : " + message);
				}
				return message;
			} else {
				erreur = true;
				closeTCP(entite, erreur, br, tcp_pw, sc);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}

	private static void analyseMssg(String mssg, PrintWriter pw) throws LengthException, NotSDLException {
		String parts[] = mssg.split(" ");
		if (parts[0].equals("WELC")) {
			int longeur = 5;
			suiteAnalyseMssg(longeur, mssg, parts);
		} else if (parts[0].equals("ACKC")) {
			int longeur = 1;
			suiteAnalyseMssg(longeur, mssg, parts);
		}
	}

	private static void suiteAnalyseMssg(int longeur, String mssg, String[] parts)
			throws LengthException, NotSDLException {
		if (parts.length != longeur) {
			throw new LengthException(longeur, parts.length, "TCP", mssg);
		}
		if (!(parts[longeur - 1].substring(parts[longeur - 1].length() - 1, parts[longeur - 1].length())
				.equals("\n"))) {
			throw new NotSDLException(mssg, "TCP");
		}
	}
}
