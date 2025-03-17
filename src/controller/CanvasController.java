package controller;

import model.*;
import view.Canvas;
import view.Mode;
import view.ToolPanel;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CanvasController extends MouseAdapter implements MouseMotionListener {
    private ToolPanel toolPanel;
    private CanvasModel model;
    private Canvas canvas;

    // 拉線相關
    private boolean isLinkDragging = false;
    private BasicObject linkStartObject = null;
    private Point linkStartPoint = null;
    private Point currentDragPoint = null;

    // 拖曳相關
    private Point selectionStart = null;
    private Point selectionEnd = null;
    private boolean isGroupDragging = false;
    private Point groupDragStartPoint = null;
    private Map<BasicObject, Point> initialPositions = new HashMap<>();

    public CanvasController(ToolPanel toolPanel, CanvasModel model, Canvas canvas) {
        this.toolPanel = toolPanel;
        this.model = model;
        this.canvas = canvas;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Mode mode = toolPanel.getCurrentMode();
        switch (mode) {
            case RECT:
                model.getObjects().add(new RectObject(
                        e.getX(), e.getY(),
                        Canvas.DEFAULT_WIDTH, Canvas.DEFAULT_HEIGHT
                ));
                canvas.repaint();
                break;
            case OVAL:
                model.getObjects().add(new OvalObject(
                        e.getX(), e.getY(),
                        Canvas.DEFAULT_WIDTH, Canvas.DEFAULT_HEIGHT
                ));
                canvas.repaint();
                break;
            case ASSOCIATION:
            case GENERALIZATION:
            case COMPOSITION:
                startLinkDragging(e);
                break;
            case SELECT:
                handleSelectPressed(e);
                break;
            default:
                break;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Mode mode = toolPanel.getCurrentMode();
        switch (mode) {
            case ASSOCIATION:
            case GENERALIZATION:
            case COMPOSITION:
                endLinkDragging(e);
                break;
            case SELECT:
                handleSelectReleased(e);
                break;
            default:
                break;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Mode mode = toolPanel.getCurrentMode();
        switch (mode) {
            case ASSOCIATION:
            case GENERALIZATION:
            case COMPOSITION:
                if (isLinkDragging) {
                    currentDragPoint = e.getPoint();
                    canvas.repaint();
                }
                break;
            case SELECT:
                handleSelectDragged(e);
                break;
            default:
                break;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Mode mode = toolPanel.getCurrentMode();
        if (mode == Mode.ASSOCIATION || mode == Mode.GENERALIZATION || mode == Mode.COMPOSITION) {
            // 滑鼠移到哪個物件就顯示 ports
            BasicObject hovered = findObjectAt(e.getPoint());
            for (BasicObject obj : model.getObjects()) {
                obj.setShowPorts(obj == hovered);
            }
            canvas.repaint();
        }
    }

    private void startLinkDragging(MouseEvent e) {
        BasicObject startObj = findObjectAt(e.getPoint());
        if (startObj != null) {
            linkStartObject = startObj;
            linkStartPoint = startObj.getClosestPort(e.getPoint());
            isLinkDragging = true;
            currentDragPoint = e.getPoint();
        }
    }

    private void endLinkDragging(MouseEvent e) {
        if (isLinkDragging) {
            BasicObject endObj = findObjectAt(e.getPoint());
            if (endObj != null && endObj != linkStartObject) {
                Point endPort = endObj.getClosestPort(e.getPoint());
                Mode mode = toolPanel.getCurrentMode();
                LinkObject link = null;
                switch (mode) {
                    case ASSOCIATION:
                        link = new AssociationLink(linkStartObject, endObj, linkStartPoint, endPort);
                        break;
                    case GENERALIZATION:
                        link = new GeneralizationLink(linkStartObject, endObj, linkStartPoint, endPort);
                        break;
                    case COMPOSITION:
                        link = new CompositionLink(linkStartObject, endObj, linkStartPoint, endPort);
                        break;
                    default:
                        break;
                }
                if (link != null) {
                    model.getLinks().add(link);
                }
            }
            isLinkDragging = false;
            linkStartObject = null;
            linkStartPoint = null;
            currentDragPoint = null;
            canvas.repaint();
        }
    }

    private void handleSelectPressed(MouseEvent e) {
        BasicObject clickedObj = findObjectAt(e.getPoint());
        List<BasicObject> selectedObjects = model.getSelectedObjects();
        if (clickedObj != null) {
            // 如果點選到物件，則清除原有選取，僅選取新物件
            if (!selectedObjects.contains(clickedObj)) {
                selectedObjects.clear();
                for (BasicObject obj : model.getObjects()) {
                    obj.setShowPorts(false);
                }
                selectedObjects.add(clickedObj);
                clickedObj.setShowPorts(true);

                // 將被點選的物件 depth 設為 0，使它位於最上層
                clickedObj.setDepth(0);
                // 如果其他物件的 depth 也為 0，則將其調整為 1（確保只有這個物件為 0）
                for (BasicObject obj : model.getObjects()) {
                    if (obj != clickedObj && obj.getDepth() == 0) {
                        obj.setDepth(1);
                    }
                }
            }
            isGroupDragging = true;
            groupDragStartPoint = e.getPoint();
            initialPositions.clear();
            for (BasicObject obj : selectedObjects) {
                initialPositions.put(obj, new Point(obj.x, obj.y));
            }
            selectionStart = null;
            selectionEnd = null;
            canvas.repaint();
        } else {
            // 點選空白處，開始區域選取
            selectionStart = e.getPoint();
            selectionEnd = e.getPoint();
            selectedObjects.clear();
            for (BasicObject obj : model.getObjects()) {
                obj.setShowPorts(false);
            }
            canvas.repaint();
        }
    }
    private void handleSelectReleased(MouseEvent e) {
        List<BasicObject> selectedObjects = model.getSelectedObjects();
        if (isGroupDragging) {
            // 結束拖曳，更新連線端點
            for (BasicObject obj : selectedObjects) {
                updateLinksForObject(obj);
            }
            isGroupDragging = false;
            initialPositions.clear();
            canvas.repaint();
        } else if (selectionStart != null && selectionEnd != null) {
            Rectangle selectionRect = new Rectangle(
                    Math.min(selectionStart.x, selectionEnd.x),
                    Math.min(selectionStart.y, selectionEnd.y),
                    Math.abs(selectionStart.x - selectionEnd.x),
                    Math.abs(selectionStart.y - selectionEnd.y)
            );
            selectedObjects.clear();
            boolean anySelected = false;
            for (BasicObject obj : model.getObjects()) {
                Rectangle objRect = new Rectangle(obj.x, obj.y, obj.width, obj.height);
                if (selectionRect.contains(objRect)) {
                    obj.setShowPorts(true);
                    selectedObjects.add(obj);
                    anySelected = true;
                } else {
                    obj.setShowPorts(false);
                }
            }
            if (!anySelected) {
                for (BasicObject obj : model.getObjects()) {
                    obj.setShowPorts(false);
                }
            }
            selectionStart = null;
            selectionEnd = null;
            canvas.repaint();
        }
    }

    private void handleSelectDragged(MouseEvent e) {
        List<BasicObject> selectedObjects = model.getSelectedObjects();
        if (isGroupDragging) {
            //如果是選到group再拖曳，把group中的object拖曳到新位置
            int deltaX = e.getX() - groupDragStartPoint.x;
            int deltaY = e.getY() - groupDragStartPoint.y;
            for (BasicObject obj : selectedObjects) {
                Point initPos = initialPositions.get(obj);
                if (obj instanceof CompositeObject) {
                    CompositeObject comp = (CompositeObject) obj;
                    int newX = initPos.x + deltaX;
                    int newY = initPos.y + deltaY;
                    int moveX = newX - comp.x;
                    int moveY = newY - comp.y;
                    comp.moveBy(moveX, moveY);
                } else {
                    obj.x = initPos.x + deltaX;
                    obj.y = initPos.y + deltaY;
                }
                updateLinksForObject(obj);
            }
            canvas.repaint();
        } else if (selectionStart != null) {
            //更新框框的右下角然後呼叫repaint繪製選取框
            selectionEnd = e.getPoint();
            canvas.repaint();
        }
    }

    private BasicObject findObjectAt(Point p) {
        List<BasicObject> objs = model.getObjects();
        for (int i = objs.size() - 1; i >= 0; i--) {
            BasicObject obj = objs.get(i);
            if (obj.contains(p)) {
                return obj;
            }
        }
        return null;
    }

    private void updateLinksForObject(BasicObject movedObj) {
        for (LinkObject link : model.getLinks()) {
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

    // 提供給 Canvas 呼叫，繪製「橡皮筋線」或「區域選取框」
    public void drawAdditionalGuides(Graphics g) {
        // 橡皮筋線
        if (isLinkDragging && linkStartPoint != null && currentDragPoint != null) {
            g.setColor(Color.GRAY);
            g.drawLine(linkStartPoint.x, linkStartPoint.y, currentDragPoint.x, currentDragPoint.y);
        }
        // 區域選取矩形
        if (toolPanel.getCurrentMode() == Mode.SELECT && selectionStart != null && selectionEnd != null) {
            g.setColor(Color.BLUE);
            int x = Math.min(selectionStart.x, selectionEnd.x);
            int y = Math.min(selectionStart.y, selectionEnd.y);
            int w = Math.abs(selectionStart.x - selectionEnd.x);
            int h = Math.abs(selectionStart.y - selectionEnd.y);
            g.drawRect(x, y, w, h);
        }
    }

    // ====================== 群組/解群組 ======================
    // 修改後的群組方法：平坦化選取物件中若有 CompositeObject 的子物件，並合併成一個新的 composite
    public void groupSelectedObjects() {
        List<BasicObject> selected = model.getSelectedObjects();
        if (selected.size() >= 2) {
            // 取得一個新的子物件集合：平坦化選取中所有 composite 的子物件
            List<BasicObject> newChildren = new ArrayList<>();
            for (BasicObject obj : selected) {
                if (obj instanceof CompositeObject) {
                    newChildren.addAll(((CompositeObject) obj).getChildren());
                } else {
                    newChildren.add(obj);
                }
            }

            // 從模型中移除原先選取的物件
            model.getObjects().removeAll(selected);

            // 移除模型中連線，如果連線兩端都在選取範圍內，則認為這是內部連線，予以刪除
            List<LinkObject> linksToRemove = new ArrayList<>();
            for (LinkObject link : model.getLinks()) {
                BasicObject start = link.getStartObject();
                BasicObject end = link.getEndObject();
                if (selected.contains(start) && selected.contains(end)) {
                    linksToRemove.add(link);
                }
            }
            model.getLinks().removeAll(linksToRemove);

            // 建立新的 composite 物件，將 newChildren 當作其子物件
            CompositeObject composite = new CompositeObject(newChildren);
            model.getObjects().add(composite);

            // 更新選取集合：清空後把新 composite 設為選取
            selected.clear();
            composite.setShowPorts(true);
            selected.add(composite);
            canvas.repaint();
        }
    }

    public void ungroupSelectedObject() {
        List<BasicObject> selected = model.getSelectedObjects();
        if (selected.size() == 1 && selected.get(0) instanceof CompositeObject) {
            CompositeObject composite = (CompositeObject) selected.get(0);
            // 從模型中移除 composite 物件
            model.getObjects().remove(composite);

            // 移除與 composite 相關的連線
            List<LinkObject> linksToRemove = new ArrayList<>();
            for (LinkObject link : model.getLinks()) {
                if (link.getStartObject() == composite || link.getEndObject() == composite) {
                    linksToRemove.add(link);
                }
            }
            model.getLinks().removeAll(linksToRemove);

            // 將 composite 的子物件加入模型
            List<BasicObject> children = composite.getChildren();
            model.getObjects().addAll(children);

            // 更新選取集合：清除原有選取，設定所有子物件為選取狀態
            selected.clear();
            for (BasicObject child : children) {
                child.setShowPorts(true);
                selected.add(child);
            }

            canvas.repaint();
        }
    }
}
