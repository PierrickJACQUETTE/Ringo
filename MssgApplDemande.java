import java.util.ArrayList;

public class MssgApplDemande extends Mssg {

	private int nmbDeMssg;
	private int numeroMssgRecu;
	private StringBuilder contenu;
	private String mssgReq;
	private String idTrans;

	public MssgApplDemande(String idm, String mssgReq) {
		super(idm);
		this.nmbDeMssg = -1;
		this.numeroMssgRecu = -1;
		this.contenu = new StringBuilder();
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

	public StringBuilder getContenu() {
		return contenu;
	}

	public void setContenu(StringBuilder contenu) {
		this.contenu = contenu;
	}

	public void addContenu(String contenu) {
		this.contenu.append(contenu);
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
	
    protected static boolean my_contains(ArrayList<MssgApplDemande> aL,String idTrans){
        for(int i=0;i<aL.size();i++){
            if(aL.get(i).getIdTrans().equals(idTrans)){
                return true;
            }
        }
        return false;
    }
    
    protected static int position(ArrayList<MssgApplDemande> aL,String idTrans){
   	 for(int i=0;i<aL.size();i++){
   		 if(aL.get(i).getIdTrans().equals(idTrans)){
   			 return i;
   		 }
   	 }
   	 return -1;
   }
}
