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


public class Canvas extends JPanel {
    public static final int DEFAULT_WIDTH = 120;
    public static final int DEFAULT_HEIGHT = 80;

    private ToolPanel toolPanel;
    private CanvasModel model;
    private CanvasController controller;

    public Canvas(ToolPanel toolPanel, CanvasModel model) {
        this.toolPanel = toolPanel;
        this.model = model;
        setBackground(Color.WHITE);

        this.controller = new CanvasController(toolPanel, model, this);

        addMouseListener(controller);
        addMouseMotionListener(controller);
    }

    public List<BasicObject> getSelectedObjects() {
        return model.getSelectedObjects();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        List<BasicObject> objs = new ArrayList<>(model.getObjects());
        // 假設 depth 越小表示越在上層，因此我們希望 depth 較小的物件後畫（最後畫的在最上層）
        objs.sort((o1, o2) -> Integer.compare(o2.getDepth(), o1.getDepth()));

        // 使用排序後的清單進行繪製
        for (BasicObject obj : objs) {
            obj.draw(g);
        }
        for (LinkObject link : model.getLinks()) {
            link.draw(g);
        }
        controller.drawAdditionalGuides(g);
    }

    public void groupSelectedObjects() {
        controller.groupSelectedObjects();
    }

    public void ungroupSelectedObject() {
        controller.ungroupSelectedObject();
    }

    public CanvasModel getModel() {
        return model;
    }


}
