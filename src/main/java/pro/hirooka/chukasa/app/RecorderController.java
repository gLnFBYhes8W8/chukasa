package pro.hirooka.chukasa.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import pro.hirooka.chukasa.domain.model.recorder.ReservedProgram;
import pro.hirooka.chukasa.domain.service.recorder.IRecorderService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("recorder")
public class RecorderController {

    private static final Logger log = LoggerFactory.getLogger(RecorderController.class);

    @Autowired
    IRecorderService recorderService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<ReservedProgram> create(@RequestBody ReservedProgram reservedProgram, UriComponentsBuilder uriComponentsBuilder){
        ReservedProgram createdReservedProgram = recorderService.create(reservedProgram);
        URI uri = uriComponentsBuilder.path("/recorder/{id}").buildAndExpand(createdReservedProgram.getId()).toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(uri);
        return new ResponseEntity<>(createdReservedProgram, httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    ReservedProgram read(@PathVariable int id){
        return recorderService.read(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    List<ReservedProgram> read(){
        return recorderService.read();
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    ReservedProgram update(@PathVariable int id, @RequestBody ReservedProgram reservedProgram){
        reservedProgram.setId(id);
        return recorderService.update(reservedProgram);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable int id){
        recorderService.delete(id);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(){
        recorderService.deleteAll();
    }
}

