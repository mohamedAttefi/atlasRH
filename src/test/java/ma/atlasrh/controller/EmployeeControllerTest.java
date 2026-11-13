package ma.atlasrh.controller;

import ma.atlasrh.dto.EmployeeResponse;
import ma.atlasrh.exception.ConflictException;
import ma.atlasrh.exception.ResourceNotFoundException;
import ma.atlasrh.entity.Employee;
import ma.atlasrh.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean EmployeeService service;

    @Test
    void shouldCreateEmployeeAndReturnJson() throws Exception {
        when(service.create(any())).thenReturn(response());

        mvc.perform(post("/api/employees").contentType("application/json")
                        .content("""
                                {"matricule":"AT-10","firstName":"Amina","lastName":"Test",
                                "email":"a@example.test","position":"Analyste","hireDate":"2024-01-02",
                                "departmentId":1,"managerId":null}
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.matricule").value("AT-10"))
                .andExpect(jsonPath("$.departmentName").value("RH"));
    }

    @Test
    void shouldRejectInvalidEmployeeRequest() throws Exception {
        mvc.perform(post("/api/employees").contentType("application/json")
                        .content("{\"matricule\":\"\",\"firstName\":\"A\",\"lastName\":\"B\",\"email\":\"bad\","
                                + "\"position\":\"Analyste\",\"hireDate\":\"2024-01-02\",\"departmentId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").isNotEmpty());
        verifyNoInteractions(service);
    }

    @Test
    void shouldReturnConflictForDuplicateEmployee() throws Exception {
        when(service.create(any())).thenThrow(new ConflictException("Matricule déjà utilisé"));

        mvc.perform(post("/api/employees").contentType("application/json")
                        .content("""
                                {"matricule":"AT-10","firstName":"Amina","lastName":"Test",
                                "email":"a@example.test","position":"Analyste","hireDate":"2024-01-02",
                                "departmentId":1,"managerId":null}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void shouldReturnPagedEmployeeJson() throws Exception {
        when(service.list(isNull(), eq("Amina"), any())).thenReturn(
                new PageImpl<>(List.of(response()), PageRequest.of(0, 20), 1));

        mvc.perform(get("/api/employees").param("search", "Amina"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("a@example.test"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void shouldReturnEmployeeById() throws Exception {
        Employee employee = new Employee();
        when(service.get(1L)).thenReturn(employee);
        when(service.response(employee)).thenReturn(response());

        mvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("a@example.test"));
    }

    @Test
    void shouldReturnNotFoundForUnknownEmployee() throws Exception {
        when(service.get(404L)).thenThrow(new ResourceNotFoundException("Employé introuvable"));

        mvc.perform(get("/api/employees/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    private EmployeeResponse response() {
        return new EmployeeResponse(1L, "AT-10", "Amina", "Test", "a@example.test",
                "Analyste", LocalDate.of(2024, 1, 2), 1L, "RH", null);
    }
}
