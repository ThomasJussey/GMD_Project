package core;

import com.sun.org.apache.regexp.internal.RE;

import java.util.ArrayList;

/**
 * This class correspond to the project's second problematic :
 * "If the sign or symptom can be caused by a disease, find all the drugs that could treat it, and if it is a side effect,
 * find all the drugs treat the side effect"
 */
public class SymptomToTreatment {

    private String causes;

    /**
     * This method will be using all the recquired objects to build the recquired objects. We will need :
     *      - The cause's nature ; if the symptom is caused by a disease, we will search for drugs that could cure it, whereas if the symptom is a side effect,
     *      - The "Indications" from "Drugbank", which correspond to the drugs' indications
     *      - The symptoms (indicated by the *FIELD* CS beacons) from "OMIM", which correspond to the genetic diseases' symptoms -> CUI
     *      - The drugs' indications from "Sider" -> CUI
     *
     * @param symptom is the symptom of which we want to determine the causes
     * @param dbm is the object used to work on DrugBank
     * @param omm is the object used to work on OMIM
     * @param sdm is the object used to work on Sider
     * @parem stm is the object used to work on Stitch
     */
    public static ArrayList<String> SymptomToTreatment(String symptom, DrugBankMatching dbm, OMIMMatching omm, SiderMatching sdm, StitchMatching stm, HPOMatching hpo){

        ArrayList<String> results = new ArrayList<String>();
        /*
        try {
            String query1 = RequestSplit.RequestSplitLucene(symptom, "Indication");
            ArrayList<String> dbm_results = dbm.search(query1,"Name");
        } catch (Exception e){
            System.out.println("Problem while trying to research the databases");
            e.printStackTrace();
            results.add("Problem while trying to research the databases");

        }
        */

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
                    query1 = RequestSplit.RequestSplitLucene(hpo_results.get(j), "Indication");
                    dbm_results = dbm.search(query1, "Name");
                    m = dbm_results.size();
                    for (int i = 0; i < m; i++) {
                        if (!results.contains(dbm_results.get(i))) {
                            results.add(dbm_results.get(i));
                        }

                    }
                }

                results.add(" ");
                results.add("Sider's matching : " );
                results.add(" ");
                for (int j = 0 ; j< n ; j++) {
                    sdm_results = sdm.searchFromSymptom2(RequestSplit.RequestSplitSQL(hpo_results.get(j),"CONCEPT_NAME"));
                    //Passage à Stitch
                    //Passage à ATC
                    m = sdm_results.size();
                    for (int i = 0; i < m; i++) {
                        query1=RequestSplit.RequestSplitLucene(sdm_results.get(i),"Name");
                        ArrayList<String> stitch_results = stm.search(query1,"ATC");
                        ArrayList<String> atc_results = ATCMatching.ATCMatching(".sensibleData/br08303.keg", stitch_results);
                        int s = atc_results.size();
                        for (int h = 0 ; h< s ; h++) {
                            if (!results.contains(atc_results.get(h))) {
                                results.add(atc_results.get(h));
                            }
                        }
                    }
                }
                /*
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
                */

            }


            else {
                //Getting the diseases
                //String query1 = RequestSplit.RequestSplitLucene(symptom, "Indication");
                //ArrayList<String> dbm_results = dbm.search("Indication:\""+query1+"\"", "Name");
                String query1 = RequestSplit.RequestSplitLucene(symptom, "Indication");
                ArrayList<String> dbm_results = dbm.search(query1, "Name");
                /*
                String query1 = RequestSplit.RequestSplitLucene(symptom, "Toxicity");
                ArrayList<String> dbm_results = dbm.search(query1, "Name");
                query1 = RequestSplit.RequestSplitLucene(symptom, "Symptom");
                ArrayList<String> omim_results = omim.search(query1, "Name");
                query1 = RequestSplit.RequestSplitLucene(symptom, "Symptom");
                ArrayList<String> odm_results = odm.search(query1, "Name");
                */
                query1 = RequestSplit.RequestSplitSQL(symptom, "CONCEPT_NAME");
                ArrayList<String> sdm_results = sdm.searchFromSymptom2(query1);



                // Ordering the datas
                int m = dbm_results.size();
                results.add("DRUGBANK's matching : " + m);
                results.add(" ");
                for (int i = 0; i < m; i++) {
                    if (!results.contains(dbm_results.get(i))) {
                        results.add(dbm_results.get(i));
                    }

                }
                m = sdm_results.size();
                results.add(" ");
                results.add("Sider's matching : " + m);
                results.add(" ");
                for (int i = 0; i < m; i++) {
                    query1=RequestSplit.RequestSplitLucene(sdm_results.get(i),"Name");
                    ArrayList<String> stitch_results = stm.search(query1,"ATC");
                    ArrayList<String> atc_results = ATCMatching.ATCMatching(".sensibleData/br08303.keg", stitch_results);
                    int s = atc_results.size();
                    for (int h = 0 ; h< s ; h++) {
                        if (!results.contains(atc_results.get(h))) {
                            results.add(atc_results.get(h));
                        }
                    }
                }
                /*
                m = omim_results.size();
                results.add(" ");
                results.add("OMIM's matching : " + m);
                results.add(" ");
                for (int i = 0; i < m; i++) {
                    if (!results.contains(omim_results.get(i))) {
                        results.add(omim_results.get(i));
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
                */
            }
            return results;

        }catch (Exception e){
            System.out.println("Problem while trying to research the databases");
            e.printStackTrace();
            results.add("Problem while trying to research the databases");
            return results;
        }


        //return results;
    }
}
