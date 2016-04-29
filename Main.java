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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

	static String option;
	public static boolean affichage;

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
			System.out.println("Voulez un Nouveau anneau ou Joindre un anneau existant ?");
			System.out.println("Taper N ou J ");
			String reponse = sc.nextLine();
			if (reponse.equals("N")) {
				correctAction = true;

				entite.setPortMultiDiff(7003, 1);
				entite.setAddrMultiDiff("239.255.000.003", 1);
				entite.setPortInUDP(7001);
				entite.setPortOutUDP(7001);
				entite.setPortTCPIn(7000);

				entite = VerifEntree.nouveauAnneau(entite, sc);
			} else if (reponse.equals("J")) {
				correctAction = true;
				System.out.println("Voulez un U ou D ?");
				reponse = sc.nextLine();
				if (reponse.equals("U")) {
					entite.setAddrNext("127.000.000.001");
					entite.setPortTCPOut(7000);
					entite.setPortTCPIn(6999);
					entite.setPortInUDP(7002);
					entite.setPortOutUDP(7005);
				} else {

					entite.setAddrNext("127.000.000.006");
					entite.setPortTCPOut(6999);
					entite.setPortTCPIn(6000);
					entite.setPortInUDP(6001);
					entite.setPortOutUDP(6002);

				}
				entite = VerifEntree.rejoindreAnneau(entite, sc, affichage);
				entite = MssgTCP.insertNouveauTCP(affichage, entite);
			} else {
				System.out.println("Erreur de frappe, recommencez");
			}
		}
		try {
			Selector selector = Selector.open();
			// --------------- TCP NON BLOQUANT ------------
			ServerSocketChannel tcp_in_ssc = ServerSocketChannel.open();
			tcp_in_ssc.configureBlocking(false);
			ServerSocket ss = tcp_in_ssc.socket();
			ss.bind(new InetSocketAddress(entite.getPortTCPIn()));
			tcp_in_ssc.register(selector, SelectionKey.OP_ACCEPT);
			// -----------------------------------------------

			// ------------------ UDP NON BLOQUANT ------------
			DatagramChannel udp_in_dc = DatagramChannel.open();
			udp_in_dc.configureBlocking(false);
			udp_in_dc.bind(new InetSocketAddress(entite.getPortInUDP()));
			udp_in_dc.register(selector, SelectionKey.OP_READ);
			// ------------------------------------------------

			// ------------ MULTI DIFF NON BLOQUANT -----------
			NetworkInterface interf = NetworkInterface.getByName("eth0"); // wlan0
			InetAddress group = (Inet4Address) InetAddress.getByName(entite.getAddrMultiDiff(1));
			DatagramChannel udp_multi_dc;
			udp_multi_dc = DatagramChannel.open(StandardProtocolFamily.INET)
					.setOption(StandardSocketOptions.SO_REUSEADDR, true)
					.bind(new InetSocketAddress(entite.getPortMultiDiff(1)))
					.setOption(StandardSocketOptions.IP_MULTICAST_IF, interf);
			udp_multi_dc.configureBlocking(false);
			udp_multi_dc.register(selector, SelectionKey.OP_READ);
			MembershipKey key = udp_multi_dc.join(group, interf);
			// ------------------------------------------------

			ByteBuffer buff = ByteBuffer.allocate(512);

			Thread scanner_in = new Thread(entite);
			scanner_in.start();

			while (true) {
				if (affichage) {
					System.out.println("Waiting for messages");
				}
				final Entite entite1 = entite;
				TimerTask task = new TimerTask() {
					@Override
					public void run() {
						MssgMultDiff.declencheMultiDiff(entite1, affichage);
					}
				};

				Timer timer = new Timer();
				TimeTest t = new TimeTest("", 0);
				timer.scheduleAtFixedRate(task, 0, t.getMaxTime());

				selector.select();
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while (it.hasNext()) {
					SelectionKey sk = it.next();
					it.remove();
					if (sk.isReadable() && sk.channel() == udp_multi_dc) {
						MssgMultDiff.receiveMultiDiff(affichage, buff, udp_multi_dc);
					}
					if (sk.isReadable() && sk.channel() == udp_in_dc) {
						entite = MssgUPD.receveUDP(entite, affichage, udp_in_dc, buff);
					} else if (sk.isAcceptable() && sk.channel() == tcp_in_ssc) {
						entite = MssgTCP.insertAnneauTCP(affichage, entite, tcp_in_ssc);
					} else {
						System.out.println("Que s'est il passe");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
