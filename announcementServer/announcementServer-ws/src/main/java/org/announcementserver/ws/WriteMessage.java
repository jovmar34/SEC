package org.announcementserver.ws;

import java.io.Serializable;

import org.announcementserver.ws.Announcement;

public class WriteMessage implements Serializable {
    private static final long serialVersionUID = -5218179225677369083L;

    private String sender;
    private String destination;
    private Announcement announcement;
    private String signature;

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSender() {
        return this.sender;
    }

    public void setDestination(String destination) {
        this.destination= destination;
    }

    public String getDestination() {
        return this.destination;
    }

    public void setAnnouncement(Announcement announcement) {
        this.announcement = announcement;
    }

    public Announcement getAnnouncement() {
        return this.announcement;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignature() {
        return this.signature;
    }

    @Override
    public String toString() {
        return String.format("Sender: %s, Dest: %s", this.sender, this.destination);
    }
}