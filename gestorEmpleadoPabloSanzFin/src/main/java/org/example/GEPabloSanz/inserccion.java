package org.example.GEPabloSanz;

import Trabajadores.Trabajador;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class inserccion {

    @FXML
    private TextField txtFieldNombre;
    @FXML
    private ComboBox<String> cmbBoxPuesto;
    @FXML
    private TextField txtFieldSalario;
    @FXML
    private ListView<String> lstVwNombres;
    @FXML
    private Label lblTrabajador;

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/bdgestorEmpleados";
    private static final String DATABASE_USER = "root";
    private static final String DATABASE_PASS = "root";

    @FXML
    protected void insertarEmpleado() {
        if (!areInputsValid()) {
            displayAlert("Error", "Todos los campos deben estar rellenados");
            return;
        }
        Trabajador trabajador = new Trabajador(txtFieldNombre.getText(), cmbBoxPuesto.getValue(), Integer.parseInt(txtFieldSalario.getText()));
        trabajador.insertarTrabajador(trabajador);
        displayAlert("HECHO", "Mensaje", "Empleado " + trabajador.getNombre() + " introducido en la base de datos satisfactoriamente.");
        verTrabajadores();
    }

    private boolean areInputsValid() {
        return !(txtFieldNombre.getText().isEmpty() || txtFieldSalario.getText().isEmpty() || cmbBoxPuesto.getValue() == null);
    }

    private void displayAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private void displayAlert(String title, String contentText) {
        displayAlert(title, null, contentText);
    }

    @FXML
    protected void cargarDatosArchivo() {
        try (Scanner scanner = new Scanner(new File(getClass().getClassLoader().getResource("txtYbd/trabajadores.txt").getFile()))) {
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                Trabajador trabajador = parsearLinea(linea);
                trabajador.insertarTrabajador(trabajador);
            }
            verTrabajadores();
        } catch (FileNotFoundException e) {
            displayAlert("Error", "Archivo no encontrado");
        }
    }

    protected static Trabajador parsearLinea(String linea) {
        String[] tokens = linea.split(";");
        return new Trabajador(tokens[0], tokens[1], Integer.parseInt(tokens[2]));
    }

    @FXML
    protected void verTrabajadores() {
        try (Connection conexion = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS)) {
            List<String> lista = new ArrayList<>();
            ResultSet rs = queryTrabajadores(conexion);
            while (rs.next()) {
                lista.add(rs.getString("NOMBRE"));
            }
            ObservableList<String> nombres = FXCollections.observableArrayList(lista);
            lstVwNombres.setItems(nombres);
            Detalles();
        } catch (SQLException e) {
            throw new IllegalStateException("Error al conectar la BD", e);
        }
    }

    private ResultSet queryTrabajadores(Connection conexion) throws SQLException {
        PreparedStatement pst = conexion.prepareStatement("SELECT NOMBRE FROM TRABAJADOR");
        return pst.executeQuery();
    }

    @FXML
    protected void Detalles() {
        if (lstVwNombres.getSelectionModel().getSelectedItem() == null) return;
        try (Connection conexion = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS)) {
            String nombreSeleccionado = lstVwNombres.getSelectionModel().getSelectedItem();
            ResultSet rs = queryTrabajadorDetalle(conexion, nombreSeleccionado);
            if (rs.next()) {
                lblTrabajador.setText(
                        rs.getString("ID") + "\n\n" +
                                rs.getString("NOMBRE") + "\n\n" +
                                rs.getString("PUESTO") + "\n\n" +
                                rs.getString("SALARIO") + "\n\n" +
                                rs.getString("FECHA"));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al conectar la BD", e);
        }
    }

    private ResultSet queryTrabajadorDetalle(Connection conexion, String nombre) throws SQLException {
        PreparedStatement pst = conexion.prepareStatement("SELECT * FROM TRABAJADOR WHERE NOMBRE = ?");
        pst.setString(1, nombre);
        return pst.executeQuery();
    }

    @FXML
    protected void EliminarEmpleado() {
        if (lstVwNombres.getSelectionModel().getSelectedItem() == null) return;
        try (Connection conexion = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS)) {
            String nombreSeleccionado = lstVwNombres.getSelectionModel().getSelectedItem();
            deleteTrabajador(conexion, nombreSeleccionado);
            displayAlert("Operación Exitosa", "El trabajador ha sido eliminado.");
            verTrabajadores();
        } catch (SQLException e) {
            throw new IllegalStateException("Error al eliminar un trabajador", e);
        }
    }

    private void deleteTrabajador(Connection conexion, String nombre) throws SQLException {
        PreparedStatement pst = conexion.prepareStatement("DELETE FROM trabajador WHERE NOMBRE = ?");
        pst.setString(1, nombre);
        pst.executeUpdate();
    }

    @FXML
    protected void EditarLista() throws IOException {
        if (lstVwNombres.getSelectionModel().getSelectedItem() == null) return;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/GEPabloSanz/edicion.fxml"));
        Parent p = fxmlLoader.load();
        edicion controller = fxmlLoader.getController();
        controller.mostrar(lstVwNombres.getSelectionModel().getSelectedItem());
        Stage stage = new Stage();
        stage.setScene(new Scene(p));
        stage.setTitle("Modificar empleado");
        stage.show();
    }
}
