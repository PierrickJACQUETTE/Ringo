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

public class Anneau {
	private Entite entite;

	public Anneau(Entite entite) {
		this.entite = entite;
	}

	public void anneau() {
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
			NetworkInterface interf = NetworkInterface.getByName(Main.TYPECONN);
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

			ByteBuffer buff = ByteBuffer.allocate(Main.SIZEMESSG);

			Thread scanner_in = new Thread(entite);
			scanner_in.start();

			MssgMultDiff mssg = new MssgMultDiff(entite);
			Thread verifMultiDiff = new Thread(mssg);
			verifMultiDiff.start();

			boolean first = true;
			DatagramChannel udp_multi_dc2 = null;
			MembershipKey key2 = null;
			while (true) {
				Annexe.waitAMssg();
				if (first == true && entite.getIsDuplicateur() == true) {
					NetworkInterface interf2 = NetworkInterface.getByName(Main.TYPECONN);
					InetAddress group2 = (Inet4Address) InetAddress.getByName(entite.getAddrMultiDiff(2));
					udp_multi_dc2 = DatagramChannel.open(StandardProtocolFamily.INET)
							.setOption(StandardSocketOptions.SO_REUSEADDR, true)
							.bind(new InetSocketAddress(entite.getPortMultiDiff(2)))
							.setOption(StandardSocketOptions.IP_MULTICAST_IF, interf2);
					udp_multi_dc2.configureBlocking(false);
					udp_multi_dc2.register(selector, SelectionKey.OP_READ);
					key2 = udp_multi_dc2.join(group2, interf2);
					first = false;
				}

				selector.select();
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while (it.hasNext()) {
					SelectionKey sk = it.next();
					it.remove();
					if (sk.isReadable() && sk.channel() == udp_multi_dc) {
						MssgMultDiff.receiveMultiDiff(entite, buff, udp_multi_dc, key);
					} else if (sk.isReadable() && sk.channel() == udp_multi_dc2) {
						MssgMultDiff.receiveMultiDiff(entite, buff, udp_multi_dc2, key2);
					} else if (sk.isReadable() && sk.channel() == udp_in_dc) {
						entite = MssgUPD.receveUDP(entite, udp_in_dc, buff);
					} else if (sk.isAcceptable() && sk.channel() == tcp_in_ssc) {
						entite = MssgTCP.insertAnneauTCP(entite, tcp_in_ssc);
					} else {
						System.out.println("Que s'est il passe");
					}
					buff = ByteBuffer.allocate(0);
					buff = ByteBuffer.allocate(512);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
