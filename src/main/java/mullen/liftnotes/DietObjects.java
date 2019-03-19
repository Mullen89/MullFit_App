package mullen.liftnotes;

/**
 * This class creates the Diet object, more specifically, the object that is called from an
 * arraylist of Diet Objects when viewing the diet "History" screen
 */
public class DietObjects {

    private String date;
    private String cal;
    private String pro;
    private String fat;
    private String carb;

    public DietObjects(String date, String cal, String pro, String fat, String carb){
        this.date = date;
        this.cal = cal;
        this.pro = pro;
        this.fat = fat;
        this.carb = carb;
    }

    public String getDate(){
        return date;
    }

    public String getCal(){
        return cal;
    }

    public String getPro(){
        return pro;
    }

    public String getFat(){
        return fat;
    }

    public String getCarb(){
        return carb;
    }
}
