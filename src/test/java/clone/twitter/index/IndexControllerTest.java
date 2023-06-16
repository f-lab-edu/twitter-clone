package clone.twitter.index;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import clone.twitter.common.BaseControllerTest;
import org.junit.jupiter.api.Test;

public class IndexControllerTest extends BaseControllerTest {
    @Test
    void index() throws Exception {
        this.mockMvc.perform(get("/tweets"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("_links.timeline-tweets").exists());
    }
}
