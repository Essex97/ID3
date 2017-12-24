import java.util.ArrayList;

public class Car {

    private String buying;
    private String maint;
    private String doors;
    private String persons;
    private String lug_boot;
    private String safety;
    private String category;

    public Car(String buying, String maint, String doors, String persons, String lug_boot, String safety, String category) {
        this.buying = buying;
        this.maint = maint;
        this.doors = doors;
        this.persons = persons;
        this.lug_boot = lug_boot;
        this.safety = safety;
        this.category = category;
    }

    //-----------------Getters-----------------//
    public String getBuying() {
        return buying;
    }

    public String getMaint() {
        return maint;
    }

    public String getDoors() {
        return doors;
    }

    public String getPersons() {
        return persons;
    }

    public String getLug_boot() {
        return lug_boot;
    }

    public String getSafety() {
        return safety;
    }

    public String getCategory() {
        return category;
    }

    public String getValueOf(String attribute){
        if(attribute.equals("buying"))
            return getBuying();
        else if(attribute.equals("maint"))
            return getMaint();
        else if(attribute.equals("doors"))
            return getDoors();
        else if(attribute.equals("persons"))
            return getPersons();
        else if(attribute.equals("lug_boot"))
            return getLug_boot();
        else if(attribute.equals("safety")){
            return getSafety();
        }
        return null;
    }


}
