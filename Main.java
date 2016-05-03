import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class Main {

	private static String option;
	protected static boolean affichage;
	protected static final long TIMEMAX = 20000;
	protected static final int SIZEMESSG = 512;

	public static void main(String[] args) {
		affichage = false;
		// if (args.length >= 1) {
		// option = args[1];
		// if (option.equals("--debeug")) {
		affichage = true;
		// }
		// }
		Scanner sc = new Scanner(System.in);
		boolean correctAction = false;
		Entite entite = new Entite();
		while (!correctAction) {
			System.out.println("Voulez un Nouveau anneau ou Joindre ou Duppliquer un anneau existant ?");
			System.out.println("Taper N ou J ou D");
			String reponse = sc.nextLine();
			if (reponse.equals("N")) {
				correctAction = true;

				entite.setPortMultiDiff(7003, 1);
				entite.setAddrMultiDiff("238.255.000.003", 1);
				entite.setPortInUDP(7001);
				entite.setPortOutUDP(7001, 1);
				entite.setPortTCPIn(7000);

				entite = VerifEntree.nouveauAnneau(entite, sc);
			} else if (reponse.equals("J") || reponse.equals("D")) {
				correctAction = true;
				boolean joindre = false;
				if (reponse.equals("J")) {
					joindre = true;
				}

				System.out.println("Voulez un A ou Z ou E?");
				reponse = sc.nextLine();
				if (reponse.equals("A")) {
					entite.setAddrNext("127.000.000.001", 1);
					entite.setPortTCPOut(7000);
					entite.setPortTCPIn(6999);
					entite.setPortInUDP(7002);
					entite.setPortOutUDP(7005, 1);
					if (joindre == false) {
						entite.setPortMultiDiff(7007, 1);
						entite.setAddrMultiDiff("238.255.000.005", 1);
					}
				} else if (reponse.equals("Z")) {
					entite.setAddrNext("127.000.000.006", 1);
					entite.setPortTCPOut(7000);
					entite.setPortTCPIn(7008);
					entite.setPortInUDP(6001);
					entite.setPortOutUDP(6002, 1);
					if (joindre == false) {
						entite.setPortMultiDiff(7007, 1);
						entite.setAddrMultiDiff("238.255.000.004", 1);
					}

				} else {
					entite.setAddrNext("127.000.000.006", 1);
					entite.setPortTCPOut(6999);
					entite.setPortTCPIn(7009);
					entite.setPortInUDP(6005);
					entite.setPortOutUDP(6009, 1);
				}
				entite = VerifEntree.rejoindreAnneau(entite, sc, joindre);
				entite = MssgTCP.insertNouveauTCP(entite, joindre);
			} else {
				System.out.println("Erreur de frappe, recommencez");
			}
		}
		Anneau anneau = new Anneau(entite);
		anneau.anneau();
	}
}
