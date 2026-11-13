package ma.atlasrh;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AtlasrhIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void shouldCreateEmployeeRequestAndApproveIt() throws Exception {
        long departmentId = createDepartment("Integration RH");
        long managerId = createEmployee("IT-MGR-1", "manager", departmentId, null);
        long employeeId = createEmployee("IT-EMP-1", "employee", departmentId, managerId);

        MvcResult createdRequest = mvc.perform(post("/api/leave-requests").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"employeeId":%d,"leaveTypeId":1,"startDate":"2026-12-07",
                                "endDate":"2026-12-08","reason":"Integration scenario"}
                                """.formatted(employeeId)))
                .andExpect(status().isCreated()).andReturn();
        long requestId = json(createdRequest).get("id").asLong();

        MvcResult decision = mvc.perform(post("/api/managers/{managerId}/leave-requests/{id}/decision", managerId, requestId)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"approved\":true,\"comment\":\"Validé\"}"))
                .andExpect(status().isOk()).andReturn();

        assertEquals("APPROUVEE", json(decision).get("status").asText());
    }

    @Test
    void shouldRefuseDeletingDepartmentThatHasEmployees() throws Exception {
        long departmentId = createDepartment("Occupied department");
        createEmployee("IT-EMP-2", "employee2", departmentId, null);

        mvc.perform(delete("/api/departments/{id}", departmentId))
                .andExpect(status().isConflict());
    }

    private long createDepartment(String name) throws Exception {
        MvcResult result = mvc.perform(post("/api/departments").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.createObjectNode().put("name", name).put("description", "Integration test").toString()))
                .andExpect(status().isCreated()).andReturn();
        return json(result).get("id").asLong();
    }

    private long createEmployee(String matricule, String suffix, long departmentId, Long managerId) throws Exception {
        var request = objectMapper.createObjectNode().put("matricule", matricule)
                .put("firstName", "Integration").put("lastName", suffix)
                .put("email", suffix + "@integration.test").put("position", "Tester")
                .put("hireDate", "2024-01-02").put("departmentId", departmentId);
        if (managerId == null) request.putNull("managerId"); else request.put("managerId", managerId);
        MvcResult result = mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
                        .content(request.toString()))
                .andExpect(status().isCreated()).andReturn();
        return json(result).get("id").asLong();
    }

    private JsonNode json(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
