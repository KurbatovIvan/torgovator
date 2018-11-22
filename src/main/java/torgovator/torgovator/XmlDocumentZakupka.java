package torgovator.torgovator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class XmlDocumentZakupka {
	private Document document;
	public Zakupka zakupka = new Zakupka();
	// public NodeList purchaseObjects;
	// public NodeList customerRequirements;

	public XmlDocumentZakupka(String filaname) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		// Создается дерево DOM документа из файла
		document = documentBuilder.parse(filaname);
		List<CustomNode> resultOfParse = traverse(document.getDocumentElement(), "");

		/*
		 * NodeList nodeList = document.getElementsByTagName("customerRequirements");
		 * System.out.println(nodeList.getLength());
		 * 
		 * for (int itr = 0; itr < nodeList.getLength(); itr++) { Node node =
		 * nodeList.item(itr); System.out.println("\nNode Name :" + node.getNodeName());
		 * if (node.getNodeType() == Node.TEXT_NODE) {
		 * System.out.println(node.getTextContent()); } }
		 */
		zakupka.setPurchaseNumber(ReturnValues(resultOfParse, "purchaseNumber"));

		zakupka.setPURCHASECODE1(ReturnArrayValues(resultOfParse, "customerRequirement_purchaseCode"));

		zakupka.setOKPD2((ReturnArrayValues(resultOfParse, "OKPD2_code")));
		if (zakupka.getOKPD2().size() == 0) {
			zakupka.setOKPD2((ReturnArrayValues(resultOfParse, "KTRU_code")));
		}

		zakupka.setPurchaseObjectInfo(ReturnValues(resultOfParse, "purchaseObjectInfo"));
		zakupka.setINN(ReturnValues(resultOfParse, "responsibleOrg_INN"));
		zakupka.setResponsibleOrg_fullName(ReturnValues(resultOfParse, "responsibleOrg_fullName"));
		zakupka.setRegnum(ReturnValues(resultOfParse, "responsibleOrg_regNum"));
		zakupka.setHref(ReturnValues(resultOfParse, "href"));

		zakupka.setPublishDate(ReturnValues(resultOfParse, "docPublishDate"));

		zakupka.setStartDate(ReturnValues(resultOfParse, "collecting_startDate"));
		zakupka.setEndDate(ReturnValues(resultOfParse, "collecting_endDate"));
		zakupka.setMaxPrice(Double.parseDouble(ReturnValues(resultOfParse, "lot_maxPrice")));

		zakupka.setPlacingWay((ReturnValues(resultOfParse, "placingWay_name")));

	}

	private List<CustomNode> traverse(Node node, String papa) {
		CustomNode currentNode = null;
		List<CustomNode> result = new ArrayList<>();

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			currentNode = new CustomNode();
			result.add(currentNode);
			currentNode.nodeName = node.getNodeName();
			if (papa != "") {
				// currentNode.nodeName = papa + currentNode.nodeName;
				// Пока работаем без папы, может понадобится в будушем

				if ((currentNode.nodeName == "purchaseNumber") || (currentNode.nodeName == "purchaseObjectInfo")
						|| (currentNode.nodeName == "href") || (currentNode.nodeName == "docPublishDate")) {
					currentNode.nodeName = currentNode.nodeName;
				} else
					currentNode.nodeName = papa + currentNode.nodeName;
			}
			;
			papa = node.getNodeName() + "_";
		}

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

		return result;
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
				// return node.getNodeValue();
			}
		}

		return result;
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

}
