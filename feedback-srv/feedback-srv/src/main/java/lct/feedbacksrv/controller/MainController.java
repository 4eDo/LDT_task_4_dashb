package lct.feedbacksrv.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Add class description
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */

@Slf4j
@Getter
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
abstract class MainController extends CommonController {

    protected final String layout;

    @Value("${spring.http.multipart.max-file-size}")
    private String maxFileSize;

    protected ModelAndView render(Map<String, Object> data) {
        return render("index", data);
    }

    protected ModelAndView render(String view, Map<String, Object> data) {

        Map<String, Object> map = new HashMap<>();
        map.put("layout", layout);
        map.put("maxFileSize", maxFileSize);
        map.put("data", data);

        return new ModelAndView(view, map);
    }

    protected Map<String, Object> getHeaderMap() {
        Map<String, Object> data = new HashMap<>();

        return data;
    }
}
