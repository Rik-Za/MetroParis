package it.polito.tdp.metroparis;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.metroparis.model.Fermata;
import it.polito.tdp.metroparis.model.Model;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MetroController {
	
	private Model model;

    @FXML
    private ResourceBundle resources;
    
    @FXML
    private TableColumn<Fermata, String> colFermata; //primo generic uguale a quello della tabella, il secondo è uguale al tipo di dato nella colonna
    @FXML
    private TableView<Fermata> tbPercorso;

    @FXML
    private URL location;

    @FXML
    private Button btnCerca;

    @FXML
    private ComboBox<Fermata> cmbArrivo;

    @FXML
    private ComboBox<Fermata> cmbPartenza;

    @FXML
    private TextArea txtResult;

    @FXML
    void doCerca(ActionEvent event) {
    	Fermata partenza = cmbPartenza.getValue();
    	Fermata arrivo = cmbArrivo.getValue();
    	if(partenza!=null && arrivo!=null && !partenza.equals(arrivo)) {
    		List<Fermata> percorso = model.calcolaPercorso(partenza, arrivo);
    		tbPercorso.setItems(FXCollections.observableArrayList(percorso));
    		txtResult.setText("Percorso trovato con "+percorso.size()+" stazioni");
    	}else {
    		txtResult.setText("Devi selezionare due stazioni diverse tra loro\n");
    	}

    }

    @FXML
    void initialize() {
        assert btnCerca != null : "fx:id=\"btnCerca\" was not injected: check your FXML file 'Metro.fxml'.";
        assert cmbArrivo != null : "fx:id=\"cmbArrivo\" was not injected: check your FXML file 'Metro.fxml'.";
        assert cmbPartenza != null : "fx:id=\"cmbPartenza\" was not injected: check your FXML file 'Metro.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Metro.fxml'.";
        
        colFermata.setCellValueFactory(new PropertyValueFactory<Fermata,String>("nome")); //attributo dell'oggetto Fermata da visualizzare

    }

	public void setModel(Model m) {
		this.model=m;
		List<Fermata> fermate = this.model.getFermate();
		cmbPartenza.getItems().addAll(fermate);
		cmbArrivo.getItems().addAll(fermate);
		
	}

}
