package view;

import enums.Mode;
import javax.swing.*;
import java.awt.*;

/**
 * ToolPanel 類別負責顯示工具按鈕，讓使用者能夠選擇不同的操作模式（例如：選取、連線、建立圖形）。
 */
public class ToolPanel extends JPanel {

	// 目前的操作模式，預設為 SELECT
	private Mode currentMode = Mode.SELECT;

	private JButton selectBtn;
	private JButton rectBtn;
	private JButton ovalBtn;
	private JButton assocBtn;
	private JButton genBtn;
	private JButton compBtn;

	// 預設與選中狀態的背景顏色
	private final Color defaultColor = Color.decode("#FFF7F3");
	private final Color selectedColor = Color.decode("#FAD0C4");

	/**
	 * 建構子：設定工具面板的版面配置與初始化各工具按鈕。
	 */
	public ToolPanel() {
		setLayout(new GridLayout(6, 1));
		initializeButtons();
		addButtonsToPanel();
		applyDefaultStyles();
		addActionListeners();
	}

	/**
	 * 初始化各工具按鈕，設定圖示與樣式。
	 */
	private void initializeButtons() {
		selectBtn = new JButton(loadIcon("/resources/select.png"));
		assocBtn = new JButton(loadIcon("/resources/association_line.png"));
		genBtn = new JButton(loadIcon("/resources/generalization_line.png"));
		compBtn = new JButton(loadIcon("/resources/composition_line.png"));
		rectBtn = new JButton(loadIcon("/resources/rect.png"));
		ovalBtn = new JButton(loadIcon("/resources/oval.png"));

		customizeButton(selectBtn);
		customizeButton(assocBtn);
		customizeButton(genBtn);
		customizeButton(compBtn);
		customizeButton(rectBtn);
		customizeButton(ovalBtn);
	}

	/**
	 * 將各工具按鈕加入面板中。
	 */
	private void addButtonsToPanel() {
		add(selectBtn);
		add(assocBtn);
		add(genBtn);
		add(compBtn);
		add(rectBtn);
		add(ovalBtn);
	}

	/**
	 * 設定所有按鈕的預設背景顏色。
	 */
	private void applyDefaultStyles() {
		selectBtn.setBackground(defaultColor);
		assocBtn.setBackground(defaultColor);
		genBtn.setBackground(defaultColor);
		compBtn.setBackground(defaultColor);
		rectBtn.setBackground(defaultColor);
		ovalBtn.setBackground(defaultColor);
	}

	/**
	 * 為每個按鈕加入對應的事件監聽器，當按下按鈕時設定相應的模式。
	 */
	private void addActionListeners() {
		selectBtn.addActionListener(e -> setMode(Mode.SELECT));
		assocBtn.addActionListener(e -> setMode(Mode.ASSOCIATION));
		genBtn.addActionListener(e -> setMode(Mode.GENERALIZATION));
		compBtn.addActionListener(e -> setMode(Mode.COMPOSITION));
		rectBtn.addActionListener(e -> setMode(Mode.RECT));
		ovalBtn.addActionListener(e -> setMode(Mode.OVAL));
	}

	/**
	 * 為按鈕設定自訂樣式，包括關閉焦點繪製、設置邊框與內邊距。
	 *
	 * @param button 欲設定樣式的按鈕
	 */
	private void customizeButton(JButton button) {
		button.setFocusPainted(false);
		button.setBorderPainted(true);
		button.setContentAreaFilled(true);
		button.setOpaque(true);

		button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 1),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
	}

	/**
	 * 根據指定路徑載入圖示，並回傳 ImageIcon 物件。
	 *
	 * @param path 圖示資源路徑
	 * @return 載入成功的 ImageIcon，否則回傳 null
	 */
	private ImageIcon loadIcon(String path) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Can't load icon image: " + path);
			return null;
		}
	}

	/**
	 * 設定工具面板的操作模式，更新按鈕背景顏色以反映當前狀態。
	 *
	 * @param mode 新的操作模式
	 */
	private void setMode(Mode mode) {
		this.currentMode = mode;
		updateButtonColors();
	}

	public Mode getCurrentMode() {
		return currentMode;
	}

	/**
	 * 更新各按鈕的背景顏色：所有按鈕恢復預設顏色，當前模式的按鈕設為選中顏色。
	 */
	private void updateButtonColors() {
		selectBtn.setBackground(defaultColor);
		assocBtn.setBackground(defaultColor);
		genBtn.setBackground(defaultColor);
		compBtn.setBackground(defaultColor);
		rectBtn.setBackground(defaultColor);
		ovalBtn.setBackground(defaultColor);

		switch (currentMode) {
		case SELECT:
			selectBtn.setBackground(selectedColor);
			break;
		case ASSOCIATION:
			assocBtn.setBackground(selectedColor);
			break;
		case GENERALIZATION:
			genBtn.setBackground(selectedColor);
			break;
		case COMPOSITION:
			compBtn.setBackground(selectedColor);
			break;
		case RECT:
			rectBtn.setBackground(selectedColor);
			break;
		case OVAL:
			ovalBtn.setBackground(selectedColor);
			break;
		default:
			break;
		}
	}
}
