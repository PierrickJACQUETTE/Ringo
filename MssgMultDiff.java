import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.util.ArrayList;

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
			if (data.length > Main.SIZEMESSG) {
				throw new LengthException(data.length, s, "MULTI DIFF envoi");
			}
			InetSocketAddress ia = new InetSocketAddress(this.entite.getAddrMultiDiff(anneau),
					this.entite.getPortMultiDiff(anneau));
			DatagramPacket paquet = new DatagramPacket(data, data.length, ia);
			dso.send(paquet);
			if (Main.affichage) {
				System.out.println("Message multi diff envoyé : " + s);
			}
		} catch (LengthException e) {
			e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static void receiveMultiDiff(Entite entite, ByteBuffer buff, DatagramChannel udp_multi_dc,
			MembershipKey key) {
		try {
			if (Main.affichage) {
				System.out.println("Message UDP multi diff recu");
			}
			udp_multi_dc.receive(buff);
			String st = new String(buff.array(), 0, buff.array().length).trim();
			if (Main.affichage) {
				System.out.println("Message recu : " + st);
			}
			if (st.length() > Main.SIZEMESSG) {
				throw new LengthException(st.length(), st, "MULTI DIFF recu");
			}
			String[] part = st.split(" ");
			if (part[0].equals("DOWN")) {
				if (entite.getIsDuplicateur() == false) {
					System.exit(0);
				} else {
					int portEnvoi = Integer.parseInt(udp_multi_dc.getLocalAddress().toString().split(":")[1]);
					String addrEnvoi = Annexe.convertIPV4Complete(key.group().getHostAddress());
					if (entite.getAddrMultiDiff(1).equals(addrEnvoi) && entite.getPortMultiDiff(1) == portEnvoi) {

						entite.setIsDuplicateur(false);
						entite = modifEntite(entite, 1);
						entite = modifEntite(entite, 2);

					} else if (entite.getAddrMultiDiff(2).equals(addrEnvoi)
							&& entite.getPortMultiDiff(2) == portEnvoi) {

						entite.setIsDuplicateur(false);
						entite = modifEntite(entite, 2);

					} else {
						System.out.println("L'addresse et/ou le port de multidiff est inconnu a cette entite");
					}
				}
			} else {
				try {
					throw new MssgSpellCheck(st, "MultiDiff");
				} catch (MssgSpellCheck e) {
					e.getMessage();
				}
			}
		} catch (LengthException e) {
			e.getMessage();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Entite modifEntite(Entite entite, int anneau) {
		String addrNext, addrMulti;
		int portOutUDp, portMulti;
		if (anneau == 1) {
			addrNext = entite.getAddrNext(2);
			addrMulti = entite.getAddrMultiDiff(2);
			portOutUDp = entite.getPortOutUDP(2);
			portMulti = entite.getPortMultiDiff(2);
			entite.setALL1(entite.getALL2());
			entite.setMssgTransmisAnneau1(entite.getMssgTransmisAnneau2());

		} else {
			addrMulti = addrNext = null;
			portMulti = portOutUDp = -1;
			entite.setALL2(new ArrayList<Long>());
		}
		entite.setAddrNext(addrNext, anneau);
		entite.setPortOutUDP(portOutUDp, anneau);
		entite.setAddrMultiDiff(addrMulti, anneau);
		entite.setPortMultiDiff(portMulti, anneau);
		entite.setMssgTransmisAnneau2(new ArrayList<String>());
		return entite;
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
