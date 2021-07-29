package torgovator.torgovator;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;

import torgovator.utils.ExceptionUtils;

public class XmlDocumentZakupka extends XmlDocument implements InsertableToDatabase {
	private static Logger log = Logger.getLogger(XmlDocumentZakupka.class.getName());
	private String prefixpurchaseNoticeData = "";

	///	private Document document;
	public Zakupka zakupka = new Zakupka();

	public XmlDocumentZakupka(String filaname) {
		super(filaname);

		List<CustomNode> resultOfParse = traverse(document.getDocumentElement(), "");

		zakupka.setFileName(FilenameUtils.getName(filaname));
		setPrefixpurchaseNoticeData(getPrefixpurchaseNoticeData(resultOfParse));

		zakupka.setPurchaseNumber(ReturnValues(resultOfParse, "purchaseNumber"));
		//		zakupka.setCUSTOMERREQUIREMENTS(ReturnArrayValues(resultOfParse, "customerRequirement_purchaseCode"));
		zakupka.setCUSTOMERREQUIREMENTS(ReturnArrayValuescustomerRequirement(resultOfParse));

		zakupka.setOKPD2((ReturnArrayValues(resultOfParse, "OKPD2_code")));
		if (zakupka.getOKPD2().size() == 0) {
			zakupka.setOKPD2((ReturnArrayValues(resultOfParse, "KTRU_code")));
		}

		zakupka.setPurchaseObjectInfo(ReturnValues(resultOfParse, "purchaseObjectInfo"));
		zakupka.setINN(ReturnValues(resultOfParse, "responsibleOrg_INN"));
		zakupka.setResponsibleOrg_fullName(ReturnValues(resultOfParse, "responsibleOrg_fullName"));
		zakupka.setCUSTOMERREGNUM(ReturnValues(resultOfParse, "responsibleOrg_regNum"));
		zakupka.setHref(ReturnValues(resultOfParse, "href"));

		zakupka.setPublishDate(ReturnValues(resultOfParse, "docPublishDate"));

		zakupka.setStartDate(ReturnValues(resultOfParse, "collecting_startDate"));
		zakupka.setEndDate(ReturnValues(resultOfParse, "collecting_endDate"));
		zakupka.setMaxPrice(Double.parseDouble(ReturnValues(resultOfParse, "lot_maxPrice")));

		zakupka.setPlacingWay((ReturnValues(resultOfParse, "placingWay_name")));
		zakupka.setZakon(0);
	}

	@Override
	public void insertToDatabase(Database data) {
		try {
			data.InsertZakupki(zakupka);
		} catch (SQLException e) {
			log.warning(ExceptionUtils.ExceptionStackToString(e));
		}
	}

	/** @return the prefixpurchaseNoticeData */
	private String getPrefixpurchaseNoticeData(List<CustomNode> resultofparse) {
		String result = "";
		String prefix = resultofparse.get(1).getNodeName();
		result = prefix;
		return result;
	}

	/** @param prefixpurchaseNoticeData
	 *            the prefixpurchaseNoticeData to set */
	public void setPrefixpurchaseNoticeData(String prefixpurchaseNoticeData) {
		this.prefixpurchaseNoticeData = prefixpurchaseNoticeData;
	}

}
