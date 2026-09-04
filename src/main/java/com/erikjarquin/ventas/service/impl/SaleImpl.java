package com.erikjarquin.ventas.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erikjarquin.ventas.exceptions.PaymentException;
import com.erikjarquin.ventas.exceptions.SaleException;
import com.erikjarquin.ventas.mapper.SaleMapper;
import com.erikjarquin.ventas.model.dto.Payment.CardPaymentRequest;
import com.erikjarquin.ventas.model.dto.Payment.CardPaymentResponse;
import com.erikjarquin.ventas.model.dto.Sale.SaleDetailHistoryResponse;
import com.erikjarquin.ventas.model.dto.Sale.SaleHistoryResponse;
import com.erikjarquin.ventas.model.dto.Sale.SaleItemRequest;
import com.erikjarquin.ventas.model.dto.Sale.SaleRequest;
import com.erikjarquin.ventas.model.dto.Sale.SaleResponse;
import com.erikjarquin.ventas.model.entity.CashRegisterEntity;
import com.erikjarquin.ventas.model.entity.ProductEntity;
import com.erikjarquin.ventas.model.entity.SaleDetailEntity;
import com.erikjarquin.ventas.model.entity.SaleEntity;
import com.erikjarquin.ventas.model.enums.PaymentMethod;
import com.erikjarquin.ventas.model.enums.PaymentStatus;
import com.erikjarquin.ventas.repository.CashRegisterRepository;
import com.erikjarquin.ventas.repository.ProductRepository;
import com.erikjarquin.ventas.repository.SaleRepository;
import com.erikjarquin.ventas.service.PaymentService;
import com.erikjarquin.ventas.service.SaleService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SaleImpl implements SaleService {
    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final CashRegisterRepository cashRepository;
    private final SaleMapper mapper;
    private final PaymentService paymentService;

    public SaleImpl(
        ProductRepository productRepository,
        SaleRepository saleRepository,
        CashRegisterRepository cashRepository,
        SaleMapper mapper,
        PaymentService paymentService
    ){
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
        this.cashRepository = cashRepository;
        this.mapper = mapper;
        this.paymentService=paymentService;
    }

    @Override 
    @Transactional(rollbackFor = Exception.class)
    public SaleResponse processSale(SaleRequest request){
        log.info("Iniciando proceso de venta. Método de pago: {}", request.getPaymentMethod());

        /* 1. VALIDACIONES INICIALES */
        validateRequest(request);

        /* 2. VERIFICAR CAJA ABIERTA*/
        CashRegisterEntity cash = getActiveCashRegister();

        /* 3. CREAR VENTA (sin detalles todavía) */
        SaleEntity sale = createBaseSale(request, cash);

        /* 4. PROCESAR PRODUCTOS Y CALCULAR TOTAL */
        ProcessedProducts processed = processProducts(request.getItems(), sale);
        sale.setDetails(processed.getDetails());
        sale.setTotal(processed.getTotal());

        /* 5. PROCESAR PAGO SEGÚN MÉTODO */
        processPayment(request, sale);

        /* 6. GUARDAR VENTA COMPLETA */
        SaleEntity savedSale = saleRepository.save(sale);
        log.info("Venta completada exitosamente. ID: {}, Total: ${}", savedSale.getId(), savedSale.getTotal());

        /* RETORNAR RESPUESTA */
        return mapper.toResponse(savedSale);
    }

    // ====== Métodos privados =====
    private void validateRequest(SaleRequest request){
        if(request == null){
            throw new SaleException("La solicitud de venta es obligatoria");        
        }

        if(request.getPaymentMethod() == null){
            throw new SaleException("Debe seleccionar un método de pago");
        }

        if(request.getItems() == null || request.getItems().isEmpty()){
            throw new SaleException("La venta no tiene productos");
        }
    }

    private CashRegisterEntity getActiveCashRegister(){
        return cashRepository.findByActiveTrue().orElseThrow(() -> new SaleException("No existe una caja abierta"));
    }

    private SaleEntity createBaseSale(SaleRequest request, CashRegisterEntity cash){
        SaleEntity sale = new SaleEntity();
        sale.setSaleDate(LocalDateTime.now());
        sale.setPaymentMethod(request.getPaymentMethod());
        sale.setCashRegister(cash);
        sale.setPaymentStatus(PaymentStatus.PENDING);
        return sale;
    }

    private ProcessedProducts processProducts(List<SaleItemRequest> items, SaleEntity sale){
        List<SaleDetailEntity> details = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for(SaleItemRequest item : items){
            validateItem(item);

            ProductEntity product = findProduct(item.getProductId());
            validateStock(product, item.getQuantity());

            //DESCONTAR STOCK (dentro de la misma transacción)
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            BigDecimal subtotal = calculateSubtotal(product, item.getQuantity());
            total = total.add(subtotal);

            SaleDetailEntity detail = createDetail(sale, product, item, subtotal);
            details.add(detail);
        }

        return new ProcessedProducts(details, total);
    }

    private void validateItem(SaleItemRequest item){
        if(item == null){
            throw new SaleException("La venta contiene un producto inválido");
        }

        if(item.getProductId() == null){
            throw new SaleException("El producto es obligatorio");
        }

        if(item.getQuantity() == null || item.getQuantity() <= 0){
            throw new SaleException("La cantidad del producto debe ser mayor a 0");
        }
    }

    private ProductEntity findProduct(Long productId){
        return productRepository.findById(productId).orElseThrow(() -> new SaleException("Producto no encontrado: " + productId));
    }

    private void validateStock(ProductEntity product, Integer quantity){
        if(product.getStock() < quantity){
            throw new SaleException("Stock insuficiente para el producto: " + product.getName() + 
            ". Disponible: " + product.getStock() + ", Solicitado: " + quantity);
        }
    }

    private BigDecimal calculateSubtotal(ProductEntity product, Integer quantity){
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    private SaleDetailEntity createDetail(SaleEntity sale, ProductEntity product, SaleItemRequest item, BigDecimal subtotal){
        SaleDetailEntity detail = new SaleDetailEntity();
        detail.setSale(sale);
        detail.setProduct(product);
        detail.setQuantity(item.getQuantity());
        detail.setUnitPrice(product.getPrice());
        detail.setSubTotal(subtotal);
        return detail;
    }

    private void processPayment(SaleRequest request, SaleEntity sale){
        if(request.getPaymentMethod() == PaymentMethod.CASH){
            processCashPayment(request, sale);
        } else if(request.getPaymentMethod() == PaymentMethod.DEBIT || request.getPaymentMethod() == PaymentMethod.CREDIT){
            processCardPayment(request,sale);
        } else {
            throw new SaleException("Método de pago no soportado: " + request.getPaymentMethod());
        }
    }

    private void processCashPayment(SaleRequest request, SaleEntity sale){
        log.info("Procesando pago con efectivo para venta ID: {}", sale.getId());

        if(request.getCashReceived() == null){
            throw new SaleException("Debe indicar el efectivo recibido");
        }

        if(request.getCashReceived().compareTo(BigDecimal.ZERO) <= 0){
            throw new SaleException("El efectivo recibido debe ser mayor a cero");
        }

        if(request.getCashReceived().compareTo(sale.getTotal()) <0){
            throw new SaleException("Pago insuficiente. Total: $" + sale.getTotal() + ", Recibido: $" + request.getCashReceived());
        }

        BigDecimal change = request.getCashReceived().subtract(sale.getTotal());
        sale.setCashReceived(request.getCashReceived());
        sale.setChangeAmount(change);
        sale.setPaymentStatus(PaymentStatus.APPROVED);

        log.info("Pago en efectivo aprobado. Cambio: {}", change);
    }

    private void processCardPayment(SaleRequest request, SaleEntity sale){
        log.info("Procesando pago con tarjeta para venta ID: {}", sale.getId());

        //Validar datos de tarjeta
        if(request.getCardPayment() == null){
            throw new SaleException("Debe proporcionar datos de la tarjeta");
        }

        //GUARDAR LA VENTA ANTES DEL PAGO(necesaria para referencia)
        SaleEntity savedSale = saleRepository.save(sale);

        try{
            //Preparar request para PaymentService
            CardPaymentRequest cardRequest = request.getCardPayment();
            cardRequest.setSaleId(savedSale.getId());
            cardRequest.setPaymentMethod(request.getPaymentMethod());

            //Procesar el pago con tarjeta(PaymentService se encarga de la asociación bidireccional)
            CardPaymentResponse paymentResponse = paymentService.processCardPayment(cardRequest);

            //Actualizar estado según la respuesta
            if(paymentResponse.getStatus() == PaymentStatus.APPROVED){
                sale.setPaymentStatus(PaymentStatus.APPROVED);
                sale.setCashReceived(null);
                sale.setChangeAmount(null);
                log.info("Pago con tarjeta aprobado. Código: {}", paymentResponse.getAuthorizationCode());
            } else {
                sale.setPaymentStatus(PaymentStatus.REJECTED);
                log.warn("Pago con tarjeta rechazada: {}", paymentResponse.getMessage());
                throw new PaymentException("Pago con tarjeta rechazado: " + paymentResponse.getMessage());
            }
        } catch (PaymentException e){
            //Relanzamos la excepción original sin convertirla
            log.error("Error en pago con tarjeta: {}", e.getMessage());
            throw e;
        } catch (Exception e){
            log.error("Error inesperado en pago con tarjeta: {}", e.getMessage());
            //Spring hará rollback automático de toda la transacción
            //No necesitamos rollback manual
            throw new SaleException("Error al procesar pago con tarjeta: " + e.getMessage(), e);
        }
    }

    //===== CLASE AUXILIAR =====
    private static class ProcessedProducts {
        private final List<SaleDetailEntity> details;
        private final BigDecimal total;

        public ProcessedProducts(List<SaleDetailEntity> details, BigDecimal total){
            this.details=details;
            this.total=total;
        }

        public List<SaleDetailEntity> getDetails(){
            return details;
        }

        public BigDecimal getTotal(){
            return total;
        }
    }

    //===== MÉTODOS DE CONSULTA ======
    @Override
    @Transactional(readOnly = true)
    public List<SaleHistoryResponse> getSales(){
        return saleRepository.findAll().stream().map(mapper::toHistoryResponse).toList();
    }

    @Override
    public SaleDetailHistoryResponse getSaleById(Long saleId){
        SaleEntity sale = saleRepository.findById(saleId).orElseThrow(
            () -> new SaleException("Venta no encontrada con ID: " + saleId));

        return mapper.toDetailResponse(sale);
    }
}
