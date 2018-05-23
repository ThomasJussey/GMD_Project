package core;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

class HPO_annotationsMatching {


        static String DRIVER= "com.mysql.jdbc.Driver";
        static Connection con;

        public HPO_annotationsMatching(String path){
                try {
                        System.out.println("I'm connecting");
                        Class.forName(DRIVER);
                        con = DriverManager.getConnection(path);
                        System.out.println("I'm connected");
                } catch (ClassNotFoundException e) {
                        System.err.println("Problem while loading the JDBC driver");
                        System.out.println("Exception :" + e);
                        e.printStackTrace();
                } catch (SQLException e) {
                        System.err.println(("SQLExeception"));
                        while (e != null) {
                                System.err.println("Error msg : " + e.getMessage());
                                System.err.println("SQLSTATE : " + e.getSQLState());
                                System.err.println("Error code : " + e.getErrorCode());
                                e.printStackTrace();
                                e = e.getNextException();
                        }

                }
        }

        public static void main(String args[]){
                //try {
                String r = "sepsis";
                HPO_annotationsMatching ham = new HPO_annotationsMatching(".sensibleData/hpo_annoyations.sqlite");
                System.out.println("Let's GO !");
                System.out.println(con);

        }

}