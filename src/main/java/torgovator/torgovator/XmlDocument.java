package torgovator.torgovator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import torgovator.utils.ExceptionUtils;

public class XmlDocument {
	private static Logger log = Logger.getLogger(XmlDocument.class.getName());
	public Document document;
	DocumentBuilder documentBuilder;

	public XmlDocument(String filaname) {
		DocumentBuilder documentBuilder;
		try {
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			// Создается дерево DOM документа из файла
			try {
				document = documentBuilder.parse(filaname);
			} catch (SAXException e) {
				log.warning(ExceptionUtils.ExceptionStackToString(e));
			} catch (IOException e) {
				System.err.println(e.getLocalizedMessage());
				e.printStackTrace();
				log.warning("Возникла ошибка при обработке файла: " + filaname);
				log.warning(ExceptionUtils.ExceptionStackToString(e));
			}

		} catch (ParserConfigurationException e) {
			System.err.println(e.getLocalizedMessage());
			e.printStackTrace();
			log.warning("Возникла ошибка при обработке файла: " + filaname);
			log.warning(ExceptionUtils.ExceptionStackToString(e));
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
			}
		}
		return result;
	}

	// Вспомогательный класс, для хранения результатов разбора текущего узла,
	// которые потом надо отображать в строке
	public static final class CustomNode {
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

	protected List<CustomNode> traverse(Node node, String papa) {
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
				//				if ((currentNode.nodeName == "purchaseObjectInfo") || (currentNode.nodeName == "href")
						|| (currentNode.nodeName == "docPublishDate")) {
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

	public static List<customerRequirement> ReturnArrayValuescustomerRequirement(List<CustomNode> AA) {
		customerRequirement customer = new customerRequirement();
		List<customerRequirement> result = new ArrayList<>();
		for (CustomNode node : AA) {
			String NodeName = node.getNodeName();
			if (NodeName.equals("customerRequirement_purchaseCode")) {
				customer.PURCHASECODE = node.getNodeValue();
			}
			if (NodeName.equals("customer_regNum")) {
				customer.REGNUM = node.getNodeValue();
			}
			if (!customer.REGNUM.equals("") && (!customer.PURCHASECODE.equals(""))) {
				result.add(customer);
				customer = new customerRequirement();
			}
			;
		}
		return result;
	}

}
