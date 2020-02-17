import java.util.ArrayList;
import java.util.Arrays;

// Класс правил КС-грамматики
public class Rule {
    // Правая и левая части правила
    private String leftPart;
    private ArrayList<String> rightPart;

    public Rule(String leftPart, ArrayList<String> rightPart) {
        this.leftPart = leftPart;
        this.rightPart = rightPart;
    }

    public Rule(String leftPart, String[] rightPart) {
        this.leftPart = leftPart;
        this.rightPart = new ArrayList<>(Arrays.asList(rightPart));
    }

    public String getLeftPart() {
        return leftPart;
    }

    public ArrayList<String> getRightPart() {
        return rightPart;
    }
}
