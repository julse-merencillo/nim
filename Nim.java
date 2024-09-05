import java.util.Scanner;
/*
General Notes:
1. The 'while (true)' loops are for memory issues. The previous version of this program used, 
for a lack of a better term, "abusive" method calling. I didn't want the program to unexpectedly
crash due to a stack overflow, so iteration it is.

2. All inputs are treated as strings, and only converted to integers when necessary.
This is due to some weird interactions I encountered while (re)writing the About section,
specifically the page numbering.

3. 'if' statements with only one code of execution are put on a single line, either with the
ternary operator '?' or 'if (condition) (statement);'
This is more of a preference, since I think it's cleaner this way; feel free to disagree.

4. Because arrays start from index 0, you will notice some +1's or -1's thrown around for 
formatting reasons.
*/
public class Nim {
   static Scanner input = new Scanner(System.in);
   static String choice;

   public static void main(String[] args) {
      while (true) {
         System.out.println(" _______ __");
         System.out.println("|    |  |__|.--------.");
         System.out.println("|       |  ||        |");
         System.out.println("|__|____|__||__|__|__|");
         
         System.out.println("[1] Play");
         System.out.println("[2] About");
         System.out.println("[3] Exit");
         System.out.print("> ");
         choice = input.nextLine();
         
         switch (choice) {
            case "1":
               gameSetup();
               break;
            case "2":
               aboutNim(0);
               break;
            case "3":
               System.out.println("Thanks for playing!");
               System.exit(0);
               break;
            default:
               easterEggs(choice);
               break;
         }
      }
   }
   
   /*
   The following code block:
   if (variable < 0) {
      System.out.println("Returning to main menu...");
      break;
   }
   is there to check for negative inputs.
   
   Also, the 'true' argument causes the game to start with Player 1, you may check the gameLoop() methods
   */
   static void gameSetup() {
      System.out.println("Setting up the board... enter any negative number at indicated points [*] to return to the main menu.");
      System.out.println("Once the game starts, you can no longer return to the main menu.");
      
      while (true) {
         int pile_count = pileCount();
         if (pile_count < 0) {
            System.out.println("Returning to main menu...");
            break;
         }
         
         int[] pile = new int[pile_count];
         pile = pileSetup(pile);
         if (pile[0] < 0) {
            System.out.println("Returning to main menu...");
            break;
         }
         
         if (pile.length == 1) {
            int max = setMax(pile[0]);
            if (max < 0) {
               System.out.println("Returning to main menu...");
               break;
            }
            gameLoop(pile[0], max, true);
         }
         else {
            gameLoop(pile, true);
         }
         break;
      }
   }
   
   static int pileCount() {
      int result;
      
      while (true) {
         System.out.println("Enter the number of piles you want to play with. [*]");
         System.out.print("> ");
         try {
            choice = input.nextLine();
            result = Integer.parseInt(choice);
            if (result == 0) throw new Exception(); // you can't have 0 piles
         }
         catch (Exception err) { 
            System.out.println("Invalid input, try again.");
            continue;
         }
         break;
      }
      return result;
   }
   
   static int[] pileSetup(int[] pile) {
      System.out.println("Enter the number of objects in each pile. [*]");
      while (true) {
         for (int i = 0; i < pile.length; i++) {
            try {
               System.out.printf("Current pile: %d%n", i + 1);
               System.out.print("> ");
               choice = input.nextLine();
               pile[i] = Integer.parseInt(choice);
               
               if (pile[i] == 0) throw new Exception(); // you can't have 0 objects when setting up
               
               if (pile[i] < 0) { // the program only checks the first element in the array
                  pile[0] = -1; // this sets that element to a negative number, causing the program to return to main
                  break;
               }
            }
            catch (Exception err) { // if the input is invalid, it will let you input again from the same position
               System.out.printf("Invalid input. Continue from pile %d.%n", i + 1);
               i--;
            }
         }
         break;
      }
      return pile;
   }
   
   // this method only applies to single-pile nim (setting pile_count to 1)
   static int setMax(int pile) {
      int result;
      while (true) {
         try {
            System.out.println("Enter the maximum number of objects to take. [*]");
            System.out.print("> ");
            choice = input.nextLine();
            result = Integer.parseInt(choice);
            
            if (result == 0) throw new Exception("Maximum value cannot be set to 0"); 
            if (result > pile) throw new Exception("Maximum value cannot exceed the number of objects in the pile");
         }
         catch (NumberFormatException nfe) {
            System.out.println("Invalid input, try again.");
            continue;
         }
         catch (Exception err) {
            System.out.printf("%s, try again.%n", err.getMessage());
            continue;
         }
         break;
      }
      return result;
   }
   
