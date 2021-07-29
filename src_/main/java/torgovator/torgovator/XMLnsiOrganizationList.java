package torgovator.torgovator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import torgovator.utils.ExceptionUtils;

public class XMLnsiOrganizationList extends XMLDocument {

	private Document document;
	public List<Organization> OrgList = new ArrayList<>();

	public XMLnsiOrganizationList(String filaname) throws ParserConfigurationException, SAXException, IOException {
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			// Создается дерево DOM документа из файла
			document = documentBuilder.parse(filaname);

			Node nsiOrganizationList = document.getFirstChild().getFirstChild(); // Докапываемся до организаций
			NodeList nodeList = nsiOrganizationList.getChildNodes();

			System.out.println(nodeList.getLength());

			for (int itr = 0; itr < nodeList.getLength(); itr++) {
				Node node = nodeList.item(itr);
				List<CustomNode> resultOfParse = traverse(node, "");
				//			System.out.println("resultOfParse:" + resultOfParse);
				Organization org = new Organization();
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
		} catch (SAXException e) {
			System.err.println(e.getLocalizedMessage());
			e.printStackTrace();
			log.warning("Возникла ошибка при обработке файла: " + filaname);
			log.warning(ExceptionUtils.ExceptionStackToString(e));

		} catch (IOException e) {
			System.err.println(e.getLocalizedMessage());
			e.printStackTrace();
			log.warning("Возникла ошибка при обработке файла: " + filaname);
			log.warning(ExceptionUtils.ExceptionStackToString(e));
		}
		setDocToInsert(OrgList);
	}

	// Вспомогательный класс, для хранения результатов разбора текущего узла,
	// которые потом надо отображать в строке
	private static final class CustomNode {
		protected String nodeName;
		protected String nodeValue = "null";

		@Override
		public String toString() {
			return nodeValue.replaceAll(";", "");
			// return nodeName + " - " + nodeValue;

		}

		public String getNodeName() {
			return nodeName;
		}

		public String getNodeValue() {
			return nodeValue;
		}
	}

	public static List<String> ReturnArrayValues(List<CustomNode> AA, String NameNode) {
		List<String> result = new ArrayList<>();

		for (CustomNode node : AA) {
			String NodeName = node.getNodeName();
			if (NodeName.equals(NameNode)) {
				result.add(node.getNodeValue());
			}
			;
		}

		return result;
	}

	private List<CustomNode> traverse(Node node, String papa) {
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

	public static String ReturnValues(List<CustomNode> AA, String NameNode) {
		String result = "";
		for (CustomNode node : AA) {
			String NodeName = node.getNodeName();

			if (NodeName.equals(NameNode)) {
				if (result.length() > 0) {
					result = result + ",";
				}
				;
				result = result + node.getNodeValue();
			}
		}

		return result;
	}

}
