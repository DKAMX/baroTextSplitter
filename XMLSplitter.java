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
    elementMap.put("Afflictions",
        Set.of("afflictionname", "afflictiondescription",
            "afflictioncauseofdeath", "afflictioncauseofdeathself"));
    elementMap.put("Items",
        Set.of("entityname", "entitydescription"));
    elementMap.put("Missions",
        Set.of("missionname", "missiondescription",
            "missionsuccess", "missionfailure",
            "missionmessage0", "missionmessage1"));
    // initialize language specific parameter
    languageSetting.put(I18N.getString("simplifiedChinese"),
        new String[] { "true", "中文(简体)" });
    languageSetting.put(I18N.getString("english"),
        new String[] { "false", "English" });
  }

  // remember the last position when reopen the file chooser
  public File lastPosition = Paths.get(".").toFile();

  // Swing Component that used by file chooser;
  private GUI window;

  // current element data
  private String elementType = "";
  private List<String[]> elements = new ArrayList<>();

  /**
   * Construct the splitter with a corresponding GUI.
   * 
   * @param window
   */
  public XMLSplitter(GUI window) {
    this.window = window;
  }

  public void parse() {
    // clear previous regardless whether continue next parse or not
    elements.clear();
    // read filepath from file chooser
    File readpath = window.showOpenDialog();

    if (readpath != null) {
      window.printText(readpath.toString());
      parse(readpath);
    }
  }

  public void export() {
    if (elements == null || elements.isEmpty()) {
      return;
    }

    File savepath = window.showSaveDialog();
    if (savepath != null) {
      int confirm = 0; // 0(Yes)
      // ask twice for overwrite existing file
      if (savepath.exists()) {
        String message = savepath.getName() + I18N.getString("confirmReplaceFile");
        confirm = window.showOptionDialog(message, I18N.getString("confirm"),
            new String[] { I18N.getString("yes"), I18N.getString("no") });
      } else {
        // add extention name xml if not given in the chooser
        if (!savepath.getName().endsWith(".xml") || !savepath.getName().endsWith(".XML")) {
          savepath = new File(savepath.getAbsolutePath() + ".xml");
        }
      }

      if (confirm == 0) {
        // choose export target langauge
        String[] languageOptions = languageSetting.keySet().toArray(new String[0]);
        int choice = window.showOptionDialog(I18N.getString("selectMessage"), I18N.getString("select"),
            languageOptions);

        if (choice < 0) {
          return;
        }

        export(savepath, languageOptions[choice]);
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
      // check root element for the current file, such as Item/Affliction
      elementType = doc.getDocumentElement().getNodeName();
      // parse all child nodes and save the result
      elements = parseNode(doc.getDocumentElement().getChildNodes());
    } catch (Exception e) {
      System.err.println(e);
      window.printText(I18N.getString("parseFail"));
    }
  }

  /**
   * parse given list of child nodes
   * 
   * @param nodes child nodes
   * @return list of attributes of each node
   */
  private List<String[]> parseNode(NodeList nodes) {
    if (nodes.getLength() == 0) {
      return null;
    }

    List<String[]> elements = new ArrayList<>();
    for (int i = 0; i < nodes.getLength(); i++) {
      Node currentNode = nodes.item(i);
      // skip none element node, such as text node which is useless in attribute
      // retrival
      if (currentNode.getNodeType() != Node.ELEMENT_NODE) {
        continue;
      }

      // process each node, save results and skip empty node
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
   * @param node node to be read
   * @return the parameters stored as an array
   */
  private String[] splitInfo(Node node) {
    // get identifier attribute
    NamedNodeMap attributes = node.getAttributes();
    Node identifierAttr = attributes.getNamedItem("identifier");
    // identifier cannot be null as the game also use this to identify a game object
    if (identifierAttr == null) {
      return null;
    }
    // get name and description attribute
    Node nameAttr = attributes.getNamedItem("name");
    Node descriptionAttr = attributes.getNamedItem("description");
    return new String[] {
        identifierAttr.getNodeValue(),
        nameAttr != null ? nameAttr.getNodeValue() : "",
        descriptionAttr != null ? descriptionAttr.getNodeValue() : ""
    };
  }

  /**
   * Write the parsed data to a file.
   * 
   * @param savepath saving location
   * @param language target language, such as English or Simplified Chinese
   */
  private void export(File savepath, String language) {
    try {
      // write all data to child element
      Element root = createRootElement(language);
      appendChildElement(root, elements);
      // use root element as the DOM source as root element contains all child element
      // within it
      DOMSource ds = new DOMSource(root);
      TransformerFactory tf = TransformerFactory.newInstance();
      // DOM to XML transformer's setting
      Transformer t = tf.newTransformer();
      t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      t.setOutputProperty(OutputKeys.INDENT, "yes");
      t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
      // set the result to be saved to a file
      Result result = new StreamResult(savepath);
      t.transform(ds, result);

      window.printText(I18N.getString("exportTo") + savepath.getAbsolutePath());
    } catch (Exception e) {
      e.printStackTrace();
      window.printText(I18N.getString("exportFail"));
    }
  }

  /**
   * @return a DOM object with a root element
   * @throws ParserConfigurationException
   */
  private Element createRootElement(String language)
      throws ParserConfigurationException {
    // instantiate Document object for create root element
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document doc = db.newDocument();
    // create root element with specified langauge attributes
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
      // Generate infotext element based on given root element's type, each attribute
      // use an element in infotext, the information is retrived from a predefiend
      // field. For example, an Item element has two attribute: Identifier and
      // Description, so we only need to generate element for these two.
      elementMap.get(elementType).forEach(a -> {
        Element node = doc.createElement(a + "." + e[0]);
        // leave a space inside each node, so it end up like: "<node> </node>", instead
        // of: "<node/>"
        node.appendChild(doc.createTextNode(" "));
        root.appendChild(node);
      });
    });
  }
}