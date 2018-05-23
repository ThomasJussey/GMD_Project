package core;

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
     *      - The symptoms (indicated by the *FIELD* CS beacons) from "OMIM", which correspond to the genetec diseases' symptoms
     *      - The drugs' side effects from "Sider"
     * @param symptom is the symptom of which we want to determine the causes
     * @param order is the order requested by the user for the datas
     * @param cause is the nature of the symptom's cause (disease's symptom or drug's side effect)
     * @param dbm is the object used to work on DrugBank
     * @param omm is the object used to work on OMIM
     * @param sdm is the object used to work on Sider
     */
    public ArrayList<String> SymptomToTreatment(String symptom, int order, int cause, DrugBankMatching dbm, OMIMMatching omm, SiderMatching sdm){

        ArrayList<String> results = new ArrayList<String>();

        // Recovering all the datas

        try {
            String query1 = RequestSplit.RequestSplitLucene(symptom, "Indication");
            ArrayList<String> dbm_results = dbm.search(query1,"Name");
        } catch (Exception e){
            System.out.println("Problem while trying to research the databases");
            e.printStackTrace();
            results.add("Problem while trying to research the databases");

        }


        // Merging the datas while exluding the synonyms

        // Ordering the datas

        return results;
    }
}
