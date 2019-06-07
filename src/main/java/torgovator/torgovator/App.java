package torgovator.torgovator;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
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
		Database data = new Database(Params.getDatabaseFDB());

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

		// Очищаю папки		
		FileUtils.RemoveAllFileinDirTo(Params.getWorkDirTorgi());
		FileUtils.clear_directory(Params.getWorkDirTorgi());

		SendEmail();

	};

	private static void SendEmail() {
		// Вот тут приделать отправку писем
		Database data = new Database(Params.getDatabaseFDB());
		String Mailhost = Params.getMailhost();
		String Mailpasswd = Params.getMailpasswd();
		String Mailfrom = Params.getMailfrom();
		//String Mailto = Params.getMailto();
		String Mailto = "";
		String USERID = "";
		String MailUsername = Params.getMailUsername();
		String MailMsg = "";
		String MailSubject = "Вероятно наши торги. Уведомление о событиях в закупках";

		ResultSet users = data.getDatasetUsers();

		try {
			if (users.next())
				do {
					Mailto = users.getString(users.findColumn("EMAIL"));
					USERID = users.getString(users.findColumn("USERID"));
					MailMsg = getEmailString(USERID);
					if (MailMsg != "") {
						MailMsg = MailMsg + "\r\n Ваш любимый торговый робот";
						if (Params.isSendemail()) {
							EMailService.SendEMail(Mailhost, MailUsername, Mailpasswd, Mailfrom, Mailto, MailMsg,
									MailSubject);
						}
					} else {
						log.info("Письмо не шлем, ибо нечего слать");
					}
				} while (users.next());
		} catch (SQLException e) {
			log.warning(ExceptionUtils.ExceptionStackToString(e));
		}

	}

	private static String getEmailString(String USERID) {
		Database data = new Database(Params.getDatabaseFDB());
		String head = FileUtils.readFile("htm/head.htm");
		String logo = FileUtils.readFile("htm/logo.htm");
		String SelectQuery = "select distinct " + "zk.placingway AS \"Способ размещения\",\r\n"
				+ "cust.purchasecode AS \"Идентификационный код закупки (ИКЗ)\",\r\n"
				+ "zk.href AS \"Ссылка на закупку\",\r\n"
				+ "zk.purchaseobjectinfo AS \"Наименование объекта закупки\",\r\n"
				+ "zk.startdate AS \"Дата и время начала подачи заявок\",\r\n"
				+ "zk.enddate AS \"Дата и время окончания подачи заявок\",\r\n"
				+ "zk.maxprice AS \"Начальная (максимальная) цена контракта\",\r\n"
				+ "zk.responsibleorg_fullname  AS \"Размещение осуществляет\",\r\n"
				+ "SUBSTRING (cust.purchasecode from 4 for 10) \"ИНН Заказчика\", \r\n"
				+ "org.fullname  AS \"Полное наименование Заказчика\",\r\n" + "org.phone  AS \"Телефон\"\r\n"
				+ "/*,cast (SUBSTRING (zk.purchasecode from 4 for 10) as NUMERIC)*/\r\n" + "from\r\n" + "zakupki zk\r\n"
				+ "inner join customerrequirements cust on (zk.id=cust.recordindex)\r\n"
				+ "inner join purchaseobjects po on (zk.id=po.recordindex) left join organization org on (SUBSTRING (cust.purchasecode from 4 for 10)=org.inn and org.actual=1 and org.phone is not null)\r\n"
				+ " where (SUBSTRING (cust.purchasecode from 4 for 10) in ( select mo.inn from my_org mo where mo.userid='@GoogleID') "
				+ " or (zk.inn in (select mo.inn from my_org mo where mo.userid='@GoogleID') )) and zk.enddate>=current_timestamp "
				+ " and (zk.startdate>current_timestamp-1.5)  and  ((po.okpd2_code like '58%')\r\n" + "or\r\n"
				+ "(po.okpd2_code like '62%')\r\n" + " or\r\n" + "(po.okpd2_code like '63%'))";
		SelectQuery = SelectQuery.replace("@GoogleID", USERID);
		String msg = data.getDatasetString(SelectQuery);

		if (msg != "") {

			return "<html>" + head + "<body>" + logo + msg + "</body>" + "</html>";
		} else {
			return "";
		}

	}

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