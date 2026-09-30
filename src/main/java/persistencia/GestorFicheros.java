package persistencia;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import modelo.Cliente;
import modelo.Producto;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;


/**
 * ================================================================
 * GESTOR DE FICHEROS
 * ================================================================
 *
 * Esta clase concentra la lógica relacionada con la persistencia
 * de nuestros objetos en ficheros.
 *
 * La idea es separar responsabilidades:
 *
 *      INTERFAZ GRÁFICA
 *             |
 *             v
 *         SERVICIO
 *             |
 *             v
 *      GestorFicheros
 *             |
 *             v
 *          FICHEROS
 *
 *
 * En esta fase estamos trabajando con cuatro formatos:
 *
 *      TXT
 *      CSV
 *      XML
 *      JSON
 *
 *
 * IMPORTANTE:
 *
 * Para TXT y CSV estamos realizando nosotros manualmente
 * la transformación entre objetos Java y texto.
 *
 * Con XML y JSON utilizaremos Jackson para realizar gran
 * parte de ese trabajo automáticamente.
 *
 *
 * =================================================================
 * EXPERIMENTOS PROPUESTOS PARA HACER CON LOS ALUMNOS
 * =================================================================
 *
 * Antes de modificar nada:
 *
 * 1. Crear varios clientes desde el programa.
 *
 * 2. Exportarlos a XML.
 *
 * 3. Exportarlos también a JSON.
 *
 * 4. Abrir ambos ficheros con IntelliJ o un editor de texto.
 *
 * 5. Comparar cómo se representa la misma información en ambos
 *    formatos.
 *
 *
 * -----------------------------------------------------------------
 * EXPERIMENTO 1 - MODIFICAR UN VALOR
 * -----------------------------------------------------------------
 *
 * XML:
 *
 *      <nombre>Ana</nombre>
 *
 * cambiar por:
 *
 *      <nombre>María</nombre>
 *
 *
 * JSON:
 *
 *      "nombre" : "Ana"
 *
 * cambiar por:
 *
 *      "nombre" : "María"
 *
 *
 * Volver a importar.
 *
 * PREGUNTA:
 *
 * ¿Aparece María en nuestra aplicación?
 *
 *
 * -----------------------------------------------------------------
 * EXPERIMENTO 2 - ELIMINAR UNA PROPIEDAD
 * -----------------------------------------------------------------
 *
 * Eliminar, por ejemplo, el teléfono.
 *
 * XML:
 *
 *      <telefono>600123456</telefono>
 *
 *
 * JSON:
 *
 *      "telefono" : "600123456"
 *
 *
 * Volver a importar.
 *
 * Observar qué valor recibe Java.
 *
 * PISTA:
 *
 * Si una propiedad de tipo referencia como String no aparece,
 * normalmente su valor quedará a null si Jackson puede construir
 * correctamente el objeto.
 *
 *
 * -----------------------------------------------------------------
 * EXPERIMENTO 3 - AÑADIR UNA PROPIEDAD DESCONOCIDA
 * -----------------------------------------------------------------
 *
 * Añadir manualmente:
 *
 * XML:
 *
 *      <ciudad>Madrid</ciudad>
 *
 *
 * JSON:
 *
 *      "ciudad" : "Madrid"
 *
 *
 * Pero nuestra clase Cliente NO tiene:
 *
 *      private String ciudad;
 *
 *
 * PREGUNTA:
 *
 * ¿Qué hará Jackson?
 *
 * ¿Ignorará el dato?
 * ¿Dará un error?
 *
 * Es interesante probarlo en XML y JSON y leer el mensaje
 * de la excepción.
 *
 *
 * -----------------------------------------------------------------
 * EXPERIMENTO 4 - ROMPER LA SINTAXIS
 * -----------------------------------------------------------------
 *
 * XML:
 *
 * eliminar una etiqueta de cierre:
 *
 *      <nombre>Ana
 *
 *
 * JSON:
 *
 * eliminar una coma:
 *
 *      {
 *          "id" : 1
 *          "nombre" : "Ana"
 *      }
 *
 *
 * Intentar importar.
 *
 * OBJETIVO:
 *
 * Comprobar que XML y JSON tienen una sintaxis que debe respetarse.
 *
 *
 * -----------------------------------------------------------------
 * EXPERIMENTO 5 - CAMBIAR EL TIPO DE UN DATO
 * -----------------------------------------------------------------
 *
 * Si id es un int, cambiar:
 *
 *      "id" : 1
 *
 * por:
 *
 *      "id" : "ABC"
 *
 *
 * O en XML:
 *
 *      <id>ABC</id>
 *
 *
 * Intentar importar.
 *
 * PREGUNTA:
 *
 * ¿Puede Jackson convertir "ABC" en un int?
 *
 *
 * -----------------------------------------------------------------
 * EXPERIMENTO 6 - AÑADIR UN CLIENTE MANUALMENTE
 * -----------------------------------------------------------------
 *
 * Copiar uno de los clientes directamente dentro del fichero,
 * modificar sus datos e intentar importarlo.
 *
 * OBJETIVO:
 *
 * Entender que el fichero es independiente del programa:
 *
 *      fichero
 *         ↓
 *      Jackson
 *         ↓
 *      objetos Java
 *
 *
 * -----------------------------------------------------------------
 * EXPERIMENTO 7 - FICHERO VACÍO
 * -----------------------------------------------------------------
 *
 * Vaciar completamente un fichero JSON o XML e intentar importarlo.
 *
 * Observar qué excepción produce Jackson.
 *
 *
 * -----------------------------------------------------------------
 * EXPERIMENTO 8 - JSON: CAMBIAR [] POR {}
 * -----------------------------------------------------------------
 *
 * Nuestro JSON contiene una LISTA:
 *
 *      [
 *          {...},
 *          {...}
 *      ]
 *
 * Cambiarlo por:
 *
 *      {
 *          ...
 *      }
 *
 * y volver a importar.
 *
 * PREGUNTA:
 *
 * Si hemos pedido a Jackson:
 *
 *      List<Cliente>
 *
 * ¿puede deserializar un único objeto como si fuera una lista?
 *
 *
 * -----------------------------------------------------------------
 * EXPERIMENTO 9 - XML: MODIFICAR EL WRAPPER
 * -----------------------------------------------------------------
 *
 * En XML utilizamos ClientesXml como clase contenedora.
 *
 * Modificar manualmente la estructura del documento y observar
 * qué ocurre cuando deja de corresponderse con la estructura
 * que Jackson espera convertir a ClientesXml.
 *
 *
 * -----------------------------------------------------------------
 * EXPERIMENTO 10 - CARACTERES ESPECIALES
 * -----------------------------------------------------------------
 *
 * Probar nombres como:
 *
 *      José Álvarez
 *      María & Ana
 *      <Pepe>
 *      "Juan"
 *
 * Exportarlos a XML y JSON.
 *
 * Observar cómo Jackson representa o escapa automáticamente
 * determinados caracteres.
 *
 * Comparar este comportamiento con todo el código que nosotros
 * tuvimos que escribir manualmente para CSV.
 *
 *
 * =================================================================
 * IDEA FUNDAMENTAL
 * =================================================================
 *
 * TXT / CSV:
 *
 *      nosotros hacemos gran parte de la conversión.
 *
 *
 * XML / JSON:
 *
 *      objeto Java
 *           ↓
 *        Jackson
 *           ↓
 *      XML / JSON
 *
 *
 * y al importar:
 *
 *      XML / JSON
 *           ↓
 *        Jackson
 *           ↓
 *      objeto Java
 *
 */
