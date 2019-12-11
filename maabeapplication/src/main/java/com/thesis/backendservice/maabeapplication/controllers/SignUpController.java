package com.thesis.backendservice.maabeapplication.controllers;

import com.thesis.backendservice.maabeapplication.components.AddTripleOWL;
import com.thesis.backendservice.maabeapplication.components.RetrieveTripleOWL;
import com.thesis.backendservice.maabeapplication.components.StoreEHR;
import com.thesis.backendservice.maabeapplication.services.SignUpService;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;


@RestController
@RequestMapping("/FrontEndService")
public class SignUpController {

    @Autowired
    SignUpService signUpService;

    @Autowired
    AddTripleOWL addTripleOWL;

    @Autowired
    StoreEHR storeEHR;

    @CrossOrigin
    @RequestMapping(value = "/signUpService", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String staffSignUp(@RequestBody MultiValueMap<String, String> formData) throws OWLOntologyStorageException, IOException {


        for(String i : formData.keySet()){
            System.out.println(i+" "+formData.get(i).get(0));
        }

       Map<String,String> responseMap =  signUpService.getMapToAdd(formData);

        if (responseMap== null){
            return "Registration Failed, Incorrect Credentials";
        }

       for(String mapKeys : responseMap.keySet()){
           if (mapKeys.equals("class") && responseMap.get(mapKeys).equals("Patient")){
               storeEHR.createEHRFields(responseMap.get("name"));
           }
       }

//       addTripleOWL.addTriple(responseMap);


        return "Registration Successful";

    }




}
