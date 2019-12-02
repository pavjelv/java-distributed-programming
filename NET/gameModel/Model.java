package NET.gameModel;

import javax.naming.InsufficientResourcesException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Model implements Serializable {
    public List<List<PointFlag>> getOwnMap() {
        return ownMap;
    }

    public void setOwnMap(List<List<PointFlag>> ownMap) {
        this.ownMap = ownMap;
    }

    public List<List<PointFlag>> getOpponentMap() {
        return opponentMap;
    }

    public void setOpponentMap(List<List<PointFlag>> opponentMap) {
        this.opponentMap = opponentMap;
    }

    public PointFlag getOperation() {
        return operation;
    }

    public void setOperation(PointFlag operation) {
        this.operation = operation;
    }

    List<List<PointFlag>> ownMap;
    List<List<PointFlag>> opponentMap;
    PointFlag operation;

    public Model(int size) {
        ownMap = new ArrayList<>();
        opponentMap = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ownMap.add(new ArrayList<>(size));
            opponentMap.add(new ArrayList<>(size));
        }
    }

    @Override
    public String toString() {
        return "Model{" +
                "ownMap=" + ownMap +
                ", opponentMap=" + opponentMap +
                ", operation=" + operation +
                '}';
    }

    public Model(List<List<PointFlag>> ownMap, List<List<PointFlag>> opponentMap, PointFlag operation) {
        this.ownMap = ownMap;
        this.opponentMap = opponentMap;
        this.operation = operation;
    }
}