public class GestorFicheros {


    /*
     * =============================================================
     * MAPPERS DE JACKSON
     * =============================================================
     *
     * Un "mapper" es el objeto encargado de realizar el mapeo:
     *
     *      objeto Java <----> formato externo
     *
     *
     * En nuestro caso utilizamos dos:
     *
     *      XmlMapper
     *          para XML
     *
     *      ObjectMapper
     *          para JSON
     *
     *
     * Ambos pertenecen a Jackson.
     */


    /*
     * XML_MAPPER
     * -------------------------------------------------------------
     *
     * XmlMapper es la variante de Jackson especializada en XML.
     *
     * Lo declaramos static porque todos los métodos de esta clase
     * son static y queremos compartir una única instancia.
     *
     * Lo declaramos final porque no queremos sustituir este mapper
     * por otro durante la ejecución.
     *
     *
     * SerializationFeature.INDENT_OUTPUT
     *
     * hace que Jackson escriba un XML formateado y legible.
     *
     * Sin indentación podríamos obtener algo parecido a:
     *
     * <ClientesXml><clientes><id>1</id>...</clientes></ClientesXml>
     *
     * Con indentación será más parecido a:
     *
     * <ClientesXml>
     *     <clientes>
     *         <id>1</id>
     *         ...
     *     </clientes>
     * </ClientesXml>
     *
     * La indentación NO cambia los datos.
     *
     * Solamente mejora la presentación del fichero.
     */
    private static final XmlMapper XML_MAPPER =
            (XmlMapper) new XmlMapper()
                    .enable(SerializationFeature.INDENT_OUTPUT);


