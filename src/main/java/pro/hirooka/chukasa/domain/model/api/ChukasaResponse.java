package pro.hirooka.chukasa.domain.model.api;

public class ChukasaResponse {

    private String message = "";

    public ChukasaResponse(){

    }

    public ChukasaResponse(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
