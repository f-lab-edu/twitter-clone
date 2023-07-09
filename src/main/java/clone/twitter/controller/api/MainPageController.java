package clone.twitter.controller.api;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class MainPageController {
    @RequestMapping("/manifast")
    public void getMainPage() {
        /*get MainPage image Icon, PNG, JPG*/
    }

    @RequestMapping("/timeline")
    public void getTimeline() {
        /*get TimeLine Feed*/
    }
}
