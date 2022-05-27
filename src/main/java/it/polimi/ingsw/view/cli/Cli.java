package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.controller.ClientController;
import it.polimi.ingsw.message.Message;
import it.polimi.ingsw.model.Assistant;
import it.polimi.ingsw.model.Student;
import it.polimi.ingsw.model.board.Cloud;
import it.polimi.ingsw.model.board.Gameboard;
import it.polimi.ingsw.model.board.Island;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Mage;
import it.polimi.ingsw.model.enums.Type;
import it.polimi.ingsw.model.enums.modeEnum;
import it.polimi.ingsw.model.playerBoard.Dashboard;
import it.polimi.ingsw.observer.ViewObservable;
import it.polimi.ingsw.view.View;

import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

public class Cli extends ViewObservable implements View {

    private Gameboard gameboard;
    private List<Dashboard> dashboards;
    private final PrintStream out;
    private Thread inputThread;
    private static final String STR_INPUT_CANCELED = "User input canceled.";

    public Cli() {
        out = System.out;
    }

    /**
     * Reads a line from the standard input.
     *
     * @return the string read from the input.
     * @throws ExecutionException if the input stream thread is interrupted.
     */
    public String readLine() throws ExecutionException {
        FutureTask<String> futureTask = new FutureTask<>(new InputReadTask());
        inputThread = new Thread(futureTask);
        inputThread.start();

        String input = null;

        try {
            input = futureTask.get();
        } catch (InterruptedException e) {
            futureTask.cancel(true);
            Thread.currentThread().interrupt();
        }
        return input;
    }

    /**
     * Starts the command-line interface.
     */
    public void init() {
        out.println("" +
                " \n" +
                " _______  _______ _________ _______  _       _________          _______ \n" +
                "(  ____ \\(  ____ )\\__   __/(  ___  )( (    /|\\__   __/|\\     /|(  ____ \\\n" +
                "| (    \\/| (    )|   ) (   | (   ) ||  \\  ( |   ) (   ( \\   / )| (    \\/\n" +
                "| (__    | (____)|    | |   | (___) ||   \\ | |   | |    \\ (_) / | (_____ \n" +
                "|  __)   |     __)    | |   |  ___  || (\\ \\) |   | |     \\   /  (_____  )\n" +
                "| (      | (\\ (      | |   | (   ) || | \\   |   | |      ) (         ) |\n" +
                "| (___/\\| ) \\ \\____) (___| )   ( || )  \\  |   | |      | |   /\\____) |\n" +
                "(_______/|/   \\__/\\_______/|/     \\||/    )_)   )_(      \\_/   \\_______)\n" +
                "                                                                        \n");

        out.println("Welcome to Eriantys Board Game!");

        try {
            askServerInfo();
        } catch (ExecutionException e) {
            out.println(STR_INPUT_CANCELED);
        }
    }

    /**
     * Asks the server address and port to the user.
     *
     * @throws ExecutionException if the input stream thread is interrupted.
     */
    public void askServerInfo() throws ExecutionException {
        Map<String, String> serverInfo = new HashMap<>();
        String defaultAddress = "localhost";
        String defaultPort = "16847";
        boolean validInput;

        out.println("Please specify the following settings. The default value is shown between brackets.");

        do {
            out.print("Enter the server address [" + defaultAddress + "]: ");

            String address = readLine();

            if (address.equals("")) {
                serverInfo.put("address", defaultAddress);
                validInput = true;
            } else if (ClientController.isValidIpAddress(address)) {
                serverInfo.put("address", address);
                validInput = true;
            } else {
                out.println("Invalid address!");
                clearCli();
                validInput = false;
            }
        } while (!validInput);

        do {
            out.print("Enter the server port [" + defaultPort + "]: ");
            String port = readLine();

            if (port.equals("")) {
                serverInfo.put("port", defaultPort);
                validInput = true;
            } else {
                if (ClientController.isValidPort(port)) {
                    serverInfo.put("port", port);
                    validInput = true;
                } else {
                    out.println("Invalid port!");
                    validInput = false;
                }
            }
        } while (!validInput);

        notifyObserver(obs -> obs.onUpdateServerInfo(serverInfo));
    }

