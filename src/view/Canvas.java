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

        // 分離未選取與選取的物件
        List<BasicObject> selectedObjs = new ArrayList<>();
        List<BasicObject> unselectedObjs = new ArrayList<>();
        for (BasicObject obj : model.getObjects()) {
            if (obj.getDepth() == -1) {
                selectedObjs.add(obj);
            } else {
                unselectedObjs.add(obj);
            }
        }

        // 分離連線：確保連線畫在對應物件之上
        List<LinkObject> allLinks = new ArrayList<>(model.getLinks());

        // 確保連線畫在對應物件之上，但仍可被新建物件覆蓋
        allLinks.sort((l1, l2) -> Integer.compare(l2.getDepth(), l1.getDepth()));

        // 按 depth 降序排序（depth 小的在上層，確保新物件會在舊物件之上）
        unselectedObjs.sort((o1, o2) -> Integer.compare(o2.getDepth(), o1.getDepth()));
        selectedObjs.sort((o1, o2) -> Integer.compare(o2.getDepth(), o1.getDepth()));

        // 畫順序：
        // (1) 未選取物件
        for (BasicObject obj : unselectedObjs) {
            obj.draw(g);
        }
        // (2) 連線（確保畫在對應物件上方，但仍可被新建物件覆蓋）
        for (LinkObject link : allLinks) {
            link.draw(g);
        }
        // (3) 選取物件
        for (BasicObject obj : selectedObjs) {
            obj.draw(g);
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
