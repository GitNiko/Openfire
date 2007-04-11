/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 2006-2007 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Public License (GPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.openfire.gateway.protocols.xmpp;

import org.jivesoftware.openfire.gateway.*;
import org.jivesoftware.openfire.roster.RosterItem;
import org.jivesoftware.util.Log;
import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.Chat;
import org.xmpp.packet.JID;
import org.xmpp.packet.Presence;

/**
 * Represents an XMPP session.
 *
 * This is the interface with which the base transport functionality will
 * communicate with an XMPP server.
 *
 * @author Daniel Henninger
 */
public class XMPPSession extends TransportSession {

    /**
     * Create an XMPP Session instance.
     *
     * @param registration Registration informationed used for logging in.
     * @param jid JID associated with this session.
     * @param transport Transport instance associated with this session.
     * @param priority Priority of this session.
     */
    public XMPPSession(Registration registration, JID jid, XMPPTransport transport, Integer priority) {
        super(registration, jid, transport, priority);

        Log.debug("Creating "+getTransport().getType()+" session for " + registration.getUsername());
        config = new ConnectionConfiguration(
                JiveGlobals.getProperty("plugin.gateway."+getTransport().getType()+".connecthost",
                        (getTransport().getType().equals(TransportType.gtalk) ? "talk.google.com" : "jabber.org")),
                JiveGlobals.getIntProperty("plugin.gateway."+getTransport().getType()+".connectport", 5222));
    }

    /*
     * XMPP connection
     */
    private XMPPConnection conn = null;

    /**
     * XMPP listener
     */
    private XMPPListener listener = null;

    /*
     * XMPP connection configuration
     */
    private ConnectionConfiguration config = null;

    public void logIn(PresenceType presenceType, String verboseStatus) {
        if (!this.isLoggedIn()) {
            try {
                listener = new XMPPListener(this);
                setLoginStatus(TransportLoginStatus.LOGGING_IN);
                conn = new XMPPConnection(config);
                conn.connect();
                conn.addConnectionListener(listener);
                conn.login(this.getRegistration().getUsername(), this.getRegistration().getPassword(), "IMGateway");
                conn.getRoster().addRosterListener(listener);
                conn.getChatManager().addChatListener(listener);
            }
            catch (XMPPException e) {
                Log.error(getTransport().getType()+" user is not able to log in: "+this.getRegistration().getUsername(), e);
            }
        }
    }

    /**
     * Log out of MSN.
     */
    public void logOut() {
        if (this.isLoggedIn()) {
            setLoginStatus(TransportLoginStatus.LOGGING_OUT);
            conn.removeConnectionListener(listener);
            conn.getRoster().removeRosterListener(listener);
            conn.getChatManager().removeChatListener(listener);
            conn.disconnect();
        }
        Presence p = new Presence(Presence.Type.unavailable);
        p.setTo(getJID());
        p.setFrom(getTransport().getJID());
        getTransport().sendPacket(p);
        setLoginStatus(TransportLoginStatus.LOGGED_OUT);
    }
    

    public void updateStatus(PresenceType presenceType, String verboseStatus) {
    }

    public void addContact(RosterItem item) {
    }

    public void removeContact(RosterItem item) {
    }

    public void updateContact(RosterItem item) {
    }

    public void sendMessage(JID jid, String message) {
        Chat chat = conn.getChatManager().createChat(getTransport().convertJIDToID(jid), listener);
        try {
            chat.sendMessage(message);
        }
        catch (XMPPException e) {
            // TODO: handle exception properly
        }
    }

    public void sendServerMessage(String message) {
        //Don't care
    }

    public void sendChatState(JID jid, ChatStateType chatState) {
    }

    public void retrieveContactStatus(JID jid) {
    }

    public void resendContactStatuses(JID jid) {
    }

}