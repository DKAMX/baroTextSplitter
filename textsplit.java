import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author DKAMX
 * @version 0.1
 */
public class textsplit {
    static String welcome = "欢迎使用潜渊症文本分离器! Code by DKAMX\n可用指令: 读取XML文件[file] 结束程序[end]";
    static String askfile = "将文件和本程序放在同一层目录下, 输入文件的名称(包括后缀名.xml), 目前支持的XML元素: Affliction, Item";
    static String donefile = "在同目录下输出了一个XML文件: ";

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
                    File file = new File(filepath);
                    Document doc = db.parse(file);

                    // setup for writing file with time and date
                    File output = new File(textsplit.getDate() + "text_" + file.getName());
                    FileOutputStream fOut = new FileOutputStream(output);
                    OutputStreamWriter writer = new OutputStreamWriter(fOut, "UTF-8");

                    // read from the NodeList
                    NodeList items = doc.getDocumentElement().getChildNodes();
                    // iterate NodeList and write values
                    writer.append("<infotexts>\n");
                    for (int i = 0; i < items.getLength(); i++) {
                        // avoid #text node (line feed)
                        String nodeName = items.item(i).getNodeName();
                        if (nodeName.equals("Item") || nodeName.equals("Affliction")) {
                            String name = null, identifier = null, description = null;
                            Node name_node = items.item(i).getAttributes().getNamedItem("name");
                            if (name_node != null) {
                                name = name_node.getNodeValue();
                            }
                            Node identifier_node = items.item(i).getAttributes().getNamedItem("identifier");
                            if (identifier_node != null) {
                                identifier = identifier_node.getNodeValue();
                            }
                            Node description_node = items.item(i).getAttributes().getNamedItem("description");
                            if (description_node != null) {
                                description = description_node.getNodeValue();
                            }
                            System.out.println(
                                    "name: " + name + ", identifier:" + identifier + ", description:"
                                            + description);
                            if (items.item(i).getNodeName().equals("Item")) {
                                writer.append(
                                        "    <entityname." + identifier + ">" + name + "</entityname." + identifier
                                                + ">\n");
                                writer.append(
                                        "    <entitydescription." + identifier + ">" + name + "</entitydescription."
                                                + identifier + ">\n");
                            }
                            if (items.item(i).getNodeName().equals("Affliction")) {
                                writer.append(
                                        "    <afflictionname." + identifier + ">" + name + "</afflictionname."
                                                + identifier
                                                + ">\n");
                                writer.append(
                                        "    <afflictiondescription." + identifier + ">" + name
                                                + "</afflictiondescription."
                                                + identifier + ">\n");
                            }
                        }
                    }
                    writer.append("</infotexts>");
                    writer.close();
                    fOut.close();
                    System.out.println(donefile + output);
                } catch (IOException except) { // handle for input wrong filename
                    System.out.println(except.getLocalizedMessage());
                    continue;
                }
            }
        } while (!typing.equalsIgnoreCase("end"));
    }

    // static method that helpful
    public static String getDate() {
        Calendar clock = Calendar.getInstance();
        int year = clock.get(Calendar.YEAR);
        int month = clock.get(Calendar.MONTH);
        int day = clock.get(Calendar.DATE);
        String date = String.valueOf(year) + String.valueOf(month) + String.valueOf(day);
        return date;
    }
}