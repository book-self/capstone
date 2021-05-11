package xyz.bookself.controllers.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import xyz.bookself.users.domain.BookList;
import xyz.bookself.users.domain.BookListEnum;
import xyz.bookself.users.repository.BookListRepository;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookListControllerTest {

    private final String apiPrefix = "/v1/book-lists";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookListRepository bookListRepository;

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));


    @Test
    void givenBookListExists_whenIdIsSuppliedToBookListEndpoint_thenABookListIsReturned()
            throws Exception {
        final String validBookListId = "99999999999";
        final BookList bookListThatExistsInDatabase = new BookList();
        bookListThatExistsInDatabase.setId(validBookListId);

        when(bookListRepository.findById(validBookListId)).thenReturn(Optional.of(bookListThatExistsInDatabase));
        mockMvc.perform(get(apiPrefix + "/" + validBookListId))
                .andExpect(status().isOk());
    }

    @Test
    void givenUserId_userListsShouldBeReturned()
            throws Exception
    {
        final Integer givenUserId = 99;
        final Collection<BookList> fourBookLists = IntStream.range(0, 4).mapToObj(i -> {
            BookList b = new BookList();
            b.setId(Integer.toHexString(i));
            b.setUserId(givenUserId);
            return b;
        }).collect(Collectors.toSet());

        when(bookListRepository.findUserBookLists(givenUserId)).thenReturn(fourBookLists);

        mockMvc.perform(get(apiPrefix + "/get-user-book-lists?userId="+givenUserId))
                .andExpect(status().isOk());
    }

    @Test
    void givenANewUserIsCreated_whenRoutedAtLeastOneBookListIsCreated_thenBookListIsReturned()
        throws Exception{

        final UserIdDTO userIdDTO = new UserIdDTO();
        userIdDTO.setUserId(1);
        when((bookListRepository.findUserBookLists(userIdDTO.getUserId()))).thenReturn(null);

        final BookList newDNF = new BookList();
        newDNF.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 24));
        newDNF.setListType(BookListEnum.DNF);
        when(bookListRepository.save(newDNF)).thenReturn(newDNF);

        String requestJson= TestUtilities.toJsonString(userIdDTO);

        mockMvc.perform(post(apiPrefix + "/" + "new-book-lists").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    void userIsAlreadyCreate_whenUserTriesToCreateMoreCannedLists_thenTheyAreRejected()
            throws Exception
    {
        final UserIdDTO userIdDTO = new UserIdDTO();
        userIdDTO.setUserId(1);
        String requestJson= TestUtilities.toJsonString(userIdDTO);

        final Collection<BookList> fourBookLists = IntStream.range(0, 4).mapToObj(i -> {
            BookList b = new BookList();
            b.setId(Integer.toHexString(i));
            b.setUserId(1);
            return b;
        }).collect(Collectors.toSet());

        when((bookListRepository.findUserBookLists(userIdDTO.getUserId()))).thenReturn(fourBookLists);
        mockMvc.perform(post(apiPrefix + "/" + "new-book-lists").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().isCreated());

    }
    @Test
    void addBookToExistingList()
            throws Exception
    {
        BookList newDNF = new BookList();
        Set<String> booksInList = newDNF.getBooks();
        newDNF.setBooks(booksInList);

        newDNF.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 24));
        newDNF.setListType(BookListEnum.DNF);
        when(bookListRepository.save(newDNF)).thenReturn(newDNF);
        bookListRepository.save(newDNF);

        final BookIdListIdDTO bookIdListIdDTO = new BookIdListIdDTO();
        bookIdListIdDTO.setBookId("123");
        bookIdListIdDTO.setListId(newDNF.getId());

        when((bookListRepository.findById(bookIdListIdDTO.getListId()))).thenReturn(Optional.of(newDNF));

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(bookIdListIdDTO);

        mockMvc.perform(post(apiPrefix + "/add-book-to-list").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().isOk());


    }

}
