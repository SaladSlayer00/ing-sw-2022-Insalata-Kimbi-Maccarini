package it.polimi.ingsw.message;
import it.polimi.ingsw.model.enums.Mage;

public class DeckMessage extends Message{
    private static final long serialVersionUID = -3704504226997118508L;
    private final Mage deck;

    public DeckMessage(String nickname,Mage mage) {
        super(nickname, MessageType.INIT_DECK);
        this.deck = mage;
    }

    @Override
    public String toString() {
        return "ColorsMessage{" +
                "nickname=" + getNickname() +
                ", Deck=" + this.deck +
                '}';
    }

    public Mage getMage() {
        return this.deck;
    }
}