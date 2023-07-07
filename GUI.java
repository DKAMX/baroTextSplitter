import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * @author Max
 */
public class GUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI().startup());
    }

    public void startup() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // setup main window
        JFrame window = new JFrame("baro Text Splitter");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationByPlatform(true);
        window.setMinimumSize(new Dimension(480, 360));

        // text area for display process output
        JTextArea text = new JTextArea();
        text.setEditable(false);
        JScrollPane textpanel = new JScrollPane(text);

        // setup button panel and buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0));

        JButton openB = new JButton("Open File");
        JButton exportB = new JButton("Export Text");
        JButton clearB = new JButton("Clear Output");
        clearB.addActionListener(e -> text.setText(null));

        buttonPanel.add(openB);
        buttonPanel.add(exportB);
        buttonPanel.add(clearB);

        // assemble panels and display
        window.add(buttonPanel, BorderLayout.NORTH);
        window.add(textpanel, BorderLayout.CENTER);
        window.setVisible(true);
    }
}