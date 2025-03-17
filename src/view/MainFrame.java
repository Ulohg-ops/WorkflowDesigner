package view;

import model.BasicObject;
import model.CanvasModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {

    private ToolPanel toolPanel;
    private Canvas canvas;

    public MainFrame() {
        super("Workflow Design Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. 建立 ToolPanel (左側)
        toolPanel = new ToolPanel();
        add(toolPanel, BorderLayout.WEST);

        // 2. 建立 Model
        CanvasModel model = new CanvasModel();

        // 3. 建立 Canvas (中央)，傳入 toolPanel & model
        canvas = new Canvas(toolPanel, model);
        add(canvas, BorderLayout.CENTER);

        // 4. 建立功能選單 (上方)
        setJMenuBar(createMenuBar());

        // 視窗大小 & 位置
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    /**
     * 建立選單列 (JMenuBar)
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // Edit Menu
        JMenu editMenu = new JMenu("Edit");
        JMenuItem groupItem = new JMenuItem("Group");
        JMenuItem unGroupItem = new JMenuItem("Ungroup");
        JMenuItem customLabelItem = new JMenuItem("Custom Label Style");

        // 群組
        groupItem.addActionListener(e -> canvas.groupSelectedObjects());
        // 解群組
        unGroupItem.addActionListener(e -> canvas.ungroupSelectedObject());

        // 自訂標籤樣式
        customLabelItem.addActionListener(e -> {
            // 取得目前選取的物件
            List<BasicObject> selected = canvas.getSelectedObjects();
            if (selected.size() == 1) {
                BasicObject obj = selected.get(0);

                // 彈出自訂標籤對話框
                CustomLabelDialog dialog = new CustomLabelDialog(
                        MainFrame.this,
                        obj.getLabel(),
                        obj.getLabelShape(),
                        obj.getLabelColor(),
                        obj.getFontSize()
                );
                dialog.setVisible(true);

                // 如果按下 OK，更新該物件的 Label 設定
                if (dialog.isConfirmed()) {
                    obj.setLabel(dialog.getLabelName());
                    obj.setLabelShape(dialog.getLabelShape());
                    obj.setLabelColor(dialog.getChosenColor());
                    obj.setFontSize(dialog.getFontSize());
                    canvas.repaint();
                }
            } else {
                JOptionPane.showMessageDialog(
                        MainFrame.this,
                        "請先選取一個物件",
                        "提示",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        // 將選單項目加入 Edit Menu
        editMenu.add(groupItem);
        editMenu.add(unGroupItem);
        editMenu.add(customLabelItem);
        menuBar.add(editMenu);

        return menuBar;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