   // multi-pile nim game loop
   static void gameLoop(int[] pile, boolean is_first_player) {
      int temp, index;
      
      while (true) {
         int total = 0;
         String current_player = is_first_player ? "Player 1" : "Player 2";
         
         try {
            System.out.printf("It is %s's turn. Please choose the pile you want to take from.%n", current_player);
            pileRender(pile); 
            System.out.print("> ");
            choice = input.nextLine();
            index = Integer.parseInt(choice) - 1;
            
            // pile access errors
            if (index < 0 || index > (pile.length - 1)) throw new Exception("The pile you are trying to access does not exist");
            if (pile[index] == 0) throw new Exception("The selected pile is empty");
            
            String single_or_plural = (pile[index] == 1) ? "" : "s"; // adds an 's' if it is plural
            System.out.println("Current pile chosen:");
            System.out.printf("[Pile %d] %d object%s%n", index + 1, pile[index], single_or_plural);
            
            System.out.println("Enter the number of objects you want to take.");
            System.out.print("> ");
            choice = input.nextLine();
            temp = pile[index] - Integer.parseInt(choice); // stores the operation temporarily
            
            // object take errors
            if (temp < 0) throw new Exception("You cannot take more than the number of objects in the pile");
            if (temp == pile[index]) throw new Exception("You must take an object from the pile");
            if (temp > pile[index]) throw new Exception("You cannot add more objects to the pile"); // saw this while testing
            
            pile[index] = temp; // finalizes the operation to the array
         }
         catch (NumberFormatException nfe) {
            System.out.println("Invalid input, try again.");
            continue;
         }
         catch (Exception err) {
            System.out.printf("%s, try again.%n", err.getMessage());
            continue;
         }
         
         for (int i = 0; i < pile.length; i++) total += pile[i]; // counts all objects in the array
         if (total == 0) break; // if it is 0, the game is over
         
         is_first_player = !is_first_player; // swaps the current player to the other
      }
      // since multi-pile nim is played misere, the player who takes the last object loses. the other player wins
      gameWinner(!is_first_player);
   }
   
   // prints out the pile, lists the number of objects in each
   static void pileRender(int[] pile) {
      String[] empty_piles = new String[pile.length]; // the empty number of piles cannot exceed the total number of piles
      int index = 0;
      
      for (int i = 0; i < pile.length; i++) {
         if (pile[i] == 0) { // the pile is added onto empty_piles if it contains nothing
            empty_piles[index] = (i + 1) + ""; 
            index++; // writes the next empty pile, if any, to the next index
         }
         else { // prints the non-empty piles
            String single_or_plural = (pile[i] == 1) ? "" : "s"; // adds an 's' if there is more than one empty pile
            System.out.printf("[Pile %d] %d object%s%n", i + 1, pile[i], single_or_plural);
         }
      }
      System.out.print("Empty pile(s): ");
      if (index == 0) System.out.print("none"); // if index remains 0, no empty piles were found
      
      for (int i = 0; i < index; i++) { // prints the empty piles
         System.out.print(empty_piles[i]);
         if (i != (index - 1)) System.out.print(", "); // if it is not the last element, a comma is added
      }
      
      System.out.println();
   }
   
   // single pile nim game loop
   static void gameLoop(int pile, int max, boolean is_first_player) {
      int temp;
      
      while (true) {
         String current_player = is_first_player ? "Player 1" : "Player 2";
         
         try {
            System.out.printf("It is %s's turn. Enter the number of objects you want to take from the pile. [1-%d]%n", current_player, max);
            String single_or_plural = (pile == 1) ? "" : "s";
            System.out.printf("[Pile] %d object%s%n", pile, single_or_plural);
            
            System.out.print("> ");
            choice = input.nextLine();
            temp = pile - Integer.parseInt(choice); // temporarily stores the operation
            
            // object take errors
            if (temp < 0) throw new Exception("You cannot take more than the number of objects in the pile");
            if (temp == pile) throw new Exception("You must take an object from the pile");
            if (temp > pile) throw new Exception("You cannot add more objects to the pile");
            if (temp < (pile - max)) throw new Exception("You cannot take more than the set maximum number of objects");
            
            pile = temp; // finalizes the operation to the pile
         }
         catch (NumberFormatException nfe) {
            System.out.println("Invalid input, try again.");
            continue;
         }
         catch (Exception err) {
            System.out.printf("%s, try again.%n", err.getMessage());
            continue;
         }
         
         if (pile == 0) break; // if there are no objects, the game ends
         
         is_first_player = !is_first_player; // swaps the current player to the other one
      }
      // single pile nim is played normally, the last player to take an object in the pile wins
      gameWinner(is_first_player);
   }
   
