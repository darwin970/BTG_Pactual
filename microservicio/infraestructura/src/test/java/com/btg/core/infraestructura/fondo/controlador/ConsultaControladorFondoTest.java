package com.btg.core.infraestructura.fondo.controlador;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ConsultaControladorFondoTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void listarFondosRetorna200ConCincoFondos() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/fondos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[*].nombre", containsInAnyOrder(
                        "FPV_BTG_PACTUAL_RECAUDADORA",
                        "FPV_BTG_PACTUAL_ECOPETROL",
                        "DEUDAPRIVADA",
                        "FDO-ACCIONES",
                        "FPV_BTG_PACTUAL_DINAMICA"
                )));
    }
}
