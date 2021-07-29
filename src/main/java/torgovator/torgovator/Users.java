package torgovator.torgovator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Users")
public class Users {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String USERID;
	private String EMAIL;
	private String PASSWD;
	private String NAME;

	/** @return the uSERID */
	public String getUSERID() {
		return USERID;
	}

	/** @param uSERID
	 *            the uSERID to set */
	public void setUSERID(String uSERID) {
		USERID = uSERID;
	}

	/** @return the eMAIL */
	public String getEMAIL() {
		return EMAIL;
	}

	/** @param eMAIL
	 *            the eMAIL to set */
	public void setEMAIL(String eMAIL) {
		EMAIL = eMAIL;
	}

	/** @return the pASSWD */
	public String getPASSWD() {
		return PASSWD;
	}

	/** @param pASSWD
	 *            the pASSWD to set */
	public void setPASSWD(String pASSWD) {
		PASSWD = pASSWD;
	}

	/** @return the nAME */
	public String getNAME() {
		return NAME;
	}

	/** @param nAME
	 *            the nAME to set */
	public void setNAME(String nAME) {
		NAME = nAME;
	}

}
