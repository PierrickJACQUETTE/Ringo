public class MssgTransmisTest extends Mssg {

	private boolean isTest;
	private long time;

	public MssgTransmisTest(String idm, boolean isTest, long time) {
		super(idm);
		this.isTest = isTest;
		this.time = time;
	}

	public boolean getIsTest() {
		return isTest;
	}

	public void setTest(boolean isTest) {
		this.isTest = isTest;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}
