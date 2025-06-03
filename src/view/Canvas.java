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
 * 負責在畫布上繪製圖形物件與連線並處理相關的滑鼠事件與輔助繪製。
 */
public class Canvas extends JPanel {
	public static final int DEFAULT_WIDTH = 120;
	public static final int DEFAULT_HEIGHT = 80;

	private ToolPanel toolPanel;
	private CanvasModel model;
	private CanvasController controller;

	/**
	 * 建構子，初始化toolPanel、modle、controller，設定背景顏色與滑鼠監聽器。
	 *
	 * @param toolPanel toolPanel
	 * @param model     畫布模型，包含所有物件與連線
	 */
	public Canvas(ToolPanel toolPanel, CanvasModel model) {
		this.toolPanel = toolPanel;
		this.model = CanvasModel.getInstance();
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
	 * 繪製畫布上的所有圖形與連線物件。 會根據物件的 depth進行排序，depth 越小的物件顯示在越上層。 
	 * 繪製順序為物件與連線依照 depth 排序 → 個別呼叫 draw 方法繪製。 最後繪製額外的輔助元素，例如拖曳線或選取框
	 * 呼叫 repaint 的時候 ， Swing 會在事件處理 thread 安排呼叫 paintComponent
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		List<Object> drawList = new ArrayList<>();

		drawList.addAll(model.getObjects());
		drawList.addAll(model.getLinks());

		drawList.sort((o1, o2) -> {
			int depth1 = (o1 instanceof BasicObject) ? ((BasicObject) o1).getDepth() : ((LinkObject) o1).getDepth();
			int depth2 = (o2 instanceof BasicObject) ? ((BasicObject) o2).getDepth() : ((LinkObject) o2).getDepth();
			return Integer.compare(depth2, depth1);
		});

		for (Object obj : drawList) {
			if (obj instanceof BasicObject) {
				((BasicObject) obj).draw(g);
			} else if (obj instanceof LinkObject) {
				((LinkObject) obj).draw(g);
			}
		}

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
