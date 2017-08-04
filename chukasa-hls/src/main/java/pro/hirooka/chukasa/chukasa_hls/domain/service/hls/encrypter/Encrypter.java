package pro.hirooka.chukasa.chukasa_hls.domain.service.hls.encrypter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import pro.hirooka.chukasa.chukasa_common.domain.constants.ChukasaConstants;
import pro.hirooka.chukasa.chukasa_common.domain.enums.StreamingType;
import pro.hirooka.chukasa.chukasa_hls.domain.model.ChukasaModel;
import pro.hirooka.chukasa.chukasa_hls.domain.service.IChukasaModelManagementComponent;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;

import static java.util.Objects.requireNonNull;

@Deprecated
@Slf4j
public class Encrypter implements Runnable {

    static final String FILE_SEPARATOR = System.getProperty("file.separator");

    final String STREAM_FILE_NAME_PREFIX = ChukasaConstants.STREAM_FILE_NAME_PREFIX;
    final int MPEG2_TS_PACKET_LENGTH = ChukasaConstants.MPEG2_TS_PACKET_LENGTH;

    private int adaptiveBitrateStreaming;

    private IChukasaModelManagementComponent chukasaModelManagementComponent;

    public Encrypter(int adaptiveBitrateStreaming, IChukasaModelManagementComponent chukasaModelManagementComponent){
        this.adaptiveBitrateStreaming = adaptiveBitrateStreaming;
        this.chukasaModelManagementComponent = requireNonNull(chukasaModelManagementComponent, "chukasaModelManagementComponent");
    }

    @Override
    public void run() {

        ChukasaModel chukasaModel = chukasaModelManagementComponent.get(adaptiveBitrateStreaming);
        final String STREAM_FILE_EXTENSION = chukasaModel.getStreamFileExtension();
        String streamPath = chukasaModel.getStreamPath();
        String tempEncPath = chukasaModel.getTempEncPath();

        int seqTsEnc = 0; //getSeqTsEnc();
        seqTsEnc = chukasaModel.getSeqTsEnc();
        if(chukasaModel.getChukasaSettings().getStreamingType().equals(StreamingType.OKKAKE)){
            seqTsEnc = chukasaModel.getSeqTsOkkake() - 1;
        }
        if(chukasaModel.isFlagLastTs()) {
            seqTsEnc = chukasaModel.getSeqTsLast();
        }

        Key sKey;
        Cipher c;
        FileOutputStream keyOut;
        FileWriter ivOut;
        FileInputStream fis;
        BufferedInputStream bis;
        FileOutputStream fos;
        CipherOutputStream cos;

        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

            sKey = makeKey(128); // Key length is 128bit
            c = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");

            c.init(Cipher.ENCRYPT_MODE, sKey);

            // Set Key File Name at random
            String keyPre = RandomStringUtils.randomAlphabetic(10);
            keyOut = new FileOutputStream(streamPath + FILE_SEPARATOR + keyPre + seqTsEnc + ".key");

            chukasaModel.getKeyArrayList().add(keyPre);
            chukasaModel = chukasaModelManagementComponent.update(adaptiveBitrateStreaming, chukasaModel);

            byte[] keyOutByte = sKey.getEncoded();
            keyOut.write(keyOutByte);
            keyOut.close();

            byte[] iv = c.getIV();

            String ivHex = "";
            for(int i = 0; i < iv.length; i++){
                String ivHexTmp = String.format("%02x", iv[i]).toUpperCase();
                ivHex = ivHex + ivHexTmp;
            }

            String ivPre = RandomStringUtils.randomAlphabetic(10);
            ivOut = new FileWriter(streamPath + FILE_SEPARATOR + ivPre + seqTsEnc + ".iv");
            ivOut.write(ivHex);
            ivOut.close();

            chukasaModel.getIvArrayList().add(ivHex);
            chukasaModel = chukasaModelManagementComponent.update(adaptiveBitrateStreaming, chukasaModel);

            fis = new FileInputStream(tempEncPath + FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + seqTsEnc + STREAM_FILE_EXTENSION);
            bis = new BufferedInputStream(fis);
            fos = new FileOutputStream(streamPath + FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + seqTsEnc + STREAM_FILE_EXTENSION);
            cos = new CipherOutputStream(fos, c);
            if(chukasaModel.getChukasaSettings().getStreamingType().equals(StreamingType.OKKAKE)){
                // TODO:
                fis = new FileInputStream(tempEncPath + FILE_SEPARATOR + "fileSequenceEncoded" + seqTsEnc + STREAM_FILE_EXTENSION);
                bis = new BufferedInputStream(fis);
                fos = new FileOutputStream(streamPath + FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + seqTsEnc + STREAM_FILE_EXTENSION);
                cos = new CipherOutputStream(fos, c);
            }

            byte[] buf = new byte[MPEG2_TS_PACKET_LENGTH];

            int ch;
            while((ch = bis.read(buf)) != -1){
                cos.write(buf, 0, ch);
            }
            cos.close();
            fos.close();
            bis.close();
            fis.close();

        } catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | NoSuchProviderException | IOException e) {
            log.error("{} {}", e.getMessage(), e);
        }

    }

    // Generate Random Key
    static Key makeKey(int keyBit) throws NoSuchAlgorithmException{

        KeyGenerator kg = KeyGenerator.getInstance("AES");
        SecureRandom rd = SecureRandom.getInstance("SHA1PRNG");
        kg.init(keyBit, rd);
        Key key = kg.generateKey();
        return key;

    } // makeKey
}


