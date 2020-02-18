// Класс позиций в тексте входной строки (используется в лексере)
public class Position {
    // Входной текст
    private String program;
    // Номер текущего рассматриваемого символа
    private int index;
    // Номер строки, в которой находится рассматриваемый символ
    private int line;
    // Номер колонки в строке, в которой находится рассматриваемый символ
    private int col;

    public Position(String program) {
        this.program = program;
        index = 0;
        line = 1;
        col = 1;
    }

    public Position(Position otherPosition) {
        this.program = otherPosition.program;
        this.index = otherPosition.index;
        this.line = otherPosition.line;
        this.col = otherPosition.col;
    }

    public int currentChar() {
        return (index >= program.length()) ? -1 : program.charAt(index);
    }

    public Position nextChar() {
        int currentChar = currentChar();
        if (currentChar != -1) {
            if (currentChar == '\n') {
                line++;
                col = 1;
            } else {
                col++;
            }
            index++;
        }
        return this;
    }

    public int getIndex() {
        return index;
    }

    public int getLine() {
        return line;
    }

    public int getCol() {
        return col;
    }

    public boolean isArithmOp() {
        return this.currentChar() == '+' || this.currentChar() == '-' || this.currentChar() == '*'
                || this.currentChar() == '/';
    }

    public boolean isReserved() {
        return this.currentChar() == '=' || this.currentChar() == '(' || this.currentChar() == ')'
                || this.currentChar() == '|' || this.currentChar() == '.' || this.currentChar() == '\\';
    }

    public boolean isPunct() {
        return this.currentChar() == '!' || this.currentChar() == '?' || this.currentChar() == ';'
                || this.currentChar() == ':' || this.currentChar() == '-' || this.currentChar() == '"'
                || this.currentChar() == ',';
    }
}
