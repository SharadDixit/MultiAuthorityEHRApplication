package com.thesis.backendservice.maabeapplication.components;

import java.io.*;
import java.util.ArrayList;

public class MA_ABEWrapper {

    public void callMA_ABEWrapper() throws IOException {

        String pythonMAABEModule = "python3"+" "+" ./maABEGeneral.py";
        String operation = "decrypt";
        String authorityName = "UMBC";
        String gID = "alice";
        ArrayList<String> attributesControlledByAuthority = new ArrayList<String>(){{
            add("STUDENT");
            add("MASTERS");
        }};
        String globalParaFilePath = globalParameterFileSearch();
        if(globalParaFilePath.contains("Not Found")){
            System.out.println("Global Parameter File Not Found");
        }

        String EHRType = "Medication";
        String msgToEncrypt = "This msg belongs to Medication of Alice.";

        operationToPerform(pythonMAABEModule, operation, authorityName, globalParaFilePath, gID,
                            attributesControlledByAuthority, EHRType, msgToEncrypt);


    }
    public static String globalParameterFileSearch(){

        File MAABEDirectory = new File("./MAABEData");

        for(File MAABEFiles : MAABEDirectory.listFiles()){
            if(MAABEFiles.toString().contains("GlobalParameters.pkl")){
                return MAABEFiles.toString();
            }
        }

        return "Global Parameter File Not Found";

    }
    public static void operationToPerform(String pythonMAABEModule, String operation,
                                          String authorityName, String globalParaFilePath,
                                          String gID, ArrayList<String> attributesMonitoredByAuthority,
                                          String EHRType, String msgToEncrypt) throws IOException {

        if(operation.equals("encrypt")){  // To encrypt the document
            // # arguments > encrypt, GlobalParameters.pkl, publicKeyDic, msgToEncrypt, policyString
            // # policy parenthesis to be written with \ to provide bash to read it  \(Check\)

            encryptUsingMAABE(pythonMAABEModule, operation, globalParaFilePath, EHRType, msgToEncrypt, gID);

        }else if(operation.equals("decrypt")){ // To decrypt the document
            // # arguments > decrypt, GlobalParameters.pkl, CipherAESKeyMAABEEncrypt.pkl, User_SecretKey.pkl, CipherTextAESEncrypt.pkl

            decryptUsingMAABE(pythonMAABEModule, operation, globalParaFilePath, EHRType, gID);

        }else if(operation.equals("authSetup")){ // To generate public key and secret key for authority
            // # arguments > authSetup, GlobalParameters.pkl, authorityName

            authSetupUsingMAABE(pythonMAABEModule, operation, authorityName, globalParaFilePath);

        }else if(operation.equals("multiAttributesKeygen")){ // For an authority to generate key for a user depending
                                                            // on his attributes controlled by the authority
            // # arguments > multiAttributesKeygen, GlobalParameters.pkl, secretKey.pkl, gid, attribute1, attribute2 (complete list)
            // # attribute should be annotated with @ , student@UT  (attibute@Authority)

            multiAttributesKeygenUsingMAABE(pythonMAABEModule,operation, authorityName, globalParaFilePath, gID, attributesMonitoredByAuthority);
        }
    }
    public static void decryptUsingMAABE(String pythonMAABEModule, String operation, String globalParaFilePath, String EHRType, String gID) throws IOException {

        String userSecretKey = getUserFiles(gID,"finalKey");
        String AESKeyMAABEEncrypt = getEHRFiles(EHRType, gID, "AESKeyMAABEEncrypt");
        String textAESEncrypted = getEHRFiles(EHRType, gID, "TextAESEncrypt");

        String decryptProcess = pythonMAABEModule+" "+operation+" "+globalParaFilePath+" "+AESKeyMAABEEncrypt+" "+userSecretKey+" "+textAESEncrypted;

        System.out.println(decryptProcess);

        runProcess(decryptProcess);

        String recoveredText = readRecoveredFile();

        System.out.println(recoveredText);

    }

    public static String readRecoveredFile() throws IOException {
        File mainDirectory = new File("./");
        StringBuilder stringBuilder = new StringBuilder();
        for(File f : mainDirectory.listFiles()){
            if(f.toString().contains("Recovered")){
                BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
                String text;
                while ((text = bufferedReader.readLine())!=null){
                    stringBuilder.append(text);
                }
            }
        }
        return stringBuilder.toString();
    }

