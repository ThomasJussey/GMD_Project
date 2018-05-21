package core;

import org.json.*;

import java.util.ArrayList;

public class OrphaDataMatching {

    private static JSONObject data;

    public OrphaDataMatching(String path) {

        try {
            data = new JSONObject(path);
        } catch (JSONException e) {
            System.out.println("Problem while loading the JSON Object");
            e.printStackTrace();
        }
    }

    public static ArrayList<String> search (String symptom){

        ArrayList<String> results = new ArrayList<String>();

        try {
            JSONArray research = data.getJSONArray("ClassificationDisease");
            int n = research.length();
            for(int i = 0; i < n; i++){
                String disease = research.getJSONObject(i).getString("Name");
                if (disease.contains(symptom)){
                    results.add(disease);
                }
            }
        }catch (JSONException e){
            System.out.println("Problem while researching the JSON");
            e.printStackTrace();
        }
        return results;
    }


    public static void main (String[] args){
        OrphaDataMatching odm = new OrphaDataMatching(".sensibleData/CouchDB_DATA.json");
        ArrayList<String> results = search("bleeding");
        System.out.println(results.get(0));
    }





}
