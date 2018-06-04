package Game.Model;

import javafx.util.Pair;

import java.util.Vector;

public interface Piece {
    public enum pieceType{
        Bishop,
        King,
        Knight,
        Pawn,
        Queen,
        Rook
    }

    public Vector< Pair<Integer, Integer> > possibleMoves(int ind1, int ind2,
                                                          Vector<Vector<Cell>> state);
    public boolean getColor();
    public boolean getHasMove();
    public pieceType getType();
}