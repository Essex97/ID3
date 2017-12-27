import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Main {

    public static void main(String[]args){
        ArrayList<Car> data = readFile("car1.txt");

        ArrayList<String> attributes = new ArrayList<String>();
        attributes.add("buying");
        attributes.add("maint");
        attributes.add("doors");
        attributes.add("persons");
        attributes.add("lug_boot");
        attributes.add("safety");

        String defaultCategory = "good";

        Node root = new Node(data, null, false);

        ID3(data, root, attributes, defaultCategory);

    }

    public static String ID3 (ArrayList<Car> data, Node root, ArrayList<String> attributes, String defaultCategory){

        if(root.getData() == null || root.getData().size() == 0 ){

            return defaultCategory;

        }else{ //if all the instances of the data belongs at the same category return this category

            int count = 0;
            for(Car tempCar : data){
                if (tempCar.getCategory().equals(data.get(0).getCategory()))
                    count++;
            }
            if(count == data.size()) {
                root.setLabel(root.getData().get(0).getCategory());
                return root.getLabel();
            }

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

            if(max == countUnacc){
                root.setLabel("unacc");
                return root.getLabel();
            }
            else if(max == countAcc){
                root.setLabel("acc");
                return root.getLabel();
            }
            else if(max == countGood) {
                root.setLabel("good");
                return root.getLabel();
            }
            else {
                root.setLabel("vgood");
                return root.getLabel();
            }

        }

        String bestAttribute = maxIG(data, attributes);
        root.setAttribute(bestAttribute);

        attributes.remove(bestAttribute);
        System.out.println("Best attribute: "+ bestAttribute);

        ArrayList<String> valuesOfBestAttr = getAttributeValues(bestAttribute);

        ArrayList<String> tree = new ArrayList<String>();

        ArrayList<Car> examples;

        for(String value : valuesOfBestAttr){

            examples = new ArrayList<Car>();
            for(Car tempcar : data){

                if(tempcar.getValueOf(bestAttribute).equals(value))
                    examples.add(tempcar);

            }
            Node newRoot = new Node(examples, bestAttribute, false);  //Mhpws prepei na valv kai kana value of best attribute edv
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");

            //return ID3(examples, newRoot, attributes, "good"); //Mhpvw edv sto default prepei na mpei h pio syxnh katigoria
            tree.add(ID3(examples, newRoot, attributes, "good"));
            System.out.println(value);
        }
        for(int i = 0; i < tree.size(); i++){
            System.out.println(tree.get(i));
        }
        return null;
    }

    //returns the entropy of the given set of data
    public static double calcEntropy(ArrayList<Car> data){
        System.out.println("entropy method");
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
                }else if(tempcar.getCategory().equals("vgood")){
                    sumOfEachCategory[3]++;
                }
            }

            //We will store the probality of an instance belonging to each category
            //P[i] = P[C=c]
            double [] P = new double[4];
            for(int i = 0; i < 4; i++){
                P[i] = ((double)sumOfEachCategory[i])/data.size();
                System.out.println("P[i]: "+P[i]);
            }

            double entropy = 0;
            //Entropy = ΣP(C=c)*logP(C=c)
            for(int i = 0; i < 4; i++){

                if(P[i] == 0) continue; //Because log(0) is not defined
                entropy -= P[i]*log2(P[i]);

            }
            System.out.println("Entropy: "+entropy);
            return entropy;
        }
    }
    //calculate log with base 2 of x
    public static double log2(double x)
    {
        return (Math.log(x)/Math.log(2));
    }

    //returns the InformationGain of this attribute
    //remember IG(C,X) = H(C) - ΣP(X=x)*H(C|X=x) where H is entropy and C the category
    public static double calcIG(ArrayList<Car> data, String attribute){
        //System.out.println("IG");
        double entropy = calcEntropy(data); //This is H(C) on the type

        ArrayList<String> attributeValues = getAttributeValues(attribute); //Values of the given attribute
        System.out.println("values size "+attributeValues.size());

        //ArrayList<ArrayList<Car>> sortedData = new ArrayList<ArrayList<Car>>(); //Data sorted by value of the given attribute
        ArrayList<Car> [] sortedData = new ArrayList[attributeValues.size()]; //4 is the max sum of values
        for (int i = 0; i < sortedData.length; i++) {
            sortedData[i] = new ArrayList<>();
        }

        //Each ArrayList will contain data with same value at given attribute
        for(Car tempcar : data){

            if(attribute.equals("buying")){

                if(tempcar.getBuying().equals("low")){
                    sortedData[0].add(tempcar);                 // The Inside ArrayList with index 0 contains all the cars with buying = low
                }else if(tempcar.getBuying().equals("med")){
                    sortedData[1].add(tempcar);                 // The Inside ArrayList with index 1 contains all the cars with buying = med
                }else if(tempcar.getBuying().equals("high")){
                    sortedData[2].add(tempcar);                 // The Inside ArrayList with index 2 contains all the cars with buying = high
                }else if(tempcar.getBuying().equals("vhigh")){
                    sortedData[3].add(tempcar);                 // The Inside ArrayList with index 3 contains all the cars with buying = vhigh
                }

            }else if(attribute.equals("maint")){

                if(tempcar.getMaint().equals("low")){
                    sortedData[0].add(tempcar);                // The Inside ArrayList with index 0 contains all the cars with maint = low
                }else if(tempcar.getMaint().equals("med")){
                    sortedData[1].add(tempcar);                // The Inside ArrayList with index 1 contains all the cars with maint = med
                }else if(tempcar.getMaint().equals("high")){
                    sortedData[2].add(tempcar);                // The Inside ArrayList with index 2 contains all the cars with maint = high
                }else if(tempcar.getMaint().equals("vhigh")){
                    sortedData[3].add(tempcar);                // The Inside ArrayList with index 3 contains all the cars with maint = vhigh
                }

            }else if(attribute.equals("doors")){

                if(tempcar.getDoors().equals("2")){
                    sortedData[0].add(tempcar);                 // The Inside ArrayList with index 0 contains all the cars with doors = 2
                }else if(tempcar.getDoors().equals("3")){
                    sortedData[1].add(tempcar);                 // The Inside ArrayList with index 1 contains all the cars with doors = 3
                }else if(tempcar.getDoors().equals("4")){
                    sortedData[2].add(tempcar);                 // The Inside ArrayList with index 2 contains all the cars with doors = 4
                }else if(tempcar.getDoors().equals("5more")){
                    sortedData[3].add(tempcar);                 // The Inside ArrayList with index 3 contains all the cars with doors = 5-more
                }

            }else if(attribute.equals("persons")){

                if(tempcar.getPersons().equals("2")){
                    sortedData[0].add(tempcar);                 // The Inside ArrayList with index 0 contains all the cars with persons = 2
                }else if(tempcar.getPersons().equals("4")){
                    sortedData[1].add(tempcar);                 // The Inside ArrayList with index 1 contains all the cars with persons = 4
                }else if(tempcar.getPersons().equals("more")){
                    sortedData[2].add(tempcar);                 // The Inside ArrayList with index 2 contains all the cars with persons = more
                }

            }else if(attribute.equals("lug_boot")){

                if(tempcar.getLug_boot().equals("small")){
                    sortedData[0].add(tempcar);                 // The Inside ArrayList with index 0 contains all the cars with lug_boot = small
                }else if(tempcar.getLug_boot().equals("med")){
                    sortedData[1].add(tempcar);                 // The Inside ArrayList with index 1 contains all the cars with lug_boot = med
                }else if(tempcar.getLug_boot().equals("big")){
                    sortedData[2].add(tempcar);                 // The Inside ArrayList with index 2 contains all the cars with lug_boot = big
                }

            }else if(attribute.equals("safety")){

                if(tempcar.getSafety().equals("low")){
                    sortedData[0].add(tempcar);                 // The Inside ArrayList with index 0 contains all the cars with safety = low
                }else if(tempcar.getSafety().equals("med")){
                    sortedData[1].add(tempcar);                 // The Inside ArrayList with index 1 contains all the cars with safety = med
                }else if(tempcar.getSafety().equals("high")){
                    sortedData[2].add(tempcar);                 // The Inside ArrayList with index 2 contains all the cars with safety = high
                }

            }

        }

        double [] P = new double[attributeValues.size()]; //We will store the probality of each value of an attribute
        double [] H = new double[attributeValues.size()]; //We will store the entropy of each data set with specific value
        double IG = entropy;
        System.out.println(" started H[C] :"+IG);

        for(int i = 0; i < attributeValues.size(); i++){
            System.out.println("attribute: "+attribute +", attribute value: "+attributeValues.get(i));
            if (sortedData[i].size() == 0) continue;

            System.out.println(sortedData[i].size() +", "+ data.size());
            P[i] = ((double)sortedData[i].size()) / data.size(); //This is P(X=x) on the type where X:atribute, x:value
            System.out.println("P[i] :"+P[i]);

            H[i] = calcEntropy(sortedData[i]);                 //This is H(C|X=x) on the type where X:atribute, x:value
            System.out.println("H[i] :"+H[i]);
            IG -= P[i] * H[i];                                 //IG(C,X) = H(C) - Σ P(X=x)*H(C|X=x)
        }
        System.out.println("IG of "+attribute +": " +IG);

        return IG;
    }


   //returns the attribute with the max InformationGain in this set of data
    public static String maxIG(ArrayList<Car> data, ArrayList<String> attributes){
        //System.out.println("maxIG");
        ArrayList<Double> attributesIG = new ArrayList<Double>();
        for(int i = 0; i < attributes.size(); i++){
            attributesIG.add(calcIG(data, attributes.get(i)));
        }
        double maxIG = Collections.max(attributesIG);
        System.out.println("INDEX "+attributesIG.indexOf(maxIG) +" max IG: "+ maxIG);
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
            values.add("5more");
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
