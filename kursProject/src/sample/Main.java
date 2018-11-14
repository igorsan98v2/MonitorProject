package sample;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.collections.ListChangeListener;

import javafx.concurrent.Task;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.*;



public class Main extends Application {
    private TCPServer server;

    private PipedOutputStream output =  new PipedOutputStream(); ;
    private PipedInputStream input ;
    private ObservableList<XYChart.Data<String, Integer>> sysList = FXCollections.observableArrayList();
    private ObservableList<XYChart.Data<String, Integer>> diaList = FXCollections.observableArrayList();
    private ObservableList<XYChart.Data<String, Integer>> hrList = FXCollections.observableArrayList();

    private ObservableList<String> myXaxisCategories = FXCollections.observableArrayList();
    private SQLDriver driver ;
    int i;
    private Task<Date> task;
    private LineChart<String,Number> lineChart;
    private XYChart.Series sysSeries;
    private XYChart.Series diaSeries;
    private XYChart.Series hrSeries;
    private CategoryAxis xAxis;
    private NumberAxis xSDH ;
    private int lastObservedSize;
    private ArrayList<Integer> bufferGlobal = new ArrayList(0);
    private Patient patient;
    private Parent root;
    private int patientsHendlers;
    public static int monitorID ;
    public int []SDHfiltred;
    private void updateInfo(){

        server = new TCPServer(output);

    }
    private void updateChart(){
        System.out.println("chart thread Go");
        while (true) {

            String msg = "";
            System.out.println(input.toString());

            try {

                int data = input.read();
                while (data != -1) {
                    System.out.print((char) data);
                    bufferGlobal.add(data);
                    data = input.read();
                }


            } catch (Exception e) {

                e.printStackTrace();
            }

        }
    }
    public  void log(String str){
        System.out.println(str);
    }
    @Override
    public void start(Stage stage) throws Exception{
        monitorID =1;
        patientsHendlers=0;

        input = new PipedInputStream(output);
        Thread serv = new Thread(this::updateInfo);
        Thread chart = new Thread(this::updateChart);

        driver = new SQLDriver();

        System.out.println(driver.getUsersCount());

        serv.start();
        chart.start();
        sysList.addListener((ListChangeListener<XYChart.Data<String, Integer>>) change -> {
            if (change.getList().size() - lastObservedSize > 15) {
                lastObservedSize += 15;
                xAxis.getCategories().remove(0, 15);
            }
        });

        stage.setTitle("Курсовой проект");
        xAxis = new CategoryAxis();
        xAxis.setLabel("Month");

        final NumberAxis yAxis = new NumberAxis();
        lineChart = new LineChart<>(xAxis,yAxis);

        lineChart.setTitle("Cнятые показатели");
        lineChart.setAnimated(false);

        task = new Task<Date>() {

            @Override
            protected Date call() throws Exception {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException iex) {
                        Thread.currentThread().interrupt();
                    }

                    if (isCancelled()) {
                        break;
                    }

                    updateValue(new Date());
                }
                return new Date();
            }
        };
         root = FXMLLoader.load(getClass().getResource("monitor.fxml"));

        task.valueProperty().addListener(new ChangeListener<Date>() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd HH:mm");


            @Override
            public void changed(ObservableValue<? extends Date> observableValue, Date oldDate, Date newDate) {
                String msg ="";
                if(bufferGlobal.size()>0){
                        System.out.println("try to get GSON");
                    for (int symbol : bufferGlobal) {
                        msg += (char) symbol;
                    }
                    System.out.println(msg);
                    System.out.println();
                    System.out.println("End try");
                    JSONObject jsonObject = new JSONObject(msg);
                    bufferGlobal = new ArrayList<Integer>(0);




                    patient = new Patient(jsonObject);

                    driver.addLogForPatient(patient);
                    if(patient.getId()==monitorID) {
                        String date =  patient.getDateAndTime().format(formatter);

                        myXaxisCategories.add(date);//date

                        sysList.add(new XYChart.Data(date, patient.getpSYS()));
                        diaList.add(new XYChart.Data(date, patient.getpDIA()));
                        hrList.add(new XYChart.Data(date, patient.getHR()));
                    }


                    if (driver.checkPatient(patient.getId())) {
                        RegistrationForm registrationForm = new RegistrationForm(patient.getId());
                        setEvents();
                    }

                }

            }
        });
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(task);

        lineChart.setPrefSize(600,600);
        Scene scene  = new Scene(root,1000,600);

        AnchorPane pane=(AnchorPane)root.lookup("#liveChartContainer");
        if(pane!=null) {
            pane.getChildren().add(lineChart);
        }
        xAxis.setCategories(myXaxisCategories);

        sysSeries = new XYChart.Series(sysList);
        diaSeries = new XYChart.Series(diaList);
        hrSeries = new XYChart.Series(hrList);

        hrSeries.setName("HR");
        diaSeries.setName("DIA");
        sysSeries.setName("SYS");

        lineChart.setId("usualChart");

        lineChart.getData().addAll(sysSeries, diaSeries,hrSeries);
        i = 0;
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(windowEvent -> {
            task.cancel();
        });
        setEvents();
        updateMediansChart();
    }
    public void setEvents(){
        Accordion accordion = (Accordion)root.lookup("#accordion");
        ObservableList<TitledPane> paneArr = accordion.getPanes();

        i=0;
        for (TitledPane pane:paneArr) {

                final int id = ++i;
                final Label hr = (Label) pane.lookup("#avgHR" + id);
                final Label pressure = (Label) pane.lookup("#avgP" + id);
                final Label status = (Label)pane.lookup("#status"+id);
                final ImageView imageView= (ImageView)pane.lookup("#img"+id);
                pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        Main.monitorID = id;
                        System.out.println("Clicked accardion : " + id);

                        hr.setText("Срeднeсуточный пульс: " + driver.getAvgHR(id) + "уд/мин");
                        ClassDetector detector = new ClassDetector(SDHfiltred);
                        String []anserws = detector.detectClass();
                        status.setText("Cтатус:"+anserws[0]);
                        Image image = new Image(anserws[1]);
                        imageView.setImage(image);
                        int avgPressure[] = driver.getAvgPressure(id);

                        if (avgPressure != null) {
                            pressure.setText("Срeднeсуточное давлениe: " + avgPressure[0] + "/" + avgPressure[1] + "мм.рт. ст");
                        }
                        int count = driver.getPatientLogCount(id);
                        System.out.println("count of log is" + count);
                        if (count > 0) {
                            ArrayList<Patient> patientLogs = driver.getLogsById(id);
                            try {
                                xAxis.getCategories().clear();
                                xAxis.getCategories().removeAll();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd HH:mm");
                            lineChart.getData().removeAll();
                            lineChart.getData().clear();
                            sysList.clear();
                            diaList.clear();
                            hrList.clear();
                            sysList.removeAll();
                            diaList.removeAll();
                            hrList.removeAll();

                            sysList = FXCollections.observableArrayList();
                            diaList = FXCollections.observableArrayList();
                            hrList = FXCollections.observableArrayList();
                            for (Patient patient : patientLogs) {
                                String date = patient.getDateAndTime().format(formatter);
                                myXaxisCategories.add(date);//date
                                sysList.add(new XYChart.Data(date, patient.getpSYS()));
                                diaList.add(new XYChart.Data(date, patient.getpDIA()));
                                hrList.add(new XYChart.Data(date, patient.getHR()));
                            }

                            sysSeries = new XYChart.Series(sysList);
                            diaSeries = new XYChart.Series(diaList);
                            hrSeries = new XYChart.Series(hrList);
                            hrSeries.setName("HR");
                            diaSeries.setName("DIA");
                            sysSeries.setName("SYS");

                            lineChart.getData().addAll(sysSeries, diaSeries, hrSeries);
                            updateMediansChart();
                        }
                    }
                });


        }
    }
    public void updateMediansChart(){
        LineChart <Integer,Integer> medianChart = (LineChart)root.lookup("#medianChart");


        xSDH = new NumberAxis();
        xSDH.setLabel("Измерения");
        final NumberAxis yAxisSDH = new NumberAxis();

        medianChart.getData().removeAll();
        medianChart.getData().clear();
        XYChart.Series sdhSeries;
        XYChart.Series sdhMedianSeries;
        ObservableList<Integer> myXaxisCategories = FXCollections.observableArrayList();

        ObservableList<XYChart.Data<Integer, Integer>> sdhList = FXCollections.observableArrayList();
        ObservableList<XYChart.Data<Integer, Integer>> sdhMedianList = FXCollections.observableArrayList();
        int mediansPoint =3;
        ArrayList<Patient> patientLogs = driver.getLogsById(monitorID);
        MedianBuilder medianBuilder = new MedianBuilder(patientLogs);
         medianBuilder.calcMedians(mediansPoint);
         ArrayList<Integer>SDH = medianBuilder.getSDH();
         int i=0;
        for(Integer log:SDH){
            myXaxisCategories.add(i);//date
            sdhList.add(new XYChart.Data(i+"", log));
            i++;
        }

        int medians[]=medianBuilder.calcMedians(3);
        SDHfiltred=medians;
        i=0;
        for(int median:medians){
            sdhMedianList.add(new XYChart.Data(i+3+"",median));
            i++;
        }

       // medianChart = new LineChart(xSDH,yAxisSDH);
        sdhSeries = new XYChart.Series(sdhList);
        sdhMedianSeries = new XYChart.Series(sdhMedianList);
        sdhSeries.setName("SDH");
        sdhMedianSeries.setName("SDH после медианного сглаживания");

        medianChart.getData().addAll(sdhSeries, sdhMedianSeries);
    }
    public static void main(String[] args) throws  InterruptedException,IOException {
        launch(args);

    }

}
