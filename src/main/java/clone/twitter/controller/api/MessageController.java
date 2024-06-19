package clone.twitter.controller.api;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messages")
public class MessageController {
    @GetMapping("/")
    public void getList() {
        
    }

    @GetMapping("/{chat}")
    public void get(@PathVariable("chat") String chatId){

    }
    @GetMapping("/{chat}/{page}")
    public void getPrevMessages(@PathVariable("chat") int chatId, @PathVariable("page") int pageId){

    }
}
