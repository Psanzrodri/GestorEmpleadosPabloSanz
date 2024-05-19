package Trabajadores;

import java.sql.Connection;
import java.sql.*;
import java.time.LocalDate;


public class Trabajador {
    private String nombre;
    private String puesto;
    private int salario;
    private LocalDate fechaAlta;

    public Trabajador(String nombre, String puesto, int salario){
        this.nombre = nombre;
        this.puesto = puesto;
        this.salario = salario;
        this.fechaAlta = LocalDate.now();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public int getSalario() {
        return salario;
    }

    public void setSalario(int salario) {
        this.salario = salario;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }


    public void insertarTrabajador(Trabajador trabajador) {
        String url = "jdbc:mysql://localhost:3306/bdgestorEmpleados";
        String user = "root";
        String pass = "root";

        String nom = trabajador.getNombre();
        String pue = trabajador.getPuesto();
        int sal = trabajador.getSalario();
        LocalDate fecha = trabajador.getFechaAlta();

        Connection conexion = null;


        try{
            conexion = DriverManager.getConnection(url, user, pass);
            System.out.println("Conectado");
            PreparedStatement st = conexion.prepareStatement("INSERT INTO TRABAJADOR(NOMBRE, PUESTO, SALARIO, FECHA) VALUES (?, ?, ?, ?)");
            st.setString(1, nom);
            st.setString(2, pue);
            st.setInt(3, sal);
            st.setDate(4, Date.valueOf(fecha));
            st.executeUpdate();
            conexion.close();
        }
        catch(SQLException e){
            throw new IllegalStateException("Error al conectar la BD");
        }
    }



}