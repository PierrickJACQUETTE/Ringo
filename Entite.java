import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;

public class Entite {

	private long identifiant;
	private int portInUDP;
	private int portOutUDP;
	private int portTCPIn;
	private int portTCPOut;
	private String addrNext;
	private String[] addrMultiDiff;
	private int[] portMultiDiff;
	private boolean isDuplicateur;
	private ArrayList<String> mssgTransmis;

	public Entite() {
		this.identifiant = -1;
		this.portInUDP = -1;
		this.portOutUDP = -1;
		this.portTCPIn = -1;
		this.portTCPOut = -1;
		this.addrNext = null;
		this.addrMultiDiff = new String[2];
		this.portMultiDiff = new int[2];
		this.portMultiDiff[0] = -1;
		this.portMultiDiff[1] = -1;
		this.isDuplicateur = false;
		this.mssgTransmis = new ArrayList<String>();
	}

	public void printEntiteSimple() {
		System.out.println("\nIdentifiant : " + this.identifiant);
		System.out.println("Port In UPD : " + this.portInUDP);
		System.out.println("Port Out UPD : " + this.portOutUDP);
		System.out.println("Port TCPIn : " + this.portTCPIn);
		System.out.println("Port TCPOut : " + this.portTCPOut);
		System.out.println("Addresse next : " + this.addrNext);
		System.out.println("L'entite est un duplicateur ? " + this.isDuplicateur);
		for (int i = 0; i < this.addrMultiDiff.length; i++) {
			System.out.println("Addresse Multi diff : " + addrMultiDiff[i]);
			System.out.println("Port Multi diff : " + portMultiDiff[i]);
		}
		System.out.println();
	}

	public void printEntiteComplex() {
		printEntiteSimple();
		System.out.println("Les messages transmis par cette entité sont : ");
		for (int i = 0; i < this.mssgTransmis.size(); i++) {
			System.out.println(i + " : " + this.mssgTransmis.get(i));
		}
		System.out.println();
	}

	public long getIdentifiant() {
		return identifiant;
	}

	public void setIdentifiant(long identifiant) {
		this.identifiant = identifiant;
	}

	public int getPortInUDP() {
		return portInUDP;
	}

	public void setPortInUDP(int portInUDP) {
		this.portInUDP = portInUDP;
	}

	public int getPortOutUDP() {
		return portOutUDP;
	}

	public void setPortOutUDP(int portOutUDP) {
		this.portOutUDP = portOutUDP;
	}

	public int getPortTCPIn() {
		return portTCPIn;
	}

	public void setPortTCPIn(int portTCP) {
		this.portTCPIn = portTCP;
	}
	
	public int getPortTCPOut() {
		return portTCPOut;
	}

	public void setPortTCPOut(int portTCP) {
		this.portTCPOut = portTCP;
	}

	public String  getAddrNext() {
		return addrNext;
	}

	public void setAddrNext(String  addrNext) {
		this.addrNext = addrNext;
	}

	public String getAddrMultiDiff(int i) {
		String res = "";
		if (i == 1) {
			res = this.addrMultiDiff[0];
		} else if (i == 2) {
			res =  this.addrMultiDiff[1];
		} else {
			System.err.println("Erreur dans getAddrMultiDiff anneau non reconnue");
		}
		return res;
	}

	public void setAddrMultiDiff(String addrMultiDiff, int i) {
		if (i == 1) {
			this.addrMultiDiff[0] = addrMultiDiff;
		} else if (i == 2) {
			this.addrMultiDiff[1] = addrMultiDiff;
		} else {
			System.err.println("Erreur dans setAddrMultiDiff anneau non reconnue");
		}
	}

	public int getPortMultiDiff(int i) {
		int res = -1;
		if (i == 1) {
			res = this.portMultiDiff[0];
		} else if (i == 2) {
			res = this.portMultiDiff[1];
		} else {
			System.err.println("Erreur dans portMultiDiff anneau non reconnue");
		}
		return res;
	}

	public void setPortMultiDiff(int portMultiDiff, int i) {
		if (i == 1) {
			this.portMultiDiff[0] = portMultiDiff;
		} else if (i == 2) {
			this.portMultiDiff[1] = portMultiDiff;
		} else {
			System.err.println("Erreur dans portMultiDiff anneau non reconnue");
		}
	}

	public boolean isDuplicateur() {
		return isDuplicateur;
	}

	public void setDuplicateur(boolean isDuplicateur) {
		this.isDuplicateur = isDuplicateur;
	}

	public ArrayList<String> getMssgTransmis() {
		return mssgTransmis;
	}

	public void setMssgTransmis(ArrayList<String> mssgTransmis) {
		this.mssgTransmis = mssgTransmis;
	}
}
