/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.paymentsystem.core;

import com.qnium.common.paymentsystem.dao.PaymentManager;
import com.qnium.common.backend.assets.dataobjects.CollectionResponseMessage;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.dataobjects.CountResponseMessage;
import com.qnium.common.backend.assets.dataobjects.CreateRequestParameters;
import com.qnium.common.backend.assets.dataobjects.DeleteRequestParameters;
import com.qnium.common.backend.assets.dataobjects.UpdateRequestParameters;
import com.qnium.common.backend.assets.dataobjects.ByIdRequestParameters;
import com.qnium.common.backend.assets.dataobjects.ObjectResponseMessage;
import com.qnium.common.backend.assets.dataobjects.ReadRequestParameters;
import com.qnium.common.backend.assets.dataobjects.ResponseMessage;
import com.qnium.common.backend.assets.definitions.SecurityLevel;
import com.qnium.common.backend.assets.handlers.generic.CreateHandler;
import com.qnium.common.backend.assets.handlers.generic.DeleteHandler;
import com.qnium.common.backend.assets.handlers.generic.ReadHandler;
import com.qnium.common.backend.assets.handlers.generic.UpdateHandler;
import com.qnium.common.backend.core.EntityManagerStorage;
import com.qnium.common.backend.core.HandlerWrapper;
import com.qnium.common.backend.core.HandlersManager;
import com.qnium.common.backend.core.EntityManager;
import com.qnium.common.paymentsystem.core.messages.OrderPaymentRequest;
import com.qnium.common.paymentsystem.core.messages.OrderStatusRequest;
import com.qnium.common.paymentsystem.dao.ChargeableServiceManager;
import com.qnium.common.paymentsystem.dao.CustomerManager;
import com.qnium.common.paymentsystem.dao.CustomerSubscriptionManager;
import com.qnium.common.paymentsystem.dao.OneTimeServiceManager;
import com.qnium.common.paymentsystem.dao.OrderManager;
import com.qnium.common.paymentsystem.dao.PaymentMethodManager;
import com.qnium.common.paymentsystem.dao.SubscriptionManager;
import com.qnium.common.paymentsystem.dataobjects.CalcPaymentParamsRequestParameters;
import com.qnium.common.paymentsystem.dataobjects.ChargeableService;
import com.qnium.common.paymentsystem.dataobjects.OneTimeService;
import com.qnium.common.paymentsystem.dataobjects.Order;
import com.qnium.common.paymentsystem.dataobjects.Payment;
import com.qnium.common.paymentsystem.dataobjects.PaymentPage;
import com.qnium.common.paymentsystem.dataobjects.Subscription;
import com.qnium.common.paymentsystem.handlers.orders.GetCustomerOrders;
import com.qnium.common.paymentsystem.handlers.orders.OrdersCreateByCustomer;
import com.qnium.common.paymentsystem.handlers.orders.OrdersGetStatus;
import com.qnium.common.paymentsystem.handlers.orders.OrdersStartPayment;
import com.qnium.common.paymentsystem.handlers.payments.CalcPaymentParamsHandler;
import com.qnium.common.paymentsystem.handlers.payments.ReadUserPaymentsHandler;
import com.qnium.common.paymentsystem.handlers.subscriptions.GetFeaturesBySubscriptionIdHandler;
import com.qnium.common.paymentsystem.handlers.subscriptions.GetFeaturesHandler;
import com.qnium.common.paymentsystem.handlers.subscriptions.SetDefaultSubscriptionHandler;
import com.qnium.common.paymentsystem.interfaces.IOrderStatusHandler;
import com.qnium.common.paymentsystem.interfaces.IPaymentSystemProvider;
import com.qnium.common.paymentsystem.interfaces.PaymentMethodProvider;
import java.sql.SQLException;
import java.util.Map;
import org.codehaus.jackson.type.TypeReference;

/**
 *
 * @author
 */
public class PaymentSystem
{
    private static IPaymentSystemProvider provider;
    private static IOrderStatusHandler orderStatusHandler;
    private static PaymentMethodManager paymentMethodManager;
    
    public static IPaymentSystemProvider getProvider(){
        return provider;
    }
    
    public static IOrderStatusHandler getOrderStatusHandler() {
        return orderStatusHandler;
    }
    
