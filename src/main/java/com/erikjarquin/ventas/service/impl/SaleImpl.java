package com.erikjarquin.ventas.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.erikjarquin.ventas.repository.PaymentRepository;
import com.erikjarquin.ventas.repository.ProductRepository;
import com.erikjarquin.ventas.repository.SaleRepository;
import com.erikjarquin.ventas.service.PaymentService;
import com.erikjarquin.ventas.service.SaleService;

@Service
@Transactional
public class SaleImpl implements SaleService {
    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final CashRegisterRepository cashRepository;
    private final SaleMapper mapper;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    public SaleImpl(
        ProductRepository productRepository,
        SaleRepository saleRepository,
        CashRegisterRepository cashRepository,
        SaleMapper mapper,
        PaymentService paymentService,
        PaymentRepository paymentRepository
    ){
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
        this.cashRepository = cashRepository;
        this.mapper = mapper;
        this.paymentService=paymentService;
        this.paymentRepository=paymentRepository;
    }

    @Override 
    public SaleResponse processSale(SaleRequest request){
        
        /* 1. VALIDAR SOLICITUD */
        if(request == null){
            throw new RuntimeException("La solicitud de venta es obligatoria");
        }

        if(request.getPaymentMethod() == null){
            throw new RuntimeException("Debe seleccionar un método de pago");
        }
        
        /* 2. VALIDAR CAJA ABIERTA */
        CashRegisterEntity cash = cashRepository.findByActiveTrue().orElseThrow(() -> 
            new RuntimeException("No existe una caja abierta"));

        /* 3. VALIDAR PRODUCTOS*/
        if(request.getItems() == null || request.getItems().isEmpty()){
            throw new RuntimeException("La venta no tiene productos");
        }

        /* 4. CREAR VENTA */
        SaleEntity sale = new SaleEntity();
        sale.setSaleDate(LocalDateTime.now());
        sale.setPaymentMethod(request.getPaymentMethod());
        sale.setCashRegister(cash);
        sale.setPaymentStatus(PaymentStatus.PENDING);

        List<SaleDetailEntity> details = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        /* 5. PROCESAR PRODUCTOS*/
        for(SaleItemRequest item : request.getItems()){
            if(item == null){
                throw new RuntimeException("La venta contiene un producto inválido");
            }

            if(item.getProductId() == null){
                throw new RuntimeException("El producto es obligatorio");
            }

            if(item.getQuantity() == null || item.getQuantity() <= 0){
                throw new RuntimeException("La cantidad del producto debe ser mayor a 0");
            }
            
            ProductEntity product = productRepository.findById(item.getProductId()).orElseThrow(() -> 
            new RuntimeException("Producto no encontrado: " + item.getProductId()));

            /* 6. Validar que haya stock*/
            if(product.getStock() < item.getQuantity()){
                throw new RuntimeException("Stock insuficiente: " + product.getName());
            }

            /* 7. CALCULAR SUBTOTAL*/
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(subtotal);

            /* 8. DESCONTAR STOCK*/
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
            
            /* 9. CREAR DETALLE*/
            SaleDetailEntity detail = new SaleDetailEntity();
            detail.setSale(sale);
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(product.getPrice());
            detail.setSubTotal(subtotal);
            details.add(detail);
        }

        /* 10. Guardar total en la venta*/
        sale.setTotal(total);
        sale.setDetails(details);

        /* 11. Procesar pago según método*/
        if(request.getPaymentMethod() == PaymentMethod.CASH){
            // ==== Pago en efectivo ====
            if(request.getCashReceived() == null){
                throw new RuntimeException("Debe indicar el efectivo recibido");
            }

            if(request.getCashReceived().compareTo(BigDecimal.ZERO) <= 0){
                throw new RuntimeException("El efectivo recibido debe ser mayor a cero");
            }

            if(request.getCashReceived().compareTo(total) < 0){
                throw new RuntimeException("Pago insuficiente");
            }

            // ==== Calcular cambio ====
            BigDecimal change = request.getCashReceived().subtract(total);
            sale.setCashReceived(request.getCashReceived());
            sale.setChangeAmount(change);
            sale.setPaymentStatus(PaymentStatus.APPROVED); //Pagado en efectivo

        } else if(request.getPaymentMethod() == PaymentMethod.DEBIT ||
                    request.getPaymentMethod() == PaymentMethod.CREDIT){
                // ==== Pago con tarjeta ====
                //Validar que vengan datos de tarjeta
                if(request.getCardPayment() == null){
                    throw new RuntimeException("Debe proporcionar datos de la tarjeta");
                }

                //Guardar la venta primero (necesaria para el pago)
                SaleEntity savedSale = saleRepository.save(sale);

                try{
                    //Preparar request para PaymentService
                    CardPaymentRequest cardRequest = request.getCardPayment();
                    cardRequest.setSaleId(savedSale.getId());
                    cardRequest.setPaymentMethod(request.getPaymentMethod());

                    //Procesar el pago con tarjeta
                    CardPaymentResponse paymentResponse = paymentService.processCardPayment(cardRequest);

                    //Actualizar el estado según la respuesta
                    if(paymentResponse.getStatus() == PaymentStatus.APPROVED){
                        savedSale.setPaymentStatus(PaymentStatus.APPROVED);
                        savedSale.setCashReceived(null);
                        savedSale.setChangeAmount(null);

                        // ← NUEVO: Asociar el pago con la venta
                        // Necesitas obtener el PaymentEntity que se creó en PaymentService
                        // Opción: Hacer que PaymentService retorne el PaymentEntity o buscarlo después
                         paymentRepository.findBySaleId(savedSale.getId()).ifPresent(savedSale::setPayment);
                        
                        //Aquí podrías guardar más información de la transacción en la venta si lo deseas
                    } else {
                        savedSale.setPaymentStatus(PaymentStatus.REJECTED);
                        throw new RuntimeException("Pago con tarjeta rechazado: " + paymentResponse.getMessage());
                    }

                    //Actualizar la venta con el estado final
                    sale = saleRepository.save(savedSale);
                } catch (Exception e){
                    // Si falla el pago, revertir el stock
                    for(SaleDetailEntity detail : details){
                        ProductEntity product = detail.getProduct();
                        product.setStock(product.getStock() + detail.getQuantity());
                        productRepository.save(product);
                    }
                    throw new RuntimeException("Error al procesar pago con tarjeta: " + e.getMessage());
                }
        } else {
            throw new RuntimeException("Método de pago no soportado");
        }

        /* 12. Guardar la venta final*/
        SaleEntity finalSale = saleRepository.save(sale);

        /* 13. Resultado*/
        return mapper.toResponse(finalSale);
    }

    @Override
    public List<SaleHistoryResponse> getSales(){
        return saleRepository.findAll().stream().map(mapper::toHistoryResponse).toList();
    }

    @Override
    public SaleDetailHistoryResponse getSaleById(Long saleId){
        SaleEntity sale = saleRepository.findById(saleId).orElseThrow(
            () -> new RuntimeException("Venta no encontrada"));

        return mapper.toDetailResponse(sale);
    }
}
