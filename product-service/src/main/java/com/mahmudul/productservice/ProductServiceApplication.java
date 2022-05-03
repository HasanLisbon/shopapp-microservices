	package com.mahmudul.productservice;

	import org.springframework.boot.SpringApplication;
	import org.springframework.boot.autoconfigure.SpringBootApplication;

	@SpringBootApplication
	public class ProductServiceApplication {

		public static void main(String[] args) {
			try{
				SpringApplication.run(ProductServiceApplication.class, args);

			}catch (Exception err){
				System.out.println(err);
			}

		}
	}
