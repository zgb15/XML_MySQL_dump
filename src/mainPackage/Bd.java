package mainPackage;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.util.Scanner;

public class Bd {

    /**
     * @see Bd
     */

    private static Statement sentenciaSQL = null;
    private static Connection conexion = null;

    /** método para conectar con la base de datos */
    final void conectar() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection("jdbc:mysql://localhost/ad2", "root", "kvMu*0Fi9RY$!Q8D"); // información de nuestra BBDD

        } catch (ClassNotFoundException | SQLException cn) {
            cn.printStackTrace();
        }
    }

    /** método para desconectarse de la base de datos */
    final void desconectar() {

        try {
            sentenciaSQL.close();
            conexion.close();

        } catch (SQLException ex) {
            System.out.println("ERROR AL CERRAR CONEXIÓN CON LA BBDD");
        }
    }

    /** método para limpiar la tabla antes de hacer el volcado de datos del XML */
    public void resetearTabla() throws SQLException, ClassNotFoundException {

        int result1;
        String sql1;
        ResultSet result=null;

        try {

            conectar();

            sentenciaSQL = conexion.createStatement();

            System.out.println("----------------------------");

            // Borra y resetea toda la tabla guardado
            sql1 = "truncate table usuarios;";

            result1 = sentenciaSQL.executeUpdate(sql1);

            // Imprime el mensaje si se ha reseteado la tabla guardado
            if (result1 >= -1) {

                System.out.println("Se ha reseteado correctamente la tabla 'usuarios'");

            }

        } catch (SQLException ex) {
            System.out.println("Error");
        } finally {
            desconectar();
        }
    }

    /** MÉTODO PARA INSERTAR EN LA TABLA EN MYSQL LOS DATOS DEL XML */
    public void insertarXML(){

        int result;
        String sql;
        ResultSet resultset1=null;
        NodeList nList;

        try {

            conectar();
            sentenciaSQL = conexion.createStatement();

            System.out.println("----------------------------");

            //localizar fichero xml
            File fXmlFile = new File("src/mainPackage/prueba.xml");
            //obtengo el árbol del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            //transformo el arbol en fichero legible
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            //transformo en documento que se puede recorrer
            Document doc = dBuilder.parse(fXmlFile);

            // Solo hay que normalizar si en la estructura del xml faltan nodos o algunos
            // están vacíos
            doc.getDocumentElement().normalize();

            // Obtenemos todos los usuarios
            nList = doc.getElementsByTagName("usuario");

            // Recorremos todos los usuarios
            for (int temp = 0; temp < nList.getLength(); temp++) {

                String todo="";
                // Seleccionamos un nodo
                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    // String en el que meteremos todos los datos del XML de cada usuario que se pasará al campo "descripcion" de la tabla en nuestra BBDD
                    todo=eElement.getElementsByTagName("nombre").item(0).getTextContent().concat(eElement.getElementsByTagName("dni").item(0).getTextContent()).concat(eElement.getElementsByTagName("telefono1").item(0).getTextContent()).concat(eElement.getElementsByTagName("telefono2").item(0).getTextContent()).concat(eElement.getElementsByTagName("edad").item(0).getTextContent()).concat(eElement.getElementsByTagName("localidad").item(0).getTextContent());

                    // sentencia para insertar los datos del XML de cada usuario en la tabla de la BBDD
                    sql = "insert into ad2.usuarios (nombre,dni,telefono1,telefono2,edad,localidad,descripcion) values('"+ eElement.getElementsByTagName("nombre").item(0).getTextContent() + "','"+ eElement.getElementsByTagName("dni").item(0).getTextContent() + "','"+ eElement.getElementsByTagName("telefono1").item(0).getTextContent() + "','"+ eElement.getElementsByTagName("telefono2").item(0).getTextContent() + "','"+ eElement.getElementsByTagName("edad").item(0).getTextContent() + "','"+ eElement.getElementsByTagName("localidad").item(0).getTextContent() + "','"+todo+"')";

                    result = sentenciaSQL.executeUpdate(sql); // ejecutamos la sentencia

                    // Imprime el mensaje si se ha reseteado la tabla guardado
                    if (result >= -1) {
                        System.out.println("Usuario agregado a la BBDD correctamente");
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            desconectar();
        }

    }

    /** buscador por línea de caracteres o palabras */
    public void buscador(){

        String sql;
        ResultSet resultset=null;

        int respuesta=0; // el usuario va eligiendo 1 si quiere introducir más palabras u otro número si desea parar
        String palabraClave=""; // la palabra que introduce el usuario para realizar la búsqueda
        String caracteresAbuscar=""; // string donde meteremos todos los caracteres
        ArrayList<String>listaPalabras = new ArrayList<>(); // lista donde almacenaremos las palabras que vaya introduciendo

        Scanner sc = new Scanner(System.in);

        System.out.println("----------------------------");

        try{
            conectar();
            sentenciaSQL = conexion.createStatement();

            // creamos la estructura del menú
            do{
                System.out.println("Introduzca una palabra clave");
                sc.nextLine(); // vaciamos el buffer
                palabraClave = sc.nextLine();

                // nos vamos guardando las palabras que introduce para buscar un registro que las contenga todas
                caracteresAbuscar = caracteresAbuscar.concat(palabraClave);

                if(!listaPalabras.isEmpty() && !listaPalabras.contains(caracteresAbuscar)){ // si la lista NO está vacía y NO contiene ya el string
                    listaPalabras.add(caracteresAbuscar);
                    // System.out.println("se ha añadido -"+caracteresAbuscar+"- a la lista");
                }

                listaPalabras.add(palabraClave);

                System.out.println("Pulse 1 para introducir más palabras, Pulse cualquier otro número para finalizar");
                respuesta = sc.nextInt();

            }while(respuesta==1); // se ejecuta mientras el usuario pulse 1, es decir, mientras responda que desea introducir más palabras

            for (String listaPalabra : listaPalabras) { // bucle con el que recorremos la lista y vamos leyendo las palabras introdujo el usuario
                sql = "select * from usuarios where descripcion like '%" + listaPalabra + "%'"; // sentencia SQL que buscará el registro cuyo campo DESCRIPCIÓN contenga esa palabra
                resultset = sentenciaSQL.executeQuery(sql);

                // mientras haya registros para mostrar
                while (resultset.next()) {
                    System.out.println(" -> Registro que contiene la búsqueda [" + listaPalabra +"]"); // imprimimos la palabra que contiene el registro encontrado
                    System.out.println("ID: "+resultset.getString("id"));
                    System.out.println("Nombre: "+resultset.getString("nombre"));
                    System.out.println("DNI: "+resultset.getString("dni"));
                    System.out.println("Telefono 1: "+resultset.getString("telefono1"));
                    System.out.println("Telefono 2: "+resultset.getString("telefono2"));
                    System.out.println("Edad: "+resultset.getString("edad"));
                    System.out.println("Localidad: "+resultset.getString("localidad"));
                    System.out.println("*************************************");
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            desconectar();
        }
    }
}
