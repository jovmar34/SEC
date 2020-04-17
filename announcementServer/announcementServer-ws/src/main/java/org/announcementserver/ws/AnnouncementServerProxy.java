package org.announcementserver.ws;

import java.util.List;

import org.announcementserver.exceptions.EmptyBoardException;
import org.announcementserver.exceptions.InvalidNumberException;
import org.announcementserver.exceptions.MessageSizeException;
import org.announcementserver.exceptions.NumberPostsException;
import org.announcementserver.exceptions.PostTypeException;
import org.announcementserver.exceptions.ReferredAnnouncementException;
import org.announcementserver.exceptions.ReferredUserException;
import org.announcementserver.exceptions.UserNotRegisteredException;

public class AnnouncementServerProxy {
    public AnnouncementServerProxy() {

    }

    public List<String> register(String publicKey, String signature) {
        return AnnouncementServer.getInstance().register(publicKey, signature);
    }

    public List<String> post(String publicKey, String message, List<String> announcementList, String signature)
            throws UserNotRegisteredException, MessageSizeException, ReferredUserException, PostTypeException,
            ReferredAnnouncementException {
        return AnnouncementServer.getInstance().post(publicKey, message, announcementList, signature);
    }

    public List<String> postGeneral(String publicKey, String message, List<String> announcementList, String signature)
            throws UserNotRegisteredException, MessageSizeException, ReferredUserException,
            ReferredAnnouncementException, PostTypeException {
        return AnnouncementServer.getInstance().postGeneral(publicKey, message, announcementList, signature);
    }

    public List<String> read(String readerKey, String publicKey, Long number, String signature)
            throws InvalidNumberException, ReferredUserException, EmptyBoardException, NumberPostsException,
            UserNotRegisteredException {
        return AnnouncementServer.getInstance().read(readerKey, publicKey, number, signature);
    }

    public List<String> readGeneral(String readerKey, Long number, String signature)
            throws InvalidNumberException, EmptyBoardException, NumberPostsException {
        return AnnouncementServer.getInstance().readGeneral(readerKey, number, signature);
    }

}