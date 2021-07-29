package torgovator.torgovator;

public class Organization extends Document {
	private static final String DocType = "NSIORG";
	private String KPP = "";
	private String OGRN = "";
	private String OKTMO = "";
	private String IKU = "";
	private String fullName = "";
	private String shortName = "";
	private String regNumber = "";
	private String zip = "";
	private String postalAddress = "";
	private String contactPerson = "";
	private String Url = "";
	private String timeZoneUtcOffset = "";
	private String okved = "";

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((KPP == null) ? 0 : KPP.hashCode());
		result = prime * result + ((OGRN == null) ? 0 : OGRN.hashCode());
		result = prime * result + ((OKTMO == null) ? 0 : OKTMO.hashCode());
		result = prime * result + ((Url == null) ? 0 : Url.hashCode());
		result = prime * result + actual;
		result = prime * result + ((contactPerson == null) ? 0 : contactPerson.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((fullName == null) ? 0 : fullName.hashCode());
		result = prime * result + ((organizationTypeCode == null) ? 0 : organizationTypeCode.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result + ((postalAddress == null) ? 0 : postalAddress.hashCode());
		result = prime * result + ((regNumber == null) ? 0 : regNumber.hashCode());
		result = prime * result + ((shortName == null) ? 0 : shortName.hashCode());
		result = prime * result + ((timeZoneUtcOffset == null) ? 0 : timeZoneUtcOffset.hashCode());
		result = prime * result + ((zip == null) ? 0 : zip.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Organization other = (Organization) obj;
		if (getId() != (other.getId()))
			return false;

		if (KPP == null) {
			if (other.KPP != null)
				return false;
		} else if (!KPP.equals(other.KPP))
			return false;
		if (OGRN == null) {
			if (other.OGRN != null)
				return false;
		} else if (!OGRN.equals(other.OGRN))
			return false;
		if (OKTMO == null) {
			if (other.OKTMO != null)
				return false;
		} else if (!OKTMO.equals(other.OKTMO))
			return false;
		if (Url == null) {
			if (other.Url != null)
				return false;
		} else if (!Url.equals(other.Url))
			return false;
		if (actual != other.actual)
			return false;
		if (contactPerson == null) {
			if (other.contactPerson != null)
				return false;
		} else if (!contactPerson.equals(other.contactPerson))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (fullName == null) {
			if (other.fullName != null)
				return false;
		} else if (!fullName.equals(other.fullName))
			return false;
		if (organizationTypeCode == null) {
			if (other.organizationTypeCode != null)
				return false;
		} else if (!organizationTypeCode.equals(other.organizationTypeCode))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		if (postalAddress == null) {
			if (other.postalAddress != null)
				return false;
		} else if (!postalAddress.equals(other.postalAddress))
			return false;
		if (regNumber == null) {
			if (other.regNumber != null)
				return false;
		} else if (!regNumber.equals(other.regNumber))
			return false;
		if (shortName == null) {
			if (other.shortName != null)
				return false;
		} else if (!shortName.equals(other.shortName))
			return false;
		if (timeZoneUtcOffset == null) {
			if (other.timeZoneUtcOffset != null)
				return false;
		} else if (!timeZoneUtcOffset.equals(other.timeZoneUtcOffset))
			return false;
		if (zip == null) {
			if (other.zip != null)
				return false;
		} else if (!zip.equals(other.zip))
			return false;
		return true;
	}

	private Integer organizationTypeCode = 0;
	private int actual;
	private String phone = "";
	private String email = "";

	/** @return the kPP */
	public String getKPP() {
		return KPP;
	}

	/** @param kPP
	 *            the kPP to set */
	public void setKPP(String kPP) {
		KPP = kPP;
	}

	/** @return the oGRN */
	public String getOGRN() {
		return OGRN;
	}

	/** @param oGRN
	 *            the oGRN to set */
	public void setOGRN(String oGRN) {
		OGRN = oGRN;
	}

	/** @return the fullName */
	public String getFullName() {
		return fullName;
	}

	/** @param fullName
	 *            the fullName to set */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/** @return the oKTMO */
	public String getOKTMO() {
		return OKTMO;
	}

	/** @param oKTMO
	 *            the oKTMO to set */
	public void setOKTMO(String oKTMO) {
		OKTMO = oKTMO;
	}

	/** @return the shortName */
	public String getShortName() {
		return shortName;
	}

	/** @param shortName
	 *            the shortName to set */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/** @return the zip */
	public String getZip() {
		return zip;
	}

	/** @param zip
	 *            the zip to set */
	public void setZip(String zip) {
		this.zip = zip;
	}

	/** @return the postalAddress */
	public String getPostalAddress() {
		return postalAddress;
	}

	/** @param postalAddress
	 *            the postalAddress to set */
	public void setPostalAddress(String postalAddress) {
		this.postalAddress = postalAddress;
	}

	/** @return the url */
	public String getUrl() {
		return Url;
	}

	/** @param url
	 *            the url to set */
	public void setUrl(String url) {
		Url = url;
	}

	/** @return the timeZoneUtcOffset */
	public String getTimeZoneUtcOffset() {
		return timeZoneUtcOffset;
	}

	/** @param timeZoneUtcOffset
	 *            the timeZoneUtcOffset to set */
	public void setTimeZoneUtcOffset(String timeZoneUtcOffset) {
		this.timeZoneUtcOffset = timeZoneUtcOffset;
	}

	/** @return the organizationTypeCode */
	public Integer getOrganizationTypeCode() {
		return organizationTypeCode;
	}

	/** @param organizationTypeCode
	 *            the organizationTypeCode to set */
	public void setOrganizationTypeCode(Integer organizationTypeCode) {
		this.organizationTypeCode = organizationTypeCode;
	}

	public void setOrganizationTypeCode(String organizationTypeCode) {
		setOrganizationTypeCode(Integer.parseInt(organizationTypeCode));
	}

	/** @return the actual */
	public int isActual() {
		return this.actual;

	}

	/** @param actual
	 *            the actual to set */
	public void setActual(String actual) {
		if (actual.equals("true")) {
			this.actual = 1;
		} else {
			this.actual = 0;
		}
	}

	/** @return the phone */
	public String getPhone() {
		return phone;
	}

	/** @param phone
	 *            the phone to set */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/** @return the email */
	public String getEmail() {
		return email;
	}

	/** @param email
	 *            the email to set */
	public void setEmail(String email) {
		this.email = email;
	}

	/** @return the regNumber */
	public String getRegNumber() {
		return regNumber;
	}

	/** @param regNumber
	 *            the regNumber to set */
	public void setRegNumber(String regNumber) {
		this.regNumber = regNumber;
	}

	/** @return the contactPerson */
	public String getContactPerson() {
		return contactPerson;
	}

	/** @param contactPerson
	 *            the contactPerson to set */
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	/** @return the IKU */
	public String getIKU() {
		return IKU;
	}

	/** @param IKU
	 *            the IKU to set */
	public void setIKU(String iKu) {
		IKU = iKu;
	}

	/** @return the okved */
	public String getOkved() {
		return okved;
	}

	/** @param okved
	 *            the okved to set */
	public void setOkved(String okved) {
		this.okved = okved;
	}

	/**
	 * @return the doctype
	 */
	public static String getDoctype() {
		return DocType;
	}

}
