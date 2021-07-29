package torgovator.torgovator;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.ini4j.InvalidFileFormatException;

import torgovator.utils.DayUtils;
import torgovator.utils.Downloader;
import torgovator.utils.EMailService;
import torgovator.utils.ExceptionUtils;
import torgovator.utils.FileUtils;
import torgovator.utils.Zipper;

/** Downloader! */

public class App {
	private static Database data = null;

	private static class Params extends ParamsWith223FZ {
	};

	private static Logger log = Logger.getLogger(App.class.getName());

	public static void main(String[] args) throws InvalidFileFormatException, IOException, SQLException {

		//		System.exit(0);

		//		============

		init();

		// Обрабатываю закачанные файлы и хреначу их в базу

		File DirTmp = new File(Params.getWorkDirTorgi().getWorkDirTmp());
		if (!DirTmp.exists()) {
			log.warning(
					"Временная папка отсутствует, делать нечего, уходим " + Params.getWorkDirTorgi().getWorkDirTmp());
			System.exit(2);
		}
		;

		System.out.println("Вставляю файлы в базу данных");
		XmlDocumentParseAndInsert(DirTmp.list());

		System.out.println("Очищаю папки");
		FileUtils.RemoveAllFileinDirTo(Params.getWorkDirTorgi());
		FileUtils.clear_directory(Params.getWorkDirTorgi());

		SendEmail();

	};

	private static void XmlDocumentParseAndInsert(String[] list) {
		for (String dirItem : list) {
			InsertFileToDatabase(dirItem);
		}

	}

	private static void InsertFileToDatabase(String dirItem) {
		XmlDocument DocXml;

		if (dirItem.contains("nsiOrganizationList")) {
			log.info("Закачиваю организации 44ФЗ: " + dirItem);
			DocXml = new XMLnsiOrganizationList(Params.getWorkDirTorgi().getWorkDirTmp() + "/" + dirItem);
			((XMLnsiOrganizationList) DocXml).insertToDatabase(data);

		} else if (dirItem.contains("Notification")) {
			log.info("Закачиваю закупки 44ФЗ: " + dirItem);
			DocXml = new XmlDocumentZakupka(Params.getWorkDirTorgi().getWorkDirTmp() + "/" + dirItem);
			((XmlDocumentZakupka) DocXml).insertToDatabase(data);
		} else if (dirItem.contains("purchaseNotice")) {
			log.info("Закачиваю закупки 223ФЗ: " + dirItem);
			DocXml = new XmlDocumentZakupka223(Params.getWorkDirTorgi().getWorkDirTmp() + "/" + dirItem);
			((XmlDocumentZakupka223) DocXml).insertToDatabase(data);

		} else if (dirItem.contains("nsiOrganization_")) {
			log.info("Закачиваю организации 223ФЗ: " + dirItem);
			DocXml = new XmlnsiOrganization(Params.getWorkDirTorgi().getWorkDirTmp() + "/" + dirItem);
			((XmlnsiOrganization) DocXml).insertToDatabase(data);

		}
	}

	private static void SendEmail() {
		// Вот тут приделать отправку писем
		//		Database data = new Database(Params.getDatabaseFDB());
		String Mailhost = Params.getMailhost();
		String Mailpasswd = Params.getMailpasswd();
		String Mailfrom = Params.getMailfrom();
		String Mailto = "";
		String USERID = "";
		String MailUsername = Params.getMailUsername();
		String MailMsg = "";
		String MailSubject = "Вероятно наши торги. Уведомление о событиях в закупках";

		ResultSet users = data.getDatasetUsers();

		try {
			log.info("Количество пользователей:" + users.getFetchSize());
			if (users.next())
				do {
					Mailto = users.getString(users.findColumn("EMAIL"));
					USERID = users.getString(users.findColumn("USERID"));

					MailMsg = getEmailString(USERID);
					System.out.println("RS закрыт:" + users.isClosed());
					if (MailMsg != "") {
						MailMsg = MailMsg + "\r\n Ваш любимый торговый робот";
						if (Params.isSendemail()) {
							EMailService.SendEMail(Mailhost, MailUsername, Mailpasswd, Mailfrom, Mailto, MailMsg,
									MailSubject);
						}
					} else {
						log.info("Письмо не шлем, ибо нечего слать");
					}
					System.out.println("RS закрыт2:" + users.isClosed());
				} while (users.next());
		} catch (SQLException e) {
			log.warning(ExceptionUtils.ExceptionStackToString(e));
		}

	}

	private static String getEmailString(String USERID) {
		// Вот тут не надо убирать подключения к БД, это новый объект чтобы не закрывался РезультСет с юзерами		
		Database data = new Database(Params.getDatabaseFDB());
		String head = FileUtils.readFile("htm/head.htm");
		String logo = FileUtils.readFile("htm/logo.htm");
		String SelectQuery = "select distinct " + "zk.placingway AS \"Способ размещения\",\r\n"
				+ "cust.purchasecode AS \"Идентификационный код закупки (ИКЗ)\",\r\n"
				+ " iif(ZK.ZAKON = 0, '44ФЗ', '223ФЗ') as \"ЗАКОН\",  iif(ZK.ZAKON = 0, 'http://zakupki.gov.ru/epz/order/notice/ea44/view/common-info.html?regNumber=' || zk.purchasenumber,'http://zakupki.gov.ru/223/purchase/public/purchase/info/common-info.html?regNumber=' || zk.purchasenumber) as \"Ссылка на закупку\","
				+ " zk.purchaseobjectinfo AS \"Наименование объекта закупки\",\r\n"
				+ " zk.startdate AS \"Дата и время начала подачи заявок\",\r\n"
				+ " zk.enddate AS \"Дата и время окончания подачи заявок\",\r\n"
				+ " zk.maxprice AS \"Начальная (максимальная) цена контракта\",\r\n"
				+ " zk.responsibleorg_fullname  AS \"Размещение осуществляет\",\r\n"
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
		data = new Database(ParamsWith223FZ.getDatabaseFDB());

		// Скачиваем Торги если не отладка
		if (!Params.isDebug()) {
			log.info("Скачиваем 44ФЗ");
			Downloader.Download(Params.getWorkDirTorgi(), Params.getDirToDownload(), Params.getFtpUrl(),
					Params.getFtpUserName(), Params.getFtpUserPasswd(), "");
			log.info("Скачиваем 223ФЗ");
			String mask = ""; //DayUtils.getNowYear_Month_Day();
			mask = Integer.toString(DayUtils.getNowYear());
			//			mask = "2018";
			log.info("Маска для скачиваня 223ФЗ : " + mask);
			Downloader.Download(Params.getWorkDirTorgi(), Params.getDirToDownload223FZ(), Params.getFtpUrl223Fz(),
					Params.getFtpUserName223Fz(), Params.getFtpUserPasswd223Fz(), mask);

		}
		// Распаковываем
		System.out.println("Распаковываем файлы");
		Zipper.unpackZipInDir(Params.getWorkDirTorgi(), Params.getPatternToExtractRegexp());

	}

}
