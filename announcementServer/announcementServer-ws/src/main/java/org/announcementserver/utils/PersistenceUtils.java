package org.announcementserver.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.announcementserver.ws.AnnouncementServer;

public class PersistenceUtils {
	
	public static final String FILENAME = "src/main/resources/serverstate.ser";
	
	public static void recover() {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		
		try {
			fis = new FileInputStream(new File(FILENAME));
			ois = new ObjectInputStream(fis);
			AnnouncementServer announcementServer = (AnnouncementServer) ois.readObject();
			AnnouncementServer.setInstance(announcementServer);
			ois.close();
			fis.close();
		} catch(Exception i) {
			i.printStackTrace();
		}
	}
	
	public static void serialize(AnnouncementServer announcementServer) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		
		try {
			fos = new FileOutputStream(new File(FILENAME));
			oos = new ObjectOutputStream(fos);
			oos.writeObject(announcementServer);
			oos.close();
			fos.close();
		} catch(Exception i) {
			i.printStackTrace();
		}
	}
}