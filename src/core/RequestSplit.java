package core;

import java.util.ArrayList;
import java.util.List;

public class RequestSplit {

    public static String RequestSplitLucene(String request, String field){

        String finalRequest = "";

        //Splitting " AND " from the request
        String[] req1 = request.split(" OR ");
        int n = req1.length;

        String[] req2;
        //Splitting " OR " from req1
        for (int i = 0 ; i < n; i++){
            if (i != 0){
                finalRequest = finalRequest + " OR ";
            }
            req2 = req1[i].split(" AND ");
            int m = req2.length;
            finalRequest = finalRequest + "( " + field + ":\"" + req2[0] + "\"";
            for ( int j = 1 ; j < m; j++){
                finalRequest = finalRequest + " AND " + field + ":\"" + req2[j] + "\"";
            }
            finalRequest = finalRequest + " )";
        }
        return finalRequest;
    }

    public static String RequestSplitSQL(String request, String field){

        String finalRequest = "";

        //Splitting " AND " from the request
        String[] req1 = request.split(" OR ");
        int n = req1.length;

        String[] req2;
        //Splitting " OR " from req1
        for (int i = 0 ; i < n; i++){
            if (i != 0){
                finalRequest = finalRequest + " OR ";
            }
            req2 = req1[i].split(" AND ");
            int m = req2.length;
            finalRequest = finalRequest + "( " + field + "='" + req2[0] + "'";
            for ( int j = 1 ; j < m; j++){
                finalRequest = finalRequest + " AND " + field + "='" + req2[j] + "'";
            }
            finalRequest = finalRequest + " )";
        }
        return finalRequest;
    }

    public static ArrayList<String> RequestSplit (String request){
        ArrayList<String> Symptoms = new ArrayList<>();

        //Splitting " AND " from the request
        String[] req1 = request.split(" OR ");
        int n = req1.length;

        String[] req2;
        //Splitting " OR " from req1
        for (int i = 0 ; i < n; i++){
            req2 = req1[i].split(" AND ");
            int m = req2.length;
            for ( int j = 0 ; j < m; j++){
                Symptoms.add(req2[j]);
            }

        }

        return Symptoms;
    }

    public static String  ConstructLuceneRequest(ArrayList<String> symptoms, String field){
        String request=field + ":\"" + symptoms + "\"";
        int m = symptoms.size();
        for (int i = 1; i<m ; i++){
            request=request + " AND " + field + ":\"'" + symptoms + "\"";
        }
        return request;
    }

    public static void main (String[] args){
        String r = "Bonjour AND MANGER OR DORMIR AND MANGER OR MIMA AND KEBAB AND YOLO AND YOLOSWAG OR YOLOSWAGDEPOULET OR STOP AND WESH";
        //String r = "bleed*";
        String r1 = RequestSplitSQL(r, "CONCEPT_NAME");
        /*int m = r1.size();
        for (int i = 0; i<m;i++){*/
            System.out.println(r1);
        //}
    }
}
