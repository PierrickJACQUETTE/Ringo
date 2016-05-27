import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;

public class MssgUPD {

	protected static Entite receveUDP(Entite entite, DatagramChannel udp_in_dc, ByteBuffer buff) {
		if (Main.affichage) {
			System.out.println("Message UDP recu");
		}
		try {
			udp_in_dc.receive(buff);
			String st = new String(buff.array(), 0, buff.array().length);
			if (Main.affichage) {
				System.out.println("Message recu : " + st);
			}
			if (st.contains("TRANS### ROK") && st.startsWith("APPL")) {

			} else {
				st = st.trim();
			}
			analyseMssg(st, buff.array(), true);
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
				mssgAPPL(buff.array(), parts, entite);
			}

		} catch (LengthException | MssgSpellCheck e) {
			if (Main.affichage) {
				e.getMessage();
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
		return entite;
	}

	private static void sendSUPP(Entite entite, String idm) {
		String newIden = Annexe.newIdentifiant();
		String message = "SUPP " + newIden + " " + idm;
		sendUDP(message.getBytes(), entite, newIden, 1);
	}

	private static void removeMssg(String idm, Entite entite, int anneau) {
		if (anneau == 1 && entite.getIsDuplicateur() == false) {
			Mssg m = new Mssg(idm);
			m.my_remove(entite.getMssgTransmisAnneau1());
			if (Main.affichage) {
				System.out.println(
						"Remove mssg with this idm : " + idm + " from this entity : " + entite.getIdentifiant());
			}
		}
	}

	private static void mssgWHO(String message, String[] parts, Entite entite) {
		String idm = parts[1];
		String idmM = Annexe.newIdentifiant();
		String message2 = "MEMB " + idmM + " " + entite.getIdentifiant() + " " + Annexe.trouveAdress(true) + " "
				+ entite.getPortInUDP();
		Mssg m = new Mssg(idm);
		if (m.my_contains(entite.getMssgTransmisAnneau1())) {
			if (entite.getIsDuplicateur() == false) {
				removeMssg(idm, entite, 1);
				sendSUPP(entite, idm);
			}
		} else {
			sendUDP(message.getBytes(), entite, idm, 1);
			entite.getMssgTransmisAnneau1().add(new Mssg(idm));
			sendUDP(message2.getBytes(), entite, idmM, 1);
			entite.getMssgTransmisAnneau1().add(new Mssg(idmM));
			membPrint(message2.split(" "));
		}
		if (entite.getIsDuplicateur() == true) {
			if (m.my_contains(entite.getMssgTransmisAnneau2())) {
			} else {
				sendUDP(message.getBytes(), entite, idm, 2);
				entite.getMssgTransmisAnneau2().add(new Mssg(idm));
				sendUDP(message2.getBytes(), entite, idmM, 2);
				entite.getMssgTransmisAnneau2().add(new Mssg(idmM));
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
		Mssg m = new Mssg(idm);
		if (m.my_contains(entite.getMssgTransmisAnneau1())) {
			if (entite.getIsDuplicateur() == false) {
				removeMssg(idm, entite, 1);
				sendSUPP(entite, idm);
			}
		} else {
			membPrint(parts);
			sendUDP(message.getBytes(), entite, idm, 1);
			entite.getMssgTransmisAnneau1().add(new Mssg(idm));
		}
		if (entite.getIsDuplicateur() == true) {
			if (m.my_contains(entite.getMssgTransmisAnneau2())) {
			} else {
				sendUDP(message.getBytes(), entite, idm, 2);
				entite.getMssgTransmisAnneau2().add(new Mssg(idm));
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
		sendUDP(message.getBytes(), tmp, parts[1], anneau);
		if (anneau == 1) {
			entite.getMssgTransmisAnneau1().add(new Mssg(idmNew));
		} else if (anneau == 2) {
			entite.getMssgTransmisAnneau2().add(new Mssg(idmNew));
		}
	}

	private static void mssgGBYE(String message, String[] parts, Entite entite) {
		String idm = parts[1];
		int anneau = 1;
		Mssg m = new Mssg(idm);
		if (m.my_contains(entite.getMssgTransmisAnneau1())) {
			if (entite.getIsDuplicateur() == false) {
				removeMssg(idm, entite, anneau);
				sendSUPP(entite, idm);
			}
		} else if (entite.getAddrNext(anneau).equals(parts[2])
				&& entite.getPortOutUDP(anneau) == Integer.parseInt(parts[3])) {
			mssgGBYE(entite, parts, anneau);
		} else {
			sendUDP(message.getBytes(), entite, idm, anneau);
			entite.getMssgTransmisAnneau1().add(new Mssg(idm));
		}
		if (entite.getIsDuplicateur() == true) {
			anneau = 2;
			if (m.my_contains(entite.getMssgTransmisAnneau2())) {
			} else if (entite.getAddrNext(anneau).equals(parts[2])
					&& entite.getPortOutUDP(anneau) == Integer.parseInt(parts[3])) {
				mssgGBYE(entite, parts, anneau);
			} else {
				sendUDP(message.getBytes(), entite, idm, anneau);
				entite.getMssgTransmisAnneau2().add(new Mssg(idm));
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
		Mssg m = new Mssg(idm);
		if (m.my_contains(entite.getMssgTransmisAnneau1())) {
			ArrayList<Mssg> aLM = entite.getMssgTransmisAnneau1();
			Mssg m1 = aLM.get(m.position(aLM));
			String idmTmp = m1.getIdm();
			m1.my_remove(aLM);
			aLM.add(new Mssg(idmTmp));
			entite.setMssgTransmisAnneau1(aLM);
			if (entite.getIsDuplicateur() == false) {
				removeMssg(idm, entite, anneau);
				sendSUPP(entite, idm);
			}
			one = true;
			System.out.println("\nL anneau est en parfait etat\n");
		} else if (!parts[2].equals(entite.getAddrMultiDiff(anneau))
				&& !parts[3].equals(entite.getPortMultiDiff(anneau))) {
			System.out.println("J'ai recu un message TEST mais ils n'appartient pas a cet anneau");
		} else {
			sendUDP(message.getBytes(), entite, idm, anneau);
			entite.getMssgTransmisAnneau1().add(new Mssg(idm));
		}
		if (entite.getIsDuplicateur() == true) {
			anneau = 2;
			if (m.my_contains(entite.getMssgTransmisAnneau2()) && one == false) {
				ArrayList<Mssg> aLM = entite.getMssgTransmisAnneau2();
				Mssg m1 = aLM.get(m.position(aLM));
				String idmTmp = m1.getIdm();
				m1.my_remove(aLM);
				aLM.add(new Mssg(idmTmp));
				entite.setMssgTransmisAnneau2(aLM);
				System.out.println("\nL anneau est en parfait etat\n");
			} else if (!parts[2].equals(entite.getAddrMultiDiff(anneau))
					&& !parts[3].equals(entite.getPortMultiDiff(anneau))) {
				System.out.println("J'ai recu un message TEST mais ils n'appartient pas a cet anneau");
			} else {
				sendUDP(message.getBytes(), entite, idm, anneau);
				entite.getMssgTransmisAnneau2().add(new Mssg(idm));
			}
		}
	}

	private static void mssgSUPP(String message, String[] parts, Entite entite) {
		String idm = parts[2];
		Mssg m = new Mssg(idm);
		if (m.my_contains(entite.getMssgTransmisAnneau1())) {
			if (entite.getIsDuplicateur() == false) {
				removeMssg(idm, entite, 1);
			}
			sendUDP(message.getBytes(), entite, idm, 1);
		}
		if (entite.getIsDuplicateur() == true) {
			if (m.my_contains(entite.getMssgTransmisAnneau2())) {
				sendUDP(message.getBytes(), entite, idm, 2);
			}
		}
	}

	private static void mssgAPPL(byte[] message, String[] parts, Entite entite) {
		String idm = parts[1];
		boolean retransmet = true;
		if (parts[2].equals("DIFF####")) {
			String messageDIFF = new String(corpsDuMssgAPPL(message, 4));
			System.out.println("\nJ'ai recu le mssg APPL est le message est : \n" + messageDIFF + "\n");
		} else if (parts[2].equals("TRANS###")) {
			retransmet = mssgAPPLTRANS(message, entite);
		}
		Mssg m = new Mssg(idm);
		if (m.my_contains(entite.getMssgTransmisAnneau1())) {
			if (entite.getIsDuplicateur() == false) {
				removeMssg(idm, entite, 1);
				sendSUPP(entite, idm);
			}
			if (parts[3].equals("REQ")) {
				System.out.println("Aucune entite n'a le fichier");
			} else if (parts[3].equals("ROK")) {
				System.out.println("Aucune entite ne voulait le fichier : " + parts[6]);
			}
		} else if (retransmet == true) {
			sendUDP(message, entite, idm, 1);
			entite.getMssgTransmisAnneau1().add(new Mssg(idm));
		}
		if (entite.getIsDuplicateur() == true) {
			if (m.my_contains(entite.getMssgTransmisAnneau2())) {
			} else if (retransmet == true) {
				sendUDP(message, entite, idm, 2);
				entite.getMssgTransmisAnneau2().add(new Mssg(idm));
			}
		}
	}

	private static byte[] corpsDuMssgAPPL(byte[] buffer, int debut) {
		 String[] parts = new String(buffer).split(" ");
		 int debutReel = 0;
		 for (int i = 0; i < debut; i++) {
		 	debutReel += parts[i].length() + 1;
		 }
		 byte[] res = new byte[buffer.length - debutReel];
		 for (int j = 0; j < res.length; j++) {
		 	res[j] = buffer[j + debutReel];
		 }
		 return res;
	}

	private static boolean mssgAPPLTRANS(byte[] buffer, Entite entite) {
		String[] parts = new String(buffer).trim().split(" ");
		boolean retransmet = true;
		switch (parts[3]) {

		case "REQ":
			if (Annexe.listerRepertoire(parts[5])) {
				retransmet = false;

				// int nbMssg = Annexe.nbrMssgApplTrans(parts[5]);
				String nbMssg = Annexe.toLittleEndian(Annexe.nbrMssgApplTrans(parts[5]) + ""); // Little
																								// endian


				//String nbMssg = ""+Annexe.nbrMssgApplTrans(parts[5]);
				String idm = Annexe.newIdentifiant();
				String message = "APPL " + idm + " TRANS### ROK ";
				String idmM = Annexe.newIdentifiant();
				message += idmM + " " + parts[4] + " " + parts[5] + " " + nbMssg;

				sendAnneau(message.getBytes(), entite, idmM, false);
				mssgAPPLSEN(idmM, parts[5], entite);
			}
			break;

		case "ROK":
			parts = new String(buffer).split(" ");
			MssgApplDemande m = new MssgApplDemande(parts[6], "");
			if (m.my_contains(entite.getDemandeFichier())) {
				int pos = m.position(entite.getDemandeFichier());
				entite.getDemandeFichier().get(pos).setIdTrans(parts[4]);
				String string = new String(Annexe.littleEndianTo(parts[7])); // Litttle
				// endian
				entite.getDemandeFichier().get(pos).setNmbDeMssg(Integer.parseInt(string.trim()));
				retransmet = false;
			}
			break;

		case "SEN":
			if (MssgApplDemande.my_contains(entite.getDemandeFichier(), parts[4])) {
				retransmet = false;
				int pos = MssgApplDemande.position(entite.getDemandeFichier(), parts[4]);
				m = entite.getDemandeFichier().get(pos);
				String string = new String(Annexe.littleEndianTo(parts[5])); // little
				// endian;
				string = string.trim();
				if (m.getNumeroMssgRecu() + 1 == Integer.parseInt(string)) {
					m.setNumeroMssgRecu(Integer.parseInt(string));
					String tmp = new String(buffer);
					tmp = tmp.substring(49);
					System.out.println(tmp + " "+ Integer.parseInt(parts[6]));
					m.addContenu(tmp.getBytes(), Integer.parseInt(parts[6].trim()));
					if (m.getNmbDeMssg() - 1 == m.getNumeroMssgRecu()) {
						File fileOut = new File(m.getIdm());
						FileOutputStream fos;
						try {
							fos = new FileOutputStream(fileOut);
							fos.write(m.getContenu());
							fos.close();
							System.out.println("Le fichier est recupere");
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} else {
					System.out.println("Transfert de fichier, probleme dans la reception de l'ordre des mssg");
					System.out.println("Je recommence");
					String mssgDeDemande = m.getMssgReq();
					String decoupeDebut = mssgDeDemande.substring(0, 4);
					String decoupeFin = mssgDeDemande.substring(14, mssgDeDemande.length());
					String idmNew = Annexe.newIdentifiant();
					String finale = decoupeDebut + " " + idmNew + " " + decoupeFin;
					sendAnneau(finale.getBytes(), entite, idmNew, true);
				}
			}
			break;
		}
		return retransmet;
	}

	private static void mssgAPPLSEN(String idmM, String nameFichier, Entite entite) {
		String message = "APPL ";
		String messageSuite = " TRANS### SEN " + idmM + " ";
		File fileIn = new File(nameFichier);
		try {
			BufferedInputStream fis = new BufferedInputStream(new FileInputStream(fileIn));
			byte[] buffer = new byte[Annexe.sizeBuffApplSend()];
			int total = fis.available();
			int ou = 0;
			int i;
			for (i = 0; i < total / buffer.length; i++) {
				ou = sendAPPLTexte(fis, buffer, message, messageSuite, ou, i, entite);
			}
			buffer = new byte[total - ou];
			sendAPPLTexte(fis, buffer, message, messageSuite, ou, i, entite);
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static int sendAPPLTexte(BufferedInputStream fis, byte[] buffer, String message, String mssgSuite, int ou,
			int i, Entite entite) {
		try {
			fis.read(buffer);
			String idm = Annexe.newIdentifiant();
			int size = buffer.length;
			String taille = Annexe.remplirZero(size, 3);

			// message += idm + mssgSuite + i + " " + taille + " " + new
			// String(buffer);
			message += idm + mssgSuite + Annexe.toLittleEndian(i + "") + " " + taille + " ";
			byte[] tmp = message.getBytes();
			byte[] mssgFinal = new byte[tmp.length + buffer.length];
			int i2 = 0;
			for (; i2 < tmp.length; i2++) {
				mssgFinal[i2] = tmp[i2];
			}
			for (int j = 0; j < buffer.length; j++) {
				mssgFinal[i2 + j] = buffer[j];
			}
			sendAnneau(mssgFinal, entite, idm, false);
			ou += buffer.length;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ou;
	}

	private static void sendAnneau(byte[] message, Entite entite, String idm, boolean fichier) {
		sendUDP(message, entite, idm, 1);
		if (fichier == false) {
			entite.getMssgTransmisAnneau1().add(new Mssg(idm));
		} else {
			entite.getMssgTransmisAnneau1().add(new MssgApplDemande(idm, new String(message)));
		}
		if (entite.getIsDuplicateur() == true) {
			sendUDP(message, entite, idm, 2);
			if (fichier == false) {
				entite.getMssgTransmisAnneau2().add(new Mssg(idm));
			} else {
				entite.getMssgTransmisAnneau2().add(new MssgApplDemande(idm, new String(message)));
			}
		}
	}

	protected static void sendUDP(byte[] tmp, Entite entite, String idm, int anneau) {
		try {
			DatagramSocket dso = new DatagramSocket();
			byte[] data;
			data = tmp;
			InetSocketAddress ia = new InetSocketAddress(entite.getAddrNext(anneau), entite.getPortOutUDP(anneau));
			DatagramPacket paquet = new DatagramPacket(data, data.length, ia);
			dso.send(paquet);
			if (Main.affichage) {
				System.out.println("Message envoye : " + new String(tmp));
			}
			dso.close();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected static void analyseMssg(String str, byte[] buffer, boolean isPrivate)
			throws LengthException, MssgSpellCheck {
		String parts[] = str.split(" ");
		if (buffer.length > Main.SIZEMESSG) {
			throw new LengthException(str.length(), str, "UDP");
		}
		if (parts[0].equals("WHOS") || (parts[0].equals("EYBG") && isPrivate == true)) {
			suiteAnalyseMssgInf(2, str, parts);
		} else if (parts[0].equals("MEMB") && isPrivate == true) {
			suiteAnalyseMssgDiff(5, str, parts);
		} else if (parts[0].equals("GBYE")) {
			suiteAnalyseMssgDiff(6, str, parts);
		} else if (parts[0].equals("TEST")) {
			suiteAnalyseMssgDiff(4, str, parts);
		} else if (parts[0].equals("APPL")) {
			if (!parts[2].equals("DIFF####") && !parts[2].equals("TRANS###")) {
				throw new MssgSpellCheck(parts[2], "UDP");
			} else if (isPrivate == true) {
				suiteAnalyseMssgInf(5, str, parts);
				if (parts[2].equals("TRANS###")) {
					int longeur = 0;
					switch (parts[3]) {

					case "REQ":
						longeur = 6;
						break;

					case "ROK":
					case "SEN":
						longeur = 8;
						break;

					default:
						throw new MssgSpellCheck(parts[3], "UDP");
					}
					suiteAnalyseMssgInf(longeur, str, parts);
				}
			} else {
				int longeur = 3;
				if (parts.length < longeur) {
					throw new LengthException(longeur, parts.length, "UDP", str);
				}
			}
		} else if (parts[0].equals("SUPP") && isPrivate == true) {
			suiteAnalyseMssgDiff(3, str, parts);
		} else {
			throw new MssgSpellCheck(parts[0], "UDP");
		}
	}

	protected static void suiteAnalyseMssgDiff(int longeur, String mssg, String[] parts) throws LengthException {
		if (parts.length != longeur) {
			throw new LengthException(longeur, parts.length, "UDP", mssg);
		}
	}

	protected static void suiteAnalyseMssgInf(int longeur, String mssg, String[] parts) throws LengthException {
		if (parts.length < longeur) {
			throw new LengthException(longeur, parts.length, "UDP", mssg);
		}
	}
}
