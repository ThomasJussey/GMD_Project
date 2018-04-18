package core;

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
     * @param omm is the object used to work on OMIM
     * @param sdm is the object used to work on Sider
     */
    public SymptomToCause(String symptom, int order, DrugBankMatching dbm, OrphaDataMatching odm, OMIMMatching omm, SiderMatching sdm){
        // Recovering all the datas

        // Merging the datas while exluding the synonyms

        // Ordering the datas

    }

}
