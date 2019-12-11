package com.thesis.backendservice.maabeapplication.controllers;

import com.thesis.backendservice.maabeapplication.components.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/FrontEndService")
public class StaffLoginController {

    @Autowired
    CredentialVerification credentialVerification;

    @Autowired
    PatientSelection patientSelection;

    @Autowired
    ABACVerification abacVerification;

    @Autowired
    FetchEHR fetchEHR;

    @Autowired
    RetrieveTripleOWL retrieveTripleOWL;

    @Autowired
    StoreEHR storeEHR;

    @CrossOrigin
    @RequestMapping(value = "/staffLogin", method = RequestMethod.POST)
    public Map<String, ArrayList<String>> staffLogin(@RequestParam String username, @RequestParam String password){

        System.out.println(username);
        System.out.println(password);

        // PatientName : <MedicalInstitution, Doctor'sName>
        Map<String, ArrayList<String>> patientList;

        boolean initialAccessDecision = credentialVerification.verification(username, password);

        if (!initialAccessDecision){
            return null;
        }

        // 'Patient Name':'MedicalInstitution', 'Doctor'sName, provided by patient signUp'
        patientList = patientSelection.getPatientList(username);
//        for (String i: patientList.keySet()) {
//            System.out.println(i);
//            System.out.println(patientList.get(i));
//
//        }

        return patientList;
    }

    @CrossOrigin
    @RequestMapping(value="/ehrSelection", method = RequestMethod.POST)
    public Map<String, ArrayList<String>> ehrSelection(@RequestBody MultiValueMap<String, String> formData) throws IOException {

        String userName = formData.get("doctorName").get(0);

        System.out.println(userName);


        Map<String, String> accessDecisions = abacVerification.runSWRL(userName);

        for (String i : accessDecisions.keySet()){
            System.out.println(i);
            System.out.println(accessDecisions.get(i));
        }

        String userClassName = retrieveTripleOWL.getOWLClass(userName);

        ArrayList<String> retrievedAttributes = retrieveTripleOWL.getPropertyValues(userName);

        retrievedAttributes.add(userClassName);

        System.out.println(retrievedAttributes.toString());

        Map<String, ArrayList<String>> EHRData = fetchEHR.getEHRDataMap(formData.get("patientName").get(0),accessDecisions, userName);

        EHRData.put("Attributes",retrievedAttributes);

        return EHRData;
    }

    @CrossOrigin
    @RequestMapping(value = "/saveEHRChanges", method = RequestMethod.POST)
    public void ehrSaveChanges(@RequestParam Map<String, String> ehrChangesMap){

        String patientName = "";
        String modifiedEHRField = "";
        String modifiedEHRFieldValue = "";

        for (String key : ehrChangesMap.keySet()){
            System.out.println("Key"+" "+key);
            System.out.println("Value"+" "+ehrChangesMap.get(key));
            if(key.equals("PatientName")){
                patientName = ehrChangesMap.get(key).substring(ehrChangesMap.get(key).indexOf(":")+1).trim();
            }else {
                modifiedEHRField = key;
                modifiedEHRFieldValue = ehrChangesMap.get(key);
            }
        }

        storeEHR.saveModifiedEHRField(patientName,modifiedEHRField, modifiedEHRFieldValue);
//        System.out.println(patientName);
//        System.out.println(modifiedEHRField);
//        System.out.println(modifiedEHRFieldValue);
    }
}
