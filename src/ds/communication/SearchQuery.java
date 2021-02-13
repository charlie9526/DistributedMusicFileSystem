package ds.communication;

public class SearchQuery {
    private String  ID;
    private String queryFileNameString;

    public SearchQuery(String ID, String queryFileNameString) {
        this.ID = ID;
        this.queryFileNameString = queryFileNameString;
    }

    public String getID() {
        return ID;
    }

    public String getQueryFileNameString() {
        return queryFileNameString;
    }
}
