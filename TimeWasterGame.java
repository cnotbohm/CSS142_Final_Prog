import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * A class containing a time-waster game.
 * The game is played on a grid, and the goal
 * is to fill the grid up completley with numbers
 * with certain rules regarding how to fill the grid.
 *
 * @author Cordelia Notbohm
 * @version 1.0.0
 */
public class TimeWasterGame {
  private int[][] board;       // the grid that holds the numbers 
  private int currentNum;      // the number that the player will add next 
  private int currentSpotRow;  // the row of the last number placed
  private int currentSpotCol;  // the column of the last number placed

  private Scanner keyboard = new Scanner(System.in); // for user input

  /**
   * Default TimeWasterGame. 
   * Creates a 10x10 board and sets the 
   * top-left corner to 1.
   * 
   * Preconditions: none
   * postconditions: a new TimeWasterGame is created
   */
  public TimeWasterGame() {
    board = new int[10][10];
    resetGame();
  }

  /**
   * This method starts the game which will 
   * ask the user to choice where to place the
   * next number till they want to quit or they 
   * lose/win the game.
   * 
   * preconditions: none
   * postconditions: user has played the game
   */
  public void play() {
    resetGame();

    // Welcomes the user and asks if they want to play a new game
    // or start from a pervious save file
    System.out.println("Welcome to the Time-Waster Game!");
    System.out.println();
    int gameMode = gameOptionsMenu();
    if (gameMode == 1) {
      readFile();
    }
    System.out.println();

    // while the game has not been finished keep printing and letting user play
    while (!hasWon() && !hasLost()) {
      printBoard();
      move();
    }

    // lets the user know whether they have won or lost the game
    if (hasWon()) {
      System.out.println("Congradulations, you won!");  
    } else if (hasLost()) {
      System.out.println("No more possible moves, you lost :(");  
    }
  }

  // resets the grid so it is ready for a new game
  // sets all values to 0, sets the top-left corner to 1
  // 
  // Preconditions: none
  // Postconditions: game is reset and ready to play
  private void resetGame() {
    for (int row = 0; row < board.length; row++) {
      for (int col = 0; col < board.length; col++) {
        board[row][col] = 0;
      }
    }
    board[0][0] = 1;
    currentNum = 2;
    currentSpotRow = 0;
    currentSpotCol = 0;
  }

  // returns a boolean that tells whether the game 
  // has been won
  // 
  // Preconditions: none
  // Postconditions: returns boolean that indicates 
  //                 if user has won 
  private boolean hasWon() {
    for(int row = 0; row < board.length; row++) {
      for(int col = 0; col < board[row].length; col++) {
        if (!isFilled(row, col)) {
          return false;
        }
      }
    }
    return true;
  }

  // returns a boolean that tells whether the game 
  // has been lost
  // 
  // Preconditions: none
  // Postconditions: returns boolean that indicates 
  //                 if user has lost
  private boolean hasLost() {
    return ((!isLegal(currentSpotRow - 3, currentSpotCol)) 
        && (!isLegal(currentSpotRow + 3, currentSpotCol))
        && (!isLegal(currentSpotRow, currentSpotCol - 3))
        && (!isLegal(currentSpotRow, currentSpotCol + 3))
        && (!isLegal(currentSpotRow - 2, currentSpotCol - 2))
        && (!isLegal(currentSpotRow - 2, currentSpotCol + 2))
        && (!isLegal(currentSpotRow + 2, currentSpotCol - 2))
        && (!isLegal(currentSpotRow + 2, currentSpotCol + 2)));
  }

  // prints the game board to the console
  //
  // Preconditions: none
  // Postconditions: board is printed to console
  private void printBoard() {
    for(int row = 0; row < board.length; row++) {
      System.out.print("|");
      for(int col = 0; col < board[row].length; col++) {
        if (board[row][col] < 10) {
          System.out.print(" ");
        }
        // prints blanks 
        if (board[row][col] == 0) {
          System.out.print(" ");
        } else {
          // prints last move
          // (implement a color change so it is easy to tell where the last number is)
          if (board[row][col] == currentNum) {
            System.out.println(board[row][col]);
          } else {
          // prints all other numbers on board
          System.out.print(board[row][col]);
          }
        }
        System.out.print("|");
      }
      System.out.println();
    }
  }

