package it.polimi.ingsw.controller.expertDeck;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Sack;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.ExpertDeck;
import it.polimi.ingsw.view.VirtualView;

/**Class for BANKER card: the player that summons this card can choose a color and every player (themselves included) has to take
* three students from the row of the chosen color and put them back in the sack
 */
public class RemoveAColorCard extends Character {
    private ExpertDeck name = ExpertDeck.BANKER;
    private GameController gameController;
    private TurnController turnController;
    private Color color;
    //constructor
    public RemoveAColorCard(GameController gameController, TurnController turnController){
        super(3);
        this.gameController = gameController;
        this.turnController = turnController;
    }


    public ExpertDeck getName() {
        return name;
    }

    @Override
    public void useEffect() {
        VirtualView vv = gameController.getVirtualViewMap().get(turnController.getActivePlayer());
        vv.showGenericMessage("You can choose a color to make everyone lose 3 students!\n");
        vv.askColor();
    }

    @Override
    public void removeEffect() {
        turnController.getToReset().remove(this);
        gameController.getGame().getGameBoard().getToReset().remove(ExpertDeck.BANKER);
    }

    /**
     * Method that sets the color to remove from every player's row
     *
     * @param color the chosen color
     * @throws emptyDecktException if the player's deck is empty
     * @throws noMoreStudentsException if there's no more students in the sack
     * @throws fullTowersException if the tower's number is full
     * @throws noStudentException if there's no student on the island
     * @throws noTowerException if there's no tower on the island
     * @throws invalidNumberException if the chose number is not allowed
     * @throws maxSizeException if the max size's reached
     */
    public void setColor(Color color) throws emptyDecktException, noMoreStudentsException, fullTowersException, noStudentException, noTowerException, invalidNumberException, maxSizeException, noTowersException {
        VirtualView vv = gameController.getVirtualViewMap().get(turnController.getActivePlayer());
        vv.showGenericMessage("Everyone puts back "+color.getText()+" students!\n");

        this.color = color;
        Sack sack = gameController.getGame().getGameBoard().getSack();
        for(Player p:gameController.getGame().getPlayers()){
            for(int i = 0;i<3;i++){
                try {
                    sack.putStudent(p.getDashboard().getRow(color).removeStudent());
                } catch (noStudentException e) {
                    vv.showGenericMessage(p.getName()+" has less than 3 students for " + color.getText() +"\n");
                    break;
                }
            }
        }
        turnController.checkProfessors(color);
        removeEffect();
    }

    public Color getColor() {
        return color;
    }

    /**
     * Method that checks if the player has enough money to play the card
     * @param p the player that called the card's effect
     * @return boolean value to notify the operation's outcome
     */
    public boolean checkMoney(Player p){
        return p.getCoins() >= getCost()+turnController.getPrice().get(this.getName());
    }

}


