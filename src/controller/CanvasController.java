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

/**
 * CanvasController 負責處理 Canvas 上的滑鼠事件，
 * 包括物件的建立、連線拖曳、選取與群組/解群組操作。
 * 重構後 BasicObject 與 LinkObject 均繼承自共用父類別 DisplayObject，
 * 因此 depth 的管理統一且點選不再改變 depth。
 */
public class CanvasController extends MouseAdapter implements MouseMotionListener {
    private ToolPanel toolPanel;
    private CanvasModel model;
    private Canvas canvas;

    private boolean isLinkDragging = false;
    private BasicObject linkStartObject = null;
    private Point linkStartPoint = null;
    private Point currentDragPoint = null;

    private Point selectionStart = null;
    private Point selectionEnd = null;
    private boolean isGroupDragging = false; // 拖曳時一個也當作是 Group
    private Point groupDragStartPoint = null;
    // 群組拖曳時記錄每個選取物件的起始位置
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
            // 顯示滑鼠所在物件的連線端口
            BasicObject hovered = findObjectAt(e.getPoint());
            for (BasicObject obj : model.getObjects()) {
                obj.setShowPorts(obj == hovered);
            }
            canvas.repaint();
        }
    }

    /**
     * 開始連線拖曳：若滑鼠點到某物件，則記錄連線起點及其端口位置。
     */
    private void startLinkDragging(MouseEvent e) {
        BasicObject startObj = findObjectAt(e.getPoint());
        if (startObj != null) {
            linkStartObject = startObj;
            linkStartPoint = startObj.getClosestPort(e.getPoint());
            isLinkDragging = true;
            currentDragPoint = e.getPoint();
        }
    }

    /**
     * 結束連線拖曳：若滑鼠放開時位於另一個物件上，則建立對應連線物件，
     * 連線的 depth 由 LinkObject 自行根據所連物件重新計算。
     */
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

    /**
     * 處理選取按下事件：
     * - 點到物件時：清除原先選取並選取該物件；若已選取則開始群組拖曳。
     * - 沒點到物件時：開始建立選取矩形。
     */
    private void handleSelectPressed(MouseEvent e) {
        BasicObject clickedObj = findObjectAt(e.getPoint());
        List<BasicObject> selectedObjects = model.getSelectedObjects();

        if (clickedObj != null) {
            if (selectedObjects.contains(clickedObj)) {
                // 已選取狀態，開始群組拖曳
                isGroupDragging = true;
                groupDragStartPoint = e.getPoint();
                initialPositions.clear();
                for (BasicObject obj : selectedObjects) {
                    initialPositions.put(obj, new Point(obj.getX(), obj.getY()));
                }
            } else {
                // 清除先前選取，再選取新的物件
                selectedObjects.clear();
                for (BasicObject obj : model.getObjects()) {
                    obj.setShowPorts(false);
                }
                selectedObjects.add(clickedObj);
                clickedObj.setShowPorts(true);

                isGroupDragging = true;
                groupDragStartPoint = e.getPoint();
                initialPositions.clear();
                initialPositions.put(clickedObj, new Point(clickedObj.getX(), clickedObj.getY()));
            }
            selectionStart = null;
            selectionEnd = null;
            canvas.repaint();
        } else {
            // 點空白處：開始建立選取矩形，並清除所有物件的連線端口顯示
            for (BasicObject obj : model.getObjects()) {
                obj.setShowPorts(false);
            }
            selectedObjects.clear();
            selectionStart = e.getPoint();
            selectionEnd = e.getPoint();
            canvas.repaint();
        }
    }

    /**
     * 處理選取放開事件：
     * - 若是群組拖曳，則更新被移動物件的連線位置；
     * - 若是選取矩形，則根據矩形選取物件，但不改變 depth。
     */
    private void handleSelectReleased(MouseEvent e) {
        List<BasicObject> selectedObjects = model.getSelectedObjects();
        if (isGroupDragging) {
            // 群組拖曳結束後更新所有被選取物件的連線位置
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
            for (BasicObject obj : model.getObjects()) {
                Rectangle objRect = new Rectangle(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
                if (selectionRect.contains(objRect)) {
                    obj.setShowPorts(true);
                    selectedObjects.add(obj);
                } else {
                    obj.setShowPorts(false);
                }
            }
            selectionStart = null;
            selectionEnd = null;
            // 更新所有連線的 depth（由 LinkObject 自行依照連接物件 recalculates）
            for (LinkObject link : model.getLinks()) {
                link.reCalcDepth();
            }
            canvas.repaint();
        }
    }

    /**
     * 處理選取拖曳事件：
     * - 若為群組拖曳，則移動選取物件並更新連線位置；
     * - 否則更新選取矩形。
     */
    private void handleSelectDragged(MouseEvent e) {
        List<BasicObject> selectedObjects = model.getSelectedObjects();
        if (isGroupDragging) {
            int deltaX = e.getX() - groupDragStartPoint.x;
            int deltaY = e.getY() - groupDragStartPoint.y;

            for (BasicObject obj : selectedObjects) {
                if (obj instanceof CompositeObject) {
                    ((CompositeObject) obj).moveBy(deltaX, deltaY);
                } else {
                    obj.setX(obj.getX() + deltaX);
                    obj.setY(obj.getY() + deltaY);
                }
                updateLinksForObject(obj);
            }
            groupDragStartPoint = e.getPoint();
            canvas.repaint();
        } else if (selectionStart != null) {
            selectionEnd = e.getPoint();
            canvas.repaint();
        }
    }

    /**
     * 在給定點 p 處找出 depth 最小（最上層）的物件
     */
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
        // 依 depth 升序排序（depth 較小者在上層）
        hitObjects.sort((o1, o2) -> Integer.compare(o1.getDepth(), o2.getDepth()));
        return hitObjects.get(0);
    }

    /**
     * 更新與指定物件相關的連線端點位置，
     * 根據物件新位置及原始偏移量重新計算連線端點，並重新計算連線 depth。
     */
    private void updateLinksForObject(BasicObject movedObj) {
        for (LinkObject link : model.getLinks()) {
            if (link.getStartObject() == movedObj) {
                Point newStartPort = new Point(
                        movedObj.getX() + link.getStartPortOffsetX(),
                        movedObj.getY() + link.getStartPortOffsetY()
                );
                link.setStartPort(newStartPort);
            }
            if (link.getEndObject() == movedObj) {
                Point newEndPort = new Point(
                        movedObj.getX() + link.getEndPortOffsetX(),
                        movedObj.getY() + link.getEndPortOffsetY()
                );
                link.setEndPort(newEndPort);
            }
            link.reCalcDepth();
        }
    }

    /**
     * 繪製額外輔助線：
     * - 連線拖曳時繪製橡皮筋線
     * - 選取模式下繪製選取矩形
     */
    public void drawAdditionalGuides(Graphics g) {
        if (isLinkDragging && linkStartPoint != null && currentDragPoint != null) {
            g.setColor(Color.GRAY);
            g.drawLine(linkStartPoint.x, linkStartPoint.y, currentDragPoint.x, currentDragPoint.y);
        }
        if (toolPanel.getCurrentMode() == Mode.SELECT && selectionStart != null && selectionEnd != null) {
            g.setColor(Color.BLUE);
            int x = Math.min(selectionStart.x, selectionEnd.x);
            int y = Math.min(selectionStart.y, selectionEnd.y);
            int w = Math.abs(selectionStart.x - selectionEnd.x);
            int h = Math.abs(selectionStart.y - selectionEnd.y);
            g.drawRect(x, y, w, h);
        }
    }

    /**
     * 將多個被選取的物件群組成一個 CompositeObject。
     */
    public void groupSelectedObjects() {
        List<BasicObject> selected = model.getSelectedObjects();
        if (selected.size() >= 2) {
            List<BasicObject> newChildren = new ArrayList<>(selected);
            model.getObjects().removeAll(selected);

            CompositeObject composite = new CompositeObject(newChildren);
            model.getObjects().add(composite);

            selected.clear();
            composite.setShowPorts(true);
            selected.add(composite);

            canvas.repaint();
        }
    }

    /**
     * 將群組的 CompositeObject 解散，將其中子物件恢復為獨立物件，
     * 並移除與該 composite 相關的連線。
     */
    public void ungroupSelectedObject() {
        List<BasicObject> selected = model.getSelectedObjects();
        if (selected.size() == 1 && selected.get(0) instanceof CompositeObject) {
            CompositeObject composite = (CompositeObject) selected.get(0);
            model.getObjects().remove(composite);
            List<LinkObject> linksToRemove = new ArrayList<>();
            for (LinkObject link : model.getLinks()) {
                if (link.getStartObject() == composite || link.getEndObject() == composite) {
                    linksToRemove.add(link);
                }
            }
            model.getLinks().removeAll(linksToRemove);
            List<BasicObject> children = composite.getChildren();
            model.getObjects().addAll(children);
            selected.clear();
            for (BasicObject child : children) {
                child.setShowPorts(true);
                selected.add(child);
            }
            canvas.repaint();
        }
    }
}
