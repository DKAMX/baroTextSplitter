import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author Max
 */
public class GUI {
  // corresponding XMLParser
  private XMLSplitter parser = new XMLSplitter(this);

  // main window
  private JFrame window = new JFrame("Barotrauma Text Splitter");
  // text area for display process output
  private JTextArea textArea = new JTextArea();
  private JScrollPane textpanel = new JScrollPane(textArea);
  // panel for placing buttons
  private JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
  private JButton openB = new JButton("Open File");
  private JButton exportB = new JButton("Export Text");
  private JButton clearB = new JButton("Clear Output");
  // file chooser used by XMLParser
  private JFileChooser fileChooser = new JFileChooser(parser.lastPosition);

  public static void main(String[] args) {
    // set visual style
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      System.err.println(e);
    }
    // start GUI
    SwingUtilities.invokeLater(() -> new GUI().start());
  }

  /**
   * assemble panels and display
   */
  public void start() {
    // set window
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setLocationByPlatform(true);
    window.setMinimumSize(new Dimension(640, 480));
    // set text area
    textArea.setEditable(false);
    // bind button action
    openB.addActionListener(e -> parser.parse());
    exportB.addActionListener(e -> {
      parser.export();
    });
    clearB.addActionListener(e -> textArea.setText(null));
    // add button to panel
    buttonPanel.add(openB);
    buttonPanel.add(exportB);
    buttonPanel.add(clearB);
    // only show xml file in the file chooser
    fileChooser.setFileFilter(new FileNameExtensionFilter("xml file", new String[] { "xml", "XML" }));

    window.add(buttonPanel, BorderLayout.NORTH);
    window.add(textpanel, BorderLayout.CENTER);
    window.setVisible(true);
  }

  /**
   * @param text text message to print
   */
  public void printText(String text) {
    textArea.append(text + System.lineSeparator());
  }

  public int showOptionDialog(Object message, String title, Object[] options) {
    return JOptionPane.showOptionDialog(window, message,
        title, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
        null, options, null);
  }

  public File showOpenDialog() {
    fileChooser.showOpenDialog(window);
    parser.updateTraversalPosition(fileChooser.getCurrentDirectory());
    return fileChooser.getSelectedFile();
  }

  public File showSaveDialog() {
    fileChooser.showOpenDialog(window);
    parser.updateTraversalPosition(fileChooser.getCurrentDirectory());
    return fileChooser.getSelectedFile();
  }
}