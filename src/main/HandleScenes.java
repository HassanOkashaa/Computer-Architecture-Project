package main;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class HandleScenes extends Scene {

	private MainView view;
	private Architecture a;

	public HandleScenes(Parent root, double width, double height, MainView view, Architecture a) {
		super(root, width, height);
		this.view = view;
		this.a=a;
	}

	public VBox home() {
		VBox root = new VBox();
		VBox pip = pip();
		HBox finalp = finalp();
		root.getChildren().add(pip);
		root.getChildren().add(finalp);
		return root;
	}
	public VBox pip() {
		TextArea output = new TextArea();
		output.setEditable(false);
		output.setMinSize(800, 500);
		a.outputText=output;
		
		try {
			a.pipeline();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		VBox root = new VBox(output);
		return root;
	}
	public HBox finalp() {
		HBox root = new HBox();
		VBox regFile = regFile();
		VBox insMem = insMem();
		VBox dataMem = dataMem();
		root.getChildren().add(regFile);
		root.getChildren().add(insMem);
		root.getChildren().add(dataMem);
		return root;
	}
	public VBox regFile() {
		TextArea output = new TextArea();
		output.setEditable(false);
		output.setMinSize(510, 500);
		a.outputTextReg=output;
		
		try {
			a.reg();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		VBox root = new VBox(output);
		return root;
	}
	public VBox insMem() {
		TextArea output = new TextArea();
		output.setEditable(false);
		output.setMinSize(510, 500);
		a.outputTextIns=output;
		
		try {
			a.ins();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		VBox root = new VBox(output);
		return root;
	}
	public VBox dataMem() {
		TextArea output = new TextArea();
		output.setEditable(false);
		output.setMinSize(510, 500);
		a.outputTextData=output;
		
		try {
			a.data();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		VBox root = new VBox(output);
		return root;
	}

}