    /*
     * MAPPER
     * -------------------------------------------------------------
     *
     * ObjectMapper es el mapper principal de Jackson para JSON.
     *
     * También activamos INDENT_OUTPUT para obtener JSON legible.
     *
     * En lugar de:
     *
     * [{"id":1,"nombre":"Ana"}]
     *
     * obtendremos algo parecido a:
     *
     * [
     *   {
     *      "id" : 1,
     *      "nombre" : "Ana"
     *   }
     * ]
     */
    private static final ObjectMapper MAPPER =
            new ObjectMapper()
                    .enable(SerializationFeature.INDENT_OUTPUT);



    // ============================================================
    // TXT
    // ============================================================


    /**
     * ============================================================
     * EXPORTAR CLIENTES A TXT
     * ============================================================
     *
     * Transforma una lista de objetos Cliente en un fichero
     * de texto.
     *
     * Hemos decidido utilizar ";" como separador.
     *
     * Ejemplo:
     *
     *      Cliente
     *      -------
     *      id       = 1
     *      nombre   = Ana
     *      email    = ana@email.com
     *      telefono = 600123456
     *
     * se guardará como:
     *
     *      1;Ana;ana@email.com;600123456
     *
     */
    public static void exportarClientesTxt(
            Path ruta,
            List<Cliente> clientes
    ) throws IOException {


        /*
         * Files.newBufferedWriter(...)
         *
         * abre un fichero para escritura.
         *
         * Utilizamos UTF-8 para que caracteres como:
         *
         *      á
         *      é
         *      ñ
         *      €
         *
         * se almacenen correctamente.
         *
         *
         * El try-with-resources:
         *
         *      try (...) {
         *
         *      }
         *
         * garantiza que Java cerrará automáticamente
         * el BufferedWriter al terminar.
         */
        try (BufferedWriter bw =
                     Files.newBufferedWriter(
                             ruta,
                             StandardCharsets.UTF_8
                     )) {


            /*
             * Recorremos todos los clientes.
             *
             * En cada iteración:
             *
             *      c
             *
             * representa un Cliente diferente.
             */
            for (Cliente c : clientes) {


                /*
                 * Construimos manualmente la representación
                 * textual del Cliente.
                 *
                 * Por ejemplo:
                 *
                 *      1;Ana;ana@email.com;600123456
                 *
                 *
                 * Estamos realizando una SERIALIZACIÓN MANUAL:
                 *
                 *      Cliente
                 *         ↓
                 *      String
                 *         ↓
                 *      fichero
                 */
                bw.write(
                        c.getId()
                                + ";"
                                + c.getNombre()
                                + ";"
                                + c.getEmail()
                                + ";"
                                + c.getTelefono()
                );


                /*
                 * Cada Cliente ocupará una línea.
                 */
                bw.newLine();
            }
        }
    }


