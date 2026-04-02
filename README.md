# 🅿️ Smart Parking Slot Finder

A Java Swing GUI application that uses **BFS (Breadth-First Search)** and **Greedy Algorithm** to find the nearest available parking slot from the entry point.

## 📌 Project Info
- **Subject:** Design and Analysis of Algorithms (DAA)
- **Type:** PBL (Project Based Learning)
- **Language:** Java (Swing GUI)

## 🧠 Algorithms Used

### 1. BFS – Breadth First Search (Graph Algorithm)
- Treats the parking lot as a **graph/grid**
- Explores all slots level by level from the entry point
- Guarantees the **shortest path** to any slot

### 2. Greedy Algorithm
- Among all slots found by BFS, **greedily picks the one with minimum distance**
- Ensures the most optimal slot is always recommended

## 🎮 Features
- Visual 5×6 parking grid with color-coded slots
- Click any slot to **toggle occupied/available**
- **Find Nearest Slot** — runs BFS + Greedy and highlights the best slot
- **Blue path** shows the exact route from entry to recommended slot
- **Randomize** button to simulate real parking scenarios
- **Reset** to clear all slots

## 🎨 Color Guide
| Color | Meaning |
|-------|---------|
| 🟣 Purple | Entry Point |
| 🟢 Green | Available Slot |
| 🔴 Red | Occupied Slot |
| 🔵 Blue | BFS Path |
| 🟡 Yellow | Best Slot (Greedy Pick) |

## 🚀 How to Run

### Prerequisites
- Java JDK 8 or higher installed

### Steps
```bash
# 1. Clone the repo
git clone https://github.com/YOUR_USERNAME/smart-parking-finder.git
cd smart-parking-finder

# 2. Compile all Java files
javac -d out src/*.java

# 3. Run the application
java -cp out Main
```

## 📁 Project Structure
```
SmartParkingFinder/
├── src/
│   ├── Main.java            # Entry point
│   ├── ParkingSlot.java     # Slot model
│   ├── ParkingAlgorithm.java # BFS + Greedy logic
│   └── ParkingGUI.java      # Swing GUI
└── README.md
```

## 👨‍💻 Algorithm Complexity
| Algorithm | Time Complexity | Space Complexity |
|-----------|----------------|-----------------|
| BFS | O(V + E) = O(rows × cols) | O(rows × cols) |
| Greedy Selection | O(1) | O(1) |

---
*Built for DAA PBL Presentation*
