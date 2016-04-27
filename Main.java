import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
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

	static String option;
	static boolean affichage;

	public static void main(String[] args) {
		/*
		 * if (args.length >= 1) { option = args[1]; if
		 * (option.equals("--debeug")) {
		 */ affichage = true;
		/*
		 * } else { affichage = false; } }
		 */ Scanner sc = new Scanner(System.in);
		boolean correctAction = false;
		Entite entite = new Entite();
		while (!correctAction) {
			System.out.println("Voulez un Nouveau anneau ou Joindre un anneau existant ?");
			System.out.println("Taper N ou J ");
			String reponse = sc.nextLine();
			if (reponse.equals("N")) {
				correctAction = true;

				entite.setPortMultiDiff(7003, 1);
				entite.setAddrMultiDiff("239.255.000.002", 1);
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

				try {
					Socket socket_tcp = new Socket(entite.getAddrNext(), entite.getPortTCPOut());
					BufferedReader br = new BufferedReader(new InputStreamReader(socket_tcp.getInputStream()));
					String message = br.readLine();
					// WELC
					message = Annexe.substringLast(message);
					if (affichage) {
						System.out.println("Recu de : " + message);
					}
					String[] parts = message.split(" ");
					entite.setAddrNext(parts[1]);
					entite.setPortOutUDP(Integer.parseInt(parts[2]));
					entite.setAddrMultiDiff(parts[3], 1);
					entite.setPortMultiDiff(Integer.parseInt(parts[4]), 1);
					PrintWriter tcp_pw = new PrintWriter(new OutputStreamWriter(socket_tcp.getOutputStream()));
					String newc = "NEWC " + Annexe.trouveAdress() + " " + entite.getPortInUDP() + "\n";
					tcp_pw.print(newc);
					tcp_pw.flush();
					if (affichage) {
						System.out.println("Envoi de : " + newc);
					}
					// ackc
					message = br.readLine();
					message = Annexe.substringLast(message);
					if (affichage) {
						System.out.println("Recu de : " + message);
					}
					socket_tcp.close();
					if (affichage) {
						System.out.println("Fin de connection TCP");
						entite.printEntiteSimple();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
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
			InetAddress group = (Inet4Address) InetAddress.getByName(entite.getAddrMultiDiff(1).replaceAll("/", ""));
			DatagramChannel udp_multi_dc;
			udp_multi_dc = DatagramChannel.open(StandardProtocolFamily.INET)
					.setOption(StandardSocketOptions.SO_REUSEADDR, true)
					.bind(new InetSocketAddress(entite.getPortMultiDiff(1)))
					.setOption(StandardSocketOptions.IP_MULTICAST_IF, interf);
			udp_multi_dc.configureBlocking(false);
			udp_multi_dc.register(selector, SelectionKey.OP_READ);
			MembershipKey key = udp_multi_dc.join(group, interf);

			ByteBuffer buff = ByteBuffer.allocate(512);

			while (true) {
				if (affichage) {
					System.out.println("Waiting for messages");
				}
				selector.select();
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while (it.hasNext()) {
					SelectionKey sk = it.next();
					it.remove();
					if (sk.isReadable() && sk.channel() == udp_multi_dc) {
						if (affichage) {
							System.out.println("Message UDP multi diff recu");
						}
						udp_multi_dc.receive(buff);
						String st = new String(buff.array(), 0, buff.array().length);
						buff.clear();
						if (affichage) {
							System.out.println("Message :" + st);
						}
					}
					if (sk.isReadable() && sk.channel() == udp_in_dc) {
						if (affichage) {
							System.out.println("Message UDP  recu");
						}
						udp_in_dc.receive(buff);
						String st = new String(buff.array(), 0, buff.array().length);
						if (affichage) {
							System.out.println("Message :" + st);
						}
					} else if (sk.isAcceptable() && sk.channel() == tcp_in_ssc) {
						if (affichage) {
							System.out.println("Evenement sur TCP");
						}
						Socket sock_tcp = tcp_in_ssc.socket().accept();
						if (affichage) {
							System.out.println("Acceptation TCP");
						}
						PrintWriter tcp_pw = new PrintWriter(new OutputStreamWriter(sock_tcp.getOutputStream()));
						String welc = "WELC " + entite.getAddrNext() + " " + entite.getPortOutUDP() + " "
								+ entite.getAddrMultiDiff(1) + " " + entite.getPortMultiDiff(1) + "\n";
						tcp_pw.print(welc);
						tcp_pw.flush();
						if (affichage) {
							System.out.println("Envoi de : " + welc);
						}
						BufferedReader tcp_br = new BufferedReader(new InputStreamReader(sock_tcp.getInputStream()));
						// newc
						String lu = tcp_br.readLine();
						lu = Annexe.substringLast(lu);
						if (affichage) {
							System.out.println("Recu  de : " + lu);
						}
						String parts[] = lu.split(" ");
						String futurAddrUDPOut = parts[1];
						String futurPortUDPOut = parts[2];
						String ackc = "ACKC\n";
						tcp_pw.print(ackc);
						tcp_pw.flush();
						if (affichage) {
							System.out.println("Envoi de : " + ackc);
						}
						entite.setAddrNext(futurAddrUDPOut);
						entite.setPortOutUDP(Integer.parseInt(futurPortUDPOut));

						tcp_br.close();
						tcp_pw.close();
						sock_tcp.close();
						if (affichage) {
							System.out.println("Fin de connection TCP");
							entite.printEntiteSimple();
						}
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
