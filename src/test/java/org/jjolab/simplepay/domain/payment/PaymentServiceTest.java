package org.jjolab.simplepay.domain.payment;

import org.jjolab.simplepay.domain.cardPostInfo.CardPostInfoRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
public class PaymentServiceTest {
    @Mock
    CardPostInfoRepository cardPostInfoRepository;
    @Mock
    private ConcurrentHashMap<String, String> paymentCardInfo;

    @InjectMocks
    PaymentService paymentService;

    PaymentRequestDto paymentRequestDtoWithVat = new PaymentRequestDto();
    PaymentRequestDto paymentRequestDtoWithoutVat = new PaymentRequestDto();

    @Before
    public void setup() {
        paymentRequestDtoWithVat.setCdno("1010101010101010");
        paymentRequestDtoWithVat.setCvc("456");
        paymentRequestDtoWithVat.setDivisionPayDate(0);
        paymentRequestDtoWithVat.setExpiredDate("0325");
        paymentRequestDtoWithVat.setPaymentAmount(1000L);
        paymentRequestDtoWithVat.setVat(100L);

        paymentRequestDtoWithoutVat.setCdno("1010101010101010");
        paymentRequestDtoWithoutVat.setCvc("456");
        paymentRequestDtoWithoutVat.setDivisionPayDate(0);
        paymentRequestDtoWithoutVat.setExpiredDate("0325");
        paymentRequestDtoWithoutVat.setPaymentAmount(1000L);
    }

    @Test
    public void paymentTest() throws Exception {
        // given
        PaymentResponseDto actual = new PaymentResponseDto();
        PaymentResponseDto expected = new PaymentResponseDto();
        expected.setUuid("eb28b5c6f3ff4288afe1");

        // when
        paymentService.payment(paymentRequestDtoWithVat, "eb28b5c6f3ff4288afe1", actual);

        // then
        Assert.assertEquals(expected.toString(), actual.toString());
        System.out.println(expected.toString());
        System.out.println(actual.toString());
    }

    @Test
    public void paymentTest2() throws Exception {
        // given
        PaymentResponseDto actual = new PaymentResponseDto();
        PaymentResponseDto expected = new PaymentResponseDto();
        expected.setUuid("eb28b5c6f3ff4288afe1");

        // when
        paymentService.payment(paymentRequestDtoWithoutVat, "eb28b5c6f3ff4288afe1", actual);

        // then
        Assert.assertEquals(expected.toString(), actual.toString());
        System.out.println(expected.toString());
        System.out.println(actual.toString());
    }
}