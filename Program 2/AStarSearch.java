//Programming project 2
//Jessica Russ Fall 2020

//INSTRUCTIONS
//No graphics are required for this program but using them will help you with debugging and problem solving.  
//Your environment should be a 15x15 tile-based world that randomly generates nodes that are unpathable (blocks) in 10% of the nodes.  
//This should be done each time the program compiles ensuring that there are different environment makeups each run.  
//The program should display the generated environment when the program runs, and should allow the user to select a starting node and goal node.  
//This can be done via text input into the console or with a GUI.  
//Once the start and goal nodes have been defined, the program should run the A* algorithm to find a path. 
//The path should be displayed (series of [x,y] nodes, highlighting nodes, or actually moving the agent) if one exists, or a message indicating that a path could not be found. 
//The user should be able to continue specifying starting and goal nodes after paths have been found.

//import arraylist
import java.util.ArrayList;
//import scanner
import java.util.Scanner;

/** A star search class */
public class AStarSearch {

    // create an array of nodes
    private Node[][] nodesArray;

    // create a start node
    private Node startNode;

    // create a goal node
    private Node goalNode;

    // create a number boundary
    private int tileBoundary;

    // create a boolean path found variable and set it false to start
    private boolean pathFound = false;

    // create an openlist using arraylist
    private ArrayList<Node> openList;

    // create a closed list using arraylist
    private ArrayList<Node> closedList;

    // create a path list using array list
    private ArrayList<Node> finalPath;

    /**
     * main method that creates a 15x15 tile environment with all nodes having a 10%
     * chance of being blocked takes input from user for x,y start and x,y end, then
     * uses a star to calculate the appropriate path, if there is one, then returns
     * the correct path from starting x,y node to ending x,y node this repeats until
     * user exits
     */
    public static void main(String[] args) {
        // create a new scanner
        Scanner scanner = new Scanner(System.in);

        // create a new tile board
        Tiles tileBoard = new Tiles();

        // print out the title of beginning environment and skip a line
        System.out.println("Starting Board");
        System.out.println("\n");

        // print out the environment tiles
        System.out.println(tileBoard.toString() + "\n");
        // set the viarbale of play again to no
        char playAgain = 'n';

        // create do while loop
        do {

            // ask user to input starting positio and skipa line
            System.out.println("Choose a starting row number");
            System.out.println("\n");

            // create a starting row variable and get user input
            System.out.println("Row Number:");
            int startingRow = scanner.nextInt();
            // create a starting column variable and get user input

            System.out.println("Choose a starting column number");
            System.out.println("\n");

            System.out.println("Column Number:");
            int startingColumn = scanner.nextInt();

            // while the tile startrow and column are unpathable
            while ((tileBoard.getType(startingRow, startingColumn)).equals(Tiles.UNPATHABLE)) {
                // print out an error and ask for new input
                System.out.println("That position is unavailable. Please try again");
                // create a starting row variable and get user input
                System.out.println("Row Number:");
                startingRow = scanner.nextInt();
                // create a starting column variable and get user input

                System.out.println("Choose a starting column number");
                System.out.println("\n");

                System.out.println("Column Number:");
                startingColumn = scanner.nextInt();
                tileBoard.setElement(startingRow, startingColumn, "s");
            } // end while loop

            // set the element
            tileBoard.setElement(startingRow, startingColumn, "s");
            // ask for a goal position
            // ask user to input goal positio and skipa line
            System.out.println("Choose a goal row number");
            System.out.println("\n");

            // create a starting row variable and get user input
            System.out.println("Row Number:");
            int goalRow = scanner.nextInt();
            // create a starting column variable and get user input

            System.out.println("Choose a goal column number");
            System.out.println("\n");

            System.out.println("Column Number:");
            int goalColumn = scanner.nextInt();

            // while the tile goal row and column are unpathable

            while ((tileBoard.getType(goalRow, goalColumn)).equals(Tiles.UNPATHABLE)) {
                // print out an error and ask for new input
                System.out.println("That position is unavailable. Please try again");

                // set the goal tile row and column
                // create a starting row variable and get user input
                System.out.println("Row Number:");
                goalRow = scanner.nextInt();
                // create a starting column variable and get user input

                System.out.println("Choose a goal column number");
                System.out.println("\n");

                System.out.println("Column Number:");
                goalColumn = scanner.nextInt();
                tileBoard.setElement(goalRow, goalColumn, "g");
            } // end while
            tileBoard.setElement(goalRow, goalColumn, "g");
            // print out the tile map
            System.out.println("\n\n" + tileBoard.toString());

            // have the tiles generate the path from start to goal
            tileBoard.createPath(startingRow, startingColumn, goalRow, goalColumn);

            // print out title of solution
            System.out.println("List of Path Nodes" + tileBoard.showPath());
            System.out.println(" ");

            System.out.println("Solution");
            // update the tile board and print
            tileBoard.updateTileBoard();
            System.out.println("\n" + tileBoard.tilePathToString());

            // ask user if they want to play again
            System.out.println("Would you like to play again?");
            System.out.println("Type y for yes or n for no: ");

            playAgain = scanner.next().charAt(0);

            // reset the nodes and path
            tileBoard.resetPath();
            tileBoard.resetNodes();

            // reset the start and goal elements
            System.out.println(" ");

            tileBoard.setElement(startingRow, startingColumn, "~");
            tileBoard.setElement(goalRow, goalColumn, "~");
            // exit program
        } // end do loop
        while (playAgain == 'y' || playAgain == 'Y');
        System.out.println("Thank you for playing");
    }// end of main method

