import com.mongodb.Block;
import com.mongodb.client.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.bson.Document;

import java.time.LocalDate;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.logging.Level;

public class Main extends Application {
    static final int SEATING_CAPACITY=42;
    HashMap<String,ArrayList<String>> seatBookingBadulla=new HashMap<>();  //HashMap to store seat reservations from Badulla to Colombo using date
    HashMap<String,ArrayList<String>> seatBookingColombo=new HashMap<>();  //HashMap to store seat reservations from Colombo to Badulla using date


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        //------DISPLAY Welcome Page-----Call  displayWelcomePage() method------
        displayWelcomePage();
        String option = "";
        int programRunningTime=1;    //Use to count program running time
        while (!option.equalsIgnoreCase("q")) {
            //******Creating new window to select Train Trip and Date********
            Stage stage = new Stage();
            stage.setTitle("Select date and root");
            AnchorPane anchorPane = new AnchorPane();

            Image logo= new Image("file:trainLogo.png");
            ImageView viewImage=new ImageView();
            viewImage.setImage(logo);
            Label titleLabel=new Label("     DENUWARA MANIKE TRAIN",viewImage);        // create  page title
            titleLabel.setStyle("-fx-font-family:Rockwell Extra Bold;-fx-font-size:30");    //set style for  title
            titleLabel.setLayoutX(20);
            titleLabel.setLayoutY(30);

            Label setDateAndTrip=new Label("  Select your Date and Train Root   ");  //Create Label to say user to select your date and trip
            setDateAndTrip.setStyle("-fx-font-family:Segoe UI Black;-fx-font-size:22;-fx-background-color:#A9A9A9");
            setDateAndTrip.setLayoutX(50);
            setDateAndTrip.setLayoutY(140);
            Label tripOn = new Label("Select Train Root  ");
            tripOn.setLayoutX(50);
            tripOn.setLayoutY(260);

            Label tripTo = new Label("Select Date  ");
            tripTo.setLayoutX(50);
            tripTo.setLayoutY(320);
            //******Create choice box to select train root********
            ChoiceBox<String> choiceBox=new ChoiceBox<>();
            choiceBox .getItems().add("--Choose the Train Root --");
            choiceBox .getItems().add("Colombo to Badulla");
            choiceBox .getItems().add("Badulla to Colombo");
            choiceBox.setValue("--Choose the Train Root --");
            choiceBox.setLayoutX(270);
            choiceBox.setLayoutY(260);

            Button next=new Button("SUBMIT");
            next.setLayoutX(450);
            next.setLayoutY(400);
            next.setStyle("-fx-background-color:#bbdce7; -fx-pref-height:40; -fx-pref-width:100;");

            //******Create Date Picker to select date***********
            DatePicker setDate=new DatePicker();
            LocalDate minimumDate=LocalDate.now();
            System.out.println("Today is : "+minimumDate);
            LocalDate maximumDate =LocalDate.of(2021,12,31);
            setDate.setDayCellFactory(d -> new DateCell(){
                @Override
                public void updateItem(LocalDate item,boolean empty){
                    super.updateItem(item, empty);
                    setDisable(item.isAfter(maximumDate) || item.isBefore(minimumDate));
                }
            });
            HBox hbox = new HBox(setDate);
            hbox.setLayoutX(270);
            hbox.setLayoutY(320);
            //******Get Choice box value and Date picker value******
            final String[] trip = new String[1];
            final String[] date = new String[1];
            next.setOnAction(event -> {
                if (choiceBox.getValue().equals("--Choose the Train Root --")) {
                    choiceBox.setStyle("-fx-background-color:#74D5DD");
                }
                else if (setDate.getValue()==null){
                    setDate.setStyle("-fx-background-color:blue");
                }else {
                    date[0] = String.valueOf(setDate.getValue());
                    trip[0] = choiceBox.getValue();
                    System.out.println("\nTrip to " + trip[0] + " On " + date[0] + "\n");
                    stage.close();
                }
            });

            anchorPane.getChildren().addAll(titleLabel,setDateAndTrip,hbox,tripOn,choiceBox,next,tripTo);
            Scene scene = new Scene(anchorPane, 700 ,500);
            stage.setScene(scene);
            stage.showAndWait();

            String[] seatReservationBadulla = new String[SEATING_CAPACITY];  //create a array for  seat reservations from Badulla to Colombo
            String[] seatReservationColombo = new String[SEATING_CAPACITY];  //create a array for  seat reservations from Colombo to Badulla
            try {
                if (programRunningTime == 1) {
                    if (trip[0].equals("Badulla to Colombo")) {
                        Arrays.fill(seatReservationBadulla, null); //fill array with null values
                    } else {
                        Arrays.fill(seatReservationColombo, null);  //Fill array with null values
                    }
                } else {
                    String reservationDate = date[0];

                    if (trip[0].equals("Badulla to Colombo")) {

                        if (seatBookingBadulla.containsKey(reservationDate)) {  //check hashmap containsKey for specified date
                            ArrayList<String> newArray = (ArrayList<String>) seatBookingBadulla.get(reservationDate).clone();
                            for (int i = 0; i < SEATING_CAPACITY; i++) {
                                seatReservationBadulla[i] = newArray.get(i);
                            }

                        } else {         //If hashmap do not contain key for that specified date then create array for that
                            Arrays.fill(seatReservationBadulla, null);
                        }
                    } else {
                        if (seatBookingColombo.containsKey(reservationDate)) {
                            ArrayList<String> newArray = (ArrayList<String>) seatBookingColombo.get(reservationDate).clone();
                            for (int i = 0; i < SEATING_CAPACITY; i++) {
                                seatReservationColombo[i] = newArray.get(i);
                            }
                        } else {
                            Arrays.fill(seatReservationColombo, null);
                        }
                    }
                }
            }catch (NullPointerException e){
                System.out.println("Before the run program you need to select train trip and date");
                continue;
            }
            menu:
            while (true) {
                String reservationDate = date[0];
                //convert array data to ArrayList
                ArrayList<String> seatReservationBadullaL=new ArrayList<>(Arrays.asList(seatReservationBadulla)); //create a ArrayList for  seat reservations from Badulla to Colombo
                ArrayList<String> seatReservationColomboL=new ArrayList<>(Arrays.asList(seatReservationColombo)); //create a ArrayList for  seat reservations from Colombo to Badulla

                if (trip[0].equals("Badulla to Colombo")){
                    seatBookingBadulla.put(reservationDate,seatReservationBadullaL);  //put ArrayList to the hashmap with specified date
                }else {
                    seatBookingColombo.put(reservationDate,seatReservationColomboL);
                }

                Scanner input = new Scanner(System.in);
                System.out.println();
                System.out.println("DENUWARA MANIKE TRAIN  : MENU");
                System.out.println("..........................................................");
                System.out.println("Enter \"C\" to change Date or Train Root .");
                System.out.println("Enter \"A\" to add a customer .");
                System.out.println("Enter \"V\" to view all the seats .");
                System.out.println("Enter \"E\" to view empty seats .");
                System.out.println("Enter \"D\" to delete a booked seat .");
                System.out.println("Enter \"F\" to find a seat by customer name .");
                System.out.println("Enter \"O\" to view seats ordered alphabetically by name  .");
                System.out.println("Enter \"S\" to store booking details .");
                System.out.println("Enter \"L\" to load booking details ." +
                        "\n   Before you load data from stored file make sure to store all the changes which you made in program." +
                        "\n   Otherwise your reservations or changes will be cancelled.");
                System.out.println("Enter \"Q\" to exit the program .");
                System.out.println("..........................................................");
                System.out.println("What is your opinion : ");
                option = input.next().toLowerCase();
                switch (option) {
                    case "a":
                        if (trip[0].equals("Badulla to Colombo")) {
                            addCustomer(seatReservationBadulla);
                            System.out.println(seatBookingBadulla.values().toString());
                        } else if (trip[0].equals("Colombo to Badulla")) {
                            addCustomer(seatReservationColombo);
                            System.out.println(Arrays.toString(seatReservationColombo));
                        } else {
                            System.out.println("Invalid Input");
                        }
                        break;
                    case "v":
                        if (trip[0].equals("Badulla to Colombo")) {
                            viewAllSeats(seatReservationBadulla);
                        } else if (trip[0].equals("Colombo to Badulla")) {
                            viewAllSeats(seatReservationColombo);
                        } else {
                            System.out.println("Invalid Input");
                        }
                        break;
                    case "e":
                        if (trip[0].equals("Badulla to Colombo")) {
                            viewEmptySeats(seatReservationBadulla);
                        } else if (trip[0].equals("Colombo to Badulla")) {
                            viewEmptySeats(seatReservationColombo);
                        } else {
                            System.out.println("Invalid Input");
                        }
                        break;
                    case "d":
                        if (trip[0].equals("Badulla to Colombo")) {
                            deleteBooking(seatReservationBadulla);
                        } else if (trip[0].equals("Colombo to Badulla")) {
                            deleteBooking(seatReservationColombo);
                        } else {
                            System.out.println("Invalid Input");
                        }
                        break;
                    case "f":
                        if (trip[0].equals("Badulla to Colombo")) {
                            findSeatByCustomer(seatReservationBadulla);
                        } else if (trip[0].equals("Colombo to Badulla")) {
                            findSeatByCustomer(seatReservationColombo);
                        } else {
                            System.out.println("Invalid Input");
                        }
                        break;
                    case "o":
                        if (trip[0].equals("Badulla to Colombo")) {
                            orderedSeatByName(seatReservationBadulla,reservationDate);
                        } else if (trip[0].equals("Colombo to Badulla")) {
                            orderedSeatByName(seatReservationColombo,reservationDate);
                        } else {
                            System.out.println("Invalid Input");
                        }
                        break;
                    case "s":
                        storeDetailBadulla(seatBookingBadulla);
                        storeDetailColombo(seatBookingColombo);
                        break;
                    case "l":
                        loadDetailBadulla(seatBookingBadulla);
                        loadDetailColombo(seatBookingColombo);
                        if (trip[0].equals("Badulla to Colombo")) {
                            //replace loading data to the program data structure
                            if (seatBookingBadulla.containsKey(reservationDate)) {  //check hashmap containsKey for specified date
                                ArrayList<String> newArray = (ArrayList<String>) seatBookingBadulla.get(reservationDate).clone();
                                for (int i = 0; i < SEATING_CAPACITY; i++) {
                                    seatReservationBadulla[i] = newArray.get(i);
                                }

                            } else {         //if hashmap do not contain key for that specified date then create array for that
                                Arrays.fill(seatReservationBadulla, null);
                            }
                        } else {
                            if (seatBookingColombo.containsKey(reservationDate)) {
                                ArrayList<String> newArray = (ArrayList<String>) seatBookingColombo.get(reservationDate).clone();
                                for (int i = 0; i < SEATING_CAPACITY; i++) {
                                    seatReservationColombo[i] = newArray.get(i);
                                }
                            } else {
                                Arrays.fill(seatReservationColombo, null);
                            }
                        }
                        break ;
                    case "c":
                        break menu;
                    case "q":
                        System.exit(0);
                        break;
                    default:
                        System.out.print("Invalid Input .Try again");
                        break;
                }
            }
            programRunningTime++;   //Increment programme run time by one
        }
    }
    //---------------STORE all passenger names which are booked for Colombo to Badulla--------------------
    private static void storeDetailColombo(HashMap<String, ArrayList<String>> seatBookingColombo) {
        try {
            java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
            MongoClient mongoClient = MongoClients.create("mongodb://LocalHost:27017");     //creating mongo client
            MongoDatabase database = mongoClient.getDatabase("TrainSeatReservationDb");    //accessing the database
            MongoCollection<Document> collectionColombo = database.getCollection("DenuwaraManikeColombo"); //create table
            // BasicDBObject document1= new BasicDBObject();
            Document document1 = new Document();
            collectionColombo.deleteMany(document1);       //delete all the data that is stored

            seatBookingColombo.forEach(new BiConsumer<String, ArrayList<String>>() {
                @Override
                public void accept(String s, ArrayList arrayList) {
                    document1.append(s, arrayList);
                }
            });
            collectionColombo.insertOne(document1);
            System.out.println("From Colombo to Badulla reservation data successfully stored to the Database ");
        }catch (Exception e){
            System.out.println("Something went wrong!");
        }

    }
    //---------------STORE all passenger names which are booked for Badulla to Colombo--------------------
    private static  void storeDetailBadulla(HashMap<String, ArrayList<String>> seatBookingBadulla) {
        try {
            java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
            MongoClient mongoClient = MongoClients.create("mongodb://LocalHost:27017");   //creating mongo client
            MongoDatabase database = mongoClient.getDatabase("TrainSeatReservationDb");  //accessing the database
            MongoCollection<Document> collectionBadulla = database.getCollection("DenuwaraManikeBadulla"); //create table
            //BasicDBObject document1= new BasicDBObject();
            Document document1 = new Document();
            collectionBadulla.deleteMany(document1);//delete all the data that is stored

            seatBookingBadulla.forEach(new BiConsumer<String, ArrayList<String>>() {
                @Override
                public void accept(String s, ArrayList arrayList) {
                    document1.append(s, arrayList);
                }
            });
            collectionBadulla.insertOne(document1);
            System.out.println("From Bdulla to Colombo reservation data successfully stored to the Database ");
        }catch (Exception e){
            System.out.println("Something went wrong!");
        }

    }
    //---------------LOAD all passenger names which are booked for Colombo to Badulla--------------------
    private static void loadDetailColombo(HashMap<String, ArrayList<String>> seatBookingColombo) {
        try {
            java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
            MongoClient mongoClient = MongoClients.create("mongodb://LocalHost:27017");   //creating mongo client
            MongoDatabase database = mongoClient.getDatabase("TrainSeatReservationDb");  //accessing the database
            MongoCollection<Document> collectionColombo = database.getCollection("DenuwaraManikeColombo"); //create table for Colombo to Badulla

            collectionColombo.find().forEach(new Block<Document>() {
                @Override
                public void apply(Document document) {
                    for (String id : document.keySet()) {
                        if (id.equals("_id"))
                            continue;
                        Object arrayObject = document.get(id);
                        ArrayList loadList = (ArrayList) arrayObject;
                        seatBookingColombo.put(id, loadList);
                    }
                }
            });
            System.out.println("Load successfully details of Colombo to Badulla Reservation ");
            System.out.println(seatBookingColombo);
        }catch (Exception e){
            System.out.println("Something went wrong!");
        }
    }
    //---------------LOAD all passenger names which are booked for Badulla to Colombo--------------------
    private static void loadDetailBadulla(HashMap<String, ArrayList<String>> seatBookingBadulla) {
        try {

            java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
            MongoClient mongoClient = MongoClients.create("mongodb://LocalHost:27017");   //creating mongo client
            MongoDatabase database = mongoClient.getDatabase("TrainSeatReservationDb");  //accessing the database
            MongoCollection<Document> collectionBadulla = database.getCollection("DenuwaraManikeBadulla"); //create table for Badulla to Colombo

            collectionBadulla.find().forEach(new Block<Document>() {
                @Override
                public void apply(Document document) {
                    for (String id : document.keySet()) {
                        if (id.equals("_id"))
                            continue;
                        Object arrayObject = document.get(id);
                        ArrayList loadList = (ArrayList) arrayObject;
                        seatBookingBadulla.put(id, loadList);
                    }
                }
            });
            System.out.println("Load successfully details of Badulla to Colombo reservation ");
            System.out.println(seatBookingBadulla);
        }catch (Exception e){
            System.out.println("Some thing went wrong!");
        }
    }
    //---------------view seats ordered alphabetically by passenger name---------------------------------
    private static void orderedSeatByName(String[] seatAdding,String reservationDate) {

        List<String> sortList = new ArrayList<>();
        for (int x = 0; x < seatAdding.length; x++) {
            if (seatAdding[x] != null) {
                String name = seatAdding[x].toLowerCase();
                sortList.add(name + " seat number " + (x + 1));
            }
        }
        if (!sortList.isEmpty()) {
            System.out.println("Booked passenger list before sort : ");
            for (String value : sortList) {
                System.out.println(value);
            }
            for (int i = 0; i < sortList.size(); i++) {
                for (int j = 0; j < sortList.size() - i - 1; j++) {
                    if (sortList.get(j + 1).compareTo(sortList.get(j)) < 0) {
                        String temp = sortList.get(j);
                        sortList.set(j, sortList.get(j + 1));
                        sortList.set(j + 1, temp);
                    }
                }
            }
            System.out.println("Booked passenger list after Sort : ");
            for (String value : sortList) {
                System.out.println(value);
            }
        }else {
            System.out.println("No reservation record for this "+ reservationDate+" date . Unavailable to sort data according to the passenger name");
        }
    }
    //---------------FIND the seat for given customer name-----------------------------------------------
    private static void findSeatByCustomer(String[] seatAdding) {
        Scanner find = new Scanner(System.in);
        System.out.println("Enter find passenger name : ");
        String findName = find.nextLine().toLowerCase();
        int findNo = 1;
        int findCount = 0;
        while (findNo <= seatAdding.length) {
            if (seatAdding[findNo - 1] != null) {
                if (seatAdding[findNo - 1].equals(findName)) {
                    System.out.println("Find customer's seat : " + findNo);
                    findCount += 1;
                }
            }
            findNo += 1;
        }
        if (findCount == 0) {
            System.out.println("For this name do not have any seat reservation .\nUnavailable to find this record.");
        }

    }
    //---------------DELETE the seat for given customer name---------------------------------------------
    private static void deleteBooking(String[] seatAdding) {
        Scanner delete=new Scanner(System.in);
        System.out.println("Enter passenger name you want to delete : ");
        String deleteName=delete.nextLine().toLowerCase();
        int deleteNo=1;
        int deleteCount=0;
        while (deleteNo<=seatAdding.length){
            if (seatAdding[deleteNo-1]!=null){
                if (seatAdding[deleteNo - 1].equals(deleteName)){
                    seatAdding[deleteNo-1]=null;
                    System.out.println("Your seat reservation is successfully deleted.");
                    System.out.println("Your reservation deleted seat number : "+deleteNo);
                    deleteCount+=1;
                }
            }
            deleteNo+=1;
        }
        if (deleteCount==0)
        {System.out.println("For this name do not have any seat reservation .\nUnavailable to delete record .");}
    }
    //---------------VIEW EMPTY seats--------------------------------------------------------------------
    private static void viewEmptySeats(String[] seatAdding) {
        Stage viewSeats = new Stage();
        viewSeats.setTitle("View of Empty seats");
        AnchorPane layoutView = new AnchorPane();
        Image logo= new Image("file:trainLogo.png");
        ImageView viewImage=new ImageView();
        viewImage.setImage(logo);
        Label label1=new Label("     DENUWARA MANIKE TRAIN ",viewImage);
        label1.setLayoutY(15);
        label1.setLayoutX(30);
        label1.setStyle("-fx-font-family:Rockwell Extra Bold;-fx-font-size:30");   //set style for label
        Label label = new Label("Up view of all seats ");
        label.setStyle("-fx-background-color:blue;-fx-text-fill:white;-fx-padding:10px;-fx-font-family:Arial;-fx-font-size:20");
        label.setLayoutX(200);
        label.setLayoutY(115);
        Button button1 = new Button("Empty seat");
        Button button2 = new Button("Booked seat");
        button1.setLayoutX(130); //*** set position of empty seats identifier button
        button1.setLayoutY(180);
        button2.setLayoutX(375); //*** set position of booked seats identifier button
        button2.setLayoutY(180);
        button1.setStyle("-fx-background-color:#00BFFF");  //***set background color of empty seats identifier button
        button2.setStyle("-fx-background-color:red");      //***set background color of booked seats identifier button
        VBox column1 = new VBox(20);
        List<Button> buttonList1 = new ArrayList<>();
        for (int c = 1; c < 42; c += 4) {
            Button seatColumn = new Button("Seat " + c);
            buttonList1.add(seatColumn);
            seatColumn.setStyle("-fx-background-color:#00BFFF");
            seatColumn.setMaxWidth(100);
            column1.getChildren().add(seatColumn);
            //****If the relevant seat do not have  reservation then that button shows blue color otherwise it is not visible****

            if (seatAdding[c - 1] == null) {
                seatColumn.setStyle("-fx-background-color:#00BFFF");
            } else {
                seatColumn.setVisible(false);
            }
        }
        System.out.println();
        VBox column2 = new VBox(20);
        List<Button> buttonList2 = new ArrayList<>();
        for (int c = 2; c < 39; c += 4) {
            Button seatColumn = new Button("Seat " + c);
            buttonList2.add(seatColumn);
            seatColumn.setStyle("-fx-background-color:#00BFFF");
            seatColumn.setMaxWidth(100);
            column2.getChildren().add(seatColumn);
            //****If the relevant seat do not have  reservation then that button shows blue color otherwise it is not visible****

            if (seatAdding[c - 1] == null) {
                seatColumn.setStyle("-fx-background-color:#00BFFF");
            } else {
                seatColumn.setVisible(false);
            }
        }
        System.out.println();
        VBox column3 = new VBox(20);
        List<Button> buttonList3 = new ArrayList<>();
        for (int c = 3; c < 40; c += 4) {
            Button seatColumn = new Button("Seat " + c);
            buttonList3.add(seatColumn);
            seatColumn.setStyle("-fx-background-color:##00BFFF");
            seatColumn.setMaxWidth(100);
            column3.getChildren().add(seatColumn);
            //****If the relevant seat do not have  reservation then that button shows blue color otherwise it is not visible****

            if (seatAdding[c - 1] == null) {
                seatColumn.setStyle("-fx-background-color:#00BFFF");
            } else {
                seatColumn.setVisible(false);
            }
        }
        System.out.println();
        VBox column4 = new VBox(20);
        List<Button> buttonList4 = new ArrayList<>();
        //System.out.println(Arrays.deepToString(seatAdding));
        for (int c = 4; c < 43; c += 4) {
            Button seatColumn = new Button("Seat " + c);
            seatColumn.setStyle("-fx-background-color:#00BFFF");
            seatColumn.setMaxWidth(100);
            buttonList4.add(seatColumn);
            column4.getChildren().add(seatColumn);
            //****If the relevant seat do not have  reservation then that button shows blue color otherwise it is not visible****

            if (seatAdding[c - 1] == null) {
                seatColumn.setStyle("-fx-background-color:#00BFFF");
            } else {
                seatColumn.setVisible(false);
            }
        }
        System.out.println();
        Button seat42=new Button("Seat 42");
        column4.getChildren().add(seat42);
        seat42.setStyle("-fx-background-color:#00BFFF");
        seat42.setMaxWidth(100);
        //****If the relevant seat do not have  reservation then that button shows blue color otherwise it is not visible****

        if (seatAdding[41]==null){
            seat42.setStyle("-fx-background-color:#00BFFF");
        }
        else {
            seat42.setVisible(false);
        }
        Button back=new Button("GO TO MENU");
        back.setLayoutY(820);back.setLayoutX(250);
        back.setOnAction(event -> viewSeats.close());
        column1.setPadding(new Insets(20,20,20,20));
        column1.setLayoutX(40);
        column1.setLayoutY(240);
        column2.setPadding(new Insets(20,20,20,20));
        column2.setLayoutX(140);
        column2.setLayoutY(240);
        column3.setPadding(new Insets(20,20,20,20));
        column3.setLayoutX(360);
        column3.setLayoutY(240);
        column4.setPadding(new Insets(20,20,20,20));
        column4.setLayoutX(460);
        column4.setLayoutY(240);
        layoutView.getChildren().addAll(column1,column2,column3,column4,back,label,label1,button1,button2);
        Scene scene=new Scene(layoutView,600,900);
        viewSeats.setScene(scene);
        viewSeats.setResizable(false);
        viewSeats.showAndWait();


    }
    //---------------VIEW ALL seats----------------------------------------------------------------------
    private static void viewAllSeats(String[] seatAdding) {
        Stage viewSeats = new Stage();
        viewSeats.setTitle("View of Empty seats");
        AnchorPane layoutView = new AnchorPane();
        //***Insert image****
        Image logo= new Image("file:trainLogo.png");
        ImageView viewImage=new ImageView();
        viewImage.setImage(logo);

        Label label1=new Label("     DENUWARA MANIKE TRAIN ",viewImage);
        label1.setLayoutY(15);
        label1.setLayoutX(30);
        label1.setStyle("-fx-font-family:Rockwell Extra Bold;-fx-font-size:30");   //set style
        Label label = new Label("Up view of all seats ");
        label.setStyle("-fx-background-color:blue;-fx-text-fill:white;-fx-padding:10px;-fx-font-family:Arial;-fx-font-size:20");
        label.setLayoutX(200);
        label.setLayoutY(115);
        Button button1 = new Button("Empty seat");
        Button button2 = new Button("Booked seat");
        button1.setLayoutX(130);   // *****set position of empty seats identifier button
        button1.setLayoutY(180);
        button2.setLayoutX(375);    // *****set position of booked seats identifier button
        button2.setLayoutY(180);
        button1.setStyle("-fx-background-color:#00BFFF");    //****set background color of empty seats identifier button
        button2.setStyle("-fx-background-color:red");        //****set background color of booked seats identifier button
        VBox column1 = new VBox(20);
        List<Button> buttonList1 = new ArrayList<>();
        for (int c = 1; c < 42; c += 4) {
            Button seatColumn = new Button("Seat " + c);
            buttonList1.add(seatColumn);
            seatColumn.setStyle("-fx-background-color:#00BFFF");
            seatColumn.setMaxWidth(100);
            column1.getChildren().add(seatColumn);
            //****If the relevant seat do not have  reservation then that button shows blue color otherwise it shows red****

            if (seatAdding[c - 1] == null) {
                seatColumn.setStyle("-fx-background-color:#00BFFF");
            } else {
                //seatColumn.setDisable(true);
                seatColumn.setStyle("-fx-background-color:red");
            }
        }
        System.out.println();
        VBox column2 = new VBox(20);
        List<Button> buttonList2 = new ArrayList<>();
        for (int c = 2; c < 39; c += 4) {
            Button seatColumn = new Button("Seat " + c);
            buttonList2.add(seatColumn);
            seatColumn.setStyle("-fx-background-color:#00BFFF");
            seatColumn.setMaxWidth(100);
            column2.getChildren().add(seatColumn);
            //****If the relevant seat do not have  reservation then that button shows blue color otherwise it shows red****

            if (seatAdding[c - 1] == null) {
                seatColumn.setStyle("-fx-background-color:#00BFFF");
            } else {
                //seatColumn.setDisable(true);
                seatColumn.setStyle("-fx-background-color:red");
            }
        }
        System.out.println();
        VBox column3 = new VBox(20);
        List<Button> buttonList3 = new ArrayList<>();
        for (int c = 3; c < 40; c += 4) {
            Button seatColumn = new Button("Seat " + c);
            buttonList3.add(seatColumn);
            seatColumn.setStyle("-fx-background-color:##00BFFF");
            seatColumn.setMaxWidth(100);
            column3.getChildren().add(seatColumn);
            //****If the relevant seat do not have  reservation then that button shows blue color otherwise it shows red****

            if (seatAdding[c - 1] == null) {
                seatColumn.setStyle("-fx-background-color:#00BFFF");
            } else {
                //seatColumn.setDisable(true);
                seatColumn.setStyle("-fx-background-color:red");
            }
        }
        System.out.println();
        VBox column4 = new VBox(20);
        List<Button> buttonList4 = new ArrayList<>();
        System.out.println(Arrays.deepToString(seatAdding));
        for (int c = 4; c < 43; c += 4) {
            Button seatColumn = new Button("Seat " + c);
            seatColumn.setStyle("-fx-background-color:#00BFFF");
            seatColumn.setMaxWidth(100);
            buttonList4.add(seatColumn);
            column4.getChildren().add(seatColumn);
            //****If the relevant seat do not have  reservation then that button shows blue color otherwise it shows red****

            if (seatAdding[c - 1] == null) {
                seatColumn.setStyle("-fx-background-color:#00BFFF");
            } else {
                //seatColumn.setDisable(true);
                seatColumn.setStyle("-fx-background-color:red");
            }
        }
        System.out.println();
        Button seat42=new Button("Seat 42");
        column4.getChildren().add(seat42);
        seat42.setStyle("-fx-background-color:#00BFFF");
        seat42.setMaxWidth(100);
        //****If the relevant seat do not have  reservation then that button shows blue color otherwise it shows red****

        if (seatAdding[41]==null){
            seat42.setStyle("-fx-background-color:#00BFFF");
        }
        else {
            //seatColumn.setDisable(true);
            seat42.setStyle("-fx-background-color:red");
        }
        Button back=new Button("GO TO MENU");
        back.setLayoutY(820);back.setLayoutX(250);
        back.setOnAction(event -> viewSeats.close());
        column1.setPadding(new Insets(20,20,20,20));
        column1.setLayoutX(40);
        column1.setLayoutY(240);
        column2.setPadding(new Insets(20,20,20,20));
        column2.setLayoutX(140);
        column2.setLayoutY(240);
        column3.setPadding(new Insets(20,20,20,20));
        column3.setLayoutX(360);
        column3.setLayoutY(240);
        column4.setPadding(new Insets(20,20,20,20));
        column4.setLayoutX(460);
        column4.setLayoutY(240);
        layoutView.getChildren().addAll(column1,column2,column3,column4,back,label,label1,button1,button2);
        Scene scene=new Scene(layoutView,600,900);
        viewSeats.setScene(scene);
        viewSeats.setResizable(false);
        viewSeats.showAndWait();

    }
    //---------------ADD passenger for the empty seat----------------------------------------------------
    private static String[] addCustomer(String[] seatAdding) {
        Stage window = new Stage();
        window.setTitle("Train seat reservation");
        AnchorPane layout1 = new AnchorPane();
        AnchorPane layout2=new AnchorPane();

        //*****First Scene (to get passenger name)-create children for the first scene and styling******

        Image logo= new Image("file:trainLogo.png");   //****Insert image to the window****
        ImageView viewImage=new ImageView();
        viewImage.setImage(logo);
        Label titleLabel=new Label("     DENUWARA MANIKE TRAIN",viewImage);
        titleLabel.setLayoutX(30);   //****set position of first scene's title****
        titleLabel.setLayoutY(20);
        titleLabel.setStyle("-fx-font-family:Rockwell Extra Bold;-fx-font-size:30");  //****set style first scene's title****
        Label nameLabel = new Label("Enter passenger Name");
        nameLabel.setLayoutX(120);        //****set position of name label****
        nameLabel.setLayoutY(200);
        TextField nameText=new TextField();      //****set position of text field****
        nameText.setLayoutX(320);
        nameText.setLayoutY(200);
        Button submitButton = new Button("Go To View Seats");
        submitButton.setLayoutX(350);  //****set position of submit button****
        submitButton.setLayoutY(300);
        Scene finalScene = new Scene(layout2, 600, 900);
        //******set OnAction to the submit button to switch Second Scene******
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //****Checking input type string or not
                if (!nameText.getText().matches("[a-zA-Z]*")){
                    nameText.setStyle("-fx-background-color:#74D5DD");//***If inputs are mismatching then change text field color
                }
                else if (nameText.getText().length()==0){
                    nameText.setStyle("-fx-background-color:#74D5DD");
                }
                else {
                    window.setScene(finalScene);}
            }
        });
        //*****Second Scene(To view seats )-create children for the  second scene*******

        Label label = new Label("Up view of train seats");
        Label label2=new Label("     DENUWARA MANIKE TRAIN ",viewImage);
        Label label3=new Label();
        Button button1=new Button("Empty seat");
        Button button2 =new Button("Booked seat");
        //***************Create first seat column************************************

        VBox column1 = new VBox(20);
        List<Button>buttonList1=new ArrayList<>();
        for (int c = 1; c < 42; c=c+4) {
            Button seatColumn=new Button("Seat "+c);
            buttonList1.add(seatColumn);
            seatColumn.setStyle("-fx-background-color:#00BFFF");
            seatColumn.setMaxWidth(100);
            column1.getChildren().add(seatColumn);
            //****If the relevant seat do not have  reservation then that button shows blue color otherwise it shows red****
            if (seatAdding[c-1]==null){
                seatColumn.setStyle("-fx-background-color:#00BFFF");
            }
            else {
                seatColumn.setStyle("-fx-background-color:red");
            }
            int finalC=c;
            //****Set OnAction to the the seat button : when button is pressed then window will close****

            seatColumn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    seatColumn.setStyle("-fx-background-color:#00BFFF");
                    window.close();
                    if (seatAdding[finalC - 1] == null) {
                        seatAdding[finalC - 1] =nameText.getText().toLowerCase() ;
                        System.out.println("You have booked seat number " + finalC + "  for " + nameText.getText());
                    } else {
                        seatColumn.setStyle("-fx-background-color:red");
                        System.out.println("This seat already booked try another seat");
                    }
                }
            });
        }
        System.out.println();
        //***************Create second seat column*******************************

        VBox column2 = new VBox(20);
        List<Button> buttonList2=new ArrayList<>();
        for (int c = 2; c < 39; c+=4) {
            Button seatColumn=new Button("Seat "+c);
            buttonList2.add(seatColumn);
            seatColumn.setStyle("-fx-background-color:#00BFFF");
            seatColumn.setMaxWidth(100);
            column2.getChildren().add(seatColumn);
            //****If the relevant seat do not have  reservation then that button shows blue color otherwise it shows red****

            if (seatAdding[c-1]==null){
                seatColumn.setStyle("-fx-background-color:#00BFFF");
            }
            else {
                seatColumn.setStyle("-fx-background-color:red");
            }
            int finalC=c;
            //****Set OnAction to the the seat button : when button is pressed then window will close****

            seatColumn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    window.close();
                    if (seatAdding[finalC - 1] == null) {
                        seatAdding[finalC - 1] = nameText.getText().toLowerCase();
                        System.out.println("You have booked seat number " + finalC + " for " + nameText.getText());
                    } else {

                        seatColumn.setStyle("-fx-background-color:red");
                        System.out.println("This seat already booked try another seat");
                    }
                }
            });
        }
        System.out.println();
        //***************Create third seat column********************************************

        VBox column3 = new VBox(20);
        List<Button> buttonList3=new ArrayList<>();
        for (int c = 3; c < 40; c+=4) {
            Button seatColumn=new Button();
            seatColumn.setText("Seat "+c);
            buttonList3.add(seatColumn);
            seatColumn.setStyle("-fx-background-color:##00BFFF");
            seatColumn.setMaxWidth(100);
            column3.getChildren().add(seatColumn);
            //****If the relevant seat do not have  reservation then that button shows blue color otherwise it shows red****

            if (seatAdding[c-1]==null){
                seatColumn.setStyle("-fx-background-color:#00BFFF");
            }
            else {
                seatColumn.setStyle("-fx-background-color:red");
            }
            int finalC = c;
            //****Set OnAction to the the seat button : when button is pressed then window will close****

            seatColumn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    window.close();
                    if (seatAdding[finalC - 1] == null) {
                        seatAdding[finalC - 1] = nameText.getText().toLowerCase();
                        System.out.println("You have booked seat number " + finalC + " for " + nameText.getText());
                    } else {

                        seatColumn.setStyle("-fx-background-color:red");
                        System.out.println("This seat already booked try another seat");
                    }
                }

            });
        }
        System.out.println();
        //***************Create fourth seat column********************************************

        VBox column4 = new VBox(20);
        List<Button> buttonList4=new ArrayList<>();
        System.out.println(Arrays.deepToString(seatAdding));
        for (int c = 4; c < 43; c+=4) {
            Button seatColumn=new Button();
            seatColumn.setText("Seat "+c);
            seatColumn.setStyle("-fx-background-color:#00BFFF");
            seatColumn.setMaxWidth(100);
            buttonList4.add(seatColumn);
            String seatNo = String.valueOf(c);
            column4.getChildren().add(seatColumn);
            //****If the relevant seat do not have  reservation then that button shows blue color otherwise it shows red****

            if (seatAdding[c-1]==null){
                seatColumn.setStyle("-fx-background-color:#00BFFF");
            }
            else {
                seatColumn.setStyle("-fx-background-color:red");
            }
            int finalC = c;
            //****Set OnAction to the the seat button : when button is pressed then window will close****

            seatColumn.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    window.close();
                    if (seatAdding[finalC - 1] == null) { ;
                        seatAdding[finalC - 1] = nameText.getText().toLowerCase();
                        System.out.println("You have booked seat number " + finalC + " for " + nameText.getText());
                    } else {
                        seatColumn.setStyle("-fx-background-color:red");
                        System.out.println("This seat already booked try another seat");
                    }
                }
            });
        }
        System.out.println();
        //***************Create last seat ************************************************

        Button seat42=new Button("Seat 42");
        column4.getChildren().add(seat42);
        seat42.setStyle("-fx-background-color:#00BFFF");
        seat42.setMaxWidth(100);
        //****If the relevant seat do not have  reservation then that button shows blue color otherwise it shows red****

        if (seatAdding[41]==null){
            seat42.setStyle("-fx-background-color:#00BFFF");
        }
        else {
            seat42.setStyle("-fx-background-color:red");
        }
        seat42.setOnAction(new EventHandler<ActionEvent>() {
            //****Set OnAction to the the seat button : when button is pressed then window will close****

            @Override
            public void handle(ActionEvent event) {
                window.close();
                if (seatAdding[41] == null) {
                    seatAdding[41] = nameText.getText().toLowerCase();
                    System.out.println("You have booked seat number 42 for " + nameText.getText());
                } else {
                    seat42.setStyle("-fx-background-color:red");
                    System.out.println("This seat already booked try another seat");
                }
            }
        });
        //*****************set styles for second scene children**********************************

        label.setStyle("-fx-background-color:blue;-fx-text-fill:white;-fx-padding:10px;-fx-font-family:Arial;-fx-font-size:20");
        label2.setStyle("-fx-font-family:Rockwell Extra Bold;-fx-font-size:30");   //****set style second scene title****

        label.setLayoutX(170);label.setLayoutY(115);  //****set position of second scene label****
        label2.setLayoutX(30);label2.setLayoutY(15);  //****set position of first scene's title****
        button1.setLayoutX(130);button1.setLayoutY(180);  // ****set position of empty seats identifier button****
        button2.setLayoutX(310);button2.setLayoutY(180);  // ****set position of booked seats identifier button****
        button1.setStyle("-fx-background-color:#00BFFF");  //****set background color of empty seats identifier button****
        button2.setStyle("-fx-background-color:red");    //****set background color of booked seats identifier button****
        column1.setPadding(new Insets(20,20,20,20));  //****set first seat column position and padding****
        column1.setLayoutX(40);
        column1.setLayoutY(240);
        column2.setPadding(new Insets(20,20,20,20));  //****set second seat column position and padding****
        column2.setLayoutX(140);
        column2.setLayoutY(240);
        column3.setPadding(new Insets(20,20,20,20));  //****set third seat column position and padding****
        column3.setLayoutX(360);
        column3.setLayoutY(240);
        column4.setPadding(new Insets(20,20,20,20));  //****set fourth seat column position and padding****
        column4.setLayoutX(460);
        column4.setLayoutY(240);
        //************************Add children to the layout************************************

        layout1.getChildren().addAll(nameLabel, submitButton,titleLabel,nameText);
        layout2.getChildren().addAll(label,label2,button1,button2,column1,column2,column3,column4);

        Scene nameScene=new Scene(layout1,700,500);
        window.setScene(nameScene);
        window.setResizable(false);
        window.showAndWait();
        return seatAdding;
    }
    //------------------Create method to view welcome page -----------------------------------
    private static void displayWelcomePage(){
        Stage display=new Stage();           //*****create new stage
        display.setTitle("Welcome Page");
        BorderPane borderPane=new BorderPane();
        borderPane.setPadding(new Insets(20,20,20,20));
        Image logo= new Image("file:trainLogo.png");
        ImageView viewImage=new ImageView();
        viewImage.setImage(logo);
        //********************************create table body****************************************
        Label titleLabel=new Label("     DENUWARA MANIKE TRAIN",viewImage);     // create home page title
        titleLabel.setStyle("-fx-font-family:Rockwell Extra Bold;-fx-font-size:30");    //set style for  title
        borderPane.setTop(titleLabel);
        Button buttonB=new Button("VIEW MENU");
        buttonB.setOnAction(event ->display.close() );
        Label title=new Label("WELCOME TO HOME PAGE");   //create page title
        title.setStyle("-fx-font-family:Cooper Black;-fx-font-size:25");    //set styles to title
        Label  heading1=new Label("  GENERAL INFORMATION  ");     //create heading 1
        heading1.setStyle("-fx-font-family:Segoe UI Black;-fx-font-size:20;-fx-background-color:#A9A9A9");
        Label label1=new Label("Train name");
        Label label1Description=new Label("Denuwara Manike ");
        Label label2=new Label("Train number ");
        Label label2Description1=new Label("1001 ( Colombo  to  Badulla )\n1002 ( Badulla  to  Colombo )");
        Label label3=new Label("Train type");
        Label label3Description=new Label("Intercity Express");
        Label label4=new Label("Frequency");
        Label label4Description=new Label("Daily");
        Label timeTable=new Label("  Train Timetable  ");
        timeTable.setStyle("-fx-font-family:Segoe UI Black;-fx-font-size:22;-fx-background-color:#A9A9A9");
        Label heading2=new Label("Colombo to Badulla");
        heading2.setStyle("-fx-font-family:Arial Black;-fx-font-size:20");
        Label label5=new Label("Train No");
        Label label5Description=new Label("1001");
        Label label6=new Label("Departure from\nColombo");
        Label label6Description=new Label("06:45");
        Label label7=new Label("Reaching\nBadulla");
        Label label7Description=new Label("15:27");
        Label heading3=new Label("Badulla to Colombo");
        heading3.setStyle("-fx-font-family:Arial Black;-fx-font-size:20");
        Label label8=new Label("Train No");
        Label label8Description=new Label("1002");
        Label label9=new Label("Departure from\nBadulla");
        Label label9Description=new Label("07:20");
        Label label10=new Label("Reaching\nColombo");
        Label label10Description=new Label("16:03");

        GridPane gridPane=new GridPane();   //create grid pane
        gridPane.setVgap(15);
        gridPane.setHgap(10);
        //************************set index of table elements***********************************
        gridPane.setPadding(new Insets(10,20,10,20));
        borderPane.setCenter(gridPane);
        gridPane.add(title,0,1);
        gridPane.add(heading1,0,2);
        gridPane.add(label1,0,4);
        gridPane.add(label1Description,1,4);
        gridPane.add(label2,0,5);
        gridPane.add(label2Description1,1,5);
        gridPane.add(label3,0,6);
        gridPane.add(label3Description,1,6);
        gridPane.add(label4,0,7);
        gridPane.add(label4Description,1,7);
        gridPane.add(timeTable,0,9);
        gridPane.add(heading2,0,10);
        gridPane.add(label5,0,11);
        gridPane.add(label5Description,0,12);
        gridPane.add(label6,1,11);
        gridPane.add(label6Description,1,12);
        gridPane.add(label7,2,11);
        gridPane.add(label7Description,2,12);
        gridPane.add(heading3,0,13);
        gridPane.add(label8,0,14);
        gridPane.add(label8Description,0,15);
        gridPane.add(label9,1,14);
        gridPane.add(label9Description,1,15);
        gridPane.add(label10,2,14);
        gridPane.add(label10Description,2,15);
        borderPane.setBottom(buttonB);
        display.setScene(new Scene(borderPane,900,900));
        display.setResizable(false);
        display.showAndWait();
    }
}




