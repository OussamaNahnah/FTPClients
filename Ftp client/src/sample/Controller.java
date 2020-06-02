package sample;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;


import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller implements Initializable {
    static Stage stage;
    public FTPClient FTP;
    boolean isConnect = false;
    ArrayList<FILES> FILESS;
    Image type;
    String path = "";
    FILES selected_file = null;
    @FXML
    private AnchorPane main_anchor;

    @FXML
    private TextField ip_address;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button connect;

    @FXML
    private Label detaille;


    @FXML
    private Button upload;

    @FXML
    private Button download;

    @FXML
    private Button delete;

    @FXML
    private Button rename;

    @FXML
    private Button add_folder;

    @FXML
    private Button refreche;

    @FXML
    private Label status;

    @FXML
    private Label log;
    @FXML
    private Button disconnect;
    @FXML
    private TableView<FILES> table;
    @FXML
    private TableColumn<FILES, ImageView> col_type;
    @FXML
    private TableColumn<FILES, String> col_name;

    @FXML
    private TableColumn<FILES, String> col_size;

    @FXML
    private Button path_back;

    @FXML
    private Button path_set;

    @FXML
    private TextField path_edit;
    @FXML
    private ProgressBar progress;
    @FXML
    private AnchorPane rename_layout;

    @FXML
    private Button rename_apply;

    @FXML
    private Button rename_annuler;
    @FXML
    private AnchorPane add_layout;

    @FXML
    private Button add_apply;

    @FXML
    private Button add_annuler;

    @FXML
    private TextField add_text;
    @FXML
    private TextField rename_text;
    @FXML
    private AnchorPane delete_layout;

    @FXML
    private Button delete_apply;

    @FXML
    private Button delete_annuler;

    @FXML
    private Label delete_text;


    @FXML
    private Label status_reply;

    @FXML
    private Button about;

    public Controller() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        optionButtonDisable(true);
        FILESS = new ArrayList<>();
        connect.setOnAction(this::initConnect);
        disconnect.setOnAction(this::Disconnect);
        path_set.setOnAction(this::path_set);
        path_back.setOnAction(this::path_back);
        upload.setOnAction(this::upload);
        rename.setOnAction(this::rename);
        add_folder.setOnAction(this::add_folder);
        about.setOnAction(this::about);
        disconnect.setDisable(true);

        FTP = new FTPClient();


        //tatna7a man ba3d
        //ip_address.setText("127.0.0.1");
        //username.setText("ftpuser1");
       // password.setText("ftp");
        rename_annuler.setOnMouseClicked(mouseEvent->{
            rename_layout.setVisible(false);
        });
        add_annuler.setOnMouseClicked(mouseEvent->{
            add_layout.setVisible(false);
        });
        add_apply.setOnMouseClicked(mouseEvent->{
            if(!add_text.getText().trim().isEmpty()) {
                try {

                    FTP.makeDirectory(add_text.getText());
                    set_Status();
                    add_text.setText("");
                    downloadFile1("");
                    add_layout.setVisible(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else log.setText("write your text first!");
        });
        delete.setOnMouseClicked(mouseEvent -> {
            close_other(delete);
            System.out.println("delete");
            if (selected_file!=null) {
                delete_text.setText("do u want delete : " + selected_file.getName() + " ?");
                delete_layout.setVisible(true);
            }else log.setText("select file first");
        });
        delete_annuler.setOnMouseClicked(mouseEvent -> {
            delete_layout.setVisible(false);
        });
        delete_apply.setOnMouseClicked(mouseEvent -> {
            try {
                if (selected_file.isDirectory())   {
                    FTP.removeDirectory(selected_file.getName());
                    set_Status();
                }else {
                    FTP.deleteFile(selected_file.getName());
                    set_Status();
                }    downloadFile1("");
                delete_layout.setVisible(false);
            } catch (IOException e) {
                e.printStackTrace();

            }
        });

        refreche.setOnMouseClicked(mouseEvent -> {
            downloadFile1("");
            System.out.println("refreche");
        });
        download.setOnMouseClicked(mouseEvent -> {

            System.out.println("download");
        try {

  /*
                // APPROACH #1: using retrieveFile(String, OutputStream)
                String remote = "b2.png";
                File download = new File("/home/oussamanh/Desktop/bb.png");
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(download));
                boolean success = FTP.retrieveFile(remote, outputStream);
                outputStream.close();

                if (success) {
                    System.out.println("File #1 has been downloaded successfully.");
                }
*/
                // APPROACH #2: using InputStream retrieveFileStream(String)

if(selected_file!=null){
progress.setVisible(true);
    FileChooser f=new FileChooser();

    f.setInitialFileName(selected_file.getName());
    File dest = f.showSaveDialog(stage);
    if (!dest.isDirectory()){

    System.out.println(dest.toPath());
                String remoteFile = selected_file.getName();
               // File downloadFile2 = new File("/home/oussamanh/Desktop/bb.mp4");
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(dest));
                InputStream inputStream = FTP.retrieveFileStream(remoteFile);
        set_Status();
        byte[] bytesArray = new byte[1024];
        int bytesRead;
        long allBytesRead = 0;
        while ((bytesRead = inputStream.read(bytesArray)) != -1) {
            outputStream.write(bytesArray, 0, bytesRead);
            allBytesRead += bytesRead;
            progress.setProgress(allBytesRead/(double)dest.length());
        }

                boolean success = FTP.completePendingCommand();
        set_Status();
                if (success) {
                    System.out.println("File  has been downloaded successfully.");
                }
                outputStream.close();
                inputStream.close();
        progress.setVisible(false);

}  }else log.setText("choose a file  please!");  } catch (IOException ex) {
                System.out.println("Error: " + ex.getMessage());
                ex.printStackTrace();
            }
            progress.setVisible(false);
        });


        rename_apply.setOnMouseClicked(mouseEvent->{
            if(selected_file!=null){
               if(!rename_text.getText().trim().isEmpty()){
                   if (!rename_text.equals(selected_file.getName())){
                   try {
                       set_Status();
                       FTP.rename(selected_file.getName(),rename_text.getText());
                       set_Status();
                       downloadFile1("");
                       rename_layout.setVisible(false);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }}else log.setText("you didn't change the name!");
               }else log.setText("write your text first!");
            }
        });/*
        rename.setOnMouseClicked(mouseEvent -> {
            System.out.println("rename");
            if (selected_file!=null) {
                rename_text.setText(selected_file.getName());
                rename_layout.setVisible(true);
            }else log.setText("select file first");
        });
        add_folder.setOnMouseClicked(mouseEvent -> {
            System.out.println("add");
            add_layout.setVisible(true);
        });*/

    }

    private void about(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("about.fxml"));
            /*
             * if "fx:controller" is not set in fxml
             * fxmlLoader.setController(NewWindowController);
             */
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage stage = new Stage();
            stage.setTitle("New Window");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }

    private void add_folder(ActionEvent actionEvent) {
close_other(add_folder);
            System.out.println("add");
            add_layout.setVisible(true);

    }

    private void rename(ActionEvent actionEvent) {
        close_other(rename);
        System.out.println("rename");
        if (selected_file!=null) {
            rename_text.setText(selected_file.getName());
            rename_layout.setVisible(true);
        }else log.setText("select file first");
    }


    private void upload(ActionEvent actionEvent) {
        progress.setVisible(true);
        System.out.println("upload");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select file ...");
        File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {

            ;

                try {


           /*           // APPROACH #1: uploads first file using an InputStream
                    File firstLocalFile = new File("/home/oussamanh/v.mp4");

                    String firstRemoteFile = "vv.mp4";
                  InputStream inputStream = new FileInputStream(firstLocalFile);

                    System.out.println("Start uploading first file");
                    boolean done = FTP.storeFile(firstRemoteFile, inputStream);
                    inputStream.close();
                    if (done) {
                        System.out.println("The first file is uploaded successfully.");
                    }

                    // APPROACH #2: uploads second file using an OutputStream

                   // File secondLocalFile = new File("/home/oussamanh/GravitDesigner.zip");
                   // String secondRemoteFile = "GravitDesignerr.zip";*/
                    InputStream   inputStream = new FileInputStream(selectedFile);

                    System.out.println("Start uploading second file");

                    OutputStream outputStream = FTP.storeFileStream(selectedFile.getName());
                    set_Status();
                   /* byte[] bytesIn = new byte[1024];
                    int read = 0;
                    double allBytesRead = 0;
                    progress.setProgress(0);
                    while ((read = inputStream.read(bytesIn)) != -1) {
                        outputStream.write(bytesIn, 0, read);
                        allBytesRead+=read;
                        System.out.println("leng="+selectedFile.length()+"red="+allBytesRead);
                        double per= (double)(allBytesRead/(double)selectedFile.length());

                        DecimalFormat df = new DecimalFormat("#.##");
                        System.out.println(df.format(per) );
                        log.setText(df.format(per));
                        progress.setProgress(Double.parseDouble(df.format(per)));
                    }*/
                    byte[] bytesIn = new byte[1024];
                    int read;
                    long allBytesRead = 0;
                    while ((read = inputStream.read(bytesIn)) != -1) {
                        outputStream.write(bytesIn, 0, read);
                        allBytesRead += read;
                        progress.setProgress(allBytesRead/(double)selectedFile.length());
                    }
                    inputStream.close();
                    outputStream.close();

                    boolean completed = FTP.completePendingCommand();
                    set_Status();
                    if (completed) {
                        System.out.println("The   file is uploaded successfully.");

                    }

                } catch (IOException ex) {
                    System.out.println("Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            // labelProgressFile.setText("Envoi d'un fichier au serveur: " + selectedFile.getName());
            //gridPaneProgress.setVisible(true);
            //  new Thread(() -> {
               /* try {
                    String RemoteFile = path + selectedFile.getName();
                    FileInputStream inputStream = new FileInputStream(selectedFile);
                    OutputStream outputStream = FTP.storeFileStream(RemoteFile);
                    byte[] bytesIn = new byte[1024];
                    int read;
                    long allBytesRead = 0;
                    while ((read = inputStream.read(bytesIn)) != -1) {
                        outputStream.write(bytesIn, 0, read);
                        allBytesRead += read;
                        progress.setProgress(allBytesRead / (double) selectedFile.length());
                    }
                    inputStream.close();
                    //  Platform.runLater(() -> MWC.gridPaneProgress.setVisible(false));
                    //  Platform.runLater(() -> MWC.updateFilesTree(null));
                    //downloadFile1
                } catch (Exception e) {
                    System.out.println(e.getCause());
                    log.setText("Erreur lors de l'envoi du fichier ,Pour une raison quelconque, il n'est pas possible d'envoyer des données dans un fichier.");
                }
                // }).start();*/
            downloadFile1("");
        progress.setVisible(false);

    }

    private void path_back(ActionEvent actionEvent) {

        int i = 0;
        if (path != null && path.length() > 0) {
            do {
                i++;
            } while (path.charAt(path.length() - i) != '/');
            path = path.substring(0, path.length() - i);
            System.out.println("" + path);
            path_edit.setText(path);
            downloadFile1("..");
        }


    }

    private void Disconnect(ActionEvent actionEvent) {
        log.setText("Disconnected");
        try {
            FTP.disconnect();
            status_reply.setText("");

        } catch (IOException e) {
            e.printStackTrace();
        }

        table.getItems().clear();
        logindisabble(false);
        disconnect.setDisable(true);
        detaille.setText("");
        path_edit.setText("");
    }

    private void close(ActionEvent actionEvent) {
        System.exit(0);
    }

    private void path_set(ActionEvent actionEvent) {
        String temp = "";
        String newpath = path_edit.getText().replaceAll(" ","");
while (path.length()>0){
    int i = 0;
    if (path != null ) {
        do {
            i++;
        } while (path.charAt(path.length() - i) != '/');
        path = path.substring(0, path.length() - i);
        System.out.println("" + path);
        path_edit.setText(path);
        downloadFile1("..");
    }


}
     if (!newpath.isEmpty()) {
            if (newpath.charAt(0) == '/'){newpath= newpath.replaceAll("^.", "");}
            if (newpath.charAt(newpath.length()-1) == '/') {newpath= newpath.replaceAll(".$", "");}


        System.out.println("" +newpath);
               int j=0;
        while (!newpath.isEmpty()&&j<5) {
            if (newpath.charAt(0) == '/'){   newpath= newpath.replaceAll("^.", "");}
            System.out.println("after remove "+newpath);
            int i = -1;
            if (newpath != null ) {
                do {
                    i++;

                } while ((newpath.charAt(i) != '/') && (newpath.length()>i+1));
                System.out.print(i+" ");
                String cd ="";
                if (newpath.length()==i+1){
                    cd=newpath;
                    newpath="";
                }else {
            cd=  newpath.substring(0,i);
                    newpath= newpath.replaceAll("^"+cd, "");}
                temp=temp+"/"+cd;
                path=temp;
downloadFile1(cd);
                //path = path.substring(0, path.length() - i);

                System.out.println("cd :    " + cd);
                System.out.println("newpath:" + newpath);
                System.out.println("path:   " + temp);
               // path_edit.setText(path);
                //downloadFile1("..");
            }
        }

        }
    }

    private void min(ActionEvent actionEvent) {
        stage.setIconified(true);
    }

    public static void init(Stage st) {
        stage = st;
    }

    private void initConnect(ActionEvent actionEvent) {
        if (isInputDataCorrect()) {
            optionButtonDisable(true);
            isConnect = false;
            try {
                FTP.setControlEncoding("UTF-8");
                if (FTP.isConnected())
                {
                    FTP.disconnect();
                    set_Status();
                }
                FTP.connect(ip_address.getText());
                set_Status();
                if (FTP.isConnected()) {
                    if (FTP.login(username.getText(), password.getText()))
                    {
                        int reply = FTP.getReplyCode();
                        set_Status();
                        if (FTPReply.isPositiveCompletion(reply)) // Проверяем подключение
                        {
                            FTP.enterLocalPassiveMode();
                            log.setText("connected");
                            isConnect = true;
                            optionButtonDisable(false);
                            logindisabble(true);
                            disconnect.setDisable(false);

                            downloadFile1("");
                          //  FTP.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
                            FTP.setFileType(FTP.BINARY_FILE_TYPE);
                            FTP.setFileTransferMode(FTP.BINARY_FILE_TYPE);
                        } else {
                            FTP.logout();
                            set_Status();
                            FTP.disconnect();
                            set_Status();
                            log.setText("Erreur de travail avec le serveur.");
                        }
                    } else {
                        FTP.logout();
                        set_Status();
                        log.setText("Nom d'utilisateur ou mot de passe invalide.");
                    }
                } else {
                    log.setText("Tentative de connexion infructueuse.");
                }
            } catch (Exception e) {
                log.setText("Tentative de connexion infructueuse.");
            }
        }
    }

    private boolean isInputDataCorrect() { // Проверка всех полей на корректность введенных данных с помощью регулярных выражений
        if (!regex(ip_address.getText(), "^(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[0-9]{2}|[0-9])(\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[0-9]{2}|[0-9])){3}$")) {
            log.setText("Adresse IP non valide.");
            return false;
        }
        if (!regex(username.getText(), "^[A-Za-z0-9@#&_]+$")) {
            log.setText("Nom d'utilisateur non valide.");
            return false;
        }
        if (!regex(password.getText(), "^[A-Za-z0-9_*]+$")) {
            log.setText("Mot de passe utilisateur non valide.");
            return false;
        }
        log.setText("");
        return true;
    }

    private boolean regex(String text, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    private void optionButtonDisable(boolean isDisable) {

        upload.setDisable(isDisable);
        download.setDisable(isDisable);
        delete.setDisable(isDisable);
        rename.setDisable(isDisable);
        refreche.setDisable(isDisable);
        add_folder.setDisable(isDisable);

    }

    private void logindisabble(boolean isDisable) {

        ip_address.setDisable(isDisable);
        username.setDisable(isDisable);
        password.setDisable(isDisable);

        connect.setDisable(isDisable);

    }

    public void downloadFile1(String remotePath) {
        selected_file=null;
        table.getItems().clear();
        path_edit.setText(path);
        FILESS.clear();
        try {
            boolean success = FTP.changeWorkingDirectory(remotePath);
            set_Status();
            if (!success && remotePath != "") {
                log.setText("your Path is wrong");
            } else {


                FTPFile[] ftpFiles = FTP.listFiles();
                set_Status();
                for (FTPFile file : ftpFiles) {
                    type = null;
                    boolean read = false, write = true, excut = false;

                    if (file.isDirectory())
                        type = new Image(getClass().getResourceAsStream("img/fileicons/folder.png"), 50, 50, true, true);
                    else
                        type = new Image(getClass().getResourceAsStream("img/fileicons/other.png"), 50, 50, true, true);

                    if (file.hasPermission(FTPFile.GROUP_ACCESS, FTPFile.READ_PERMISSION)) {
                        read = true;
                    }
                    if (file.hasPermission(FTPFile.GROUP_ACCESS, FTPFile.WRITE_PERMISSION)) {
                        write = true;
                    }
                    if (file.hasPermission(FTPFile.GROUP_ACCESS, FTPFile.EXECUTE_PERMISSION)) {
                        excut = true;
                    }
                    FILESS.add(new FILES(new ImageView(type), remotePath + "/" + file.getName(), file.getName(), file.getSize() , read, write, excut, file.isDirectory()));
                }

                ObservableList data = FXCollections.observableList(FILESS);
                col_type.setCellValueFactory(new PropertyValueFactory("type"));
                col_type.setStyle("-fx-alignment: CENTER;");
                col_name.setCellValueFactory(new PropertyValueFactory("name"));
                col_size.setCellValueFactory(new PropertyValueFactory("size"));

                table.setItems(data);
                table.setRowFactory(tv -> {
                    TableRow<FILES> row = new TableRow<>();
                    row.setOnMouseClicked(event -> {
                        if (event.getClickCount() == 1 && (!row.isEmpty())) {
                            selected_file = row.getItem();
                            detaille.setStyle("-fx-alignment: center-left;");
                            String r = "", w = "", e = "";
                            if (selected_file.isReadable()) r = "Readable";
                            else r = " Not Readable";
                            if (selected_file.isWriteble()) w = "Writeble";
                            else w = "Not Writeble";
                            if (selected_file.isExcutibale()) e = "Excutibale";
                            else e = "Not Excutibale";
                            detaille.setText("Path:" + selected_file.getPath() + "\nPermission : " + r + "\n                   " + w + "\n                   " + e);
                        }
                        if (event.getClickCount() == 2 && (!row.isEmpty()) && row.getItem().isDirectory()) {

                            path = path + "/" + row.getItem().getName();


                            downloadFile1(row.getItem().getName());

                        }
                    });

                    return row;
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close_other(Button btn){
        if (btn.equals(add_folder)){
            rename_layout.setVisible(false);
            delete_layout.setVisible(false);
        }else   if (btn.equals(rename)){
            add_layout.setVisible(false);
            delete_layout.setVisible(false);
        }else  if (btn.equals(delete)){
            rename_layout.setVisible(false);
            add_layout.setVisible(false);
        }
    }
    public void set_Status(){
        String serverReply = FTP.getReplyString();
        status_reply.setText("status: "+serverReply);

    }

}
