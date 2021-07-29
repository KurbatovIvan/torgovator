package torgovator.torgovator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import torgovator.utils.ExceptionUtils;

// Организации 44 ФЗ
public class XMLnsiOrganizationList extends XmlDocument implements InsertableToDatabase {
	private static Logger log = Logger.getLogger(XMLnsiOrganizationList.class.getName());
	public List<Organization> OrgList = new ArrayList<>();

	public XMLnsiOrganizationList(String filaname) {
		super(filaname);
		System.out.println("Закачиваем организации 44 ФЗ = " + filaname);

		Node nsiOrganizationList = document.getFirstChild().getFirstChild(); // Докапываемся до организаций
		// ns2:body		
		NodeList nodeList = nsiOrganizationList.getChildNodes();

		System.out.println("Количество организаций:" + nodeList.getLength());

		for (int itr = 0; itr < nodeList.getLength(); itr++) {
			Node node = nodeList.item(itr);
			List<CustomNode> resultOfParse = traverse(node, "");
			Organization org = new Organization();
			org.setFileName(FilenameUtils.getName(filaname));
			org.setId(Long.parseLong(ReturnValues(resultOfParse, "nsiOrganization_oos:INN")));
			org.setKPP(ReturnValues(resultOfParse, "nsiOrganization_oos:KPP"));
			org.setEmail(ReturnValues(resultOfParse, "nsiOrganization_oos:email"));
			org.setOGRN(ReturnValues(resultOfParse, "nsiOrganization_oos:OGRN"));
			org.setFullName(ReturnValues(resultOfParse, "nsiOrganization_oos:fullName"));
			org.setShortName(ReturnValues(resultOfParse, "nsiOrganization_oos:shortName"));
			org.setRegNumber(ReturnValues(resultOfParse, "nsiOrganization_oos:regNumber"));
			org.setContactPerson(ReturnValues(resultOfParse, "oos:contactPerson_oos:lastName") + " "
					+ ReturnValues(resultOfParse, "oos:contactPerson_oos:firstName") + " "
					+ ReturnValues(resultOfParse, "oos:contactPerson_oos:middleName"));

			org.setActual(ReturnValues(resultOfParse, "nsiOrganization_oos:actual"));
			org.setPhone(ReturnValues(resultOfParse, "nsiOrganization_oos:phone"));
			org.setOKTMO(ReturnValues(resultOfParse, "oos:OKTMO_oos:code"));
			org.setZip(ReturnValues(resultOfParse, "oos:factualAddress_oos:zip"));
			org.setPostalAddress(ReturnValues(resultOfParse, "nsiOrganization_oos:postalAddress"));
			org.setOrganizationTypeCode(ReturnValues(resultOfParse, "oos:organizationType_oos:code"));
			org.setIKU(ReturnValues(resultOfParse, "oos:IKUInfo_oos:IKU"));
			org.setOkved(ReturnValues(resultOfParse, "nsiOrganization_oos:OKVED"));

			// Проверяем есть ли уже такая организация в массиве				
			Boolean orgexist = false;
			for (Organization orgExist : OrgList) {
				if (org.equals(orgExist)) {
					orgexist = true;
				}
				;
			}
			if (orgexist == false)
				OrgList.add(org);

		}

	}

	protected List<CustomNode> traverse(Node node, String papa) {
		CustomNode currentNode = null;
		List<CustomNode> result = new ArrayList<>();

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			currentNode = new CustomNode();
			result.add(currentNode);
			currentNode.nodeName = node.getNodeName();

			if (papa != "") {
				currentNode.nodeName = papa + currentNode.nodeName;
			}
			papa = node.getNodeName() + "_";

			Node childNode = node.getFirstChild();
			while (childNode != null) {
				if (childNode.getNodeType() == Node.ELEMENT_NODE) {
					result.addAll(traverse(childNode, papa));
				} else if (childNode.getNodeType() == Node.TEXT_NODE) {
					String nodeText = childNode.getTextContent().trim();
					if (!"".equals(nodeText)) {
						currentNode.nodeValue = nodeText;
					}
				}

				childNode = childNode.getNextSibling();
			}

		}
		return result;
	}

	@Override
	public void insertToDatabase(Database data) {
		try {
			data.InsertOrg(OrgList);
		} catch (SQLException e) {
			log.warning(ExceptionUtils.ExceptionStackToString(e));
		}

	}

}