    /**
     * ============================================================
     * IMPORTAR CLIENTES DESDE TXT
     * ============================================================
     *
     * Hace el proceso contrario:
     *
     *      fichero
     *         ↓
     *      String
     *         ↓
     *      Cliente
     *
     */
    public static List<Cliente> importarClientesTxt(
            Path ruta
    ) throws IOException {


        /*
         * Aquí iremos guardando todos los clientes
         * que consigamos reconstruir correctamente.
         */
        List<Cliente> resultado = new ArrayList<>();


        /*
         * Abrimos el fichero para lectura.
         */
        try (BufferedReader br =
                     Files.newBufferedReader(
                             ruta,
                             StandardCharsets.UTF_8
                     )) {


            /*
             * Variable que almacenará temporalmente
             * cada línea del fichero.
             */
            String linea;


            /*
             * readLine() devuelve:
             *
             *      una línea
             *
             * o:
             *
             *      null
             *
             * cuando llegamos al final del fichero.
             */
            while ((linea = br.readLine()) != null) {


                /*
                 * Dividimos la línea utilizando ";".
                 *
                 * Ejemplo:
                 *
                 *      1;Ana;ana@email.com;600123456
                 *
                 * se convierte en:
                 *
                 *      p[0] -> "1"
                 *      p[1] -> "Ana"
                 *      p[2] -> "ana@email.com"
                 *      p[3] -> "600123456"
                 */
                String[] p = linea.split(";");


                /*
                 * Nuestro Cliente necesita exactamente
                 * cuatro campos.
                 *
                 * Si no tenemos cuatro, descartamos la línea.
                 */
                if (p.length != 4) {
                    continue;
                }


                try {

                    /*
                     * El fichero solamente contiene texto.
                     *
                     * Por tanto:
                     *
                     *      p[0]
                     *
                     * es un String.
                     *
                     * Tenemos que convertirlo:
                     *
                     *      "123" -> 123
                     */
                    int id = Integer.parseInt(p[0]);


                    /*
                     * Estos campos ya son String.
                     */
                    String nombre = p[1];
                    String email = p[2];
                    String telefono = p[3];


                    /*
                     * Reconstruimos el objeto Cliente.
                     */
                    Cliente cliente =
                            new Cliente(
                                    id,
                                    nombre,
                                    email,
                                    telefono
                            );


                    /*
                     * Lo añadimos a la lista.
                     */
                    resultado.add(cliente);


                } catch (NumberFormatException ex) {

                    /*
                     * Si encontramos:
                     *
                     *      ABC;Ana;ana@email.com;600123456
                     *
                     * Integer.parseInt("ABC")
                     *
                     * provocará NumberFormatException.
                     */
                    System.err.println(
                            "Cliente incorrecto: " + linea
                    );
                }
            }
        }


        return resultado;
    }



    // ============================================================
    // CSV
    // ============================================================


    /*
     * Ahora aparece un problema nuevo.
     *
     * CSV utiliza normalmente una coma como separador.
     *
     * Podríamos tener:
     *
     *      1,Ana,ana@email.com,600123456
     *
     *
     * Pero ¿qué sucede si el propio dato contiene una coma?
     *
     * Por ejemplo:
     *
     *      Pérez, Juan
     *
     *
     * No podemos escribir:
     *
     *      1,Pérez, Juan,email,telefono
     *
     * porque parecería que Pérez y Juan son campos distintos.
     *
     *
     * CSV permite utilizar comillas:
     *
     *      1,"Pérez, Juan",email,telefono
     *
     *
     * Por tanto necesitamos implementar dos procesos:
     *
     *
     * ESCRITURA:
     *
     *      String
     *        ↓
     *      csv()
     *        ↓
     *      campo correctamente escapado
     *
     *
     * LECTURA:
     *
     *      línea CSV
     *        ↓
     *      parseCsv()
     *        ↓
     *      campos individuales
     */


    /**
     * ============================================================
     * MÉTODO csv()
     * ============================================================
     *
     * Recibe UN campo y devuelve ese campo preparado
     * para poder introducirlo correctamente en nuestro CSV.
     *
     * IMPORTANTE:
     *
     * Este método NO escribe en el fichero.
     *
     * Solamente transforma un String.
     *
     *
     * Ejemplos:
     *
     *      csv("Ana")
     *
     * devuelve:
     *
     *      Ana
     *
     *
     * Pero:
     *
     *      csv("Pérez, Juan")
     *
     * devuelve:
     *
     *      "Pérez, Juan"
     *
     */
    private static String csv(String valor) {


        /*
         * PRIMER CASO:
         *
         * El valor recibido es null.
         *
         * No podemos hacer:
         *
         *      valor.contains(...)
         *
         * sobre null porque provocaría:
         *
         *      NullPointerException
         *
         * Hemos decidido representar null mediante
         * una cadena vacía.
         */
        if (valor == null) {
            return "";
        }


        /*
         * Comprobamos si el contenido tiene alguno
         * de los caracteres que necesitan tratamiento
         * especial:
         *
         *      ,
         *      "
         *      salto de línea
         *
         *
         * || significa OR lógico.
         *
         * Basta con que UNA de las condiciones sea true
         * para entrar en el if.
         */
        if (
                valor.contains(",")
                        || valor.contains("\"")
                        || valor.contains("\n")
        ) {


            /*
             * Aquí hacemos DOS operaciones diferentes.
             *
             *
             * OPERACIÓN 1
             * -----------
             *
             * Escapar las comillas interiores.
             *
             * En CSV una comilla interior se puede representar
             * duplicándola.
             *
             *
             * Tenemos:
             *
             *      Tienda "Pepe"
             *
             * Después de:
             *
             *      valor.replace("\"", "\"\"")
             *
             * tendremos:
             *
             *      Tienda ""Pepe""
             *
             *
             * IMPORTANTE:
             *
             * \" es simplemente la manera de representar
             * el carácter " dentro de un String Java.
             *
             *
             * OPERACIÓN 2
             * -----------
             *
             * Rodeamos todo el campo con comillas.
             *
             *
             * Resultado final:
             *
             *      "Tienda ""Pepe"""
             *
             *
             * Otro ejemplo:
             *
             *      Pérez, Juan
             *
             * se convierte en:
             *
             *      "Pérez, Juan"
             *
             *
             * IMPORTANTE:
             *
             * Este replace NO elimina saltos de línea.
             *
             * Solamente sustituye:
             *
             *      "
             *
             * por:
             *
             *      ""
             */
            return "\""
                    + valor.replace("\"", "\"\"")
                    + "\"";
        }


        /*
         * Si el valor no tiene:
         *
         *      comas
         *      comillas
         *      saltos de línea
         *
         * no necesitamos modificarlo.
         */
        return valor;
    }



