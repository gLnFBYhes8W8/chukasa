package pro.hirooka.chukasa.domain.service.hls.detector;

import org.apache.commons.text.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import pro.hirooka.chukasa.domain.config.ChukasaConstants;
import pro.hirooka.chukasa.domain.config.common.type.FfmpegVcodecType;
import pro.hirooka.chukasa.domain.config.common.type.PlaylistType;
import pro.hirooka.chukasa.domain.model.hls.ChukasaModel;
import pro.hirooka.chukasa.domain.service.hls.IChukasaModelManagementComponent;
import pro.hirooka.chukasa.domain.service.hls.detector.event.LastMediaSegmentSequenceEvent;
import pro.hirooka.chukasa.domain.service.hls.playlist.IPlaylistCreator;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.*;

import static java.util.Objects.requireNonNull;

@Component
public class FfmpegHlsMediaSegmentDetector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(FfmpegHlsMediaSegmentDetector.class);

    private final String FILE_SEPARATOR = ChukasaConstants.FILE_SEPARATOR;
    private final int MPEG2_TS_PACKET_LENGTH = ChukasaConstants.MPEG2_TS_PACKET_LENGTH;
    private final String STREAM_FILE_NAME_PREFIX = ChukasaConstants.STREAM_FILE_NAME_PREFIX;
    private final String HLS_IV_FILE_EXTENSION = ChukasaConstants.HLS_IV_FILE_EXTENSION;

    private int adaptiveBitrateStreaming;
    private final IChukasaModelManagementComponent chukasaModelManagementComponent;
    private final IPlaylistCreator playlistBuilder;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public FfmpegHlsMediaSegmentDetector(IChukasaModelManagementComponent chukasaModelManagementComponent, IPlaylistCreator playlistBuilder, ApplicationEventPublisher applicationEventPublisher) {
        this.chukasaModelManagementComponent = requireNonNull(chukasaModelManagementComponent, "chukasaModelManagementComponent");
        this.playlistBuilder = requireNonNull(playlistBuilder, "playlistBuilder");
        this.applicationEventPublisher = requireNonNull(applicationEventPublisher, "applicationEventPublisher");
    }

    @Override
    public void run() {

        final ChukasaModel chukasaModel = chukasaModelManagementComponent.get(adaptiveBitrateStreaming);
        final String STREAM_FILE_EXTENSION = chukasaModel.getStreamFileExtension();
        final int sequenceMediaSegment = chukasaModel.getSequenceMediaSegment();
        final boolean canEncrypt = chukasaModel.getChukasaSettings().isCanEncrypt();
        final String mediaPath = chukasaModel.getStreamPath();
        final String encryptedMediaTemporaryPath = chukasaModel.getTempEncPath();
        log.info("sequenceMediaSegment = {}", sequenceMediaSegment);

        final String commonPath = FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + (sequenceMediaSegment + 1) + STREAM_FILE_EXTENSION;
        final String mediaSegmentPath;
        if(canEncrypt){
            mediaSegmentPath = encryptedMediaTemporaryPath + commonPath;
        }else{
            mediaSegmentPath = mediaPath + commonPath;
        }
        log.info("mediaSegmentPath = {}", mediaSegmentPath);

        File mediaSegmentFile = new File(mediaSegmentPath);
        if (mediaSegmentFile.exists()) {
            log.info("file exists: {}", mediaSegmentFile.getAbsolutePath());
            final String nextCommonPath = FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + (sequenceMediaSegment + 2) + STREAM_FILE_EXTENSION;
            final String nextMediaSegmentPath;
            if(canEncrypt){
                nextMediaSegmentPath = encryptedMediaTemporaryPath + nextCommonPath;
            }else{
                nextMediaSegmentPath = mediaPath + nextCommonPath;
            }
            final File nextMediaSegmentFile = new File(nextMediaSegmentPath);
            if (nextMediaSegmentFile.exists()) {
                log.info("file exists: {}", nextMediaSegmentFile.getAbsolutePath());
                if(canEncrypt) {
                    try {
                        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
                        final int HLS_KEY_LENGTH = ChukasaConstants.HLS_KEY_LENGTH;
                        final Key key = makeKey(HLS_KEY_LENGTH);
                        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
                        cipher.init(Cipher.ENCRYPT_MODE, key);

                        final RandomStringGenerator randomStringGenerator = new RandomStringGenerator.Builder()
                                .withinRange('a', 'z').build();
                        final String keyPrefix = randomStringGenerator.generate(10);
                        final String HLS_KEY_FILE_EXTENSION = ChukasaConstants.HLS_KEY_FILE_EXTENSION;
                        final FileOutputStream keyFileOutputStream = new FileOutputStream(mediaPath + FILE_SEPARATOR + keyPrefix + (sequenceMediaSegment + 1) + HLS_KEY_FILE_EXTENSION);

                        chukasaModel.getKeyArrayList().add(keyPrefix);

                        assert key != null;
                        final byte[] keyByteArray = key.getEncoded();
                        keyFileOutputStream.write(keyByteArray);
                        keyFileOutputStream.close();

                        final byte[] ivArray = cipher.getIV();

                        String ivHex = "";
                        for(byte iv : ivArray){
                            final String ivHexTmp = String.format("%02x", iv).toUpperCase();
                            ivHex = ivHex + ivHexTmp;
                        }

                        final String ivPrefix = randomStringGenerator.generate(10);
                        final FileWriter ivFileWriter = new FileWriter(mediaPath + FILE_SEPARATOR + ivPrefix + (sequenceMediaSegment + 1) + HLS_IV_FILE_EXTENSION);
                        ivFileWriter.write(ivHex);
                        ivFileWriter.close();

                        chukasaModel.getIvArrayList().add(ivHex);

                        if(chukasaModel.getFfmpegVcodecType() == FfmpegVcodecType.HEVC_NVENC || chukasaModel.getFfmpegVcodecType() == FfmpegVcodecType.HEVC_QSV) {
                            Path fmp4InitFileInputPath = FileSystems.getDefault().getPath(encryptedMediaTemporaryPath + FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + ".mp4");
                            Path fmp4InitFileOutputPath = FileSystems.getDefault().getPath(mediaPath + FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + ".mp4");
                            Files.copy(fmp4InitFileInputPath, fmp4InitFileOutputPath, StandardCopyOption.REPLACE_EXISTING);
                        }

                        final BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(encryptedMediaTemporaryPath + FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + (sequenceMediaSegment + 1) + STREAM_FILE_EXTENSION));
                        final FileOutputStream fileOutputStream = new FileOutputStream(mediaPath + FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + (sequenceMediaSegment + 1) + STREAM_FILE_EXTENSION);
                        final CipherOutputStream cipherOutputStream = new CipherOutputStream(fileOutputStream, cipher);

                        final byte[] buf = new byte[MPEG2_TS_PACKET_LENGTH];

                        int ch;
                        while ((ch = bufferedInputStream.read(buf)) != -1) {
                            cipherOutputStream.write(buf, 0, ch);
                        }
                        cipherOutputStream.close();
                        fileOutputStream.close();
                        bufferedInputStream.close();

                        // PlaylistType に関わらず，テンポラリディレクトリ内の過去の不要なファイルを削除する．
                        final File temporaryTSFile = new File(encryptedMediaTemporaryPath + FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + (sequenceMediaSegment) + STREAM_FILE_EXTENSION);
                        if (temporaryTSFile.exists()) {
                            temporaryTSFile.delete();
                        }
                        // LIVE プレイリストの場合は過去の不要なファイルを削除する．
                        if (chukasaModel.getChukasaSettings().getPlaylistType() == PlaylistType.LIVE) {
                            final int URI_IN_PLAYLIST = chukasaModel.getHlsConfiguration().getUriInPlaylist();
                            for (int i = 0; i < sequenceMediaSegment - 3 * URI_IN_PLAYLIST; i++) {
                                final File oldMediaSegmentFile = new File(mediaPath + FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + i + STREAM_FILE_EXTENSION);
                                if (oldMediaSegmentFile.exists()) {
                                    oldMediaSegmentFile.delete();
                                }
                                final File oldKeyFile = new File(mediaPath + FILE_SEPARATOR + keyPrefix + i + HLS_KEY_FILE_EXTENSION);
                                if (oldKeyFile.exists()) {
                                    oldKeyFile.delete();
                                }
                                final File oldIVFile = new File(mediaPath + FILE_SEPARATOR + ivPrefix + i + HLS_IV_FILE_EXTENSION);
                                if (oldIVFile.exists()) {
                                    oldIVFile.delete();
                                }
                                final File oldEncryptedMediaSegmentTemporaryFile = new File(encryptedMediaTemporaryPath + FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + i + STREAM_FILE_EXTENSION);
                                if (oldEncryptedMediaSegmentTemporaryFile.exists()) {
                                    oldEncryptedMediaSegmentTemporaryFile.delete();
                                }
                            }
                        }

                    } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | IOException e) {
                        log.error("{} {}", e.getMessage(), e);
                    }
                }else{
                    if(chukasaModel.getChukasaSettings().getPlaylistType() == PlaylistType.LIVE) {
                        final int URI_IN_PLAYLIST = chukasaModel.getHlsConfiguration().getUriInPlaylist();
                        for (int i = 0; i < sequenceMediaSegment - 3 * URI_IN_PLAYLIST; i++) {
                            final File oldMediaSegmentFile = new File(mediaPath + FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + i + STREAM_FILE_EXTENSION);
                            if (oldMediaSegmentFile.exists()) {
                                oldMediaSegmentFile.delete();
                            }
                        }
                    }
                }

                final int nextSequenceMediaSegment = sequenceMediaSegment + 1;
                chukasaModel.setSequenceMediaSegment(nextSequenceMediaSegment);
                chukasaModel.getExtinfList().add((double)chukasaModel.getHlsConfiguration().getDuration());
                chukasaModelManagementComponent.update(adaptiveBitrateStreaming, chukasaModel);

                //
                final int lastSequenceMediaSegment = chukasaModel.getSequenceLastMediaSegment();
                log.info("ls = {}", lastSequenceMediaSegment);
                if(lastSequenceMediaSegment > -1){
                    if(sequenceMediaSegment >= lastSequenceMediaSegment - (chukasaModel.getHlsConfiguration().getUriInPlaylist() - 1)){
                        applicationEventPublisher.publishEvent(new LastMediaSegmentSequenceEvent(this, adaptiveBitrateStreaming));
                    }
                }
            }

        }
        playlistBuilder.create();

    }

    private Key makeKey(int keyBit) {
        try {
            final KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            final SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            keyGenerator.init(keyBit, secureRandom);
            final Key generatedKey = keyGenerator.generateKey();
            return generatedKey;
        } catch (NoSuchAlgorithmException e) {
            log.error("{} {}", e.getMessage(), e);
            return null;
        }
    }

    public void setAdaptiveBitrateStreaming(int adaptiveBitrateStreaming) {
        this.adaptiveBitrateStreaming = adaptiveBitrateStreaming;
    }
}