    public static String getEHRFiles(String EHRType, String gID, String callType){
        System.out.println(EHRType);
        System.out.println(gID);
        System.out.println(callType);
        File EHRDataDirectory = new File("./EHRData");
        for(File EHRFilesPerson: EHRDataDirectory.listFiles()){
            if(EHRFilesPerson.toString().contains(gID)){
                for(File EHRFiles : EHRFilesPerson.listFiles()){
                    if(EHRFiles.toString().contains(EHRType)){
                        for(File EHRFile : EHRFiles.listFiles()){
                            if(callType.equals("AESKeyMAABEEncrypt") && EHRFile.toString().contains(callType)){
                                return EHRFile.toString();
                            }else if(callType.equals("TextAESEncrypt") && EHRFile.toString().contains(callType)){
                                return EHRFile.toString();
                            }
                        }
                    }
                }
            }
        }

        return "File Not Found, check getEHRFiles";
    }

    public static void encryptUsingMAABE(String pythonMAABEModule, String operation, String globalParaFilePath,
                                         String EHRType, String msgToEncrypt, String gID) throws IOException {

        // gID here whose patient file is about to be encrypted, this information will be received from frontend

        String publicKeyDic = getPublicKeyDic();
        if(publicKeyDic.contains("Not")){
            System.out.println(publicKeyDic);
        }

        // EHRType = Allergies tells to fetch policy for allergies
        String policyString = getPolicy(EHRType);

        if(policyString.contains("Not Found")){
            System.out.println(policyString);
        }

        // Done this way because the msg and policy String have spaces in them and terminal called through java doesn't handle it properly
        String[] processCommand = {"python3","./maABEGeneral.py",operation,globalParaFilePath,publicKeyDic,msgToEncrypt, policyString};

        Process process = Runtime.getRuntime().exec(processCommand);
        consoleOutput(process);

//        String encryptProcess = pythonMAABEModule+" "+operation+" "+globalParaFilePath+" "+publicKeyDic+" "+msgToEncrypt+" "+policyString;
//
//        System.out.println(encryptProcess);
//
//        runProcess(encryptProcess);

        // Call EHR Store Class
        storeFileNewLocation("TextAESEncrypt", gID, EHRType);
        storeFileNewLocation("AESKeyMAABEEncrypt", gID, EHRType);

    }
    public static String getPublicKeyDic(){
        File MAABEAuthorityDirectory = new File("./MAABEData/Authorities");

        for(File authorityFiles : MAABEAuthorityDirectory.listFiles()){
            if(authorityFiles.toString().contains("Dic")){
                return authorityFiles.toString();
            }
        }
        return "Public Key Dic Not Found";
    }
    public static String getPolicy(String EHRType) throws IOException {

        File MAABEDirectory = new File("./MAABEData/Policy");

        for(File MAABEFiles : MAABEDirectory.listFiles() ){
            if(MAABEFiles.toString().contains(EHRType)){
                BufferedReader bufferedReader = new BufferedReader(new FileReader(MAABEFiles));
                String policy;
                while ((policy = bufferedReader.readLine())!=null){
                    System.out.println(policy);
                        return policy;
                }
            }
        }

        return "Policy Not Found, check getPolicy function";
    }
    public static void multiAttributesKeygenUsingMAABE(String pythonMAABEModule,String operation, String authorityName,
                                                       String globalParaFilePath, String gID, ArrayList<String> attributesMonitoredByAuthority) throws IOException {

        String fileType = "secret";

        //Need authority secret key
        String authoritySecretKey = getAuthorityFiles(authorityName, fileType);

        if(authoritySecretKey.contains("Not Found")){
            System.out.println(authoritySecretKey);
        }

        //Need attributes controlled by authority which are to be allocated to the user
        StringBuilder attributesAllocatedToUser = new StringBuilder();

        for(String attribute : attributesMonitoredByAuthority){
            String attibutesInFormat = attribute+"@"+authorityName;
            attributesAllocatedToUser.append(attibutesInFormat);
            attributesAllocatedToUser.append(" ");
        }
        String multiAttributesKeygenProcess = pythonMAABEModule+" "+operation+" "+globalParaFilePath+" "+authoritySecretKey+
                                            " "+gID+" "+attributesAllocatedToUser.toString();
        System.out.println(multiAttributesKeygenProcess);

        runProcess(multiAttributesKeygenProcess); //Generates secret key of user from a particular authority which was generating it

        // Store in different location
        storeFileNewLocation("secretKeyUser", gID);

        // Now we need to merge all secret keys of the user for which new secret was generated by an authority
        mergeSecretKeysUser(pythonMAABEModule, gID);

    }
    public static void mergeSecretKeysUser(String pythonMAABEModule, String gID) throws IOException {
        String secretKeysUser = getUserFiles(gID, "mergeIndividual");
        String operation = "mergeSecretKeysUser";
        String mergeSecretKeysUserProcess = pythonMAABEModule+" "+operation+" "+gID+" "+secretKeysUser;

        System.out.println(mergeSecretKeysUserProcess);
        runProcess(mergeSecretKeysUserProcess);

        // Store in different location
        storeFileNewLocation("secretKeyUser", gID);

    }
//
    public static String getUserFiles(String gID, String callType) {
        File MAABEUserDirectory = new File("./MAABEData/Users");
        StringBuilder userSecretKeys = new StringBuilder();
        for (File userDirectory : MAABEUserDirectory.listFiles()) {
            if(callType.equals("mergeIndividual")){
                if (userDirectory.toString().contains(gID)) {
                    for(File userFiles : userDirectory.listFiles()){
                        userSecretKeys.append(userFiles.toString());
                        userSecretKeys.append(" ");
                    }
                }
            }else if(callType.equals("finalKey")){
                if(userDirectory.toString().contains(gID)){
                    for(File userFiles : userDirectory.listFiles()){
                        if(!userFiles.toString().contains("@")){
                            return userFiles.toString();
                        }
                    }
                }
            }

        }

        return userSecretKeys.toString();
    }

