import java.util.ArrayList;

public class Mssg{
    private String idm;

    public Mssg(String idm){
        this.idm = idm;
    }

	public String getIdm() {
		return idm;
	}

	public void setIdm(String idm) {
		this.idm = idm;
	}
	
    public boolean my_contains(ArrayList<? extends Mssg> aL){
        for(int i=0;i<aL.size();i++){
            if(aL.get(i).getIdm().equals(this.idm)){
                return true;
            }
        }
        return false;
    }
    
    protected int position(ArrayList<? extends Mssg> aL){
    	 for(int i=0;i<aL.size();i++){
    		 if(aL.get(i).getIdm().equals(this.idm)){
    			 return i;
    		 }
    	 }
    	 return -1;
    }
    
    public void my_remove(ArrayList<? extends Mssg> aL){
        int i = this.position(aL);
        if(i!=-1){
        	aL.remove(i);
        }
    }
}
