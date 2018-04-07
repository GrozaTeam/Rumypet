package dognose.cd_dog;

import android.graphics.drawable.Drawable;

/**
 * Created by paeng on 2018. 4. 8..
 */

public class ListViewItem {

    private Drawable iconDrawable ;
    private String nameStr ;
    private String speciesStr ;
    private String genderStr ;
    private String ageStr ;

    public void setIcon(Drawable icon) {
        iconDrawable = icon ;
    }
    public void setNameStr(String name) {
        nameStr = name ;
    }
    public void setSpeciesStr(String species){
        speciesStr = species;
    }
    public void setGenderStr(String gender) {
        genderStr = gender ;
    }
    public void setAgeStr(String age){
        ageStr = age ;
    }
    public Drawable getIcon() {
        return this.iconDrawable ;
    }
    public String getSpeciesStr(){
        return this.speciesStr;
    }
    public String getNameStr() {
        return this.nameStr ;
    }
    public String getGenderStr() {
        return this.genderStr ;
    }
    public String getAgeStr() {
        return this.ageStr ;
    }
}
