package org.announcementserver.ws;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceContext;

@WebService(endpointInterface = "org.announcementserver.ws.AnnouncementServerPortType", wsdlLocation = "", name = "AnnouncementServerWebService", portName = "AnnouncementServerPort", targetNamespace = "http://ws.announcementserver.org", serviceName = "AnnouncementServerService")
public class AnnouncementServerPortImpl implements AnnouncementServerPortType {

	@Resource
	private WebServiceContext wsContext;

	/**
	 * The Endpoint manager controls the Web Service instance during its whole
	 * lifecycle
	 */
	@SuppressWarnings("unused")
	private final AnnouncementServerEndpointManager endpointManager;

	protected AnnouncementServerProxy proxy;

	/** Constructor receives a reference to the endpoint manager. */
	public AnnouncementServerPortImpl(AnnouncementServerEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
		this.proxy = new AnnouncementServerProxy();
	}

	// Main operations -----------------------------------------------

	/* Register */
	public RegisterRet register(RegisterReq request) {
		RegisterRet res = null;

		try {
			res = proxy.register(request);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}

		return res;
	}

	/* Post */
	public WriteRet post(WriteReq request) {
		WriteRet res = null;

		try {
			res = proxy.post(request);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}

		return res;
	}

	/* Post General */
	public WriteRet postGeneral(WriteReq request) {

		WriteRet res = null;

		try {
			res = proxy.postGeneral(request);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}

		return res;
	}

	/* Read */
	public ReadRet read(ReadReq request) {
		ReadRet res = null;

		try {
			res = proxy.read(request);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}

		return res;
	}

	/* Read General */
	public ReadRet readGeneral(ReadGeneralReq request){
		ReadRet res = null;

		try {
			res = proxy.readGeneral(request);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}

		return res;
	}

	/* Write Back */
	public WriteBackRet writeBack(WriteBackReq request) {
		WriteBackRet res = null;

		try {
			res = proxy.writeBack(request);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}

		return res;
	}
	
}