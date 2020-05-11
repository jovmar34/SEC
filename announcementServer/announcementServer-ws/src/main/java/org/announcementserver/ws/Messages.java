package org.announcementserver.ws;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Messages {
    public static byte[] toByteArray(WriteMessage mess) {
        byte[] ret = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);   
            out.writeObject(mess);
            out.flush();
            ret = bos.toByteArray();
            bos.close();
        } catch (IOException e) {
            System.out.println("Issue with getting byte array");
        }

        return ret;
    }

    public static WriteMessage getWriteMessage(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        WriteMessage ret = null;
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            ret = (WriteMessage) in.readObject(); 
        } catch (IOException e) {
            System.out.println("Issue obtaining Write Message");
        } catch (ClassNotFoundException e) {
            System.out.println("Class couldn't be resolved");
        }
        return ret;
    }
}