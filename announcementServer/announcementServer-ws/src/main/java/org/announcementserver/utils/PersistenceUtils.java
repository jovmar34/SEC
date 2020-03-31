package org.announcementserver.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.announcementserver.ws.AnnouncementServer;

public class PersistenceUtils {
	
	public static final String FILENAME = "src/main/resources/serverState.ser";
	public static final String BACKUP_FILENAME = "src/main/resources/serverStateBackup.ser";
	
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
			try {
				fis = new FileInputStream(new File(BACKUP_FILENAME));
				ois = new ObjectInputStream(fis);
				AnnouncementServer announcementServer = (AnnouncementServer) ois.readObject();
				AnnouncementServer.setInstance(announcementServer);
			} catch(Exception j) {
				j.printStackTrace();
			}
		}
	}
	
	public static void serialize(AnnouncementServer announcementServer) {
		FileOutputStream fos = null;
		FileOutputStream fosb = null;
		ObjectOutputStream oos = null;
		ObjectOutputStream oosb = null;
		
		try {
			fos = new FileOutputStream(new File(FILENAME));
			fosb = new FileOutputStream(new File(BACKUP_FILENAME));
			oos = new ObjectOutputStream(fos);
			oosb = new ObjectOutputStream(fosb);
			oos.writeObject(announcementServer);
			oosb.writeObject(announcementServer);
			oos.close();
			oosb.close();
			fos.close();
			fosb.close();
		} catch(Exception i) {
			i.printStackTrace();
		}
	}
}