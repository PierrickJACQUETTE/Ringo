
public class TimeTest {

	private String identifiant;
	private long time;
	private final long MaxTime = 6000;

	public TimeTest(String identifiant, long time) {
		this.identifiant = identifiant;
		this.time = time;
	}

	public String getIdentifiant() {
		return identifiant;
	}

	public void setIdentifiant(String identifiant) {
		this.identifiant = identifiant;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getMaxTime() {
		return MaxTime;
	}
}
