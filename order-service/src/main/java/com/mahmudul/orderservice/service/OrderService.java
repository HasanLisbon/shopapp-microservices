package com.mahmudul.orderservice.service;

import com.mahmudul.orderservice.dto.InventoryResponse;
import com.mahmudul.orderservice.dto.OrderLineItemsDto;
import com.mahmudul.orderservice.dto.OrderRequest;
import com.mahmudul.orderservice.model.Order;
import com.mahmudul.orderservice.model.OrderLineItems;
import com.mahmudul.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;

    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems=orderRequest.getOrderLineItemsList()
                .stream()
                .map(this:: mapToDto)
                .collect(Collectors.toList());

        order.setOrderLineItemsList(orderLineItems);

       List<String> skuCodes= order.getOrderLineItemsList().stream()
                .map(OrderLineItems:: getSkuCode)
                .collect(Collectors.toList());

        log.info("Calling inventory service");

        // call inverntory service and place order if product is in stock
       InventoryResponse[] inventoryResponses =  webClientBuilder.build().get()
                .uri("http://localhost:8082/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
       boolean allProductsInStock = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);

       if(allProductsInStock){
           orderRepository.save(order);
       }else {
           throw  new IllegalArgumentException("The product is not in stock");
       }

    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return  orderLineItems;
    }
}
