package it.polimi.ingsw.model.test;

import it.polimi.ingsw.exceptions.emptyDecktException;
import it.polimi.ingsw.exceptions.lowerLimitException;
import it.polimi.ingsw.model.Assistant;
import it.polimi.ingsw.model.Deck;
import it.polimi.ingsw.model.enums.Mage;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

class PlayerTest {

    //things that will be quite essential for the tests
    Player playerT;
    private final int playerID = 0;
    private final String name = "nikeTest";
    private String chosenMage = "fairy";

    //set up for the Player
    @BeforeEach
    void startingSetUp(){
        playerT = new Player(name, playerID);
        playerT.setDeck(Mage.FAIRY);
    }

    //tests the getter of the name
    @Test
    @DisplayName("Name getter and ID getter test")
    void getNameTest() {
        assertEquals(name, playerT.getName());
        assertEquals(playerID, playerT.getPlayerID());
    }

    //tests the getter of the deck
    @Test
    void getDeckTest() {
        assertEquals(playerT.getDeck().getMage(),Mage.FAIRY);
    }

    //it returns the player's dashboard
    @Test
    @DisplayName("Player's dashboard test")
    void getDashboardTets() {
       //should find a way to test this because in this way it's impossible...
    }

    //it resturns the number of coins
    @Test
    @DisplayName("Player's coins setter and getter test")
    void CoinsTest() throws lowerLimitException {
        playerT.addCoin();
        assertEquals(1, playerT.getCoins());
        playerT.removeCoin();
        assertEquals(0, playerT.getCoins());
    }

    /* dunno if this could be tested here...
    @Test
    void getStateTest() {
    }
    */

    //tests the getter and setter of the player's group
    @Test
    @DisplayName("Setter and getter of the player's group")
    void groupTest(){
        playerT.setGroup(1);
        assertEquals(1, playerT.getGroup());
    }

    //tests the setter and the getter for the Assistant
    @Test
    void AssistantTest() throws emptyDecktException {
        Assistant aTest = new Assistant(4,3);
        playerT.setCard(aTest);
        assertEquals(aTest,playerT.getCardChosen());
    }
    /* this method uses dashboard so probably not very useful to test it here ???
    @Test
    void takeStudent() {
    }
    */
    //it tests :
    @Test
    void changeStateTest() {

    }

    //returns a boolean true if the player has the professor of the color's parameter
    @Test
    void hasProfessorTets() {
    }

    static class RowTest {

        @Test
        void getName() {
        }

        @Test
        void addProfessor() {
        }

        @Test
        void addStudent() {
        }

        @Test
        void removeProfessor() {
        }
    }
}