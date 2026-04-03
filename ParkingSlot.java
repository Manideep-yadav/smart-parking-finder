public class ParkingSlot {
    private int row, col;
    private boolean occupied;
    private String slotId;

    public ParkingSlot(int row, int col) {
        this.row = row;
        this.col = col;
        this.occupied = false;
        this.slotId = "P" + (row + 1) + "-" + (col + 1);
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public boolean isOccupied() { return occupied; }
    public void setOccupied(boolean occupied) { this.occupied = occupied; }
    public String getSlotId() { return slotId; }
}
