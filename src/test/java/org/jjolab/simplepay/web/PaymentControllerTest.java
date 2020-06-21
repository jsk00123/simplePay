package org.jjolab.simplepay.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jjolab.simplepay.domain.cancel.CancelRequestDto;
import org.jjolab.simplepay.domain.cancel.CancelResponseDto;
import org.jjolab.simplepay.domain.cancel.CancelService;
import org.jjolab.simplepay.domain.cardPostInfo.CardPostInfoRepository;
import org.jjolab.simplepay.domain.payment.PaymentRequestDto;
import org.jjolab.simplepay.domain.payment.PaymentResponseDto;
import org.jjolab.simplepay.domain.payment.PaymentService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PaymentControllerTest {
    @Autowired
    private CardPostInfoRepository cardPostInfoRepository;
    @Autowired
    @Qualifier(value = "paymentCardInfo")
    private ConcurrentHashMap<String, String> paymentCardInfo;
    @Autowired
    @Qualifier(value = "cancelPaymentInfo")
    private ConcurrentHashMap<String, String> cancelPaymentInfo;

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private CancelService cancelService;

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    private PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
    private CancelRequestDto cancelRequestDto = new CancelRequestDto();
    private CancelResponseDto cancelResponseDto = new CancelResponseDto();

    @Before
    public void setup() {
        paymentRequestDto.setCdno("1010101010101010");
        paymentRequestDto.setDivisionPayDate(0);
        paymentRequestDto.setExpiredDate("0325");
        paymentRequestDto.setCvc("456");
    }

    @Test
    public void paymentTest1() throws Exception {
        paymentRequestDto.setPaymentAmount(11000L);
        paymentRequestDto.setVat(1000L);

        PaymentResponseDto paymentResponseDto = objectMapper.readValue(mockMvc.perform(post("/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequestDto)))
                .andReturn().getResponse().getContentAsString(), PaymentResponseDto.class);

        Assert.assertTrue(paymentResponseDto.getErrorMessages().isEmpty());

        cancelRequestDto.setUuid(paymentResponseDto.getUuid());
        cancelRequestDto.setCancelAmount(1100L);
        cancelRequestDto.setVat(100L);

        cancelResponseDto = objectMapper.readValue(mockMvc.perform(post("/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelRequestDto)))
                .andReturn().getResponse().getContentAsString(), CancelResponseDto.class);

        Assert.assertTrue(cancelResponseDto.getErrorMessages().isEmpty());

        cancelRequestDto.setCancelAmount(3300L);
        cancelRequestDto.setVat(null);

        cancelResponseDto = objectMapper.readValue(mockMvc.perform(post("/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelRequestDto)))
                .andReturn().getResponse().getContentAsString(), CancelResponseDto.class);

        Assert.assertTrue(cancelResponseDto.getErrorMessages().isEmpty());

        cancelRequestDto.setCancelAmount(7000L);
        cancelRequestDto.setVat(null);

        cancelResponseDto = objectMapper.readValue(mockMvc.perform(post("/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelRequestDto)))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), CancelResponseDto.class);

        Assert.assertEquals("총 취소 금액이 결제 금액 보다 많습니다.", cancelResponseDto.getErrorMessages().get(0));

        cancelRequestDto.setCancelAmount(6600L);
        cancelRequestDto.setVat(700L);

        cancelResponseDto = objectMapper.readValue(mockMvc.perform(post("/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelRequestDto)))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), CancelResponseDto.class);

        Assert.assertEquals("총 취소 부가세 금액이 총 부가세 보다 많습니다.", cancelResponseDto.getErrorMessages().get(0));

        cancelRequestDto.setCancelAmount(6600L);
        cancelRequestDto.setVat(600L);

        cancelResponseDto = objectMapper.readValue(mockMvc.perform(post("/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelRequestDto)))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), CancelResponseDto.class);

        Assert.assertTrue(cancelResponseDto.getErrorMessages().isEmpty());

        cancelRequestDto.setCancelAmount(100L);
        cancelRequestDto.setVat(null);

        cancelResponseDto = objectMapper.readValue(mockMvc.perform(post("/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelRequestDto)))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), CancelResponseDto.class);

        Assert.assertEquals("총 취소 금액이 결제 금액 보다 많습니다.", cancelResponseDto.getErrorMessages().get(0));
    }

    @Test
    public void paymentTest2() throws Exception {
        paymentRequestDto.setPaymentAmount(20000L);
        paymentRequestDto.setVat(909L);

        PaymentResponseDto paymentResponseDto = objectMapper.readValue(mockMvc.perform(post("/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequestDto)))
                .andReturn().getResponse().getContentAsString(), PaymentResponseDto.class);

        Assert.assertTrue(paymentResponseDto.getErrorMessages().isEmpty());

        cancelRequestDto.setUuid(paymentResponseDto.getUuid());
        cancelRequestDto.setCancelAmount(10000L);
        cancelRequestDto.setVat(0L);

        cancelResponseDto = objectMapper.readValue(mockMvc.perform(post("/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelRequestDto)))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), CancelResponseDto.class);

        Assert.assertTrue(cancelResponseDto.getErrorMessages().isEmpty());

        cancelRequestDto.setCancelAmount(10000L);
        cancelRequestDto.setVat(0L);

        cancelResponseDto = objectMapper.readValue(mockMvc.perform(post("/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelRequestDto)))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), CancelResponseDto.class);

        Assert.assertEquals("취소 금액은 모두 취소 되었으나 부가세가 남았습니다.", cancelResponseDto.getErrorMessages().get(0));

        cancelRequestDto.setCancelAmount(10000L);
        cancelRequestDto.setVat(909L);

        cancelResponseDto = objectMapper.readValue(mockMvc.perform(post("/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelRequestDto)))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), CancelResponseDto.class);

        Assert.assertTrue(cancelResponseDto.getErrorMessages().isEmpty());
    }

    @Test
    public void paymentTest3() throws Exception {
        paymentRequestDto.setPaymentAmount(20000L);
        paymentRequestDto.setVat(null);

        PaymentResponseDto paymentResponseDto = objectMapper.readValue(mockMvc.perform(post("/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequestDto)))
                .andReturn().getResponse().getContentAsString(), PaymentResponseDto.class);

        Assert.assertTrue(paymentResponseDto.getErrorMessages().isEmpty());

        cancelRequestDto.setUuid(paymentResponseDto.getUuid());
        cancelRequestDto.setCancelAmount(10000L);
        cancelRequestDto.setVat(1000L);

        cancelResponseDto = objectMapper.readValue(mockMvc.perform(post("/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelRequestDto)))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), CancelResponseDto.class);

        Assert.assertTrue(cancelResponseDto.getErrorMessages().isEmpty());

        cancelRequestDto.setCancelAmount(10000L);
        cancelRequestDto.setVat(909L);

        cancelResponseDto = objectMapper.readValue(mockMvc.perform(post("/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelRequestDto)))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), CancelResponseDto.class);

        Assert.assertEquals("총 취소 부가세 금액이 총 부가세 보다 많습니다.", cancelResponseDto.getErrorMessages().get(0));

        cancelRequestDto.setCancelAmount(10000L);
        cancelRequestDto.setVat(null);

        cancelResponseDto = objectMapper.readValue(mockMvc.perform(post("/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelRequestDto)))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), CancelResponseDto.class);

        Assert.assertTrue(cancelResponseDto.getErrorMessages().isEmpty());
    }
}