package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GUIController  implements Initializable {	
	ARMSim arm;
	
	@FXML
	private Button btnStop, btnRun, btnReload, btnStepInto;
	
	@FXML
	private TextField fileName;
	
	@FXML
	public TextArea txtRegister, txtStack, txtOutputView, txtMemView, consoleOutput, consoleInput;
	
	@FXML
	public void run(){
		try{
			arm.simulate();
		}catch(SwiExitException e){
	    	btnRun.setDisable(true);
	    	btnStop.setDisable(true);
	    	btnStepInto.setDisable(true);			
		}
		
		arm.output.close();
		txtOutputView.setText(getOutput("output.txt"));
		txtRegister.setText(arm.getRegisterStatus());
		txtStack.setText(arm.getMemoryStatus());
	}
	
	@FXML
	public void stepInto(){
		try{
			arm.nextStep();
		}catch(SwiExitException e){
	    	btnRun.setDisable(true);
	    	btnStop.setDisable(true);
	    	btnStepInto.setDisable(true);
		}
		arm.output.close();
		String outputs = getOutput("output.txt");
		outputs+="\n\n";
		txtOutputView.setText(outputs);
		try {
			arm.output = new PrintWriter(new BufferedWriter(new FileWriter("output.txt")));
		} catch (IOException e) {
		}
		arm.output.print(outputs);
		txtRegister.setText(arm.getRegisterStatus());
		txtStack.setText(arm.getMemoryStatus());
	}
	
	@FXML
	public void reload(){
    	btnRun.setDisable(false);
    	btnStop.setDisable(false);
    	btnStepInto.setDisable(false);
		arm.init();
		String file = fileName.getText();
		if(file.equals("")){
			file = "input.mem";
		}
		arm.loadMem(file);
		try {
			arm.output = new PrintWriter(new BufferedWriter(new FileWriter("output.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		txtOutputView.clear();
		txtMemView.setText(getMem(file));
		txtRegister.setText(arm.getRegisterStatus());
		txtStack.setText(arm.getMemoryStatus());
	}
	
	@FXML
	public void stop(){
		try{
			arm.swi_exit();
		}catch(SwiExitException e){
	    	btnRun.setDisable(true);
	    	btnStop.setDisable(true);
	    	btnStepInto.setDisable(true);			
		}
	}
    @Override
    public void initialize(URL location, ResourceBundle resources) {   
    	arm = new ARMSim();
    	arm.runOnGUI = true;
    	btnRun.setDisable(true);
    	btnStop.setDisable(true);
    	btnStepInto.setDisable(true);
    	txtRegister.setEditable(false);
    	txtStack.setEditable(false);
    	//txtMemView.setEditable(false);
    	txtOutputView.setEditable(false);
		txtStack.setScrollTop(Double.MAX_VALUE);
    }
    

	
	public String getMem(String filename){
		String InsMem = "";
		Scanner in =null;
		try
		{
			in= new Scanner(new BufferedReader(new FileReader(filename)));
			while(in.hasNext()){
				InsMem+=in.nextLine()+"\n";
			}
			return InsMem;
		}
		catch(IOException e)
		{
			// write error opening file
			e.printStackTrace();
			
		}
		finally
		{
			if(in!=null)
				in.close();			

			return InsMem;
		}
	}
	
	public String getOutput(String filename){
		String InsMem = "";
		Scanner in =null;
		try
		{
			in= new Scanner(new BufferedReader(new FileReader(filename)));
			while(in.hasNext()){
				InsMem+=in.nextLine()+"\n";
			}
			return InsMem;
		}
		catch(IOException e)
		{
			// write error opening file
			e.printStackTrace();
			
		}
		finally
		{
			if(in!=null)
				in.close();		

			return InsMem;
		}
	}
	 
}