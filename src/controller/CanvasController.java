package controller;

import model.*;
import view.Canvas;
import enums.Mode;
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
                    // 設定連線的 depth 確保比兩個連接的物件小（在最上層）
                    link.setDepth(Math.min(linkStartObject.getDepth(), endObj.getDepth()) - 1);
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
            // 將原先已選取物件恢復預設：用 getNextDepth() 更新其 depth
            for (BasicObject obj : selectedObjects) {
                obj.setDepth(BasicObject.getNextDepth());
            }
            selectedObjects.clear();
            for (BasicObject obj : model.getObjects()) {
                obj.setShowPorts(false);
            }
            selectedObjects.add(clickedObj);
            clickedObj.setShowPorts(true);

            // 將被選取的物件 depth 設為 -1，代表在最上層
            clickedObj.setDepth(-1);

            // 更新所有與 clickedObj 連接的連線
            for (LinkObject link : model.getLinks()) {
                if (link.getStartObject() == clickedObj || link.getEndObject() == clickedObj) {
                    link.recalcDepth();
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
            // 點選空白處，恢復所有物件預設深度：使用全域計數器取得下一個值
            for (BasicObject obj : model.getObjects()) {
                obj.setShowPorts(false);
                obj.setDepth(BasicObject.getNextDepth());
            }
            selectedObjects.clear();
            selectionStart = e.getPoint();
            selectionEnd = e.getPoint();
            canvas.repaint();
        }
    }

    private void handleSelectReleased(MouseEvent e) {
        List<BasicObject> selectedObjects = model.getSelectedObjects();
        if (isGroupDragging) {
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
                    // 設定選取物件 depth 為 -1（最上層）
                    obj.setDepth(-1);
                } else {
                    obj.setShowPorts(false);
                    // **更新未選取物件的 depth，使它們永遠在最上層**
                    obj.setDepth(BasicObject.getNextDepth());
                }
            }
            if (!anySelected) {
                for (BasicObject obj : model.getObjects()) {
                    obj.setShowPorts(false);
                    // **確保所有物件不會因為取消選取而沉到底層**
                    obj.setDepth(BasicObject.getNextDepth());
                }
            }
            selectionStart = null;
            selectionEnd = null;
            for (LinkObject link : model.getLinks()) {
                link.recalcDepth();
            }
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
        List<BasicObject> hitObjects = new ArrayList<>();
        for (BasicObject obj : model.getObjects()) {
            if (obj.contains(p)) {
                hitObjects.add(obj);
            }
        }
        if (hitObjects.isEmpty()) {
            return null;
        }
        // 依 depth 升序排序（-1 最小）
        hitObjects.sort((o1, o2) -> Integer.compare(o1.getDepth(), o2.getDepth()));
        return hitObjects.get(0);
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
            link.recalcDepth();
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
