package gameModel;

import javax.naming.InsufficientResourcesException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Model implements Serializable {
    public List<List<Integer>> getOwnMap() {
        return ownMap;
    }

    public void setOwnMap(List<List<Integer>> ownMap) {
        this.ownMap = ownMap;
    }

    public List<List<Integer>> getOpponentMap() {
        return opponentMap;
    }

    public void setOpponentMap(List<List<Integer>> opponentMap) {
        this.opponentMap = opponentMap;
    }

    public Integer getOperation() {
        return operation;
    }

    public void setOperation(Integer operation) {
        this.operation = operation;
    }

    List<List<Integer>> ownMap;
    List<List<Integer>> opponentMap;
    Integer operation;

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

    public Model(List<List<Integer>> ownMap, List<List<Integer>> opponentMap, Integer operation) {
        this.ownMap = ownMap;
        this.opponentMap = opponentMap;
        this.operation = operation;
    }
}
;