    /** a star search constructor */
    public AStarSearch(Node[][] nodeArray, int startRow, int startColumn, int goalRow, int goalColumn, int boundary) {

        // set the boundary to boundary
        tileBoundary = boundary;

        // set the node array to nodes
        nodesArray = nodeArray;

        // initialize the open list to a blank array list
        openList = new ArrayList<>();

        // initliaizethe closed list toa new blank array list
        closedList = new ArrayList<>();

        // set the goal node to the goal row and goalcolumn
        goalNode = nodesArray[goalRow][goalColumn];

        // setthe start node to the start row and column
        startNode = nodesArray[startRow][startColumn];

        // set the G ofstart tozero
        startNode.setG(0);

        // set the H of start to the calculated heuristic of start

        startNode.setH(calcHeuristic(startNode));

        // set the F of start to g+h using method
        startNode.setF();

        // set the parent of start to null
        startNode.setParent(null);

        // add start to the openlist
        openList.add(startNode);

        // do search
        searchBoard();

    }// end of constructor

    /**
     * method to search the tiles, pulling from the open list, generating the
     * appropraite path, and then adding the node to the closed list
     */
    public void searchBoard() {

        // make a node called currentnode
        Node currentNode;

        // while the size of theopenlist isntzero, aka after start is initialized
        while (openList.size() != 0) {

            // set the current node by using thefind lowest method
            currentNode = findLowestValue();

            // remove the current node from the open list
            openList.remove(currentNode);

            // if thecurrent node is the goal node
            if (currentNode.equals(goalNode)) {

                // set the path found to true
                pathFound = true;

                // generate the solution path
                createPath();

            } // end of if statement

            // generate the neighbors of the current node
            createNeighbors(currentNode);

            // add the current node to the closed list
            closedList.add(currentNode);

        } // end of while loop
    }// end of search board method

    /**
     * method to generate the node neighbors , check if the node is valid, calculate
     * the cost of moving, setting G, H, and F, then updating the parent, and adding
     * the node to the open list if it isnt already there
     */
    public void createNeighbors(Node node) {
        // set the row to the node row
        int row = node.getRow();

        // set the column to the node column
        int column = node.getColumn();

        // loop through all neighbors
        for (int i = 0; i < 8; i++) {

            // create a check node
            Node checkNode;
            try {
                // go through all of for loop and set the node row and column based on
                // surroundings

                // node is one above
                if (i == 0) {
                    checkNode = nodesArray[row + 1][column];
                } // end of if i=0

                // node is one below
                else if (i == 1) {
                    checkNode = nodesArray[row - 1][column];
                } // end of if i=1

                // node is one right
                else if (i == 2) {
                    checkNode = nodesArray[row][column + 1];
                } // end of if i=2

                // node is one left
                else if (i == 3) {
                    checkNode = nodesArray[row][column - 1];
                } // end of if i=3

                // node is diagonal bottom left
                else if (i == 4) {
                    checkNode = nodesArray[row - 1][column - 1];
                } // end of if i=4

                // node is diagonal bottom right
                else if (i == 5) {
                    checkNode = nodesArray[row - 1][column + 1];
                } // end of if i=5

                // node is diagonal top left
                else if (i == 6) {
                    checkNode = nodesArray[row + 1][column - 1];
                } // end of if i=6

                // node is diagonal top right
                else {
                    checkNode = nodesArray[row + 1][column + 1];
                } // end of

            } // end d0
            catch (Exception e) {
                continue;
            }

            // see ifthe checked node is valid
            if (isValidMove(checkNode)) {

                // create a cost of moving and set it to the g cost
                int costOfMoving = node.getG();

                // if less than 4 aka a straight move, add 10 to the move ocst
                if (i < 4) {
                    costOfMoving = costOfMoving + 10;

                } // end of if i<4

                // otherwise add 14 to the move cost because its diagonal
                else {
                    costOfMoving = costOfMoving + 14;

                } // end of else statement

                // if the gcost of thechecked node is zero or if the g cost ofthechecked node is
                // greater than zero and the cost of moving is less than g cost
                if (checkNode.getG() == 0 || (checkNode.getG() > 0 && costOfMoving < checkNode.getG())) {

                    // set the g costof hte checked node tothe current movecost
                    checkNode.setG(costOfMoving);

                    // set the h of the checked node tothe heuristic calcuation of the node

                    checkNode.setH(calcHeuristic(checkNode));

                    // set the f node of the checked node
                    checkNode.setF();

                    // set the parent node of the checked node
                    checkNode.setParent(node);

                    // if the open list doesnt have the checked node in it,add it to the openlist
                    if (!openList.contains(checkNode)) {
                        openList.add(checkNode);
                    } // end of inner if
                } // end of outer if statement
            } // end of if valid
        } // end of for loop

    }// end of generate neighbors method

