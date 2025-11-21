package org.skypro.socksStock.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skypro.socksStock.model.dto.request.Operation;
import org.skypro.socksStock.model.dto.request.SocksRequestDTO;
import org.skypro.socksStock.model.dto.response.SocksResponseDTO;
import org.skypro.socksStock.security.CustomUserDetailsService;
import org.skypro.socksStock.security.JwtTokenProvider;
import org.skypro.socksStock.service.SocksStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SocksStockController.class)
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
public class SocksStockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SocksStockService socksStockServiceMock;

    @MockBean
    private JwtTokenProvider jwtTokenProviderMock;

    @MockBean
    private CustomUserDetailsService customUserDetailsServiceMock;

    private final String TEST_COLOR = "red";
    private final Integer TEST_COTTON_PART = 80;
    private final Integer TEST_QUANTITY = 100;

    @DisplayName("Приход носков на склад - должен успешно добавить носки и вернуть статус 201")
    @Test
    void incomeSocksWhenValidRequestCreateSocksAndReturnCreated() throws Exception {
        SocksRequestDTO request = createSocksRequestDTO(TEST_COLOR, TEST_COTTON_PART, TEST_QUANTITY);
        SocksResponseDTO response = createSocksResponseDTO(TEST_COLOR, TEST_COTTON_PART, TEST_QUANTITY);

        when(socksStockServiceMock.incomeSocks(any(SocksRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/socks/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.color").value(TEST_COLOR))
                .andExpect(jsonPath("$.cottonPart").value(TEST_COTTON_PART))
                .andExpect(jsonPath("$.quantity").value(TEST_QUANTITY));
    }

    @DisplayName("Расход носков со склада - должен успешно списать носки и вернуть статус 200")
    @Test
    void outcomeSocksWhenValidRequestRemoveSocksAndReturnOk() throws Exception {
        SocksRequestDTO request = createSocksRequestDTO(TEST_COLOR, TEST_COTTON_PART, 50);
        SocksResponseDTO response = createSocksResponseDTO(TEST_COLOR, TEST_COTTON_PART, 50);

        when(socksStockServiceMock.outcomeSocks(any(SocksRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/socks/outcome")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value(TEST_COLOR))
                .andExpect(jsonPath("$.cottonPart").value(TEST_COTTON_PART))
                .andExpect(jsonPath("$.quantity").value(50));
    }

    @DisplayName("Получение количества носков по критериям - должен вернуть корректное количество")
    @Test
    void getQuantityWhenValidParametersReturnCorrectCount() throws Exception {
        Operation operation = Operation.moreThan;
        Integer expectedQuantity = 150;

        when(socksStockServiceMock.getQuantity(eq(TEST_COLOR), eq(operation), eq(TEST_COTTON_PART)))
                .thenReturn(expectedQuantity);

        mockMvc.perform(get("/api/socks")
                        .param("color", TEST_COLOR)
                        .param("operation", operation.name())
                        .param("cottonPart", TEST_COTTON_PART.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedQuantity.toString()));
    }

    @DisplayName("Получение количества носков с операцией equal - должен вернуть количество")
    @Test
    void getQuantityWithEqualOperationReturnCorrectCount() throws Exception {
        Operation operation = Operation.equal;
        Integer expectedQuantity = 75;

        when(socksStockServiceMock.getQuantity(eq(TEST_COLOR), eq(operation), eq(TEST_COTTON_PART)))
                .thenReturn(expectedQuantity);

        mockMvc.perform(get("/api/socks")
                        .param("color", TEST_COLOR)
                        .param("operation", operation.name())
                        .param("cottonPart", TEST_COTTON_PART.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedQuantity.toString()));
    }

    @DisplayName("Получение количества носков с операцией lessThan - должен вернуть количество")
    @Test
    void getQuantityWithLessThanOperationReturnCorrectCount() throws Exception {
        Operation operation = Operation.lessThan;
        Integer expectedQuantity = 25;

        when(socksStockServiceMock.getQuantity(eq(TEST_COLOR), eq(operation), eq(TEST_COTTON_PART)))
                .thenReturn(expectedQuantity);

        mockMvc.perform(get("/api/socks")
                        .param("color", TEST_COLOR)
                        .param("operation", operation.name())
                        .param("cottonPart", TEST_COTTON_PART.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedQuantity.toString()));
    }

    @DisplayName("Получение количества носков с отсутствующими параметрами - должен вернуть ошибку")
    @Test
    void getQuantityWhenMissingParametersReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/socks")
                        .param("color", TEST_COLOR)
                        .param("operation", Operation.moreThan.name()))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Получение количества носков с некорректной операцией - должен вернуть ошибку")
    @Test
    void getQuantityWhenInvalidOperationReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/socks")
                        .param("color", TEST_COLOR)
                        .param("operation", "invalidOperation")
                        .param("cottonPart", TEST_COTTON_PART.toString()))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Удаление всех носков - должен успешно выполнить операцию")
    @Test
    void allDeleteRemoveAllSocks() throws Exception {
        doNothing().when(socksStockServiceMock).deleteAll();

        mockMvc.perform(delete("/api/socks/delete"))
                .andExpect(status().isOk());

        verify(socksStockServiceMock, times(1)).deleteAll();
    }

    private SocksRequestDTO createSocksRequestDTO(String color, Integer cottonPart, Integer quantity) {
        SocksRequestDTO dto = new SocksRequestDTO();
        dto.setColor(color);
        dto.setCottonPart(cottonPart);
        dto.setQuantity(quantity);
        return dto;
    }

    private SocksResponseDTO createSocksResponseDTO(String color, Integer cottonPart, Integer quantity) {
        SocksResponseDTO dto = new SocksResponseDTO();
        dto.setColor(color);
        dto.setCottonPart(cottonPart);
        dto.setQuantity(quantity);
        return dto;
    }
}