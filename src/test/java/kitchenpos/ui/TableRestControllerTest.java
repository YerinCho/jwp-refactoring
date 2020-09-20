package kitchenpos.ui;

import static kitchenpos.domain.DomainCreator.*;
import static org.hamcrest.core.StringContains.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.TableService;
import kitchenpos.domain.OrderTable;

@WebMvcTest(TableRestController.class)
class TableRestControllerTest {
    private final String BASE_URL = "/api/tables";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private TableService tableService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .addFilter(new CharacterEncodingFilter("UTF-8", true))
            .build();
    }

    @Test
    void create() throws Exception {
        OrderTable orderTable = createOrderTable(false);
        orderTable.setId(1L);
        String body = objectMapper.writeValueAsString(orderTable);

        given(tableService.create(any())).willReturn(orderTable);

        mockMvc.perform(post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(body)
        )
            .andDo(print())
            .andExpect(content().string(containsString("1")))
            .andExpect(content().string(containsString("false")))
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("orderTable의 목록을 불러올 수 있어야 한다.")
    void list() throws Exception {
        OrderTable orderTable1 = createOrderTable(false);
        orderTable1.setId(1L);
        OrderTable orderTable2 = createOrderTable(false);
        orderTable2.setId(2L);

        given(tableService.list()).willReturn(Arrays.asList(orderTable1, orderTable2));

        mockMvc.perform(get(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(content().string(containsString("1")))
            .andExpect(content().string(containsString("2")))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    void changeEmpty() throws Exception {
        OrderTable orderTable = createOrderTable(false);
        orderTable.setId(1L);
        OrderTable orderTableToChange = createOrderTable(true);

        String body = objectMapper.writeValueAsString(orderTable);

        given(tableService.changeEmpty(orderTable.getId(), orderTable)).willReturn(orderTableToChange);

        mockMvc.perform(put(BASE_URL + "/1/empty")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(body)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("true")));
    }

    @Test
    void changeNumberOfGuests() throws Exception {
        OrderTable orderTable = createOrderTable(false);
        orderTable.setId(1L);
        OrderTable orderTableToChange = createOrderTable(false);
        orderTableToChange.setNumberOfGuests(3);

        String body = objectMapper.writeValueAsString(orderTable);

        given(tableService.changeNumberOfGuests(orderTable.getId(), orderTable)).willReturn(orderTableToChange);

        mockMvc.perform(put(BASE_URL + "/1/number-of-guests")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(body)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("3")));
    }
}