    /**
     * ============================================================
     * parseCsv()
     * ============================================================
     *
     * Este método hace aproximadamente el proceso contrario
     * de csv().
     *
     *
     * Recibe UNA LÍNEA completa:
     *
     *      3,"Pérez, ""Juan""",Ourense,Pepa
     *
     *
     * y debe obtener los diferentes campos.
     *
     *
     * NO podemos utilizar:
     *
     *      linea.split(",")
     *
     * porque la coma puede aparecer dentro de un campo:
     *
     *              ↓
     *      "Pérez, Juan"
     *
     *
     * Esa coma NO separa columnas.
     *
     *
     * Por eso necesitamos analizar la línea
     * CARÁCTER A CARÁCTER.
     */
    private static List<String> parseCsv(String linea) {


        /*
         * Lista donde almacenaremos los campos
         * que vayamos encontrando.
         */
        List<String> campos = new ArrayList<>();


        /*
         * StringBuilder nos permite ir construyendo
         * el campo carácter a carácter.
         */
        StringBuilder actual = new StringBuilder();


        /*
         * false -> estamos fuera de comillas.
         * true  -> estamos dentro de comillas.
         */
        boolean entreComillas = false;


        /*
         * Recorremos toda la línea carácter a carácter.
         */
        for (int i = 0; i < linea.length(); i++) {


            /*
             * Obtenemos el carácter situado en la posición i.
             */
            char c = linea.charAt(i);


            /*
             * CASO 1:
             *
             * Encontramos una comilla.
             */
            if (c == '"') {


                /*
                 * Si estamos dentro de comillas y encontramos:
                 *
                 *      ""
                 *
                 * significa que el contenido contiene
                 * una comilla real.
                 */
                if (
                        entreComillas
                                && i + 1 < linea.length()
                                && linea.charAt(i + 1) == '"'
                ) {


                    /*
                     * Añadimos una única comilla al resultado.
                     */
                    actual.append('"');


                    /*
                     * Saltamos la segunda comilla porque ya
                     * hemos procesado las dos.
                     */
                    i++;


                } else {


                    /*
                     * Si no es una comilla escapada, esta
                     * comilla abre o cierra un campo.
                     */
                    entreComillas = !entreComillas;
                }


                /*
                 * CASO 2:
                 *
                 * Encontramos una coma FUERA de comillas.
                 *
                 * Esa coma sí funciona como separador.
                 */
            } else if (
                    c == ',' && !entreComillas
            ) {


                /*
                 * Guardamos el campo que acabamos de terminar.
                 */
                campos.add(actual.toString());


                /*
                 * Vaciamos StringBuilder para empezar
                 * a construir el siguiente campo.
                 */
                actual.setLength(0);


            } else {


                /*
                 * CASO 3:
                 *
                 * Es un carácter normal.
                 *
                 * Lo incorporamos al campo actual.
                 */
                actual.append(c);
            }
        }


        /*
         * El último campo no termina con coma.
         *
         * Por eso debemos añadirlo manualmente
         * después de terminar el bucle.
         */
        campos.add(actual.toString());


        /*
         * Devolvemos todos los campos encontrados.
         */
        return campos;
    }



