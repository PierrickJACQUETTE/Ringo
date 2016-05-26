import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Enumeration;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramSocket;
import java.net.Inet4Address;

public class Annexe {

	private static boolean isNumber(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	private static boolean inf9999(int i) {
		return (i < 9999) ? true : false;
	}

	private static boolean sup0(int i) {
		return (i > 0) ? true : false;
	}

	private static boolean sup1023(int i) {
		return (i > 1023) ? true : false;
	}

	private static boolean inf65636(int i) {
		return (i < 65636) ? true : false;
	}

	private static boolean isGoodAddrMultiDiff(InetAddress i) {
		String tmp = i.toString();
		String[] parts = tmp.split("\\.");
		int fisrt = Integer.parseInt(parts[0].replaceAll("/", ""));
		if (fisrt < 224 || fisrt > 239) {
			return false;
		}
		return true;
	}

	protected static boolean listerRepertoire(String name) {
		File repertoire = new File(".");
		String[] listefichiers;
		listefichiers = repertoire.list();
		for (int i = 0; i < listefichiers.length; i++) {
			if (listefichiers[i].equals(name)) {
				return true;
			}
		}
		return false;
	}

	protected static String remplirZero(int size, int sizeVoulu) {
		String taille = "" + size;
		taille = Annexe.addZero(taille, 16);
		taille = taille.substring(taille.length() - sizeVoulu, taille.length());
		return taille;
	}

	private static String addZero(String str, int nbrZero) {
		String res = "";
		for (int i = 0; i < nbrZero; i++) {
			res += "0";
		}
		return res + str;
	}

	protected static String removeWhite(String tmp) {
		return tmp.replaceAll("[\t ]", "");
	}

	protected static String substringLast(String str) {
		return str.substring(0, str.length() - 1);
	}

	protected static String serveur(String textAddr) {
		try {
			InetAddress ia = (Inet4Address) InetAddress.getByName(textAddr);
			String debut = "";
			if (ia.toString().charAt(0) != '/') {
				String[] partie = ia.toString().split("/");
				textAddr = partie[1];
				debut = partie[0] + "/";
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return textAddr;
	}

	private static String base(int i) {
		return Integer.toString(i, Character.MAX_RADIX);
	}

	protected static String convertIPV4Complete(String textAddr) {
		textAddr = serveur(textAddr);
		String[] parts = textAddr.split("\\.");
		String addrComplete = "";
		for (String s : parts) {
			int i = Integer.parseInt(s);
			if ((i >= 0) && (i < 10)) {
				addrComplete += addZero(i + "", 2);
			} else if (i >= 10 && i < 99) {
				addrComplete += addZero(i + "", 1);
			} else {
				addrComplete += addZero(i + "", 0);
			}
			addrComplete += ".";
		}
		return addrComplete.substring(0, addrComplete.length() - 1);
	}

	protected static String trouveAdress(boolean complete) {
		try {
			Enumeration<NetworkInterface> listNi = NetworkInterface.getNetworkInterfaces();
			while (listNi.hasMoreElements()) {
				NetworkInterface nic = listNi.nextElement();
				if (!nic.getName().equals("lo")) {
					Enumeration<InetAddress> listIa = nic.getInetAddresses();
					while (listIa.hasMoreElements()) {
						InetAddress iac = listIa.nextElement();
						if (iac instanceof Inet4Address) {
							if (complete == true) {
								return convertIPV4Complete(iac.toString().substring(1));
							} else {
								return iac.toString().substring(1);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected static boolean verifNombre(String str, boolean isUDP) {
		if (isNumber(str) == false) {
			System.out.println("Ce n'est pas un nombre.");
			return false;
		}
		int i = entier(str);
		if (sup0(i) == false) {
			System.out.println("Le nombre doit etre superieur a 0.");
			return false;
		}
		if (sup1023(i) == false) {
			System.out.println("Le nombre doit etre superieur a 1023.");
			return false;
		}
		if (isUDP) {
			if (inf9999(i) == false) {
				System.out.println("Le nombre doit etre inferieur a 9999.");
				return false;
			}
		} else {
			if (inf65636(i) == false) {
				System.out.println("Le nombre doit etre inferieur a 65636.");
				return false;
			}
		}
		return true;
	}

	private static boolean complAdrr(String str) {
		String[] parts = str.split("\\.");
		return (parts.length == 4) ? true : false;
	}

	protected static void waitAMssg() {
		System.out.println(
				"\nWaiting for messages : WHOS, GBYE, TEST, INFO [SIMPLE|COMPLEX], APPL [DIFF mess| TRANS nom_fichier]\n");
	}

	protected static boolean verifAddress(String str, boolean isIPV4, boolean multi) {
		try {
			if (isIPV4) {
				InetAddress i = (Inet4Address) InetAddress.getByName(str);
				if (i.toString().charAt(0) == '/') {
					if (complAdrr(str) == false) {
						return false;
					}
					if (multi) {
						return isGoodAddrMultiDiff(i);
					} else {
						return true;
					}
				}
			} else {
				InetAddress.getByName(str);
			}
		} catch (UnknownHostException e) {
			return false;
		}
		return true;
	}

	protected static int entier(String str) {
		return Integer.parseInt(str);
	}

	protected static int sizeBuffApplSend() {
		// APPL_8_TRANS###_SEN_8_8_3_ => 4+9+9+4+9+9+5 => 49
		return Main.SIZEMESSG - 49;
	}

	protected static int nbrMssgApplTrans(String name) {
		File fileIn = new File(name);
		FileInputStream fis;
		int res = 0;
		try {
			fis = new FileInputStream(fileIn);
			res = (fis.available() / sizeBuffApplSend()) + 1;
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	protected static String toLittleEndian(String str) {
		Long i = Long.parseLong(str);
		String res = null;
		try {
			ByteBuffer bb = ByteBuffer.allocate(8);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			bb.putLong(i);
			byte[] result = bb.array();
			res = new String(result, "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return res;
	}

	protected static String littleEndianTo(String str) {
		String res = null;
		try {
			byte[] tmp;
			tmp = str.getBytes("ISO-8859-1");
			ByteBuffer wrapped = ByteBuffer.wrap(tmp).order(ByteOrder.LITTLE_ENDIAN);
			res = "" + wrapped.getLong();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return res;
	}

	protected static String newIdentifiant() {
		return length8(new String("" + System.nanoTime()));
	}

	private static String length8(String tmp) {
		int coupure = 8;
		int debut;
		int suite = 0;
		if (tmp.length() > coupure) {
			debut = Integer.parseInt(tmp.substring(0, coupure));
			suite = Integer.parseInt(tmp.substring(coupure, tmp.length()));
		} else {
			debut = Integer.parseInt(tmp);
		}
		String str = base(debut);
		str += base(suite);

		if (str.length() > coupure) {
			str = str.substring(str.length() - coupure, str.length());
		} else {
			for (int c = str.length(); c < coupure; c++) {
				str += "0";
			}
		}
		return str;
	}

	private static String identifiantEntite(Entite entite) {
		String str = Annexe.serveur(trouveAdress(false));
		String tmp = "";
		String[] part = str.split("\\.");
		for (String s : part) {
			tmp += s;
		}
		tmp += entite.getPortTCPIn();

		return length8(tmp);
	}

	private static boolean testPortInUDP(int port) {
		String addr = trouveAdress(true);
		try {
			DatagramSocket ds = new DatagramSocket(port);
			InetSocketAddress ia = new InetSocketAddress(addr, port);
			ds.close();
		} catch (SocketException e) {
			System.out.println("Erreur BindException: Adresse déjà utilisée : essayer une autre addresse (PORT UDP)");
			return false;
		}
		return true;
	}

	private static boolean testPortInTCP(int port) {
		String addr = trouveAdress(true);
		try {
			ServerSocket server = new ServerSocket(port);
			Socket socket_tcp = new Socket(addr, port);
			socket_tcp.close();
			server.close();
		} catch (IOException e) {
			System.out.println("Erreur BindException: Adresse déjà utilisée : essayer une autre addresse (PORT TCP)");
			return false;
		}
		return true;
	}

	protected static Entite initEntite(Entite entite, Scanner sc) {

		boolean correct = false;
		String reponse = "";
		while (!correct) {
			System.out.println("Veuillez entrer son numero du port UDP : ");
			reponse = sc.nextLine();
			correct = verifNombre(reponse, true);
			if (correct == true) {
				correct = testPortInUDP(entier(reponse));
			}
		}
		entite.setPortInUDP(entier(reponse));
		entite.setPortOutUDP(entier(reponse), 1);
		correct = false;
		while (!correct) {
			System.out.println("Veuillez entrer son numero du port TCP : ");
			reponse = sc.nextLine();
			correct = verifNombre(reponse, false);
			if (correct == true) {
				correct = testPortInTCP(entier(reponse));
			}
		}
		entite.setPortTCPIn(entier(reponse));

		entite.setIdentifiant(identifiantEntite(entite));
		return entite;
	}

}
