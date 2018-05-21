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

    public static void main (String[] args){
        //String r = "Bonjour AND MANGER OR DORMIR AND MANGER OR MIMA AND KEBAB AND YOLO AND YOLOSWAG\n OR YOLOSWAGDEPOULET OR STOP AND WESH";
        String r = "bleed*";
        String r1 = RequestSplitLucene(r, "Toxicity");
        System.out.println(r1);
    }
}