    /**
     * ============================================================
     * EXPORTAR CLIENTES A CSV
     * ============================================================
     *
     * Ahora utilizamos csv() para preparar correctamente
     * cada uno de los campos.
     */
    public static void exportarClientesCsv(
            Path ruta,
            List<Cliente> clientes
    ) throws IOException {


        /*
         * Abrimos el fichero para escritura.
         */
        try (BufferedWriter bw =
                     Files.newBufferedWriter(
                             ruta,
                             StandardCharsets.UTF_8
                     )) {


            /*
             * Escribimos la cabecera del CSV.
             */
            bw.write("id,nombre,email,telefono");

            bw.newLine();


            /*
             * Recorremos todos los clientes.
             */
            for (Cliente c : clientes) {


                /*
                 * Construimos el registro CSV.
                 *
                 * Aplicamos csv() a los String porque podrían
                 * contener comas, comillas o saltos de línea.
                 */
                bw.write(
                        c.getId()
                                + ","
                                + csv(c.getNombre())
                                + ","
                                + csv(c.getEmail())
                                + ","
                                + csv(c.getTelefono())
                );


                /*
                 * Cada Cliente ocupa un registro.
                 */
                bw.newLine();
            }
        }
    }



    /**
     * ============================================================
     * IMPORTAR CLIENTES DESDE CSV
     * ============================================================
     */
    public static List<Cliente> importarClientesCsv(
            Path ruta
    ) throws IOException {


        /*
         * Lista donde almacenaremos los objetos reconstruidos.
         */
        List<Cliente> resultado = new ArrayList<>();


        /*
         * Abrimos el fichero CSV.
         */
        try (BufferedReader br =
                     Files.newBufferedReader(
                             ruta,
                             StandardCharsets.UTF_8
                     )) {


            /*
             * Leemos una vez para consumir la cabecera:
             *
             *      id,nombre,email,telefono
             */
            String linea = br.readLine();


            /*
             * A partir de aquí procesamos los registros reales.
             */
            while ((linea = br.readLine()) != null) {


                /*
                 * Nuestro parser convierte la línea en campos.
                 */
                List<String> c =
                        parseCsv(linea);


                /*
                 * Un Cliente necesita exactamente cuatro campos.
                 */
                if (c.size() != 4) {
                    continue;
                }


                try {


                    /*
                     * Convertimos el primer campo de String a int.
                     */
                    int id =
                            Integer.parseInt(c.get(0));


                    /*
                     * Recuperamos los campos de texto.
                     */
                    String nombre =
                            c.get(1);

                    String email =
                            c.get(2);

                    String telefono =
                            c.get(3);


                    /*
                     * Reconstruimos el objeto.
                     */
                    Cliente cliente =
                            new Cliente(
                                    id,
                                    nombre,
                                    email,
                                    telefono
                            );


                    /*
                     * Lo incorporamos al resultado.
                     */
                    resultado.add(cliente);


                } catch (NumberFormatException e) {


                    /*
                     * Si el id no es numérico no podemos
                     * reconstruir correctamente el Cliente.
                     */
                    System.err.println(
                            "Cliente erróneo: " + linea
                    );
                }
            }
        }


        /*
         * Devolvemos todos los clientes reconstruidos.
         */
        return resultado;
    }



    // ============================================================
    // XML
    // ============================================================


    /**
     * ============================================================
     * EXPORTAR CLIENTES A XML
     * ============================================================
     *
     * A diferencia de TXT y CSV, aquí NO vamos a construir
     * manualmente el texto XML.
     *
     * No hacemos cosas como:
     *
     *      bw.write("<cliente>");
     *      bw.write("<nombre>" + nombre + "</nombre>");
     *
     * Dejamos ese trabajo a Jackson.
     *
     *
     * El recorrido será:
     *
     *      List<Cliente>
     *           ↓
     *      ClientesXml
     *           ↓
     *       XmlMapper
     *           ↓
     *       fichero XML
     *
     *
     * ClientesXml es una clase contenedora o WRAPPER.
     *
     * Su función es envolver:
     *
     *      List<Cliente>
     *
     * dentro de un objeto Java que Jackson puede utilizar
     * como estructura raíz del documento.
     */
    public static void exportarClientesXml(
            Path ruta,
            List<Cliente> clientes
    ) throws IOException {


        /*
         * Primero construimos el wrapper.
         *
         * Si clientes contiene:
         *
         *      Ana
         *      Luis
         *      Marta
         *
         * tendremos conceptualmente:
         *
         *      ClientesXml
         *           |
         *           +-- clientes
         *                  |
         *                  +-- Ana
         *                  +-- Luis
         *                  +-- Marta
         */
        ClientesXml contenedor =
                new ClientesXml(clientes);


        /*
         * ruta es un Path.
         *
         * Jackson puede escribir directamente sobre un File,
         * por lo que:
         *
         *      ruta.toFile()
         *
         * convierte la representación Path en File.
         *
         *
         * writeValue recibe:
         *
         *      1. DÓNDE escribir.
         *      2. QUÉ objeto serializar.
         *
         *
         * Jackson inspeccionará ClientesXml y sus propiedades
         * utilizando los getters.
         */
        XML_MAPPER.writeValue(
                ruta.toFile(),
                contenedor
        );
    }



