package view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private ToolPanel toolPanel;
    private Canvas canvas;

    public MainFrame() {
        super("Workflow Design Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 建立 ToolPanel (左側)
        toolPanel = new ToolPanel();
        add(toolPanel, BorderLayout.WEST);

        // 建立 Canvas (中央)，將 toolPanel 傳進去以便取得當前模式
        canvas = new Canvas(toolPanel);
        add(canvas, BorderLayout.CENTER);

        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
