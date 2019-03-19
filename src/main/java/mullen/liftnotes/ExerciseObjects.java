package mullen.liftnotes;

/**
 * This class creates the Exercise object, more specifically, the object that is called from an
 * arraylist of Exercise Objects after clicking on an item from the main "workout" screen.
 */
public class ExerciseObjects {

    private String ex1;
    private String ex2;
    private String ex3;
    private String ex4;

    public ExerciseObjects(String ex1, String ex2, String ex3, String ex4){
        this.ex1 = ex1;
        this.ex2 = ex2;
        this.ex3 = ex3;
        this.ex4 = ex4;
    }

    public String getEx1(){
        return ex1;
    }

    public String getEx2(){
        return ex2;
    }

    public String getEx3(){
        return ex3;
    }

    public String getEx4(){
        return ex4;
    }
}