    /**
     * ============================================================
     * IMPORTAR CLIENTES DESDE XML
     * ============================================================
     *
     * Realizamos el proceso inverso:
     *
     *      fichero XML
     *           ↓
     *       XmlMapper
     *           ↓
     *      ClientesXml
     *           ↓
     *      List<Cliente>
     *
     *
     * Ahora hablamos de DESERIALIZACIÓN.
     */
    public static List<Cliente> importarClientesXml(
            Path ruta
    ) throws IOException {


        /*
         * readValue necesita saber:
         *
         *      1. qué fichero debe leer;
         *      2. qué tipo de objeto Java queremos obtener.
         *
         *
         * Primer argumento:
         *
         *      ruta.toFile()
         *
         * indica el origen de los datos.
         *
         *
         * Segundo argumento:
         *
         *      ClientesXml.class
         *
         * indica el TIPO de objeto que Jackson debe construir.
         *
         *
         * IMPORTANTE:
         *
         * ClientesXml.class NO es un objeto ClientesXml.
         *
         * Es un objeto de tipo:
         *
         *      Class<ClientesXml>
         *
         * que representa la clase ClientesXml.
         *
         *
         * Podemos pensar:
         *
         *      ClientesXml
         *          -> nombre del tipo
         *
         *      new ClientesXml()
         *          -> objeto de ese tipo
         *
         *      ClientesXml.class
         *          -> objeto que representa ese tipo
         *
         *
         * Jackson necesita esta información porque todavía
         * NO existe el objeto: precisamente queremos que
         * Jackson lo construya a partir del XML.
         */
        ClientesXml contenedor =
                XML_MAPPER.readValue(
                        ruta.toFile(),
                        ClientesXml.class
                );


        /*
         * Ahora ya tenemos:
         *
         *      ClientesXml
         *           |
         *           +-- List<Cliente>
         *
         *
         * Queremos devolver solamente la lista.
         *
         *
         * Esta expresión utiliza el operador ternario:
         *
         *      condición ? valorSiTrue : valorSiFalse
         *
         *
         * Si:
         *
         *      contenedor.getClientes() == null
         *
         * devolvemos una lista vacía.
         *
         * En caso contrario devolvemos la lista obtenida
         * del XML.
         *
         *
         * Esto evita devolver null al resto de la aplicación.
         */
        return contenedor.getClientes() == null
                ? new ArrayList<>()
                : contenedor.getClientes();
    }



    // ============================================================
    // WRAPPER / CLASE CONTENEDORA PARA XML
    // ============================================================


    /**
     * ============================================================
     * ClientesXml
     * ============================================================
     *
     * Esta clase funciona como WRAPPER o clase contenedora.
     *
     *
     * Sin wrapper tendríamos:
     *
     *      List<Cliente>
     *
     *
     * Con wrapper tenemos:
     *
     *      ClientesXml
     *           |
     *           +-- List<Cliente>
     *
     *
     * Es decir, estamos "envolviendo" la lista dentro
     * de otro objeto.
     *
     *
     * IMPORTANTE:
     *
     * No debemos confundir este significado de wrapper
     * con las wrapper classes de Java:
     *
     *      int     -> Integer
     *      double  -> Double
     *      boolean -> Boolean
     *
     * Aquí hablamos simplemente de una clase utilizada
     * como CONTENEDOR.
     */
    public static class ClientesXml {


        /*
         * Esta es la información que realmente queremos guardar.
         *
         * Inicializamos la lista para evitar que inicialmente
         * tenga valor null.
         */
        private List<Cliente> clientes =
                new ArrayList<>();


        /**
         * Constructor vacío.
         *
         * Es especialmente importante durante la
         * deserialización.
         *
         * Jackson puede necesitar crear primero:
         *
         *      new ClientesXml()
         *
         * y posteriormente introducir los datos mediante
         * los setters.
         */
        public ClientesXml() {

        }


