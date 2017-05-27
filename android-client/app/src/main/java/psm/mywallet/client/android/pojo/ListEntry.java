package psm.mywallet.client.android.pojo;

/**
 * @author Adrian Michalski
 */

public class ListEntry {

    private String description;
    private String tags;
    private String value;
    private String balance;

    public ListEntry(String description, String tags, String value, String balance) {
        this.description = description;
        this.tags = tags;
        this.value = value;
        this.balance = balance;
    }

    public String getDescription() {
        return description;
    }

    public String getTags() {
        return tags;
    }

    public String getValue() {
        return value;
    }

    public String getBalance() {
        return balance;
    }
}
