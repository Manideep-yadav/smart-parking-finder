public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            ParkingGUI gui = new ParkingGUI();
            gui.setVisible(true);
        });
    }
}
