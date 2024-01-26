package qrcodeapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
public class QRCodeController {
    private final QRCodeImageGenerator imageGenerator;

    @Autowired
    public QRCodeController(QRCodeImageGenerator imageGenerator) {
        this.imageGenerator = imageGenerator;
    }

    @GetMapping("/api/health")
    public ResponseEntity<String> health() {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @GetMapping(path = "/api/qrcode")
    public ResponseEntity<?> getImage(
            @RequestParam String contents,
            @RequestParam(defaultValue = "250") int size,
            @RequestParam(defaultValue = "png") String type,
            @RequestParam(defaultValue = "L") String correction) {
        if (contents == null || contents.isBlank()) {
            return getErrorResponse("Contents cannot be null or blank");
        }
        if (size < 150 || size > 350) {
            return getErrorResponse("Image size must be between 150 and 350 pixels");
        }
        if (!Objects.equals(correction, "L") && !Objects.equals(correction, "M") && !Objects.equals(correction, "H") && !Objects.equals(correction, "Q")) {
            return getErrorResponse("Permitted error correction levels are L, M, Q, H");
        }
        if (!Objects.equals(type, "png")
                && !Objects.equals(type, "jpeg")
                && !Objects.equals(type, "gif")) {
            return getErrorResponse("Only png, jpeg and gif image types are supported");
        }
        MediaType mediaType = getMediaType(type);
        BufferedImage bufferedImage = imageGenerator.createBufferedImage(contents, size, correction);

        return ResponseEntity
                .ok()
                .contentType(mediaType)
                .body(bufferedImage);
    }

    private MediaType getMediaType(String type) {
        return switch (type.toLowerCase()) {
            case "png" -> MediaType.IMAGE_PNG;
            case "jpeg", "jpg" -> MediaType.IMAGE_JPEG; // Added jpg for common usage
            case "gif" -> MediaType.IMAGE_GIF;
            default -> MediaType.IMAGE_PNG; // Default case, can be changed or handled differently
        };
    }
    private ResponseEntity<Map<String, String>> getErrorResponse(String errorMessage) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", errorMessage);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
}
