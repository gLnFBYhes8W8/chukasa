package pro.hirooka.chukasa.api.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pro.hirooka.chukasa.api.v1.exception.ChukasaBadRequestException;
import pro.hirooka.chukasa.api.v1.exception.ChukasaInternalServerErrorException;
import pro.hirooka.chukasa.api.v1.helper.ChukasaUtility;
import pro.hirooka.chukasa.domain.activity.IFileActivity;
import pro.hirooka.chukasa.domain.activity.IHlsActivity;
import pro.hirooka.chukasa.domain.activity.IProgramActivity;
import pro.hirooka.chukasa.domain.activity.ISystemActivity;
import pro.hirooka.chukasa.domain.config.ChukasaConstants;
import pro.hirooka.chukasa.domain.model.api.ChukasaResponse;
import pro.hirooka.chukasa.domain.model.api.HlsPlaylist;
import pro.hirooka.chukasa.domain.model.hls.ChukasaModel;
import pro.hirooka.chukasa.domain.model.hls.ChukasaSettings;

import javax.servlet.http.HttpServletRequest;

import static java.util.Objects.requireNonNull;

@RestController
@RequestMapping("api/v1/chukasa")
public class  HlsPlayerRestController {

    private static final Logger log = LoggerFactory.getLogger(HlsPlayerRestController.class);

    private final HttpServletRequest httpServletRequest;
    private final IHlsActivity hlsActivity;
    private final IFileActivity fileActivity;
    private final ISystemActivity systemActivity;
    private final IProgramActivity programActivity;

    @Autowired
    public HlsPlayerRestController(
            HttpServletRequest httpServletRequest,
            IHlsActivity hlsActivity,
            IFileActivity fileActivity,
            ISystemActivity systemActivity,
            IProgramActivity programActivity) {
        this.httpServletRequest = requireNonNull(httpServletRequest);
        this.hlsActivity = requireNonNull(hlsActivity);
        this.fileActivity = requireNonNull(fileActivity);
        this.systemActivity = requireNonNull(systemActivity);
        this.programActivity = requireNonNull(programActivity);
    }

    @PostMapping(value = "/start")
    HlsPlaylist play(@RequestBody @Validated ChukasaSettings chukasaSettings) throws ChukasaBadRequestException, ChukasaInternalServerErrorException {

        final String userAgent = httpServletRequest.getHeader("user-agent");
        log.info("user-agent: {}", userAgent);
        if(!userAgent.contains(ChukasaConstants.USER_AGENT)){
            throw new ChukasaBadRequestException("User-Agent is invalid");
        }

        String servletRealPath = httpServletRequest.getSession().getServletContext().getRealPath("");
        log.info("servletRealPath = {}", servletRealPath);

        final ChukasaModel chukasaModel = hlsActivity.startPlayback(chukasaSettings, userAgent, servletRealPath);
        final String playlistURI = ChukasaUtility.buildM3u8URI(chukasaModel);
        if(playlistURI.equals("/")){
            //
        }

        final HlsPlaylist hlsPlaylist = new HlsPlaylist();
        hlsPlaylist.setUri(playlistURI);
        return hlsPlaylist;
    }

    @GetMapping(value = "/stop")
    ChukasaResponse stop() {
        hlsActivity.stopPlayback();
        return remove();
    }

    @GetMapping(value = "/remove")
    ChukasaResponse remove() {
        hlsActivity.removeStream();
        ChukasaResponse chukasaResponseModel = new ChukasaResponse();
        chukasaResponseModel.setMessage("Streaming stopped successfully.");
        return chukasaResponseModel;
    }
}
