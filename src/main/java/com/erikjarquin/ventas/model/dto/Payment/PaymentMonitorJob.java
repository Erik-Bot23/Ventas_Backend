package com.erikjarquin.ventas.model.dto.Payment;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.erikjarquin.ventas.model.entity.PaymentEntity;
import com.erikjarquin.ventas.model.enums.PaymentStatus;
import com.erikjarquin.ventas.repository.PaymentRepository;
import com.erikjarquin.ventas.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//
@Slf4j //
@Component //
@RequiredArgsConstructor //
public class PaymentMonitorJob {  
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;


    //Ejecutar cada 5 minutos
    @Scheduled(fixedDelay = 300000) //
    public void monitorPendingPayments(){
        log.info("Monitoreando pagod pendientes...");

        //Pagos pendientes por más de 5 minutos
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        List<PaymentEntity> pendingPayments = paymentRepository.findByStatusAndPaymentDateBefore(PaymentStatus.PENDING, fiveMinutesAgo);

        if(pendingPayments.isEmpty()){
            log.info("No hay pagos pendientes por momitorear");
            return;
        }

        log.info("Encontrados {} pagos pendientes", pendingPayments.size());

        for(PaymentEntity payment : pendingPayments){
            try{
                log.info("Consultando estado de pago: {}", payment.getTransactionId());

                paymentService.getPaymentStatus(payment.getTransactionId());

                //Pequeña pausa para no sobrecargar
                Thread.sleep(1000);
            }catch(Exception e){
                log.error("Error monitoreando pago {}: {}", payment.getTransactionId(), e.getMessage());
            }
        }

    }
}
