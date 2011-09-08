package http;

import java.io.File;

public class FileHandler {

	public FileHandler() {
	
	}
	
	public static String[] getFileList(String folderpath) {
		File dir = new File(folderpath);
		return dir.list();
	}
	
	public static int countFiles(String folderpath) {
		return getFileList(folderpath).length;
	}
	
	public static int countFiles(String folderpath, String type) {
		String[] files = getFileList(folderpath);
		int count = 0;
		for (int i=0; i<files.length; i++) {
			if (files[i].endsWith(type)) {
				count++;
			}
		}
		return count;
	}
	
	public static int countImageFiles(String folderpath) {
		int count = countFiles(folderpath, ".gif") +
					countFiles(folderpath, ".jpg") +
					countFiles(folderpath, ".jpeg") +
					countFiles(folderpath, ".png") +
					countFiles(folderpath, ".bmp");
		return count;
	}
	
}