  // saves an text image of the board to a file
  // 
  // preconditions: the user will enter a correct
  //                file name with the .txt extention 
  // postconditions: a file will be saved in the same 
  //                 directiory as the java files with 
  //                 an image of their game board so far 
  private void save() {
    System.out.println();
    System.out.println("Enter a filename to save under (example: example.txt):");
    boolean saved = false;
    while (!saved) {
      String fileName = keyboard.nextLine();
      try {
        PrintWriter file = new PrintWriter(new FileOutputStream(fileName));

        // prints the board to the txt file 
        for(int row = 0; row < board.length; row++) {
          file.print("|");
          for(int col = 0; col < board[row].length; col++) {
            if (board[row][col] < 10) {
              file.print(" ");
            }
            if (board[row][col] == 0) {
              file.print(" ");
            } else {
              file.print(board[row][col]);
            }
            file.print("|");
          }
          file.println();
        }

        file.close();
        System.out.println("Board successfully saved.");
        System.out.println();
        saved = true;
      } catch (FileNotFoundException e) {
        System.out.println();
        System.out.println("Error saving to file, please enter a diffrent file name:");
      }
    }
  }

  // reads a text file and sets the board to and other 
  // instance values to be the values of that text files
  // board so that the user can continue that game
  //
  // preconditions: user will enter a file is a txt file made from 
  //                this classes save() method for a board size 
  //                that is the same as this objects board size 
  // postconditions: the instances variables will emulate the given 
  //                 txt file so the user can continue that board               
  private void readFile() {
    System.out.println();
    System.out.println("Enter a filename to open (example: example.txt):");
    boolean opened = false;
    Scanner file = null;
    String fileName = keyboard.nextLine();

    // opens a given file
    while (!opened) {
      try {
        file = new Scanner(new FileInputStream(fileName));
        opened = true;
      } catch (FileNotFoundException e) {
        System.out.println("Error opening file, please enter a diffrent file name:");
        fileName = keyboard.nextLine();
      }
    }

    // setting the board to proper values
    resetGame();
    int row = 0;
    int col = 0;
    int largestNum = 1;
    int largestNumRow = 0;
    int largestNumCol = 0;
    while (file.hasNextLine()) {
      StringTokenizer line = new StringTokenizer(file.nextLine(), "|" );
      col = 0;
      while (line.hasMoreTokens()) {
        String number = line.nextToken();
        // setting the blank to 0
        if (number.equals("  ")) {
          board[row][col] = 0;  
        } else {
          // getting rid of leading ' '
          if (number.charAt(0) == ' ') {
            number = number.substring(1);
          }
          // setting other numbers in board 
          int valueAtSpot = Integer.valueOf(number);
          board[row][col] = valueAtSpot;
          // checking and finding the largest
          // number on the board and its
          // location
          if (valueAtSpot > largestNum) {
            largestNum = valueAtSpot;
            largestNumRow = row;
            largestNumCol = col;
          }
        }
        col++;
      }
    row++;
    }

    // setting the boards next number and user location 
    currentNum = largestNum + 1;
    currentSpotRow = largestNumRow;
    currentSpotCol = largestNumCol;

    System.out.println("Opening " + fileName);
  }

  // takes the users choice and calls the proper method to 
  // fufill the user's wish, or places the next number in 
  // the spot user designated if user chose to move
  //
  // preconditions: none
  // postconditions: the user will have 'played a round' of the
  //                 game and moved or selected special option to
  //                 save, reset, or quit the game 
  private void move() {
    int rowChange = 0; // the spaces up or down the user will move 
    int colChange = 0; // the spaces left or right the user will move 
    boolean validChoice = false;
    while(!validChoice) {
      menu();
      String choice = keyboard.nextLine();
      rowChange = 0;
      colChange = 0;

      // if the user choose one of the special menu options
      if (choice.equalsIgnoreCase("help")) {
        gameExplanation();
        return;
      } if (choice.equalsIgnoreCase("quit")) {
        System.out.println();
        System.out.println("Thank you for playing!");
        System.exit(0);
      } if (choice.equalsIgnoreCase("reset")) {
        resetGame();
        System.out.println();
        return;
      } if (choice.equalsIgnoreCase("save")) {
        save();
        return;

        // if the player choose a valid move option
        // set the rowChange and colChange to reflect 
        // the spot the user choice to move to 
      } else {
        switch(choice) {
          case "^":
            rowChange = -3;
            break;
          case "v":
            rowChange = 3;
            break;
          case "<":
            colChange = -3;
            break;
          case ">":
            colChange = 3;
            break;
          case "^<":
            rowChange = -2;
            colChange = -2;
            break;
          case "^>":
            rowChange = -2;
            colChange = 2;
            break;
          case "v<":
            rowChange = 2;
            colChange = -2;
            break;
          case "v>":
            rowChange = 2;
            colChange = 2;
            break;
          default:
            break;
        }
        // if user choose an invalid choice keep asking for a move
        if (!isLegal(currentSpotRow + rowChange, currentSpotCol + colChange)) {
          System.out.println();
          System.out.println("Invalid choice!");  
        } else {
          validChoice = true;
        } 
      }  
    }

    // prefroms the move, setting the next number on the spot the user designated
    board[currentSpotRow + rowChange][currentSpotCol + colChange] = currentNum;
    currentSpotRow += rowChange;
    currentSpotCol += colChange;
    currentNum++;
    System.out.println();
  }

