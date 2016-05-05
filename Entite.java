import java.util.ArrayList;
import java.util.Scanner;

public class Entite implements Runnable {

	private String identifiant;
	private int portInUDP;
	private int[] portOutUDP;
	private int portTCPIn;
	private int portTCPOut;
	private String[] addrNext;
	private String[] addrMultiDiff;
	private int[] portMultiDiff;
	private boolean isDuplicateur;
	private boolean alreadyReceivedEYBG;
	private ArrayList<Mssg> mssgTransmisAnnneau1;
	private ArrayList<Mssg> mssgTransmisAnnneau2;
	private ArrayList<MssgApplDemande> demandeFichier;

	public Entite() {
		this.identifiant = "-1";
		this.portInUDP = -1;
		this.portOutUDP = new int[2];
		this.portOutUDP[0] = -1;
		this.portOutUDP[1] = -1;
		this.portTCPIn = -1;
		this.portTCPOut = -1;
		this.addrNext = new String[2];
		this.addrMultiDiff = new String[2];
		this.portMultiDiff = new int[2];
		this.portMultiDiff[0] = -1;
		this.portMultiDiff[1] = -1;
		this.isDuplicateur = false;
		this.alreadyReceivedEYBG = false;
		this.mssgTransmisAnnneau1 = new ArrayList<Mssg>();
		this.mssgTransmisAnnneau2 = new ArrayList<Mssg>();
		this.demandeFichier = new ArrayList<MssgApplDemande>();
	}

	public void printEntiteSimple() {
		System.out.println("\nIdentifiant : " + this.identifiant);
		System.out.println("Port In UPD : " + this.portInUDP);
		System.out.println("Port TCPIn : " + this.portTCPIn);
		System.out.println("Port TCPOut : " + this.portTCPOut);
		for (int i = 0; i < this.addrNext.length; i++) {
			System.out.println("Addresse next : " + this.addrNext[i]);
			System.out.println("Port Out UPD : " + this.portOutUDP[i]);
		}
		System.out.println("L'entite est un duplicateur ? " + this.isDuplicateur);
		for (int i = 0; i < this.addrMultiDiff.length; i++) {
			System.out.println("Addresse Multi diff : " + this.addrMultiDiff[i]);
			System.out.println("Port Multi diff : " + this.portMultiDiff[i]);
		}
		System.out.println("Si cest un dupp et deja recu EYBE ? " + this.alreadyReceivedEYBG);
		System.out.println();
	}
	