    public static void initialize(IPaymentSystemProvider provider, 
            IOrderStatusHandler orderStatusHandler) throws Exception
    {         
        PaymentSystem.provider = provider;
        PaymentSystem.orderStatusHandler = orderStatusHandler;
        paymentMethodManager = PaymentMethodManager.getInstance();
        EntityManagerStorage entityManagerStorage = EntityManagerStorage.getInstance();
                
        PaymentManager paymentManager = PaymentManager.getInstance();
        
        entityManagerStorage.addEntityManager("payments", paymentManager);
        //entityManagerStorage.addEntityManager("chargeableServices", EntityManager.getInstance(ChargeableService.class));
        entityManagerStorage.addEntityManager("chargeableServices", ChargeableServiceManager.getInstance());
        entityManagerStorage.addEntityManager("subscriptions", SubscriptionManager.getInstance());
        entityManagerStorage.addEntityManager("currentSubscriptions", CustomerSubscriptionManager.getInstance());
        entityManagerStorage.addEntityManager("customers", CustomerManager.getInstance());
        entityManagerStorage.addEntityManager("oneTimeServices", OneTimeServiceManager.getInstance());
        entityManagerStorage.addEntityManager("customerOrders", OrderManager.getInstance());
                
        HandlersManager handlersManager = HandlersManager.getInstance();
        
        handlersManager.addHandler("payments.read", new HandlerWrapper(
                new TypeReference<RequestMessage<ReadRequestParameters>>() {},
                new TypeReference<CollectionResponseMessage>() {},
                new ReadHandler()));
        
        handlersManager.addHandler("payments.create", new HandlerWrapper(
                new TypeReference<RequestMessage<CreateRequestParameters<Payment>>>() {},
                new TypeReference<ResponseMessage>() {},
                new CreateHandler()));
        
        handlersManager.addHandler("payments.update", new HandlerWrapper(
                new TypeReference<RequestMessage<UpdateRequestParameters<Payment>>>() {},
                new TypeReference<CountResponseMessage>() {},
                new UpdateHandler()));
        
        handlersManager.addHandler("payments.delete", new HandlerWrapper(
                new TypeReference<RequestMessage<DeleteRequestParameters<Payment>>>() {},
                new TypeReference<CountResponseMessage>() {},
                new DeleteHandler()));
        
        handlersManager.addHandler("payments.calcPaymentParams", new HandlerWrapper(
                new TypeReference<RequestMessage<CalcPaymentParamsRequestParameters>>() {},
                new TypeReference<ObjectResponseMessage>() {},
                new CalcPaymentParamsHandler()));

        handlersManager.addHandler("payments.readByCurrentUser", new HandlerWrapper(
                new TypeReference<RequestMessage<ReadRequestParameters>>() {},
                new TypeReference<CollectionResponseMessage>() {},
                new ReadUserPaymentsHandler()));
        

        handlersManager.addHandler("chargeableServices.read", new HandlerWrapper(
                new TypeReference<RequestMessage<ReadRequestParameters>>() {},
                new TypeReference<CollectionResponseMessage>() {},
                new ReadHandler(SecurityLevel.NONE)));
        
        handlersManager.addHandler("chargeableServices.create", new HandlerWrapper(
                new TypeReference<RequestMessage<CreateRequestParameters<ChargeableService>>>() {},
                new TypeReference<ResponseMessage>() {},
                new CreateHandler()));
        
        handlersManager.addHandler("chargeableServices.update", new HandlerWrapper(
                new TypeReference<RequestMessage<UpdateRequestParameters<ChargeableService>>>() {},
                new TypeReference<CountResponseMessage>() {},
                new UpdateHandler()));
        
        handlersManager.addHandler("chargeableServices.delete", new HandlerWrapper(
                new TypeReference<RequestMessage<DeleteRequestParameters<ChargeableService>>>() {},
                new TypeReference<CountResponseMessage>() {},
                new DeleteHandler()));


        
        handlersManager.addHandler("subscriptions.read", new HandlerWrapper(
                new TypeReference<RequestMessage<ReadRequestParameters>>() {},
                new TypeReference<CollectionResponseMessage>() {},
                new ReadHandler(SecurityLevel.NONE)));
        
        handlersManager.addHandler("subscriptions.getFeaturesBySubscriptionId", new HandlerWrapper(
                new TypeReference<RequestMessage<ByIdRequestParameters>>() {},
                new TypeReference<CollectionResponseMessage>() {},
                new GetFeaturesBySubscriptionIdHandler()));
        
        handlersManager.addHandler("subscriptions.getFeatures", new HandlerWrapper(
                new TypeReference<RequestMessage>() {},
                new TypeReference<CollectionResponseMessage>() {},
                new GetFeaturesHandler()));
        
        handlersManager.addHandler("subscriptions.update", new HandlerWrapper(
                new TypeReference<RequestMessage<UpdateRequestParameters<Subscription>>>() {},
                new TypeReference<CountResponseMessage>() {},
                new UpdateHandler()));
        
        handlersManager.addHandler("subscriptions.create", new HandlerWrapper(
                new TypeReference<RequestMessage<CreateRequestParameters<Subscription>>>() {},
                new TypeReference<ResponseMessage>() {},
                new CreateHandler()));
        
        handlersManager.addHandler("subscriptions.setDefault", new HandlerWrapper(
                new TypeReference<RequestMessage<UpdateRequestParameters<Subscription>>>() {},
                new TypeReference<CountResponseMessage>() {},
                new SetDefaultSubscriptionHandler()));
        
        handlersManager.addHandler("subscriptions.delete", new HandlerWrapper(
                new TypeReference<RequestMessage<DeleteRequestParameters<Subscription>>>() {},
                new TypeReference<CountResponseMessage>() {},
                new DeleteHandler()));
        
        
        
        handlersManager.addHandler("features.read", new HandlerWrapper(
                new TypeReference<RequestMessage<ReadRequestParameters>>() {},
                new TypeReference<CollectionResponseMessage>() {},
                new GetFeaturesHandler()));
        
        

        handlersManager.addHandler("currentSubscriptions.read", new HandlerWrapper(
                new TypeReference<RequestMessage<ReadRequestParameters>>() {},
                new TypeReference<CollectionResponseMessage>() {},
                new ReadHandler()));
        
        
        
        handlersManager.addHandler("customers.read", new HandlerWrapper(
                new TypeReference<RequestMessage<ReadRequestParameters>>() {},
                new TypeReference<CollectionResponseMessage>() {},
                new ReadHandler()));
        
        
        
        handlersManager.addHandler("oneTimeServices.read", new HandlerWrapper(
                new TypeReference<RequestMessage<ReadRequestParameters>>() {},
                new TypeReference<CollectionResponseMessage>() {},
                new ReadHandler()));
        
        handlersManager.addHandler("oneTimeServices.create", new HandlerWrapper(
                new TypeReference<RequestMessage<CreateRequestParameters<OneTimeService>>>() {},
                new TypeReference<ResponseMessage>() {},
                new CreateHandler()));
        
        handlersManager.addHandler("oneTimeServices.update", new HandlerWrapper(
                new TypeReference<RequestMessage<UpdateRequestParameters<OneTimeService>>>() {},
                new TypeReference<CountResponseMessage>() {},
                new UpdateHandler()));
        
        handlersManager.addHandler("oneTimeServices.delete", new HandlerWrapper(
                new TypeReference<RequestMessage<DeleteRequestParameters<OneTimeService>>>() {},
                new TypeReference<CountResponseMessage>() {},
                new DeleteHandler()));
        
        
        
        handlersManager.addHandler("customerOrders.read", new HandlerWrapper(
                new TypeReference<RequestMessage<ReadRequestParameters>>() {},
                new TypeReference<CollectionResponseMessage>() {},
                new GetCustomerOrders()));
        
        handlersManager.addHandler("orders.createByCustomer", new HandlerWrapper(
                new TypeReference<RequestMessage<CreateRequestParameters<Order>>>() {},
                new TypeReference<ObjectResponseMessage<Long>>() {},
                new OrdersCreateByCustomer()));
        
        handlersManager.addHandler("orders.getStatus", new HandlerWrapper(
                new TypeReference<RequestMessage<OrderStatusRequest>>() {},
                new TypeReference<ObjectResponseMessage<Boolean>>() {},
                new OrdersGetStatus()));
        
        handlersManager.addHandler("orders.startPayment", new HandlerWrapper(
                new TypeReference<RequestMessage<OrderPaymentRequest>>() {},
                new TypeReference<ObjectResponseMessage<PaymentPage>>() {},
                new OrdersStartPayment()));
    }
    
    public static void registerPaymentMethodProvider(PaymentMethodProvider provider, Map<String, String> parameters) {
        paymentMethodManager.registerProvider(provider, parameters);
    }
}
