package it.polimi.ingsw.message;

public enum MessageType {
        LOGIN_REQUEST, LOGIN_REPLY,
        PLAYERNUMBER_REQUEST, PLAYERNUMBER_REPLY,
        GAMEMODE_REPLY,
        LOBBY,
        INIT_DECK,
        ASK_TEAM, //BOH
        INIT_TOWERS,
        INIT_GAMEBOARD,
        PICK_CLOUD,
        DRAW_ASSISTANT,
        MOVE_ON_ISLAND,
        MOVE_ON_BOARD,
        MOVE_MOTHER,
        GET_FROM_CLOUD,
        USE_EXPERT,
        WIN,
        WIN_FX,
        LOSE,

        //utility:
        GAME_LOAD,
        MATCH_INFO,
        DISCONNECTION,
        GENERIC_MESSAGE,
        PING,
        ERROR,
        ENABLE_EFFECT,
        APPLY_EFFECT,
        PERSISTENCE

}
