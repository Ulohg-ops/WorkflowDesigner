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

    // 左側工具面板，提供模式切換與工具按鈕
    private ToolPanel toolPanel;
    // 中央畫布，用於繪製物件與連線
    private Canvas canvas;

    /**
     * 建構子：初始化主視窗，設置版面配置、工具面板、畫布與選單列。
     */
    public MainFrame() {
        // 設定視窗標題
        super("Workflow Design Editor");
        // 設定視窗關閉時退出程式
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 使用 BorderLayout 分佈版面
        setLayout(new BorderLayout());

        // 初始化並加入工具面板到左側
        toolPanel = new ToolPanel();
        add(toolPanel, BorderLayout.WEST);

        // 建立模型，包含所有繪製物件與連線
        CanvasModel model = new CanvasModel();

        // 建立畫布，傳入工具面板與模型供畫布操作
        canvas = new Canvas(toolPanel, model);
        add(canvas, BorderLayout.CENTER);

        // 設定選單列（包含 File 與 Edit 選單）
        setJMenuBar(createMenuBar());

        // 設定視窗大小與置中顯示
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

        // ===== File Menu =====
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        // 按下 Exit 時直接結束程式
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // ===== Edit Menu =====
        JMenu editMenu = new JMenu("Edit");
        JMenuItem groupItem = new JMenuItem("Group");
        JMenuItem unGroupItem = new JMenuItem("Ungroup");
        JMenuItem customLabelItem = new JMenuItem("Custom Label Style");

        // 群組：呼叫畫布的 groupSelectedObjects() 方法
        groupItem.addActionListener(e -> canvas.groupSelectedObjects());
        // 解群組：呼叫畫布的 ungroupSelectedObject() 方法
        unGroupItem.addActionListener(e -> canvas.ungroupSelectedObject());

        // 自訂標籤樣式：彈出 CustomLabelDialog 讓使用者修改標籤設定
        customLabelItem.addActionListener(e -> {
            // 取得目前選取的物件列表
            List<BasicObject> selected = canvas.getSelectedObjects();
            // 只允許對一個物件進行標籤設定
            if (selected.size() == 1) {
                BasicObject obj = selected.get(0);

                // 建立自訂標籤對話框，傳入當前物件的標籤設定
                CustomLabelDialog dialog = new CustomLabelDialog(
                        MainFrame.this,
                        obj.getLabel(),
                        obj.getLabelShape(),
                        obj.getLabelColor(),
                        obj.getFontSize()
                );
                // 顯示對話框（模態對話框，會阻塞後續操作）
                dialog.setVisible(true);

                // 如果使用者確認 (按下 OK)，則更新物件的標籤設定
                if (dialog.isConfirmed()) {
                    obj.setLabel(dialog.getLabelName());
                    obj.setLabelShape(dialog.getLabelShape());
                    obj.setLabelColor(dialog.getChosenColor());
                    obj.setFontSize(dialog.getFontSize());
                    // 更新畫布顯示
                    canvas.repaint();
                }
            } else {
                // 若沒有或選取多個物件，則彈出提示訊息
                JOptionPane.showMessageDialog(
                        MainFrame.this,
                        "請先選取一個物件",
                        "提示",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        // 將 Edit Menu 的選項加入選單列
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
