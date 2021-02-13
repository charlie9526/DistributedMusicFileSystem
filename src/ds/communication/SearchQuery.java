package ds.communication;

public class SearchQuery {
    private int ID;
    private String queryFileNameString;

    public SearchQuery(int ID, String queryFileNameString) {
        this.ID = ID;
        this.queryFileNameString = queryFileNameString;
    }

    public int getID() {
        return ID;
    }

    public String getQueryFileNameString() {
        return queryFileNameString;
    }
}
