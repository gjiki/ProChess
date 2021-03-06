package Game.Model;

import Game.Model.Pieces.*;
import javafx.util.Pair;
//import org.jetbrains.annotations.Nullable;

import java.util.Vector;

public abstract class Piece implements Cloneable{
    public static Piece createPiece(Constants.pieceType t, Constants.pieceColor col){
        switch (t){
            case King: return new King(col);
            case Pawn: return new Pawn(col);
            case Rook: return new Rook(col);
            case Queen: return new Queen(col);
            case Bishop: return new Bishop(col);
            case Knight: return new Knight(col);
            default: return null;
        }
    }

    public static Piece createPiece(Constants.pieceType t, Constants.pieceColor col, boolean hasMoved){
        switch (t){
            case King: return new King(col, hasMoved);
            case Pawn: return new Pawn(col, hasMoved);
            case Rook: return new Rook(col, hasMoved);
            case Queen: return new Queen(col, hasMoved);
            case Bishop: return new Bishop(col, hasMoved);
            case Knight: return new Knight(col, hasMoved);
            default: return null;
        }
    }

    // This method is called when piece has done a move
    public abstract void hasMoved();

    // This method checks if king is safe
    protected static boolean noCheckCaused(int row, int col, int newRow, int newCol,
                                  Vector<Vector<Cell>> state, Pair<Integer,Integer> allieKingPos) {
        boolean NoCheck = true;
        // Save pieces on given coordinates
        Piece killedPiece=null;
        Piece curPiece=null;
        if(newRow!=row || newCol!=col) {
            killedPiece = state.get(newRow).get(newCol).getPiece();
            curPiece = state.get(row).get(col).getPiece();
            // make move
            state.get(row).get(col).removePiece();
            state.get(newRow).get(newCol).putPiece(curPiece);
        }
        // Check if king is safe after this move
        if(checkForCheck(state, allieKingPos))
            NoCheck = false ;
        // Undo move
        if(newRow!=row || newCol!=col) {
            state.get(row).get(col).putPiece(curPiece);
            state.get(newRow).get(newCol).putPiece(killedPiece);
        }
        return NoCheck;
    }

    // This method checks every possible cell
    // where king can be killed from
    private static boolean checkForCheck( Vector<Vector<Cell>> state, Pair<Integer, Integer> allieKingPos){
        int r = allieKingPos.getKey();
        int c = allieKingPos.getValue();
        Constants.pieceColor kingColor = state.get(r).get(c).getPieceColor();

        if(checkKnights(r, c, state, kingColor) ||
                checkPawns(r, c, state, kingColor) ||
                checkDiagonals(r, c, state, kingColor) ||
                checkRowsAndCols(r, c, state, kingColor)) return true;

        return false;
    }

    // Ths method checks rows and columns where king is places
    private static boolean checkRowsAndCols(int row, int col,
                                            Vector<Vector<Cell>> state, Constants.pieceColor kingColor) {
        Piece firstPiece;
        int[] dr = new int[]{1,0,-1,0};
        int[] dc = new int[]{0,1,0,-1};

        for(int i=0; i < 4; i++) {
            firstPiece = findFirstPiece(row, col, dr[i], dc[i],state);
            if (firstPiece != null && firstPiece.getColor() != kingColor &&
                    (firstPiece.getType()==Constants.pieceType.Rook || firstPiece.getType() == Constants.pieceType.Queen))
                return true;
        }

        return false;
    }

    // This method finds first place where
    // other piece is placed in kings row and column
    private static Piece findFirstPiece(int row, int col, int dr, int dc,
                                        Vector<Vector<Cell>> state) {
        int curRow = row+dr;
        int curCol = col+dc;

        while(true) {
            if (!inbounds(curRow, curCol))
                return null;
            if (state.get(curRow).get(curCol).hasPiece())
                return state.get(curRow).get(curCol).getPiece();
            curRow += dr;
            curCol += dc;
        }
    }

    // This method checks diagonals from king's position
    private static boolean checkDiagonals(int row, int col, Vector<Vector<Cell>> state, Constants.pieceColor kingColor) {
        Piece firstPiece;
        int[] dr = new int[]{1,1,-1,-1};
        int[] dc = new int[]{-1,1,1,-1};

        for(int i=0; i < 4; i++) {
            firstPiece = findFirstPiece(row, col, dr[i], dc[i], state);
            if (firstPiece != null && firstPiece.getColor() != kingColor &&
                    (firstPiece.getType()==Constants.pieceType.Bishop ||
                            firstPiece.getType() == Constants.pieceType.Queen))
                return true;
        }

        return false;
    }

    // This method checks every possible position
    // where opponent's pawn can be placed
    private static boolean checkPawns(int row, int col, Vector<Vector<Cell>> state,
                                      Constants.pieceColor kingColor) {
        if(kingColor == Constants.pieceColor.black){
            if(inbounds(row-1,col-1) &&
                    state.get(row-1).get(col-1).hasPiece() && state.get(row-1).get(col-1).getPieceColor() != kingColor &&
                    state.get(row-1).get(col-1).getPieceType() == Constants.pieceType.Pawn)
                return true;
            if(inbounds(row-1,col+1) &&
                    state.get(row-1).get(col+1).hasPiece() && state.get(row-1).get(col+1).getPieceColor() != kingColor &&
                    state.get(row-1).get(col+1).getPieceType() == Constants.pieceType.Pawn)
                return true;
        }else{
            if(inbounds(row+1,col-1) &&
                    state.get(row+1).get(col-1).hasPiece() && state.get(row+1).get(col-1).getPieceColor() != kingColor &&
                    state.get(row+1).get(col-1).getPieceType() == Constants.pieceType.Pawn)
                return true;
            if(inbounds(row+1,col+1) &&
                    state.get(row+1).get(col+1).hasPiece() && state.get(row+1).get(col+1).getPieceColor() != kingColor &&
                    state.get(row+1).get(col+1).getPieceType() == Constants.pieceType.Pawn)
                return true;
        }
        return false;
    }

    // This method checks if cell is in bounds of board
    private static boolean inbounds(int row, int col) {
        if(row<0 || row>= Constants.NUMBER_OF_ROWS) return false;
        if (col<0|| col>= Constants.NUMBER_OF_COLUMNS) return false;
        return true;
    }

    // This method checks every possible position
    // where opponent's knights can be placed
    private static boolean checkKnights(int row, int col, Vector<Vector<Cell>> state, Constants.pieceColor kingColor) {
        int[] dr = new int[] {2, 2, 1, 1, -1, -1, -2, -2};
        int[] dc = new int[] {-1, 1, -2, 2, -2, 2, -1, 1};

        for(int i = 0; i < 8; i++){
            if(inbounds(row+dr[i], col+dc[i]) &&
                    state.get(row + dr[i]).get(col+dc[i]).hasPiece() &&
                    state.get(row + dr[i]).get(col+dc[i]).getPieceColor()!=kingColor &&
                    state.get(row + dr[i]).get(col+dc[i]).getPieceType() == Constants.pieceType.Knight)
                return true;
        }

        return false;
    }

    public abstract Vector< Pair<Integer, Integer> > possibleMoves(int row, int col,
                                                          Vector<Vector<Cell>> state, Pair<Integer,Integer> allieKingPos);
    public abstract Constants.pieceColor getColor();

    public abstract boolean getHasMoved();

    public abstract void setHasMoved(boolean b);

    public abstract Constants.pieceType getType();

    public abstract String toString();

    @Override
    protected abstract Object clone() throws CloneNotSupportedException;
}
