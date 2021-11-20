package main;

import java.util.ArrayList;
import java.util.List;

public class Expression {

    // Is always either:
    // 1. A number with no operator (in which case _operator = "None")
    // 2. An expression containing an operator and two more numbers or expressions (in which case _value = 0)

    private boolean _hasError = false;
    private final List<String> _tokens;
    private final String _operator;
    private final int _value; // Only if operator is None
    private final Expression _left;
    private final Expression _right;

    public Expression(List<String> tokens) {

        // Remove redundant brackets
        while (tokens.get(0).equals("(") && tokens.get(tokens.size() - 1).equals(")")) {
            tokens = tokens.subList(1, tokens.size() - 1);
        }

        _tokens = new ArrayList<>(tokens);

        String previous = "None"; // Last token
        int bracketOffset = 0; // Checks bracket nesting and symmetry
        String mainOp = "None"; // The lowest priority (final) operator to perform (and therefore the value of _operator)
        int leftEndIndex = 0; // The last token of the left side of the operator
        int rightStartIndex = 0; // The first token of the right side of the operator

        int counter = 0;
        for (String token : tokens) {

            if (token.equals("(")) { // Open bracket

                if (previous.matches("[+-]") && bracketOffset == 0) { // Add or subtract
                    mainOp = previous;
                    leftEndIndex = counter - 2;
                    rightStartIndex = counter;
                }
                if (mainOp.matches("None|[*/]") && previous.matches("\\d+") && bracketOffset == 0) { // Implicit multiplication
                    mainOp = "*";
                    leftEndIndex = counter - 1;
                    rightStartIndex = counter;
                }

                bracketOffset++;

            } else if (token.equals(")")) { // Close bracket
                if (bracketOffset == 0) displayError("Error: Premature close bracket");
                if (previous.matches("[+*/-]")) displayError("Error: Operator empty on right");

                bracketOffset--;

            } else if (token.matches("[*/]")) { // Asterisk / Slash
                if (previous.matches("[+*/-]")) displayError("Error: Illegal adjacent operators");
                if (!previous.matches("\\d+|-\\d+|[(]")) displayError("Error: Operator empty on left");

                if (mainOp.matches("None|[*/]") && bracketOffset == 0) { // Multiplication and division
                    mainOp = token;
                    leftEndIndex = counter - 1;
                    rightStartIndex = counter + 1;
                }

            } else if (token.equals("+")) { // Plus
                if (bracketOffset == 0) { // Addition
                    mainOp = token;
                    leftEndIndex = counter - 1;
                    rightStartIndex = counter + 1;
                }

            } else { // Signed number
                if (token.matches("-\\d+") && previous.matches("\\d+|-\\d+") && bracketOffset == 0) { // Subtraction
                    mainOp = "+";
                    leftEndIndex = counter - 1;
                    rightStartIndex = counter;
                } else if (mainOp.matches("None|[*/]") && previous.equals(")") && bracketOffset == 0) { // Implicit multiplication
                    mainOp = "*";
                    leftEndIndex = counter - 1;
                    rightStartIndex = counter;
                }
            }

            previous = token;
            counter++;
        }

        if (previous.matches("[+*/-]")) displayError("Error: Operator empty on right");
        if (bracketOffset != 0) displayError("Error: Open-ended brackets");

        _operator = mainOp;

        if (_operator.equals("None")) { // Number
            _value = Integer.parseInt(tokens.get(0));
            _left = null;
            _right = null;
        } else { // Complete expression
            //System.out.println("Found " + _operator + " operator in " + toString());
            _value = 0;
            _left = new Expression(tokens.subList(0, leftEndIndex + 1));
            _right = new Expression(tokens.subList(rightStartIndex, tokens.size()));
        }
    }

    public double evaluate() throws ArithmeticException {
        if (_hasError) {
            System.out.println("Cannot evaluate.");
            return 0;
        }

        switch (_operator) {
            case "None": return _value;
            case "+": return _left.evaluate() + _right.evaluate();
            case "-": return _left.evaluate() - _right.evaluate(); // Only used when minus is before an open bracket, otherwise + is used
            case "*": return _left.evaluate() * _right.evaluate();
            case "/":
                if (_right.evaluate() == 0) {
                    System.out.println("Error: Division by zero. Returning 1");
                    return 1;
                }
                return _left.evaluate() / _right.evaluate();
            default: throw new RuntimeException("Error: Unrecognized Operator");
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        _tokens.forEach(sb::append);
        return sb.toString();
    }

    private void displayError(String message) {
        System.out.println(message);
        _hasError = true;
    }
}
