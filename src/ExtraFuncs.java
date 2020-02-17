import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

// Класс, содержащий различные функции, используемые при выполнении различных этапов работы компилятора
public class ExtraFuncs {
    // Читает файл в строку с помощью класса Files
    public static String readFileUsingFiles(String fileName) throws IOException {
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
        String nodeName = node.toString();
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
