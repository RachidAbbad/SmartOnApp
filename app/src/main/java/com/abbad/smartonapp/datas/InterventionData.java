package com.abbad.smartonapp.datas;

import android.app.Activity;
import android.util.Log;

import com.abbad.smartonapp.classes.Intervention;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InterventionData {
    public static List<Intervention> listInterventions;

    public InterventionData(){
        fillInterv();
    }

    public static List<Intervention> getListInterventions(){
        fillInterv();
        return listInterventions;
    }

    private static void fillInterv(){
        listInterventions = new ArrayList<>();
        listInterventions.add(new Intervention("la dépose de la buse de fumée du raccordement au conduit"
                ,"02-06-2021"
                ,3
                ,new String[]{"le nettoyage intérieur avec repose après travaux","le nettoyage des éléments et du local après intervention"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ,"Interv735651"
        ));
        listInterventions.add(new Intervention("Effectuer une calibration des contrôles"
                ,"15-06-2021"
                ,4
                ,new String[]{"Vérifiez les pompes","la lubrification","le fonctionnement du moteur","l’alignement"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ,"Interv735652"
        ));
        listInterventions.add(new Intervention("Vérification le système de contrôle"
                ,"02-06-2021"
                ,1
                ,new String[]{"les sondes de température intérieure et extérieure","les dispositifs de sécurité","la valve de relâche haute pression et haute température"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ,"Interv735653"
        ));
        listInterventions.add(new Intervention("la dépose de la buse de fumée du raccordement au conduit"
                ,"11-07-2021"
                ,3
                ,new String[]{"le nettoyage intérieur avec repose après travaux","le nettoyage des éléments et du local après intervention"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ,"Interv735663"
        ));
        listInterventions.add(new Intervention("Vérification du cheminée (évacuation des fumées de combustion)"
                ,"29-05-2021"
                ,3
                ,new String[]{"les échangeurs de chaleur","l’alimentation de gaz vers la chaudière","les fuites de gaz"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ,"Interv735654"
        ));
        listInterventions.add(new Intervention("la dépose de la buse de fumée du raccordement au conduit"
                ,"19-06-2021"
                ,3
                ,new String[]{"le nettoyage intérieur avec repose après travaux","le nettoyage des éléments et du local après intervention"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ,"Interv735655"
        ));
        listInterventions.add(new Intervention("Effectuer une calibration des contrôles"
                ,"26-05-2021"
                ,4
                ,new String[]{"Vérifiez les pompes","la lubrification","le fonctionnement du moteur","l’alignement"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ,"Interv735656"
        ));
        listInterventions.add(new Intervention("Vérification le système de contrôle"
                ,"18-06-2021"
                ,1
                ,new String[]{"les sondes de température intérieure et extérieure","les dispositifs de sécurité","la valve de relâche haute pression et haute température"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ,"Interv735657"
        ));
        listInterventions.add(new Intervention("Vérification du cheminée (évacuation des fumées de combustion)"
                ,"14-06-2021"
                ,5
                ,new String[]{"les échangeurs de chaleur","l’alimentation de gaz vers la chaudière","les fuites de gaz"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ,"Interv735658"
        ));
        listInterventions.add(new Intervention("la dépose de la buse de fumée du raccordement au conduit"
                ,"02-06-2021"
                ,4
                ,new String[]{"le nettoyage intérieur avec repose après travaux","le nettoyage des éléments et du local après intervention"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ,"Interv735659"
        ));
        listInterventions.add(new Intervention("Effectuer une calibration des contrôles"
                ,"09-06-2021"
                ,5
                ,new String[]{"Vérifiez les pompes","la lubrification","le fonctionnement du moteur","l’alignement"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ,"Interv735660"
        ));
        listInterventions.add(new Intervention("Vérification le système de contrôle"
                ,"14-06-2021"
                ,1
                ,new String[]{"les sondes de température intérieure et extérieure","les dispositifs de sécurité","la valve de relâche haute pression et haute température"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ,"Interv735661"
        ));
        listInterventions.add(new Intervention("Vérification du cheminée (évacuation des fumées de combustion)"
                ,"30-05-2021"
                ,3
                ,new String[]{"les échangeurs de chaleur","l’alimentation de gaz vers la chaudière","les fuites de gaz"}
                ,new String[]{"Tournevis","lunettes de protection"}
                ,"Interv735662"
        ));
    }

    public static Intervention getInterventionById(String id){
        for (Intervention inter: listInterventions) {
            if (inter.getId().equals(id))
                return inter;
        }
        return null;
    }

    public static List<File> getImages(String idIntervention, Activity activity) {
        try {

            File directory = new File(activity.getExternalCacheDir()+"/FinalImages");
            List<File> files = Arrays.asList(directory.listFiles());
            List<File> results = new ArrayList<>();
            for (int i = 0; i < files.size(); i++)
            {
                String name = files.get(i).getName();
                if(name.contains(idIntervention))
                    results.add(files.get(i));
            }
            return results;
        }catch (Exception ex)
        {
            return new ArrayList<>();
        }
    }

    public static List<File> getVideos(String idIntervention,Activity activity) {
        File directory = null;
        try {
            directory = new File(activity.getExternalCacheDir()+"/FinalVideo");
            List<File> files = Arrays.asList(directory.listFiles());
            List<File> results = new ArrayList<>();
            for (int i = 0; i < files.size(); i++)
            {
                String name = files.get(i).getName();
                if(name.contains(idIntervention))
                    results.add(files.get(i));
            }
            return results;

        }catch (Exception ex){
            return new ArrayList<>();
        }
    }

    public static List<File> getAudios(String idIntervention,Activity activity) {
        File directory = null;
        try {
            directory = new File(activity.getExternalCacheDir()+"/FinalAudios");
            List<File> files = Arrays.asList(directory.listFiles());
            List<File> results = new ArrayList<>();
            for (int i = 0; i < files.size(); i++)
            {
                String name = files.get(i).getName();
                if(name.contains(idIntervention))
                    results.add(files.get(i));
            }
            return results;

        }catch (Exception ex){
            return new ArrayList<>();
        }
    }

    public static List<File> getComments(String idIntervention,Activity activity) {
        File directory = null;
        try {
            directory = new File(activity.getExternalCacheDir()+"/FinalComments");
            List<File> files = Arrays.asList(directory.listFiles());
            List<File> results = new ArrayList<>();
            for (int i = 0; i < files.size(); i++)
            {
                String name = files.get(i).getName();
                if(name.contains(idIntervention))
                    results.add(files.get(i));
            }
            return results;

        }catch (Exception ex){
            return new ArrayList<>();
        }
    }

    public static boolean deleteAllData(String idIntervention,Activity activity){
        boolean deleteResult = true;
        List<File> listFiles = new ArrayList<>();
        listFiles.addAll(getVideos(idIntervention,activity));
        listFiles.addAll(getAudios(idIntervention,activity));
        listFiles.addAll(getImages(idIntervention,activity));
        listFiles.addAll(getComments(idIntervention,activity));
        try{
            for (File file:listFiles) {
                if(!file.delete())
                    deleteResult = false;
            }
            return deleteResult;
        }
        catch (Exception ex){
            System.out.printf(ex.getMessage());
            return false;
        }
    }
}
