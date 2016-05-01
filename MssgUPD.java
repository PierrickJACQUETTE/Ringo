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
			buff.clear();
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

	private static void removeMssg(String idm, Entite entite) {
		entite.getMssgTransmisAnneau1().remove(idm);
		if (Main.affichage) {
			System.out.println("Remove mssg with this idm : " + idm + " from this entity : " + entite.getIdentifiant());
		}
		String newIden = Annexe.newIdentifiant();
		String message = "SUPP " + newIden + " " + idm;
		sendUDP(message, entite, newIden);
	}

	private static void mssgWHO(String message, String[] parts, Entite entite) {
		String idm = parts[1];
		if (entite.getMssgTransmisAnneau1().contains(idm)) {
			removeMssg(idm, entite);
		} else {
			sendUDP(message, entite, idm);
			entite.getMssgTransmisAnneau1().add(idm);
		}
		String idmM = Annexe.newIdentifiant();
		message = "MEMB " + idmM + " " + entite.getIdentifiant() + " " + Annexe.trouveAdress() + " "
				+ entite.getPortInUDP();
		sendUDP(message, entite, idm);
		entite.getMssgTransmisAnneau1().add(idmM);
	}

	private static void mssgMEMB(String message, String[] parts, Entite entite) {
		String idm = parts[1];
		if (entite.getMssgTransmisAnneau1().contains(idm)) {
			removeMssg(idm, entite);
		} else {
			sendUDP(message, entite, idm);
			entite.getMssgTransmisAnneau1().add(idm);
			System.out.println("\nDans l'anneau est present : ");
			System.out.println("Une entite avec cette identifiant : " + parts[2]);
			System.out.println("Qui a comme port : " + parts[4] + " et comme addresse : " + parts[3] + "\n");
		}
	}

	private static void mssgGBYE(String message, String[] parts, Entite entite) {
		String idm = parts[1];
		if (entite.getMssgTransmisAnneau1().contains(idm)) {
			removeMssg(idm, entite);
		} else if (Annexe.trouveAdress().equals(parts[2]) && entite.getPortOutUDP(1) == Integer.parseInt(parts[3])) {
			Entite tmp = new Entite();
			tmp.setAddrNext(entite.getAddrNext(1), 1);
			tmp.setPortOutUDP(entite.getPortOutUDP(1), 1);
			entite.setAddrNext(parts[4], 1);
			entite.setPortOutUDP(Integer.parseInt(parts[5]), 1);
			String idmNew = Annexe.newIdentifiant();
			message = "EYBG" + " " + idmNew;
			sendUDP(message, tmp, idm);
		} else {
			sendUDP(message, entite, idm);
		}
	}

	private static void mssgEYBG(String message, String[] parts, Entite entite) {
		System.exit(0);
	}

	private static void mssgTEST(String message, String[] parts, Entite entite) {
		String idm = parts[1];
		if (entite.getMssgTransmisAnneau1().contains(idm)) {
			removeMssg(idm, entite);
			entite.getALL().remove(Long.parseLong(idm));
			System.out.println("\nL anneau est en parfait etat\n");
		} else if (!parts[2].equals(entite.getAddrMultiDiff(1)) && !parts[3].equals(entite.getPortMultiDiff(1))) {
			System.out.println("J'ai recu un message TEST mais ils n'appartient pas à mon anneau");
		} else {
			sendUDP(message, entite, idm);
			entite.getMssgTransmisAnneau1().add(idm);
		}
	}

	private static void mssgSUPP(String message, String[] parts, Entite entite) {
		String idm = parts[2];
		if (entite.getMssgTransmisAnneau1().contains(idm)) {
			removeMssg(idm, entite);
			sendUDP(message, entite, idm);
		}

	}

	private static void mssgAPPL(String message, String[] parts, Entite entite) {
		String idm = parts[1];
		String messageDIFF = "";
		for(int i=4;i<parts.length;i++){
			messageDIFF+= parts[i]+" ";
		}
		System.out.println("\nJ'ai recu le mssg APPL est le message est : \n"+messageDIFF+"\n");
		if (entite.getMssgTransmisAnneau1().contains(idm)) {
			removeMssg(idm, entite);
		} else {
			sendUDP(message, entite, idm);
		}
	}

	protected static void sendUDP(String tmp, Entite entite, String idm) {
		try {
			DatagramSocket dso = new DatagramSocket();
			byte[] data;
			data = tmp.getBytes();
			InetSocketAddress ia = new InetSocketAddress(entite.getAddrNext(1), entite.getPortOutUDP(1));
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
			int longeur = 2;
			if (parts.length == longeur) {
				suiteAnalyseMssg(longeur, str, parts);
			}
		} else if (parts[0].equals("MEMB") && isPrivate == true) {
			int longeur = 5;
			if (parts.length == longeur) {
				suiteAnalyseMssg(longeur, str, parts);
			}
		} else if (parts[0].equals("GBYE")) {
			int longeur = 6;
			if (parts.length == longeur) {
				suiteAnalyseMssg(longeur, str, parts);
			}
		} else if (parts[0].equals("TEST")) {
			int longeur = 4;
			if (parts.length == longeur) {
				suiteAnalyseMssg(longeur, str, parts);
			}

		} else if (parts[0].equals("APPL")) {
			int longeur = 5;
			if (parts.length == longeur) {
				suiteAnalyseMssg(longeur, str, parts);
			}
			if (!parts[2].equals("DIFF####")) {
				throw new MssgSpellCheck(parts[2]);
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
			throw new MssgSpellCheck(parts[0]);
		}
	}

	protected static void suiteAnalyseMssg(int longeur, String mssg, String[] parts) throws LengthException {
		if (parts.length != longeur) {
			throw new LengthException(longeur, parts.length, "UDP", mssg);
		}
	}
}
