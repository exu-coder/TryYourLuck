package com.my.luck;

public class ChessEngine {
    public static boolean isValidMove(char[][] board, int fromRow, int fromCol, int toRow, int toCol) {
        char piece = board[fromRow][fromCol];
        if (piece == ' ') return false;

        char target = board[toRow][toCol];
        if (target != ' ' && Character.isUpperCase(piece) == Character.isUpperCase(target)) {
            return false;
        }

        char p = Character.toLowerCase(piece);
        switch (p) {
            case 'p': return isValidPawnMove(board, fromRow, fromCol, toRow, toCol, Character.isUpperCase(piece));
            case 'r': return isValidRookMove(board, fromRow, fromCol, toRow, toCol);
            case 'n': return isValidKnightMove(fromRow, fromCol, toRow, toCol);
            case 'b': return isValidBishopMove(board, fromRow, fromCol, toRow, toCol);
            case 'q': return isValidQueenMove(board, fromRow, fromCol, toRow, toCol);
            case 'k': return isValidKingMove(fromRow, fromCol, toRow, toCol);
            default: return false;
        }
    }

    private static boolean isValidPawnMove(char[][] board, int fromRow, int fromCol, int toRow, int toCol, boolean isWhite) {
        int direction = isWhite ? -1 : 1;
        int startRow = isWhite ? 6 : 1;

        // Forward move
        if (fromCol == toCol && board[toRow][toCol] == ' ') {
            if (toRow == fromRow + direction) return true;
            if (fromRow == startRow && toRow == fromRow + 2 * direction && board[fromRow + direction][fromCol] == ' ') {
                return true;
            }
        }

        // Capture
        if (Math.abs(fromCol - toCol) == 1 && toRow == fromRow + direction) {
            return board[toRow][toCol] != ' ';
        }

        return false;
    }

    private static boolean isValidRookMove(char[][] board, int fromRow, int fromCol, int toRow, int toCol) {
        if (fromRow != toRow && fromCol != toCol) return false;

        int rowStep = Integer.compare(toRow - fromRow, 0);
        int colStep = Integer.compare(toCol - fromCol, 0);
        int r = fromRow + rowStep;
        int c = fromCol + colStep;

        while (r != toRow || c != toCol) {
            if (board[r][c] != ' ') return false;
            r += rowStep;
            c += colStep;
        }
        return true;
    }

    private static boolean isValidKnightMove(int fromRow, int fromCol, int toRow, int toCol) {
        int dr = Math.abs(fromRow - toRow);
        int dc = Math.abs(fromCol - toCol);
        return (dr == 2 && dc == 1) || (dr == 1 && dc == 2);
    }

    private static boolean isValidBishopMove(char[][] board, int fromRow, int fromCol, int toRow, int toCol) {
        if (Math.abs(fromRow - toRow) != Math.abs(fromCol - toCol)) return false;

        int rowStep = Integer.compare(toRow - fromRow, 0);
        int colStep = Integer.compare(toCol - fromCol, 0);
        int r = fromRow + rowStep;
        int c = fromCol + colStep;

        while (r != toRow || c != toCol) {
            if (board[r][c] != ' ') return false;
            r += rowStep;
            c += colStep;
        }
        return true;
    }

    private static boolean isValidQueenMove(char[][] board, int fromRow, int fromCol, int toRow, int toCol) {
        return isValidRookMove(board, fromRow, fromCol, toRow, toCol) ||
                isValidBishopMove(board, fromRow, fromCol, toRow, toCol);
    }

    private static boolean isValidKingMove(int fromRow, int fromCol, int toRow, int toCol) {
        return Math.abs(fromRow - toRow) <= 1 && Math.abs(fromCol - toCol) <= 1;
    }
}