    public static void authSetupUsingMAABE(String pythonMAABEModule, String operation, String authorityName, String globalParaFilePath) throws IOException {
        String authSetupProcess = pythonMAABEModule+" "+operation+" "+ globalParaFilePath +" "+ authorityName;
        System.out.println(authSetupProcess);

        runProcess(authSetupProcess); // Generates secret key and public key of authority

        // Store in different location
        storeFileNewLocation("publicKeyAuthority",authorityName);
        storeFileNewLocation("secretKeyAuthority",authorityName);

        // Now we need to merge all public keys and generate a common public key
        mergePublicKeysAuthority(pythonMAABEModule);


    }
    public static void mergePublicKeysAuthority(String pythonMAABEModule) throws IOException {

        String authorityName = null; //Because we need to get all the public keys of all authorities and finally merge them
        String fileType = "public";

        String authoritiesPublicKeys = getAuthorityFiles(authorityName, fileType);

        if(authoritiesPublicKeys.contains("Not Found")){
            System.out.println(authoritiesPublicKeys);
        }

        String mergePublicKeysAuthorityProcess = pythonMAABEModule+" "+"mergePublicKeysAuthority"+" "+ authoritiesPublicKeys;

        System.out.println(mergePublicKeysAuthorityProcess);

        runProcess(mergePublicKeysAuthorityProcess);

        // Store in different location
        storeFileNewLocation("publicKeyDic","system");

    }
//  Get authority public and secret keys
    public static String getAuthorityFiles(String authorityName, String fileType){

        File MAABEAuthorityDirectory = new File("./MAABEData/Authorities");
        StringBuilder authoritiesPublicKeys = new StringBuilder();

        for(File authorityDirectory : MAABEAuthorityDirectory.listFiles()){
            // This check is kept because of .DS_Store file generated when folder created using Intellij
            if(authorityDirectory.isDirectory()){
                if(fileType.equals("secret")){
                    String authoritySecretKey;
                    if(authorityDirectory.toString().contains(authorityName)){
                        for(File authorityFiles : authorityDirectory.listFiles()){
                            if(authorityFiles.toString().contains("SK")) {
                                authoritySecretKey = authorityFiles.toString();
                                return authoritySecretKey;
                            }
                        }
                    }
                }else if(fileType.equals("public")){
                    for(File authorityFiles : authorityDirectory.listFiles()){
                        if(authorityFiles.toString().contains("PK")){
                            authoritiesPublicKeys.append(authorityFiles.toString());
                            authoritiesPublicKeys.append(" ");
                        }
                    }

                }
            }
        }
        if (fileType.equals("public")){
            return authoritiesPublicKeys.toString();
        }

        return "Keys Not found, Check getAuthorityFiles function";
    }
    public static void runProcess(String processToRun) throws IOException {
        Process process = Runtime.getRuntime().exec(processToRun);
        consoleOutput(process);
    }

