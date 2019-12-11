package com.thesis.backendservice.maabeapplication.components;

import com.thesis.backendservice.maabeapplication.OntologyProperty;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class CredentialVerification {

    @Autowired
    OntologyProperty ontologyProperty;

    public Boolean verification(String userName, String password){

        OWLOntology ontology = ontologyProperty.getOntology();
        OWLDataFactory dataFactory = ontologyProperty.getDataFactory();
        PrefixManager prefixManager = ontologyProperty.getPrefixManager();

        OWLReasoner reasoner = ontologyProperty.getReasoner();

        OWLNamedIndividual namedIndividual = dataFactory.getOWLNamedIndividual(userName, prefixManager);

        if(!ontology.containsIndividualInSignature(namedIndividual.getIRI())){
            System.out.println("Wrong Username Entered");
            return false;
        }

        OWLDataProperty dataProperty = dataFactory.getOWLDataProperty("hasPassword",prefixManager);

        Set<OWLLiteral> literal = reasoner.getDataPropertyValues(namedIndividual, dataProperty);

        if(password.equals(literal.iterator().next().getLiteral())){
//            System.out.println(literal.iterator().next().getLiteral());
            return true;
        }else {
            return false;
        }

    }
}
