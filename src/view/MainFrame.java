package view;

import model.BasicObject;
import java.util.List;
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

        // 建立功能選單 (上方)
        setJMenuBar(createMenuBar());


        setSize(800, 600);
        setLocationRelativeTo(null);
    }

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

        groupItem.addActionListener(e -> canvas.groupSelectedObjects());
        unGroupItem.addActionListener(e -> canvas.ungroupSelectedObject());

        customLabelItem.addActionListener(e -> {
            // 取得目前選取的物件
            List<BasicObject> selected = canvas.getSelectedObjects();
            if (selected.size() == 1) {
                BasicObject obj = selected.get(0);
                // 建立並顯示 CustomLabelDialog，傳入 MainFrame 作為 owner
                CustomLabelDialog dialog = new CustomLabelDialog(
                        MainFrame.this,
                        obj.getLabel(),
                        obj.getLabelShape(),
                        obj.getLabelColor(),
                        obj.getFontSize()
                );
                dialog.setVisible(true);
                if (dialog.isConfirmed()) {
                    // 更新物件的 Label 設定
                    obj.setLabel(dialog.getLabelName());
                    obj.setLabelShape(dialog.getLabelShape());
                    obj.setLabelColor(dialog.getChosenColor());
                    obj.setFontSize(dialog.getFontSize());
                    canvas.repaint();
                }
            } else {
                JOptionPane.showMessageDialog(MainFrame.this, "請先選取一個物件", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });



        // 這裡可註冊事件或呼叫對應的 controller 方法
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
