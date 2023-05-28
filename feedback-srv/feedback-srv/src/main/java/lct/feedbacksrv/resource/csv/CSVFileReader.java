package lct.feedbacksrv.resource.csv;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lct.feedbacksrv.csvTemplates.MessageTemplate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.mozilla.universalchardet.UniversalDetector;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CSVFileReader {

    public static Pair<List<MessageTemplate>, List<String[]>> decode(MultipartFile file) {
        try {
            UniversalDetector detector = new UniversalDetector(null);
            byte[] buf = new byte[4096];
            int bytesToRead;
            final InputStream inputStream = file.getInputStream();
            while ((bytesToRead = inputStream.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, bytesToRead);
            }
            detector.dataEnd();
            String encoding = detector.getDetectedCharset();
            Charset charset;
            if (StringUtils.isNotEmpty(encoding)) {
                if (encoding.equalsIgnoreCase(StandardCharsets.UTF_8.toString())) {
                    charset = StandardCharsets.UTF_8;
                } else if (encoding.equalsIgnoreCase(Charsets.WINDOWS_1251.getName())) {
                    charset = Charset.forName(Charsets.WINDOWS_1251.getName());
                } else if (encoding.equalsIgnoreCase(Charsets.WINDOWS_1252.getName())) {
                    charset = Charset.forName(Charsets.WINDOWS_1252.getName());
                } else {
                    throw new IllegalArgumentException("Not supported charset: " + encoding + ", supported only 'UTF-8 BOM', 'Windows-1251' or 'Windows-1252'");
                }
            } else {
                charset = StandardCharsets.UTF_8;
                log.info("Empty charset, used 'UTF-8 BOM' as default");
            }
            Reader reader = new InputStreamReader(new BOMInputStream(file.getInputStream()), charset);
            try {
                HeaderColumnNameMappingStrategy<MessageTemplate> mappingStrategy = new HeaderColumnNameMappingStrategy();
                mappingStrategy.setType(MessageTemplate.class);

                final CsvToBean<MessageTemplate> csvToBean = new CsvToBeanBuilder<MessageTemplate>(reader)
                        .withType(MessageTemplate.class)
                        .withIgnoreLeadingWhiteSpace(true)
                        .withFieldAsNull(CSVReaderNullFieldIndicator.BOTH)
                        .withThrowExceptions(false)
                        .withMappingStrategy(mappingStrategy)
                        .withSeparator(';')
                        .build();

                final List<MessageTemplate> messages = csvToBean.parse();
                validateHeader(Arrays.asList(mappingStrategy.generateHeader(null)));

                List<String[]> errors = new ArrayList<>();
                csvToBean.getCapturedExceptions().forEach(exception -> {
                    List<String> gwData = new ArrayList<>(Arrays.asList(exception.getLine()));
                    gwData.add(exception.getMessage());
                    errors.add(gwData.toArray(new String[0]));
                });

                return Pair.of(messages, errors);
            } catch (Exception exc) {
                log.error("Invalid csv file", exc);
                throw new IllegalArgumentException("Invalid CSV file, please check number of data fields, number of header fields and their names");
            } finally {
                reader.close();
                inputStream.close();
            }
        } catch (IOException e) {
            log.error("IO error occurred while processing the CSV file", e);
            throw new IllegalArgumentException("IO error occurred while processing the CSV file");
        }
    }

    public static List<String> getValidHeader() throws CsvRequiredFieldEmptyException {
        final HeaderColumnNameMappingStrategy<MessageTemplate> mappingStrategy = new HeaderColumnNameMappingStrategy<>();
        mappingStrategy.setType(MessageTemplate.class);
        return Arrays.asList(mappingStrategy.generateHeader(null));
    }

    public static void validateHeader(List<String> actualHeader) throws CsvRequiredFieldEmptyException {
        List<String> sortedActualLowerCaseHeader = new ArrayList<>(actualHeader).stream()
                .map(String::toLowerCase)
                .sorted()
                .collect(Collectors.toList());
        log.info("{}", getValidHeader());
        if(!sortedActualLowerCaseHeader.equals(getValidHeader())){
//            throw new IllegalArgumentException("Invalid header");
        }
    }
}