        /**
         * Constructor con parámetros.
         *
         * Este constructor nos resulta especialmente cómodo
         * al EXPORTAR.
         *
         * Podemos hacer:
         *
         *      new ClientesXml(clientes)
         *
         * y envolver inmediatamente nuestra lista.
         */
        public ClientesXml(List<Cliente> clientes) {

            /*
             * this.clientes:
             *      atributo del objeto.
             *
             * clientes:
             *      parámetro recibido.
             */
            this.clientes = clientes;
        }


        /**
         * Getter.
         *
         * Permite obtener la lista almacenada en el wrapper.
         *
         * Jackson también puede utilizar los getters para
         * descubrir propiedades durante la serialización.
         */
        public List<Cliente> getClientes() {

            return clientes;
        }


        /**
         * Setter.
         *
         * Permite sustituir la lista de clientes.
         *
         * Es especialmente relevante durante la
         * deserialización, cuando Jackson reconstruye
         * el objeto a partir del XML.
         */
        public void setClientes(
                List<Cliente> clientes
        ) {

            this.clientes = clientes;
        }
    }



    // ============================================================
    // JSON
    // ============================================================


    /**
     * ============================================================
     * EXPORTAR CLIENTES A JSON
     * ============================================================
     *
     * En JSON podemos serializar directamente:
     *
     *      List<Cliente>
     *
     * sin utilizar nuestro wrapper ClientesXml.
     *
     *
     * Si tenemos:
     *
     *      clientes
     *          |
     *          +-- Ana
     *          +-- Luis
     *
     * Jackson puede producir directamente:
     *
     *      [
     *          {
     *              "id" : 1,
     *              "nombre" : "Ana",
     *              ...
     *          },
     *          {
     *              "id" : 2,
     *              "nombre" : "Luis",
     *              ...
     *          }
     *      ]
     *
     *
     * Los corchetes:
     *
     *      [ ]
     *
     * representan un ARRAY JSON.
     */
    public static void exportarClientesJson(
            Path ruta,
            List<Cliente> clientes
    ) throws IOException {


        /*
         * writeValue recibe:
         *
         *      1. fichero destino;
         *      2. objeto que queremos serializar.
         *
         *
         * En este caso el objeto es directamente:
         *
         *      clientes
         *
         * cuyo tipo es:
         *
         *      List<Cliente>
         *
         *
         * Jackson recorrerá la lista y después inspeccionará
         * cada Cliente para obtener sus propiedades.
         */
        MAPPER.writeValue(
                ruta.toFile(),
                clientes
        );
    }



    /**
     * ============================================================
     * IMPORTAR CLIENTES DESDE JSON
     * ============================================================
     *
     * El proceso es:
     *
     *      JSON
     *       ↓
     *    Jackson
     *       ↓
     *   List<Cliente>
     *
     *
     * Aquí aparece una diferencia muy interesante respecto
     * al XML anterior.
     */
    public static List<Cliente> importarClientesJson(
            Path ruta
    ) throws IOException {


        /*
         * Podríamos pensar inicialmente en escribir:
         *
         *      List<Cliente>.class
         *
         * pero eso NO existe en Java.
         *
         *
         * El problema está relacionado con los genéricos.
         *
         * En tiempo de ejecución necesitamos comunicar
         * a Jackson que queremos exactamente:
         *
         *      una List
         *
         * cuyos elementos sean:
         *
         *      Cliente
         *
         *
         * Para conservar esa información utilizamos:
         *
         *      TypeReference<List<Cliente>>
         *
         *
         * Es decir, TypeReference proporciona a Jackson
         * información más detallada que simplemente:
         *
         *      List.class
         *
         *
         * Porque List.class solamente indicaría:
         *
         *      "quiero una lista"
         *
         * pero no:
         *
         *      "quiero una lista de Cliente"
         */
        return MAPPER.readValue(

                /*
                 * Primer argumento:
                 *
                 * fichero JSON que vamos a leer.
                 */
                ruta.toFile(),


                /*
                 * Segundo argumento:
                 *
                 * descripción completa del tipo esperado:
                 *
                 *      List<Cliente>
                 *
                 *
                 * Las llaves:
                 *
                 *      {}
                 *
                 * crean una clase anónima derivada de
                 * TypeReference.
                 *
                 * Esta técnica permite conservar la información
                 * del tipo genérico para que Jackson pueda
                 * reconstruir correctamente cada Cliente.
                 */
                new TypeReference<List<Cliente>>() {
                }
        );
    }
}