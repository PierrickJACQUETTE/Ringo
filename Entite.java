import java.util.ArrayList;
import java.util.Scanner;

public class Entite implements Runnable {

	private String identifiant;
	private int portInUDP;
	private int portOutUDP;
	private int portTCPIn;
	private int portTCPOut;
	private String addrNext;
	private String[] addrMultiDiff;
	private int[] portMultiDiff;
	private boolean isDuplicateur;
	private ArrayList<String> mssgTransmisAnnneau1;
	private ArrayList<String> mssgTransmisAnnneau2;

	public Entite() {
		this.identifiant = "-1";
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
		this.mssgTransmisAnnneau1 = new ArrayList<String>();
		this.mssgTransmisAnnneau2 = new ArrayList<String>();
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

	private void printMssgAnneau(ArrayList<String> arl) {
		for (int i = 0; i < arl.size(); i++) {
			System.out.println(i + " : " + arl.get(i));
		}
	}

	public void printEntiteComplex() {
		printEntiteSimple();
		System.out.println("Les messages transmis par cette entité sont : ");
		System.out.println("Sur l'anneau 1 : ");
		printMssgAnneau(this.mssgTransmisAnnneau1);
		System.out.println("Sur l'anneau 2 : ");
		printMssgAnneau(this.mssgTransmisAnnneau2);
		System.out.println();
	}

	public String getIdentifiant() {
		return identifiant;
	}

	public void setIdentifiant(String identifiant) {
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

	public String getAddrNext() {
		return addrNext;
	}

	public void setAddrNext(String addrNext) {
		this.addrNext = addrNext;
	}

	public String getAddrMultiDiff(int i) {
		String res = "";
		if (i == 1) {
			res = this.addrMultiDiff[0];
		} else if (i == 2) {
			res = this.addrMultiDiff[1];
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

	public ArrayList<String> getMssgTransmisAnneau1() {
		return this.mssgTransmisAnnneau1;
	}

	public ArrayList<String> getMssgTransmisAnneau2() {
		return this.mssgTransmisAnnneau2;
	}

	public void setMssgTransmisAnneau1(ArrayList<String> mssgTransmis) {
		this.mssgTransmisAnnneau1 = mssgTransmis;
	}

	public void setMssgTransmisAnneau2(ArrayList<String> mssgTransmis) {
		this.mssgTransmisAnnneau2 = mssgTransmis;
	}

	public void run() {
		while (true) {
			Scanner sc = new Scanner(System.in);
			String tmp = sc.nextLine();
			tmp = Annexe.removeWhite(tmp);
			String idm = Annexe.newIdentifiant();
			String message = tmp + " " + idm;
			try {
				if (tmp.equals("GBYE")) {
					message += " " + Annexe.trouveAdress() + " " + this.portInUDP + " " + this.addrNext + " "
							+ this.portOutUDP;
				}
				MssgUPD.analyseMssg(message, false);
				MssgUPD.sendUDP(message, this, Main.affichage);
				this.mssgTransmisAnnneau1.add(idm);
			} catch (LengthException e) {
				e.getMessage();
			} catch (MssgSpellCheck e) {
				e.getMessage();
			}
		}
	}
}
