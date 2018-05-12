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
     *      - The symptoms (indicated by the *FIELD* CS beacons) from "OMIM", which correspond to the genetec diseases' symptoms
     *      - The drugs' side effects from "Sider"
     * @param symptom is the symptom of which we want to determine the causes
     * @param order is the order requested by the user for the datas
     * @param dbm is the object used to work on DrugBank
     * @param odm is the object used to work on OrphaData
     * @param omim is the object used to work on OMIM
     * @param sdm is the object used to work on Sider
     */
    public SymptomToCause(String symptom, int order, DrugBankMatching dbm, OrphaDataMatching odm, OMIMMatching omim, SiderMatching sdm){



        // Recovering all the datas
        try {
            ArrayList<String> dbm_results = dbm.search("Toxicity:\""+symptom+"\"");
            ArrayList<String> omim_results = omim.search("Symptom:\""+symptom+"\"");
            //ArrayList<String> odm_results = odm.search();
            //ArrayList<String> sdm_results = sdm.search();
        }catch (Exception e){
            System.out.println("Problem while trying to research the databases");
            e.printStackTrace();
        }

        // Merging the datas while exluding the synonyms
        ArrayList<String> drug_results = new ArrayList<>();
        ArrayList<String> diseases_results = new ArrayList<>();

        // Ordering the datas

    }

}
