package com.sigap.readings.listener;


import com.sigap.readings.client.billing.BillingClient;
import com.sigap.readings.dto.CreateWaterBillFromReadingRequest;
import com.sigap.readings.event.MeterReadingCreatedEvent;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
@Slf4j
public class MeterReadingBillingListener {

    private final BillingClient billingClient;

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void createBillAfterReadingCreated(MeterReadingCreatedEvent event) {

        var reading = event.reading();
        var assignment = event.assignment();
        try{

            log.info(
                    "Creando factura automatica para lectura. readingId={}, meterId={}, period={}",
                    reading.getReadingId(),
                    reading.getMeterId(),
                    reading.getPeriod()
            );

            CreateWaterBillFromReadingRequest request = new CreateWaterBillFromReadingRequest(
                    reading.getReadingId(),
                    reading.getMeterId(),
                    reading.getAssignmentId(),
                    reading.getPartnerId(),
                    reading.getPeriod(),
                    assignment.identificacionSocio(),
                    null,
                    assignment.numeroMedidor(),
                    reading.getCalculatedConsumption(),
                    reading.getReadingDate(),
                    reading.getObservation()
            );

            billingClient.createFromReading(request);

        } catch (FeignException.Conflict e) {
            log.warn(
                    "La factura ya existe para la lectura. readingId={}",
                    reading.getReadingId()
            );
        } catch (FeignException ex) {
            log.error(
                    "No se pudo crear la factura automaticamente. readingId={}, status={}",
                    reading.getReadingId(),
                    ex.status()
            );
        }catch (Exception ex){
            log.error(
                    "Error inesperado al crear factura automaticamente. readingId={}",
                    reading.getReadingId(),
                    ex
            );
        }

    }
}
