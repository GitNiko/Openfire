/**
 * 
 */
package com.niko.openfire.chatlog;

import java.io.File;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Message;
import org.xmpp.packet.IQ;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
/**
 * @author niko
 *
 */
public class ChatLogPlugin implements PacketInterceptor, Plugin {
	
	private XMPPServer server;
	// 
	private InterceptorManager interceptorManager;
	//private ChatLogManager chatLogManager;
	
	public ChatLogPlugin() {
		interceptorManager = InterceptorManager.getInstance();
		//chatLogManager = ChatLogManager.getInstance();
	}

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		server = XMPPServer.getInstance();
		// regist interceptor
		interceptorManager.addInterceptor(this);
		System.out.println("installing plugin");
		System.out.println(server.getServerInfo());
	}

	@Override
	public void destroyPlugin() {
		// destory interceptor
		interceptorManager.removeInterceptor(this);
		System.out.println("destory plugin");
	}

	@Override
	public void interceptPacket(Packet packet, Session session,
			boolean incoming, boolean processed) throws PacketRejectedException {
		System.out.println("enter interceptPacket");
		if (packet != null) {
			if (packet instanceof Message) {
				Message msg = (Message)packet;
				//System.out.println("chat log:" + msg.toXML());
				//chatLogManager.insertLog();
				
			} else if (packet instanceof IQ) {
				IQ iq = (IQ)packet;
				System.out.println(iq.getType());
				if (iq.getType() == IQ.Type.get) {
					// client request
					System.out.println("==================get================");
					Element docIq = iq.getElement();
					Element query = docIq.element("query");
					if (query != null) {
						if (query.getNamespaceForURI("jabber:iq:roster") != null) {
							// client request roster query
							System.out.println("*************get roster*******************");
						}
					}
					
					
				}
				System.out.println("IQ log:" + iq.toXML());
			}
		}
		
	}
	

}
