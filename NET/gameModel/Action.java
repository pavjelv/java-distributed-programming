package gameModel;

import javafx.util.Pair;

import java.util.List;

public class Action {
    Flag type;

    Id id;
    Object payload;
    List<List<PointFlag>> map;

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public Action(Id id) {
        type = Flag.START;
        this.id = id;
    }
    public Action(List<List<PointFlag>> map) {
        type = Flag.SET_FLEET;
        this.map = map;
    }

    public Action(Pair<Integer, Integer> attemptCoordinates) {
        this.type = Flag.TRY;
        this.attemptCoordinates = attemptCoordinates;
    }

    public Pair<Integer, Integer> getAttemptCoordinates() {
        return attemptCoordinates;
    }

    public void setAttemptCoordinates(Pair<Integer, Integer> attemptCoordinates) {
        this.attemptCoordinates = attemptCoordinates;
    }

    Pair<Integer, Integer> attemptCoordinates;
    public List<List<PointFlag>> getMap() {
        return map;
    }

    public void setMap(List<List<PointFlag>> map) {
        this.map = map;
    }

    public Action(Flag type) {
        this.type = type;
    }

    public Action(Flag type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Action{" +
                "type=" + type +
                ", id=" + id +
                ", payload=" + payload +
                ", map=" + map +
                ", attemptCoordinates=" + attemptCoordinates +
                '}';
    }

    public Flag getType() {
        return type;
    }

    public void setType(Flag type) {
        this.type = type;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
