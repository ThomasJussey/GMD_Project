package core;

import java.sql.*;
import java.util.ArrayList;

/** Réécrire la première requete correctemnt :maddra_all_se.SIDE_EFFECT_NAME -> meddra_all_se.CUI -> meddra.CUI -> meddra.LABEL
* rename la fonction de requete
 * faire une deuxieme focntion de requete pour SymptomToTreatment ( je connais aps le chemin dans la base de donnée
*/

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
            con = DriverManager.getConnection(DB_SERVER + DB, USER_NAME, USER_PSWD);
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
        ArrayList<String> inte = new ArrayList<String>();
        String w=new String();

        try{


            //SQL Query
            String query = "SELECT DISTINCT STITCH_COMPOUND_ID1 FROM  meddra_all_se WHERE SIDE_EFFECT_NAME='"+ symptom + "'";
            //String query = "SELECT DISTINCT LABEL FROM meddra WHERE CUI = 'C0036690'";
            //String query = "SELECT DISTINCT CONCEPT_NAME FROM meddra_all_indications WHERE STITCH_COMPOUND_ID ='CID024846132'";
            Statement st = con.createStatement();
            System.out.println("I'm doing the first query");
            ResultSet res = st.executeQuery(query);
            ResultSetMetaData r = res.getMetaData();
            System.out.println("First Query done");


            //Display of the results

            System.out.println("Process of the first results");
           /*System.out.println("Column Name");
            for(int i = 1; i <= r.getColumnCount(); i++) {
                System.out.print("\t" + r.getColumnName(i).toUpperCase() + "\t *");
             }*/

            System.out.println("Column content");
            while(res.next()) {
                for (int i = 1; i <= r.getColumnCount(); i++) {
                    w = res.getObject(i).toString();
                    System.out.print("\t" + w + "\t |");
                    inte.add(w);
                    System.out.println("\n---------------------------------");
                }
            }

            //Second Query
            int n = inte.size();
            for(int j =0; j<n; j++) {
                query = "SELECT DISTINCT CONCEPT_NAME FROM meddra_all_indications WHERE STITCH_COMPOUND_ID ='"+ inte.get(j) + "'";
                System.out.println("I'm doing the query n°"+j);
                res = st.executeQuery(query);
                r = res.getMetaData();
                System.out.println("Query n°"+j+" done");

                //Display of the results

                while (res.next()) {
                    for (int i = 1; i <= r.getColumnCount(); i++) {
                        w = res.getObject(i).toString();
                        if (!results.contains(w))
                        System.out.print("\t" + w + "\t |");
                        results.add(w);
                    }
                }
            }

            //Closing
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
       //try {
           String r = "sepsis";
           SiderMatching sdm = new SiderMatching();
           System.out.println("Let's GO !");
           System.out.println(con);
           ArrayList<String> results = sdm.searchFromSymptom(r);
           System.out.println(results.size());
      /* }catch(SQLException e){
           System.err.println(("SQLExeception"));
           while(e!=null) {
               System.err.println("Error msg : " + e.getMessage());
               System.err.println("SQLSTATE : " + e.getSQLState());
               System.err.println("Error code : " + e.getErrorCode());
               e.printStackTrace();
               e = e.getNextException();
           }
       }*/
      closeCon();
    }


}
