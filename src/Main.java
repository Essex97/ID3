import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Main {

    public static void main(String[]args){
        ArrayList<Car> data = readFile("car.data");
        Node root = new Node(data, null, false);

        ArrayList<String> attributes = new ArrayList<String>();
        attributes.add("buying");
        attributes.add("maint");
        attributes.add("doors");
        attributes.add("persons");
        attributes.add("lug_boot");
        attributes.add("safety");
        String defaultCategory = "good";

        ID3(data, root, attributes, defaultCategory);

    }

    public static String ID3 (ArrayList<Car> data, Node root, ArrayList<String> attributes, String defaultCategory){

        if(root.getData() == null){

            return defaultCategory;

        }else{ //if all the instances of the data belongs at the same category return this category

            int count = 0;
            for(Car tempCar : data){
                if (tempCar.getCategory().equals(data.get(0).getCategory()))
                    count++;
            }
            if(count == data.size()) return data.get(0).getCategory();

        }

        if(attributes == null){ //if we don't have attributes we return the most recent category of the data

            int countUnacc = 0;
            int countAcc = 0;
            int countGood = 0;
            int countVgood = 0;
            for(Car temp : data){
                if (temp.getCategory().equals("unacc"))
                    countUnacc++;
                else if (temp.getCategory().equals("acc"))
                    countAcc++;
                else if (temp.getCategory().equals("good"))
                    countGood++;
                else if (temp.getCategory().equals("vgood"))
                    countVgood++;
            }

            int max = Math.max(countUnacc, Math.max(countAcc, Math.max(countGood, countVgood)));

            if(max == countUnacc) return "unacc";
            else if(max == countAcc) return "acc";
            else if(max == countGood) return "good";
            else return "vgood";

        }

        String bestAttribute = maxIG(data, attributes);
        root.setAttribute(bestAttribute);

        attributes.remove(bestAttribute);

        ArrayList<String> valuesOfBestAttr = getAttributeValues(bestAttribute);

        for(String value : valuesOfBestAttr){
            ArrayList<Car> examples = new ArrayList<Car>();
            for(Car tempcar : data){
                if(tempcar.getValueOf(bestAttribute).equals(value))
                    examples.add(tempcar);
            }
            Node newRoot = new Node(examples, bestAttribute, false);
            return ID3(examples, newRoot, attributes, "good");
        }
        return null;
    }

    //returns the entropy of the given set of data
    public static double calcEntropy(ArrayList<Car> data){
        System.out.println("entropy");
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
            //P[i] = P[C=c]
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
    public static double calcIG(ArrayList<Car> data, String attribute){

        double entropy = calcEntropy(data); //This is H(C) on the type

        ArrayList<String> attributeValues = getAttributeValues(attribute); //Values of the given attribute

        ArrayList<ArrayList<Car>> sortedData = new ArrayList<ArrayList<Car>>(); //Data sorted by value of the given attribute
        //Each ArrayList will contain data with same value at given attribute
        for(Car tempcar : data){

            if(attribute.equals("buying")){

                if(tempcar.getBuying().equals("low")){
                    sortedData.get(0).add(tempcar);           // The Inside ArrayList with index 0 contains all the cars with buying = low
                }else if(tempcar.getBuying().equals("med")){
                    sortedData.get(1).add(tempcar);           // The Inside ArrayList with index 1 contains all the cars with buying = med
                }else if(tempcar.getBuying().equals("high")){
                    sortedData.get(2).add(tempcar);           // The Inside ArrayList with index 2 contains all the cars with buying = high
                }else if(tempcar.getBuying().equals("vhigh")){
                    sortedData.get(3).add(tempcar);           // The Inside ArrayList with index 3 contains all the cars with buying = vhigh
                }

            }else if(attribute.equals("maint")){

                if(tempcar.getMaint().equals("low")){
                    sortedData.get(0).add(tempcar);           // The Inside ArrayList with index 0 contains all the cars with maint = low
                }else if(tempcar.getMaint().equals("med")){
                    sortedData.get(1).add(tempcar);           // The Inside ArrayList with index 1 contains all the cars with maint = med
                }else if(tempcar.getMaint().equals("high")){
                    sortedData.get(2).add(tempcar);           // The Inside ArrayList with index 2 contains all the cars with maint = high
                }else if(tempcar.getMaint().equals("vhigh")){
                    sortedData.get(3).add(tempcar);           // The Inside ArrayList with index 3 contains all the cars with maint = vhigh
                }

            }else if(attribute.equals("doors")){

                if(tempcar.getDoors().equals("2")){
                    sortedData.get(0).add(tempcar);           // The Inside ArrayList with index 0 contains all the cars with doors = 2
                }else if(tempcar.getDoors().equals("3")){
                    sortedData.get(1).add(tempcar);           // The Inside ArrayList with index 1 contains all the cars with doors = 3
                }else if(tempcar.getDoors().equals("4")){
                    sortedData.get(2).add(tempcar);           // The Inside ArrayList with index 2 contains all the cars with doors = 4
                }else if(tempcar.getDoors().equals("5-more")){
                    sortedData.get(3).add(tempcar);           // The Inside ArrayList with index 3 contains all the cars with doors = 5-more
                }

            }else if(attribute.equals("persons")){

                if(tempcar.getPersons().equals("2")){
                    sortedData.get(0).add(tempcar);           // The Inside ArrayList with index 0 contains all the cars with persons = 2
                }else if(tempcar.getPersons().equals("4")){
                    sortedData.get(1).add(tempcar);           // The Inside ArrayList with index 1 contains all the cars with persons = 4
                }else if(tempcar.getPersons().equals("more")){
                    sortedData.get(2).add(tempcar);           // The Inside ArrayList with index 2 contains all the cars with persons = more
                }

            }else if(attribute.equals("lug_boot")){

                if(tempcar.getLug_boot().equals("small")){
                    sortedData.get(0).add(tempcar);           // The Inside ArrayList with index 0 contains all the cars with lug_boot = small
                }else if(tempcar.getLug_boot().equals("med")){
                    sortedData.get(1).add(tempcar);           // The Inside ArrayList with index 1 contains all the cars with lug_boot = med
                }else if(tempcar.getLug_boot().equals("big")){
                    sortedData.get(2).add(tempcar);           // The Inside ArrayList with index 2 contains all the cars with lug_boot = big
                }

            }else if(attribute.equals("safety")){

                if(tempcar.getLug_boot().equals("low")){
                    sortedData.get(0).add(tempcar);           // The Inside ArrayList with index 0 contains all the cars with safety = low
                }else if(tempcar.getLug_boot().equals("med")){
                    sortedData.get(1).add(tempcar);           // The Inside ArrayList with index 1 contains all the cars with safety = med
                }else if(tempcar.getLug_boot().equals("high")){
                    sortedData.get(2).add(tempcar);           // The Inside ArrayList with index 2 contains all the cars with safety = high
                }

            }

        }

        double [] P = new double[attributeValues.size()]; //We will store the probality of each value of an attribute
        double [] H = new double[attributeValues.size()]; //We will store the entropy of each data set with specific value
        double IG = entropy;

        for(int i = 0; i < attributeValues.size(); i++){
            P[i] = sortedData.get(i).size() / data.size();//This is P(X=x) on the type where X:atribute, x:value
            H[i] = calcEntropy(sortedData.get(i));        //This is H(C|X=x) on the type where X:atribute, x:value
            IG -= P[i] * H[i];                            //IG(C,X) = H(C) - Σ P(X=x)*H(C|X=x)
        }

        return IG;
    }

   //returns the attribute with the max InformationGain in this set of data
    public static String maxIG(ArrayList<Car> data, ArrayList<String> attributes){
        ArrayList<Double> attributesIG = new ArrayList<Double>();
        for(int i = 0; i < attributes.size(); i++){
            attributesIG.add(calcIG(data, attributes.get(i)));
        }
        double maxIG = Collections.max(attributesIG);
        return attributes.get(attributesIG.indexOf(maxIG));
    }

    //returns a list with the possible values of this attribute
    public static ArrayList<String> getAttributeValues(String attribute){

        ArrayList<String> values = new ArrayList<>();
        if(attribute.equals("buying") || attribute.equals("maint")){
            values.add("low");
            values.add("med");
            values.add("high");
            values.add("vhigh");
        }else if(attribute.equals("doors")){
            values.add("2");
            values.add("3");
            values.add("4");
            values.add("5-more");
        }else if(attribute.equals("persons")){
            values.add("2");
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


    public static ArrayList<Car> readFile(String data)
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
            return Cars;

        } catch (IOException e) {
            System.out.println(e);

        } catch (NullPointerException e) {
            System.out.println(e);
        }
        return null;
    }


}
