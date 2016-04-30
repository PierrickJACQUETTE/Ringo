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
			char[] data = new char[512];
			int mssg = br.read(data);
			String message = new String(data, 0, mssg);
			// WELC
			if (Main.affichage) {
				System.out.println("Recu de : " + message);
			}
			analyseMssg(message, tcp_pw);
			message = Annexe.substringLast(message);
			String[] parts = message.split(" ");
			entite.setAddrNext(parts[1], 1);
			entite.setPortOutUDP(Integer.parseInt(parts[2]), 1);
			entite.setAddrMultiDiff(parts[3], 1);
			entite.setPortMultiDiff(Integer.parseInt(parts[4]), 1);
			String newc = "NEWC " + Annexe.trouveAdress() + " " + entite.getPortInUDP() + "\n";
			tcp_pw.print(newc);
			tcp_pw.flush();
			if (Main.affichage) {
				System.out.println("Envoi de : " + newc);
			}
			// ackc
			mssg = br.read(data);
			message = new String(data, 0, mssg);
			if (Main.affichage) {
				System.out.println("Recu de : " + message);
			}
			analyseMssg(message, tcp_pw);
			message = Annexe.substringLast(message);
			socket_tcp.close();
			if (Main.affichage) {
				System.out.println("Fin de connection TCP");
				entite.printEntiteSimple();
			}
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
		PrintWriter tcp_pw = new PrintWriter(new OutputStreamWriter(sock_tcp.getOutputStream()));
		String welc = "WELC " + entite.getAddrNext(1) + " " + entite.getPortOutUDP(1) + " " + entite.getAddrMultiDiff(1)
				+ " " + entite.getPortMultiDiff(1) + "\n";
		tcp_pw.print(welc);
		tcp_pw.flush();
		if (Main.affichage) {
			System.out.println("Envoi de : " + welc);
		}
		BufferedReader tcp_br = new BufferedReader(new InputStreamReader(sock_tcp.getInputStream()));
		// newc
		char[] data = new char[512];
		int mssg = tcp_br.read(data);
		boolean erreur = false;
		if (mssg != -1) {
			String lu = new String(data, 0, mssg);
			if (Main.affichage) {
				System.out.println("Recu  de : " + lu);
			}
			lu = Annexe.substringLast(lu);
			String parts[] = lu.split(" ");
			String futurAddrUDPOut = parts[1];
			String futurPortUDPOut = parts[2];
			String ackc = "ACKC\n";
			tcp_pw.print(ackc);
			tcp_pw.flush();
			if (Main.affichage) {
				System.out.println("Envoi de : " + ackc);
			}
			entite.setAddrNext(futurAddrUDPOut, 1);
			entite.setPortOutUDP(Integer.parseInt(futurPortUDPOut), 1);
		} else {
			erreur = true;
		}
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
		return entite;
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
