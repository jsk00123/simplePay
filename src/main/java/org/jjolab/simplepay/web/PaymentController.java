package org.jjolab.simplepay.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jjolab.simplepay.domain.cancel.CancelRequestDto;
import org.jjolab.simplepay.domain.cancel.CancelResponseDto;
import org.jjolab.simplepay.domain.cancel.CancelService;
import org.jjolab.simplepay.domain.common.ResponseDto;
import org.jjolab.simplepay.domain.payment.PaymentRequestDto;
import org.jjolab.simplepay.domain.payment.PaymentResponseDto;
import org.jjolab.simplepay.domain.payment.PaymentService;
import org.jjolab.simplepay.domain.search.SearchRequestDto;
import org.jjolab.simplepay.domain.search.SearchResponseDto;
import org.jjolab.simplepay.domain.search.SearchService;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class PaymentController {
    private PaymentService paymentService;
    private CancelService cancelService;
    private SearchService searchService;

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponseDto> payment(@Valid @RequestBody PaymentRequestDto paymentRequestDto, Errors errors) {
        PaymentResponseDto paymentResponseDto = new PaymentResponseDto();

        if (checkParameterValidation(errors, paymentResponseDto))
            return new ResponseEntity<>(paymentResponseDto, HttpStatus.BAD_REQUEST);

        try {
            paymentService.payment(paymentRequestDto, generateUuid(), paymentResponseDto);
        } catch (Exception e) {
            e.printStackTrace();
            paymentResponseDto.getErrorMessages().add(e.getMessage());
            return new ResponseEntity<>(paymentResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(paymentResponseDto, HttpStatus.OK);
    }

    @PostMapping("/cancel")
    public ResponseEntity<CancelResponseDto> cancel(@RequestBody @Valid CancelRequestDto cancelRequestDto, Errors errors) {
        CancelResponseDto cancelResponseDto = new CancelResponseDto();

        if (checkParameterValidation(errors, cancelResponseDto))
            return new ResponseEntity<>(cancelResponseDto, HttpStatus.BAD_REQUEST);

        try {
            cancelService.cancel(cancelRequestDto, generateUuid(), cancelResponseDto);
        } catch (Exception e) {
            e.printStackTrace();
            cancelResponseDto.getErrorMessages().add(e.getMessage());
            return new ResponseEntity<>(cancelResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(cancelResponseDto, HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<SearchResponseDto> searchPaymentInfo(@RequestBody @Valid SearchRequestDto searchRequestDto, Errors errors) {
        SearchResponseDto searchResponseDto = new SearchResponseDto();

        if (checkParameterValidation(errors, searchResponseDto))
            return new ResponseEntity<>(searchResponseDto, HttpStatus.BAD_REQUEST);

        try {
            searchService.searchPaymentInfo(searchRequestDto, searchResponseDto);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(searchResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(searchResponseDto, HttpStatus.OK);
    }

    private boolean checkParameterValidation(Errors errors, ResponseDto responseDto) {
        if (errors.hasErrors()) {
            errors.getAllErrors().forEach(each -> System.out.println(each.getDefaultMessage()));
            responseDto.setErrorMessages(
                    errors.getAllErrors().stream()
                            .map(DefaultMessageSourceResolvable::getDefaultMessage)
                            .collect(Collectors.toList())
            );
            return true;
        }
        return false;
    }

    private String generateUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20);
    }
}
