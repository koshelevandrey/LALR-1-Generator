import java.util.ArrayList;

// Класс лексеров арифметических выражений
/*

    (F) = n | \( (E) \).
    (T) = (F) (T1).
    (T1) = * (F) (T1) | .
    (axiom E) = (T) (E1).
    (E1) = + (T) (E1) | .

*/
public class ArithmLexer {
    private String program;
    private Position position;

    public ArithmLexer(String program) {
        this.program = program;
        position = new Position(program);
    }

    public Token nextToken() {
        while (position.currentChar() != -1) {
            // Пропускаем пробельные символы
            if (Character.isWhitespace(position.currentChar())) {
                while (position.currentChar() != -1 && Character.isWhitespace(position.currentChar())) {
                    position.nextChar();
                }
                // Начинаем итерацию заново, чтобы проверить, что текст не закончился
                continue;
            }

            // Опознаём токен
            Position startPos = new Position(position);
            if (position.currentChar() == '(') {
                // Нашли терминал (
                position.nextChar();
                return new Token("(", new Position(startPos), new Position(position), "(");
            } else if (position.currentChar() == ')') {
                // Нашли терминал )
                position.nextChar();
                return new Token(")", new Position(startPos), new Position(position), ")");
            } else if (position.currentChar() == '*') {
                // Нашли терминал *
                position.nextChar();
                return new Token("*", new Position(startPos), new Position(position), "*");
            } else if (position.currentChar() == '+') {
                // Нашли терминал +
                position.nextChar();
                return new Token("+", new Position(startPos), new Position(position), "+");
            } else if (Character.isDigit(position.currentChar())) {
                // Нашли число
                while (position.currentChar() != -1 && Character.isDigit(position.currentChar())) {
                    position.nextChar();
                }
                return new Token("n", new Position(startPos), new Position(position),
                        program.substring(startPos.getIndex(), position.getIndex()));
            } else {
                // Возвращаем ошибку
                position.nextChar();
                return new Token("error", new Position(startPos), new Position(position),
                        "unallowed symbol " + program.substring(startPos.getIndex(), position.getIndex()));
            }
        }

        return new Token("EOF", new Position(position), new Position(position), "EOF");
    }

    // Возвращает все токены
    public ArrayList<Token> getAllTokens() {
        ArrayList<Token> tokens = new ArrayList<>();
        Token curToken = nextToken();
        while (!curToken.getTag().equals("EOF")) {
            tokens.add(curToken);
            curToken = nextToken();
        }

        return tokens;
    }

    // Есть ли лексически ошибки
    public static boolean hasErrors(ArrayList<Token> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            Token curToken = tokens.get(i);
            if (curToken.getTag().equals("error")) {
                return true;
            }
        }

        return false;
    }

    // Печатает токены
    public static void printTokens(ArrayList<Token> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            Token curToken = tokens.get(i);
            if (curToken.getTag().equals("error")) {
                System.out.print("[ERROR]" );
            }
            System.out.println("(" + curToken.getTag() + ", " + curToken.getAttr() + ") : " +
                    "(" + curToken.getStart().getLine() + ", " + curToken.getStart().getCol() + ") - (" +
                    curToken.getFollow().getLine() + ", " + curToken.getFollow().getCol() + ")");
        }
    }

    public static boolean findErrorsAndPrintTokens(ArrayList<Token> tokens) {
        boolean hasError = false;
        for (int i = 0; i < tokens.size(); i++) {
            Token curToken = tokens.get(i);
            if (curToken.getTag().equals("error")) {
                hasError = true;
            }
            System.out.println("(" + curToken.getTag() + ", " + curToken.getAttr() + ")");
        }

        return hasError;
    }
}
