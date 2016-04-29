import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Scanner;
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
		String[] parts = tmp.split(".");
		int fisrt = Integer.parseInt(parts[0]);
		if (fisrt < 224 || fisrt > 239) {
			return false;
		}
		return true;
	}

	private static String addZero(String str, int nbrZero) {
		String res = "";
		for (int i = 0; i < nbrZero; i++) {
			res += "0";
		}
		return res + str;
	}
	
	protected static String newIdentifiant(){
		return new String(""+System.currentTimeMillis());
	}
	
	protected static String removeWhite(String tmp){
		return tmp.replaceAll("[\t ]" ,"");
	}

	protected static String substringLast(String str) {
		return str.substring(0, str.length() - 1);
	}

	protected static String convertIPV4Complete(String textAddr) {
		String[] parts = textAddr.split("\\.");
		String addrComplete = "";
		for (String s : parts) {
			int i = Integer.parseInt(s);
			if ((i >= 0) && (i < 10)) {
				addrComplete += addZero(s, 2);
			} else if (i >= 10 && i < 99) {
				addrComplete += addZero(s, 1);
			} else {
				addrComplete += addZero(s, 0);
			}
			addrComplete += ".";
		}
		return addrComplete.substring(0, addrComplete.length() - 1);
	}

	protected static String trouveAdress() {
		try {
			Enumeration<NetworkInterface> listNi = NetworkInterface.getNetworkInterfaces();
			while (listNi.hasMoreElements()) {
				NetworkInterface nic = listNi.nextElement();
				if (!nic.getName().equals("lo")) { // eth0
					Enumeration<InetAddress> listIa = nic.getInetAddresses();
					while (listIa.hasMoreElements()) {
						InetAddress iac = listIa.nextElement();
						if (iac instanceof Inet4Address) {
							return convertIPV4Complete(iac.toString().substring(1));
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

	protected static boolean verifAddress(String str, boolean isIPV4) {
		try {
			if (isIPV4) {
				InetAddress i = (Inet4Address) InetAddress.getByName(str);
				return isGoodAddrMultiDiff(i);
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

	protected static Entite initEntite(Entite entite, Scanner sc) {
		/*
		 * boolean correct = false; String reponse = ""; while (!correct) {
		 * System.out.println("Veuillez entrer son numero du port UDP : ");
		 * reponse = sc.nextLine(); correct = Annexe.verifNombre(reponse, true);
		 * } entite.setPortInUDP(Annexe.entier(reponse));
		 * entite.setPortOutUDP(Annexe.entier(reponse)); correct = false; while
		 * (!correct) { System.out.println(
		 * "Veuillez entrer son numero du port TCP : "); reponse =
		 * sc.nextLine(); correct = Annexe.verifNombre(reponse, false); }
		 * entite.setPortTCPIn(Annexe.entier(reponse));
		 */

		String tmp = entite.getPortInUDP() + "" + entite.getPortTCPIn();
		entite.setIdentifiant(tmp);
		return entite;
	}

}
