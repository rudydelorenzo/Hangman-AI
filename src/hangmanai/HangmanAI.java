package hangmanai;

import javax.swing.*;
import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.regex.*;
import java.io.*;

public class HangmanAI {

    public static Scanner wordscan = new Scanner(System.in);
    public static String solution;
    public static ArrayList <Character> usedLetters = new ArrayList();
    public static ArrayList <String> origWords;
    public static ArrayList <String> words;
    public static ArrayList <Integer> appearances;
    public static ArrayList <Integer> wordsAppearances = new ArrayList();
    public static int mode = 0; //0 is AI, 1 is human;
    public static int maxWrong = 11;
    public static boolean gaveUp = false;
    public static boolean debugMode = false;
    
    public static void main(String[] args) {
        ArrayList <String> rawArrayList;
        String dictFilename = "hangman.rudydict";
        boolean playAgain = true;
        String[] modes = {"Artificial Intelligence", "Human"};
        System.out.println("IMPORTING DICTIONARY...");
        rawArrayList = new ArrayList(Arrays.asList(loadArrFromFile(dictFilename)));
        origWords = getWordsArrFromRaw(rawArrayList);
        wordsAppearances = getAppearancesArrFromRaw(rawArrayList);
        cleanUpArrays();
        System.out.println("DONE IMPORTING.");
        while (playAgain) {
            words = (ArrayList<String>)origWords.clone();
            appearances = (ArrayList<Integer>)wordsAppearances.clone();
            gaveUp = false;
            usedLetters = new ArrayList();
            Object selectedMode = JOptionPane.showInputDialog(null, "Choose mode", "Mode selection", JOptionPane.INFORMATION_MESSAGE, null, modes, modes[0]);
            if (selectedMode.toString().toLowerCase().contains("intelligence")) mode = 0;
            else mode = 1;
            solution = JOptionPane.showInputDialog("What's the right answer?").toLowerCase();
            char[] template = new char[solution.length()];
            for (int i = 0; i<solution.length(); i++) template[i] = '_';
            boolean didGuess = false;
            char guess = ' ';
            int wrongGuesses = 0;
            String wrongLetters = " ";
            int attempts = 0;
            
            
            pause(500);
            System.out.print("Starting in 3...");
            pause(1000);
            System.out.print("2...");
            pause(1000);
            System.out.println("1...");
            System.out.println();
            pause(1000);
            
            double startTime = System.currentTimeMillis();
            while (!didGuess && wrongGuesses <= (maxWrong-1)) {
                //print template
                for (int i = 0; i<template.length; i++) {
                    System.out.print(template[i]+" ");
                }
                System.out.println();

                //ask for input and ensures this isn't a duplicate
                do {
                    System.out.println("Enter your guess!");
                    try {
                        if (mode == 0) guess = getMostCommonChar(template, wrongLetters);
                        else if (mode == 1) guess = wordscan.nextLine().toLowerCase().charAt(0);
                    } catch (StringIndexOutOfBoundsException e) {
                        System.out.println("Please enter a letter.");
                    }
                    if (checkDuplicate(guess)) System.out.println("Enter a letter you haven't already entered!");
                } while (checkDuplicate(guess));

                //update template
                if (checkLetter(guess)) {
                    template = updateTemplate(guess, template);
                } else {
                    wrongLetters = wrongLetters + guess;
                    wrongGuesses++;
                }

                printHangman(wrongGuesses);

                didGuess = checkEndOfGame(template);
                
                attempts++;
            }
            double endTime = System.currentTimeMillis();
            double elapsedTime = (endTime-startTime)/1000.00000;
            if (wrongGuesses >= maxWrong) {
                System.out.println("You lost!");
                System.out.println("The word was \"" + solution + "\"!");
                System.out.println("Wrong letters guessed:" + wrongLetters);
            } else {
                System.out.println("You got it!");
                System.out.println("The word was \"" + solution + "\"!");
                System.out.println("Number of wrong guesses: " + wrongGuesses);
                System.out.println("Wrong letters guessed:" + wrongLetters);
            }
            
            
            System.out.println("Time elapsed: " + elapsedTime + " seconds.");

            boolean wordIsAbsentFromDictionary = true;
            for (int i = 0; i<origWords.size(); i++) {
                if (solution.equals(origWords.get(i))) {
                    wordsAppearances.set(i, wordsAppearances.get(i) + 1);
                    wordIsAbsentFromDictionary = false;
                    break;
                }
            }
            if (gaveUp || wordIsAbsentFromDictionary) {
                origWords.add(solution);
                wordsAppearances.add(1);
                System.out.println("Word wasn't in dictionary");
            }
            
            //save the array
            System.out.println("SAVING NEW DICTIONARY FILE...");
            mergeSaveStringArray(dictFilename, origWords, wordsAppearances);
            System.out.println("DONE SAVING.");
            
            System.out.println("Want to play again? Y/N");
            String playAgainAnswer = wordscan.nextLine();
            if (playAgainAnswer.toLowerCase().contains("y")) playAgain = true;
            else if (playAgainAnswer.toLowerCase().contains("n")) playAgain = false;
            
        }
    }
    public static boolean checkDuplicate(char guessedLetter) {
        for (int i = 0; i<usedLetters.size(); i++) {
            if (usedLetters.get(i) == guessedLetter) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean checkLetter(char guessedLetter) {
        usedLetters.add(guessedLetter);
        for (int i = 0; i<solution.length(); i++) {
            if (guessedLetter == solution.charAt(i)) {
                return true;
            }
        }
        return false;
    }
    
    public static char[] updateTemplate(char guess, char[] template) {
        for (int i = 0; i<template.length; i++) {
            if (guess == solution.charAt(i)) template[i] = guess;
        }
        return template;
    }
    
    public static boolean checkEndOfGame(char[] template) {
        for (int i = 0; i<template.length; i++) {
            if (template[i] == '_') return false;
        }
        return true;
    }
    
    public static void printHangman(int wrongNum) {
        if (wrongNum == 0) {
            System.out.println("------------\n" +
            "\n" +
            "\n" +
            "\n");
        } else if (wrongNum == 1) {
            System.out.println("------------\n" +
            "     |\n" +
            "\n" +
            "\n");
        } else if (wrongNum == 2) {
            System.out.println("------------\n" +
               "     |\n" +
               "     O     \n" +
            "\n");
        } else if (wrongNum == 3) {
            System.out.println("------------\n" +
            "     |\n" +
            "     O                       \n" +
            "  /\n");
        } else if (wrongNum == 4) {
            System.out.println("------------\n" +
            "     |\n" +
            "     O                       \n" +
            "  /  |\n");
        } else if (wrongNum == 5) {
            System.out.println("------------\n" +
            "     |\n" +
            "     O                       \n" +
            "  /  |  \\    \n");
        } else if (wrongNum == 6) {
            System.out.println("------------\n" +
            "     |\n" +
            "     O                       \n" +
            "  /  |  \\                     \n" +
            "   /");
        } else if (wrongNum >= maxWrong) {
            System.out.println("------------\n" +
            "     |\n" +
            "     O                       \n" +
            "  /  |  \\                     \n" +
            "   /   \\  ");
        }
    }
    
    public static String[] loadArrFromFile(String filename){
        String addLines = "";
        try {
            BufferedReader file = new BufferedReader(new FileReader(filename));
            while (file.ready()) {
                addLines += file.readLine().toLowerCase();
                addLines += ",";
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        String[] tempStringArray = addLines.split(",");
        return tempStringArray;
    }
    
    public static void saveStringArray(String filename, ArrayList temp ) {
        try {
            PrintWriter file = new PrintWriter(new FileWriter(filename));
            for (int i = 0; i < temp.size(); i++) {
                file.println(temp.get(i));
            }
            file.close();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }
    
    public static void mergeSaveStringArray(String filename, ArrayList wordsArr, ArrayList occurrences ) {
        try {
            PrintWriter file = new PrintWriter(new FileWriter(filename));
            for (int i = 0; i < wordsArr.size(); i++) {
                file.println(wordsArr.get(i) + "\t" + occurrences.get(i));
            }
            file.close();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }

    public static char getMostCommonChar(char[] template, String wrongLetters) {
        Pattern wrongLettersPattern = Pattern.compile("[" + wrongLetters + "]");
        Pattern mask = Pattern.compile(templateToRegEx(template));
        for (int i = 0; i<words.size(); i++) {
            if (words.get(i).length() != template.length || wrongLettersPattern.matcher(words.get(i)).find() || !mask.matcher(words.get(i)).matches()) {
                words.remove(i);
                appearances.remove(i);
                i--;
            }
        }
        if (debugMode) {
            for (int i =0; i<words.size(); i++) {
                System.out.println(words.get(i) + ", " + appearances.get(i));
            }
        }
        
        //if there are no more options give up
        if (words.isEmpty()) {
            mode = 1; //comment out this line to stop "killswitch" when AI has given up
            gaveUp = true;
        }
        
        int[] letterCount = letterCountArr();
        
        int largestNum = 0;
        int largestNumIndex = 0;
        for (int i = 0; i<letterCount.length; i++) {
            boolean hasBeenUsed = false;
            if (letterCount[i] >= largestNum) {
                for (int j = 0; j<usedLetters.size(); j++) {
                    if ((char)i+97 == usedLetters.get(j)) {
                        hasBeenUsed = true;
                    }
                }
                if (!hasBeenUsed) {
                    largestNum = letterCount[i];
                    largestNumIndex = i;
                }
            }
        }
        int asciiCode = 97+largestNumIndex;
        if (debugMode) System.out.println("Most common char: " + (char)asciiCode);
        return (char)asciiCode;
    }
    
    public static String templateToRegEx(char[] templateArray) {
        String toReturn = "";
        for (int i = 0; i<templateArray.length; i++) {
            if (templateArray[i] == '_') toReturn += ".";
            else toReturn += templateArray[i];
        }
        if (debugMode) System.out.println("Regex that'll be used as a mask: " + toReturn);
        return toReturn;
    }
    
    public static int[] letterCountArr() {
        int[] tempLetterCount = new int[26];
        for (int i = 0; i<words.size(); i++) {
            for (int j = 0; j<words.get(i).length(); j++) {
                tempLetterCount[((int)words.get(i).charAt(j))-97]++;
            }
        }
        return tempLetterCount;
    }
    
    public static void pause(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
    
    public static void cleanUpArrays() {
        Pattern illegalCharacters = Pattern.compile("[ -/,/.ñáéíóú@]");
        for (int i = 0; i<origWords.size(); i++) {
            if (illegalCharacters.matcher(origWords.get(i)).find()) {
                System.out.println("Removed Word: " + origWords.get(i));
                origWords.remove(i);
                wordsAppearances.remove(i);
                i--;
            }
        }
    }
    
    public static ArrayList getWordsArrFromRaw(ArrayList rawAL) {
        ArrayList <String> returnArr = new ArrayList();
        for (int i = 0; i<rawAL.size(); i++) {
            returnArr.add(i, rawAL.get(i).toString().substring(0, rawAL.get(i).toString().indexOf("\t")));
        }
        return returnArr;
    }
    
    public static ArrayList getAppearancesArrFromRaw(ArrayList rawAL) {
        ArrayList <Integer> returnArr = new ArrayList();
        for (int i = 0; i<rawAL.size(); i++) {
            returnArr.add(i, Integer.parseInt(rawAL.get(i).toString().substring(rawAL.get(i).toString().indexOf("\t")+1)));
        }
        return returnArr;
    }
    
    public static ArrayList cleanUpArray(ArrayList array) {
        ArrayList <String> toReturn = new ArrayList();
        Pattern illegalCharacters = Pattern.compile("[ -/,/.ñáéíóú@]");
        for (int i = 0; i<array.size(); i++) {
            if (illegalCharacters.matcher(array.get(i).toString()).find()) {
                System.out.println("Removed Word: " + array.get(i));
                array.remove(i);
                i--;
            }
        }
        return array;
    }
    
}
