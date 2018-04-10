package dognose.cd_dog.model;

/**
 * Created by paeng on 2018. 4. 10..
 */

public class Dog {
    private String dogId;
    private String ownerId;
    private String dogName;
    private String dogGender;
    private String dogBirth;
    private String dogSpecies;
    private String created_at;
    private String newPassword;
    private String token;

    public void setDogId(String dogId){this.dogId = dogId; }

    public void setOwnerId(String ownerId){
        this.ownerId = ownerId;
    }

    public void setName(String dogName) {
        this.dogName = dogName;
    }

    public void setGender(String dogGender) {
        this.dogGender = dogGender;
    }

    public void setBirth(String dogBirth) {
        this.dogBirth = dogBirth;
    }

    public void setSpecies(String dogSpecies){
        this.dogSpecies = dogSpecies;
    }

    public String getDogId() { return dogId; }

    public String getOwnerId() { return ownerId; }

    public String getName() {
        return dogName;
    }

    public String getGender() {
        return dogGender;
    }

    public String getBirth() {
        return dogBirth;
    }

    public String getSpecies() { return dogSpecies; }

    public String getCreated_at() {
        return created_at;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
