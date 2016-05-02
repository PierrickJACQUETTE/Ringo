import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class MssgUPD {

	protected static Entite receveUDP(Entite entite, DatagramChannel udp_in_dc, ByteBuffer buff) {
		if (Main.affichage) {
			System.out.println("Message UDP recu");
		}
		try {
			udp_in_dc.receive(buff);
			String st = new String(buff.array(), 0, buff.array().length).trim();
			if (Main.affichage) {
				System.out.println("Message recu : " + st);
			}
			analyseMssg(st, true);
			String parts[] = st.split(" ");
			if (parts[0].equals("WHOS")) {
				mssgWHO(st, parts, entite);
			} else if (parts[0].equals("MEMB")) {
				mssgMEMB(st, parts, entite);
			} else if (parts[0].equals("GBYE")) {
				mssgGBYE(st, parts, entite);
			} else if (parts[0].equals("EYBG")) {
				mssgEYBG(st, parts, entite);
			} else if (parts[0].equals("TEST")) {
				mssgTEST(st, parts, entite);
			} else if (parts[0].equals("SUPP")) {
				mssgSUPP(st, parts, entite);
			} else if (parts[0].equals("APPL")) {
				mssgAPPL(st, parts, entite);
			}

		} catch (LengthException | MssgSpellCheck e) {
			if (Main.affichage) {
				System.out.println("Je ne transfere pas ce message");
				e.getMessage();
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
		return entite;
	}

	private static void removeMssg(String idm, Entite entite, int anneau) {
		// if (anneau == 1 && entite.getIsDuplicateur()==false) {
		// entite.getMssgTransmisAnneau1().remove(idm);
		// if (Main.affichage) {
		// System.out.println("Remove mssg with this idm : " + idm + " from this
		// entity : " + entite.getIdentifiant());
		// }
		// String newIden = Annexe.newIdentifiant();
		// String message = "SUPP " + newIden + " " + idm;
		// sendUDP(message, entite, newIden, 1);
		// }
	}

	private static void mssgWHO(String message, String[] parts, Entite entite) {
		String idm = parts[1];
		String idmM = Annexe.newIdentifiant();
		String message2 = "MEMB " + idmM + " " + entite.getIdentifiant() + " " + Annexe.trouveAdress() + " "
				+ entite.getPortInUDP();
		if (entite.getMssgTransmisAnneau1().contains(idm)) {
			removeMssg(idm, entite, 1);
		} else {
			sendUDP(message, entite, idm, 1);
			entite.getMssgTransmisAnneau1().add(idm);
			sendUDP(message2, entite, idmM, 1);
			entite.getMssgTransmisAnneau1().add(idmM);
			membPrint(message2.split(" "));
		}
		if (entite.getIsDuplicateur() == true) {
			if (entite.getMssgTransmisAnneau2().contains(idm)) {
				removeMssg(idm, entite, 2);
			} else {
				sendUDP(message, entite, idm, 2);
				entite.getMssgTransmisAnneau2().add(idm);
				sendUDP(message2, entite, idmM, 2);
				entite.getMssgTransmisAnneau2().add(idmM);
			}
		}
	}

	protected static void membPrint(String[] parts) {
		System.out.println("\nDans l'anneau est present : ");
		System.out.println("Une entite avec cette identifiant : " + parts[2]);
		System.out.println("Qui a comme port : " + parts[4] + " et comme addresse : " + parts[3] + "\n");
	}

	private static void mssgMEMB(String message, String[] parts, Entite entite) {
		String idm = parts[1];
		if (entite.getMssgTransmisAnneau1().contains(idm)) {
			removeMssg(idm, entite, 1);
		} else {
			membPrint(parts);
			sendUDP(message, entite, idm, 1);
			entite.getMssgTransmisAnneau1().add(idm);
		}
		if (entite.getIsDuplicateur() == true) {
			if (entite.getMssgTransmisAnneau2().contains(idm)) {
				removeMssg(idm, entite, 2);
			} else {
				sendUDP(message, entite, idm, 2);
				entite.getMssgTransmisAnneau2().add(idm);
			}
		}
	}

	private static void mssgGBYE(Entite entite, String[] parts, int anneau) {
		Entite tmp = new Entite();
		tmp.setAddrNext(entite.getAddrNext(anneau), anneau);
		tmp.setPortOutUDP(entite.getPortOutUDP(anneau), anneau);
		entite.setAddrNext(parts[4], anneau);
		entite.setPortOutUDP(Integer.parseInt(parts[5]), anneau);
		String idmNew = Annexe.newIdentifiant();
		String message = "EYBG" + " " + idmNew;
		sendUDP(message, tmp, parts[1], anneau);
		if (anneau == 1) {
			entite.getMssgTransmisAnneau1().add(idmNew);
		} else if (anneau == 2) {
			entite.getMssgTransmisAnneau2().add(idmNew);
		}
	}

	private static void mssgGBYE(String message, String[] parts, Entite entite) {
		String idm = parts[1];
		int anneau = 1;
		if (entite.getMssgTransmisAnneau1().contains(idm)) {
			removeMssg(idm, entite, anneau);
		} else if (Annexe.trouveAdress().equals(parts[2])
				&& entite.getPortOutUDP(anneau) == Integer.parseInt(parts[3])) {
			mssgGBYE(entite, parts, anneau);
		} else {
			sendUDP(message, entite, idm, anneau);
			entite.getMssgTransmisAnneau1().add(idm);
		}
		if (entite.getIsDuplicateur() == true) {
			anneau = 2;
			if (entite.getMssgTransmisAnneau2().contains(idm)) {
				removeMssg(idm, entite, anneau);
			} else if (Annexe.trouveAdress().equals(parts[2])
					&& entite.getPortOutUDP(anneau) == Integer.parseInt(parts[3])) {
				mssgGBYE(entite, parts, anneau);
			} else {
				sendUDP(message, entite, idm, anneau);
				entite.getMssgTransmisAnneau2().add(idm);
			}
		}
	}

	private static void mssgEYBG(String message, String[] parts, Entite entite) {
		if (entite.getIsDuplicateur() == false) {
			System.exit(0);
		}
		if (entite.getIsDuplicateur() == true) {
			if (entite.getAlreadyReceivedEYBG() == true) {
				System.exit(0);
			} else {
				entite.setAlreadyReceivedEYBG(true);
			}
		}
	}

	private static void mssgTEST(String message, String[] parts, Entite entite) {
		String idm = parts[1];
		boolean one = false;
		int anneau = 1;
		if (entite.getMssgTransmisAnneau1().contains(idm)) {
			removeMssg(idm, entite, anneau);
			entite.getALL1().remove(Long.parseLong(idm));
			System.out.println("\nL anneau est en parfait etat\n");
		} else if (!parts[2].equals(entite.getAddrMultiDiff(anneau))
				&& !parts[3].equals(entite.getPortMultiDiff(anneau))) {
			System.out.println("J'ai recu un message TEST mais ils n'appartient pas a cet anneau");
		} else {
			sendUDP(message, entite, idm, anneau);
			entite.getMssgTransmisAnneau1().add(idm);
		}
		if (entite.getIsDuplicateur() == true) {
			anneau = 2;
			if (entite.getMssgTransmisAnneau2().contains(idm)) {
				removeMssg(idm, entite, anneau);
				entite.getALL2().remove(Long.parseLong(idm));
				System.out.println("\nL anneau est en parfait etat\n");
			} else if (!parts[2].equals(entite.getAddrMultiDiff(anneau))
					&& !parts[3].equals(entite.getPortMultiDiff(anneau))) {
				System.out.println("J'ai recu un message TEST mais ils n'appartient pas a cet anneau");
			} else {
				sendUDP(message, entite, idm, anneau);
				entite.getMssgTransmisAnneau2().add(idm);
			}
		}
	}

	private static void mssgSUPP(String message, String[] parts, Entite entite) {
		String idm = parts[2];
		if (entite.getMssgTransmisAnneau1().contains(idm)) {
			removeMssg(idm, entite, 1);
			sendUDP(message, entite, idm, 1);
		}
		if (entite.getIsDuplicateur() == true) {
			if (entite.getMssgTransmisAnneau2().contains(idm)) {
				removeMssg(idm, entite, 2);
				sendUDP(message, entite, idm, 2);
			}
		}

	}

	private static void mssgAPPL(String message, String[] parts, Entite entite) {
		String idm = parts[1];
		String messageDIFF = "";
		for (int i = 4; i < parts.length; i++) {
			messageDIFF += parts[i];
			if (i != parts.length) {
				messageDIFF += " ";
			}
		}
		System.out.println("\nJ'ai recu le mssg APPL est le message est : \n" + messageDIFF + "\n");
		if (entite.getMssgTransmisAnneau1().contains(idm)) {
			removeMssg(idm, entite, 1);
		} else {
			sendUDP(message, entite, idm, 1);
			entite.getMssgTransmisAnneau1().add(idm);
		}
		if (entite.getIsDuplicateur() == true) {
			if (entite.getMssgTransmisAnneau2().contains(idm)) {
				removeMssg(idm, entite, 2);
			} else {
				sendUDP(message, entite, idm, 2);
				entite.getMssgTransmisAnneau2().add(idm);

			}
		}
	}

	protected static void sendUDP(String tmp, Entite entite, String idm, int anneau) {
		try {
			DatagramSocket dso = new DatagramSocket();
			byte[] data;
			data = tmp.getBytes();
			InetSocketAddress ia = new InetSocketAddress(entite.getAddrNext(anneau), entite.getPortOutUDP(anneau));
			DatagramPacket paquet = new DatagramPacket(data, data.length, ia);
			dso.send(paquet);
			if (Main.affichage) {
				System.out.println("Message envoye : " + tmp);
			}
			dso.close();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected static void analyseMssg(String str, boolean isPrivate) throws LengthException, MssgSpellCheck {
		String parts[] = str.split(" ");
		if (parts[0].equals("WHOS") || (parts[0].equals("EYBG") && isPrivate == true)) {
			suiteAnalyseMssg(2, str, parts);
		} else if (parts[0].equals("MEMB") && isPrivate == true) {
			suiteAnalyseMssg(5, str, parts);
		} else if (parts[0].equals("GBYE")) {
			suiteAnalyseMssg(6, str, parts);
		} else if (parts[0].equals("TEST")) {
			suiteAnalyseMssg(4, str, parts);
		} else if (parts[0].equals("APPL")) {
			int longeur = 3;
			if (parts.length < longeur) {
				suiteAnalyseMssg(longeur, str, parts);
			}
			if (!parts[2].equals("DIFF####")) {
				throw new MssgSpellCheck(parts[2], "UDP");
			}
			longeur = 512 - 4 - 1 - 8 - 1 - 8 - 1 - 3 - 1;
			if (parts[4].length() > longeur) {
				throw new LengthException(longeur, parts[4].length(), str);
			}
		} else if (parts[0].equals("SUPP") && isPrivate == true) {
			int longeur = 3;
			if (parts.length == longeur) {
				suiteAnalyseMssg(longeur, str, parts);
			}
		} else {
			throw new MssgSpellCheck(parts[0], "UDP");
		}
	}

	protected static void suiteAnalyseMssg(int longeur, String mssg, String[] parts) throws LengthException {
		if (parts.length != longeur) {
			throw new LengthException(longeur, parts.length, "UDP", mssg);
		}
	}
}
