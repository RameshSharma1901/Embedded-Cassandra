package com.cassandra.test;

import com.casandra.test.config.CassandraConfig;
import com.casandra.test.domain.cassandra.Order;
import com.casandra.test.repository.OrderRepository;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.log4j.Logger;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CassandraConfig.class)
public class OrderRepositoryTest {

    private static Logger LOGGER = Logger.getLogger(OrderRepositoryTest.class);

    public static final String KEYSPACE_CREATION_QUERY = "CREATE KEYSPACE IF NOT EXISTS movemoney WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': '3' };";

    public static final String KEYSPACE_ACTIVATE_QUERY = "USE movemoney;";

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
        LOGGER.info("" +
                " created and activated.");
        Thread.sleep(5000);
    }

    @Test
    public void test() {
        final Order order = new Order();
        order.setOrderID("UATgUc7WgapVpLd7Kkn1hoX2");
        order.setOrderDetails("4STaAyHoaoDQ2zUbwDXoQS1HugBH4NlCpxNB6tqSC6cBc6dmR4dtmxDcOEmzGypJclYFpY4eSMnQ3ecZn90rE-V62arJAqXiyegz22y2Seg65diHWAP114KH_EwlkecuZgKib_AJI_qY9BMVNQQX1ZEbqPBll8IoFT_Rq8cdm_LXOtd77t36V3plbb2Pw0MqKQBfL0pHwTXtYcNtBI4KzTSjsprxuax2UnIhQwrqDz0f_lTMD-ifSoMJkBnnQ9Our3kdVM4_mDbWpZUKO-w_QABPec3leFPMeHL9Fk-6AY2ltKa0T5bF0ETI_cTNE0zruB6091mmEKUXP5O1xXFttCvMibbTEKmFw07-0VnyaMTGA9ufMlBObktm2us_cEEDAavxaHFREzuNuxhS78WjKzX-yqJvwZ43sHIZN3PZb-6dONlJNlfVFTtqgOoRPpnswi9doYV0rDW-NBxzmHQdExRqaKpSPVsgKp2kfkidZsYibqXo7QwnQzD5YMtInCiRsdF4BEIHXmwcjIM7gL_V9luEqeqM_-lfZJY-IJyQ7S6Jmyjyk0fvgCuo5qEqh3X_fhP7_d3PsaUCW8mZ-_i-1g5tzlRv7wUyGyn-i0KuYpuQGti1PmldaW9uG1Z9RA==");
        order.setTransactionFlowType("SECT");

        orderRepository.save(order);
        final Iterable<Order> rules = orderRepository.findByOrderId(order.getOrderID());
        assertEquals(order.getOrderID(), rules.iterator().next().getOrderID());
    }

}
