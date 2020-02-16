import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MainApp {
    // Файлы, содержащие грамматику описания грамматик и грамматику арифметических выражений
    public static String MAIN_GRAMMAR_DESCRIPTION = "LanguageGrammarOnItself.txt";
    public static String ARITHM_GRAMMAR_DESCRIPTION = "ArithmGrammar.txt";

    // Примеры выражений, записанных на грамматике арифметических выражений
    public static String ARITHM_EXAMPLE_1 = "ArithmExample1.txt";
    public static String ARITHM_EXAMPLE_2 = "ArithmExample2.txt";
    public static String ARITHM_EXAMPLE_3 = "ArithmExample3.txt";

    // Куда сохранять данные для грамматик
    public static String MAIN_GRAMMAR_RULES_FILE = "MainRules.txt";
    public static String MAIN_GRAMMAR_TABLE_FILE = "MainTable.txt";

    public static String ARITHM_GRAMMAR_RULES_FILE = "ArithmRules.txt";
    public static String ARITHM_GRAMMAR_TABLE_FILE = "ArithmTable.txt";

    // Генерирует список правил и LALR таблицы ACTION, GOTO для языка описания грамматик
    public static void generateMainGrammarData() {
        // Считываем грамматику
        String inputText = null;
        try {
            inputText = readFileUsingFiles(MAIN_GRAMMAR_DESCRIPTION);
        } catch (IOException err) {
            System.out.println("[ERROR] " + err);
            return;
        }

        // Создаём лексер
        MainLexer lexer = new MainLexer(inputText);
        // Получаем токены
        ArrayList<Token> tokens = lexer.getAllTokens();
        // Если есть ошибки, печатаем токены и завершаем работу
        if (MainLexer.hasErrors(tokens)) {
            System.out.println("[ERROR] Lexer has errors");
            MainLexer.printTokens(tokens);
            return;
        }

        // Используем парсер методом рекурсивного спуска
        RecursiveParser recursiveParser = new RecursiveParser(tokens);
        // Строим дерево разбора
        Node parserTreeRoot;
        try {
            parserTreeRoot = recursiveParser.getParseTree();
        } catch (Error err) {
            System.out.println("Parsing error: " + err);
            return;
        }

        // Печатаем дерево разбора
        /*
        System.out.println();
        String treeString = treeToString(parserTreeRoot);
        System.out.println(treeString);
        */

        // Переходим к созданию таблиц ACTION и GOTO
        LALRGenerator generator = null;
        try {
            generator = new LALRGenerator(parserTreeRoot);
        } catch (Error err) {
            System.out.println("Generating ACTION and GOTO error: " + err);
            return;
        }

        // Сохраняем правила и таблицу в файлы
        generator.saveRulesAndTables(MAIN_GRAMMAR_RULES_FILE, MAIN_GRAMMAR_TABLE_FILE);
    }

    // Генерирует правила и таблицы для грамматики арифметических выражений
    public static void generateArithmGrammarData() {
        // Считываем грамматику арифметических выражений
        String inputText = null;
        try {
            inputText = readFileUsingFiles(ARITHM_GRAMMAR_DESCRIPTION);
        } catch (IOException err) {
            System.out.println("[ERROR] " + err);
            return;
        }

        // Создаём лексер
        MainLexer lexer = new MainLexer(inputText);
        // Получаем токены
        ArrayList<Token> tokens = lexer.getAllTokens();
        // Если есть ошибки, печатаем токены и завершаем работу
        if (MainLexer.hasErrors(tokens)) {
            System.out.println("[ERROR] Lexer has errors");
            MainLexer.printTokens(tokens);
            return;
        }

        // Читаем правила и таблицы для языка описания грамматик
        ArrayList<Rule> readRules = LALRGenerator.readRules(MAIN_GRAMMAR_RULES_FILE);
        HashMap<String, HashMap<String, HashMap<String, String>>> readTable =
                LALRGenerator.readTable(MAIN_GRAMMAR_TABLE_FILE);

        // Строим дерево разбора с использованием универсального LR-парсера
        LRParser lrParser = new LRParser(readRules, readTable);
        Node treeRoot = lrParser.Parse(tokens);

        // Создаём таблицы ACTION и GOTO для грамматики арифметических выражений
        LALRGenerator generator = null;
        try {
            generator = new LALRGenerator(treeRoot);
        } catch (Error err) {
            System.out.println("Generating ACTION and GOTO error: " + err);
            return;
        }

        // Печатаем дерево разбора
        /*
        System.out.println("Arithmetic exprs tree:");
        String treeString = treeToString(treeRoot);
        System.out.println(treeString);
        */

        // Сохраняем правила и таблицу для грамматики арифметических выражений в файлы
        generator.saveRulesAndTables(ARITHM_GRAMMAR_RULES_FILE, ARITHM_GRAMMAR_TABLE_FILE);
    }

    // Парсит арифметическое выражение и возвращает заложенное в него число
    public static int parseArithmExpr(String exprFileName) {
        // Считываем пример арифметического выражения
        String inputText = null;
        try {
            inputText = readFileUsingFiles(exprFileName);
        } catch (IOException err) {
            System.out.println("[ERROR] " + err);
            return -1;
        }

        System.out.println("Arithm expr: " + inputText);

        // Создаём лексер арифметических выражений
        ArithmLexer lexer = new ArithmLexer(inputText);
        // Получаем токены
        ArrayList<Token> tokens = lexer.getAllTokens();
        // Если есть ошибки, печатаем токены и завершаем работу
        if (MainLexer.hasErrors(tokens)) {
            System.out.println("[ERROR] Lexer has errors");
            MainLexer.printTokens(tokens);
            return -1;
        }

        // Читаем правила и таблицы для языка арифметических выражений
        ArrayList<Rule> readRules = LALRGenerator.readRules(ARITHM_GRAMMAR_RULES_FILE);
        HashMap<String, HashMap<String, HashMap<String, String>>> readTable =
                LALRGenerator.readTable(ARITHM_GRAMMAR_TABLE_FILE);

        // Строим дерево разбора с использованием универсального LR-парсера
        LRParser lrParser = new LRParser(readRules, readTable);
        Node treeRoot = lrParser.Parse(tokens);

        // Печатаем дерево разбора
        System.out.println();
        String treeString = treeToString(treeRoot);
        System.out.println(treeString);

        // По дереву разбора получаем значение арифметического выражения
        return goThroughArithmTree(treeRoot);
    }

    public static void main(String[] args) {
        // 1) Генерируем таблицы синтаксического анализа и правила для языка описания грамматик
        generateMainGrammarData();

        // 2) Генерируем данные для языка арифметических выражений
        generateArithmGrammarData();

        // 3) Тестируем сгенерированный парсер на примерах
        int exprVal = parseArithmExpr(ARITHM_EXAMPLE_1);
        System.out.println("Expr value: " + exprVal);
        System.out.println();

        exprVal = parseArithmExpr(ARITHM_EXAMPLE_2);
        System.out.println("Expr value: " + exprVal);
        System.out.println();

        exprVal = parseArithmExpr(ARITHM_EXAMPLE_3);
        System.out.println("Expr value: " + exprVal);
        System.out.println();
    }

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

    // Читает файл в строку с помощью класса Files
    private static String readFileUsingFiles(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }


    // Возвращает строку с деревом разбора
    public static String treeToString(Node treeRoot) {
        StringBuilder buffer = new StringBuilder(50);
        formTree(buffer, "", "", treeRoot);
        return buffer.toString();
    }

    // Представляет строку дерева разбора в удобном виде
    private static void formTree(StringBuilder buffer, String prefix, String childrenPrefix, Node node) {
        buffer.append(prefix);
        String nodeName = "";
        if (node.getMarker() != null) {
            nodeName = node.getMarker();
        } else {
            if (node.getToken() != null) {
                nodeName = "(" + node.getToken().getTag() + ", " + node.getToken().getAttr() + ")";
            }
        }
        buffer.append(nodeName);
        buffer.append('\n');
        for (Iterator<Node> it = node.children.iterator(); it.hasNext();) {
            Node next = it.next();
            if (it.hasNext()) {
                formTree(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ", next);
            } else {
                formTree(buffer, childrenPrefix + "└── ", childrenPrefix + "    ", next);
            }
        }
    }
}
