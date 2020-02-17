import java.io.IOException;
import java.util.ArrayList;

public class Calculator {

    // Рекурсивно проходит по дереву арифметического выражения и получает его значение
    private static int goThroughArithmTree(Node node) {
        if (node.getMarker() != null) {
            if (node.getMarker().equals("E")) {
                // (axiom E) = (T) (E1).
                return goThroughArithmTree(node.children.get(0)) + goThroughArithmTree(node.children.get(1));
            } else if (node.getMarker().equals("E1")) {
                // (E1) = + (T) (E1) | .
                if (node.children.size() == 3) {
                    if (node.children.get(2).children.size() == 1 && node.children.get(2).children.get(0).getToken() != null ||
                            node.children.get(2).children.get(0).getToken().getAttr().equals("epsilon")) {
                        // Если E1 раскрывается в epsilon
                        return goThroughArithmTree(node.children.get(1));
                    }
                    return goThroughArithmTree(node.children.get(1)) + goThroughArithmTree(node.children.get(2));
                } else {
                    return 0;
                }
            } else if (node.getMarker().equals("T")) {
                // (T) = (F) (T1).
                return goThroughArithmTree(node.children.get(0)) * goThroughArithmTree(node.children.get(1));
            } else if (node.getMarker().equals("F")) {
                // (F) = n | \( (E) \).
                if (node.children.size() == 3) {
                    return goThroughArithmTree(node.children.get(1));
                } else {
                    return goThroughArithmTree(node.children.get(0));
                }
            } else if (node.getMarker().equals("T1")) {
                // (T1) = * (F) (T1) | .
                if (node.children.size() == 3) {
                    if (node.children.get(2).children.size() == 1 && node.children.get(2).children.get(0).getToken() != null ||
                            node.children.get(2).children.get(0).getToken().getAttr().equals("epsilon")) {
                        // Если T1 раскрывается в epsilon
                        return goThroughArithmTree(node.children.get(1));
                    }
                    return goThroughArithmTree(node.children.get(1)) * goThroughArithmTree(node.children.get(2));
                } else {
                    return 1;
                }
            }
        } else {
            Token token = node.getToken();
            if (token.getTag().equals("n")) return Integer.valueOf(token.getAttr());
        }

        System.out.println("[ERROR] going through tree, got: " + node.toString());
        return -1;
    }

    // Принимает в качестве аргумента файл, в котором записано арифметическое выражение,
    // печатает дерево разбора и заложенное в выражение число
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Calculator should have 1 arg");
            return;
        }
        String arithmExprFilename = args[0];

        String expr = null;
        try {
            expr = ExtraFuncs.readFileUsingFiles(arithmExprFilename);
        } catch (IOException err) {
            System.out.println("[ERROR] " + err);
            return;
        }

        // Создаём лексер арифметических выражений
        ArithmLexer lexer = new ArithmLexer(expr);
        // Получаем токены
        ArrayList<Token> tokens = lexer.getAllTokens();
        // Если есть ошибки, печатаем токены и завершаем работу
        if (MainLexer.hasErrors(tokens)) {
            System.out.println("[ERROR] Lexer has errors");
            MainLexer.printTokens(tokens);
            return;
        }

        Node treeRoot = null;
        // Строим дерево разбора с использованием универсального LR-парсера
        LRParser lrParser = new LRParser(ArithmGrammarData.rules, ArithmGrammarData.actionTable, ArithmGrammarData.gotoTable);
        treeRoot = lrParser.Parse(tokens);

        if (treeRoot == null) {
            System.out.println("[ERROR] tree root wasn't created");
            return;
        }

        // Печатаем дерево разбора
        String treeString = ExtraFuncs.treeToString(treeRoot);
        System.out.println(treeString);

        // Печатаем значение арифметического выражения
        System.out.println();
        System.out.println(goThroughArithmTree(treeRoot));
    }
}