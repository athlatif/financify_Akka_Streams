package app;

public final class Number {
    private String record;

    public Number() {

    }

    public Number(String _id) {
        this.record = _id;
    }

    public void setId(String _id) {
        this.record = _id;
    }

    public String getId() {
        return record;
    }

}