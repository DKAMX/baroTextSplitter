import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * @author DKAMX
 * @version 0.1
 */
public class textsplit {
    static String welcome = "欢迎使用潜渊症文本分离器! Code by DKAMX\n可用指令: 读取XML文件[file] 结束程序[end]";
    static String askfile = "将文件和本程序放在同一层目录下，输入文件的名称(包括后缀名.xml)";
    static String donefile = "在同目录下输出了一个名为output的XML文件";

    public static void main(String[] args) throws Exception { // throws as methods demand
        String typing;
        BufferedReader echo = new BufferedReader(new InputStreamReader(System.in));
        do {
            System.out.println(welcome);
            typing = echo.readLine();
            if (typing.equalsIgnoreCase("file")) {
                System.out.println(askfile);
                String filepath = echo.readLine();
                filepath = filepath.replaceAll("\\\\", "/");

                try {
                    // setup a parser for XML
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document doc = db.parse(new File(filepath));

                    // setup for writing file
                    File output = new File("output.xml");
                    FileOutputStream fOut = new FileOutputStream(output);
                    OutputStreamWriter writer = new OutputStreamWriter(fOut, "UTF-8");

                    // read from the NodeList
                    NodeList items = doc.getDocumentElement().getChildNodes();
                    // iterate NodeList and write values
                    writer.append("<infotexts>\n");
                    for (int i = 0; i < items.getLength(); i++) {
                        // avoid #text node (line feed)
                        if (!items.item(i).getNodeName().equals("#text")) {
                            String name = items.item(i).getAttributes().getNamedItem("name").getNodeValue();
                            String identifier = items.item(i).getAttributes().getNamedItem("identifier").getNodeValue();
                            String description = items.item(i).getAttributes().getNamedItem("description").getNodeValue();
                            System.out.println(
                                    "name: " + name + ", identifier:" + identifier + ", description:" + description);
                            writer.append(
                                    "    <entityname." + identifier + ">" + name + "</entityname." + identifier
                                            + ">\n");
                            writer.append("    <entitydescription." + identifier + ">" + name + "</entitydescription."
                                    + identifier + ">\n");
                        }
                    }
                    writer.append("</infotexts>");
                    writer.close();
                    fOut.close();
                    System.out.println(donefile);
                } catch (IOException except) { // handle for input wrong filename
                    System.out.println(except.getLocalizedMessage());
                    continue;
                }
            }
        } while (!typing.equalsIgnoreCase("end"));
    }
}