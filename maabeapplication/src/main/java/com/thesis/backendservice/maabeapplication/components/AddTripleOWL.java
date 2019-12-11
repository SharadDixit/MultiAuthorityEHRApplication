package com.thesis.backendservice.maabeapplication.components;

import com.thesis.backendservice.maabeapplication.OntologyProperty;
import org.semanticweb.owlapi.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AddTripleOWL {

    @Autowired
    OntologyProperty ontologyProperty;

    public void addTriple(Map<String, String> responseMap) throws OWLOntologyStorageException {

        OWLOntologyManager ontologyManager = ontologyProperty.getOntologyManager();
        OWLOntology ontology = ontologyProperty.getOntology();
        OWLDataFactory dataFactory = ontologyProperty.getDataFactory();
        PrefixManager prefixManager = ontologyProperty.getPrefixManager();

        OWLClass classOfIndividual = null;
        OWLNamedIndividual nameOfIndividual = null;

//        ArrayList<OWLEntity> owlEntities = new ArrayList<OWLEntity>();

        for (String responseMapKeys : responseMap.keySet()){

            if(responseMapKeys.equals("class")){
                classOfIndividual = dataFactory.getOWLClass( responseMap.get(responseMapKeys), prefixManager);
//                System.out.println(responseMap.get(responseMapKeys));
//                owlEntities.add(classOfIndividual);

            }
            else if(responseMapKeys.equals("name")){
                nameOfIndividual = dataFactory.getOWLNamedIndividual(responseMap.get(responseMapKeys),prefixManager);
//                System.out.println(responseMap.get(responseMapKeys));
//                owlEntities.add(nameOfIndividual);
            }


        }

//      Add Class and name of individual first to the ontology: That is Shashwat type Doctor
        ontologyManager.addAxiom(ontology, dataFactory.getOWLClassAssertionAxiom(classOfIndividual, nameOfIndividual));

//      Now we will add all the properties
        for (String responseMapKeys : responseMap.keySet()) {

            if(!responseMapKeys.equals("class") && !responseMapKeys.equals("name")){

                if(responseMapKeys.contains("has")){

                    OWLDataProperty dataProperty = dataFactory.getOWLDataProperty(responseMapKeys, prefixManager);

                    ontologyManager.addAxiom(ontology,
                            dataFactory.getOWLDataPropertyAssertionAxiom(dataProperty, nameOfIndividual, responseMap.get(responseMapKeys)));

                }else{

                    String responseMapKeysUpdated;

                    if(responseMapKeys.contains("_")){
                        responseMapKeysUpdated = responseMapKeys.substring(0,responseMapKeys.indexOf("_"));
                    }else {
                        responseMapKeysUpdated = responseMapKeys;
                    }

                    System.out.println(responseMapKeysUpdated);
                    OWLObjectProperty objectProperty = dataFactory.getOWLObjectProperty(responseMapKeysUpdated, prefixManager);

                    OWLNamedIndividual objectPropertyObject = dataFactory.getOWLNamedIndividual(responseMap.get(responseMapKeys), prefixManager);

                    ontologyManager.addAxiom(ontology, dataFactory.getOWLObjectPropertyAssertionAxiom(objectProperty, nameOfIndividual, objectPropertyObject));
                }


            }

        }

        ontologyManager.saveOntology(ontology);

    }
}
