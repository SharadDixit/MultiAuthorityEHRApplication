package com.thesis.backendservice.maabeapplication.components;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class FetchEHR {

    public Map<String, ArrayList<String>> getEHRDataMap(String patientName, Map<String, String> accessDecisions, String doctorName) throws IOException {

        // EHRData >> ['EHRType':EHRArray] >> ['Medication':[Modify],[EHRActualData]]
        Map<String, ArrayList<String>> EHRDataMap = new HashMap<>();

        EHRDataMap.put("PatientName", Lists.newArrayList(patientName));
        EHRDataMap.put("DoctorName",Lists.newArrayList(doctorName));
//
//
//        EHRDataMap.put("Medication",Lists.newArrayList("Modify","The patient is advised to follow as written below,\n" +
//                "Over-the-counter Calcium tablets is fine"));
//        EHRDataMap.put("Allergies",Lists.newArrayList("Modify","The patient is allergic to : None"));
//        EHRDataMap.put("BillingInfo",Lists.newArrayList("Modify","The patient has been billed as follows,\n" +
//                "Doctor's charges\n" +
//                "Doctor visit charges : $400\n" +
//                "\n" +
//                "Medical Insurance Coverage\t:$0\n" +
//                "\n" +
//                "Total expenses to be paid\t:$400"));
//        EHRDataMap.put("Diagnoses",Lists.newArrayList("Modify","The patient has been diagnosed with interscapular pain or shoulder blade pain. The patient suffers from aching, dull, sore, and shooting pain in the upper part of back between shoulder blades."));
//        EHRDataMap.put("DoctorNotes",Lists.newArrayList("Modify","This shoulder stretch sometimes helps:\n" +
//                "Cross one arm over your body.\n" +
//                "Use your other arm to pull the elbow of your outstretched arm toward your chest.\n" +
//                "Hold this stretch for about 10 seconds.\n" +
//                "Discontinue if pain continues or aggravates."));
//        EHRDataMap.put("ImmunizationDates",Lists.newArrayList("Modify","The patient's immunization schedule is, HepB : June 29, 2014"));




        for (String accessDecision: accessDecisions.keySet() ){

            ArrayList<String> ehrArray = new ArrayList<>();
            String permission = getPermissionFromAccessDecision(accessDecision);

            // ehrArray >> ['Permission','']


            ehrArray.add(permission);

            String ehrType = getEHRTypeFromAccessDecision(accessDecision, permission);

            String ehrDataFilePath = getEHRData(ehrType,patientName);

            String ehrData = readEHRFile(ehrDataFilePath);

            ehrArray.add(ehrData);

            if(!ehrData.equals("")){
                EHRDataMap.put(ehrType,ehrArray);
            }
        }

        return EHRDataMap;
    }
    public String getEHRData(String EHRType, String PatientName){

        File EHRDataDirectory = new File("./EHRData");
        for(File EHRFilesPerson: EHRDataDirectory.listFiles()){
            if(EHRFilesPerson.toString().contains(PatientName)){
                for(File EHRFiles : EHRFilesPerson.listFiles()){
                    if(EHRFiles.toString().contains(EHRType)){
                        for(File EHRFile : EHRFiles.listFiles()){
                            return (EHRFile.toString());
                        }
                    }
                }
            }
        }

        return "File Not Found, check getEHRFiles";
    }
    public String getPermissionFromAccessDecision(String accessDecision){

        if(accessDecision.contains("Modify")){
            return "Modify";
        }else{
            return "Read";
        }
    }
    public String getEHRTypeFromAccessDecision(String accessDecision, String permission){

        return accessDecision.substring(accessDecision.indexOf(permission)+permission.length());
    }
    public String readEHRFile(String filePath) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();
        File f = new File(filePath);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
        String text;
        while ((text = bufferedReader.readLine())!=null){
            stringBuilder.append(text);
        }

        return stringBuilder.toString();

    }
}
