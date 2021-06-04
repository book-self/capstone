package xyz.bookself.controllers.recommendationEngine;

import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import xyz.bookself.books.repository.BookRepository;
import xyz.bookself.books.repository.RatingRepository;
import xyz.bookself.controllers.book.RatingController;
import xyz.bookself.users.domain.BookList;
import xyz.bookself.users.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RecommendationEngineTest {

    private static final int authenticatedUserId = 1;
    private static final int unauthorizedUser = 0;
    private final String apiPrefix = "/v1/recommendations";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private RatingRepository ratingRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private BookRepository bookRepository;

    @Test
    void whenUserGoesForRecommendationByAuthor_thenTheyAreReturnedABookByAuthor()
        throws Exception
    {
        final String validBookListId = "99";
        final BookList bookListThatExistsInDatabase = new BookList();
        bookListThatExistsInDatabase.setId(validBookListId);
        mockMvc.perform(get(apiPrefix + "/" + unauthorizedUser).param("recommend-by", "author"))
                .andExpect(status().isUnauthorized());

        //when(userRepository.existsById(authenticatedUserId)).thenReturn(true);
    }

}