    @Override
    public void askNickname() {
        out.print("Enter your nickname: ");
        try {
            String nickname = readLine();
            notifyObserver(obs -> obs.onUpdateNickname(nickname));
        } catch (ExecutionException e) {
            out.println(STR_INPUT_CANCELED);
        }
    }

    @Override
    public void askGameMode(String nickname, List<modeEnum> gameModes) {
        modeEnum game;
        String question = "Please " + nickname + " choose the game mode: ";
        game = modeInput(gameModes, question);
        notifyObserver(obs -> obs.OnUpdateGameMode(game));
    }


    //metodo chiamato quando il gamecontroller richiede dichiedere il numero di players
    @Override
    public void askPlayersNumber() {
        int playerNumber;
        String question = "How many players are going to play? (You can choose between 2, 3 or 4 players): ";

        playerNumber = numberInput(question);
        notifyObserver(obs -> obs.onUpdatePlayersNumber(playerNumber));
    }

    @Override
    public void askInitDeck(String nickname, List<Mage> availableDecks) {
        clearCli();
        Mage mage;
        if (availableDecks.size() > 1) {
            String question = "Please "+ nickname+", select a mage from the list!";

            out.println("Please, enter the name in LOWERCASE and confirm with ENTER.");
                mage = mageInput(availableDecks, question);
                //Mage.choose(mage);

                notifyObserver(obs -> obs.OnUpdateInitDeck(mage));
        }
        else if(availableDecks.size() ==1){
            out.println(nickname + ", you're the last player, your mage is: " + availableDecks.get(0).getText());
            notifyObserver(obs -> obs.OnUpdateInitDeck(availableDecks.get(0)));
        }
        else{
            showErrorAndExit("no mages found in the request.");
        }


    }

    @Override
    public void askInitType(String nickname, List<Type> availableTeams) {
        clearCli();
        Type team;
        if (availableTeams.size() > 1) {
            String question = "Please "+ nickname + ", select a team from the list!";
            out.println("Please, enter the name in LOWERCASE and confirm with ENTER.");
            team = teamInput(availableTeams, question);
                //Type.choose(team);

            notifyObserver(obs -> obs.OnUpdateInitTower(team));
        }
        else if(availableTeams.size() ==1){
            out.println(nickname + ", you're the last player, your team is: "+ availableTeams.get(0).getText());
            notifyObserver(obs -> obs.OnUpdateInitTower(availableTeams.get(0)));
        }
        else{
            showErrorAndExit("no teams found in the request.");
        }


    }


    //@TODO riguardare un attimo come funziona sto metodo
    @Override
    public void askStart(String nickname, String answer){
        clearCli();
        String input;
        if(answer.equals(null)) {
                String question = "Please "+ nickname + ", select a team from the list!";
                input = answerInput(question);
                notifyObserver(obs -> obs.OnStartAnswer(input));
        }
        else{
            showErrorAndExit("wrong message format.");
        }

    }

    @Override
    public void askCloud(String nickname, List<Cloud> availableClouds){
        clearCli();
        showTable(gameboard, dashboards);
        int index;
        if (availableClouds.size() > 1) {
            String question = "Please "+ nickname + ", select a cloud from the list!";
            out.println("Please, enter the cloud's index and press ENTER.");
                index = cloudInput(availableClouds, question);
                notifyObserver(obs -> obs.OnUpdatePickCloud(index));

        }
        else if(availableClouds.size() ==1){
            out.println(nickname + ", you're the last player, your cloud is: 0 ");
            notifyObserver(obs -> obs.OnUpdatePickCloud(availableClouds.get(0).getIndex()));
        }
        else{
            showErrorAndExit("no clouds found in the request.");
        }
    }

    @Override
    public void askAssistant(String nickname, List<Assistant> availableAssistants){
        clearCli();
        showTable(gameboard, dashboards);
        Assistant assistant;
        if (!availableAssistants.equals(null)) {
            String question = "Please "+ nickname + ", select an assistant from the list!";
            out.println("Please, enter the assistant's index and press ENTER.");
                assistant = assistantInput(availableAssistants, question);
                notifyObserver(obs -> obs.OnUpdateAssistant(assistant));
        }
        else{
            showErrorAndExit("no assistants found in the request.");
        }

    }