    /**
     * method to generate the path once the goal has been reached by backtracking
     * parent nodes and adding them to the array list
     */
    public void createPath() {
        // create a new path variable
        finalPath = new ArrayList<>();

        // set the current node to the goal node
        Node currentNode = goalNode;

        // while the parent of hte current nodeisnt null
        while (currentNode.getParent() != null) {

            // add the current node to the path
            finalPath.add(currentNode);

            // set the current node to its parent node to backtrack
            currentNode = currentNode.getParent();
        } // end of while loop

        finalPath.add(currentNode);

    }// end of generate path method

    /** method to get the node path */
    public ArrayList<Node> getNodePath() {
        // return the node path
        return finalPath;
    }// end of get node path method

    /** method to return if the path is found or not */
    public boolean pathFound() {
        // return the pathfound variable
        return pathFound;

    }// end of path found method

    /**
     * method to return if the move is valid, within the bounds and is pathable, and
     * is not in the closed list
     */
    public boolean isValidMove(Node node) {
        // if the node iswithin the bounds and is pathable, and its not in the closed
        // list return true, otherwise returnfalse
        if (isWithinBounds(node) && pathable(node) && !closedList.contains(node)) {
            return true;
        } // end of if statement
        else {
            return false;
        } // end else

    }// end of valid method

    /** method to check if the node is within the bounds of the environment */
    public boolean isWithinBounds(Node node) {
        // set the row to the node row
        int row = node.getRow();

        // set the column to the node column
        int column = node.getColumn();

        // if therow is greater orequal to 0 and its less than the boundary of hte
        // mapand the column is greater orequal to 0 and its less than the boundary
        // return true
        if (row >= 0 && row < tileBoundary && column >= 0 && column < tileBoundary) {
            return true;
        } // end of if statement

        // else returnfalse
        else {
            return false;
        } // end else

    }// end of is within bounds

    /** method to check if the node is pathable */
    public boolean pathable(Node node) {
        // if the node type is pathable return true
        if (node.getType() == Node.PATHABLE) {
            return true;
        } // end of if statmenet
          // else return false
        else {
            return false;
        } // end else statemnet
    }// end of pathable method

    /** method to find the lowest f value from the open list */
    public Node findLowestValue() {
        // if the size of the openlist isnt zero
        if (openList.size() != 0) {

            // create a lowest node and set it to the open list 0
            Node lowestFNode = openList.get(0);

            // loop through theopen list
            for (int i = 1; i < openList.size(); i++) {

                // if the open list node f cost is less than the lowest f cost
                if (openList.get(i).getF() < lowestFNode.getF()) {

                    // set the lowest tothe open list node
                    lowestFNode = openList.get(i);
                } // end of inner if
            } // end of for statement

            // return the lowest
            return lowestFNode;

        } // end of outer if statement

        // return null
        return null;
    }// end of find lowest value method

    /** method to calculate the heuristic value of the node */
    public int calcHeuristic(Node node) {
        // create a current row variableand set it to the node row
        int currentRow = node.getRow();

        // create a currentcolumn variable and set it to the node column
        int currentColumn = node.getColumn();

        // set the heuristic value to 0
        int heuristic = 0;

        // while the current row is less than the row of the goal
        while (currentRow < goalNode.getRow()) {
            // increase the current row
            currentRow++;
            // add 10 to the heuristic value
            heuristic = heuristic + 10;
        } // end while loop

        // while the current row is greater than the goal row
        while (currentRow > goalNode.getRow()) {
            // decrease the current row
            currentRow--;
            // add 10 to the heuristic value
            heuristic = heuristic + 10;
        } // end while loop

        // while the current column is less than the column of the goal
        while (currentColumn < goalNode.getColumn()) {

            // increase the current column
            currentColumn++;
            // add 10 to the heuristic value
            heuristic = heuristic + 10;
        } // end while loop

        // while the current column is greater than the column of the goal
        while (currentColumn > goalNode.getColumn()) {
            // decrease the current column
            currentColumn--;
            // add 10 to the heuristic value
            heuristic = heuristic + 10;
        } // end while loop

        return heuristic;
    }// end of calculate heuristic method

}// end of a start search