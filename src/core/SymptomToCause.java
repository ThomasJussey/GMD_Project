package core;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * This class correspond to the project's first problematic :
 * "From a sign or a symptom, find all the diseases that could be the cause, and all the drugs that could have it as a side effect"
 */
public class SymptomToCause{

    private String causes;

    /**
     * This method will be using all the recquired objects to build the recquired objects. We will need :
     *      - The "toxicity" from "Drugbank", which correspond to the drugs' side effects
     *      - The "clinical sign" from "OrphaData", which correspond to rare diseases' symptoms
     *      - The symptoms (indicated by the *FIELD* CS beacons) from "OMIM", which correspond to the genetic diseases' symptoms
     *      - The drugs' side effects from "Sider"
     * @param symptom is the symptom of which we want to determine the causes
     * @param dbm is the object used to work on DrugBank
     * @param odm is the object used to work on OrphaData
     * @param omim is the object used to work on OMIM
     * @param sdm is the object used to work on Sider
     * @param onto is the object used to work on omin_onto
     * @param hpo is the object used to work on HPO
     */
    public static ArrayList<String> SymptomToCause(String symptom,
                          DrugBankMatching dbm,
                          OrphaDataMatching odm,
                          OMIMMatching omim,
                          SiderMatching sdm,
                          OMIMONTOMatching onto,
                          HPOMatching hpo) {

        ArrayList<String> results = new ArrayList<String>();

        //Searching for the synonyms if the request is about one symptom

        try {
            ArrayList<String> Symptoms = RequestSplit.RequestSplit(symptom);
            if (Symptoms.size()==1){
                ArrayList<String> hpo_results = hpo.search("Name:\"" + symptom + "\"","Synonym");
                hpo_results.add(symptom);
                int n = hpo_results.size();
                // Recovering all the datas
                int k=n-1;
                results.add("There is (are) : " + k + " synonyms of "+ symptom );
                results.add(" ");
                String query1;
                int m;
                ArrayList<String> dbm_results;
                ArrayList<String> omim_results;
                ArrayList<String> odm_results;
                ArrayList<String> sdm_results;

                results.add("DRUGBANK's matching ");
                results.add(" ");
                for (int j = 0 ; j< n ; j++) {
                    query1 = RequestSplit.RequestSplitLucene(hpo_results.get(j), "Toxicity");
                    dbm_results = dbm.search(query1, "Name");
                    m = dbm_results.size();
                    for (int i = 0; i < m; i++) {
                        if (!results.contains(dbm_results.get(i))) {
                            results.add(dbm_results.get(i));
                        }

                    }
                }

                results.add(" ");
                results.add("OMIM's matching : " );
                results.add(" ");
                for (int j = 0 ; j< n ; j++) {
                    query1 = RequestSplit.RequestSplitLucene(hpo_results.get(j), "Symptom");
                    omim_results = omim.search(query1, "Name");
                    m=omim_results.size();
                    for (int i = 0; i < m; i++) {
                        if (!results.contains(omim_results.get(i))) {
                            results.add(omim_results.get(i));
                        }
                    }
                }

                results.add(" ");
                results.add("OrphaData's matching : ");
                results.add(" ");
                for (int j = 0 ; j< n ; j++) {
                    query1 = RequestSplit.RequestSplitLucene(hpo_results.get(j), "Symptom");
                    odm_results = odm.search(query1, "Name");
                    m = odm_results.size();

                    for (int i = 0 ; i < m; i++){
                        if(!results.contains(odm_results.get(i))){
                            results.add(odm_results.get(i));
                        }
                    }
                }

                results.add(" ");
                results.add("Sider's matching : " );
                results.add(" ");
                for (int j = 0 ; j< n ; j++) {
                    sdm_results = sdm.searchFromSymptom(hpo_results.get(j));
                    m = sdm_results.size();

                    for (int i = 0 ; i < m; i++){
                        if(!results.contains(sdm_results.get(i))){
                            results.add(sdm_results.get(i));
                        }
                    }
                }

            }

            else {
                String query1 = RequestSplit.RequestSplitLucene(symptom, "Toxicity");
                ArrayList<String> dbm_results = dbm.search(query1, "Name");
                query1 = RequestSplit.RequestSplitLucene(symptom, "Symptom");
                ArrayList<String> omim_results = omim.search(query1, "Name");
                query1 = RequestSplit.RequestSplitLucene(symptom, "Symptom");
                ArrayList<String> odm_results = odm.search(query1, "Name");
                query1 = RequestSplit.RequestSplitLucene(symptom, "side_effect_name");
                ArrayList<String> sdm_results = sdm.searchFromSymptom(query1);


                // Ordering the datas
                int m = dbm_results.size();
                results.add("DRUGBANK's matching : " + m);
                results.add(" ");
                for (int i = 0; i < m; i++) {
                    if (!results.contains(dbm_results.get(i))) {
                        results.add(dbm_results.get(i));
                    }

                }
                m = omim_results.size();
                results.add(" ");
                results.add("OMIM's matching : " + m);
                results.add(" ");
                for (int i = 0; i < m; i++) {
                    if (!results.contains(omim_results.get(i))) {
                        results.add(omim_results.get(i));
                    }
                }
                m = sdm_results.size();
                results.add(" ");
                results.add("Sider's matching : " + m);
                results.add(" ");
                for (int i = 0; i < m; i++) {
                    if (!results.contains(sdm_results.get(i))) {
                        results.add(sdm_results.get(i));
                    }
                }
                m = odm_results.size();
                results.add(" ");
                results.add("OrphaData's matching : " + m);
                results.add(" ");
                for (int i = 0; i < m; i++) {
                    if (!results.contains(odm_results.get(i))) {
                        results.add(odm_results.get(i));
                    }
                }
            }
            return results;

        }catch (Exception e){
            System.out.println("Problem while trying to research the databases");
            e.printStackTrace();
            results.add("Problem while trying to research the databases");
            return results;
        }

    }

    public static void main (String[] args){

    }

}
