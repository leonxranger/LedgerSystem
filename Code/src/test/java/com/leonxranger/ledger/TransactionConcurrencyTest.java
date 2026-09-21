package com.leonxranger.ledger;
import com.leonxranger.ledger.Repository.AccountRepository;
import com.leonxranger.ledger.Services.TransactionService;


import com.leonxranger.ledger.dto.TransactionItem;
import com.leonxranger.ledger.dto.TransactionRequest;
import com.leonxranger.ledger.entity.AccountType;
import com.leonxranger.ledger.entity.Accounts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
@Testcontainers
public class TransactionConcurrencyTest {
		@Container
		private static final PostgreSQLContainer<?> postgres =
					new PostgreSQLContainer<>("postgres:16-alpine")
							.withDatabaseName("Test-Ledger")
							.withUsername("test")
							.withPassword("test");


		@DynamicPropertySource
		static void dynamicProperties(DynamicPropertyRegistry registry){
			registry.add("spring.datasource.url",postgres::getJdbcUrl);
			registry.add("spring.datasource.username",postgres::getUsername);
			registry.add("spring.datasource.password",postgres::getPassword);
			registry.add("spring.flyway.enabled",()->true);
			registry.add("spring.datasource.hikari.maximum-pool-size", () -> "50");
		}


		@Autowired
		private TransactionService transactionService;

		@Autowired
		private AccountRepository accountRepository;

		private  String sourceAccountCode = "ACC-SOURCE";
		private String targetAccountCode="ACC-TARGET";


		@BeforeEach
		void Setup(){
			accountRepository.deleteAll();
			Accounts Source = new Accounts(0L,sourceAccountCode,"Source", AccountType.ASSET);
			Accounts Destination = new Accounts(0L,targetAccountCode,"Target",AccountType.LIABILITY);

			accountRepository.save(Source);
			accountRepository.save(Destination);
		}


		@Test
		void ConcurrencyTestWithThreads() throws InterruptedException{
			int NumberOfThreads = 50;
			ExecutorService executorService = Executors.newFixedThreadPool(NumberOfThreads);
			CountDownLatch readyLatch = new CountDownLatch(NumberOfThreads);
			CountDownLatch startLatch = new CountDownLatch(1);
			CountDownLatch doneLatch = new CountDownLatch(NumberOfThreads);

			AtomicInteger failedTransactions = new AtomicInteger();
			AtomicInteger SuccessfulTransactions = new AtomicInteger();
			BigDecimal TransferAmount = new BigDecimal("10.00");


				for (int i = 0; i < NumberOfThreads; i++) {

					executorService.submit(() -> {
						readyLatch.countDown();
						try {
						startLatch.await();
						TransactionItem debitItem = new TransactionItem();
						debitItem.setAccountCode(sourceAccountCode);
						debitItem.setAmount(TransferAmount);

						TransactionItem creditItem = new TransactionItem();
						creditItem.setAccountCode(targetAccountCode);
						creditItem.setAmount(TransferAmount.negate());

						TransactionRequest transactionRequest = new TransactionRequest();
						transactionRequest.setItemlist(List.of(debitItem,creditItem));

						transactionService.recordTransaction(transactionRequest);
						SuccessfulTransactions.incrementAndGet();

						} catch (Exception e) {
							failedTransactions.incrementAndGet();
						}finally {
							doneLatch.countDown();
						}

					});

				}


			readyLatch.await();
			startLatch.countDown();
			//waits until donelatch's value become 0
			boolean completed = doneLatch.await(15, java.util.concurrent.TimeUnit.SECONDS);
			executorService.shutdown();

			if(!completed){
				throw new RuntimeException("The test timed out! Threads are deadlocked or starved.");
			}
				System.out.println("Successful transfers:" + SuccessfulTransactions.get());
				System.out.println("failed transfers:" + failedTransactions.get());


				assertEquals(NumberOfThreads, SuccessfulTransactions.get(), "All transactions should have successfully serialised through pessimistic locks");




		}

		}


