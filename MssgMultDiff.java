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

	protected void declencheMultiDiff(int anneau) {
		if (anneau == 1) {
			for (int i = 0; i < this.entite.getALL1().size(); i++) {
				if (System.nanoTime() - this.entite.getALL1().get(i) > Main.TIMEMAX) {
					this.sendMutiDiff(anneau);
				}
			}
		} else if (anneau == 2) {
			for (int i = 0; i < this.entite.getALL2().size(); i++) {
				if (System.nanoTime() - this.entite.getALL2().get(i) > Main.TIMEMAX) {
					this.sendMutiDiff(anneau);
				}
			}
		}
	}

	private void sendMutiDiff(int anneau) {
		try {
			DatagramSocket dso = new DatagramSocket();
			byte[] data;
			String s = "DOWN";
			data = s.getBytes();
			InetSocketAddress ia = new InetSocketAddress(this.entite.getAddrMultiDiff(anneau),
					this.entite.getPortMultiDiff(anneau));
			DatagramPacket paquet = new DatagramPacket(data, data.length, ia);
			dso.send(paquet);
			if (Main.affichage) {
				System.out.println("Message multi diff envoyé : " + s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static void receiveMultiDiff(Entite entite, ByteBuffer buff, DatagramChannel udp_multi_dc) {
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
			String[] part = st.split(" ");
			if (part[0].equals("DOWN")) {
				if (entite.getIsDuplicateur() == false) {
					System.exit(0);
				} else {
					System.out.println("Ne pas quitter comme ca !!!!!!");
					// boolean an argument pour savoir anneau 1 ou deux , si
					// cest deux , modifier le boolean isDuplicateur a false
					// si cest le 1 mettre les attributs de 2 dans 1 et boolean
					// false

					// apres faiut faire le mssg NOTC
				}
			} else {
				System.out.println("Message recu en multidiff en inconnu");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		boolean stop = false;
		while (!stop) {
			this.declencheMultiDiff(1);
			if (this.entite.getIsDuplicateur() == true) {
				this.declencheMultiDiff(2);
			}
			try {
				sleep(Main.TIMEMAX);
			} catch (InterruptedException e) {
				stop = true;
				e.printStackTrace();
			}
		}
	}
}
