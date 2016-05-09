import java.util.ArrayList;

public class MssgApplDemande extends Mssg {

	private int nmbDeMssg;
	private int numeroMssgRecu;
	private byte[] contenu;
	private String mssgReq;
	private String idTrans;

	public MssgApplDemande(String idm, String mssgReq) {
		super(idm);
		this.nmbDeMssg = -1;
		this.numeroMssgRecu = -1;
		this.contenu = new byte[0];
		this.mssgReq = mssgReq;
		this.idTrans = null;
	}

	public int getNmbDeMssg() {
		return nmbDeMssg;
	}

	public void setNmbDeMssg(int nmbDeMssg) {
		this.nmbDeMssg = nmbDeMssg;
	}

	public int getNumeroMssgRecu() {
		return numeroMssgRecu;
	}

	public void setNumeroMssgRecu(int numeroMssgRecu) {
		this.numeroMssgRecu = numeroMssgRecu;
	}

	public byte[] getContenu() {
		return contenu;
	}

	public void setContenu(byte[] contenu) {
		this.contenu = contenu;
	}

	public void addContenu(byte[] newContenu, int size) {
		byte[] tmp = new byte[this.contenu.length + size];
		int i = 0;
		for (; i < this.contenu.length; i++) {
			tmp[i] = this.contenu[i];
		}
		for (int j = 0; j < size; j++) {
			tmp[i + j] = newContenu[j];
		}
		this.contenu = tmp;
	}

	public String getMssgReq() {
		return mssgReq;
	}

	public void setMssgReq(String mssgReq) {
		this.mssgReq = mssgReq;
	}

	public String getIdTrans() {
		return idTrans;
	}

	public void setIdTrans(String idTrans) {
		this.idTrans = idTrans;
	}

	protected static boolean my_contains(ArrayList<MssgApplDemande> aL, String idTrans) {
		for (int i = 0; i < aL.size(); i++) {
			if (aL.get(i).getIdTrans().equals(idTrans)) {
				return true;
			}
		}
		return false;
	}

	protected static int position(ArrayList<MssgApplDemande> aL, String idTrans) {
		for (int i = 0; i < aL.size(); i++) {
			if (aL.get(i).getIdTrans().equals(idTrans)) {
				return i;
			}
		}
		return -1;
	}
}
