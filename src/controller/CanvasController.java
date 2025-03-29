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
 */
public class CanvasController extends MouseAdapter implements MouseMotionListener {
    private ToolPanel toolPanel;
    private CanvasModel model;
    private Canvas canvas;

    // ===== 拉線相關變數 =====
    private boolean isLinkDragging = false;
    private BasicObject linkStartObject = null;
    private Point linkStartPoint = null;
    private Point currentDragPoint = null;

    // ===== 拖曳選取相關變數 =====
    private Point selectionStart = null;
    private Point selectionEnd = null;
    private boolean isGroupDragging = false;
    private Point groupDragStartPoint = null;
    // 記錄每個被選取物件拖曳前的初始位置，用於計算位移
    private Map<BasicObject, Point> initialPositions = new HashMap<>();

    /**
     * 建構子，初始化控制器，並綁定工具面板、模型與畫布。
     *
     * @param toolPanel 工具面板
     * @param model     畫布模型，包含所有物件和連線
     * @param canvas    畫布視圖，負責繪製物件
     */
    public CanvasController(ToolPanel toolPanel, CanvasModel model, Canvas canvas) {
        this.toolPanel = toolPanel;
        this.model = model;
        this.canvas = canvas;
    }

    /**
     * 處理滑鼠按下事件：
     * 根據目前的模式來建立物件、開始連線拖曳或開始選取操作。
     */
    @Override
    public void mousePressed(MouseEvent e) {
        Mode mode = toolPanel.getCurrentMode();
        switch (mode) {
            case RECT:
                // 建立新的矩形物件，並加入模型中
                model.getObjects().add(new RectObject(
                        e.getX(), e.getY(),
                        Canvas.DEFAULT_WIDTH, Canvas.DEFAULT_HEIGHT
                ));
                canvas.repaint();
                break;
            case OVAL:
                // 建立新的橢圓物件，並加入模型中
                model.getObjects().add(new OvalObject(
                        e.getX(), e.getY(),
                        Canvas.DEFAULT_WIDTH, Canvas.DEFAULT_HEIGHT
                ));
                canvas.repaint();
                break;
            case ASSOCIATION:
            case GENERALIZATION:
            case COMPOSITION:
                // 在連線模式下，開始拖曳連線
                startLinkDragging(e);
                break;
            case SELECT:
                // 處理選取按下事件（點選物件或開始拉選範圍）
                handleSelectPressed(e);
                break;
            default:
                break;
        }
    }

    /**
     * 處理滑鼠放開事件：
     * 根據模式結束連線拖曳或完成選取操作。
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        Mode mode = toolPanel.getCurrentMode();
        switch (mode) {
            case ASSOCIATION:
            case GENERALIZATION:
            case COMPOSITION:
                // 結束連線拖曳，建立連線物件
                endLinkDragging(e);
                break;
            case SELECT:
                // 完成選取操作或群組拖曳後更新物件與連線
                handleSelectReleased(e);
                break;
            default:
                break;
        }
    }

    /**
     * 處理滑鼠拖曳事件：
     * 根據模式更新連線的拖曳位置或移動選取物件/選取矩形。
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        Mode mode = toolPanel.getCurrentMode();
        switch (mode) {
            case ASSOCIATION:
            case GENERALIZATION:
            case COMPOSITION:
                // 更新連線拖曳時的滑鼠位置，重繪畫布
                if (isLinkDragging) {
                    currentDragPoint = e.getPoint();
                    canvas.repaint();
                }
                break;
            case SELECT:
                // 處理選取時的拖曳事件
                handleSelectDragged(e);
                break;
            default:
                break;
        }
    }

    /**
     * 處理滑鼠移動事件：
     * 在連線模式下，當滑鼠移到某物件上時顯示其連線端口。
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        Mode mode = toolPanel.getCurrentMode();
        if (mode == Mode.ASSOCIATION || mode == Mode.GENERALIZATION || mode == Mode.COMPOSITION) {
            // 找出滑鼠位置下的物件，僅顯示該物件的連線端口
            BasicObject hovered = findObjectAt(e.getPoint());
            for (BasicObject obj : model.getObjects()) {
                obj.setShowPorts(obj == hovered);
            }
            canvas.repaint();
        }
    }

    /**
     * 開始連線拖曳：當滑鼠按下時，如果指向一個物件，則記錄連線起點及其端口位置。
     *
     * @param e 滑鼠事件
     */
    private void startLinkDragging(MouseEvent e) {
        BasicObject startObj = findObjectAt(e.getPoint());
        if (startObj != null) {
            linkStartObject = startObj;
            // 取得最靠近滑鼠點的連線端口位置
            linkStartPoint = startObj.getClosestPort(e.getPoint());
            isLinkDragging = true;
            currentDragPoint = e.getPoint();
        }
    }

