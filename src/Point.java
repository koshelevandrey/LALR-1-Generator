import java.util.ArrayList;

// Класс пунктов
public class Point {
    // Правая и левая части правила пункта
    private String leftPart;
    private ArrayList<String> rightPart;

    // Позиция точки в пункте
    private int pointPosition;

    // Символ предпросмотра пункта
    private String lookaheadSymbol;

    public Point(String leftPart, ArrayList<String> rightPart, int pointPosition, String lookaheadSymbol) {
        this.leftPart = leftPart;
        this.rightPart = rightPart;
        this.pointPosition = pointPosition;
        this.lookaheadSymbol = lookaheadSymbol;
    }

    // Проверка равенства ядер двух пунктов
    public boolean kernelIsEqual(Point otherPoint) {
        if (leftPart.equals(otherPoint.leftPart) &&
                pointPosition == otherPoint.pointPosition) {
            if (rightPart.size() != otherPoint.rightPart.size()) {
                return false;
            }
            for (int i = 0; i < rightPart.size(); i++) {
                if (!rightPart.get(i).equals(otherPoint.getRightPart().get(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    // Проверка равенства двух пунктов
    public boolean isEqual(Point otherPoint) {
        if (leftPart.equals(otherPoint.leftPart) &&
                pointPosition == otherPoint.pointPosition &&
                lookaheadSymbol.equals(otherPoint.lookaheadSymbol)) {
            if (rightPart.size() != otherPoint.rightPart.size()) {
                return false;
            }
            for (int i = 0; i < rightPart.size(); i++) {
                if (!rightPart.get(i).equals(otherPoint.getRightPart().get(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public int getPointPosition() {
        return pointPosition;
    }

    public String getLeftPart() {
        return leftPart;
    }

    public ArrayList<String> getRightPart() {
        return rightPart;
    }

    public String getLookaheadSymbol() {
        return lookaheadSymbol;
    }

    @Override
    public String toString() {
        String rightPartStr = "";
        for (int i = 0; i < rightPart.size(); i++) {
            rightPartStr += " ";
            if (pointPosition == i) {
                rightPartStr += "*";
            }
            rightPartStr += rightPart.get(i);
        }
        if (pointPosition == rightPart.size()) {
            rightPartStr += "*";
        }
        return "[" + leftPart + " ->" + rightPartStr + ", " + lookaheadSymbol + "]";
    }
}
