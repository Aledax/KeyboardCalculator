package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        while (true) {

            System.out.print("Enter an expression: ");
            String input = scanner.next();

            if (input.equals("quit")) return;

            try {
                System.out.println("Result: " + calculate(input));
            } catch (IllegalArgumentException IAE) {
                System.out.println("Invalid Input");
            }
        }
    }

    private static double calculate(String input) throws IllegalArgumentException {

        List<String> tokens;

        try {
            tokens = lexArithmetic(input);
        } catch (IllegalArgumentException IAE) {
            throw new IllegalArgumentException();
        }

        Expression mainExpression = new Expression(tokens);

        return mainExpression.evaluate();
    }

    private static List<String> lexArithmetic(String input) throws IllegalArgumentException {

        Pattern pattern = Pattern.compile("\\d+|-\\d+|[+*/()-]");
        Pattern patternBad = Pattern.compile("[^0-9+*/()-]");
        Matcher matcher = pattern.matcher(input);
        Matcher matcherBad = patternBad.matcher(input);

        if (matcherBad.find()) throw new IllegalArgumentException();

        List<String> tokens = new ArrayList<>();
        while (matcher.find()) {
            //System.out.println("Found: " + matcher.group());
            tokens.add(matcher.group());
        }

        return tokens;
    }
}
