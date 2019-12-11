package com.thesis.backendservice.maabeapplication.services;

import com.thesis.backendservice.maabeapplication.components.RetrieveTripleOWL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

@Service
public class SignUpService {

    @Autowired
    RetrieveTripleOWL retrieveTripleOWL;

    public Map<String, String> getMapToAdd(MultiValueMap<String, String> formData){

        Map<String, String> responseMap = new HashMap<>();

        // has is tagged for data property when adding triples in the ontology
        // class for class it belongs and name for entity name
        // other than that all creates object properties
        for(String formFields : formData.keySet()){

            if(formFields.equals("Name")){
                responseMap.put("name",formData.get(formFields).get(0));
            }
            if(formFields.equals("ProfessionalDesignation")){
                responseMap.put("class",formData.get(formFields).get(0));
            }
            if(formFields.equals("Password")){
                responseMap.put("hasPassword",formData.get(formFields).get(0));
            }
            if(formFields.equals("UID")){
                responseMap.put("hasUID",formData.get(formFields).get(0));
            }
            if(formFields.equals("Certification") && !formData.get(formFields).get(0).equals("")){
                if (!retrieveTripleOWL.checkIndividualExist(formData.get(formFields).get(0))){
                    return null;
                }
                updateResponseMap(responseMap, formData, formFields, "isCertifiedBy");
            }
            if(formFields.equals("Specialization") && !formData.get(formFields).get(0).equals("")){
                if (!retrieveTripleOWL.checkIndividualExist(formData.get(formFields).get(0))){
                    return null;
                }
                updateResponseMap(responseMap, formData, formFields, "isSpecializedIn");
            }
            if(formFields.equals("MedicalInstitution")){
                if (!retrieveTripleOWL.checkIndividualExist(formData.get(formFields).get(0))){
                    return null;
                }
                responseMap.put("fromMedicalInstitution", formData.get(formFields).get(0));
            }
            if(formFields.equals("TreatedBy")){
                if (!retrieveTripleOWL.checkIndividualExist(formData.get(formFields).get(0))){
                    return null;
                }
                responseMap.put("getTreatedBy",formData.get(formFields).get(0));
            }

        }

        if(responseMap.size() !=0){
            return responseMap;
        }else {
            return null;
        }
    }

    public void updateResponseMap(Map<String, String> responseMap, MultiValueMap<String, String> formData, String formFields, String propertyName){

        int count = formData.get(formFields).size();

        if(count>1){
            for(int i=0; i<count; i++){
                responseMap.put(propertyName+"_"+i,formData.get(formFields).get(i));

            }
        }else {
            responseMap.put(propertyName, formData.get(formFields).get(0));
        }


    }

}
