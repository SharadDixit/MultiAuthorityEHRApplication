package com.thesis.backendservice.maabeapplication;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.swrlapi.core.SWRLRuleEngine;
import org.swrlapi.factory.SWRLAPIFactory;

import java.io.File;

@Component
public class OntologyProperty {

    public OWLOntologyManager getOntologyManager() {
        return ontologyManager;
    }

    public void setOntologyManager(OWLOntologyManager ontologyManager) {
        this.ontologyManager = ontologyManager;
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    public void setOntology(OWLOntology ontology) {
        this.ontology = ontology;
    }

    public OWLDataFactory getDataFactory() {
        return dataFactory;
    }

    public void setDataFactory(OWLDataFactory dataFactory) {
        this.dataFactory = dataFactory;
    }

    public IRI getOntologyIRI() {
        return ontologyIRI;
    }

    public void setOntologyIRI(IRI ontologyIRI) {
        this.ontologyIRI = ontologyIRI;
    }

    public PrefixManager getPrefixManager() {
        return prefixManager;
    }

    public void setPrefixManager(PrefixManager prefixManager) {
        this.prefixManager = prefixManager;
    }

    public OWLReasonerFactory getReasonerFactory() {
        return reasonerFactory;
    }

    public void setReasonerFactory(OWLReasonerFactory reasonerFactory) {
        this.reasonerFactory = reasonerFactory;
    }

    public OWLReasoner getReasoner() {
        return reasoner;
    }

    public void setReasoner(OWLReasoner reasoner) {
        this.reasoner = reasoner;
    }

    public SWRLRuleEngine getSwrlRuleEngine() {
        return swrlRuleEngine;
    }

    public void setSwrlRuleEngine(SWRLRuleEngine swrlRuleEngine) {
        this.swrlRuleEngine = swrlRuleEngine;
    }

    OWLOntologyManager ontologyManager;
    OWLOntology ontology;
    OWLDataFactory dataFactory;

    IRI ontologyIRI;


    PrefixManager prefixManager;

    OWLReasonerFactory reasonerFactory;
    OWLReasoner reasoner;




    // Create a SWRL rule engine using the SWRLAPI
    SWRLRuleEngine swrlRuleEngine;

    @EventListener(ApplicationReadyEvent.class)
    public void startUpConfiguration() throws OWLOntologyCreationException {

        ontologyManager = OWLManager.createOWLOntologyManager();
        ontology = ontologyManager.loadOntologyFromOntologyDocument(new File("./MultiAuthorityOWL.owl"));

        dataFactory = ontologyManager.getOWLDataFactory();

        ontologyIRI = ontology.getOntologyID().getOntologyIRI().get();

        prefixManager = new DefaultPrefixManager(ontologyIRI.toString().concat("#"));

        reasonerFactory = new StructuralReasonerFactory();

        reasoner = reasonerFactory.createReasoner(ontology);

        swrlRuleEngine = SWRLAPIFactory.createSWRLRuleEngine(ontology);

    }
}
