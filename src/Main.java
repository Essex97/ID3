import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[]args){
        ArrayList<Car> data = readFile("car.data");
        if(data != null) Collections.shuffle(data);

        double trainSize = ((double)data.size()/100)*80;

        ArrayList<Car> train = new ArrayList<Car>();
        for(int i = 0; i < trainSize - 1; i++){
            train.add(data.get(i));
        }

        double validateSize = ((double)data.size()/100)*10;

        ArrayList<Car> validate = new ArrayList<Car>();
        for(int i = (int)trainSize; i < trainSize + validateSize - 1; i++){
            validate.add(data.get(i));
        }

        ArrayList<Car> test = new ArrayList<Car>();
        for(int i = (int)(trainSize + validateSize); i < data.size(); i++){
            test.add(data.get(i));
        }


        ArrayList<String> attributes = new ArrayList<String>();
        attributes.add("buying");
        attributes.add("maint");
        attributes.add("doors");
        attributes.add("persons");
        attributes.add("lug_boot");
        attributes.add("safety");

        Node root = new Node(train, null, false, null, null);

        ID3(root, attributes, null); //Trainings the algorithm with train data

        System.out.println("\n------------THE TREE--------------------------------\n");

        print_tree(root);  //print the tree

        validate(validate, root);  //Validating the algorithm with validation data

        test(test, root, true); //Testing the algorithm with test data

    }


    private static void ID3 (Node root, ArrayList<String> attributes, String defaultCategory){

        if(root.getData() == null || root.getData().size() == 0 ){

            return;

        }else{ //if all the instances of the data belongs at the same category return this category

            int count = 0;
            for(Car tempCar : root.getData()){
                if (tempCar.getCategory().equals(root.getData().get(0).getCategory()))
                    count++;
            }
            if(count == root.getData().size()) {
                root.setLeaf(true);
                root.setLabel(root.getData().get(0).getCategory());
                return;
            }

        }

        int countUnacc = 0;
        int countAcc = 0;
        int countGood = 0;
        int countVgood = 0;

        for(Car temp : root.getData()){
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

        if(max == countUnacc)
            defaultCategory = "unacc";
        else if(max == countAcc)
            defaultCategory = "acc";
        else if(max == countGood)
            defaultCategory = "good";
        else
            defaultCategory = "vgood";


        if(attributes == null || attributes.size() == 0){ //if we don't have attributes we return the most recent category of the data
            root.setLabel(defaultCategory);
            root.setLeaf(true);
            return;
        }

        String bestAttribute = maxIG(root.getData(), attributes);
        root.setAttribute(bestAttribute);

        attributes.remove(bestAttribute);
        System.out.println("Best attribute: "+ bestAttribute);

        ArrayList<String> valuesOfBestAttr = getAttributeValues(bestAttribute);

        ArrayList<Car> examples;

        for(String value : valuesOfBestAttr){

            examples = new ArrayList<Car>();
            for(Car tempcar : root.getData()){

                if(tempcar.getValueOf(bestAttribute).equals(value))
                    examples.add(tempcar);

            }

            Node children = new Node(examples, null, false, bestAttribute, value);
            root.setChildren(children);
            ID3(children, attributes, defaultCategory);
        }
    }

    //returns the entropy of the given set of data
    private static double calcEntropy(ArrayList<Car> data){

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
            double [] P = new double[4];
            //P[i] = P[C=c]
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
    private static double log2(double x)
    {
        return (Math.log(x)/Math.log(2));
    }


    //returns the InformationGain of this attribute
    //remember IG(C,X) = H(C) - ΣP(X=x)*H(C|X=x) where H is entropy and C the category
    private static double calcIG(ArrayList<Car> data, String attribute){

        double entropy = calcEntropy(data); //This is H(C) on the type

        ArrayList<String> attributeValues = getAttributeValues(attribute); //Values of the given attribute
        ArrayList<Car> [] sortedData = new ArrayList[attributeValues.size()];   //Data sorted by value of the given attribute

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

        for(int i = 0; i < attributeValues.size(); i++){
            System.out.println("attribute: "+attribute +", attribute value: "+attributeValues.get(i));
            if (sortedData[i].size() == 0) continue;

            P[i] = ((double)sortedData[i].size()) / data.size(); //This is P(X=x) on the type where X:atribute, x:value
            H[i] = calcEntropy(sortedData[i]);                   //This is H(C|X=x) on the type where X:atribute, x:value
            IG -= P[i] * H[i];                                   //IG(C,X) = H(C) - Σ P(X=x)*H(C|X=x)
        }
        System.out.println("IG of "+attribute +": " +IG);

        return IG;
    }


   //returns the attribute with the max InformationGain in this set of data
    private static String maxIG(ArrayList<Car> data, ArrayList<String> attributes){

        ArrayList<Double> attributesIG = new ArrayList<Double>();

        for(int i = 0; i < attributes.size(); i++){
            attributesIG.add(calcIG(data, attributes.get(i)));
        }
        double maxIG = Collections.max(attributesIG);

        return attributes.get(attributesIG.indexOf(maxIG));
    }


    //returns a list with the possible values of this attribute
    private static ArrayList<String> getAttributeValues(String attribute){

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

    //returns an int who represent each category
    private static int getCategoryRepresentation(String category){
        switch (category) {
            case "unacc": return 1;
            case "acc": return 2;
            case "good": return 3;
            case "vgood": return 4;
        }
        return 0;
    }

    private static double test(ArrayList<Car> test, Node root, boolean toPrint){

        int sqErr = 0;                  //We will use as error the square Error
        String predictedCategory;

        int sumOfCorrectPredictUnacc = 0;
        int sumOfCorrectPredictAcc = 0;
        int sumOfCorrectPredictGood = 0;
        int sumOfCorrectPredictVgood = 0;

        int sumOfPredictedUnacc = 0;
        int sumOfPredictedAcc = 0;
        int sumOfPredictedGood = 0;
        int sumOfPredictedVgood = 0;

        for(Car tempCar : test){        //Testing the algorithm with test data

            predictedCategory = predict(tempCar, root, toPrint);

            if(predictedCategory == null){
                if(toPrint) System.out.println("Something went wrong...");
            }else if(predictedCategory.equals(tempCar.getCategory())){

                switch (tempCar.getCategory()) {
                    case "unacc":
                        sumOfCorrectPredictUnacc += 1;
                        sumOfPredictedUnacc += 1 ;
                        break;
                    case "acc":
                        sumOfCorrectPredictAcc += 1;
                        sumOfPredictedAcc += 1;
                        break;
                    case "good":
                        sumOfCorrectPredictGood += 1;
                        sumOfPredictedGood += 1;
                        break;
                    case "vgood":
                        sumOfCorrectPredictVgood += 1;
                        sumOfPredictedVgood += 1;
                        break;
                }

                continue; //it means that the prediction is true so we don't have error

            }else{
                if(predictedCategory.equals("unacc")){                                                     //predicted value: 1 => which represent unacc category
                    sqErr += Math.pow(Math.abs(1 - getCategoryRepresentation(tempCar.getCategory())), 2);  //actual value   : getCategoreRepresentation(tempCar.getCategory())
                    sumOfPredictedUnacc += 1;

                }else if(predictedCategory.equals("acc")){                                                //predicted value: 2 => which represent acc category
                    sqErr += Math.pow(Math.abs(2 - getCategoryRepresentation(tempCar.getCategory())), 2); //actual value   : getCategoreRepresentation(tempCar.getCategory())
                    sumOfPredictedAcc += 1;

                }else if(predictedCategory.equals("good")){                                               //predicted value: 3 => which represent good category
                    sqErr += Math.pow(Math.abs(3 - getCategoryRepresentation(tempCar.getCategory())), 2); //actual value   : getCategoreRepresentation(tempCar.getCategory())
                    sumOfPredictedGood += 1;

                }else if(predictedCategory.equals("vgood")){                                              //predicted value: 4 => which represent vgood category
                    sqErr += Math.pow(Math.abs(4 - getCategoryRepresentation(tempCar.getCategory())), 2); //actual value   : getCategoreRepresentation(tempCar.getCategory())
                    sumOfPredictedVgood += 1;
                }
            }

        }

        int sumOfUnacc = 0;
        int sumOfAcc = 0;
        int sumOfGood = 0;
        int sumOfVgood = 0;

        for(Car car : test){
            switch (car.getCategory()) {
                case "unacc": sumOfUnacc += 1;
                case "acc": sumOfAcc += 1;
                case "good": sumOfGood += 1;
                case "vgood": sumOfVgood += 1;
            }
        }

        double accuracy = (double)(sumOfCorrectPredictAcc + sumOfCorrectPredictGood + sumOfCorrectPredictUnacc + sumOfCorrectPredictVgood)/test.size() * 100;

        if(toPrint){
            System.out.println("\nSquare error is: "+ sqErr +"\n");

            System.out.println("Precision for Unacc category: "+ (double)sumOfCorrectPredictUnacc/sumOfPredictedUnacc);
            System.out.println("Recall for Unacc category: "+ (double)sumOfCorrectPredictUnacc/sumOfUnacc+"\n");

            System.out.println("Precision for Acc category: "+ (double)sumOfCorrectPredictAcc/sumOfPredictedAcc);
            System.out.println("Recall for Acc category: "+ (double)sumOfCorrectPredictAcc/sumOfAcc+"\n");

            System.out.println("Precision for Good category: "+ (double)sumOfCorrectPredictGood/sumOfPredictedGood);
            System.out.println("Recall for Good category: "+ (double)sumOfCorrectPredictGood/sumOfGood+"\n");

            System.out.println("Precision for Very Good category: "+ (double)sumOfCorrectPredictVgood/sumOfPredictedVgood);
            System.out.println("Recall for Very Good category: "+ (double)sumOfCorrectPredictVgood/sumOfVgood+"\n");

            System.out.println("Accuracy: "+ (int)accuracy +"%");
        }

        return accuracy;
    }

    private static String predict(Car car, Node root, Boolean toPrint){

        if(root == null){
            System.out.println("Our tree is null");
            return null;
        }else if(root.isLeaf()){
            if(toPrint) System.out.println("The car belongs to "+root.getLabel()+".");
            return root.getLabel();
        }

        String attributeVal = car.getValueOf(root.getAttribute());

        for(int j = 0; j<root.getChildren().size(); j++){

            if(root.getChildren().get(j).getValueOfAtrr().equals(attributeVal) && root.getChildren().get(j).isLeaf()){
                if(toPrint) System.out.println("The car belongs to "+root.getChildren().get(j).getLabel()+".");
                return root.getChildren().get(j).getLabel();
            }

        }
        for(int j = 0; j<root.getChildren().size(); j++) {
            if(root.getChildren().get(j).isLeaf()) //That means tha the node is Leaf but the value of the attribute on the given car are not the same
                continue;
            String prediction = predict(car, root.getChildren().get(j), toPrint);
            if(prediction != null) return prediction; //if its null we continue the search on the others children
        }
        return null;
    }

    public static void validate(ArrayList<Car> validate, Node root){

        HashMap<Node, Double> set = new HashMap<Node, Double>();

        ArrayList<Node> nodesTovisit = new ArrayList<Node>();

        for(int i = 0; i < root.getChildren().size(); i++){
            nodesTovisit.add(root.getChildren().get(i));    //add all the children of the root
        }


        Node currentNode;

        while(nodesTovisit.size() != 0){

            currentNode = nodesTovisit.remove(0);
            if(!currentNode.isLeaf()){

                for(int i = 0; i < currentNode.getChildren().size(); i++){

                    nodesTovisit.add(currentNode.getChildren().get(i));

                }

                currentNode.setLeaf(true); //prune the subtree
                set.put(currentNode, test(validate, root, false));
                currentNode.setLeaf(false); //restore the pruned tree so we can prune another subtree
            }

        }

        double maxValue = (Collections.max(set.values()));
        for(Map.Entry<Node, Double> entry : set.entrySet() ){
            if(entry.getValue() == maxValue){
                entry.getKey().setLeaf(true); //prune the subtree which gave me the max accurancy
            }
        }


    }

    private static void print_tree(Node root){
        if(root != null ){
            System.out.print("attribute: "+root.getAttribute() +", value :"+ root.getValueOfAtrr() +", CATEGORY: "+root.getLabel());
            System.out.println("\n----------------------------------------------------------------------------");
            if(root.getChildren() != null){
                for(int j = 0; j<root.getChildren().size(); j++){
                    System.out.print("attribute: "+root.getChildren().get(j).getAttribute() +", value :"+ root.getChildren().get(j).getValueOfAtrr() +", CATEGORY: "+root.getChildren().get(j).getLabel()+" | ");
                }
                System.out.println("\n----------------------------------------------------------------------------");
                for(int j = 0; j<root.getChildren().size(); j++){
                    if(root.getChildren().get(j).isLeaf())
                        continue;
                    print_tree(root.getChildren().get(j));
                }
            }
        }
    }

    private static ArrayList<Car> readFile(String data)
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
            System.err.println(e);
        }
        return null;
    }

}
