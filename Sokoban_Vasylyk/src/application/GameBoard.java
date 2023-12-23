package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class GameBoard extends Group {
    private static final String WALL_IMAGE_PATH = "/sprCol_0.png";
    private static final String BOX_IMAGE_PATH = "/sprBox_0.png";
    private static final String PLAYER_UP_IMAGE_PATH = "/roboUp_0.png";
    private static final String PLAYER_DOWN_IMAGE_PATH = "/roboDown_0.png";
    private static final String PLAYER_LEFT_IMAGE_PATH = "/roboLeft_0.png";
    private static final String PLAYER_RIGHT_IMAGE_PATH = "/roboRight_0.png";
    private static final String HOLE_IMAGE_PATH = "/sprHole_0.png";
    private static final int HOLE = 4;
    private int[][] currentBoard;
    private static final int WALL = 1;
    private static final int BOX = 2;
    private static final int PLAYER = 3;
    private static final int EMPTY = 0;
    private static final int IMAGE_SIZE = 50;
    private int prevPlayerRow;
    private int prevPlayerCol;
    private int playerRow;
    private int playerCol;
    private int currentLevel =0;
    public static int[][] board ;
    private boolean boxMovedThisFrame = false;
    
    public GameBoard() {
    	if(currentLevel ==0) {
    		showMenu();// Display the welcome menu for the first level
    	}else {
    	// Load the board for the current level, set up the player, and handle key events.
        String nextLevelFileName = "lvl" + currentLevel + ".txt";
        loadBoardFromFile(nextLevelFileName);
        copyBoard();
        renderBoard();
        setPlayerStartPosition();
        setOnKeyPressed(event -> handleKeyPress(event.getCode()));
        setFocusTraversable(true);}
    }
    
    private void showMenu() {
    	// Display the initial menu with a welcome message and a "Start" button.
        getChildren().clear();
        String menuMessage = "Welcome to the Game!\n control the player using the movement buttons or WASD,\n to restart the level press the Q button";
        double centerX = 200; 
        double centerY = 150; 
        getChildren().add(createText(menuMessage, centerX, centerY));
        String startButtonText = "Start";
        double buttonX = 200; 
        double buttonY = 250; 
        getChildren().add(createButton(startButtonText, buttonX, buttonY));
    }
    private Button createButton(String buttonText, double x, double y) {
        Button button = new Button(buttonText);
        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setStyle("-fx-font-size: 24;"); 

        button.setOnAction(event -> loadNextLevel());
        return button;
    }


    public void start() {
    	//initialize game board, set player position, etc.
        getChildren().clear();
        String nextLevelFileName = "lvl" + currentLevel + ".txt";
        loadBoardFromFile(nextLevelFileName);

        copyBoard();
        setPlayerStartPosition();
        renderBoard();
        setOnKeyPressed(event -> handleKeyPress(event.getCode()));
        setFocusTraversable(true);
    }
    private void copyBoard() {
    	// Create a deep copy of the board to track changes during gameplay
        currentBoard = new int[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            currentBoard[i] = board[i].clone();
        }
    }
    
 // Check if the level is completed by iterating through the board.
    private boolean isLevelCompleted() {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == BOX) {
                    if (currentBoard[row][col] != HOLE) {
                        return false;
                    }
                }
            }
        }
        return true;
        
    }
    
 // Load the next level or display the final menu if all levels are completed.
    public void loadNextLevel() {
    	getChildren().clear();
        currentLevel++;
        if (currentLevel == 8) {
        	// Display the final menu with a message and "Restart" and "Exit" buttons.
            String finalMenuMessage = "Congratulations! You have completed the game.\nTo play again, press the 'Restart' button.\nTo close the game, press the 'Exit' button.";
            double centerX = 200;
            double centerY = 150;
            getChildren().add(createText(finalMenuMessage, centerX, centerY));

            String restartButtonText = "Restart";
            double restartButtonX = 200;
            double restartButtonY = 250;
            getChildren().add(createRestartButton(restartButtonText, restartButtonX, restartButtonY));

            String exitButtonText = "Exit";
            double exitButtonX = 200;
            double exitButtonY = 300;
            getChildren().add(createExitButton(exitButtonText, exitButtonX, exitButtonY));
        }else {
        // Load the next level if not completed, otherwise display completion message.
        start();}
    }
    




    private void setPlayerStartPosition() {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == PLAYER) {
                    playerRow = row;
                    playerCol = col;
                    System.out.println(row + " " + col);
                    return;
                }
            }
        }
    }

    private boolean isBoxOnHole(int row, int col) {
        return board[row][col] == BOX && currentBoard[row][col] == HOLE;
    }


    

    private void handleKeyPress(KeyCode code) {
        switch (code) {
            case UP:
            case W:
                movePlayer(-1, 0);
                break;
            case DOWN:
            case S:
                movePlayer(1, 0);
                break;
            case LEFT:
            case A:
                movePlayer(0, -1);
                break;
            case RIGHT:
            case D:
                movePlayer(0, 1);
                break;
            case SPACE:
                if (isLevelCompleted()) {
                    loadNextLevel();
                }
                break;
            default:
                break;
        }
    }



    

    private void renderBoard() {
    	// Clear existing content and render the game board based on the current state.
        getChildren().clear(); 

        boolean levelCompleted = true; 

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == WALL) {
                    getChildren().add(createImageView(WALL_IMAGE_PATH, col * IMAGE_SIZE, row * IMAGE_SIZE));
                } else if (board[row][col] == BOX) {
                    if (isBoxOnHole(row, col)) {
                        getChildren().add(createImageView("/sprBox_1.png", col * IMAGE_SIZE, row * IMAGE_SIZE));
                    } else {
                        getChildren().add(createImageView(BOX_IMAGE_PATH, col * IMAGE_SIZE, row * IMAGE_SIZE));
                    }
                    if (currentBoard[row][col] != HOLE) {
                        levelCompleted = false;
                    }
                } else if (board[row][col] == PLAYER) {
                    String playerImagePath = getPlayerImagePath();
                    getChildren().add(createImageView(playerImagePath, col * IMAGE_SIZE, row * IMAGE_SIZE));
                } else if (currentBoard[row][col] == HOLE) {
                    getChildren().add(createImageView(HOLE_IMAGE_PATH, col * IMAGE_SIZE, row * IMAGE_SIZE));
                }
            }
        }

        if (levelCompleted) {
            displayLevelCompletedMessage();
        }
    }
    private void displayLevelCompletedMessage() {
        String message = "Level Completed! Press 'SPACE' for the next level.";
        double centerX = board[0].length * IMAGE_SIZE / 2 - message.length() * 2;
        double centerY = board.length * IMAGE_SIZE / 2;
        
        getChildren().add(createText(message, centerX, centerY));
    }
    private Text createText(String message, double x, double y) {
        Text text = new Text(message);
        text.setX(x);
        text.setY(y);
        text.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        text.setFill(Color.BLACK);
        return text;
    }
    private ImageView createImageView(String imagePath, double x, double y) {
        ImageView imageView = new ImageView(new Image(getClass().getResource(imagePath).toExternalForm()));
        imageView.setX(x);
        imageView.setY(y);
        imageView.setFitWidth(IMAGE_SIZE);
        imageView.setFitHeight(IMAGE_SIZE);
        return imageView;
    }


    





    private String getPlayerImagePath() {
        int rowChange = playerRow - prevPlayerRow;
        int colChange = playerCol - prevPlayerCol;

        if (rowChange == -1) {
            return PLAYER_UP_IMAGE_PATH;
        } else if (rowChange == 1) {
            return PLAYER_DOWN_IMAGE_PATH;
        } else if (colChange == -1) {
            return PLAYER_LEFT_IMAGE_PATH;
        } else if (colChange == 1) {
            return PLAYER_RIGHT_IMAGE_PATH;
        }

            return PLAYER_DOWN_IMAGE_PATH;
    }




    private void movePlayer(int rowChange, int colChange) {
    	// Move the player on the board based on user input and handle box movements.
        prevPlayerRow = playerRow;
        prevPlayerCol = playerCol;

        int newRow = playerRow + rowChange;
        int newCol = playerCol + colChange;
        
        if (isValidMove(newRow, newCol)) {
            if (board[newRow][newCol] == BOX && !boxMovedThisFrame) {
                int newBoxRow = newRow + rowChange;
                int newBoxCol = newCol + colChange;
                if (isValidMove(newBoxRow, newBoxCol)) {
                    if (board[newBoxRow][newBoxCol] == EMPTY || board[newBoxRow][newBoxCol] == HOLE) {
                        if (board[newBoxRow][newBoxCol] == HOLE) {
                            board[newRow][newCol] = HOLE;
                            board[newBoxRow][newBoxCol] = BOX;
                        } else {
                            board[newRow][newCol] = EMPTY;
                            board[newBoxRow][newBoxCol] = BOX;
                        }
                        boxMovedThisFrame = true;
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }

            

            board[playerRow][playerCol] = EMPTY;
            playerRow = newRow;
            playerCol = newCol;
            board[playerRow][playerCol] = PLAYER;

            
            renderBoard();
            boxMovedThisFrame = false;
        }
    }






    private boolean isValidMove(int row, int col) {
    	//check if the move is within the board boundaries and not into a wall
        return row >= 0 &&
        	   row < board.length && 
        	   col >= 0 && 
        	   col < board[0].length && 
        	   board[row][col] != WALL;
    }
    
    private void loadBoardFromFile(String filename) {
    	// Load the game board from a file and initialize the board array.
        try (InputStream inputStream = getClass().getResourceAsStream(filename)) {
            if (inputStream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }

                    String[] lines = content.toString().split("\n");
                    int numRows = lines.length;
                    int numCols = 0;

                    for (String l : lines) {
                        numCols = Math.max(numCols, l.length());
                    }

                    board = new int[numRows][numCols];

                    int row = 0;
                    for (String l : lines) {
                        for (int col = 0; col < l.length(); col++) {
                            char charAtPos = l.charAt(col);
                            board[row][col] = Character.getNumericValue(charAtPos);
                        }
                        row++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Button createRestartButton(String buttonText, double x, double y) {
        Button restartButton = new Button(buttonText);
        restartButton.setLayoutX(x);
        restartButton.setLayoutY(y);
        restartButton.setStyle("-fx-font-size: 24;");

        restartButton.setOnAction(event -> restartGame());
        return restartButton;
    }

    private Button createExitButton(String buttonText, double x, double y) {
        Button exitButton = new Button(buttonText);
        exitButton.setLayoutX(x);
        exitButton.setLayoutY(y);
        exitButton.setStyle("-fx-font-size: 24;");

        exitButton.setOnAction(event -> System.exit(0)); 
        return exitButton;
    }

    private void restartGame() {
        getChildren().clear();
        currentLevel = 1; 
        start();
    }
}