    @Override
    public void askMoves(List<Student> students, List<Island> islands){
        clearCli();
        showTable(gameboard, dashboards);
        Color student;
        String location;
        if (!(students.size()==0)) {
            String question = "Please, choose a student to move! Enter the LOWERCASE color of it";
            student = studentInput(question, students);
            out.println("Please, choose where do you want to move your students!");
            question = "Please, enter ISLAND or ROW and press ENTER.";

                location = locationInput(question);
                if("ISLAND".equals(location.toUpperCase())){
                    askIslandMoves(student, islands);
                }
                else if("ROW".equals(location.toUpperCase())){
                    notifyObserver(obs -> obs.OnUpdateMoveOnBoard(student,student));
                }
        }
        else{
            showErrorAndExit("no students found in the hall.");
        }
    }

    @Override
    public void askIslandMoves(Color student, List<Island> islands){
        clearCli();
        showTable(gameboard, dashboards);
        String question = "Please, choose where do you want to move your student!";
        int location;
        location = islandInput(question, student, islands);
        notifyObserver(obs -> obs.OnUpdateMoveOnIsland(student,location, islands));
        //try {
          //  location = islandInput(question, student, islands);
           // notifyObserver(obs -> obs.OnUpdateMoveOnIsland(student,location, islands));
        //}catch(ExecutionException e) {
            //out.println(STR_INPUT_CANCELED);
        //}

    }

    public void askMotherMoves(String nickname, int possibleMoves) {
        clearCli();
        showTable(gameboard, dashboards);
        int number=0;
        do{

            try {
                out.print("Please choose a number of mother nature moves between 1 and "+ possibleMoves);
                number = Integer.parseInt(readLine());

                if (number < 1 || number > possibleMoves) {
                    out.println("Invalid number! Please try again.\n");
                }
            } catch (IllegalArgumentException | ExecutionException e) {
                out.println("Invalid mode! Please try again.");
            }
        } while (number < 1 || number > possibleMoves);

    }

    public void clearCli() {
        out.print(ColorCli.CLEAR);
        out.flush();
    }

    //INPUT METHODS

    public modeEnum modeInput(List<modeEnum> modeEnums, String question){
        modeEnum mode = null;
        String in;
        String modeStr = modeEnums.stream()
                .map(modeEnum::getText)
                .collect(Collectors.joining(", "));

        do {
            out.print(question);
            out.print("Choose between " + modeStr + ": ");

            try {
                in = readLine();
                mode = modeEnum.valueOf(in.toLowerCase());

                if (!modeEnums.contains(mode)) {
                    out.println("Invalid mode! Please try again.");
                }
            } catch (IllegalArgumentException | ExecutionException e) {
                out.println("Invalid mode! Please try again.");
            }
        } while (!modeEnums.contains(mode));

        return mode;
    }

    public int numberInput(String question){
        int number = 0;
        do{

            try {
                out.print(question);
                number = Integer.parseInt(readLine());

                if (number < 2 || number > 4) {
                    out.println("Invalid number! Please try again.\n");
                }
            } catch (IllegalArgumentException | ExecutionException e) {
                out.println("Invalid mode! Please try again.");
            }
        } while (number < 2 || number > 4);

        return number;

    }

    public Mage mageInput(List<Mage> available, String question){
        Mage mage = null;
        String in;
        String modeStr = available.stream()
                .map(Mage::getText)
                .collect(Collectors.joining(", "));

        do {
            out.print(question);
            out.print("Choose between " + modeStr + ": ");

            try {
                in = readLine();
                mage = Mage.valueOf(in.toLowerCase());

                if (!available.contains(mage)) {
                    out.println("Invalid mage! Please try again.");
                }
            } catch (IllegalArgumentException | ExecutionException e) {
                out.println("Invalid mage! Please try again.");
            }
        } while (!available.contains(mage));

        return mage;

    }

    public Type teamInput(List<Type> available, String question){
        Type team = null;
        String in;
        String modeStr = available.stream()
                .map(Type::getText)
                .collect(Collectors.joining(", "));

        do {
            out.print(question);
            out.print("Choose between "+ modeStr + ": ");
            try {
                in = readLine();
                team = Type.valueOf(in.toLowerCase());

                if (!available.contains(team)) {
                    out.println("Invalid mage! Please try again.");
                }
            } catch (IllegalArgumentException | ExecutionException e) {
                out.println("Invalid mage! Please try again.");
            }
        } while (!available.contains(team));

        return team;

    }

