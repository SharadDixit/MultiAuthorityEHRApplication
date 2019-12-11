package com.thesis.backendservice.maabeapplication.components;

import com.thesis.backendservice.maabeapplication.OntologyProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.swrlapi.core.SWRLRuleEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class ABACVerification {

    @Autowired
    OntologyProperty ontologyProperty;

    public Map<String, String> runSWRL(String doctorName){

        Map<String, String> accessDecision = new HashMap<>();

        SWRLRuleEngine swrlRuleEngine = ontologyProperty.getSwrlRuleEngine();

        swrlRuleEngine.run();

        Set<OWLAxiom> owlAxiomSet = swrlRuleEngine.getInferredOWLAxioms();

        int count = 0;
        for (OWLAxiom inferredAxioms : owlAxiomSet){
            if(inferredAxioms.getIndividualsInSignature().toString().contains(doctorName)){
                count++;
//                System.out.println(inferredAxioms);
                if(inferredAxioms.getDataPropertiesInSignature().toString().contains("#")){
//                    System.out.println("Here" + inferredAxioms);
                    int fieldStartIndex = inferredAxioms.getDataPropertiesInSignature().toString().indexOf("#")+1;
                    int fieldEndIndex = inferredAxioms.getDataPropertiesInSignature().toString().length()-2;

                    String field = inferredAxioms.getDataPropertiesInSignature().toString()
                            .substring(fieldStartIndex,fieldEndIndex);

                    boolean permission ;

//                  if inferredAxioms will contain true then permission will be true else false; contains method returns boolean
                    permission = inferredAxioms.toString().contains("true");

//                    System.out.println(field +" "+ permission);

                    accessDecision.put(field,Boolean.toString(permission));

                }
            }
        }
        if(count == 0){
            System.out.println("CHECK DOCTOR NAME, RUNNING PROBLEM IN SWRL");
        }

        return accessDecision;
    }
}