    /**
     * 結束連線拖曳：如果滑鼠放開時位於另一個物件上，則建立相對應的連線物件。
     *
     * @param e 滑鼠事件
     */
    private void endLinkDragging(MouseEvent e) {
        if (isLinkDragging) {
            BasicObject endObj = findObjectAt(e.getPoint());
            // 確保結束物件存在且與起始物件不同
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
                    // 設定連線的 depth，確保連線比兩物件更上層
                    link.setDepth(Math.min(linkStartObject.getDepth(), endObj.getDepth()) - 1);
                    model.getLinks().add(link);
                }
            }
            // 重置連線拖曳相關變數
            isLinkDragging = false;
            linkStartObject = null;
            linkStartPoint = null;
            currentDragPoint = null;
            canvas.repaint();
        }
    }

    /**
     * 處理選取按下事件：
     * 若點選到物件，則清除先前選取並選取此物件；否則開始建立選取矩形。
     *
     * @param e 滑鼠事件
     */
    private void handleSelectPressed(MouseEvent e) {
        BasicObject clickedObj = findObjectAt(e.getPoint());
        List<BasicObject> selectedObjects = model.getSelectedObjects();

        if (clickedObj != null) {
            // 清除先前選取，並隱藏所有物件的連線端口
            selectedObjects.clear();
            for (BasicObject obj : model.getObjects()) {
                obj.setShowPorts(false);
            }
            // 選取目前點選的物件，並顯示其連線端口
            selectedObjects.add(clickedObj);
            clickedObj.setShowPorts(true);

            // 準備群組拖曳：記錄每個選取物件的初始位置
            isGroupDragging = true;
            groupDragStartPoint = e.getPoint();
            initialPositions.clear();
            for (BasicObject obj : selectedObjects) {
                initialPositions.put(obj, new Point(obj.getX(), obj.getY()));
            }
            // 若是群組拖曳，清除選取矩形座標
            selectionStart = null;
            selectionEnd = null;
            canvas.repaint();
        } else {
            // 點選空白區域，清除所有物件的選取狀態，開始建立選取矩形
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
     * - 若進行群組拖曳，則更新被移動物件的連線位置。
     * - 否則，根據選取矩形選取位於該區域內的物件，並更新連線深度。
     *
     * @param e 滑鼠事件
     */
    private void handleSelectReleased(MouseEvent e) {
        List<BasicObject> selectedObjects = model.getSelectedObjects();
        if (isGroupDragging) {
            // 群組拖曳結束後，更新所有被選取物件的連線位置
            for (BasicObject obj : selectedObjects) {
                updateLinksForObject(obj);
            }
            isGroupDragging = false;
            initialPositions.clear();
            canvas.repaint();
        } else if (selectionStart != null && selectionEnd != null) {
            // 建立選取矩形，根據矩形範圍選取物件
            Rectangle selectionRect = new Rectangle(
                    Math.min(selectionStart.x, selectionEnd.x),
                    Math.min(selectionStart.y, selectionEnd.y),
                    Math.abs(selectionStart.x - selectionEnd.x),
                    Math.abs(selectionStart.y - selectionEnd.y)
            );
            selectedObjects.clear();
            boolean anySelected = false;
            // 判斷每個物件是否完全落在選取矩形中
            for (BasicObject obj : model.getObjects()) {
                Rectangle objRect = new Rectangle(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
                if (selectionRect.contains(objRect)) {
                    obj.setShowPorts(true);
                    selectedObjects.add(obj);
                    anySelected = true;
                    // 將選取物件的 depth 設為 -1，使其顯示在最上層
                    obj.setDepth(-1);
                } else {
                    obj.setShowPorts(false);
                    // 更新未選取物件的 depth，確保它們位於較低層
                    obj.setDepth(BasicObject.getNextDepth());
                }
            }
            if (!anySelected) {
                // 若沒有物件被選取，則清除所有物件的選取狀態並更新深度
                for (BasicObject obj : model.getObjects()) {
                    obj.setShowPorts(false);
                    obj.setDepth(BasicObject.getNextDepth());
                }
            }
            selectionStart = null;
            selectionEnd = null;
            // 更新所有連線的深度
            for (LinkObject link : model.getLinks()) {
                link.reCalcDepth();
            }
            canvas.repaint();
        }
    }

    /**
     * 處理選取拖曳事件：
     * - 若正在群組拖曳，則移動選取物件並更新連線位置。
     * - 否則，更新選取矩形的結束點，並重繪選取框。
     *
     * @param e 滑鼠事件
     */
    private void handleSelectDragged(MouseEvent e) {
        List<BasicObject> selectedObjects = model.getSelectedObjects();
        if (isGroupDragging) {
            // 計算從上次拖曳位置到當前位置的位移量
            int deltaX = e.getX() - groupDragStartPoint.x;
            int deltaY = e.getY() - groupDragStartPoint.y;

            // 對所有被選取物件進行移動
            for (BasicObject obj : selectedObjects) {
                if (obj instanceof CompositeObject) {
                    ((CompositeObject) obj).moveBy(deltaX, deltaY);
                } else {
                    // 若不是群組物件，直接更新位置
                    obj.setX(obj.getX() + deltaX);
                    obj.setY(obj.getY() + deltaY);
                }
                updateLinksForObject(obj);
            }
            // 更新 groupDragStartPoint 為當前滑鼠位置，以便下一次拖曳計算增量
            groupDragStartPoint = e.getPoint();
            canvas.repaint();
        } else if (selectionStart != null) {
            selectionEnd = e.getPoint();
            canvas.repaint();
        }
    }

    /**
     * 在給定點 p 處找出最上層（depth 最小）的物件。
     *
     * @param p 檢查點
     * @return 該點所在的物件，若無則回傳 null
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
        // 根據 depth 升序排序，depth 較小者（例如 -1）代表較上層
        hitObjects.sort((o1, o2) -> Integer.compare(o1.getDepth(), o2.getDepth()));
        return hitObjects.get(0);
    }

    /**
     * 更新與指定物件相關的連線端點位置，
     * 根據該物件的新位置和原始偏移量重新計算連線端點，
     * 並重新計算連線的 depth。
     *
     * @param movedObj 被移動的物件
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
            // 更新連線深度
            link.reCalcDepth();
        }
    }

    /**
     * 繪製額外的輔助線：
     * - 在連線拖曳時，繪製橡皮筋線。
     * - 在選取模式下，繪製選取矩形。
     *
     * @param g Graphics 物件
     */
    public void drawAdditionalGuides(Graphics g) {
        // 繪製橡皮筋線（連線拖曳時）
        if (isLinkDragging && linkStartPoint != null && currentDragPoint != null) {
            g.setColor(Color.GRAY);
            g.drawLine(linkStartPoint.x, linkStartPoint.y, currentDragPoint.x, currentDragPoint.y);
        }
        // 繪製選取矩形
        if (toolPanel.getCurrentMode() == Mode.SELECT && selectionStart != null && selectionEnd != null) {
            g.setColor(Color.BLUE);
            int x = Math.min(selectionStart.x, selectionEnd.x);
            int y = Math.min(selectionStart.y, selectionEnd.y);
            int w = Math.abs(selectionStart.x - selectionEnd.x);
            int h = Math.abs(selectionStart.y - selectionEnd.y);
            g.drawRect(x, y, w, h);
        }
    }

    // ===== 群組/解群組操作 =====

    /**
     * 將多個被選取的物件群組成一個 CompositeObject。
     * 1. 收集所有被選取的物件（若其中有 CompositeObject，則平坦化其子物件）。
     * 2. 從模型中移除這些物件，並移除所有僅連接於這些物件間的連線。
     * 3. 建立新的 CompositeObject 並加入模型，同時更新選取狀態。
     */
    public void groupSelectedObjects() {
        List<BasicObject> selected = model.getSelectedObjects();
        // 只有當選取的物件數量大於等於 2 時才進行群組操作
        if (selected.size() >= 2) {
            // 將所有選取的物件直接作為新群組的子物件，
            // 不平坦化已存在的 composite 物件，達到一層一層增加的效果
            List<BasicObject> newChildren = new ArrayList<>(selected);

            // 從模型中移除原本被選取的物件
            model.getObjects().removeAll(selected);

            // 移除所有僅連接在選取物件之間的連線
            List<LinkObject> linksToRemove = new ArrayList<>();
            for (LinkObject link : model.getLinks()) {
                BasicObject start = link.getStartObject();
                BasicObject end = link.getEndObject();
                if (selected.contains(start) && selected.contains(end)) {
                    linksToRemove.add(link);
                }
            }
            model.getLinks().removeAll(linksToRemove);

            // 建立新的 CompositeObject，並傳入 newChildren 作為其子物件
            CompositeObject composite = new CompositeObject(newChildren);
            model.getObjects().add(composite);

            // 更新選取狀態：清除原先的選取，並選取新建立的 composite 物件
            selected.clear();
            composite.setShowPorts(true);
            selected.add(composite);

            canvas.repaint();
        }
    }


    /**
     * 將被群組的 CompositeObject 解散，將其中的子物件恢復為獨立物件，
     * 並移除與該 composite 相關的連線。
     */
    public void ungroupSelectedObject() {
        List<BasicObject> selected = model.getSelectedObjects();
        if (selected.size() == 1 && selected.get(0) instanceof CompositeObject) {
            CompositeObject composite = (CompositeObject) selected.get(0);
            // 從模型中移除 composite 物件
            model.getObjects().remove(composite);
            // 移除所有與 composite 相關的連線
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
            // 更新選取狀態：清除原選取，並將所有子物件設為選取
            selected.clear();
            for (BasicObject child : children) {
                child.setShowPorts(true);
                selected.add(child);
            }
            canvas.repaint();
        }
    }
}
