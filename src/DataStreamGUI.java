import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataStreamGUI extends JFrame {
    private JTextArea originalTextArea;
    private JTextArea filteredTextArea;
    private JTextField searchField;
    private JButton loadButton;
    private JButton searchButton;
    private JButton quitButton;
    private Path currentFilePath;

    public DataStreamGUI() {
        setTitle("Data Stream File Filter");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeComponents();
        layoutComponents();
        addEventHandlers();

        searchButton.setEnabled(false);
    }

    private void initializeComponents() {
        originalTextArea = new JTextArea();
        originalTextArea.setEditable(false);
        originalTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        filteredTextArea = new JTextArea();
        filteredTextArea.setEditable(false);
        filteredTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        searchField = new JTextField(20);

        loadButton = new JButton("Load File");
        searchButton = new JButton("Search File");
        quitButton = new JButton("Quit");
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search String:"));
        topPanel.add(searchField);
        topPanel.add(loadButton);
        topPanel.add(searchButton);
        topPanel.add(quitButton);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JLabel("Original File:"), BorderLayout.NORTH);
        JScrollPane leftScroll = new JScrollPane(originalTextArea);
        leftPanel.add(leftScroll, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(new JLabel("Filtered Results:"), BorderLayout.NORTH);
        JScrollPane rightScroll = new JScrollPane(filteredTextArea);
        rightPanel.add(rightScroll, BorderLayout.CENTER);

        centerPanel.add(leftPanel);
        centerPanel.add(rightPanel);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void addEventHandlers() {
        loadButton.addActionListener(e -> loadFile());
        searchButton.addActionListener(e -> searchFile());
        quitButton.addActionListener(e -> System.exit(0));

        searchField.addActionListener(e -> {
            if (searchButton.isEnabled()) {
                searchFile();
            }
        });
    }

    private void loadFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a Text File");

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            currentFilePath = fileChooser.getSelectedFile().toPath();

            try {
                String content = Files.readString(currentFilePath);
                originalTextArea.setText(content);
                filteredTextArea.setText("");
                searchButton.setEnabled(true);

                JOptionPane.showMessageDialog(this,
                        "File loaded successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error loading file: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                searchButton.setEnabled(false);
            }
        }
    }

    private void searchFile() {
        String searchString = searchField.getText().trim();

        if (searchString.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a search string!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentFilePath == null) {
            JOptionPane.showMessageDialog(this,
                    "Please load a file first!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Stream<String> lines = Files.lines(currentFilePath)) {
            String filteredContent = lines
                    .filter(line -> line.contains(searchString))
                    .collect(Collectors.joining("\n"));

            if (filteredContent.isEmpty()) {
                filteredTextArea.setText("No lines found containing: " + searchString);
            } else {
                filteredTextArea.setText(filteredContent);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error searching file: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DataStreamGUI gui = new DataStreamGUI();
            gui.setVisible(true);
        });
    }
}
