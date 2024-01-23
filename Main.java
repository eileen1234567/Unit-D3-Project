import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.*;

public class Main extends JFrame{
  private DefaultListModel<String> taskListModel;
  private JList<String> taskList;
  private JTextField taskInput;
  private JButton addButton;
  private JButton removeButton;
  private JLabel dateLabel;
  private JTextField dateInput;
  static ArrayList<String> listItems = new ArrayList<>();

  public Main(){
    setTitle("To-Do List");
    setSize(800, 400);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    taskListModel = new DefaultListModel<>();
    taskList = new JList<>(taskListModel);

    taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    taskInput = new JTextField(12);
    dateLabel = new JLabel("Due Date (mm/dd/yyyy): ");
    dateInput = new JTextField(8);
    addButton = new JButton("Add Task");
    removeButton = new JButton("Remove Task");

    addButton.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e){
        addTask();
      }
  });

    removeButton.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e){
        removeTask();
      }
});

    setLayout(new BorderLayout());

    JPanel inputPanel = new JPanel();
    inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    inputPanel.add(new JLabel("Task: "));
    inputPanel.add(taskInput);
    inputPanel.add(addButton);
    inputPanel.add(removeButton);
    inputPanel.add(dateLabel);
    inputPanel.add(dateInput);


    add(inputPanel, BorderLayout.NORTH);
    add(new JScrollPane(taskList), BorderLayout.CENTER);
    setVisible(true);
  }

  private void addTask(){
    String task = taskInput.getText().trim();
    String date = dateInput.getText().trim();
    if(!task.isEmpty()){
      taskListModel.addElement(task + " (to be completed on " + date + ")");;
      taskInput.setText("");
      dateInput.setText("");
      sortTasksByDate();
    }
  }

  private void sortTasksByDate() { 
    try {
      File file = new File("data.txt");
      if (file.exists()) {
        Scanner scan = new Scanner(file);
        while (scan.hasNextLine()){
          String data = scan.nextLine();
          writeToFile(data);
          listItems.add(data);
        }
    }
      
    List<String> tasks = Collections.list(taskListModel.elements()); 
    tasks.sort(Comparator.comparing(task -> {
      int dateStart = task.lastIndexOf("on ") + 3;
      String dateString = task.substring(dateStart);
      return dateString;
    }));
    taskListModel.clear(); 
    for (String sortedTask : tasks) { 
      taskListModel.addElement(sortedTask);
    }
  } catch (FileNotFoundException e) {
      showError("An error has occurred.");
      e.printStackTrace();
    }
  }

  public static void writeToFile(String data){
    try{
      FileWriter writer = new FileWriter("data.txt", true);
      writer.write(data + "\n");
      writer.close();
    } catch (IOException e) {
      showError("An error has occurred.");
      e.printStackTrace();
    }
  }

  private void removeTask(){
    int selectedIndex = taskList.getSelectedIndex();
    if(selectedIndex != -1){
      taskListModel.remove(selectedIndex);
      try {
        FileWriter writer = new FileWriter("data.txt");
        for (String task : listItems) {
          writer.write(task + "\n");
        }
        writer.close();
        
      } catch (IOException e) {
        showError("An error has occurred.");
        e.printStackTrace();
      }
    }

    else {
      showError("Task not found.");
    }
  }
  
  public static void main(String[] args){
    SwingUtilities.invokeLater(new Runnable(){
      @Override
      public void run(){
        new Main();
      }
    });
  }
  public static void showError(String message){
    System.out.println("Error: " + message);
  }
}