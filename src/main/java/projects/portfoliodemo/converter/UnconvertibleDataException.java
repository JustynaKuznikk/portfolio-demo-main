package projects.portfoliodemo.converter;

public class UnconvertibleDataException extends IllegalArgumentException{

    public UnconvertibleDataException(String message){
        super(message);
    }
}
