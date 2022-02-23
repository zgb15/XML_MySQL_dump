package mainPackage;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
	// programa que inserte en una tabla de BBDD la info de un XML y permita hacer búsquedas en esa BBDD

        try {


            Bd bd = new Bd();

            // Para resetear la tabla
            bd.resetearTabla();

            // Para insertar en la base de datos el contenido XML
            bd.insertarXML();

            // Para realizar búsquedas por palabras
            bd.buscador();

        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
