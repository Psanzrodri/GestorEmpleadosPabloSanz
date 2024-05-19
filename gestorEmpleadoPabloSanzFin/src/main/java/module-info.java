module org.example.GEPabloSanz{
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jdk.jdi;
    requires jdk.jconsole;


    opens org.example.GEPabloSanz to javafx.fxml;
    exports org.example.GEPabloSanz;
}