   // prints the winner of the game
   static void gameWinner(boolean is_first_player) {
      String winner = is_first_player ? "Player 1" : "Player 2";
      
      System.out.printf("Congratulations %s, you have won the game!%n", winner);
      System.out.println("Enter anything to return to the main menu...");
      System.out.print("> ");
      choice = input.nextLine();
      
      System.out.println("Returning to main menu...");
   }
   
   // You may notice that the pages go from 0-9. This will be explained later.
   static void aboutNim(int page) {
      while (true) {
         // page entries
         System.out.printf("About Nim (Page %d of 10)%n", page + 1);
         switch (page) {
            case 0:
               System.out.println("""
                  Overview:
                  Nim is a mathematical strategy game, which presumably originates from ancient China. Its current name was
                  coined by Charles L. Bouton in 1901, along with the mathematical theory behind the game. Nim is played
                  between two players, each taking turns removing objects from only one pile out of multiple. There are two
                  different variants of Nim that can be played in this program: single-pile and multi-pile. Additionally, 
                  these variants have different win conditions.""");
               break;
            case 1:
               System.out.println("""
                  Variants:
                  Single-pile Nim is a simple variant of Nim where two players take turns removing a set amount of objects
                  from a single pile. The players agree on the number of objects in the pile. On their turn, they may take
                  anywhere from one object to a maximum amount set in advance. The maximum amount must not exceed the initial
                  number of objects in the pile. The winner of this variant is the player who takes the last object in the pile.""");
               break;
            case 2:
               System.out.println("""
                  Variants (cont'd):
                  Multi-pile Nim is the standard variant of Nim where two players take turns removing any amount of objects
                  from one of many piles. The players agree on the number of piles. On their turn, they choose the pile in
                  which they will take from, and then take as many objects in that pile only. Unlike single-pile Nim, the
                  player who takes the last object in all piles loses. Thus, the winner of this variant is the player who
                  forces the other to take the last object in all piles.""");
               break;
            case 3:
               System.out.println("""
                  Winning Strategies:
                  There are separate winning strategies for single-pile and multi-pile nim. However, what they have in common
                  is the idea of a winning or losing position. If you are in a winning position, you can guarantee a win,
                  provided that you know the winning strategy. If you are not, you will have to pray for your opponent to play
                  incorrectly, which will guarantee you the winning position.""");
               break;
            case 4:
               System.out.println("""
                  Winning Strategies, Single-pile Nim:
                  For single-pile nim, the strategy is as follows:
                  1. Determine the maximum number of objects that can be taken. Call this number max.
                  2. The objects with an index that is a multiple of (max + 1) are the losing objects. Any player that takes
                  these objects are guaranteed to lose, assuming perfect play. Do not take these objects as much as possible.
                  3. The winning objects are those immediately after the losing objects. If you are winning, you must take the
                  winning object. This forces the other player to take the losing object and guarantees your win.""");
               break;
            case 5:
               System.out.println("""
                  Winning Strategies, Single-pile Nim (cont'd):
                  To see why this strategy guarantees a win, consider a pile of 10 objects, and a maximum to take of 3.
                  [Pile 1] o o o o o o o o o o
                  We will separate each piles such that each section contains 3 (max) + 1 = 4 objects as much as possible. Do
                  not discard the remainders, however.
                  [Pile 1] oooo oooo oo
                  We will also mark the losing objects with x and the winning objects with +
                  [Pile 1] ooox +oox +o
                  Notice that if you are forced to take the losing object, you are one off from taking the winning object.
                  To see why this matters, suppose we reduce this pile to only 4 objects.
                  [Pile 1] ooox
                  It is now obvious. If you are forced to take the losing object, you cannot take the last object. The logic
                  only differs slightly for different maximum values to take.""");
               break;
            case 6:
               System.out.println("""
                  Winning Strategies, Multi-pile Nim:
                  For multi-pile nim, the strategy is much more complicated:
                  1. Determine the number of objects in each pile, and convert each into binary.
                  2. Perform a nim-sum operation on all numbers obtained in the previous step. A nim-sum is a bitwise XOR
                  operation on all the operands.
                  3. If the result is 0, the player during this turn is in a losing position. Otherwise, the player is in a
                  winning position.
                  4. If you are in a winning position, you must take enough objects to make the nim-sum equal 0.
                  5. However, if there are only two piles left, and one pile contains only one object, take every object
                  from the other pile. This forces the player to take the last object and wins you the game.""");
               break;
            case 7:
               System.out.println("""
                  Winning Strategies, Multi-pile Nim (cont'd):
                  To convert a denary (base-10) number into a binary (base-2) number, write the number as a sum of powers of 2.
                  Then, determine which power of 2 corresponds to which column by starting on the right-most column with a 0,
                  and counting up by 1 when moving left. The column will correspond to a power of 2^n, where n is the number
                  of the column. Then, write a 1 wherever the number has that corresponding power of 2 in its sum.
                  To perform an XOR operation on multiple operands, line up all the numbers. Then, for each column, write a
                  1 if the number of 1's is an odd number. Otherwise, write a 0.""");
               break;
            case 8:
               System.out.println("""
                  Winning Strategies, Multi-pile Nim (cont'd):
                  As previously mentioned, the strategy is rather complicated and harder to justify. Consider that if the game
                  starts with 0 piles, the player starting the game loses by default. Note that the nim-sum of 0 piles is 0. 
                  We can then construct the entire game from there, with the observation that if the nim-sum is 0, the current
                  player will always make a move that causes the nim-sum to be non-zero. You can then force the nim-sum to be 0
                  on your turn, which eventually corresponds to the 0 pile.
                  However, remember that the game is played misere. You do not want to be the last to take an object, so the
                  last step serves as a divergence from the usual strategy in normal play. This move forces the other player
                  to empty all the piles which is losing.""");
               break;
            case 9:
               System.out.println("""
                  More Readings: 
                  The mathematical theory behind the winning strategies of Nim, invented by Charles L. Bouton.
                  https://doi.org/10.2307/1967631
                  https://www.jstor.org/stable/1967631
                  
                  The context and history of a Nim variant, along with some history of Nim itself.
                  https://doi.org/10.1109%2FMAHC.2009.41
                  https://ieeexplore.ieee.org/document/5223985
                  
                  A website showing and simplifying the winning strategy for Nim.
                  https://www.archimedes-lab.org/How_to_Solve/Win_at_Nim.html""");
               break;
         }
         
         try {
            System.out.println("\nEnter [0] to return to the main menu, or anything else to move to the next page.");
            System.out.println("You may also enter the page number to jump to that section.");
            
            System.out.print("> ");
            choice = input.nextLine();
            /*
            If a page number is entered, this temporarily stores it.
            Also, subtracting by -1 allows for a nice modulo trick
            */
            int temp_page = Integer.parseInt(choice) - 1;
            
            // input is not within 0-10
            if (temp_page > 9 || temp_page < -1) throw new Exception(); 
            
            page = temp_page; // finalizes the page to jump to
         }
         catch (Exception err) {
            // jumps to the next page, loops you back to the first page once you reach the end
            page = (page + 1) % 10;
         }
         if (page == -1) { // have to adjust to the subtraction as a result. this is a '0' input
            System.out.println("Returning to main menu...");
            break;
         }
      }
   }
   
