package com.gft.digitalbank.exchange.solution.service.jms;

import com.gft.digitalbank.exchange.solution.service.events.ExchangeEventBus;
import com.gft.digitalbank.exchange.solution.service.events.TradingFinishedEvent;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.List;

/**
 * Created by iozi on 2016-06-28.
 */
@Singleton
public class JMSSessionManager {

    private final TradingMessageListenerFactory tradingMessageListenerFactory;

    @Inject
    public JMSSessionManager(TradingMessageListenerFactory tradingMessageListenerFactory,
                             ExchangeEventBus exchangeEventBus) {
        this.tradingMessageListenerFactory = tradingMessageListenerFactory;
        exchangeEventBus.register(this);
    }

    private static final String CONNECTION_FACTORY_NAME = "ConnectionFactory";

    private Session session;

    public void connectWithBrokers(List<String> destinations) {
        Context context = null;
        try {
            context = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup(CONNECTION_FACTORY_NAME);
            Connection connection = connectionFactory.createConnection();
            connection.start();
            this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            for (String queueName : destinations) {
                Destination destination = session.createQueue(queueName);
                MessageConsumer consumer = session.createConsumer(destination);
                consumer.setMessageListener(tradingMessageListenerFactory.createTradingMessageListener());
            }
        } catch (NamingException | JMSException e) {
            //TODO
            e.printStackTrace();
        }
    }

    @Subscribe
    public void closeSession(TradingFinishedEvent tradingFinishedEvent) {
        try {
            long start = System.currentTimeMillis();
            session.close();
        } catch (JMSException e) {
            //TODO
            e.printStackTrace();
        }
    }
}
