package lct.feedbacksrv.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Add class description
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
@Slf4j
@RestController
@ApiIgnore
public class RootController extends MainController{

    private static final String LAYOUT = "navigation";

    public RootController() {
        super(LAYOUT);
    }

    @GetMapping
    @RequestMapping( {"/", ""})
    public ModelAndView getStartPage() {
        return new ModelAndView("index", getMap("navigation", "main"));
    }


    private Map<String, Object> getMap(String layout, String content) {

        Map<String, Object> map = new HashMap<>();
        map.put("layout", layout);

        Map<String, Object> data = new HashMap<>();
        data.put("content", content);

        map.put("data", data);

        return map;
    }
}
