public class MssgApplDemande extends Mssg {

	private int nmbDeMssg;
	private int numeroMssgRecu;
	private StringBuilder contenu;

	public MssgApplDemande(String idm, int nmbDeMssg, int numeroMssgRecu, String contenu) {
		super(idm);
		this.nmbDeMssg = nmbDeMssg;
		this.numeroMssgRecu = numeroMssgRecu;
		this.contenu = new StringBuilder(contenu);
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

	public StringBuilder getContenu() {
		return contenu;
	}

	public void setContenu(StringBuilder contenu) {
		this.contenu = contenu;
	}

	public void addContenu(String contenu) {
		this.contenu.append(contenu);
	}
}
