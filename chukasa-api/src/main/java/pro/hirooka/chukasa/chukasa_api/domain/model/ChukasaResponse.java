package pro.hirooka.chukasa.chukasa_api.domain.model;

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
