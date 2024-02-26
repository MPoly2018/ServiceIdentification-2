package tmp.uqam.stage.metamodel.jsonparser;

/**
 * Message between two classes representation
 */
public class Message {
    private String from;
    private String to;
    private int count;

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getCount() {
        return count;
    }
}