  // tells the user all the choices that they can make 
  //
  // preconditions: none 
  // postcondtions: all the possible choices the user can 
  //                make are printed to the console
  private void menu() {
    System.out.println("Which way would you like to move?");
    // explains special options the user can choose from
    System.out.println("enter: 'HELP' for an explanation of the rules");
    System.out.println("enter: 'QUIT' to end program or 'RESET' to reset the game");
    System.out.println("enter: 'SAVE' to save your board to a txt file");

    // tells the user which legal moves they can make
    if(isLegal(currentSpotRow - 3, currentSpotCol)) {
      System.out.println("'^': up");
    } if(isLegal(currentSpotRow + 3, currentSpotCol)) {
      System.out.println("'v': down");
    } if(isLegal(currentSpotRow, currentSpotCol - 3)) {
      System.out.println("'<': left");
    } if(isLegal(currentSpotRow, currentSpotCol + 3)) {
      System.out.println("'>': right");
    } if(isLegal(currentSpotRow - 2, currentSpotCol - 2)) {
      System.out.println("'^<': up-left");
    } if(isLegal(currentSpotRow - 2, currentSpotCol + 2)) {
      System.out.println("'^>': up-right");
    } if(isLegal(currentSpotRow + 2, currentSpotCol - 2)) {
      System.out.println("'v<': down-left");
    } if(isLegal(currentSpotRow + 2, currentSpotCol + 2)) {
      System.out.println("'v>': down-right");
    } 
  }

  // asks the user what game option they would like to play (old or new)
  //
  // preconditions: none
  // postconditions: method will return an int value that will 
  //                 represent which game option they want to play
  private int gameOptionsMenu() {
    System.out.println("Would you like to play a new game (Enter: 'NEW')");
    System.out.println("Or open an old save file (Enter: 'OLD')");

    String choice = keyboard.nextLine();
    while (!(choice.equalsIgnoreCase("new") || choice.equalsIgnoreCase("old"))) {
      System.out.println("ERROR not one of the options enter 'NEW or 'OLD'");
      choice = keyboard.nextLine();
    }

    if (choice.equalsIgnoreCase("new")) {
      return 0;
    } if (choice.equalsIgnoreCase("old")) {
      return 1;
    }
    return 0;
  }

  // explains the game's rules
  // 
  // preconditions: none
  // postconditions: the rules will be printed to the console
  private void gameExplanation() {
    System.out.println();
    System.out.println("The game is played on a 10x10 grid");
    System.out.println("You start with the number 1");
    System.out.println("Each turn you can move in any direction");
    System.out.println("When moving up, down, left, or right you move 3 spaces");
    System.out.println("When moving diagnonaly you move 2 spaces");
    System.out.println("Each time you move the number increased by one is placed on the new spot");
    System.out.println("The goal of the game is to reach 100 and fill the grid");
    System.out.println();
  }

  // returns a boolean that tells whether the 
  // player can move to a certain square on the grid
  // 
  // preconditions: none
  // postconditions: a boolean will be returned telling 
  //                 if the location is a valid square to
  //                 move to
  private boolean isLegal(int row, int col) {
    // if outside the grid
    if (row > board.length - 1 || row < 0) {
      return false;
    } if (col > board[row].length - 1 || col < 0) {
      return false;
      // if filled already 
    } if (isFilled(row, col)) {
      return false;
    }
    return true;
  }

  // checks if there is a number in the spot
  // 
  // preconditions: none
  // postconditions: boolean is returned tellinng
  //                 if the spot aleady has a number
  private boolean isFilled(int row, int col) {
    return board[row][col] != 0;    
  }

}