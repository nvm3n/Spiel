import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public int playerMoney = 1000;

    public int getPlayerMoney() {
        return playerMoney;
    }

    public void setPlayerMoney(int money) {
        this.playerMoney = money;
    }

    // Save the player's money to a file
    public void saveMoneyState(String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(String.valueOf(playerMoney));
            System.out.println("Money state saved successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while saving money state: " + e.getMessage());
        }
    }

    // Load the player's money from a file
    public void loadMoneyState(String fileName) {
        try (Scanner scanner = new Scanner(new File(fileName))) {
            if (scanner.hasNextInt()) {
                playerMoney = scanner.nextInt();
                System.out.println("Money state loaded successfully.");
            } else {
                System.out.println("Invalid data in the file.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while loading money state: " + e.getMessage());
        }
    }

    // Blackjack game logic
    public void playBlackjack() {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        System.out.println("Welcome to Blackjack!");
        System.out.println("You have $" + playerMoney);

        while (playerMoney > 0) {
            System.out.print("Enter your bet: ");
            int bet = scanner.nextInt();

            if (bet > playerMoney) {
                System.out.println("You don't have enough money to make that bet.");
                continue;
            }

            // Deal initial cards
            List<Integer> playerHand = new ArrayList<>();
            List<Integer> dealerHand = new ArrayList<>();
            playerHand.add(dealCard(random));
            playerHand.add(dealCard(random));
            dealerHand.add(dealCard(random));
            dealerHand.add(dealCard(random));

            System.out.println("Your hand: " + playerHand + " (Total: " + calculateHandValue(playerHand) + ")");
            System.out.println("Dealer's visible card: " + dealerHand.get(0));

            // Player's turn
            while (true) {
                System.out.print("Do you want to (1) Hit or (2) Stand? ");
                int choice = scanner.nextInt();
                if (choice == 1) {
                    playerHand.add(dealCard(random));
                    System.out.println("Your hand: " + playerHand + " (Total: " + calculateHandValue(playerHand) + ")");
                    if (calculateHandValue(playerHand) > 21) {
                        System.out.println("You busted!");
                        playerMoney -= bet;
                        break;
                    }
                } else if (choice == 2) {
                    break;
                } else {
                    System.out.println("Invalid choice. Please choose 1 or 2.");
                }
            }

            // Dealer's turn
            if (calculateHandValue(playerHand) <= 21) {
                System.out.println("Dealer's hand: " + dealerHand + " (Total: " + calculateHandValue(dealerHand) + ")");
                while (calculateHandValue(dealerHand) < 17) {
                    dealerHand.add(dealCard(random));
                    System.out.println("Dealer's hand: " + dealerHand + " (Total: " + calculateHandValue(dealerHand) + ")");
                }

                // Determine winner
                int playerTotal = calculateHandValue(playerHand);
                int dealerTotal = calculateHandValue(dealerHand);

                if (dealerTotal > 21 || playerTotal > dealerTotal) {
                    System.out.println("You win!");
                    playerMoney += bet;
                } else if (playerTotal < dealerTotal) {
                    System.out.println("You lose!");
                    playerMoney -= bet;
                } else {
                    System.out.println("It's a tie!");
                }
            }

            System.out.println("Your current money: $" + playerMoney);
            System.out.print("Do you want to play again? (yes/no): ");
            String playAgain = scanner.next();
            if (!playAgain.equalsIgnoreCase("yes")) {
                break;
            }
        }

        System.out.println("Game over! You finished with $" + playerMoney);
        saveMoneyState("moneyState.txt");
    }

    // Deal a random card (values 1-11, where 11 represents an Ace)
    private int dealCard(Random random) {
        return random.nextInt(11) + 1;
    }

    // Calculate the total value of a hand
    private int calculateHandValue(List<Integer> hand) {
        int total = 0;
        int aces = 0;

        for (int card : hand) {
            if (card == 11) {
                aces++;
            }
            total += card;
        }

        // Adjust for Aces
        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }

        return total;
    }

    public static void main(String[] args) {
        Main game = new Main();
        game.loadMoneyState("moneyState.txt");
        game.playBlackjack();
    }
}