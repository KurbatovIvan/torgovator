package torgovator.torgovator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Node;

import torgovator.utils.ExceptionUtils;

public class XmlDocumentZakupka223 extends XmlDocument implements InsertableToDatabase {

	private static Logger log = Logger.getLogger(XmlDocumentZakupka.class.getName());
	private String prefixpurchaseNoticeData = "";

	public Zakupka zakupka = new Zakupka();

	public XmlDocumentZakupka223(String filaname) {
		super(filaname);
		try {
			List<CustomNode> resultOfParse = traverse(document.getDocumentElement(), "");

			zakupka.setFileName(FilenameUtils.getName(filaname));
			setPrefixpurchaseNoticeData(getPrefixpurchaseNoticeData(resultOfParse));
			zakupka.setPurchaseNumber(ReturnValues(resultOfParse, prefixpurchaseNoticeData + "ns2:registrationNumber"));
			zakupka.setPurchaseObjectInfo(ReturnValues(resultOfParse, prefixpurchaseNoticeData + "ns2:name"));
			zakupka.setINN(ReturnValues(resultOfParse, prefixpurchaseNoticeData + "ns2:customer_mainInfo_inn"));

			zakupka.setPublishDate(ReturnValues(resultOfParse, prefixpurchaseNoticeData + "ns2:publicationDateTime"));
			zakupka.setStartDate(ReturnValues(resultOfParse,
					prefixpurchaseNoticeData + "ns2:documentationDelivery_deliveryStartDateTime"));
			zakupka.setEndDate(ReturnValues(resultOfParse, prefixpurchaseNoticeData + "ns2:submissionCloseDateTime"));

			zakupka.setOKPD2((ReturnArrayValues(resultOfParse,
					//					"ns2:purchaseNotice_ns2:body_ns2:item_ns2:purchaseNoticeData_ns2:lots_lot_lotData_lotItems_lotItem_okpd2_code")));
					prefixpurchaseNoticeData + "ns2:lots_lot_lotData_lotItems_lotItem_okpd2_code")));
			zakupka.setMaxPrice(getinitialSum(resultOfParse));
			zakupka.setPlacingWay(ReturnValues(resultOfParse, prefixpurchaseNoticeData + "ns2:purchaseCodeName"));
			zakupka.setCUSTOMERREGNUM(
					ReturnValues(resultOfParse, prefixpurchaseNoticeData + "ns2:customer_mainInfo_iko"));

			zakupka.setZakon(1);
		} catch (NullPointerException e) {
			zakupka.setEmptyZakupka(true);
			log.warning("Плохой файл: " + filaname);
			log.warning(ExceptionUtils.ExceptionStackToString(e));
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
			//			papa = node.getNodeName() + "_";
			papa = papa + node.getNodeName() + "_";

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

	public double getinitialSum(List<CustomNode> resultOfParse) {
		List<String> initialSumArray;
		initialSumArray = ReturnArrayValues(resultOfParse,
				prefixpurchaseNoticeData + "ns2:lots_lot_lotData_initialSum");
		Double initialSum = 0.0;
		for (int i = 0; i < initialSumArray.size(); i++) {
			initialSum = initialSum + Double.parseDouble(initialSumArray.get(i));
		}
		if (initialSum.equals(0.0)) {
			initialSumArray = ReturnArrayValues(resultOfParse,
					prefixpurchaseNoticeData + "ns2:lots_lot_lotData_maxContractPrice");
			for (int i = 0; i < initialSumArray.size(); i++) {
				initialSum = initialSum + Double.parseDouble(initialSumArray.get(i));
			}

		}
		return initialSum;
	}

	private String getPrefixpurchaseNoticeData(List<CustomNode> resultofparse) {
		String result = "";
		String prefix = resultofparse.get(0).getNodeName();
		result = prefix + "_ns2:body_ns2:item_" + prefix + "Data_";
		return result;
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
	public String getPrefixpurchaseNoticeData() {
		return prefixpurchaseNoticeData;
	}

	/** @param prefixpurchaseNoticeData
	 *            the prefixpurchaseNoticeData to set */
	public void setPrefixpurchaseNoticeData(String prefixpurchaseNoticeData) {
		this.prefixpurchaseNoticeData = prefixpurchaseNoticeData;
	}
}
