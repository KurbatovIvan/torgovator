package torgovator.utils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;
import java.util.stream.Stream;

import torgovator.torgovator.App;
import torgovator.torgovator.WorkDir;

public class FileUtils {

	private FileUtils() {
		// Закрытый конструктор , чтобы никто не создал экземпляр класса (объект)		
		throw new AssertionError();
	}

	private static Logger log = Logger.getLogger(App.class.getName());

	/* Перемещатор файлов */
	/** @param newdir
	 *            Куда перемещать
	 * @param filename
	 *            Имя файла
	 * @return возвращает true или false */
	public static boolean RemoveFileTo(String newdir, String filename) {
		File file = new File(filename);
		// File dir = new File(newdir);
		boolean success = file.renameTo(new File(newdir + "/", file.getName()));
		return success;
	}

	public static boolean fileExist(String filePathString) {
		File f = new File(filePathString);
		if (f.exists() && !f.isDirectory()) {
			return true;
		} else
			return false;
	}

	public static void RemoveAllFileinDirTo(WorkDir workdir) {
		if (workdir != null) {
			RemoveAllFileinDirTo(workdir.getWorkDirOk(), workdir.getWorkDirIn());
		} else {
			log.warning("Параметры для перемещения неизвестны ");
		}
	}

	public static void RemoveAllFileinDirTo(String newdir, String olddir) {
		log.info("Перемещаю файлы архива");
		File DirIn = new File(olddir);
		String[] list = DirIn.list(); // Список файлов
		for (String dirItem : list) {

			if (FileUtils.RemoveFileTo(newdir, olddir + "/" + dirItem)) {
				log.info("Переместил файл " + dirItem);
			} else {
				log.warning("Не смог переместить файл " + dirItem);
			}
		}
	}

	public static void clear_directory(WorkDir workdir) {
		if (workdir == null) {
			log.warning("Не заполнен один или несколько обязательных параметров для очистки папки");
			return;
		}

		clear_directory(workdir.getWorkDirTmp());

	}

	/* Удалятор файлов */
	public static void clear_directory(String path) {
		log.info("Очищаю папку " + path);
		try {
			for (File file : new File(path).listFiles())
				if (file.isFile())
					file.delete();
		} catch (Exception e) {
			log.warning("Не смог очистить папку " + path);
			log.warning(e.getMessage());
		}

	}

	public static String readFile(String filePath) {
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");
		try {

			try (Stream<String> lines = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
				for (String line : (Iterable<String>) lines::iterator) {

					stringBuilder.append(line);
					stringBuilder.append(ls);

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.warning(ExceptionUtils.ExceptionStackToString(e));
		}
		return stringBuilder.toString();
	}

}
