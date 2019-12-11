package com.thesis.backendservice.maabeapplication.components;

import com.thesis.backendservice.maabeapplication.OntologyProperty;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class PatientSelection {

    @Autowired
    OntologyProperty ontologyProperty;

    public Map<String, ArrayList<String>> getPatientList(String staffName){

        // 'Patient Name':'MedicalInstitution', 'Doctor'sName, provided by patient signUp'

        OWLDataFactory dataFactory = ontologyProperty.getDataFactory();
        PrefixManager prefixManager = ontologyProperty.getPrefixManager();
        OWLReasoner reasoner = ontologyProperty.getReasoner();

        Map<String, ArrayList<String>> patientListMap = new HashMap<String, ArrayList<String>>();

        // Staff

        OWLNamedIndividual staffIndividual = dataFactory.getOWLNamedIndividual(staffName, prefixManager);

        OWLObjectProperty objectPropertyMedicalInstitution = dataFactory.getOWLObjectProperty("fromMedicalInstitution",prefixManager);

        NodeSet<OWLNamedIndividual> staffObjectPropertyValueSet = reasoner.getObjectPropertyValues(staffIndividual,objectPropertyMedicalInstitution);

        String staffMedicalInstitutionName = getObjectPropertyValue(staffObjectPropertyValueSet);

        // Patient
        OWLClass PatientClass = dataFactory.getOWLClass("Patient", prefixManager);

        Set<OWLNamedIndividual> patientIndividuals = reasoner.getInstances(PatientClass, false).getFlattened();

        OWLObjectProperty objectPropertyGetsTreated = dataFactory.getOWLObjectProperty("getsTreatedBy",prefixManager);

        for(OWLNamedIndividual patient : patientIndividuals){

            ArrayList<String> patientDetailsList = new ArrayList<String>();
            String patientName = patient.getIRI().getShortForm();

            NodeSet<OWLNamedIndividual> patientsMedicalInstitutionValueSet = reasoner.getObjectPropertyValues(patient,objectPropertyMedicalInstitution);

            if(patientsMedicalInstitutionValueSet.getFlattened().size()!=0){

                String patientMedicalInstitutionName = getObjectPropertyValue(patientsMedicalInstitutionValueSet);
//                System.out.println(patientName);
//                System.out.println(patientMedicalInstitutionName);

                if(patientMedicalInstitutionName.equals(staffMedicalInstitutionName)){
                    NodeSet<OWLNamedIndividual> patientsGetsTreatedValueSet = reasoner.getObjectPropertyValues(patient,objectPropertyGetsTreated);

//                    System.out.println(patientsGetsTreatedValueSet);
//                    System.out.println(patient.toString());
                    String patientGetsTreatedName = getObjectPropertyValue(patientsGetsTreatedValueSet);
//                    System.out.println(patientGetsTreatedName);

                    patientDetailsList.add(patientMedicalInstitutionName);
                    patientDetailsList.add(patientGetsTreatedName);

                    patientListMap.put(patientName, patientDetailsList);
                }
            }
        }

        return patientListMap;


    }
    public String getObjectPropertyValue(NodeSet<OWLNamedIndividual> staffObjectPropertyValueSet){
        return staffObjectPropertyValueSet.getFlattened().iterator().next().getIRI().getShortForm();
    }
}
// Patient selection is done by checking all patients fromMedicalInstitution value with staff signing up fromMedicalInstitution
// Basically a staff member from Baltimore Hospital can select from all patients of Baltimore Hospital, however how much access he gets will depend upon policies