import java.io.File;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Max
 */
public class XMLParser {
  private static final String AFFLICTIONS = "Afflictions";
  private static final String ITEMS = "Items";
  private static final String MISSIONS = "Missions";

  private static final String description = "description";
  private static final String identifier = "identifier";
  private static final String name = "name";

  // remember the last position when reopen the file chooser
  public File lastPosition = Paths.get(".").toFile();

  private JFileChooser fc = new JFileChooser(lastPosition);
  // Swing Component that used by file chooser;
  private GUI window;

  public XMLParser(GUI window) {
    this.window = window;
  }

  public void parse() {
    fc.showOpenDialog(window.getParentWindow());
    // remember current filepath
    File currentDirectory = fc.getCurrentDirectory();
    if (currentDirectory != null) {
      this.lastPosition = currentDirectory;
    }
    // read filepath from file chooser
    File filepath = fc.getSelectedFile();
    if (filepath != null) {
      window.printText(filepath.toString());
      parse(filepath);
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
      parseNode(doc.getDocumentElement().getChildNodes());
    } catch (Exception e) {
      System.err.println(e);
    }
  }

  private void parseNode(NodeList nodes) {
    if (nodes.getLength() == 0) {
      return;
    }
    for (int i = 0; i < nodes.getLength(); i++) {
      Node currentNode = nodes.item(i);
      if (currentNode.getNodeType() != Node.ELEMENT_NODE) {
        continue;
      }
      String result = splitInfo(currentNode);
      if (result == null) {
        continue;
      }
      window.printText(result);
    }
  }

  private String splitInfo(Node node) {
    NamedNodeMap attributes = node.getAttributes();
    String result = "";
    Node identifierAttr = attributes.getNamedItem(XMLParser.identifier);
    if (identifierAttr == null) {
      return null;
    }
    result += identifierAttr.getNodeValue() + ", ";

    Node nameAttr = attributes.getNamedItem(XMLParser.name);
    if (nameAttr == null) {
      result += "null, ";
    } else {
      result += nameAttr.getNodeValue() + ", ";
    }

    Node descriptionAttr = attributes.getNamedItem(XMLParser.description);
    if (descriptionAttr == null) {
      result += "null";
    } else {
      result += descriptionAttr.getNodeValue();
    }

    return result;
  }
}