    public String answerInput(String question){
        String answer = null;
        do{

            try {
                out.print(question);
                answer = readLine();

                if (answer.toUpperCase().equals("NO")) {
                    out.println("Ok! You can type YES when you're ready! \n");
                }
            } catch (IllegalArgumentException | ExecutionException e) {
                out.println("Invalid argument! Please try again.");
            }
        } while (answer.equals(null) || answer.equals("NO"));

        return answer;
    }

    public int cloudInput(List<Cloud> available, String question){
        clearCli();
        showTable(gameboard, dashboards);
        int number = -1;
        do{

            try {
                out.print(question);
                out.print("Choose between ");
                for(Cloud c : available){
                    out.print(c.getIndex() + "\n");
                }
                number = Integer.parseInt(readLine());

                if (number < 0 || number > available.size()) {
                    out.println("Invalid number! Please try again.\n");
                }
            } catch (IllegalArgumentException | ExecutionException e) {
                out.println("Invalid mode! Please try again.");
            }
        } while (number < 0 || number > available.size());

        return number;
    }

    public Assistant assistantInput(List<Assistant> available, String question){
        clearCli();
        showTable(gameboard, dashboards);
        int index;
        Assistant assistant = null;

        do{

            try {
                out.print(question);
                out.print("Choose between ");
                for(Assistant a : available){
                    out.print(a.getNumOrder() + "\n");
                }
                index = Integer.parseInt(readLine());
                assistant = new Assistant(index, 0);
                if (!available.contains(assistant)) {
                    out.println("Invalid assitant! Please try again.\n");
                }
            } catch (IllegalArgumentException | ExecutionException e) {
                out.println("Invalid mode! Please try again.");
            }
        } while (!available.contains(assistant));

        return assistant;
    }

    public Color studentInput(String question, List<Student> students){
        clearCli();
        showTable(gameboard, dashboards);
        Color color = null;
        String in;
        List<Color> colors = new ArrayList<Color>();
        for(Student s : students){
            colors.add(s.getColor());
        }
        do {
            out.print(question);
            out.print("Choose between: ");
            for(Student s : students){
                out.print(s.getColor().getText() + "\n");
            }

            try {
                in = readLine();
                color = Color.valueOf(in.toLowerCase());

                if (!colors.contains(color)) {
                    out.println("Invalid student! Please try again.");
                }
            } catch (IllegalArgumentException | ExecutionException e) {
                out.println("Invalid student! Please try again.");
            }
        } while (!colors.contains(color));

        return color;
    }

    public String locationInput(String question){
        clearCli();
        showTable(gameboard, dashboards);
        String answer = null;
        do{

            try {
                out.print(question);
                answer = readLine();

                if (!answer.toUpperCase().equals("ROW") && !answer.toUpperCase().equals("ISLAND")) {
                    out.println("Invalid input! Please try again! \n");
                }
            } catch (IllegalArgumentException | ExecutionException e) {
                out.println("Invalid input! Please try again.");
            }
            } while (!answer.equals("ROW") && !answer.equals("ISLAND"));

        return answer;
    }

    public int islandInput(String question, Color student, List<Island> islands){
        clearCli();
        showTable(gameboard, dashboards);
        int index = -1;
        do {
            out.print(question);
            out.print("Choose between: ");
            for(Island  i : islands){
                out.print(i.getIndex() + "\n");
            }

            try {
                index = Integer.parseInt(readLine());

                if (index < 0 || index > islands.size()) {
                    out.println("Invalid number! Please try again.");
                }
            } catch (IllegalArgumentException | ExecutionException e) {
                out.println("Invalid number! Please try again.");
            }
        } while (index<0||index > islands.size());

       return index;
    }

    public void showGenericMessage(String genericMessage) {
        out.println(genericMessage);
    }

    public void showWinMessage(String winner) {
        out.println("Game finished: " + winner + " WINS!");
        System.exit(0);
    }

    @Override
    public void showDrawMessage() {

    }

    public void showErrorAndExit(String error) {
        inputThread.interrupt();

        out.println("\nERROR: " + error);
        out.println("EXIT.");

        System.exit(1);
    }

    @Override
    public void showTable(Gameboard gameboard, List<Dashboard> dashboards ){
        clearCli();
        showDashboards(dashboards);
        out.print("\n");
        out.print("\n");
        showBoard(gameboard);

    }

