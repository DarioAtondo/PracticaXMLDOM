import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        //inicialización de variables
        Scanner s = new Scanner(System.in);
        String departamento;
        double porcentaje;

        try{

            //leer el archivo xml
            File archivo = new File("src\\sales.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbf.newDocumentBuilder();
            Document doc = dBuilder.parse(archivo);

            //validación del departamento
            boolean valido = false;
            do{
                departamento = asignarDepartamento(s);
                doc.getDocumentElement().normalize();
                NodeList nodeList = doc.getElementsByTagName("sale_record");

                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node saleRecord = nodeList.item(i);

                    if (saleRecord.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) saleRecord;

                        String departmentElement = element.getElementsByTagName("department").item(0).getTextContent();

                        if (departmentElement.equalsIgnoreCase(departamento)) {
                            valido = true;
                            break;
                        }
                    }
                }
                if(!valido){
                    System.out.println("El departamento no es válido");
                }
            }while(!valido);

            //validación del porcentaje y recorrido de nodos
            porcentaje = asignarPorcentaje(s);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("sale_record");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) node;
                    String departamentos = element.getElementsByTagName("department").item(0).getTextContent();

                    if (departamentos.equalsIgnoreCase(departamento)) {
                        double ventas = Double.parseDouble(element.getElementsByTagName("sales").item(0).getTextContent());
                        double ventasAumento = ventas * (1 + (porcentaje / 100));

                        element.getElementsByTagName("sales").item(0).setTextContent(String.format("%.2f", ventasAumento));
                    }
                }
            }

            //generar el nuevo archivo
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            DOMSource source = new DOMSource(doc);
            File nuevoArchivo = new File("src\\new_sales.xml");
            StreamResult result = new StreamResult(new FileOutputStream(nuevoArchivo));
            transformer.transform(source, result);
            System.out.println("Los resultados han sido guardados en un nuevo documento xml");

        }catch(Exception e) {
            e.printStackTrace();
        }

    }

    //método para validar porcentaje
    static double asignarPorcentaje(Scanner s){
        double porcentaje;
        while(true){
            System.out.println("Ingresa un porcentaje de aumento de ventas entre 5 y 15");
            porcentaje = s.nextDouble();
            try{
                if (porcentaje >= 5 && porcentaje <= 15){
                    break;
                }else{
                    System.out.println("El porcentaje debe ser entre 5 y 15");
                }
            }catch(Exception e){
                System.out.println("Ingresa un número válido");
            }
        }
        return porcentaje;
    }

    //método para asignar departamento
    static String asignarDepartamento(Scanner s){
        System.out.println("Ingresa el departamento ");
        return s.nextLine();
    }

}
