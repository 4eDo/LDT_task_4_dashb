package lct.feedbacksrv.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * TODO: Add class description
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
public interface CsvReaderService {

    Boolean decodeMessages(MultipartFile file);
}
