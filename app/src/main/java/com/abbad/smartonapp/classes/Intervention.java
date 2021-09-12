package com.abbad.smartonapp.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Intervention  implements Parcelable{

    private String id;
    private String title;
    private String date;
    private List<User> collaboraters;
    private int gravity;
    private String type;
    //Used for simplify report generation :
    //private String[] todos;
    //private String[] tools;
    //Used for displaying data :
    private List<Task> listTaches;
    private List<String> listMaterials;
    private List<String> listOutils;
    //Intervention Duration :
    private String idResponsableExecutif;
    private String idContremaitreExploitation;
    private String idResponsable;
    private String fullDateFormat;
    private String idSite;

    private String nomResponsableExecutif;
    private String nomContremaitreExploitation;
    private String nomResponsable;
    private String nomSite;




    public Intervention(String id, String title, String date, List<User> collaboraters, List<Task> listTaches, List<String> listMaterials, List<String> listOutils) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.collaboraters = collaboraters;
        this.listTaches = listTaches;
        this.listMaterials = listMaterials;
        this.listOutils = listOutils;
    }


    protected Intervention(Parcel in) {
        id = in.readString();
        title = in.readString();
        date = in.readString();
        gravity = in.readInt();
        listTaches = in.createTypedArrayList(Task.CREATOR);
        listMaterials = in.createStringArrayList();
        listOutils = in.createStringArrayList();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(date);
        dest.writeInt(gravity);
        dest.writeString(type);
        dest.writeTypedList(listTaches);
        dest.writeStringList(listMaterials);
        dest.writeStringList(listOutils);
        dest.writeString(idResponsableExecutif);
        dest.writeString(idContremaitreExploitation);
        dest.writeString(idResponsable);
        dest.writeString(fullDateFormat);
        dest.writeString(idSite);
        dest.writeString(nomResponsableExecutif);
        dest.writeString(nomContremaitreExploitation);
        dest.writeString(nomResponsable);
        dest.writeString(nomSite);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Intervention> CREATOR = new Creator<Intervention>() {
        @Override
        public Intervention createFromParcel(Parcel in) {
            return new Intervention(in);
        }

        @Override
        public Intervention[] newArray(int size) {
            return new Intervention[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<User> getCollaboraters() {
        return collaboraters;
    }

    public void setCollaboraters(List<User> collaboraters) {
        this.collaboraters = collaboraters;
    }

    public List<Task> getListTaches() {
        return listTaches;
    }

    public void setListTaches(List<Task> listTaches) {
        this.listTaches = listTaches;
    }

    public List<String> getListMaterials() {
        return listMaterials;
    }

    public void setListMaterials(List<String> listMaterials) {
        this.listMaterials = listMaterials;
    }

    public List<String> getListOutils() {
        return listOutils;
    }

    public void setListOutils(List<String> listOutils) {
        this.listOutils = listOutils;
    }

    public String getIdSite() {
        return idSite;
    }

    public void setIdSite(String idSite) {
        this.idSite = idSite;
    }

    public String getIdResponsableExecutif() {
        return idResponsableExecutif;
    }

    public void setIdResponsableExecutif(String idResponsableExecutif) {
        this.idResponsableExecutif = idResponsableExecutif;
    }

    public String getIdContremaitreExploitation() {
        return idContremaitreExploitation;
    }

    public void setIdContremaitreExploitation(String idContremaitreExploitation) {
        this.idContremaitreExploitation = idContremaitreExploitation;
    }

    public String getIdResponsable() {
        return idResponsable;
    }

    public void setIdResponsable(String idResponsable) {
        this.idResponsable = idResponsable;
    }

    public String getFullDateFormat() {
        return fullDateFormat;
    }

    public void setFullDateFormat(String fullDateFormat) {
        this.fullDateFormat = fullDateFormat;
    }

    public String getNomResponsableExecutif() {
        return nomResponsableExecutif;
    }

    public void setNomResponsableExecutif(String nomResponsableExecutif) {
        this.nomResponsableExecutif = nomResponsableExecutif;
    }

    public String getNomContremaitreExploitation() {
        return nomContremaitreExploitation;
    }

    public void setNomContremaitreExploitation(String nomContremaitreExploitation) {
        this.nomContremaitreExploitation = nomContremaitreExploitation;
    }

    public String getNomResponsable() {
        return nomResponsable;
    }

    public void setNomResponsable(String nomResponsable) {
        this.nomResponsable = nomResponsable;
    }

    public String getNomSite() {
        return nomSite;
    }

    public void setNomSite(String nomSite) {
        this.nomSite = nomSite;
    }


}