    //TODO vedere come è inizializzato quell'array
    public void showDashboards(List<Dashboard> dashboards){
        String leftAlignFormat = "| %-10s | %-4d |%n";
        for(int i = 0; i< dashboards.size();i++) {
            System.out.format("+-----------------+------+%n");
            System.out.format("| Column name     | ID   |%n");
            System.out.format("+-----------------+------+%n");
            System.out.format(leftAlignFormat, "HALL", dashboards.get(i).getHall().size());
            System.out.format(leftAlignFormat, "RED", dashboards.get(i).getRow("red").getNumOfStudents());
            System.out.format(leftAlignFormat, "GREEN", dashboards.get(i).getRow("green").getNumOfStudents());
            System.out.format(leftAlignFormat, "PINK", dashboards.get(i).getRow("pink").getNumOfStudents());
            System.out.format(leftAlignFormat, "YELLOW", dashboards.get(i).getRow("yellow").getNumOfStudents());
            System.out.format(leftAlignFormat, "BLUE", dashboards.get(i).getRow("blue").getNumOfStudents());
            System.out.format("+-----------------+------+%n");
        }
    }

    public void showBoard(Gameboard gameboard) {
        StringBuilder strBoardBld = new StringBuilder();
        String leftAlignFormat = "| 10s%- |";
        strBoardBld.append(ColorCli.YELLOW_BOLD).append("\n   +-----+-----+-----+-----+-----+\n").append(ColorCli.RESET);
        for (Island i : gameboard.getIslands()) {
            System.out.format("+------+%n");
            System.out.format(leftAlignFormat, "["+i.getStudents().get(Color.YELLOW).size() +"]");
            System.out.format(leftAlignFormat, "["+i.getStudents().get(Color.BLUE).size() +"]\n");
            System.out.format(leftAlignFormat, "["+i.getStudents().get(Color.GREEN).size() +"]");
            System.out.format(leftAlignFormat, "["+i.getStudents().get(Color.PINK).size() +"]\n");
            System.out.format(leftAlignFormat, "["+i.getStudents().get(Color.RED).size() +"]");
            System.out.format("+------+%n");
        }
        strBoardBld.append(ColorCli.YELLOW_BOLD).append("\n   +-----+-----+-----+-----+-----+\n").append(ColorCli.RESET);
        out.println(strBoardBld.toString());
    }

    @Override
    public void showAssistant(int number){
        showTable(gameboard, dashboards);
        String leftAlignFormat = "| %-6d |%n";
        System.out.format("+------+%n");
        System.out.format(leftAlignFormat, number);
        System.out.format("+------+%n");
    }

    /**
     * Shows the login result on the terminal.
     * On login fail, the program is terminated immediatly.
     *
     * @param nicknameAccepted     indicates if the chosen nickname has been accepted.
     * @param connectionSuccessful indicates if the connection has been successful.
     * @param nickname             the nickname of the player to be greeted.
     */
    @Override
    public void showLoginResult(boolean nicknameAccepted, boolean connectionSuccessful, String nickname) {
        clearCli();

        if (nicknameAccepted && connectionSuccessful) {
            out.println("Hi, " + nickname + "! You connected to the server.");
        } else if (connectionSuccessful) {
            askNickname();
        } else if (nicknameAccepted) {
            out.println("Max players reached. Connection refused.");
            out.println("EXIT.");

            System.exit(1);
        } else {
            showErrorAndExit("Could not contact server.");
        }
    }

    @Override
    public void errorCommunicationAndExit(String nickname) {
        inputThread.interrupt();

        out.println("\nERROR: " + nickname);
        out.println("EXIT.");

        System.exit(1);
    }

    @Override
    public void effectEnabled(String summoner) {
        out.println(summoner);
    }

    @Override
    public void showMatchInfo(int chosen, int actual) {
        out.println("MATCH INFO1");
    }

    @Override
    public void showMatchInfo(List<String> activePlayers, String activePlayerNickname) {
        out.println("SHOW INFO2");
    }

    @Override
    public void winCommunication(String winner) {
        out.println("Game finished: " + winner + " WINS!");
        System.exit(0);
    }

//TODO facciamo un set gameboard dal controller che setta gli attributi tramite cui posso stampare la board

}