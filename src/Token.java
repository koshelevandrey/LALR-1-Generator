// Класс токенов
public class Token {
    // Лексический домен
    private String tag;
    // Позиции начала и конца токена
    private Position start;
    private Position follow;
    // Аттрибут токена
    private String attr;

    public Token(String tag, Position start, Position follow, String attr) {
        this.tag = tag;
        this.start = start;
        this.follow = follow;
        this.attr = attr;
    }

    public String getTag() {
        return tag;
    }

    public String getAttr() {
        return attr;
    }

    public Position getStart() {
        return start;
    }

    public Position getFollow() {
        return follow;
    }

    @Override
    public String toString() {
        return "(" + tag + ", " + attr + ")";
    }
}
