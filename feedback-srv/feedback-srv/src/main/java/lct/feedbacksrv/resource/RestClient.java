package lct.feedbacksrv.resource;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * REST client
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */

@Slf4j
public class RestClient {
    private static final String TIMED_OUT_MESSAGE = "Timed out on request {} {} requestId={}";
    private static final String SUCCESS_RESPONSE = "Got response for requestId={}, total process time {}ms";
    private static final String ERROR_MESSAGE = "Что-то пошло не так, обратитесь в службу поддержки";

    private CloseableHttpClient httpClient;

    private String host;
    private Integer port;
    private boolean secure;

    public RestClient(String host) {
        init(host);
    }

    public RestClient(String host, int port) {
        init(host, port);
    }

    public RestClient(String host, int port, boolean secure) {
        init(host, port, secure);
    }
    private void init(String host) {
        init(host, null, false);
    }

    private void init(String host, Integer port) {
        init(host, port, false);
    }

    private void init(String host, Integer port, boolean secure) {
        httpClient = HttpClients.createDefault();
        this.host = host;
        this.port = port;
        this.secure = secure;
    }

    public RestResponse get(String path, Map<String, String> params, Map<String, String> headers) {
        HttpGet request = new HttpGet(buildUri(path, params));
        buildHeaders(request, headers);
        return execute(request);
    }

    public InputStream getFileStream(String path, Map<String, String> params, Map<String, String> headers) {
        HttpGet request = new HttpGet(buildUri(path, params));
        buildHeaders(request, headers);
        return executeFileStream(request);
    }

    public RestResponse post(String path, Map<String, String> params, Map<String, String> headers) {
        HttpPost request = new HttpPost(buildUri(path, params));
        buildHeaders(request, headers);
        return execute(request);
    }

    public RestResponse delete(String path, Map<String, String> params, Map<String, String> headers) {
        HttpDelete request = new HttpDelete(buildUri(path, params));
        buildHeaders(request, headers);
        return execute(request);
    }

    private RestResponse execute(HttpUriRequest request) {
        String requestId = generateId();
        log.info("requestId={}, {} {}", requestId, request.getMethod(), request.getURI());
        RestResponse.RestResponseBuilder restResponse = RestResponse.builder();
        long startExecute = System.currentTimeMillis();
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getStatusLine().getStatusCode() >= HttpStatus.SC_BAD_REQUEST) {
                restResponse.status(org.springframework.http.HttpStatus.NOT_FOUND.value());
                restResponse.data(response.getStatusLine().getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR ? ERROR_MESSAGE : EntityUtils.toString(response.getEntity()));
                restResponse.description(response.getStatusLine().getReasonPhrase());
            } else {
                String data = Objects.nonNull(response.getEntity()) ? EntityUtils.toString(response.getEntity()) : "";
                restResponse.data(data);
                restResponse.status(org.springframework.http.HttpStatus.OK.value());
            }
        } catch (SocketTimeoutException exc) {
            log.error(TIMED_OUT_MESSAGE, request.getMethod(), request.getURI(), requestId);
            restResponse.status(org.springframework.http.HttpStatus.BAD_REQUEST.value());
            restResponse.description(exc.getMessage());
        } catch (IOException ioe) {
            restResponse.status(org.springframework.http.HttpStatus.BAD_REQUEST.value());
            restResponse.description(ioe.getMessage());
        } catch (Exception e) {
            log.error("headers = {},\n URI = {}\n {}", request.getAllHeaders(), request.getURI(), e);
            restResponse.status(org.springframework.http.HttpStatus.BAD_REQUEST.value());
            restResponse.description(e.getMessage());
        }
        if (log.isDebugEnabled()) {
            log.debug(SUCCESS_RESPONSE, requestId, System.currentTimeMillis() - startExecute);
        }
        return restResponse.build();
    }
    private InputStream executeFileStream(HttpUriRequest request) {
        String requestId = generateId();
        log.info("requestId={}, {} {}", requestId, request.getMethod(), request.getURI());
        try  {
            CloseableHttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() >= HttpStatus.SC_BAD_REQUEST) {
                return null;
            } else {
                return response.getEntity().getContent();
            }
        } catch (SocketTimeoutException exc) {
            log.error(TIMED_OUT_MESSAGE, request.getMethod(), request.getURI(), requestId);
            return null;
        } catch (IOException ioe) {
            return null;
        } catch (Exception e) {
            log.error("headers = {},\n URI = {}\n {}", request.getAllHeaders(), request.getURI(), e);
            return null;
        }
    }

    private URI buildUri(String path, Map<String, String> params) {
        try {
            URIBuilder uriBuilder = new URIBuilder()
                    .setScheme(secure ? "https" : "http")
                    .setHost(host)
                    .setPath(path);
            if (port != null) {
                uriBuilder.setPort(port);
            }
            if(params != null && !params.isEmpty()){
                for (Map.Entry<String, String> pair : params.entrySet()) {
                    uriBuilder.addParameter(pair.getKey(), pair.getValue());
                }
            }
            return uriBuilder.build();
        } catch (URISyntaxException e) {
            log.error(String.format("Exception on build URI with path '%s'", path), e);
            throw new IllegalStateException(e);
        }
    }

    private void buildHeaders(HttpUriRequest request, Map<String, String> headers) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        if (headers.size() > 0) {
            headers.forEach(request::addHeader);
        }
    }

    public static String generateId() {
        return ((StringBuilder) ThreadLocalRandom.current().ints(48, 122).filter((i) -> {
            return (i < 57 || i > 65) && (i < 90 || i > 97);
        }).mapToObj((i) -> {
            return (char)i;
        }).limit(8L).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)).toString();
    }
}
