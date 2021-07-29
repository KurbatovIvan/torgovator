package torgovator.torgovator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// Организации 223 ФЗ
public class XmlnsiOrganization extends XmlDocument implements InsertableToDatabase {
	private static Logger log = Logger.getLogger(XmlnsiOrganization.class.getName());
	public List<Organization> OrgList = new ArrayList<>();

	public XmlnsiOrganization(String filaname) {
		super(filaname);
		log.info("Закачиваем организации 223 ФЗ = " + filaname);
		NodeList nsiOrganizationList = document.getFirstChild().getChildNodes(); // Докапываемся до организаций
		NodeList nodeList = nsiOrganizationList.item(3).getChildNodes();

		log.info("Количество организаций:" + nodeList.getLength());

		for (int itr = 0; itr < nodeList.getLength(); itr++) {
			Node node = nodeList.item(itr);
			List<CustomNode> resultOfParse = traverse(node, "");
			if ((resultOfParse.size() > 0) && !ReturnValues(resultOfParse, "ns2:mainInfo_inn").equals("")) {

				Organization org = new Organization();
				org.setFileName(FilenameUtils.getName(filaname));
				org.setId(Long.parseLong(ReturnValues(resultOfParse, "ns2:mainInfo_inn")));
				org.setKPP(ReturnValues(resultOfParse, "ns2:mainInfo_kpp"));
				org.setEmail(ReturnValues(resultOfParse, "ns2:contactInfo_ns2:contactEmail"));
				org.setOGRN(ReturnValues(resultOfParse, "ns2:mainInfo_ogrn"));
				org.setFullName(ReturnValues(resultOfParse, "ns2:mainInfo_fullName"));
				org.setShortName(ReturnValues(resultOfParse, "ns2:mainInfo_shortName"));
				org.setRegNumber(ReturnValues(resultOfParse, "ns2:nsiOrganizationData_ns2:code"));
				org.setContactPerson(ReturnValues(resultOfParse, "ns2:contactInfo_ns2:contactLastName") + " "
						+ ReturnValues(resultOfParse, "ns2:contactInfo_ns2:contactFirstName") + " "
						+ ReturnValues(resultOfParse, "ns2:contactInfo_ns2:contactMiddleName"));
				org.setActual("");
				org.setPhone(ReturnValues(resultOfParse, "ns2:contactInfo_ns2:phone"));
				org.setOKTMO(ReturnValues(resultOfParse, "ns2:classification_ns2:oktmo"));
				org.setZip("");
				org.setPostalAddress(ReturnValues(resultOfParse, "ns2:mainInfo_postalAddress"));
				org.setOrganizationTypeCode(-1);
				org.setIKU("");
				org.setOkved(ReturnValues(resultOfParse, "ns2:okved2_ns2:code"));
				if (org.getOkved().length() > 20) {
					org.setOkved(org.getOkved().substring(0, 20));
				}

				OrgList.add(org);

			}
			if ((ReturnValues(resultOfParse, "ns2:mainInfo_inn").equals("")) && (resultOfParse.size() > 0)) {
				//					throw new NumberFormatException("Не заполнено поле ИНН");
				log.warning("Не заполнено поле ИНН");
				for (int i = 0; i < resultOfParse.size(); i++) {
					log.warning(("nodeName:" + resultOfParse.get(i).nodeName + "=" + resultOfParse.get(i).nodeValue));
				}

			}

		}
	}

	@Override
	public void insertToDatabase(Database data) {
		try {
			data.InsertOrg(OrgList);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
