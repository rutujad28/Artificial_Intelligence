import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Solution {

    public static PriorityQueue<State> pq = new PriorityQueue<>();
    public static ArrayList<State> expanded = new ArrayList<>();
    public static String[][] goal;

    public Solution(State first) {
        if (first == null) {
            System.out.println("Please provide an input");
        }
        pq.add(first);
        ArrayList<State> list = new ArrayList<>();
        while (!pq.isEmpty()) {
            State current = pq.poll();
            expanded.add(current);
            if (Arrays.deepEquals(current.blocks, goal)) {
                break;
            }
            list = current.expand(current);
            for (State l : list) {
                boolean visited = false;
                for (State e : expanded) {
                    if (Arrays.deepEquals(l.blocks, e.blocks)) {
                        visited = true;
                        break;
                    }
                }
                if (visited)
                    continue;
                pq.add(l);
            }
        }
    }

    public static void main(String args[]) {
        String a[][];
        int i, j, rows, columns;
        rows = columns = 3;
        Scanner sc = new Scanner(System.in);
        a = new String[rows][columns];
        goal = new String[rows][columns];
        System.out.println("Please input the elements for initial state :");

        for (i = 0; i < a.length; i++) {
            for (j = 0; j < a.length; j++) {
                a[i][j] = sc.nextLine();
                if (a[i][j].length() != 1 || (a[i][j].charAt(0) < '1' && a[i][j].charAt(0) != ' ')
                        || a[i][j].charAt(0) > '8') {
                    System.out.println(
                            "Error: Input should be any number between 1 to 8 or a single space\nProgram Terminated");
                    return;
                }
            }
        }
        System.out.println("Please input the Goal state:");

        for (i = 0; i < goal.length; i++) {
            for (j = 0; j < goal.length; j++) {
                goal[i][j] = sc.nextLine();
                if (goal[i][j].length() != 1 || (goal[i][j].charAt(0) < '1' && goal[i][j].charAt(0) != ' ')
                        || goal[i][j].charAt(0) > '8') {
                    System.out.println(
                            "Error: Input should be any number between 1 to 8 or a single space\nProgram Terminated");
                    return;
                }
            }
        }

        // Check if the initial state is solvable
        int[][] initialState = new int[rows][columns];
        for (i = 0; i < rows; i++) {
            for (j = 0; j < columns; j++) {
                initialState[i][j] = a[i][j].equals(" ") ? 0 : Integer.parseInt(a[i][j]);
            }
        }

        if (!isSolvable(initialState)) {
            System.out.println("This puzzle is not solvable");
            return;
        }

        State state = new State(a, 0);
        new Solution(state);
        for (State states : expanded) {
            for (int l = 0; l < 3; l++) {
                for (int m = 0; m < 3; m++) {
                    System.out.print(states.blocks[l][m] + "\t");
                }
                System.out.println();
            }
            System.out.println("f(n) :" + states.f);
            System.out.println("h(n) :" + (states.f - states.level));
            System.out.println("g(n) :" + (states.level));
            System.out.println('\n');
        }
        System.out.println("Total Nodes expanded :" + expanded.size());
        System.out.println("Total Nodes generated:" + (expanded.size() + pq.size()));
    }

    static int getInvCount(int[] arr) {
        int inv_count = 0;
        for (int i = 0; i < 9; i++)
            for (int j = i + 1; j < 9; j++)
                if (arr[i] > 0 && arr[j] > 0 && arr[i] > arr[j])
                    inv_count++;
        return inv_count;
    }

    static boolean isSolvable(int[][] puzzle) {
        int linearPuzzle[] = new int[9];
        int k = 0;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                linearPuzzle[k++] = puzzle[i][j];
        int invCount = getInvCount(linearPuzzle);
        return (invCount % 2 == 0);
    }
}

class State implements Comparable<State> {
    public int f;
    public String[][] blocks;
    public int level;

    public State(String[][] a, int level) {
        int N = a.length;
        this.blocks = new String[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                this.blocks[i][j] = a[i][j];
            }
        }
        this.level = level;
        this.f = manhattan() + level;
    }

    private int manhattan() {
        int sum = 0;
        int[] index = new int[2];
        int N = Solution.goal.length;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (this.blocks[i][j].trim().isEmpty()) {
                    continue;
                }
                index = find_index(Integer.parseInt(this.blocks[i][j]));
                sum = sum + (Math.abs(i - index[0]) + Math.abs(j - index[1]));
            }
        }
        return sum;
    }

    private int[] find_index(int a) {
        int[] index = new int[2];
        int N = Solution.goal.length;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (Solution.goal[i][j].trim().isEmpty()) {
                    continue;
                }
                if (Solution.goal[i][j].trim().equals(String.valueOf(a))) {
                    index[0] = i;
                    index[1] = j;
                    return index;
                }
            }
        }
        return index;
    }

    public ArrayList<State> expand(State parent) {
        ArrayList<State> successor = new ArrayList<>();
        int N = this.blocks.length;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (parent.blocks[i][j].trim().isEmpty()) {
                    if (i - 1 >= 0) {
                        String[][] a = new String[N][N];
                        for (int l = 0; l < N; l++) {
                            for (int m = 0; m < N; m++) {
                                a[l][m] = parent.blocks[l][m];
                            }
                        }
                        a = swap(a, i, j, i - 1, j);
                        State b = new State(a, parent.level + 1);
                        successor.add(b);
                    }
                    if (j - 1 >= 0) {
                        String[][] a = new String[N][N];
                        for (int l = 0; l < N; l++) {
                            for (int m = 0; m < N; m++) {
                                a[l][m] = parent.blocks[l][m];
                            }
                        }
                        a = swap(a, i, j, i, j - 1);
                        State b = new State(a, parent.level + 1);
                        successor.add(b);
                    }
                    if (i + 1 < N) {
                        String[][] a = new String[N][N];
                        for (int l = 0; l < N; l++) {
                            for (int m = 0; m < N; m++) {
                                a[l][m] = parent.blocks[l][m];
                            }
                        }
                        a = swap(a, i, j, i + 1, j);
                        State b = new State(a, parent.level + 1);
                        successor.add(b);
                    }
                    if (j + 1 < N) {
                        String[][] a = new String[N][N];
                        for (int l = 0; l < N; l++) {
                            for (int m = 0; m < N; m++) {
                                a[l][m] = parent.blocks[l][m];
                            }
                        }
                        a = swap(a, i, j, i, j + 1);
                        State b = new State(a, parent.level + 1);
                        successor.add(b);
                    }
                }
            }
        }
        return successor;
    }

    private String[][] swap(String[][] a, int row1, int col1, int row2, int col2) {
        String[][] copy = new String[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) {
            System.arraycopy(a[i], 0, copy[i], 0, a[i].length);
        }
        String tmp = copy[row1][col1];
        copy[row1][col1] = copy[row2][col2];
        copy[row2][col2] = tmp;
        return copy;
    }

    @Override
    public int compareTo(State o) {
        if (this.f == o.f) {
            return this.manhattan() - o.manhattan();
        }
        return this.f - o.f;
    }
}
