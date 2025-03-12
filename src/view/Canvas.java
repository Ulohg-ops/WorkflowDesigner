package view;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Canvas extends JPanel {
    private ToolPanel toolPanel;
    private List<BasicObject> objects = new ArrayList<>();
    private List<LinkObject> links = new ArrayList<>();

    // 拉線相關（連線模式用）
    private boolean isLinkDragging = false;
    private BasicObject linkStartObject = null;
    private Point linkStartPoint = null;
    private Point currentDragPoint = null;

    // 選取與拖曳相關（Select 模式用）
    private Point selectionStart = null;
    private Point selectionEnd = null;
    // 目前群組選取的物件集合（單一點選時，此集合只包含一個物件）
    private List<BasicObject> selectedObjects = new ArrayList<>();
    // 用於群組拖曳：記錄滑鼠拖曳起始點
    private boolean isGroupDragging = false;
    private Point groupDragStartPoint = null;
    // 記錄各選取物件的原始位置（物件左上角）
    private Map<BasicObject, Point> initialPositions = new HashMap<>();

    private static final int DEFAULT_WIDTH = 80;
    private static final int DEFAULT_HEIGHT = 60;

    public Canvas(ToolPanel toolPanel) {
        this.toolPanel = toolPanel;
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Mode mode = toolPanel.getCurrentMode();
                if (mode == Mode.RECT) {
                    // 新增矩形物件（可傳入預設 depth 或其他參數）
                    objects.add(new RectObject(e.getX(), e.getY(), DEFAULT_WIDTH, DEFAULT_HEIGHT));
                    repaint();
                } else if (mode == Mode.OVAL) {
                    // 新增橢圓物件
                    objects.add(new OvalObject(e.getX(), e.getY(), DEFAULT_WIDTH, DEFAULT_HEIGHT));
                    repaint();
                } else if (mode == Mode.ASSOCIATION || mode == Mode.GENERALIZATION || mode == Mode.COMPOSITION) {
                    // 連線模式：嘗試開始拉線
                    BasicObject startObj = findObjectAt(e.getPoint());
                    if (startObj != null) {
                        linkStartObject = startObj;
                        linkStartPoint = startObj.getClosestPort(e.getPoint());
                        isLinkDragging = true;
                        currentDragPoint = e.getPoint();
                    }
                } else if (mode == Mode.SELECT) {
                    // Select 模式：先檢查點選是否在物件上
                    BasicObject clickedObj = findObjectAt(e.getPoint());
                    if (clickedObj != null) {
                        // 若點選的物件已在選取集合中，則開始群組拖曳
                        if (!selectedObjects.contains(clickedObj)) {
                            // 若原本有選取，清除並僅選取此物件
                            selectedObjects.clear();
                            for (BasicObject obj : objects) {
                                obj.setShowPorts(false);
                            }
                            selectedObjects.add(clickedObj);
                            clickedObj.setShowPorts(true);
                        }
                        // 開始群組拖曳，記錄拖曳起點與各物件原始位置
                        isGroupDragging = true;
                        groupDragStartPoint = e.getPoint();
                        initialPositions.clear();
                        for (BasicObject obj : selectedObjects) {
                            initialPositions.put(obj, new Point(obj.x, obj.y));
                        }
                        // 清除選取區域（若有）
                        selectionStart = null;
                        selectionEnd = null;
                        repaint();
                    } else {
                        // 點選空白處：開始區域選取，並清除原有選取
                        selectionStart = e.getPoint();
                        selectionEnd = e.getPoint();
                        selectedObjects.clear();
                        for (BasicObject obj : objects) {
                            obj.setShowPorts(false);
                        }
                        repaint();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Mode mode = toolPanel.getCurrentMode();
                if ((mode == Mode.ASSOCIATION || mode == Mode.GENERALIZATION || mode == Mode.COMPOSITION) && isLinkDragging) {
                    // 連線模式：結束拉線
                    BasicObject endObj = findObjectAt(e.getPoint());
                    if (endObj != null && endObj != linkStartObject) {
                        Point endPort = endObj.getClosestPort(e.getPoint());
                        LinkObject link = new LinkObject(linkStartObject, endObj, linkStartPoint, endPort, mode);
                        links.add(link);
                    }
                    isLinkDragging = false;
                    linkStartObject = null;
                    linkStartPoint = null;
                    currentDragPoint = null;
                    repaint();
                } else if (mode == Mode.SELECT) {
                    if (isGroupDragging) {
                        // 群組拖曳結束：更新每個移動物件的連線端點（使用儲存的偏移量方式）
                        for (BasicObject obj : selectedObjects) {
                            updateLinksForObject(obj);
                        }
                        isGroupDragging = false;
                        initialPositions.clear();
                        repaint();
                    } else if (selectionStart != null && selectionEnd != null) {
                        // 結束區域選取：計算選取矩形，將完全包含的物件加入選取集合
                        Rectangle selectionRect = new Rectangle(
                                Math.min(selectionStart.x, selectionEnd.x),
                                Math.min(selectionStart.y, selectionEnd.y),
                                Math.abs(selectionStart.x - selectionEnd.x),
                                Math.abs(selectionStart.y - selectionEnd.y)
                        );
                        selectedObjects.clear();
                        boolean anySelected = false;
                        for (BasicObject obj : objects) {
                            Rectangle objRect = new Rectangle(obj.x, obj.y, obj.width, obj.height);
                            if (selectionRect.contains(objRect)) {
                                obj.setShowPorts(true);
                                selectedObjects.add(obj);
                                anySelected = true;
                            } else {
                                obj.setShowPorts(false);
                            }
                        }
                        // 若無物件完全包含於選取矩形，則清除所有選取
                        if (!anySelected) {
                            for (BasicObject obj : objects) {
                                obj.setShowPorts(false);
                            }
                        }
                        selectionStart = null;
                        selectionEnd = null;
                        repaint();
                    }
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Mode mode = toolPanel.getCurrentMode();
                if ((mode == Mode.ASSOCIATION || mode == Mode.GENERALIZATION || mode == Mode.COMPOSITION) && isLinkDragging) {
                    // 連線模式：更新橡皮筋線終點
                    currentDragPoint = e.getPoint();
                    repaint();
                } else if (mode == Mode.SELECT) {
                    if (isGroupDragging) {
                        // 群組拖曳：計算位移，並更新所有選取物件的位置
                        int deltaX = e.getX() - groupDragStartPoint.x;
                        int deltaY = e.getY() - groupDragStartPoint.y;
                        for (BasicObject obj : selectedObjects) {
                            Point initPos = initialPositions.get(obj);
                            if (obj instanceof CompositeObject) {
                                CompositeObject comp = (CompositeObject)obj;
                                // 計算應該移動的差距（相對於初始位置）
                                int newX = initPos.x + deltaX;
                                int newY = initPos.y + deltaY;
                                int moveX = newX - comp.x;
                                int moveY = newY - comp.y;
                                // 移動 composite（同時移動其內部的子物件）
                                comp.moveBy(moveX, moveY);
                            } else {
                                obj.x = initPos.x + deltaX;
                                obj.y = initPos.y + deltaY;
                            }
                            updateLinksForObject(obj);
                        }
                        repaint();
                    } else if (selectionStart != null) {
                        // 更新區域選取矩形
                        selectionEnd = e.getPoint();
                        repaint();
                    }
                }
            }


            @Override
            public void mouseMoved(MouseEvent e) {
                Mode mode = toolPanel.getCurrentMode();
                if (mode == Mode.ASSOCIATION || mode == Mode.GENERALIZATION || mode == Mode.COMPOSITION) {
                    // 連線模式下：滑鼠移到哪個物件就顯示該物件的 ports
                    BasicObject hovered = findObjectAt(e.getPoint());
                    for (BasicObject obj : objects) {
                        obj.setShowPorts(obj == hovered);
                    }
                    repaint();
                }
            }
        });
    }

    // 更新某個物件移動後的連線端點，利用 LinkObject 記錄的 offset 保持固定
    private void updateLinksForObject(BasicObject movedObj) {
        for (LinkObject link : links) {
            if (link.getStartObject() == movedObj) {
                Point newStartPort = new Point(
                        movedObj.x + link.getStartPortOffsetX(),
                        movedObj.y + link.getStartPortOffsetY()
                );
                link.setStartPort(newStartPort);
            }
            if (link.getEndObject() == movedObj) {
                Point newEndPort = new Point(
                        movedObj.x + link.getEndPortOffsetX(),
                        movedObj.y + link.getEndPortOffsetY()
                );
                link.setEndPort(newEndPort);
            }
        }
    }

    // 依照 depth（或其他排序規則）從最上層檢查點 p 是否落在某物件上
    private BasicObject findObjectAt(Point p) {
        // 此處從後往前檢查（假設後加入的物件在上層）
        for (int i = objects.size() - 1; i >= 0; i--) {
            BasicObject obj = objects.get(i);
            if (obj.contains(p)) {
                return obj;
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 繪製所有物件
        for (BasicObject obj : objects) {
            obj.draw(g);
        }
        // 繪製所有連線
        for (LinkObject link : links) {
            link.draw(g);
        }
        // 繪製拉線（橡皮筋線）：連線模式下使用
        if (isLinkDragging && linkStartPoint != null && currentDragPoint != null) {
            g.setColor(Color.GRAY);
            g.drawLine(linkStartPoint.x, linkStartPoint.y, currentDragPoint.x, currentDragPoint.y);
        }
        // 繪製選取矩形：Select 模式下使用
        if (toolPanel.getCurrentMode() == Mode.SELECT && selectionStart != null && selectionEnd != null) {
            g.setColor(Color.BLUE);
            int x = Math.min(selectionStart.x, selectionEnd.x);
            int y = Math.min(selectionStart.y, selectionEnd.y);
            int w = Math.abs(selectionStart.x - selectionEnd.x);
            int h = Math.abs(selectionStart.y - selectionEnd.y);
            g.drawRect(x, y, w, h);
        }
    }

    // 群組選取中的物件，形成一個 CompositeObject
    public void groupSelectedObjects() {
        if (selectedObjects.size() >= 2) {
            // 建立 composite 物件（傳入目前所有被選取的基本物件）
            CompositeObject composite = new CompositeObject(new ArrayList<>(selectedObjects));
            // 移除個別物件，加入 composite 物件
            objects.removeAll(selectedObjects);
            objects.add(composite);
            // 更新選取集合：只選取新的 composite 物件
            selectedObjects.clear();
            composite.setShowPorts(true);
            selectedObjects.add(composite);
            repaint();
        }
    }
    // 解構唯一選取的 composite 物件
    public void ungroupSelectedObject() {
        if (selectedObjects.size() == 1 && selectedObjects.get(0) instanceof CompositeObject) {
            CompositeObject composite = (CompositeObject) selectedObjects.get(0);
            // 移除 composite 物件，並把子物件加入全域物件清單
            objects.remove(composite);
            List<BasicObject> children = composite.getChildren();
            objects.addAll(children);
            // 更新選取集合：可選擇自動選取所有剛剛解群的物件
            selectedObjects.clear();
            for (BasicObject child : children) {
                child.setShowPorts(true);
                selectedObjects.add(child);
            }
            repaint();
        }
    }
    public List<BasicObject> getSelectedObjects() {
        return selectedObjects;
    }



}
