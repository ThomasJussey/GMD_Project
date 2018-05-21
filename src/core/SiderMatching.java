package core;

import java.sql.*;
import java.util.ArrayList;


public class SiderMatching {

    static String DB_SERVER = "jdbc:mysql://neptune.telecomnancy.univ-lorraine.fr/";
    static String DB = "gmd";
    static String DRIVER= "com.mysql.jdbc.Driver";
    static String USER_NAME = "gmd-read";
    static String USER_PSWD = "esial";
    static Connection con;

    public SiderMatching() {
        try {
            System.out.println("I'm connecting");
            Class.forName(DRIVER);
            Connection con = DriverManager.getConnection(DB_SERVER + DB, USER_NAME, USER_PSWD);
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

    public static ArrayList<String> searchFromSymptom (String symptom){

        ArrayList<String> results = new ArrayList<String>();

        try{


            //SQL Query
            String query = "(SELECT umls_concept_id_for_meddra_term FROM meddra_all_se WHERE " + symptom+")";
            query = "SELECT concept_name FROM meddra_all_indications WHERE umls_concept_id_for_meddra_term =" + query ;
            Statement st = con.createStatement();
            System.out.println("I'm doing the query");
            ResultSet res = st.executeQuery(query);
            System.out.println("Query done");


            //Display of the results
            while (res.next()){
                results.add(res.getString("concept_name"));
            }
            System.out.println("Process of the results");

            res.close();
            st.close();

            System.out.println("END");



        }catch (SQLException e){
            System.err.println(("SQLExeception"));
            while(e!=null){
                System.err.println("Error msg : " + e.getMessage());
                System.err.println("SQLSTATE : " + e.getSQLState());
                System.err.println("Error code : " + e.getErrorCode());
                e.printStackTrace();
                e=e.getNextException();
            }
        }
        return results;

    }

    public static void closeCon(){
        try {
            con.close();
        } catch (SQLException e){
            System.err.println(("SQLExeception"));
            while(e!=null){
                System.err.println("Error msg : " + e.getMessage());
                System.err.println("SQLSTATE : " + e.getSQLState());
                System.err.println("Error code : " + e.getErrorCode());
                e.printStackTrace();
                e=e.getNextException();
            }
        }
    }

    public static void main(String args[]){
        String r = "plop";
        SiderMatching sdm = new SiderMatching();
        System.out.println("Let's GO !");
        ArrayList<String> results = sdm.searchFromSymptom(r);
        System.out.println(results.get(0));
    }


}
