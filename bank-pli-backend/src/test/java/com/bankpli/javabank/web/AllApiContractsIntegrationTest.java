package com.bankpli.javabank.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AllApiContractsIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test
    void exercisesEveryPublicApi() throws Exception {
        mvc.perform(get("/api/health")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("UP"));
        mvc.perform(get("/api/menu")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(8));

        JsonNode first = createCustomer("Grace", "Hopper", 740);
        JsonNode second = createCustomer("Alan", "Turing", 710);
        JsonNode disposable = createCustomer("Katherine", "Johnson", 590);
        long firstId = first.get("id").asLong();
        long secondId = second.get("id").asLong();
        long disposableId = disposable.get("id").asLong();

        mvc.perform(get("/api/customers/{id}", firstId)).andExpect(status().isOk()).andExpect(jsonPath("$.firstName").value("Grace"));
        mvc.perform(get("/api/customers").param("q", "Turing")).andExpect(status().isOk()).andExpect(jsonPath("$.content[0].lastName").value("Turing"));
        mvc.perform(put("/api/customers/{id}", secondId).contentType(MediaType.APPLICATION_JSON)
                .content(customerJson("Alan", "Turing", 730))).andExpect(status().isOk()).andExpect(jsonPath("$.creditScore").value(730));
        mvc.perform(post("/api/customers/{id}/manual-review", firstId).contentType(MediaType.APPLICATION_JSON)
                .content("{\"reason\":\"Compliance sample\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.manualReview").value(true));
        mvc.perform(delete("/api/customers/{id}", disposableId)).andExpect(status().isNoContent());
        mvc.perform(get("/api/customers/{id}", disposableId)).andExpect(status().isNotFound());
        mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                .content(customerJson("John3", "Smith", 700))).andExpect(status().isBadRequest());

        JsonNode source = createAccount(firstId, "CHECKING", "10000.00", "1000.00", "0.01");
        JsonNode target = createAccount(secondId, "SAVINGS", "100.00", "0.00", "0.025");
        long sourceId = source.get("id").asLong();
        long targetId = target.get("id").asLong();
        String sourceNumber = source.get("accountNumber").asText();
        String targetNumber = target.get("accountNumber").asText();
        String sourceSort = source.get("sortCode").asText();
        String targetSort = target.get("sortCode").asText();

        mvc.perform(get("/api/accounts/{id}", sourceId)).andExpect(status().isOk()).andExpect(jsonPath("$.active").value(true));
        mvc.perform(post("/api/accounts/{id}/holders/{customerId}", sourceId, secondId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.holderIds.length()").value(2));
        mvc.perform(delete("/api/accounts/{id}/holders/{customerId}", sourceId, secondId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.holderIds.length()").value(1));
        mvc.perform(get("/api/accounts/mortgage").param("principal", "100000").param("annualRate", "0.06").param("months", "360"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.payment").value(599.55));
        mvc.perform(get("/api/accounts/interest").param("balance", "10000").param("annualRate", "0.05").param("days", "30"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.accrued").value(41.10));
        mvc.perform(delete("/api/customers/{id}", firstId)).andExpect(status().isConflict());

        String transfer = transferJson(sourceNumber, sourceSort, targetNumber, targetSort, "500.00");
        mvc.perform(post("/api/transfers/fee").contentType(MediaType.APPLICATION_JSON).content(transfer))
                .andExpect(status().isOk()).andExpect(jsonPath("$.fee").value(5.00));
        mvc.perform(post("/api/transfers").contentType(MediaType.APPLICATION_JSON).content(transfer))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.sourceBalance").value(9495.00)).andExpect(jsonPath("$.targetBalance").value(600.00));
        mvc.perform(post("/api/transfers").contentType(MediaType.APPLICATION_JSON)
                .content(transferJson(sourceNumber, sourceSort, sourceNumber, sourceSort, "10.00")))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/api/credit/score").contentType(MediaType.APPLICATION_JSON)
                .content("{\"bureauScores\":[720,700,680,710,690],\"history\":[650,655,660,665,670,675,680,685,690,695,700,705]}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.responses").value(3));
        mvc.perform(get("/api/credit/decision/720")).andExpect(status().isOk()).andExpect(jsonPath("$.decision").value("APPROVE"));
        mvc.perform(get("/api/credit/decision/650")).andExpect(status().isOk()).andExpect(jsonPath("$.decision").value("CONDITIONAL_REVIEW"));
        mvc.perform(get("/api/credit/decision/649")).andExpect(status().isOk()).andExpect(jsonPath("$.decision").value("DENY"));

        mvc.perform(get("/api/swift/bic/DEUTDEFF")).andExpect(status().isOk()).andExpect(jsonPath("$.valid").value(true));
        mvc.perform(get("/api/swift/bic/DE1TDEFF")).andExpect(status().isOk()).andExpect(jsonPath("$.valid").value(false));
        mvc.perform(get("/api/swift/fx").param("amount", "100").param("from", "USD").param("to", "EUR"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.converted").value(92.00));
        mvc.perform(get("/api/swift/format").param("amount", "1234.56").param("currency", "EUR").param("locale", "fr-FR"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.formatted").exists()).andExpect(jsonPath("$.picture").exists());
        mvc.perform(post("/api/swift/payments").contentType(MediaType.APPLICATION_JSON)
                .content("{\"messageType\":\"103\",\"senderBic\":\"CHASUS33XXX\",\"receiverBic\":\"DEUTDEFFXXX\",\"amount\":50000,\"currency\":\"EUR\",\"beneficiary\":\"François Dubois SA\",\"remittance\":\"Invoice 12345\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("QUEUED"));

        mvc.perform(get("/api/audit")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
        mvc.perform(post("/api/audit/parse").contentType(MediaType.APPLICATION_JSON)
                .content("{\"line\":\"TXN1,DEBIT,25.50,Sample purchase\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.amount").value(25.50));
        mvc.perform(get("/api/audit/report")).andExpect(status().isOk()).andExpect(jsonPath("$.recordCount").value(2));

        mvc.perform(get("/api/admin/config")).andExpect(status().isOk()).andExpect(jsonPath("$['routing.max-depth']").value("10"));
        mvc.perform(put("/api/admin/config/{key}", "routing.max-depth").contentType(MediaType.APPLICATION_JSON)
                .content("{\"value\":\"12\"}")).andExpect(status().isOk()).andExpect(jsonPath("$['routing.max-depth']").value("12"));
        mvc.perform(post("/api/admin/kyc").contentType(MediaType.APPLICATION_JSON)
                .content("{\"legalName\":\"Grace Hopper\",\"documentId\":\"DOC-123456\",\"password\":\"correct-horse-battery\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.valid").value(true)).andExpect(jsonPath("$.profileHash").isNotEmpty());
        mvc.perform(post("/api/batch/generate").param("count", "3")).andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(3)).andExpect(jsonPath("$.total").value(375.00));
        mvc.perform(post("/api/accounts/{id}/deactivate", targetId)).andExpect(status().isOk()).andExpect(jsonPath("$.active").value(false));
    }

    private JsonNode createCustomer(String first, String last, int score) throws Exception {
        String body = mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                .content(customerJson(first, last, score))).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(body);
    }

    private String customerJson(String first, String last, int score) throws Exception {
        return mapper.writeValueAsString(new CustomerPayload("Mx", first, last, first.toLowerCase()+"@bank.test", "5551234567", "1990-01-01", score));
    }

    private JsonNode createAccount(long customerId, String type, String opening, String overdraft, String rate) throws Exception {
        String body = mvc.perform(post("/api/accounts").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new AccountPayload(customerId, type, opening, overdraft, rate))))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(body);
    }

    private String transferJson(String from, String fromSort, String to, String toSort, String amount) throws Exception {
        return mapper.writeValueAsString(new TransferPayload(from, fromSort, to, toSort, amount, "STANDARD", "INTERNAL",
                LocalDate.now().toString(), false, false));
    }

    private record CustomerPayload(String title, String firstName, String lastName, String email, String phone,
                                   String dateOfBirth, int creditScore) {}
    private record AccountPayload(long customerId, String type, String openingBalance, String overdraftLimit, String interestRate) {}
    private record TransferPayload(String fromAccount, String fromSortCode, String toAccount, String toSortCode,
                                   String amount, String priority, String type, String scheduledDate,
                                   boolean externalBankVerified, boolean wireAuthorized) {}
}
