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
	
    public static boolean my_contains(ArrayList<Mssg> aL,String idm){
        for(int i=0;i<aL.size();i++){
            if(aL.get(i).getIdm().equals(idm)){
                return true;
            }
        }
        return false;
    }
    
    protected static int position(ArrayList<Mssg> aL,String idm){
    	 for(int i=0;i<aL.size();i++){
    		 if(aL.get(i).getIdm().equals(idm)){
    			 return i;
    		 }
    	 }
    	 return -1;
    }

    public static void my_remove(ArrayList<Mssg> aL, String idm){
        int i = position(aL, idm);
        if(i!=-1){
        	aL.remove(i);
        }
    }
}