    public static void consoleOutput(Process process) throws IOException {
        BufferedReader stdOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));

        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String text;

        // read the output from the command
        if(stdOutput.readLine()!=null){
            System.out.println("Output of the command:\n");
            while ((text = stdOutput.readLine()) != null) {
                System.out.println(text);
            }
        }

        // read any errors from the attempted command
        if(stdError.readLine()!=null){
            System.out.println("Error of the command (if any):\n");
            while ((text = stdError.readLine()) != null) {
                System.out.println(text);
            }
        }
    }

//  Method overloading if calling with EHRType then call this to store only EHR documents
    public static void storeFileNewLocation(String changeType, String caller, String EHRType){

        // Code to Change Directory from main to the allocated one
        File fileOldLocation = getNewFile(changeType,caller);

        String directoryToStore = caller+"/"+EHRType;
        String newFileLocation = getNewLocation(changeType, directoryToStore, fileOldLocation.toString());
        changeDirectory(fileOldLocation, newFileLocation);

    }
//  Method overloading if calling without EHRType then call this to store all MAABE related keys
    public static void storeFileNewLocation(String changeType, String caller){

        // Code to Change Directory from main to the allocated one
        File fileOldLocation = getNewFile(changeType,caller);
        String newFileLocation = getNewLocation(changeType, caller, fileOldLocation.toString());
        changeDirectory(fileOldLocation, newFileLocation);

    }

    // For getting files in main directory, which are stored in main directory and then has to be stored in respective location
    public static File getNewFile(String changeType, String caller){
        File mainDirectory = new File("./");
        for(File file : mainDirectory.listFiles()){
            if(changeType.equals("secretKeyUser")){
                if(file.toString().contains(caller)){
                    System.out.println(file.toString());
                    return file;
                }
            }
            if(changeType.equals("publicKeyAuthority")){
                if(file.toString().contains(caller) && file.toString().contains("PK")){
                    System.out.println(file.toString());
                    return file;
                }
            }
            if(changeType.equals("secretKeyAuthority")){
                if(file.toString().contains(caller) && file.toString().contains("SK")){
                    System.out.println(file.toString());
                    return file;
                }
            }if(changeType.equals("publicKeyDic")){
                if(file.toString().contains("Dic")){
                    System.out.println(file.toString());
                    return file;
                }
            }if(changeType.equals("TextAESEncrypt")){
                if(file.toString().contains("TextAESEncrypt")){
                    System.out.println(file.toString());
                    return file;
                }
            }if(changeType.equals("AESKeyMAABEEncrypt")){
                if(file.toString().contains("AESKeyMAABEEncrypt")){
                    System.out.println(file.toString());
                    return file;
                }
            }
        }

        return null;
    }
    // For getting new location, these are fixed locations and in future to be changed to cloud server
    // Fixed String paths and not searching in here
    public static String getNewLocation(String changeType, String caller, String fileOldLocation){

        String fileName = fileOldLocation.substring(fileOldLocation.indexOf("/"));

        System.out.println(fileName);

        if(changeType.equals("secretKeyUser")){
            String filePath = "./MAABEData/Users/"+caller;
            checkDirectoryExist(filePath);

            filePath = filePath + fileName;

            System.out.println(filePath);
            return filePath;
        }
        if(changeType.equals("secretKeyAuthority") || changeType.equals("publicKeyAuthority")){
            String filePath = "./MAABEData/Authorities/"+caller;
            checkDirectoryExist(filePath);

            filePath = filePath + fileName;
            System.out.println(filePath);
            return filePath;
        }
        if(changeType.equals("publicKeyDic")){
            String filePath = "./MAABEData/Authorities/"+fileName;
            return filePath;
        }
        // Below is when storing EHR document
        if(changeType.equals("TextAESEncrypt") || changeType.equals("AESKeyMAABEEncrypt")){
            String filePath = "./EHRData/"+caller+"/";
            checkDirectoryExist(filePath);

            // caller here >> alice/EHRType and we want >> alice_Medication_AESKey
            String nameOfFile = caller.replace("/","_")+"_"+changeType+".pkl";

            filePath = filePath+nameOfFile;
            System.out.println(filePath);
            return filePath;
        }

        return "File New Location not found, Check getNewFileLocation";
    }
    public static void checkDirectoryExist(String path){
        // If directory doesn't exist make new one
        File directory = new File(path);
        System.out.println(directory);
        if(!(directory.exists())){
            directory.mkdirs();
        }
    }
    public static void changeDirectory(File fileOldLocation, String fileNewLocation){

        System.out.println(fileOldLocation.renameTo(new File(fileNewLocation)));
    }
}
