package view;

import controller.CanvasController;
import model.BasicObject;
import model.CanvasModel;
import model.LinkObject;

import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

/**
 * Canvas 類別繼承自 JPanel，負責在畫布上繪製圖形物件與連線
 * 並處理相關的滑鼠事件與輔助繪製。
 */
public class Canvas extends JPanel {
    public static final int DEFAULT_WIDTH = 120;
    public static final int DEFAULT_HEIGHT = 80;

    private ToolPanel toolPanel;
    private CanvasModel model;
    private CanvasController controller;

    /**
     * 建構子，初始化工具面板、模型與控制器，並設定背景顏色與滑鼠監聽器。
     *
     * @param toolPanel 工具面板
     * @param model     畫布模型，包含所有物件與連線
     */
    public Canvas(ToolPanel toolPanel, CanvasModel model) {
        this.toolPanel = toolPanel;
        this.model = model;
        setBackground(Color.WHITE);

        this.controller = new CanvasController(toolPanel, model, this);
        addMouseListener(controller);
        addMouseMotionListener(controller);
    }

    /**
     * 取得目前被選取的物件列表。
     *
     * @return 被選取的 BasicObject 列表
     */
    public List<BasicObject> getSelectedObjects() {
        return model.getSelectedObjects();
    }

    /**
     * 覆寫 paintComponent 方法，根據物件與連線的 depth 排序後進行繪製。
     * 繪製順序為：未選取物件 → 連線 → 選取物件。
     *
     * @param g Graphics 物件
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 建立一個新列表存放物件與連線
        List<Object> drawList = new ArrayList<>();

        // 將所有物件加入 drawList
        drawList.addAll(model.getObjects());
        // 將所有連線加入 drawList
        drawList.addAll(model.getLinks());

        // 依 depth 進行排序 (depth 較大的先畫，較小的在上層)
        drawList.sort((o1, o2) -> {
            int depth1 = (o1 instanceof BasicObject) ? ((BasicObject) o1).getDepth() : ((LinkObject) o1).getDepth();
            int depth2 = (o2 instanceof BasicObject) ? ((BasicObject) o2).getDepth() : ((LinkObject) o2).getDepth();
            return Integer.compare(depth2, depth1); // 降序排列
        });

        // 依照排序後的順序繪製
        for (Object obj : drawList) {
            if (obj instanceof BasicObject) {
                ((BasicObject) obj).draw(g);
            } else if (obj instanceof LinkObject) {
                ((LinkObject) obj).draw(g);
            }
        }

        // 繪製其他輔助指引 (例如拖曳時的輔助線)
        controller.drawAdditionalGuides(g);
    }

    /**
     * 將目前選取的物件進行群組化。
     */
    public void groupSelectedObjects() {
        controller.groupSelectedObjects();
    }

    /**
     * 將群組物件解散，取消群組狀態。
     */
    public void ungroupSelectedObject() {
        controller.ungroupSelectedObject();
    }

    /**
     * 取得目前畫布的模型。
     *
     * @return CanvasModel 模型
     */
    public CanvasModel getModel() {
        return model;
    }
}
