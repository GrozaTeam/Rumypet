package dognose.cd_dog.ListViewContent;

/**
 * Created by paeng on 2018. 5. 25..
 */

public class ListViewItemCandidate {

    private String rank;
    private String iconUrl ;
    private String nameStr ;
    private String speciesStr ;
    private String genderStr ;
    private String ageStr ;

    public void setRank(String rank){
        this.rank = rank;
    }
    public void setIconUrl(String icon) {
        iconUrl = icon ;
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
    public String getRank(){
        return this.rank;
    }
    public String getIconUrl() { return this.iconUrl ; }
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
