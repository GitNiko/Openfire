package com.niko.openfire.chatlog;

import org.jivesoftware.openfire.group.DefaultGroupProvider;
import org.jivesoftware.openfire.group.GroupManager;
import org.jivesoftware.openfire.group.GroupProvider;
import org.jivesoftware.util.ClassUtils;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class ChatLogManager {
	private static final Logger Log = LoggerFactory.getLogger(ChatLogManager.class);
	
	private ChatLogProvider provider;
	
    private static final class ChatLogManagerContainer {
        private static final ChatLogManager instance = new ChatLogManager();
    }
    
    public static ChatLogManager getInstance() {
    	return ChatLogManagerContainer.instance;
    }
    
    private ChatLogManager() {
    	System.out.println("============init ChatLogMananger===============");
    	initProvider();
    }
    
    private void initProvider() {
    	JiveGlobals.migrateProperty("provider.chatlog.className");
    	
        String className = JiveGlobals.getProperty("provider.chatlog.className",
                "com.niko.openfire.chatlog.JDBCChatLogProvider");
        try {
            Class c = ClassUtils.forName(className);
            provider = (ChatLogProvider) c.newInstance();
        }
        catch (Exception e) {
            Log.error("Error loading group provider: " + className, e);
            provider = new JDBCChatLogProvider();
        }
    }
    
    public void insertLog() {
    	provider.insertLog("","","");
    }

}
