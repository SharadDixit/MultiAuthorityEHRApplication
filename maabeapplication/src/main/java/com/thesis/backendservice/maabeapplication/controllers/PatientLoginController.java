package com.thesis.backendservice.maabeapplication.controllers;

import com.google.common.collect.Lists;
import com.thesis.backendservice.maabeapplication.components.ABACVerification;
import com.thesis.backendservice.maabeapplication.components.CredentialVerification;
import com.thesis.backendservice.maabeapplication.components.FetchEHR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/FrontEndService")
public class PatientLoginController {

    @Autowired
    CredentialVerification credentialVerification;

    @Autowired
    FetchEHR fetchEHR;

    @Autowired
    ABACVerification abacVerification;

    @CrossOrigin
    @RequestMapping(value = "/patientLogin", method = RequestMethod.POST)
    public Map<String, String> patientLogin(@RequestParam String patientName, @RequestParam String password) throws IOException {

        System.out.println(patientName);
        System.out.println(password);


        boolean initialVerification = credentialVerification.verification(patientName, password);

        System.out.println(initialVerification);

        if (!initialVerification){
            return null;
        }

        Map<String, String> accessDecisions = abacVerification.runSWRL(patientName);

//        Map<String, String> accessDecisions = new HashMap<>();

//        accessDecisions.put("canReadDiagnoses","true");
//        accessDecisions.put("canReadMedication","true");
//        accessDecisions.put("canReadAllergies","true");
//        accessDecisions.put("canReadDoctorNotes","true");
//        accessDecisions.put("canReadBillingInfo","true");
//        accessDecisions.put("canReadImmunizationDates","true");
//        accessDecisions.put("canReadPrescription","true");
//

        for (String i : accessDecisions.keySet()){
            System.out.println(i);
            System.out.println(accessDecisions.get(i));
        }

        Map<String, String> EHRMap = new HashMap<>();
        EHRMap.put("PatientName", patientName);

        for (String accessDecision : accessDecisions.keySet()){
            String permission = fetchEHR.getPermissionFromAccessDecision(accessDecision);
            String ehrType = fetchEHR.getEHRTypeFromAccessDecision(accessDecision, permission);

            String ehrDataFilePath = fetchEHR.getEHRData(ehrType, patientName);

            String ehrData = fetchEHR.readEHRFile(ehrDataFilePath);

            if(!ehrData.equals("")){
                EHRMap.put(ehrType,ehrData);
            }
        }


        return EHRMap;
    }


}
