import java.util.*;

public class ParkingAlgorithm {

    private int rows, cols;
    private ParkingSlot[][] grid;

    // Directions for BFS: up, down, left, right
    private int[][] directions = {{-1,0},{1,0},{0,-1},{0,1}};

    public ParkingAlgorithm(int rows, int cols, ParkingSlot[][] grid) {
        this.rows = rows;
        this.cols = cols;
        this.grid = grid;
    }

    /**
     * BFS from entry point to find all reachable slots with their distances.
     * Then Greedy picks the nearest available slot.
     * @param entryRow - entry row
     * @param entryCol - entry col
     * @return int[] {bestRow, bestCol, distance} or null if no slot found
     */
    public int[] findNearestSlot(int entryRow, int entryCol) {
        boolean[][] visited = new boolean[rows][cols];
        Queue<int[]> queue = new LinkedList<>();
        // int[] = {row, col, distance}
        queue.add(new int[]{entryRow, entryCol, 0});
        visited[entryRow][entryCol] = true;

        // Greedy: track best slot by minimum distance
        int bestRow = -1, bestCol = -1, bestDist = Integer.MAX_VALUE;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int r = current[0], c = current[1], dist = current[2];

            // Check if this cell is an available slot (not entry point)
            if (!(r == entryRow && c == entryCol)) {
                if (!grid[r][c].isOccupied()) {
                    // Greedy: pick the slot with minimum distance
                    if (dist < bestDist) {
                        bestDist = dist;
                        bestRow = r;
                        bestCol = c;
                    }
                    // Once we found slots at this BFS level, no need to go deeper
                    // (BFS guarantees shortest path, greedy picks best at this level)
                    if (dist > bestDist) break;
                }
            }

            // Explore neighbors
            for (int[] dir : directions) {
                int nr = r + dir[0];
                int nc = c + dir[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && !visited[nr][nc]) {
                    visited[nr][nc] = true;
                    queue.add(new int[]{nr, nc, dist + 1});
                }
            }
        }

        if (bestRow == -1) return null;
        return new int[]{bestRow, bestCol, bestDist};
    }

    /**
     * BFS to get the actual path from entry to target slot
     */
    public List<int[]> getPath(int entryRow, int entryCol, int targetRow, int targetCol) {
        boolean[][] visited = new boolean[rows][cols];
        int[][][] parent = new int[rows][cols][2];
        for (int[][] row : parent)
            for (int[] cell : row) { cell[0] = -1; cell[1] = -1; }

        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{entryRow, entryCol});
        visited[entryRow][entryCol] = true;

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int r = cur[0], c = cur[1];

            if (r == targetRow && c == targetCol) break;

            for (int[] dir : directions) {
                int nr = r + dir[0], nc = c + dir[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && !visited[nr][nc]) {
                    visited[nr][nc] = true;
                    parent[nr][nc][0] = r;
                    parent[nr][nc][1] = c;
                    queue.add(new int[]{nr, nc});
                }
            }
        }

        // Reconstruct path
        List<int[]> path = new ArrayList<>();
        int r = targetRow, c = targetCol;
        while (!(r == entryRow && c == entryCol)) {
            path.add(new int[]{r, c});
            int pr = parent[r][c][0];
            int pc = parent[r][c][1];
            r = pr; c = pc;
        }
        path.add(new int[]{entryRow, entryCol});
        Collections.reverse(path);
        return path;
    }
}