   // don't ask why this exists
   static void easterEggs(String easter_egg) {
      switch(easter_egg) {
         case "congregation":
            System.out.println("""
               If you're not careful and you noclip out of Geometry Dash in the wrong areas of a level,
               you'll end up in...
                ______                                            __   __              
               |      |.-----.-----.-----.----.-----.-----.---.-.|  |_|__|.-----.-----.
               |   ---||  _  |     |  _  |   _|  -__|  _  |  _  ||   _|  ||  _  |     |
               |______||_____|__|__|___  |__| |_____|___  |___._||____|__||_____|__|__|
                                   |_____|          |_____|                  
                                
               ...where it's nothing but the faint glow of pink, blue, and red lights, the single tones
               of the song, the endless akward timings at minimum frame windows, and approximately
               3 minutes and 4 seconds of repetitive gameplay to be trapped in. Robtop save you if you
               fall into 42%, because it sure as Tartarus will catch you off guard.""");   
            break;
         case "proof":
            System.out.println("""
               \"I have a proof for the truth of the P vs NP conjecture... but I am afraid that it is too big to be contained
               in this program\" - Pierre de Fermat, after time travelling to 2023.""");
            break;
         case "among us":
            System.out.print("You seem pretty sus. Ejecting you now.");
            System.exit(15062018);
            break;
         case "rick astley":
            System.out.println("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
            break;
         case "make love":
            System.out.println("not war?");
            break;
         default:
            System.out.println("Invalid input.");
            break;
      }
   }    
}
