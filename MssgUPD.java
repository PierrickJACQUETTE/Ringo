import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class MssgUPD {
	protected static Entite receveUDP(Entite entite, boolean affichage, DatagramChannel udp_in_dc, ByteBuffer buff) {
		if (affichage) {
			System.out.println("Message UDP recu");
		}
		try {
			udp_in_dc.receive(buff);
			String st = new String(buff.array(), 0, buff.array().length).trim();
			if (affichage) {
				System.out.println("Message recu :" + st);
			}
			analyseMssg(st);
			String parts[] = st.split(" ");
			if (parts[0].equals("WHOS")) {
				mssgWHO(st,parts, entite,affichage);
			}
			else if(parts[0].equals("MEMB")){
				mssgMEMB(st,parts, entite, affichage);
			}
			buff.clear();
		} catch (LengthException | MssgSpellCheck e) {
			if (affichage) {
				System.out.println("Je ne transfere pas ce message");
				e.getMessage();
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
		return entite;
	}
	
	private static void removeMssg(String idm,Entite entite,boolean affichage){
		entite.getMssgTransmisAnneau1().remove(idm);
		if(affichage){
			System.out.println("Remove mssg with this idm : "+idm);
		}
	}

	private static void mssgWHO(String message, String[] parts, Entite entite,boolean affichage) {
		String idm = parts[1];
		if (entite.getMssgTransmisAnneau1().contains(idm)) {
			removeMssg(idm, entite, affichage);
		} else {
			sendUDP(message, entite,affichage);
			
		}
		String idmM = Annexe.newIdentifiant();
		message = "MEMB "+ idmM +" "+entite.getIdentifiant() +" "+Annexe.trouveAdress() + " "+entite.getPortInUDP();
		sendUDP(message, entite, affichage);
		entite.getMssgTransmisAnneau1().add(idmM);
	}
	
	private static void mssgMEMB(String message,String[] parts, Entite entite,boolean affichage){
		String idm = parts[1];
		if (entite.getMssgTransmisAnneau1().contains(idm)) {
			removeMssg(idm, entite, affichage);
		}
		else{
			sendUDP(message, entite, affichage);
			System.out.println("\nDans l'anneau est present : ");
			System.out.println("Une entite avec cette identifiant : "+parts[2]);
			System.out.println("Qui a comme port : "+parts[4] +" et comme addresse : "+parts[3]+"\n");
		}
	}

	protected static void sendUDP(String tmp, Entite entite,boolean affichage) {
		try {
			DatagramSocket dso = new DatagramSocket();
			byte[] data;
			data = tmp.getBytes();
			InetSocketAddress ia = new InetSocketAddress(entite.getAddrNext(), entite.getPortOutUDP());
			DatagramPacket paquet = new DatagramPacket(data, data.length, ia);
			dso.send(paquet);
			if(affichage){
				System.out.println("Message envoye : "+ tmp);
			}
			dso.close();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected static void analyseMssg(String str) throws LengthException, MssgSpellCheck {
		String parts[] = str.split(" ");
		if (parts[0].equals("WHOS")) {
			int longeur = 2;
			if (parts.length == longeur) {
				suiteAnalyseMssg(longeur, str, parts);
			}
		} 
		else if(parts[0].equals("MEMB")){
			int longeur = 5;
			if (parts.length == longeur) {
				suiteAnalyseMssg(longeur, str, parts);
			}
		}
		else {
			throw new MssgSpellCheck(parts[0]);
		}
	}

	private static void suiteAnalyseMssg(int longeur, String mssg, String[] parts) throws LengthException {
		if (parts.length != longeur) {
			throw new LengthException(longeur, parts.length, "UDP", mssg);
		}
	}
}
