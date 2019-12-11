package com.thesis.backendservice.maabeapplication.components;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class StoreEHR {

    public void saveModifiedEHRField(String patientName, String modifiedEHRField, String modifiedEHRFieldValue){

        String directoryPath = "./EHRData/"+patientName+"/"+modifiedEHRField;
        String ehrFieldPath = directoryPath+"/"+modifiedEHRField+".txt";
        System.out.println(ehrFieldPath);
        File ehrFieldFile = new File(ehrFieldPath);
        FileWriter fileWriter;

        try {
            fileWriter = new FileWriter(ehrFieldFile,false);
            fileWriter.write(modifiedEHRFieldValue);
            fileWriter.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public void createEHRFields(String patientName) throws IOException {

        String directoryPath = "./EHRData/"+patientName;
        File patientEHRDataDirectory = new File(directoryPath);

        checkDirectoryExist(patientEHRDataDirectory);

        ArrayList<String> ehrFields = Lists.newArrayList("Allergies","BillingInfo","Medication",
                "Diagnoses","DoctorNotes","ImmunizationDates","LabResults","VitalStats","Prescription");

        for(String ehrField : ehrFields){
            String ehrFieldDirectoryPath = directoryPath+"/"+ehrField;

            File ehrFieldDirectory = new File(ehrFieldDirectoryPath);
            checkDirectoryExist(ehrFieldDirectory);

            String ehrFieldFilePath = ehrFieldDirectoryPath+"/"+ehrField+".txt";
            File ehrFieldFile = new File(ehrFieldFilePath);
            ehrFieldFile.createNewFile();
        }

    }
    public void checkDirectoryExist(File directory){
        // If directory doesn't exist make new one
        if(!(directory.exists())){
            directory.mkdirs();
        }
    }
}
