package torgovator.torgovator;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.ini4j.InvalidFileFormatException;
import org.xml.sax.SAXException;

import torgovator.utils.Downloader;
import torgovator.utils.EMailService;
import torgovator.utils.ExceptionUtils;
import torgovator.utils.FileUtils;
import torgovator.utils.Zipper;

/** Downloader! */

public class App {

	private static Logger log = Logger.getLogger(App.class.getName());

	public static void main(String[] args) throws InvalidFileFormatException, IOException, SQLException {
		init();

		// Обрабатываю закачанные файлы и хреначу их в базу

		File DirTmp = new File(Params.getWorkDirTorgi().getWorkDirTmp());
		if (!DirTmp.exists()) {
			log.warning(
					"Временная папка отсутствует, делать нечего, уходим " + Params.getWorkDirTorgi().getWorkDirTmp());
			System.exit(2);
		}
		;
		String[] list = DirTmp.list();
		Database data = new Database();

		for (String dirItem : list) {
			if (!dirItem.contains("nsiOrganization")) {
				try {
					XmlDocumentZakupka Doc = new XmlDocumentZakupka(
							Params.getWorkDirTorgi().getWorkDirTmp() + "/" + dirItem);
					Doc.zakupka.setFileName(dirItem);
					data.InsertZakupki(Doc.zakupka);

				} catch (ParserConfigurationException e) {
					log.warning(ExceptionUtils.ExceptionStackToString(e));
				} catch (SAXException e) {
					log.warning(ExceptionUtils.ExceptionStackToString(e));
				}
			} else {
				// Закачиваем организации				
				System.out.println("Закачиваем организации = " + dirItem);
				try {
					XMLnsiOrganizationList OrganizationList = new XMLnsiOrganizationList(
							Params.getWorkDirTorgi().getWorkDirTmp() + "/" + dirItem);
					data.InsertOrg(OrganizationList.OrgList, dirItem);
				} catch (ParserConfigurationException e) {
					log.warning(ExceptionUtils.ExceptionStackToString(e));
				} catch (SAXException e) {
					log.warning(ExceptionUtils.ExceptionStackToString(e));
				}

			}

		}

		FileUtils.RemoveAllFileinDirTo(Params.getWorkDirTorgi());

		// Очищаю папку 
		FileUtils.clear_directory(Params.getWorkDirTorgi());

		// Вот тут приделать отправку писем			
		String Mailhost = Params.getMailhost();
		String Mailpasswd = Params.getMailpasswd();
		String Mailfrom = Params.getMailfrom();
		String Mailto = Params.getMailto();
		String MailUsername = Params.getMailUsername();
		String MailMsg = "";
		MailMsg = data.getDatasetString("select distinct " + "zk.placingway AS \"Способ размещения\",\r\n"
				+ "cust.purchasecode AS \"Идентификационный код закупки (ИКЗ)\",\r\n"
				+ "zk.href AS \"Ссылка на закупку\",\r\n"
				+ "zk.purchaseobjectinfo AS \"Наименование объекта закупки\",\r\n"
				+ "zk.startdate AS \"Дата и время начала подачи заявок\",\r\n"
				+ "zk.enddate AS \"Дата и время окончания подачи заявок\",\r\n"
				+ "zk.maxprice AS \"Начальная (максимальная) цена контракта\",\r\n"
				+ "zk.responsibleorg_fullname  AS \"Размещение осуществляет\",\r\n"
				+ "SUBSTRING (cust.purchasecode from 4 for 10) \"ИНН Заказчика\" \r\n"
				+ "/*,cast (SUBSTRING (zk.purchasecode from 4 for 10) as NUMERIC)*/\r\n" + "from\r\n" + "zakupki zk\r\n"
				+ "inner join customerrequirements cust on (zk.id=cust.recordindex)\r\n"
				+ "inner join purchaseobjects po on (zk.id=po.recordindex)\r\n" + "\r\n" + "where\r\n"
				+ "(SUBSTRING (cust.purchasecode from 4 for 10) in (\r\n" + "select mo.inn\r\n" + "from my_org mo\r\n"
				+ ") or (zk.inn in\r\n" + "(select mo.inn from my_org mo) ))\r\n" + "\r\n"
				+ "and zk.enddate>=current_timestamp\r\n" + "\r\n" + "       \r\n" + "and\r\n" + "(\r\n"
				+ "(po.okpd2_code like '58%')\r\n" + "or\r\n" + "(po.okpd2_code like '62%')\r\n" + "or\r\n"
				+ "(po.okpd2_code like '63%')\r\n" + ")\r\n" /*
																 * + "and\r\n" + "zk.id in\r\n" +
																 * "(select max(zz.id) from zakupki zz group by zz.purchasenumber)\r\n"
																 * + "\r\n" + "\r\n" + "order by zk.startdate"
																 */);
		if (MailMsg == "") {
			MailMsg = "Нет торгов по нашим клиентам и по нашим услугам";
		}
		;
		MailMsg = MailMsg + "\r\n Ваш любимый торговый робот";
		if (Params.isSendemail()) {
			EMailService.SendEMail(Mailhost, MailUsername, Mailpasswd, Mailfrom, Mailto, MailMsg,
					"Вероятно наши торги");
		}

	};

	public static void init() {
		try {
			LogManager.getLogManager().readConfiguration(App.class.getResourceAsStream("/logging.properties"));
		} catch (IOException e) {
			log.info(("Could not setup logger configuration: " + e.toString()));
		} catch (NullPointerException e) {
			log.info(("Could not setup logger configuration: " + e.toString()));
		}
		new Params();

		// Скачиваем Торги если не отладка
		if (!Params.isDebug()) {
			Downloader.Download(Params.getWorkDirTorgi(), Params.getDirToDownload(), Params.getFtpUrl(),
					Params.getFtpUserName(), Params.getFtpUserPasswd());
		}
		// Распаковываем
		Zipper.unpackZipInDir(Params.getWorkDirTorgi(), Params.getPatternToExtractRegexp());

	}

}