	private void printMssgAnneau(ArrayList<? extends Mssg> arl) {
		for (int i = 0; i < arl.size(); i++) {
			System.out.println(i + " : " + arl.get(i).getIdm());
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
		
		System.out.println("Les demandes de fichiers sont : ");
		printMssgAnneau(this.getDemandeFichier());
	}

	public String getIdentifiant() {
		return this.identifiant;
	}

	public void setIdentifiant(String identifiant) {
		this.identifiant = identifiant;
	}

	public int getPortInUDP() {
		return this.portInUDP;
	}

	public void setPortInUDP(int portInUDP) {
		this.portInUDP = portInUDP;
	}

	public int getPortOutUDP(int i) {
		int res = 0;
		if (i == 1) {
			res = this.portOutUDP[0];
		} else if (i == 2) {
			res = this.portOutUDP[1];
		} else {
			System.err.println("Erreur dans getAddrMultiDiff anneau non reconnue");
		}
		return res;
	}

	public void setPortOutUDP(int portOutUDP, int i) {
		if (i == 1) {
			this.portOutUDP[0] = portOutUDP;
		} else if (i == 2) {
			this.portOutUDP[1] = portOutUDP;
		} else {
			System.err.println("Erreur dans setAddrNext anneau non reconnue");
		}
	}

	public int getPortTCPIn() {
		return this.portTCPIn;
	}

	public void setPortTCPIn(int portTCP) {
		this.portTCPIn = portTCP;
	}

	public int getPortTCPOut() {
		return this.portTCPOut;
	}

	public void setPortTCPOut(int portTCP) {
		this.portTCPOut = portTCP;
	}

	public String getAddrNext(int i) {
		String res = "";
		if (i == 1) {
			res = this.addrNext[0];
		} else if (i == 2) {
			res = this.addrNext[1];
		} else {
			System.err.println("Erreur dans getAddrNext anneau non reconnue");
		}
		return res;
	}

	public void setAddrNext(String addrNext, int i) {
		if (i == 1) {
			this.addrNext[0] = addrNext;
		} else if (i == 2) {
			this.addrNext[1] = addrNext;
		} else {
			System.err.println("Erreur dans setAddrNext anneau non reconnue");
		}
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

	public boolean getIsDuplicateur() {
		return this.isDuplicateur;
	}

	public void setIsDuplicateur(boolean isDuplicateur) {
		this.isDuplicateur = isDuplicateur;
	}

	public boolean getAlreadyReceivedEYBG() {
		return this.alreadyReceivedEYBG;
	}

	public void setAlreadyReceivedEYBG(boolean alreadyReceivedEYBG) {
		this.alreadyReceivedEYBG = alreadyReceivedEYBG;
	}

	public ArrayList<Mssg> getMssgTransmisAnneau1() {
		return this.mssgTransmisAnnneau1;
	}

	public void setMssgTransmisAnneau1(ArrayList<Mssg> mssgTransmis) {
		this.mssgTransmisAnnneau1 = mssgTransmis;
	}

	public ArrayList<Mssg> getMssgTransmisAnneau2() {
		return this.mssgTransmisAnnneau2;
	}

	public void setMssgTransmisAnneau2(ArrayList<Mssg> mssgTransmis) {
		this.mssgTransmisAnnneau2 = mssgTransmis;
	}

	public ArrayList<MssgApplDemande> getDemandeFichier() {
		return this.demandeFichier;
	}

	public void setDemandeFichier(ArrayList<MssgApplDemande> list) {
		this.demandeFichier = list;
	}

	public void run() {
		while (true) {
			try {
				Scanner sc = new Scanner(System.in);
				String tmp = sc.nextLine();
				String tmp2 = new String(tmp);
				String[] suite = null;
				boolean info = false;
				if (tmp.equals("INFO SIMPLE")) {
					this.printEntiteSimple();
					info = true;
				} else if (tmp.equals("INFO COMPLEX")) {
					this.printEntiteComplex();
					info = true;
				} else if (tmp.equals("WHOS")) {
					envoi("MEMB", "", suite, true);
				} else if (tmp.contains("APPL")) {
					suite = tmp.split(" ");
					MssgUPD.suiteAnalyseMssgInf(3, tmp, suite);
					tmp = suite[0];
				}
				if (info == false) {
					envoi(tmp, tmp2, suite, false);
				}
				Annexe.waitAMssg();
			} catch (LengthException e) {
				e.printStackTrace();
			}
		}
	}

	private Mssg trans(String idm, String message, int anneau){
		Mssg m = new Mssg(idm);
		String parts [] = message.split(" ");
		if(anneau == 1 && parts[3].equals("REQ")) {
			m = new MssgApplDemande(parts[5], message);
			this.getDemandeFichier().add((MssgApplDemande) m);
			m= new MssgApplDemande(idm, message);
		}
		if (parts[0].equals("TEST")) {
			m = new MssgTransmisTest(idm, true, System.nanoTime());
		}
		return m;
		
	}

	private void envoi(String tmp, String tmp2, String[] suite, boolean isPossible) {
		Mssg m = sendAnneau(tmp, tmp2, 1, suite, isPossible);
		this.mssgTransmisAnnneau1.add(m);
		if (this.isDuplicateur == true) {
			m = sendAnneau(tmp, tmp2, 2, suite, isPossible);
			this.mssgTransmisAnnneau2.add(m);
		}
	}

	private Mssg sendAnneau(String tmp, String tmpAPPL, int i, String[] suite, boolean isPossible) {
		try {
			tmp = Annexe.removeWhite(tmp);
			String idm = Annexe.newIdentifiant();
			String message = tmp + " " + idm;
			if (tmp.equals("MEMB")) {
				message = "MEMB " + idm + " " + this.identifiant + " " + Annexe.trouveAdress(true) + " "
						+ this.portInUDP;
			} else if (tmp.equals("GBYE")) {
				message += " " + Annexe.trouveAdress(true) + " " + this.portInUDP + " " + this.addrNext[i - 1] + " "
						+ this.portOutUDP[i - 1];
			} else if (tmp.equals("TEST")) {
				message += " " + this.addrMultiDiff[i - 1] + " " + this.portMultiDiff[i - 1];
			} else if (tmp.equals("APPL")) {
				message += " " + suite[1] + "###";
				if (suite[1].equals("DIFF")) {
					message += "# ";
					tmpAPPL = tmpAPPL.substring(10, tmpAPPL.length());
					int size = tmpAPPL.length();
					String taille = Annexe.remplirZero(size,3);
					message += taille + " " + tmpAPPL;
				} else if (suite[1].equals("TRANS")) {
					message += " REQ ";
					int size = suite[2].length();
					String taille = Annexe.remplirZero(size,2);
					message += taille + " " + suite[2];
				}
			}
			if (isPossible == true) {
				MssgUPD.membPrint(message.split(" "));
				MssgUPD.analyseMssg(message, true);
			} else {
				MssgUPD.analyseMssg(message, false);
			}
			MssgUPD.sendUDP(message, this, idm, i);
			return trans(idm, message,i);
		} catch (LengthException e) {
			e.getMessage();
		} catch (MssgSpellCheck e) {
			e.getMessage();
		}
		return null;
	}
}
