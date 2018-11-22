package torgovator.torgovator;

import java.util.Date;
import java.util.List;

public class Zakupka extends Document {
	private List<String> PURCHASECODE1;
	private List<String> OKPD2;
	private String purchaseNumber = "";
	private String OKPD2_name = "";
	private String INN = "";
	private String responsibleOrg_fullName = "";
	private String placingWay = "";
	private String Regnum = "";
	private String purchaseObjectInfo = "";
	// private String PURCHASECODE = "";
	private String href = "";
	private java.sql.Timestamp PublishDate;
	private java.sql.Timestamp endDate;
	private java.sql.Timestamp startDate;
	private double maxPrice = 0;

	/*
	 * TODO Напилить модификации закупок ТЕГ modification
	 */
	// XXX Надо много учиться !

	/** @return the purchaseNumber */
	public String getPurchaseNumber() {
		return purchaseNumber;
	}

	/** @param purchaseNumber
	 *            the purchaseNumber to set */
	public void setPurchaseNumber(String purchaseNumber) {
		this.purchaseNumber = purchaseNumber;
	}

	/** @return the oKPD2_name */
	public String getOKPD2_name() {
		return OKPD2_name;
	}

	/** @param oKPD2_name
	 *            the oKPD2_name to set */
	public void setOKPD2_name(String oKPD2_name) {
		OKPD2_name = oKPD2_name;
	}

	/** @return the iNN */
	public String getINN() {
		return INN;
	}

	/** @param iNN
	 *            the iNN to set */
	public void setINN(String iNN) {
		INN = iNN;
	}

	/** @return the regnum */
	public String getRegnum() {
		return Regnum;
	}

	/** @param regnum
	 *            the regnum to set */
	public void setRegnum(String regnum) {
		Regnum = regnum;
	}

	/** @return the purchaseObjectInfo */
	public String getPurchaseObjectInfo() {
		return purchaseObjectInfo;
	}

	/** @param purchaseObjectInfo
	 *            the purchaseObjectInfo to set */
	public void setPurchaseObjectInfo(String purchaseObjectInfo) {
		this.purchaseObjectInfo = purchaseObjectInfo;
	}

	/** @return the href */
	public String getHref() {
		return href;
	}

	/** @param href
	 *            the href to set */
	public void setHref(String href) {
		this.href = href;
	}

	/** @return the pURCHASECODE1 */
	public List<String> getPURCHASECODE1() {
		return PURCHASECODE1;
	}

	/** @param pURCHASECODE1
	 *            the pURCHASECODE1 to set */
	public void setPURCHASECODE1(List<String> pURCHASECODE1) {
		PURCHASECODE1 = pURCHASECODE1;
	}

	/** @return the oKPD2 */
	public List<String> getOKPD2() {
		return OKPD2;
	}

	/** @param oKPD2
	 *            the oKPD2 to set */
	public void setOKPD2(List<String> oKPD2) {
		OKPD2 = oKPD2;
	}

	/** @return the endDate */
	public Date getEndDate() {
		return endDate;
	}

	/** @param endDate
	 *            the endDate to set */
	public void setEndDate(java.sql.Timestamp endDate) {
		this.endDate = endDate;
	}

	/** @return the startDate */
	public Date getStartDate() {
		return startDate;
	}

	/** @param startDate
	 *            the startDate to set */
	public void setStartDate(java.sql.Timestamp startDate) {
		this.startDate = startDate;
	}

	public java.sql.Date convertJavaDateToSqlDate(java.util.Date date) {
		return new java.sql.Date(date.getTime());
	}

	public void setStartDate(String returnValues) {
		if (returnValues != "") {
			setStartDate(StrToDateSql(returnValues));
		}

	}

	public void setEndDate(String returnValues) {
		if (returnValues != "") {
			setEndDate(StrToDateSql(returnValues));
		}
	}

	/** @return the maxPrice */
	public double getMaxPrice() {
		return maxPrice;
	}

	/** @param maxPrice
	 *            the maxPrice to set */
	public void setMaxPrice(double maxPrice) {
		this.maxPrice = maxPrice;
	}

	/** @return the placingWay */
	public String getPlacingWay() {
		return placingWay;
	}

	/** @param placingWay
	 *            the placingWay to set */
	public void setPlacingWay(String placingWay) {
		this.placingWay = placingWay;
	}

	/** @return the responsibleOrg_fullName */
	public String getResponsibleOrg_fullName() {
		return responsibleOrg_fullName;
	}

	/** @param responsibleOrg_fullName
	 *            the responsibleOrg_fullName to set */
	public void setResponsibleOrg_fullName(String responsibleOrg_fullName) {
		this.responsibleOrg_fullName = responsibleOrg_fullName;
	}

	/** @return the publishDate */
	public java.sql.Timestamp getPublishDate() {
		return PublishDate;
	}

	/** @param publishDate
	 *            the publishDate to set */
	public void setPublishDate(java.sql.Timestamp publishDate) {
		PublishDate = publishDate;
	}

	public void setPublishDate(String returnValues) {
		if (returnValues != "") {
			setPublishDate(StrToDateSql(returnValues));
		}
	}

}
