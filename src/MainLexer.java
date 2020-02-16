import java.util.ArrayList;

// Класс лексеров для рассматриваемого языка описания грамматик
/*

    (axiom S) = (S1).
    (S1) = (P) (S2).
    (S2) = (P) (S2) | .
    (P) = (L) (P1).
    (P1) = (R) (R1) dot | dot.
    (L) = nonterm equals | axiomnonterm equals
    (R) = nonterm | term | pipe.
    (R1) = (R) (R1) |  .

*/
public class MainLexer {
    private String program;
    private Position position;

    public MainLexer(String program) {
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
            if (position.currentChar() == '|') {
                // Нашли терминал pipe
                position.nextChar();
                return new Token("pipe", new Position(startPos), new Position(position), "|");
            } else if (position.currentChar() == '=') {
                // Нашли терминал equals
                position.nextChar();
                return new Token("equals", new Position(startPos), new Position(position), "=");
            } else if (position.currentChar() == '.') {
                // Нашли терминал dot
                position.nextChar();
                return new Token("dot", new Position(startPos), new Position(position), ".");
            } else if (position.currentChar() == '\\') {
                // Нашли backslash
                // Следующим символом должен следовать терминал
                startPos = new Position(position.nextChar());
                position.nextChar();
                return new Token("term", new Position(startPos), new Position(position),
                        program.substring(startPos.getIndex(), position.getIndex()));
            } else if (position.currentChar() == '(') {
                // Нашли открывающую скобку
                while (true) {
                    position.nextChar();
                    if (position.currentChar() == ' ') {
                        position.nextChar();
                    } else if (position.currentChar() == ')') {
                        // Нашли закрывающую скобку
                        // Проверяем, нет ли после открывающей скобки строки "axiom "
                        if (program.substring(startPos.getIndex()+1, startPos.getIndex()+7)
                                .equals("axiom ")) {
                            // Нашли аксиому грамматики
                            position.nextChar();
                            // Находим нетерминал, соответствующий аксиоме
                            String axiomNontermStr = program.substring(startPos.getIndex()+7, position.getIndex()-1);
                            return new Token("axiomnonterm", new Position(startPos), new Position(position),
                                    axiomNontermStr);
                        } else {
                            // Нашли нетерминал грамматики
                            position.nextChar();
                            return new Token("nonterm", new Position(startPos), new Position(position),
                                    program.substring(startPos.getIndex()+1, position.getIndex()-1));
                        }
                    } else if (position.currentChar() == -1 || position.currentChar() == '\n' ||
                            position.currentChar() == '\t' || position.currentChar() == '\r') {
                        // Внутри скобок символ '\n', '\r', '\t' или конец программы
                        // Возвращаем ошибку
                        position.nextChar();
                        return new Token("error", new Position(startPos), new Position(position),
                                "'\\n', '\\r', '\\t' symbol or EOF between lparen and rparen");
                    } else if (!Character.isAlphabetic(position.currentChar()) &&
                            !Character.isDigit(position.currentChar())) {
                        // Внутри скобок неалфавитный символ
                        // Возвращаем ошибку
                        position.nextChar();
                        return new Token("error", new Position(startPos), new Position(position),
                                "not alpha symbol after lparen");
                    }
                }
            } else {
                // Возвращаем токен-терминал
                while (position.currentChar() != -1 && !Character.isWhitespace(position.currentChar()) &&
                        position.currentChar() != '.') {
                    position.nextChar();
                }
                return new Token("term", new Position(startPos), new Position(position),
                        program.substring(startPos.getIndex(), position.getIndex()));
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
            System.out.println("(" + curToken.getTag() + ", " + curToken.getAttr() + ")");
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
