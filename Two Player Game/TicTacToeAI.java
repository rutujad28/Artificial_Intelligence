import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class TicTacToeAI {
    private static final int HUMAN = -1;
    private static final int COMP = +1;
    private static final int[][] board = new int[3][3];

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        char h_choice = ' '; // human
        char c_choice = ' '; // computer
        char first = ' '; // who will play first

        // Human chooses X or O to play
        while (h_choice != 'O' && h_choice != 'X') {
            System.out.print("Choose X or O\nChosen: ");
            h_choice = scanner.next().toUpperCase().charAt(0);
        }

        // Setting computer's choice
        if (h_choice == 'X') {
            c_choice = 'O';
        } else {
            c_choice = 'X';
        }

        // Human may start first
        System.out.println("First to start?[y/n]: ");
        while (first != 'Y' && first != 'N') {
            try {
                String input = scanner.nextLine().toUpperCase();
                if (input.length() > 0) {
                    first = input.charAt(0);
                }
            } catch (Exception e) {
                System.out.println("Bad choice");
            }
        }

        // Main loop of this game
        while (emptyCells(board).size() > 0 && !gameOver(board)) {
            if (first == 'N') {
                aiTurn(c_choice, h_choice);
                first = ' ';
            }

            humanTurn(c_choice, h_choice, scanner);
            aiTurn(c_choice, h_choice);
        }

        // Game over message
        if (wins(board, HUMAN)) {
            System.out.println("Human Turn" + h_choice);
            render(board, c_choice, h_choice);
            System.out.println("YOU WIN! How is this Possible?");
        } else if (wins(board, COMP)) {
            render(board, c_choice, h_choice);
            System.out.println("YOU LOSE!");
        } else {
            render(board, c_choice, h_choice);
            System.out.println("DRAW!");
        }
    }

    private static int evaluate(int[][] state) {
        if (wins(state, COMP)) {
            return +1;
        } else if (wins(state, HUMAN)) {
            return -1;
        } else {
            return 0;
        }
    }

    private static boolean wins(int[][] state, int player) {
        int[][] winState = {
                { state[0][0], state[0][1], state[0][2] },
                { state[1][0], state[1][1], state[1][2] },
                { state[2][0], state[2][1], state[2][2] },
                { state[0][0], state[1][0], state[2][0] },
                { state[0][1], state[1][1], state[2][1] },
                { state[0][2], state[1][2], state[2][2] },
                { state[0][0], state[1][1], state[2][2] },
                { state[2][0], state[1][1], state[0][2] }
        };
        for (int[] row : winState) {
            if (row[0] == player && row[1] == player && row[2] == player) {
                return true;
            }
        }
        return false;
    }

    private static boolean gameOver(int[][] state) {
        return wins(state, HUMAN) || wins(state, COMP);
    }

    private static List<int[]> emptyCells(int[][] state) {
        List<int[]> cells = new ArrayList<>();
        for (int x = 0; x < state.length; x++) {
            for (int y = 0; y < state[0].length; y++) {
                if (state[x][y] == 0) {
                    cells.add(new int[] { x, y });
                }
            }
        }
        return cells;
    }

    private static boolean validMove(int x, int y) {
        List<int[]> cells = emptyCells(board);
        for (int[] cell : cells) {
            if (cell[0] == x && cell[1] == y) {
                return true;
            }
        }
        return false;
    }

    private static boolean setMove(int x, int y, int player) {
        if (validMove(x, y)) {
            board[x][y] = player;
            return true;
        } else {
            return false;
        }

    }

    private static int[] minimax(int[][] state, int depth, int player) {
        int[] best = { -1, -1, (player == COMP) ? Integer.MIN_VALUE : Integer.MAX_VALUE };

        if (depth == 0 || gameOver(state)) {
            int score = evaluate(state);
            return new int[] { -1, -1, score };
        }

        for (int[] cell : emptyCells(state)) {
            int x = cell[0];
            int y = cell[1];
            state[x][y] = player;
            int[] score = minimax(state, depth - 1, -player);
            state[x][y] = 0;
            score[0] = x;
            score[1] = y;

            if (player == COMP) {
                if (score[2] > best[2]) {
                    best = score; // max value
                }
            } else {
                if (score[2] < best[2]) {
                    best = score; // min value
                }
            }
        }
        return best;
    }

    public static void render(int[][] state, char cChoice, char hChoice) {
        Map<Integer, Character> chars = new HashMap<>();
        chars.put(-1, hChoice);
        chars.put(1, cChoice);
        chars.put(0, ' ');

        String strLine = "---------------";

        System.out.println("\n" + strLine);
        for (int[] row : state) {
            for (int cell : row) {
                char symbol = chars.get(cell);
                System.out.print("| " + symbol + " |");
            }
            System.out.println("\n" + strLine);
        }
    }

    private static void aiTurn(char cChoice, char hChoice) {
        int depth = emptyCells(board).size();
        if (depth == 0 || gameOver(board)) {
            return;
        }

        System.out.println("Computer turn [" + cChoice + "]");
        render(board, cChoice, hChoice);

        int[] bestMove;
        if (depth == 9) {
            Random rand = new Random();
            int x = rand.nextInt(3);
            int y = rand.nextInt(3);
            bestMove = new int[] { x, y };
        } else {
            bestMove = findBestMove(board, HUMAN);
        }

        setMove(bestMove[0], bestMove[1], COMP);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void humanTurn(char cChoice, char hChoice, Scanner scanner) {
        int depth = emptyCells(board).size();
        if (depth == 0 || gameOver(board)) {
            return;
        }

        int move = -1;
        int[][] moves = {
                { 0, 0 }, { 0, 1 }, { 0, 2 },
                { 1, 0 }, { 1, 1 }, { 1, 2 },
                { 2, 0 }, { 2, 1 }, { 2, 2 }
        };

        System.out.println("Human turn [" + hChoice + "]");
        render(board, cChoice, hChoice);

        while (move < 1 || move > 9) {
            try {
                System.out.print("Use numpad (1..9): ");
                move = scanner.nextInt();
                int[] coord = moves[move - 1];
                boolean canMove = setMove(coord[0], coord[1], HUMAN);

                if (!canMove) {
                    System.out.println("Bad move");
                    move = -1;
                }
            } catch (Exception e) {
                System.out.println("Bad choice");
                scanner.next();
            }
        }
    }

    private static int[] findBestMove(int[][] board, int player) {
        int[] bestChild = null;
        int previous = Integer.MIN_VALUE;
        List<int[]> positions = emptyCells(board);
        for (int[] position : positions) {
            int x = position[0];
            int y = position[1];
            board[x][y] = COMP;
            int[] current = minimax(board, emptyCells(board).size(), HUMAN);
            board[x][y] = 0;
            System.out.println("Heuristic Value: " + current[2]);
            if (current[2] > previous) {
                bestChild = position;
                previous = current[2];
            }
        }
        return bestChild;
    }
}
