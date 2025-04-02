package view;

import model.BasicObject;
import model.CanvasModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * MainFrame 是整個應用程式的主要視窗，
 * 包含工具面板、畫布以及選單列，負責初始化與配置應用程式的主要組件。
 */
public class MainFrame extends JFrame {

    private ToolPanel toolPanel;
    private Canvas canvas;

    /**
     * 建構子：初始化主視窗，設置版面配置、工具面板、畫布與選單列。
     */
    public MainFrame() {
        super("Workflow Design Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        toolPanel = new ToolPanel();
        add(toolPanel, BorderLayout.WEST);

        CanvasModel model = new CanvasModel();

        canvas = new Canvas(toolPanel, model);
        add(canvas, BorderLayout.CENTER);

        setJMenuBar(createMenuBar());

        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    /**
     * 建立選單列 (JMenuBar)，包含 File 與 Edit 選單。
     *
     * File 選單目前只有 Exit 選項；Edit 選單提供群組、解群組與自訂標籤樣式功能。
     *
     * @return 建立好的 JMenuBar 物件
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem groupItem = new JMenuItem("Group");
        JMenuItem unGroupItem = new JMenuItem("Ungroup");
        JMenuItem customLabelItem = new JMenuItem("Custom Label Style");

        groupItem.addActionListener(e -> canvas.groupSelectedObjects());
        unGroupItem.addActionListener(e -> canvas.ungroupSelectedObject());

        customLabelItem.addActionListener(e -> {
            List<BasicObject> selected = canvas.getSelectedObjects();
            if (selected.size() == 1) {
                BasicObject obj = selected.get(0);

                CustomLabelDialog dialog = new CustomLabelDialog(
                        MainFrame.this,
                        obj.getLabel(),
                        obj.getLabelShape(),
                        obj.getLabelColor(),
                        obj.getFontSize()
                );
                dialog.setVisible(true);

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

        editMenu.add(groupItem);
        editMenu.add(unGroupItem);
        editMenu.add(customLabelItem);
        menuBar.add(editMenu);

        return menuBar;
    }

    /**
     * 主方法：使用 SwingUtilities.invokeLater 來啟動 GUI 程式，
     * 確保 GUI 相關操作在事件分派緒中執行。
     *
     * @param args 命令列參數
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
