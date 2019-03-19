package mullen.liftnotes;

/**
 * Creates the PR Object
 */
public class PRObject {
    private String title;
    private String num;
    private String date;

    public PRObject(String title, String num, String date){
        this.title = title;
        this.num = num;
        this.date = date;
    }

    public String getTitle(){
        return title;
    }

    public String getNum(){
        return num;
    }

    public String getDate(){
        return date;
    }
}
