package com.cassandra.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.log4j.Logger;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.config.CassandraEntityClassScanner;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.convert.CassandraConverter;
import org.springframework.data.cassandra.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.mapping.BasicCassandraMappingContext;
import org.springframework.data.cassandra.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.mapping.SimpleUserTypeResolver;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.casandra.test.domain.cassandra.Order;
import com.casandra.test.repository.OrderRepository;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class OrderRepositoryTest {

	private static Logger LOGGER = Logger.getLogger(OrderRepositoryTest.class);

	public static final String KEYSPACE_CREATION_QUERY = "CREATE KEYSPACE IF NOT EXISTS test_01 WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': '3' };";

	public static final String KEYSPACE_ACTIVATE_QUERY = "USE test_01;";

	public static final String KEYSPACE = "test_01";

	public static final String ORDER_DATA = "order_data";

	@Autowired
	private OrderRepository orderRepository;

	@BeforeClass
	public static void startCassandraEmbedded()
			throws InterruptedException, TTransportException, ConfigurationException, IOException {
		EmbeddedCassandraServerHelper.startEmbeddedCassandra(10000000L);
		final Cluster cluster = Cluster.builder().addContactPoints("127.0.0.1").withPort(9142).build();
		LOGGER.info("Server Started at 127.0.0.1:9142... ");
		final Session session = cluster.connect();
		session.execute(KEYSPACE_CREATION_QUERY);
		session.execute(KEYSPACE_ACTIVATE_QUERY);
		LOGGER.info("KeySpace created and activated.");
		Thread.sleep(5000);
	}

	@Test
	public void test() {
		final Order order = new Order();
		order.setOrderID("1");
		order.setAmount(1f);
		order.setDiscount(1f);

		orderRepository.save(order);
		final Iterable<Order> rules = orderRepository.findByOrderId(order.getOrderID());
		assertEquals(order.getOrderID(), rules.iterator().next().getOrderID());
	}

	@Configuration
	@EnableCassandraRepositories(basePackages = { "com.casandra.test.repository" })
	public static class DaoConfiguration {
		@Bean
		public CassandraClusterFactoryBean cluster() {
			CassandraClusterFactoryBean cluster = new CassandraClusterFactoryBean();
			cluster.setContactPoints("127.0.0.1");
			cluster.setPort(9142);
			return cluster;
		}

		@Bean
		public CassandraMappingContext mappingContext() throws ClassNotFoundException {
			BasicCassandraMappingContext mappingContext = new BasicCassandraMappingContext();
			mappingContext.setUserTypeResolver(new SimpleUserTypeResolver(cluster().getObject(), KEYSPACE));
			mappingContext.setInitialEntitySet(
					CassandraEntityClassScanner.scan(new String[] { "com.casandra.test.domain.cassandra" }));
			return mappingContext;
		}

		@Bean
		public CassandraConverter converter() throws ClassNotFoundException {
			return new MappingCassandraConverter(mappingContext());
		}

		@Bean
		public CassandraSessionFactoryBean session() throws Exception {
			CassandraSessionFactoryBean session = new CassandraSessionFactoryBean();
			session.setCluster(cluster().getObject());
			session.setKeyspaceName(KEYSPACE);
			session.setConverter(converter());
			session.setSchemaAction(SchemaAction.CREATE_IF_NOT_EXISTS);
			return session;
		}

		@Bean
		public CassandraOperations cassandraTemplate() throws Exception {
			return new CassandraTemplate(session().getObject());
		}

	}
}
