import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

//pour compiler option -Djava.net.preferIPv4Stack=true FAIRE UN MAKE

public class MssgMultDiff extends Thread {

	private Entite entite;

	public MssgMultDiff(Entite entite) {
		this.entite = entite;
	}

	protected void declencheMultiDiff() {
		for (int i = 0; i < this.entite.getALL().size(); i++) {
			if (System.currentTimeMillis() - this.entite.getALL().get(i) > Main.TIMEMAX) {
				this.sendMutiDiff();
			}
		}

	}

	private void sendMutiDiff() {
		try {
			DatagramSocket dso = new DatagramSocket();
			byte[] data;
			String s = "DOWN";
			data = s.getBytes();
			InetSocketAddress ia = new InetSocketAddress(this.entite.getAddrMultiDiff(1),
					this.entite.getPortMultiDiff(1));
			DatagramPacket paquet = new DatagramPacket(data, data.length, ia);
			dso.send(paquet);
			if (Main.affichage) {
				System.out.println("Message multi diff envoyé : " + s);
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	protected static void receiveMultiDiff(ByteBuffer buff, DatagramChannel udp_multi_dc) {
		try {
			if (Main.affichage) {
				System.out.println("Message UDP multi diff recu");
			}
			udp_multi_dc.receive(buff);
			String st = new String(buff.array(), 0, buff.array().length).trim();
			if (Main.affichage) {
				System.out.println("Message recu : " + st);
			}
			buff.clear();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		boolean stop = false;
		while (!stop) {
			this.declencheMultiDiff();
			try {
				sleep(Main.TIMEMAX);
			} catch (InterruptedException e) {
				stop = true;
				e.printStackTrace();
			}
		}
	}
}
