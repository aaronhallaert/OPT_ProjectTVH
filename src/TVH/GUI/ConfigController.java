package TVH.GUI;

import TVH.Config;
import TVH.Problem;
import TVH.Solution;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class ConfigController {

    @FXML
    TextField temperature;

    @FXML
    TextField trucks;

    @FXML
    TextField machinetypes;

    @FXML
    TextField jobs;


    // Jeroen of Aaron
    @FXML
    TextField type;

    @FXML
    TextField timefactor;

    @FXML
    TextField orderfactor;

    @FXML
    TextField frviolationsfactor;

    @FXML
    TextField distancefactor;

    @FXML
    TextField time;

    @FXML
    Spinner<String> problem;

    @FXML
    Button run;

    @FXML
    TextField file;

    public void initialize(){
        List<String> options= new ArrayList<>();
        options.add("tvh_problem_4");
        options.add("tvh_problem_3");
        problem.setValueFactory(new SpinnerValueFactory.ListSpinnerValueFactory<String>(FXCollections.observableArrayList(options)));

    }

    @FXML
    public void setConfig(){
        // set temperature
        int temp= Integer.parseInt(temperature.getText());

        // set trucks
        int numberofTrucks= Integer.parseInt(trucks.getText());

        // set mt
        int mt= Integer.parseInt(machinetypes.getText());

        // set jobs
        int jbs= Integer.parseInt(jobs.getText());

        // set type
        String annealingType= type.getText();

        // set timefactor
        int tFactor= Integer.parseInt(timefactor.getText());

        // set orderfactor
        int oFactor= Integer.parseInt(orderfactor.getText());

        // set frviolationsfactor
        int fFactor= Integer.parseInt(frviolationsfactor.getText());

        // set distancefactor
        int dFactor= Integer.parseInt(distancefactor.getText());

        // set duration
        int hours= Integer.parseInt(time.getText().split(":")[0]);
        int minutes= Integer.parseInt(time.getText().split(":")[1]);
        int seconds= Integer.parseInt(time.getText().split(":")[2]);

        int totalSeconds= hours*360+minutes*60+seconds;
        System.out.println("duur"+totalSeconds);

        // set problem
        String problemString=problem.getValue();
        System.out.println("problem : "+ problemString);

        Config.getInstance().update(temp, numberofTrucks, mt, jbs, annealingType, tFactor, oFactor, fFactor, dFactor, totalSeconds, problemString);

    }

    public void runProgram(){
        setConfig();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String INPUT_FILE = Config.getInstance().getProblem()+".txt";
                String OUTPUT_FILE = Config.getInstance().getProblem()+"_out.txt";
                long BEGIN_TIME = System.currentTimeMillis();

                System.out.println(INPUT_FILE);
                File inputFile = new File(INPUT_FILE);
                long startTime = System.currentTimeMillis();

                try{
                    Problem problem = Problem.newInstance(inputFile);
                    Solution solution = problem.solve();
                    solution.writeToFile(OUTPUT_FILE);
                    System.out.println("Calculation time: "+(System.currentTimeMillis()-startTime)+"ms");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void loadConfig(){
        System.out.println("laden van file "+file+ " in GUI");
        String filestring= file.getText();
        File fileConfig = new File(filestring+".txt");

        Scanner sc = null;
        try {
            sc = new Scanner(fileConfig).useLocale(Locale.US);
            String [] eigenschappen=sc.nextLine().split(", ");

            temperature.setText(eigenschappen[0]);
            trucks.setText(eigenschappen[1]);
            machinetypes.setText(eigenschappen[2]);
            jobs.setText(eigenschappen[3]);
            type.setText(eigenschappen[4]);
            timefactor.setText(eigenschappen[5]);
            orderfactor.setText(eigenschappen[6]);
            frviolationsfactor.setText(eigenschappen[7]);
            distancefactor.setText(eigenschappen[8]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }



    }

    public void saveConfig(){
        setConfig();
        Config.getInstance().writeToFile(file.getText()+".txt");
    }

}
