package com.thesis.backendservice.maabeapplication.components;

import com.google.common.collect.Lists;
import com.thesis.backendservice.maabeapplication.OntologyProperty;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Set;

@Component
public class RetrieveTripleOWL {

    @Autowired
    OntologyProperty ontologyProperty;

    public ArrayList<String> getPropertyValues(String userName){

        ArrayList<String> propertyNames = Lists.newArrayList("isCertifiedBy","isSpecializedIn","fromMedicalInstitution");

        ArrayList<String> propertyValues = new ArrayList<>();

        for(String propertyName : propertyNames){
            propertyValues.add(getOWLObjectPropertyValue(userName,propertyName));
        }

        return propertyValues;


    }
    public String getOWLObjectPropertyValue(String userName, String propertyName){

        OWLDataFactory dataFactory = ontologyProperty.getDataFactory();
        PrefixManager prefixManager = ontologyProperty.getPrefixManager();
        OWLReasoner reasoner = ontologyProperty.getReasoner();

        OWLNamedIndividual staffIndividual = dataFactory.getOWLNamedIndividual(userName, prefixManager);

        OWLObjectProperty objectPropertyMedicalInstitution = dataFactory.getOWLObjectProperty(propertyName,prefixManager);

        NodeSet<OWLNamedIndividual> objectPropertyValueSet = reasoner.getObjectPropertyValues(staffIndividual,objectPropertyMedicalInstitution);

//        System.out.println(objectPropertyValueSet);

        StringBuilder owlObjectPropertyValue = new StringBuilder();
        if(objectPropertyValueSet.iterator().hasNext()) {

            Set<OWLNamedIndividual> owlNamedIndividualObjectPropertySet = objectPropertyValueSet.getFlattened();

            for(OWLNamedIndividual owlNamedIndividualObjectProperty: owlNamedIndividualObjectPropertySet){
                owlObjectPropertyValue.append(owlNamedIndividualObjectProperty.getIRI().getShortForm());
                owlObjectPropertyValue.append(" ");
            }
//            System.out.println(owlObjectPropertyValue.toString());

            return owlObjectPropertyValue.toString();
        }else{
            return null;
        }

    }
    public String getOWLClass(String userName){

        OWLDataFactory dataFactory = ontologyProperty.getDataFactory();
        PrefixManager prefixManager = ontologyProperty.getPrefixManager();
        OWLOntology ontology = ontologyProperty.getOntology();

        OWLNamedIndividual owlNamedIndividual = dataFactory.getOWLNamedIndividual(userName, prefixManager);
        String className = EntitySearcher.getTypes(owlNamedIndividual,ontology).iterator().next()
                    .asOWLClass().getIRI().getShortForm();
        return className;
    }
    public boolean checkIndividualExist(String individualName){

        OWLDataFactory dataFactory = ontologyProperty.getDataFactory();
        PrefixManager prefixManager = ontologyProperty.getPrefixManager();

        OWLNamedIndividual namedIndividual = dataFactory.getOWLNamedIndividual(individualName, prefixManager);

        OWLOntology ontology = ontologyProperty.getOntology();
        if(!ontology.containsIndividualInSignature(namedIndividual.getIRI())){
            System.out.println("Individual Doesn't Exist in Ontology");
            return false;
        }
        return true;
    }

}
