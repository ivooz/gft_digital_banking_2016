package com.gft.digitalbank.exchange.solution.service.jms;

import com.gft.digitalbank.exchange.solution.model.Cancel;
import com.gft.digitalbank.exchange.solution.model.Modification;
import com.gft.digitalbank.exchange.solution.model.Order;
import com.gft.digitalbank.exchange.solution.service.deserialization.DeserializationException;
import com.gft.digitalbank.exchange.solution.service.deserialization.DeserializationResult;
import com.gft.digitalbank.exchange.solution.service.deserialization.MessageDeserializer;
import com.gft.digitalbank.exchange.solution.service.dispatching.TradingMessageDispatcher;
import com.gft.digitalbank.exchange.solution.service.events.ExchangeEventBus;
import com.gft.digitalbank.exchange.solution.service.monitoring.ProcessingMonitor;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * Created by iozi on 2016-06-28.
 */
public class TradingMessageListener implements MessageListener {

    private final TradingMessageDispatcher tradingMessageDispatcher;
    private final MessageDeserializer messageDeserializer;
    private final ProcessingMonitor processingMonitor;

    public TradingMessageListener(TradingMessageDispatcher tradingMessageDispatcher,
                                  MessageDeserializer messageDeserializer,
                                  ExchangeEventBus exchangeEventBus,
                                  ProcessingMonitor processingMonitor) {
        this.tradingMessageDispatcher = tradingMessageDispatcher;
        this.messageDeserializer = messageDeserializer;
        this.processingMonitor = processingMonitor;
    }

    @Override
    public synchronized void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            DeserializationResult deserializationResult = messageDeserializer.deserialize(textMessage.getText());
            switch (deserializationResult.getMessageType()) {
                case ORDER:
                    tradingMessageDispatcher.dispatchOrder((Order) deserializationResult.getResult());
                    break;
                case MODIFICATION:
                    tradingMessageDispatcher.dispatchModification((Modification) deserializationResult.getResult());
                    break;
                case CANCEL:
                    tradingMessageDispatcher.dispatchCancel((Cancel) deserializationResult.getResult());
                    break;
                case SHUTDOWN:
                    processingMonitor.decreaseBrokerCounter();
                    break;
            }
        } catch (Exception e) {
            //TODO logging
            e.printStackTrace();
        } catch (DeserializationException e) {
            e.printStackTrace();
        }
    }
}
