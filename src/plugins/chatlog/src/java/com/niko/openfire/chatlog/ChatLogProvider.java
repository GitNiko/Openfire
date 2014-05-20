package com.niko.openfire.chatlog;

public interface ChatLogProvider {
	void insertLog(String sender, String receiver, String content);
	//int updateSession(int userId, int touserId, int messageId);
	
	
}
