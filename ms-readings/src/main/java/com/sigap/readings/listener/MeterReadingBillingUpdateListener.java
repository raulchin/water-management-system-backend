package com.sigap.readings.listener;

import com.sigap.readings.client.billing.BillingClient;
import com.sigap.readings.dto.RecalculateWaterBillFromReadingRequest;
import com.sigap.readings.event.MeterReadingUpdatedEvent;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
@Slf4j
public class MeterReadingBillingUpdateListener {

    private final BillingClient billingClient;

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void recalculateBillAfterReadingUpdated(MeterReadingUpdatedEvent event) {
        var reading = event.reading();
        var assignment = event.assignment();

        try {
            log.info(
                    "Solicitando recalculo de factura por actualizacion de lectura. readingId={}, meterId={}, period={}",
                    reading.getReadingId(),
                    reading.getMeterId(),
                    reading.getPeriod()
            );

            RecalculateWaterBillFromReadingRequest request = new RecalculateWaterBillFromReadingRequest(
                    reading.getReadingId(),
                    reading.getMeterId(),
                    reading.getAssignmentId(),
                    reading.getPartnerId(),
                    reading.getPeriod(),
                    assignment == null ? null : assignment.identificacionSocio(),
                    null,
                    assignment == null ? null : assignment.numeroMedidor(),
                    reading.getCalculatedConsumption(),
                    reading.getObservation()
            );

            billingClient.recalculateFromReading(reading.getReadingId(), request);

            log.info(
                    "Factura recalculada correctamente desde ms-readings. readingId={}",
                    reading.getReadingId()
            );

        } catch (FeignException.BadRequest ex) {
            log.warn(
                    "No se pudo recalcular factura por regla de negocio. readingId={}, status={}",
                    reading.getReadingId(),
                    ex.status()
            );
        } catch (FeignException.NotFound ex) {
            log.warn(
                    "No existe factura asociada a la lectura actualizada. readingId={}",
                    reading.getReadingId()
            );
        } catch (FeignException ex) {
            log.error(
                    "Error Feign al recalcular factura. readingId={}, status={}",
                    reading.getReadingId(),
                    ex.status(),
                    ex
            );
        } catch (Exception ex) {
            log.error(
                    "Error inesperado al recalcular factura. readingId={}",
                    reading.getReadingId(),
                    ex
            );
        }
    }
}
