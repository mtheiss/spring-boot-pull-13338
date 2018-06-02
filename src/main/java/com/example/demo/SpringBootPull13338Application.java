package com.example.demo;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

@SpringBootApplication
@EnableCaching
public class SpringBootPull13338Application implements BeanClassLoaderAware {
	private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootPull13338Application.class);
	
	private ClassLoader beanClassLoader;
	
	@Bean
	public DisposableBean contextShutdownGate(ApplicationContext context) {
		CountDownLatch latch = new CountDownLatch(1);
		Thread await = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					latch.await();
				} catch (InterruptedException e) {
					LOGGER.error("contextAwait interrupted", e);
				}
			}
		}, "contextAwait-" + context.getId() + "-" + context.getStartupDate());
		await.setDaemon(false);
		await.start();
		return () -> {
			latch.countDown();
		};
	}
	
	@Bean
	public CommandLineRunner cli(SimpleProductRepository productRepository) {
		return args -> {
			StopWatch stopWatch = new StopWatch("getProductById");
			stopWatch.start();
			Product p1 = productRepository.getProductById(1l);
			stopWatch.stop();
			LOGGER.info("Fetched product \"{}\" in {}ms", p1.getName(), stopWatch.getTotalTimeMillis());
		};
	}
	
	private CachingProvider getCachingProvider(String cachingProviderFqn) {
		if (StringUtils.hasText(cachingProviderFqn)) {
			return Caching.getCachingProvider(cachingProviderFqn);
		}
		return Caching.getCachingProvider();
	}
	
	@Bean
	@ConditionalOnProperty(name = "jcache.beanclassloader", havingValue = "true")
	public CacheManager jCacheManager(CacheProperties cacheProperties) throws IOException {
		CachingProvider cachingProvider = getCachingProvider(cacheProperties.getJcache().getProvider());
		Resource configLocation = cacheProperties.resolveConfigLocation(cacheProperties.getJcache().getConfig());
		if (configLocation != null) {
			return cachingProvider.getCacheManager(configLocation.getURI(), beanClassLoader);
		}
		return cachingProvider.getCacheManager();
	}
	
	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootPull13338Application.class, args);
	}
}
