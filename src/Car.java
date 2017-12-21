import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Car {

    private String bying;
    private String maint;
    private String doors;
    private String persons;
    private String lug_boot;
    private String safety;
    private String category;

    public Car(String bying, String maint, String doors, String persons, String lug_boot, String safety, String category) {
        this.bying = bying;
        this.maint = maint;
        this.doors = doors;
        this.persons = persons;
        this.lug_boot = lug_boot;
        this.safety = safety;
        this.category = category;
    }
//-----------------Getters-----------------//
    public String getBying() {
        return bying;
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

    public void readFile(String data)
    {
        File f = null;
        BufferedReader reader = null;
        String line;
        String[] splited;
        ArrayList<Car> Cars = new ArrayList<Car>();

        try {
            f = new File(data);
        } catch (NullPointerException e) {
            System.err.println("Error occured while opening files...");
        }

        try {
            reader = new BufferedReader(new FileReader(f));
            System.out.println("File loaded...");
        } catch (FileNotFoundException e) {
            System.err.println("Files not found...");
        }

        try {
            while ((line = reader.readLine()) != null) {

                if (line.length() == 0) continue;
                splited = line.split(",");
                Cars.add(new Car(splited[0], splited[1], splited[2], splited[3], splited[4], splited[5], splited[6]));
            }
        } catch (IOException e) {
            System.out.println(e);

        } catch (NullPointerException e) {
            System.out.println(e);
        }
    }

    //returns the entropy of the given set of data
    public double calcEntropy(ArrayList<car> data){

        if(data.size() == 0){
            return 100000; //return something too high
        }else{
            //Calculate the instances of each category
            double[] sumOfEachCategory = new double[4]; //0:unacc 1:acc 2:good 3:v-good

            for(Car tempcar : data)
            {
                if(tempcar.getCategory().equals("unacc")) {
                    sumOfEachCategory[0]++;
                }else if(tempcar.getCategory().equals("acc")){
                    sumOfEachCategory[1]++;
                }else if(tempcar.getCategory().equals("good")){
                    sumOfEachCategory[2]++;
                }else if(tempcar.getCategory().equals("v-good")){
                    sumOfEachCategory[3]++;
                }
            }

            //We will store the probality of an instance belonging to each category
            double [] P = new double[4];
            for(int i = 0; i < 3; i++){
                P[i] = sumOfEachCategory[i]/data.size();
            }

            //Entropy = ΣP(C=c)*logP(C=c)
            double entropy = 0;
            for(int i = 0; i < 3; i++){
                entropy += P[i]*Math.log(P[i]);
            }

            return entropy;
        }

    }

    //returns the InformationGain of this attribute
    //remember IG(C,X) = H(C) - ΣP(X=x)*H(C|X=x) where H is entropy and C the category
    public double calcIG(ArrayList<car> data, String attribute){

        double entropy = calcEntropy(data); //This is H(C) on the type


    }

    //returns the attribute with the max InformationGain in this set of data
    public String heuristic(ArrayList<car> data, ArrayList<String> remainingAttributes){

    }

    //returns a list withe the possible values of this attribute
    public ArrayList<String> getAttributeValues(String attribute){

        ArrayList<String> values = new ArrayList<>();
        if(attribute.equals("bying") || attribute.equals("maint")){
            values.add("low");
            values.add("med");
            values.add("high");
            values.add("vhigh");
        }else if(attribute.equals("doors")){
            values.add("2");
            values.add("3");
            values.add("4");
            values.add("5more");
        }else if(attribute.equals("persons")){
            values.add("2");
            values.add("3");
            values.add("4");
            values.add("more");
        }else if(attribute.equals("lug_boot")){
            values.add("small");
            values.add("med");
            values.add("big");
        }else if(attribute.equals("safety")){
            values.add("low");
            values.add("med");
            values.add("high");
        }
        return values;
    }
}
