package org.example.GEPabloSanz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Objects;

public class edicion {

    @FXML private Label lblIdTrabajador;
    @FXML private TextField txtFldNombre;
    @FXML private TextField txtFldPuesto;
    @FXML private TextField txtFldSal;

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/bdgestorEmpleados";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root";

    private String nombre1;

    @FXML
    protected void mostrar(String nom) {
        nombre1 = nom;
        try (Connection conexion = DriverManager.getConnection(DATABASE_URL, DB_USER, DB_PASS)) {
            PreparedStatement pst = conexion.prepareStatement("SELECT * FROM trabajador WHERE nombre = ?");
            pst.setString(1, nom);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                lblIdTrabajador.setText(rs.getString("ID"));
                txtFldNombre.setText(rs.getString("Nombre"));
                txtFldPuesto.setText(rs.getString("Puesto"));
                txtFldSal.setText(rs.getString("Salario"));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al conectar la Base de Datos", e);
        }
    }

    @FXML
    protected void Modificar() {
        if (PuestoInvalido(txtFldPuesto.getText())) {
            mostrarAlertaPuestoInvalido();
            return;
        }

        actualizarTrabajador();
    }

    private boolean PuestoInvalido(String puesto) {
        String[] validPuestos = {"Scada Manager", "Sales Manager", "Product Owner", "Product Manager", "Analyst Programmer", "Junior Programmer"};
        for (String valid : validPuestos) {
            if (Objects.equals(puesto, valid)) {
                return false;
            }
        }
        return true;
    }

    private void mostrarAlertaPuestoInvalido() {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText("Introduzca un valor correcto de puesto:");
        alerta.setContentText("Scada Manager, Sales Manager, Product Owner, Product Manager, Analyst Programmer, o Junior Programmer");
        alerta.showAndWait();
    }

    private void actualizarTrabajador() {
        try (Connection conexion = DriverManager.getConnection(DATABASE_URL, DB_USER, DB_PASS)) {
            conexion.setAutoCommit(false);
            int id = obtenerIDTrabajador(conexion);
            PreparedStatement pst1 = conexion.prepareStatement("UPDATE TRABAJADOR SET NOMBRE = ?, PUESTO = ?, SALARIO = ? WHERE ID = ?");
            pst1.setString(1, txtFldNombre.getText());
            pst1.setString(2, txtFldPuesto.getText());
            pst1.setInt(3, Integer.parseInt(txtFldSal.getText()));
            pst1.setInt(4, id);
            pst1.executeUpdate();
            conexion.commit();
            mostrarInformacionActualizada();
        } catch (SQLException e) {
            throw new IllegalStateException("Error al modificar trabajador", e);
        }




















    }

    private int obtenerIDTrabajador(Connection conexion) throws SQLException {
        PreparedStatement st = conexion.prepareStatement("SELECT ID FROM trabajador WHERE NOMBRE = ?");
        st.setString(1, nombre1);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            return rs.getInt("ID");
        }
        throw new SQLException("Trabajador no encontrado");
    }

    private void mostrarInformacionActualizada() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Trabajador actualizado");
        alert.showAndWait();
    }

    @FXML
    protected void Cancelar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
