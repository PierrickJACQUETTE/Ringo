import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;

public class MssgTCP {

	protected static Entite insertNouveauTCP(Entite entite, boolean joindre) {
		try {
			Socket socket_tcp = new Socket(entite.getAddrNext(1), entite.getPortTCPOut());
			BufferedReader br = new BufferedReader(new InputStreamReader(socket_tcp.getInputStream()));
			PrintWriter tcp_pw = new PrintWriter(new OutputStreamWriter(socket_tcp.getOutputStream()));

			// WELC ou NOTC
			String message = receiveTCP(br, entite, tcp_pw, socket_tcp);
			analyseMssg(message);
			message = Annexe.substringLast(message);

			String[] parts = message.split(" ");
			if (parts[0].equals("WELC")) {
				if (joindre == true) {
					entite.setAddrNext(parts[1], 1);
					entite.setPortOutUDP(Integer.parseInt(parts[2]), 1);
					entite.setAddrMultiDiff(parts[3], 1);
					entite.setPortMultiDiff(Integer.parseInt(parts[4]), 1);
				}

				String send = "";
				if (joindre == true) {
					send = "NEWC " + Annexe.trouveAdress(true) + " " + entite.getPortInUDP() + "\n";
				} else {
					send = "DUPL " + Annexe.trouveAdress(true) + " " + entite.getPortInUDP() + " "
							+ entite.getAddrMultiDiff(1) + " " + entite.getPortMultiDiff(1) + "\n";
				}
				analyseMssg(send);
				sendTCP(send, tcp_pw);

				// ACKC ou ACKD
				message = receiveTCP(br, entite, tcp_pw, socket_tcp);
				analyseMssg(message);
				message = Annexe.substringLast(message);
				if (joindre == false) {
					parts = message.split(" ");
					entite.setPortOutUDP(Integer.parseInt(parts[1]), 1);
				}
				closeTCP(entite, br, tcp_pw, socket_tcp);
				print(false, entite);
			} else {
				System.out.println("Entite ou se connecter est deja un doubleur");
				closeTCP(entite, br, tcp_pw, socket_tcp);
				print(true, entite);
				System.exit(0);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (MssgSpellCheck e) {
			e.getMessage();
		} catch (LengthException e) {
			e.getMessage();
			System.exit(0);
		} catch (NotSDLException e) {
			e.getMessage();
			System.exit(0);
		}
		return entite;
	}

	protected static Entite insertAnneauTCP(Entite entite, ServerSocketChannel tcp_in_ssc) {
		if (Main.affichage) {
			System.out.println("Evenement sur TCP");
		}
		try {
			Socket sock_tcp = tcp_in_ssc.socket().accept();
			if (Main.affichage) {
				System.out.println("Acceptation TCP");
			}
			BufferedReader tcp_br = new BufferedReader(new InputStreamReader(sock_tcp.getInputStream()));
			PrintWriter tcp_pw = new PrintWriter(new OutputStreamWriter(sock_tcp.getOutputStream()));

			String mssg = "";
			if (entite.getIsDuplicateur() == false) {
				// WELC
				mssg = "WELC " + entite.getAddrNext(1) + " " + entite.getPortOutUDP(1) + " "
						+ entite.getAddrMultiDiff(1) + " " + entite.getPortMultiDiff(1) + "\n";
			} else {
				// NOTC
				mssg = "NOTC\n";
			}
			analyseMssg(mssg);
			sendTCP(mssg, tcp_pw);

			if (entite.getIsDuplicateur() == false) {
				// NEWC ou DUPL
				String lu = receiveTCP(tcp_br, entite, tcp_pw, sock_tcp);
				analyseMssg(lu);
				lu = Annexe.substringLast(lu);
				String parts[] = lu.split(" ");
				String futurAddrUDPOut = parts[1];
				String futurPortUDPOut = parts[2];
				int anneau = 1;
				String futurMultiDiffAddr = "";
				String futurMultiDiffPort = "";
				String envoi = "";
				boolean demandeDupplication = false;
				if (parts[0].equals("NEWC")) {
					envoi = "ACKC\n";
				} else if (parts[0].equals("DUPL")) {
					anneau = 2;
					futurMultiDiffAddr = parts[3];
					futurMultiDiffPort = parts[4];
					envoi = "ACKD " + entite.getPortInUDP() + "\n";
					demandeDupplication = true;
				}
				analyseMssg(envoi);
				sendTCP(envoi, tcp_pw);

				entite.setAddrNext(futurAddrUDPOut, anneau);
				entite.setPortOutUDP(Integer.parseInt(futurPortUDPOut), anneau);
				if (parts[0].equals("DUPL")) {
					entite.setAddrMultiDiff(futurMultiDiffAddr, 2);
					entite.setPortMultiDiff(Integer.parseInt(futurMultiDiffPort), 2);
				}

				closeTCP(entite, tcp_br, tcp_pw, sock_tcp);
				entite.setIsDuplicateur(demandeDupplication);
				print(false, entite);
			} else {
				closeTCP(entite, tcp_br, tcp_pw, sock_tcp);
				print(true, entite);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MssgSpellCheck e) {
			e.getMessage();
		} catch (LengthException e) {
			e.getMessage();
		} catch (NotSDLException e) {
			e.getMessage();
		}
		return entite;
	}

	private static void print(boolean erreur, Entite entite) {
		if (Main.affichage) {
			if (erreur) {
				System.out.println("Erreur lors de l'insertion donc pas d'insertion -- entite inchange\n");
			} else {
				entite.printEntiteSimple();
			}
		}
	}

	private static void closeTCP(Entite entite, BufferedReader tcp_br, PrintWriter tcp_pw, Socket sock_tcp) {
		try {
			tcp_br.close();
			tcp_pw.close();
			sock_tcp.close();
			if (Main.affichage) {
				System.out.println("Fin de connection TCP");
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
				closeTCP(entite, br, tcp_pw, sc);
				print(erreur, entite);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}

	private static void analyseMssg(String mssg) throws LengthException, NotSDLException, MssgSpellCheck {
		String parts[] = mssg.split(" ");
		if (mssg.length() > Main.SIZEMESSG) {
			throw new LengthException(mssg.length(), mssg, "TCP");
		}
		if (parts[0].equals("WELC") || parts[0].equals("DUPL")) {
			suiteAnalyseMssg(5, mssg, parts);
		} else if (parts[0].equals("ACKC\n") || parts[0].equals("NOTC\n")) {
			suiteAnalyseMssg(1, mssg, parts);
		} else if (parts[0].equals("ACKD")) {
			suiteAnalyseMssg(2, mssg, parts);
		} else if (parts[0].equals("NEWC")) {
			suiteAnalyseMssg(3, mssg, parts);
		} else {
			throw new MssgSpellCheck(parts[0], "TCP");
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
