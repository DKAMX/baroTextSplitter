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
  private JFrame window = new JFrame(I18N.getString("title"));
  // text area for display process output
  private JTextArea textArea = new JTextArea(I18N.getString("welcome"));
  private JScrollPane textpanel = new JScrollPane(textArea);
  // panel for placing buttons
  private JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
  private JButton openB = new JButton(I18N.getString("openFile"));
  private JButton exportB = new JButton(I18N.getString("exportFile"));
  private JButton clearB = new JButton(I18N.getString("clearOutput"));
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
    exportB.addActionListener(e -> parser.export());
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

  /**
   * Ask user to select among given options.
   * 
   * @param message message of the dialog
   * @param title   title of the dialog
   * @param options set of option to choose
   * @return number indicate the choice, -1 if no options has been choosen
   */
  public int showOptionDialog(Object message, String title, Object[] options) {
    return JOptionPane.showOptionDialog(window, message,
        title, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
        null, options, null);
  }

  /**
   * Ask user for file to be opened.
   * 
   * @return file to be opened
   */
  public File showOpenDialog() {
    int state = fileChooser.showOpenDialog(window);
    parser.updateTraversalPosition(fileChooser.getCurrentDirectory());
    if (state == JFileChooser.CANCEL_OPTION) {
      return null;
    }
    return fileChooser.getSelectedFile();
  }

  /**
   * Ask user for file location to save.
   * 
   * @return location to save
   */
  public File showSaveDialog() {
    int state = fileChooser.showOpenDialog(window);
    parser.updateTraversalPosition(fileChooser.getCurrentDirectory());
    if (state == JFileChooser.CANCEL_OPTION) {
      return null;
    }
    return fileChooser.getSelectedFile();
  }
}