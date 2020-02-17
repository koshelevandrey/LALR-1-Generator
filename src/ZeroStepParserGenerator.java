import java.io.IOException;
import java.util.ArrayList;

// Используется для выполнения нулевого шага раскрутки компилятора через метод рекурсивного спуска
public class ZeroStepParserGenerator {

    // Считывает описание грамматики из файла, указанном в 1-ом аргументе,
    // и порождает данные в файл, имя которого записано во 2-ом аргументе
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Zero Step Parser Generator should have 2 args");
            return;
        }
        String grammarDescriptionFilename = args[0];
        String saveClassFilename = args[1];

        String grammar = null;
        try {
            grammar = ExtraFuncs.readFileUsingFiles(grammarDescriptionFilename);
        } catch (IOException err) {
            System.out.println("[ERROR] " + err);
            return;
        }

        // Создаём лексер
        MainLexer lexer = new MainLexer(grammar);
        // Получаем токены
        ArrayList<Token> tokens = lexer.getAllTokens();

        // Если есть ошибки, печатаем токены и завершаем работу
        if (MainLexer.hasErrors(tokens)) {
            System.out.println("[ERROR] Lexer has errors");
            MainLexer.printTokens(tokens);
            return;
        }

        Node treeRoot = null;
        // Используем парсер, написанный методом рекурсивного спуска
        RecursiveParser recursiveParser = new RecursiveParser(tokens);
        try {
            treeRoot = recursiveParser.getParseTree();
        } catch (Error err) {
            System.out.println("Parsing error: " + err);
            return;
        }

        if (treeRoot == null) {
            System.out.println("[ERROR] tree root wasn't created");
            return;
        }

        // Печатаем дерево разбора
        // String treeString = ExtraFuncs.treeToString(treeRoot);
        // System.out.println(treeString);

        // Генерируем список правил, таблицы ACTION и GOTO
        LALRGenerator generator = null;
        try {
            generator = new LALRGenerator(treeRoot, saveClassFilename);
        } catch (Error err) {
            System.out.println("Generating ACTION and GOTO error: " + err);
            return;
        }
    }
}
