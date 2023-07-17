import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Max
 */
public class XMLSplitter {
  // element type and parameter defined by Barotrauma
  public static final Map<String, Set<String>> elementMap = new HashMap<>();
  // parameter specific for language setting, 0(nowhitespace), 1(translatedname)
  public static final Map<String, String[]> languageSetting = new HashMap<>();

  static {
    // initialize element constant
    elementMap.put("Affliction",
        Set.of("afflictionname", "afflictiondescription",
            "afflictioncauseofdeath", "afflictioncauseofdeathself"));
    elementMap.put("Items",
        Set.of("entityname", "entitydescription"));
    elementMap.put("Missions",
        Set.of("missionname", "missiondescription",
            "missionsuccess", "missionfailure",
            "missionmessage0", "missionmessage1"));
    // initialize language specific parameter
    languageSetting.put("Simplified Chinese",
        new String[] { "true", "中文(简体)" });
    languageSetting.put("English",
        new String[] { "false", "English" });
  }

  // remember the last position when reopen the file chooser
  public File lastPosition = Paths.get(".").toFile();

  // Swing Component that used by file chooser;
  private GUI window;

  // current element data
  private String elementType = "";
  private List<String[]> elements = new ArrayList<>();

  public XMLSplitter(GUI window) {
    this.window = window;
  }

  public void parse() {
    // read filepath from file chooser
    File readpath = window.showOpenDialog();
    if (readpath != null) {
      window.printText(readpath.toString());
      parse(readpath);
    }
  }

  public void export() {
    if (elements.isEmpty()) {
      window.printText("File not opened");
      return;
    }
    File savepath = window.showSaveDialog();
    if (savepath != null) {
      int confirm = 0; // 0(Yes)
      if (savepath.exists()) {
        String message = savepath.getName() + " already exists, want to replace it?";
        confirm = window.showOptionDialog(message, "Confirm", new String[] { "Yes", "No" });
      }
      if (confirm == 0) {
        String[] languageOptions = new String[] { "Simplified Chinese", "English" };
        int choice = window.showOptionDialog("Select export language", "Select", languageOptions);
        if (choice < 0) {
          window.printText("Export file canceled");
          return;
        }
        export(savepath, languageOptions[choice]);
      } else {
        window.printText("Export file canceled");
      }
    }
  }

  /**
   * Remember current position of file traversal.
   */
  public void updateTraversalPosition(File currentDirectory) {
    if (currentDirectory != null) {
      lastPosition = currentDirectory;
    }
  }

  /**
   * @param filepath path to XML file
   */
  private void parse(File filepath) {
    try {
      // setup a parser for XML
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(filepath);
      elementType = doc.getDocumentElement().getNodeName();
      elements = parseNode(doc.getDocumentElement().getChildNodes());
    } catch (Exception e) {
      System.err.println(e);
      window.printText("Parse file failed");
    }
  }

  private List<String[]> parseNode(NodeList nodes) throws Exception {
    if (nodes.getLength() == 0) {
      throw new IllegalArgumentException("empty nodes");
    }
    List<String[]> elements = new ArrayList<>();
    for (int i = 0; i < nodes.getLength(); i++) {
      Node currentNode = nodes.item(i);
      if (currentNode.getNodeType() != Node.ELEMENT_NODE) {
        continue;
      }
      String[] result = splitInfo(currentNode);
      if (result == null) {
        continue;
      }
      elements.add(result);
      window.printText(result[0]);
    }
    return elements;
  }

  /**
   * Split target parameters from given node.
   * 
   * @param node
   * @return the parameters stored as an array
   */
  private String[] splitInfo(Node node) {
    NamedNodeMap attributes = node.getAttributes();
    Node identifierAttr = attributes.getNamedItem("identifier");
    if (identifierAttr == null) {
      return null;
    }
    Node nameAttr = attributes.getNamedItem("name");
    Node descriptionAttr = attributes.getNamedItem("description");
    return new String[] {
        identifierAttr.getNodeValue(),
        nameAttr != null ? nameAttr.getNodeValue() : "",
        descriptionAttr != null ? descriptionAttr.getNodeValue() : ""
    };
  }

  private void export(File savepath, String language) {
    try {
      Element root = createRootElement(language);
      appendChildElement(root, elements);

      DOMSource ds = new DOMSource(root);
      TransformerFactory tf = TransformerFactory.newInstance();

      Transformer t = tf.newTransformer();
      t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      t.setOutputProperty(OutputKeys.INDENT, "yes");
      t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

      Result result = new StreamResult(savepath);
      t.transform(ds, result);

      window.printText("Export file to " + savepath.getAbsolutePath());
    } catch (Exception e) {
      e.printStackTrace();
      window.printText("Export file failed");
    }
  }

  /**
   * @return a DOM object with a root element
   * @throws ParserConfigurationException
   */
  private Element createRootElement(String language)
      throws ParserConfigurationException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document doc = db.newDocument();
    Element root = doc.createElement("infotexts");
    String[] setting = languageSetting.get(language);
    root.setAttribute("language", language);
    root.setAttribute("nowhitespace", setting[0]);
    root.setAttribute("translatedname", setting[1]);
    return root;
  }

  /**
   * Create child node based on given element list and append to given root node.
   * 
   * @param root     root node
   * @param elements element list
   */
  private void appendChildElement(Element root, List<String[]> elements) {
    elements.forEach(e -> {
      Document doc = root.getOwnerDocument();
      elementMap.get(elementType).forEach(a -> {
        Element node = doc.createElement(a + "." + e[0]);
        node.appendChild(doc.createTextNode(" "));
        root.appendChild(node);
      });
    });
  }
}