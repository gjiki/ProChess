package Game.Model.Pieces;

import Game.Model.Cell;
import Game.Model.Constants;
import Game.Model.Piece;
import javafx.util.Pair;

import java.util.Vector;

public class Bishop extends Piece {
    private final Constants.pieceColor color;
    private boolean hasMoved;

    // Constructor
    public Bishop(Constants.pieceColor color){
        this.color = color;
        this.hasMoved = false;
    }

    public Bishop(Constants.pieceColor color, boolean hasMoved){
        this.color = color;
        this.hasMoved = hasMoved;
    }

    @Override
    // Called when bishop has done a move
    public void hasMoved() {
        this.hasMoved = true;
    }

    @Override
    // Returns every possible move for bishop
    public Vector<Pair<Integer, Integer>> possibleMoves(int row, int col, Vector<Vector<Cell>> state,
                                                        Pair<Integer,Integer> allieKingPos) {
        Vector<Pair<Integer, Integer> > result = new Vector<Pair<Integer, Integer>>();

        findPossibleMoves(row, col, state, result, allieKingPos, 1, 1);
        findPossibleMoves(row, col, state, result, allieKingPos, -1, 1);
        findPossibleMoves(row, col, state, result, allieKingPos, 1, -1);
        findPossibleMoves(row, col, state, result, allieKingPos, -1, -1);

        return result;
    }

    @Override
    public Constants.pieceColor getColor() {
        return this.color;
    }

    @Override
    public boolean getHasMoved() {
        return this.hasMoved;
    }

    @Override
    public void setHasMoved(boolean b){
        this.hasMoved = b;
    }

    // This method finds all possible moves for bishop
    private void findPossibleMoves(int row, int col, Vector<Vector<Cell>> state,
                                   Vector<Pair<Integer, Integer>> result, Pair<Integer, Integer> allieKingPos, int dir1, int dir2){
        // making first step
        int curRow = row + dir1;
        int curCol = col + dir2;

        // checking if after this step rook will stay in board
        // and if that cell will be empty
        while( validMove(curRow, curCol, state) ){
            // Checking if check is caused
            if(noCheckCaused(row, col, curRow, curCol, state, allieKingPos))
                result.add(new Pair<>(curRow, curCol));

            // make another step
            curRow += dir1;
            curCol += dir2;
        }

        // Checking if rook can kill opponent's piece
        if( canKill(row, col, curRow, curCol, state, allieKingPos) )
            result.add(new Pair<>(curRow, curCol));
    }

    // This method checks if after given move piece stays in board,
    // if cell is empty and if move doesn't cause check
    private boolean validMove(int curRow, int curCol, Vector<Vector<Cell>> state){
        if(curRow >= 0 && curRow < Constants.NUMBER_OF_ROWS
                && curCol >= 0 && curCol < Constants.NUMBER_OF_COLUMNS
                && !(state.get(curRow).get(curCol).hasPiece()) ){
            return true;
        }

        return false;
    }

    // This method checks if bishop can kill
    // opponent's piece on given curRow and curCol
    private boolean canKill(int row, int col, int curRow, int curCol,
                            Vector<Vector<Cell>> state, Pair<Integer,Integer> allieKingPos) {
        if(curRow >= 0 && curRow < Constants.NUMBER_OF_ROWS
                && curCol >= 0 && curCol < Constants.NUMBER_OF_COLUMNS
                && state.get(curRow).get(curCol).hasPiece()
                && state.get(curRow).get(curCol).getPieceColor() != this.getColor()
                && noCheckCaused(row, col, curRow, curCol, state, allieKingPos)){
            return true;
        }

        return false;
    }

    @Override
    public Constants.pieceType getType() {
        return Constants.pieceType.Bishop;
    }

    @Override
    public String toString() {
        String col = "White";
        if(this.color == Constants.pieceColor.black)
            col = "Black";

        return col + " Bishop";
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Bishop newBishop = new Bishop(this.getColor(), this.getHasMoved());

        return newBishop;
    }
}
