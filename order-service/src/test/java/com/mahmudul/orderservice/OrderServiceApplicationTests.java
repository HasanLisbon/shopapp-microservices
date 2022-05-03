package com.mahmudul.orderservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahmudul.orderservice.dto.OrderLineItemsDto;
import com.mahmudul.orderservice.dto.OrderRequest;
import com.mahmudul.orderservice.model.Order;
import com.mahmudul.orderservice.model.OrderLineItems;
import com.mahmudul.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class OrderServiceApplicationTests {
	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Container
	public static PostgreSQLContainer postgresDB = new PostgreSQLContainer("postgres:14.2")
			.withUsername("postgres")
			.withPassword("postgres")
			.withDatabaseName("order-service");

	@DynamicPropertySource
	public static void properties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url",postgresDB::getJdbcUrl);
		registry.add("spring.datasource.username", postgresDB::getUsername);
		registry.add("spring.datasource.password", postgresDB::getPassword);

	}

	@Test
	void shouldPlaceOrder() throws Exception {
		List<OrderLineItems> orderLineItems = getOrderRequest();
		Order order = Order.builder()
						.orderNumber(UUID.randomUUID().toString())
						.orderLineItemsList(orderLineItems)
						.build();
		String orderRequestString = objectMapper.writeValueAsString(order);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/order")
						.contentType(MediaType.APPLICATION_JSON)
						.content(orderRequestString))
				.andExpect(status().isCreated());
		Assertions.assertEquals(1, orderRepository.findAll().size());
	}

	private List<OrderLineItems> getOrderRequest() {
		List<OrderLineItems> orderLineItemsDtos = new ArrayList<>();
		OrderLineItems orderLineItemsDto = OrderLineItems.builder()
				.skuCode("iphone_13")
				.price(BigDecimal.valueOf(1200))
				.quantity(1)
				.build();
		orderLineItemsDtos.add(orderLineItemsDto);
		return orderLineItemsDtos;
	}

}
