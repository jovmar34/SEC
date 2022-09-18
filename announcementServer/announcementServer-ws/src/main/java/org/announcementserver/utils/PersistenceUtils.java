package org.announcementserver.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.announcementserver.ws.AnnouncementServer;

public class PersistenceUtils {
	
	public static final String FILEPATH = "src/main/resources/";
	
	public static void recover(String serverId) {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		
		try {
			fis = new FileInputStream(new File(FILEPATH+serverId+"State.ser"));
			ois = new ObjectInputStream(fis);
			AnnouncementServer announcementServer = (AnnouncementServer) ois.readObject();
			AnnouncementServer.setInstance(announcementServer);
			ois.close();
			fis.close();
		} catch(Exception i) {
			try {
				fis = new FileInputStream(new File(FILEPATH+serverId+"StateBackup.ser"));
				ois = new ObjectInputStream(fis);
				AnnouncementServer announcementServer = (AnnouncementServer) ois.readObject();
				AnnouncementServer.setInstance(announcementServer);
			} catch(Exception j) {
				j.printStackTrace();
			}
		}
	}
	
	public static void serialize(AnnouncementServer announcementServer, String serverId) {
		FileOutputStream fos = null;
		FileOutputStream fosb = null;
		ObjectOutputStream oos = null;
		ObjectOutputStream oosb = null;
		
		try {
			fos = new FileOutputStream(new File(FILEPATH+serverId+"State.ser"));
			oos = new ObjectOutputStream(fos);
			oos.writeObject(announcementServer);
			oos.close();
			fos.close();
			fosb = new FileOutputStream(new File(FILEPATH+serverId+"StateBackup.ser"));
			oosb = new ObjectOutputStream(fosb);
			oosb.writeObject(announcementServer);
			oosb.close();
			fosb.close();
		} catch(Exception i) {
			i.printStackTrace();
		}
	}
}