package com.niko.openfire.chatlog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.jivesoftware.util.JiveGlobals;

public class JDBCChatLogProvider implements ChatLogProvider{
	
	private static final Logger Log = LoggerFactory.getLogger(JDBCChatLogProvider.class);

	private String connectionString;
	private String saveMsgSql;
    private boolean allowUpdate;
    private boolean useConnectionProvider;
	
	public JDBCChatLogProvider() {
        // Convert XML based provider setup to Database based
        JiveGlobals.migrateProperty("jdbcProvider.driver");
        JiveGlobals.migrateProperty("jdbcProvider.connectionString");
        JiveGlobals.migrateProperty("jdbcChatLogProvider.saveMsgSql");
        
        useConnectionProvider = JiveGlobals.getBooleanProperty("jdbcChatLogProvider.useConnectionProvider");

        if (!useConnectionProvider) {
            // Load the JDBC driver and connection string.
            String jdbcDriver = JiveGlobals.getProperty("jdbcProvider.driver");
            try {
               Class.forName(jdbcDriver).newInstance();
            }
            catch (Exception e) {
                Log.error("Unable to load JDBC driver: " + jdbcDriver, e);
                return;
            }
            connectionString = JiveGlobals.getProperty("jdbcProvider.connectionString");
        }

        // Load SQL statements.
        saveMsgSql = JiveGlobals.getProperty("jdbcChatLogProvider.saveMsgSql");
        System.out.println("============================");
        System.out.println(saveMsgSql);
        
        
	}
	@Override
	public void insertLog(String sender, String receiver, String content) {
		String insertSql = "";
		Connection con = null;
		PreparedStatement pstmt = null;
		insertSql = "INSERT INTO SW_DIALOG(USERID,TOUSERID,CONTENT,TYPE,DATECREATE,ISREAD) VALUES(?, ?, ?, '1', NOW(), '1')";
        try {
            con = getConnection();
            pstmt = con.prepareStatement(insertSql);
            //pstmt.setString(1, username);
            pstmt.setInt(0, Integer.parseInt(sender));
            pstmt.setInt(1, Integer.parseInt(receiver));
            pstmt.setString(2, content);

            pstmt.execute();

        }
        catch (SQLException e) {
            Log.error("Exception in JDBCAuthProvider", e);
        }
        catch (Exception e) {
        	Log.error("Exception in JDBCAuthProvider", e);
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
		
	}
	
	private int updateSession(int userId, int touserId, int messageId) {
		int sessionId = 0;
		String updateSql = "";
		Connection con = null;
		PreparedStatement pstmt = null;
		updateSql = "INSERT INTO SW_DIALOG(USERID,TOUSERID,CONTENT) VALUES(?, ?, ?)";
        try {
            con = getConnection();
            pstmt = con.prepareStatement(updateSql);
            //pstmt.setString(1, username);

            pstmt.execute();

        }
        catch (SQLException e) {
            Log.error("Exception in JDBCAuthProvider", e);
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
		return sessionId;
	}
    private Connection getConnection() throws SQLException {
        if (useConnectionProvider)
            return DbConnectionManager.getConnection();
        return DriverManager.getConnection(connectionString);
    }

}
