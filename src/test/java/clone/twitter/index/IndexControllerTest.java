package clone.twitter.index;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import clone.twitter.common.RestDocsConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

//@ExtendWith(RestDocumentationExtension.class) // according to Spring Rest Docs official documentation
@ActiveProfiles("test")
@Import(RestDocsConfiguration.class) // for http body json formatting customization
@AutoConfigureMockMvc // according to lecture reference
@AutoConfigureRestDocs // according to lecture reference. Junit5에서는 작동하지 않는다?
@Transactional
@SpringBootTest // according to lecture reference
public class IndexControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void index() throws Exception {
        this.mockMvc.perform(get("/tweets"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("_links.timeline-tweets").exists());
    }
}
