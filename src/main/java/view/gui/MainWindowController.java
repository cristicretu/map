package view.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import model.PrgState;
import model.value.IValue;
import model.statement.IStmt;
import controller.Controller;
import exceptions.StackException;
import utils.IHeap;
import utils.IStack;
import utils.MyDict;
import utils.MyList;
import utils.MyStack;
import utils.ISemaphore;
import javafx.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import model.value.StringValue;
import java.lang.reflect.Field;

public class MainWindowController {
  private Controller controller;
  private PrgState selectedProgram;

  @FXML
  private TextField numberOfPrgStatesTextField;

  @FXML
  private TableView<Map.Entry<Integer, IValue>> heapTableView;
  @FXML
  private TableColumn<Map.Entry<Integer, IValue>, String> heapAddressColumn;
  @FXML
  private TableColumn<Map.Entry<Integer, IValue>, String> heapValueColumn;

  @FXML
  private ListView<String> outputListView;
  @FXML
  private ListView<String> fileTableListView;
  @FXML
  private ListView<Integer> prgStateIdentifiersListView;
  @FXML
  private ListView<String> exeStackListView;

  @FXML
  private TableView<Map.Entry<String, IValue>> symTableView;
  @FXML
  private TableColumn<Map.Entry<String, IValue>, String> symTableVarNameColumn;
  @FXML
  private TableColumn<Map.Entry<String, IValue>, String> symTableValueColumn;

  @FXML
  private Button runOneStepButton;

  @FXML
  private TableView<Map.Entry<Integer, Pair<Integer, List<Integer>>>> semaphoreTableView;
  @FXML
  private TableColumn<Map.Entry<Integer, Pair<Integer, List<Integer>>>, String> semaphoreIndexColumn;
  @FXML
  private TableColumn<Map.Entry<Integer, Pair<Integer, List<Integer>>>, String> semaphoreValueColumn;
  @FXML
  private TableColumn<Map.Entry<Integer, Pair<Integer, List<Integer>>>, String> semaphoreListColumn;

  public void setController(Controller controller) {
    this.controller = controller;
    populateAll();
  }

