package pro.hirooka.chukasa.domain.model.api;

import lombok.Data;

@Data
public class ChukasaResponse {

    private String message = "";

    public ChukasaResponse(){

    }

    public ChukasaResponse(String message){
        this.message = message;
    }
}
