package it.polimi.ingsw.message;

import it.polimi.ingsw.model.enums.Type;

/**
 * Class used to send chosen tower update messages
 */
public class TowerMessage extends Message{
    private static final long serialVersionUID = -3704504226997118508L;
    private final Type tower;

    public TowerMessage(String nickname,Type tower) {
        super(nickname, MessageType.INIT_TOWERS);
        this.tower = tower;
    }

    @Override
    public String toString() {
        return "TowerMessage{" +
                "nickname=" + getNickname() +
                ", Tower=" + this.tower +
                '}';
    }

    public Type getType() {
        return this.tower;
    }

}
