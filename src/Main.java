import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;

/**
 * Main class that coordinates all of the other components
 */
public class Main extends Application {

    // Private variables used by the application
    private String fileSelectionStr = "sample";
    private File fileSelection;
    public static final int WIDTH = 700;
    public static final int HEIGHT = 500;
    private GraphDisplay graphDisplay;
    private Stage window;
    private String buttonStyle =  "    -fx-text-fill: #101024;\n" +
            "    -fx-background-color: #EEEEEE;\n" +
            "    -fx-border-color: #111111;" +
            "    -fx-padding: 8;";
    private Text selectedFileText;

    /**
     * Called by the Application for initialization
     *
     * @param primaryStage Stage of the GUI
     * @throws Exception JavaFX specific
     */
    @Override
    public void start(Stage primaryStage) {

        // Create the window from the 
        window = primaryStage;

        // Ends all threads and closes window
        primaryStage.setOnCloseRequest(e -> {
            System.exit(0);
            primaryStage.close();
        });

        // Set the title and set the scene
        primaryStage.setTitle("Mobile Agents");
        primaryStage.setScene(introScene());
        primaryStage.show();
    }

    /**
     * Initialize the Graph with the given file
     *
     * @param file File of the emulation configuration file
     * @return A Graph class
     */
    private Graph initGraph(File file){
        // Initialize gragh data structure
        Graph graph = new Graph(file);

        // Display graph
        graphDisplay = new GraphDisplay(graph);

        // Opted for getStation so that it can be used by the initial baseStation
        new MobileAgent(graph.getStation(), graphDisplay,  true);

        // Start simulation
        graph.startThreads();

        return graph;
    }

    /**
     * Introduces the scence
     *
     * @return Returns the newly created scene
     */
    private Scene introScene(){

        // Root to this scene
        GridPane introRoot = new GridPane();

        // Set Title
        Text title = new Text("Mobile Agents");
        title.setFont(Font.font(70));
        title.setStyle("-fx-font: 24 arial;");

        // Button that confirms the selection and starts the simulation
        Button startButton = new Button("Start");
        startButton.setStyle(buttonStyle);
        startButton.addEventHandler(MouseEvent.MOUSE_CLICKED, handleConfirm());

        // Pane containing all the graph options
        ScrollPane graphselectionPane = graphSelectionScrollPane();

        // Opens a dialog giving infor mationa about the project
        Button infoBoxButton = new Button("Info");
        infoBoxButton.setStyle(buttonStyle);

        // On action lambda expression of the info button!
        infoBoxButton.setOnAction(event -> {
            // Alerts the user in a modal box
            Alert alert = new Alert(Alert.AlertType.WARNING);

            // Set the GUI specific information
            alert.setGraphic(null);
            alert.setTitle("");
            alert.setHeaderText("What is Mobile Agents?");
            alert.setContentText("It is a simulation of a communication network as it launches concurrent programs " +
                    "to contain a 'forest fire' destroying the nodes on the network.");

            // Close when done
            alert.showAndWait();
            alert.close();
        });

        // Tells what file is selected byt he selector
        selectedFileText = new Text(fileSelectionStr);

        // Button that opens a file selector
        Button openButton = new Button("Open");
        openButton.setStyle(buttonStyle);

        // Set the on Action lambda for choosing a file
        openButton.setOnAction(event -> {

            // Opens the File Chooser dialogue
            FileChooser fileChooser = new FileChooser();
            try {
                fileSelection = fileChooser.showOpenDialog(window);
            } catch (NullPointerException e){return;}

            // Sets the file
            selectedFileText.setText(fileSelectionStr.substring(0, fileSelectionStr.length() - 4));
        });

        //Add everything to the scene root
        introRoot.setAlignment(Pos.CENTER);
        introRoot.setHgap(25);
        introRoot.setVgap(15);
        introRoot.add(title, 1,1);
        introRoot.add(graphselectionPane, 1, 2);
        introRoot.add(openButton, 1, 3);
        introRoot.add(selectedFileText,1,4);
        introRoot.add(startButton, 2,2);
        introRoot.add(infoBoxButton, 3,2);

        // Return the scene
        return new Scene(introRoot, WIDTH, HEIGHT);
    }

    /**
     * Creates the graphSelectionScrollPane instead of the filechooser
     *
     * @return ScrollPane of options
     */
    private ScrollPane graphSelectionScrollPane() {

        ScrollPane scrollPane = new ScrollPane();

        //Selectable list of options
        ListView<String> listView = new ListView<String>();
        listView.addEventHandler(MouseEvent.MOUSE_CLICKED, handleListClick(listView));

        //Read in file names into list for listview
        File folder = new File("resources");
        ObservableList<String> items = FXCollections.observableArrayList (Arrays.asList(folder.list()));

        listView.setItems(items);

        //Prepare scroll pane
        scrollPane.setContent(listView);
        scrollPane.setMaxHeight(300);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    /**
     * Handles starting the simulation making sure a file is selected and changing the scene.
     * @return Event handler object
     */
    private EventHandler<MouseEvent> handleConfirm() {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                //If a file is selected, load up Graph and start the simulation
                if (fileSelection != null){
                    initGraph(fileSelection);
                    window.setScene(new Scene(graphDisplay.getRoot(),WIDTH, HEIGHT));
                } else {
                    selectedFileText.setText("Please Choose File");
                }
            }
        };
    }

    /**
     * If the list item is clicked, it prepares its associated file.
     * @param listView List View of things that are clickable
     * @return Event handler object
     */
    private EventHandler<MouseEvent> handleListClick(ListView<String> listView) {
        return event -> {
            fileSelectionStr = listView.getSelectionModel().getSelectedItem();

            fileSelection = new File("resources/" + fileSelectionStr);
            selectedFileText.setText(fileSelectionStr.substring(0, fileSelectionStr.length() - 4));

        };
    }

    /**
     * Call the function required for the GUI
     *
     * @param args Potential CLA, (Not used in this application)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
