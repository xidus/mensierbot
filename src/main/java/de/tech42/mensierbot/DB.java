/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tech42.mensierbot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dan
 */
public class DB {

    private Connection con;
    private String lastUsedUser;

    public DB(String filename) {
        try {
            Class.forName("org.sqlite.JDBC");
            this.con = DriverManager.getConnection("jdbc:sqlite:" + filename);
        } catch (Exception ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public BotSettings readSettings(String username) {
        BotSettings botSet = new BotSettings();
        this.lastUsedUser = username;
        try {


            PreparedStatement stmt = con.prepareStatement("SELECT * FROM settings WHERE username = ?;");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                botSet.username = rs.getString("username");
                botSet.oAuthAccessToken = rs.getString("oAuthAccessToken");
                botSet.oAuthAccessTokenSecret = rs.getString("oAuthAccessTokenSecret");
                botSet.oAuthConsumerKey = rs.getString("oAuthConsumerKey");
                botSet.oAuthConsumerSecret = rs.getString("oAuthConsumerSecret");
                botSet.lastTweet = rs.getLong("lastTweet");
            }

            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }

        return botSet;
    }

    public void updateLastTweet(long sinceId) {
        try {
            PreparedStatement stmt = con.prepareStatement("UPDATE settings SET lastTweet = ? WHERE username = ?;");
            stmt.setLong(1, sinceId);
            stmt.setString(2, this.lastUsedUser);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public MensaZeit getMensaZeit(String day) {
        MensaZeit mz = new MensaZeit();
        try {
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM times WHERE day = ? ORDER BY time ASC;");
            stmt.setString(1, day);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String zeit = rs.getString("time");
                String user = rs.getString("user");

                if (mz.get(zeit) == null) {
                    mz.put(zeit, new ArrayList<String>());
                }

                mz.get(zeit).add(user);
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mz;
    }

    public void setYes(String user, String datum, String zeit) {
        try {
            PreparedStatement stmt = con.prepareStatement("INSERT INTO times (user, day, time) VALUES (?,?,?)");
            stmt.setString(1, user);
            stmt.setString(2, datum);
            stmt.setString(3, zeit);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
        public void setNo(String user, String datum, String zeit) {
        try {
            PreparedStatement stmt = con.prepareStatement("DELETE FROM times WHERE user = ? AND day = ? AND time = ?;");
            stmt.setString(1, user);
            stmt.setString(2, datum);
            stmt.setString(3, zeit);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
                public void setNoAlle(String user, String datum) {
        try {
            PreparedStatement stmt = con.prepareStatement("DELETE FROM times WHERE user = ? AND day = ?;");
            stmt.setString(1, user);
            stmt.setString(2, datum);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
