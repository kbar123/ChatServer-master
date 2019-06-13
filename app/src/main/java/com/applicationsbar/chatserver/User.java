package com.applicationsbar.chatserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class User {

    public  String firstName; //the first name of the player
    public  String lastName; //the last name of the player
    public  String password; //the password for the account
    public  String username; //the username for the account
    public int points; // points earned
    public int user_id; //user's identification number
    public static User getUser(String username, String password){
        String sql="select * from users where username=? and password=?";
        Connection con= MySql.getConnection();
        User u = null;
        try {
            PreparedStatement st=con.prepareStatement(sql);
            st.setString(1,username);
            st.setString(2,password);
            ResultSet rs=st.executeQuery();
            if (rs.next()) {
                u=new User();
                u.firstName = rs.getString("firstName");
                u.lastName = rs.getString("lastName");
                u.points = rs.getInt("points");
                u.username = username;
                u.password = password;
                u.user_id = rs.getInt("user_id");
            }

            rs.close();
            st.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return u;


    }

    public static void addUser(String firstName, String lastName,String username, String password)
    {
        String zero = "0";
        String sql = "INSERT INTO `sketchat`.`users` (`username`, `password`, `firstName`, `lastName`, `points`)" +
                " VALUES ('"+username+"','"+password+"','"+firstName+"','"+lastName+"','"+zero+"')";


        Connection con= MySql.getConnection();
        try {
            Statement st=con.createStatement();
            st.executeUpdate(sql);
            con.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPoints()
    {
        int user;
        int gain = this.points + 5;
        this.points =gain;
        String num = Integer.toString(gain);

        Connection con= MySql.getConnection();
        try {

            String update ="UPDATE `sketchat`.`users` SET `points` = '"+gain+"' WHERE (`user_id` = '"+this.user_id+"')";
            Statement st=con.createStatement();
            st.executeUpdate(update);
            con.close();
            st.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
    public static String getTopUsers() {
        String sql = "select * from users order by points desc";
        Connection con = MySql.getConnection();

        String result = "";
        try {
            PreparedStatement st = con.prepareStatement(sql);

            ResultSet rs = st.executeQuery();
            int i = 0;

            while (rs.next() && i < 10) {

                if (i == 0)
                    result = rs.getString("firstName") + " " + rs.getString("lastName") + " " + rs.getInt("points");
                else
                    result = result + "&" + rs.getString("firstName") + " " + rs.getString("lastName") + " " + rs.getInt("points");
                i++;
            }

            rs.close();
            st.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;

        }

    }
