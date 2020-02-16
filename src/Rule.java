import java.io.Serializable;
import java.util.ArrayList;

// Класс правил КС-грамматики
// (Serializable для возможности записи в файл)
public class Rule implements Serializable {
    // Правая и левая части правила
    private String leftPart;
    private ArrayList<String> rightPart;

    public Rule(String leftPart, ArrayList<String> rightPart) {
        this.leftPart = leftPart;
        this.rightPart = rightPart;
    }

    public String getLeftPart() {
        return leftPart;
    }

    public ArrayList<String> getRightPart() {
        return rightPart;
    }
}
