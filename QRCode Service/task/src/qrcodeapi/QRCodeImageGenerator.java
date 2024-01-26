package qrcodeapi;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

@Service

public class QRCodeImageGenerator {
    private static final Logger logger = LoggerFactory.getLogger(QRCodeImageGenerator.class);
    public BufferedImage createBufferedImage(String content, int size, String correction) {
        ErrorCorrectionLevel errorCorrectionLevel = switch (correction) {
            case "L" -> ErrorCorrectionLevel.L;
            case "M" -> ErrorCorrectionLevel.M;
            case "Q" -> ErrorCorrectionLevel.Q;
            case "H" -> ErrorCorrectionLevel.H;
            default -> ErrorCorrectionLevel.L; // Default to L if none specified
        };

        QRCodeWriter writer = new QRCodeWriter();
        Map<EncodeHintType, ?> hints = Map.of(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
        BufferedImage bufferedImage = null;
        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints);
            bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (WriterException e) {
            logger.error("Error creating QR Code: ", e);
        }
        return bufferedImage;
    }
}

//        BufferedImage bufferedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
//        Graphics2D g = bufferedImage.createGraphics();
//        g.setColor(Color.WHITE);
//        g.fillRect(0, 0, size, size);
//        g.dispose();