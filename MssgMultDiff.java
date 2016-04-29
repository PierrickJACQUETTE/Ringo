import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.sql.Time;
import java.util.ArrayList;

//pour compiler option -Djava.net.preferIPv4Stack=true FAIRE UN MAKE

public class MssgMultDiff {

	protected static void declencheMultiDiff(Entite entite, boolean affichage) {
		for (TimeTest tt : entite.aLTT) {
			if (System.currentTimeMillis() - tt.getTime() > tt.getMaxTime()) {
				sendMutiDiff(entite, affichage);
			}
		}
	}

	private static void sendMutiDiff(Entite entite, boolean affichage) {
		try {
			DatagramSocket dso = new DatagramSocket();
			byte[] data;
			String s = "DOWN";
			data = s.getBytes();
			InetSocketAddress ia = new InetSocketAddress(entite.getAddrMultiDiff(1), entite.getPortMultiDiff(1));
			DatagramPacket paquet = new DatagramPacket(data, data.length, ia);
			dso.send(paquet);
			if (affichage) {
				System.out.println("Message multi diff envoyé : " + s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static void receiveMultiDiff(boolean affichage, ByteBuffer buff, DatagramChannel udp_multi_dc) {
		try {
			if (affichage) {
				System.out.println("Message UDP multi diff recu");
			}
			udp_multi_dc.receive(buff);
			String st = new String(buff.array(), 0, buff.array().length);
			if (affichage) {
				System.out.println("Message recu :" + st);
			}
			buff.clear();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
