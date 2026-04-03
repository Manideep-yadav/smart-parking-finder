import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ParkingGUI extends JFrame {

    private static final int ROWS = 5;
    private static final int COLS = 6;
    private static final int CELL_SIZE = 90;

    // Entry point is fixed at top-left (0,0)
    private static final int ENTRY_ROW = 0;
    private static final int ENTRY_COL = 0;

    private ParkingSlot[][] grid = new ParkingSlot[ROWS][COLS];
    private JButton[][] buttons = new JButton[ROWS][COLS];
    private ParkingAlgorithm algorithm;

    private JLabel statusLabel;
    private JLabel resultLabel;
    private int[] recommendedSlot = null;
    private List<int[]> currentPath = null;

    // Colors
    private final Color COLOR_AVAILABLE   = new Color(76, 175, 80);   // Green
    private final Color COLOR_OCCUPIED    = new Color(244, 67, 54);    // Red
    private final Color COLOR_RECOMMENDED = new Color(255, 235, 59);   // Yellow
    private final Color COLOR_PATH        = new Color(33, 150, 243);   // Blue
    private final Color COLOR_ENTRY       = new Color(156, 39, 176);   // Purple
    private final Color COLOR_BG          = new Color(30, 30, 40);     // Dark bg
    private final Color COLOR_PANEL       = new Color(45, 45, 60);     // Panel bg

    public ParkingGUI() {
        setTitle("Smart Parking Slot Finder");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout(10, 10));

        initGrid();
        algorithm = new ParkingAlgorithm(ROWS, COLS, grid);

        add(buildTitlePanel(), BorderLayout.NORTH);
        add(buildGridPanel(), BorderLayout.CENTER);
        add(buildControlPanel(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void initGrid() {
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                grid[r][c] = new ParkingSlot(r, c);

        // Pre-occupy some random slots to make it realistic
        int[][] preOccupied = {{0,2},{1,1},{1,4},{2,3},{3,0},{3,5},{4,2},{4,4},{0,5},{2,1}};
        for (int[] pos : preOccupied)
            grid[pos[0]][pos[1]].setOccupied(true);
    }

    private JPanel buildTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 20, 5, 20));

        JLabel title = new JLabel("Smart Parking Slot Finder", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Algorithm: BFS (Graph Traversal) + Greedy Selection", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        subtitle.setForeground(new Color(180, 180, 200));

        panel.add(title, BorderLayout.NORTH);
        panel.add(subtitle, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildGridPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(10, 10));
        wrapper.setBackground(COLOR_BG);
        wrapper.setBorder(new EmptyBorder(10, 20, 10, 20));

        JPanel gridPanel = new JPanel(new GridLayout(ROWS, COLS, 6, 6));
        gridPanel.setBackground(COLOR_BG);

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                JButton btn = createSlotButton(r, c);
                buttons[r][c] = btn;
                gridPanel.add(btn);
            }
        }

        // Legend
        JPanel legend = buildLegend();

        wrapper.add(gridPanel, BorderLayout.CENTER);
        wrapper.add(legend, BorderLayout.SOUTH);
        return wrapper;
    }

    private JButton createSlotButton(int r, int c) {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 80), 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        updateButtonAppearance(btn, r, c);

        // Click to toggle occupied/available
        btn.addActionListener(e -> {
            if (r == ENTRY_ROW && c == ENTRY_COL) return; // Can't toggle entry
            grid[r][c].setOccupied(!grid[r][c].isOccupied());
            recommendedSlot = null;
            currentPath = null;
            refreshGrid();
            statusLabel.setText("Slot " + grid[r][c].getSlotId() +
                (grid[r][c].isOccupied() ? " marked OCCUPIED" : " marked AVAILABLE"));
        });

        return btn;
    }

    private void updateButtonAppearance(JButton btn, int r, int c) {
        String label = grid[r][c].getSlotId();

        if (r == ENTRY_ROW && c == ENTRY_COL) {
            btn.setBackground(COLOR_ENTRY);
            btn.setForeground(Color.WHITE);
            btn.setText("<html><center>ENTRY<br>▶</center></html>");
            return;
        }

        // Check if on path
        boolean onPath = false;
        if (currentPath != null) {
            for (int[] p : currentPath) {
                if (p[0] == r && p[1] == c && !(r == ENTRY_ROW && c == ENTRY_COL)) {
                    onPath = true; break;
                }
            }
        }

        // Check if recommended
        boolean isRecommended = recommendedSlot != null &&
            recommendedSlot[0] == r && recommendedSlot[1] == c;

        if (isRecommended) {
            btn.setBackground(COLOR_RECOMMENDED);
            btn.setForeground(Color.BLACK);
            btn.setText("<html><center>" + label + "<br>[BEST]</center></html>");
        } else if (onPath) {
            btn.setBackground(COLOR_PATH);
            btn.setForeground(Color.WHITE);
            btn.setText("<html><center>" + label + "<br>→</center></html>");
        } else if (grid[r][c].isOccupied()) {
            btn.setBackground(COLOR_OCCUPIED);
            btn.setForeground(Color.WHITE);
            btn.setText("<html><center>" + label + "<br>🚗</center></html>");
        } else {
            btn.setBackground(COLOR_AVAILABLE);
            btn.setForeground(Color.WHITE);
            btn.setText("<html><center>" + label + "<br>FREE</center></html>");
        }
    }

    private JPanel buildLegend() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        panel.setBackground(COLOR_BG);

        addLegendItem(panel, COLOR_ENTRY, "Entry Point");
        addLegendItem(panel, COLOR_AVAILABLE, "Available");
        addLegendItem(panel, COLOR_OCCUPIED, "Occupied");
        addLegendItem(panel, COLOR_PATH, "BFS Path");
        addLegendItem(panel, COLOR_RECOMMENDED, "Best Slot (Greedy)");

        return panel;
    }

    private void addLegendItem(JPanel panel, Color color, String text) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        item.setBackground(COLOR_BG);

        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(16, 16));
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JLabel label = new JLabel(text);
        label.setForeground(new Color(200, 200, 210));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        item.add(colorBox);
        item.add(label);
        panel.add(item);
    }

    private JPanel buildControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 8));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(new EmptyBorder(12, 20, 15, 20));

        // Buttons row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setBackground(COLOR_PANEL);

        JButton findBtn = makeButton("Find Nearest Slot", new Color(33, 150, 243));
        JButton resetBtn = makeButton("Reset All", new Color(100, 100, 120));
        JButton randomBtn = makeButton("Randomize", new Color(255, 152, 0));

        findBtn.addActionListener(e -> findNearestSlot());
        resetBtn.addActionListener(e -> resetAll());
        randomBtn.addActionListener(e -> randomize());

        btnRow.add(findBtn);
        btnRow.add(randomBtn);
        btnRow.add(resetBtn);

        // Status labels
        statusLabel = new JLabel("Click a slot to toggle occupied/available. Then click Find Nearest Slot.", SwingConstants.CENTER);
        statusLabel.setForeground(new Color(180, 180, 200));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        resultLabel = new JLabel(" ", SwingConstants.CENTER);
        resultLabel.setForeground(new Color(255, 235, 59));
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JPanel labelsPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        labelsPanel.setBackground(COLOR_PANEL);
        labelsPanel.add(statusLabel);
        labelsPanel.add(resultLabel);

        panel.add(btnRow, BorderLayout.NORTH);
        panel.add(labelsPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void findNearestSlot() {
        algorithm = new ParkingAlgorithm(ROWS, COLS, grid);
        int[] result = algorithm.findNearestSlot(ENTRY_ROW, ENTRY_COL);

        if (result == null) {
            recommendedSlot = null;
            currentPath = null;
            refreshGrid();
            statusLabel.setText("No available slots found!");
            resultLabel.setText("Parking lot is FULL");
            return;
        }

        recommendedSlot = result;
        currentPath = algorithm.getPath(ENTRY_ROW, ENTRY_COL, result[0], result[1]);
        refreshGrid();

        String slotId = grid[result[0]][result[1]].getSlotId();
        statusLabel.setText("BFS explored the grid. Greedy selected the nearest available slot.");
        resultLabel.setText(">> Best Slot: " + slotId + "  |  Distance: " + result[2] + " steps  |  Path shown in blue");
    }

    private void resetAll() {
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                grid[r][c].setOccupied(false);
        recommendedSlot = null;
        currentPath = null;
        refreshGrid();
        statusLabel.setText("All slots cleared. Click Find Nearest Slot to begin.");
        resultLabel.setText(" ");
    }

    private void randomize() {
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++) {
                if (r == ENTRY_ROW && c == ENTRY_COL) continue;
                grid[r][c].setOccupied(Math.random() < 0.45);
            }
        recommendedSlot = null;
        currentPath = null;
        refreshGrid();
        statusLabel.setText("Parking lot randomized! Click Find Nearest Slot.");
        resultLabel.setText(" ");
    }

    private void refreshGrid() {
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                updateButtonAppearance(buttons[r][c], r, c);
    }
}