  @FXML
  public void initialize() {
    heapAddressColumn
        .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey().toString()));
    heapValueColumn
        .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue().toString()));

    symTableVarNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
    symTableValueColumn
        .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue().toString()));

    semaphoreIndexColumn
        .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey().toString()));
    semaphoreValueColumn
        .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue().getKey().toString()));
    semaphoreListColumn
        .setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getValue().getValue().toString()));

    prgStateIdentifiersListView.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
          if (newValue != null) {
            selectedProgram = controller.getRepo().getPrgList().stream()
                .filter(p -> p.getId() == newValue)
                .findFirst()
                .orElse(null);
            populateExeStack();
            populateSymTable();
          }
        });
  }

  private void populateAll() {
    populateHeapTable();
    populateOutput();
    populateFileTable();
    populatePrgStateIdentifiers();
    populateNumberOfPrgStates();
    populateSemaphoreTable();

    if (selectedProgram == null && !controller.getRepo().getPrgList().isEmpty()) {
      selectedProgram = controller.getRepo().getPrgList().get(0);
      prgStateIdentifiersListView.getSelectionModel().select(0);
    }

    if (selectedProgram != null) {
      populateExeStack();
      populateSymTable();
    }
  }

  private void populateNumberOfPrgStates() {
    numberOfPrgStatesTextField.setText(String.valueOf(controller.getRepo().getPrgList().size()));
  }

  private void populateHeapTable() {
    IHeap<Integer, IValue> heap = controller.getRepo().getPrgList().get(0).getHeap();
    ObservableList<Map.Entry<Integer, IValue>> heapEntries = FXCollections.observableArrayList(
        heap.getHeap().entrySet());
    heapTableView.setItems(heapEntries);

    // Add a listener to refresh the heap table view whenever its items change
    this.heapTableView.getItems()
        .addListener((javafx.collections.ListChangeListener.Change<? extends Map.Entry<Integer, IValue>> change) -> {
          while (change.next()) {
            if (change.wasUpdated()) {
              this.heapTableView.refresh();
            }
          }
        });
  }

  private void populateOutput() {
    ObservableList<String> output = FXCollections.observableArrayList();
    if (!controller.getRepo().getPrgList().isEmpty()) {
      MyList<IValue> outList = (MyList<IValue>) controller.getRepo().getPrgList().get(0).getOutput();
      try {
        Field listField = MyList.class.getDeclaredField("list");
        listField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<IValue> list = (List<IValue>) listField.get(outList);
        output.addAll(list.stream()
            .map(Object::toString)
            .collect(Collectors.toList()));
      } catch (NoSuchFieldException | IllegalAccessException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Error accessing output list: " + e.getMessage());
        alert.showAndWait();
      }
    }
    outputListView.setItems(output);
  }

  private void populateFileTable() {
    ObservableList<String> files = FXCollections.observableArrayList();
    if (!controller.getRepo().getPrgList().isEmpty()) {
      MyDict<StringValue, BufferedReader> fileTable = (MyDict<StringValue, BufferedReader>) controller.getRepo()
          .getPrgList().get(0).getFileTable();
      files.addAll(fileTable.getValues().stream()
          .filter(br -> br != null)
          .map(br -> br.toString())
          .collect(Collectors.toList()));
    }
    fileTableListView.setItems(files);
  }

  private void populatePrgStateIdentifiers() {
    ObservableList<Integer> identifiers = FXCollections.observableArrayList();
    identifiers.addAll(controller.getRepo().getPrgList().stream()
        .map(PrgState::getId)
        .collect(Collectors.toList()));
    prgStateIdentifiersListView.setItems(identifiers);
  }

  private void populateExeStack() {
    ObservableList<String> exeStack = FXCollections.observableArrayList();
    if (selectedProgram != null) {
      List<String> stackElements = new ArrayList<>();
      IStack<IStmt> stack = selectedProgram.getExeStack();
      IStack<IStmt> tempStack = new MyStack<>();

      try {
        while (!stack.isEmpty()) {
          IStmt stmt = stack.pop();
          stackElements.add(0, stmt.toString());
          tempStack.push(stmt);
        }

        while (!tempStack.isEmpty()) {
          stack.push(tempStack.pop());
        }
      } catch (StackException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Error accessing stack: " + e.getMessage());
        alert.showAndWait();
      }

      exeStack.addAll(stackElements);
    }
    exeStackListView.setItems(exeStack);
  }

  private void populateSymTable() {
    ObservableList<Map.Entry<String, IValue>> symTableEntries = FXCollections.observableArrayList();
    if (selectedProgram != null) {
      MyDict<String, IValue> symTable = (MyDict<String, IValue>) selectedProgram.getSymTable();
      try {
        Field dictField = MyDict.class.getDeclaredField("dict");
        dictField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, IValue> dict = (Map<String, IValue>) dictField.get(symTable);
        symTableEntries.addAll(dict.entrySet());
      } catch (NoSuchFieldException | IllegalAccessException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Error accessing symbol table: " + e.getMessage());
        alert.showAndWait();
      }
    }
    symTableView.setItems(symTableEntries);

    // Add a listener to refresh the symbol table view whenever its items change
    this.symTableView.getItems()
        .addListener((javafx.collections.ListChangeListener.Change<? extends Map.Entry<String, IValue>> change) -> {
          while (change.next()) {
            if (change.wasUpdated()) {
              this.symTableView.refresh();
            }
          }
        });
  }

  private void populateSemaphoreTable() {
    if (!controller.getRepo().getPrgList().isEmpty()) {
      ISemaphore<Integer, Pair<Integer, List<Integer>>> semaphoreTable = controller.getRepo().getPrgList().get(0)
          .getSemaphoreTable();
      ObservableList<Map.Entry<Integer, Pair<Integer, List<Integer>>>> semaphoreEntries = FXCollections
          .observableArrayList(
              semaphoreTable.getContent().entrySet());
      semaphoreTableView.setItems(semaphoreEntries);

      // Add a listener to refresh the semaphore table view whenever its items change
      this.semaphoreTableView.getItems()
          .addListener((
              javafx.collections.ListChangeListener.Change<? extends Map.Entry<Integer, Pair<Integer, List<Integer>>>> change) -> {
            while (change.next()) {
              if (change.wasUpdated()) {
                this.semaphoreTableView.refresh();
              }
            }
          });
    }
  }

  @FXML
  private void runOneStep() {
    if (controller == null) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error");
      alert.setHeaderText(null);
      alert.setContentText("No program selected!");
      alert.showAndWait();
      return;
    }

    boolean programStateLeft = controller.executeOneStep();
    populateAll();

    // Force refresh the views to show updated values
    this.symTableView.refresh();
    this.heapTableView.refresh();

    if (!programStateLeft) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Program finished");
      alert.setHeaderText(null);
      alert.setContentText("Program execution finished!");
      alert.showAndWait();
    }
  }
}