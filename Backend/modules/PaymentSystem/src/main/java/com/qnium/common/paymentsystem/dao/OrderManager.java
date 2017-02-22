/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.paymentsystem.dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.qnium.common.backend.assets.definitions.FieldOperations;
import com.qnium.common.backend.assets.interfaces.IEntityManager;
import com.qnium.common.backend.core.ConfigManager;
import com.qnium.common.backend.core.EntityManager;
import com.qnium.common.backend.core.FieldFilter;
import com.qnium.common.backend.core.FilteredResult;
import com.qnium.common.paymentsystem.core.PaymentSystem;
import com.qnium.common.paymentsystem.dataobjects.ChargeableService;
import com.qnium.common.paymentsystem.dataobjects.OrderInfoItem;
import com.qnium.common.paymentsystem.dataobjects.Customer;
import com.qnium.common.paymentsystem.dataobjects.Order;
import com.qnium.common.paymentsystem.dataobjects.OrderChargeableServiceAssignment;
import com.qnium.common.paymentsystem.dataobjects.OrderInfo;
import com.qnium.common.paymentsystem.dataobjects.Payment;
import com.qnium.common.paymentsystem.dataobjects.SubscriptionInfo;
import com.qnium.common.paymentsystem.definitions.ChargeableServiceType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Drozhin
 */
public class OrderManager implements IEntityManager<Order>
{
    private static OrderManager instance;
    private static EntityManager<Order> man;
    
    private final Dao<Order, Long> orderDao;
    private final Dao<OrderChargeableServiceAssignment, Long> orderServiceAssignmentDao;
    private final Dao<Payment, Long> paymentDao;
    private final Dao<ChargeableService, Long> chargeableServiceDao;
    
    public static synchronized OrderManager getInstance() throws SQLException {
        if (instance == null) {
            instance = new OrderManager();
            man = EntityManager.getInstance(Order.class);
        }
        return instance;
    }
    
    private OrderManager() throws SQLException
    {
        ConnectionSource connectionSource = new JdbcConnectionSource(ConfigManager.getInstance().getDatabaseURL());
        
        orderDao = DaoManager.createDao(connectionSource, Order.class);
        if (!orderDao.isTableExists()) {
            TableUtils.createTable(connectionSource, Order.class);
        }
        
        orderServiceAssignmentDao = DaoManager.createDao(connectionSource, OrderChargeableServiceAssignment.class);
        if (!orderServiceAssignmentDao.isTableExists()) {
            TableUtils.createTable(connectionSource, OrderChargeableServiceAssignment.class);
        }
        
        paymentDao = DaoManager.createDao(connectionSource, Payment.class);
        if (!paymentDao.isTableExists()) {
            TableUtils.createTable(connectionSource, Payment.class);
        }
        
        chargeableServiceDao = DaoManager.createDao(connectionSource, ChargeableService.class);
        if (!chargeableServiceDao.isTableExists()) {
            TableUtils.createTable(connectionSource, ChargeableService.class);
        }
    }
    
    @Override
    public void create(Order entity) throws Exception {
        createAndReturnId(entity);
    }
    
    public long createAndReturnId(Order entity) throws Exception
    {
        if(entity.chargeableServices != null){
            boolean orderHaveSubscriptions = entity.chargeableServices.stream().anyMatch(p->p.type == ChargeableServiceType.SUBSCRIPTION.getValue());
            if(orderHaveSubscriptions){
                SubscriptionInfo si = SubscriptionManager.getInstance().getCurrentSubscription(entity.customerId);
                if(!si.subscription._default)
                    throw new Exception("SUBSCRIPTION_ALREADY_PAID");        
            }                
        }
        
        entity.creationDate = Calendar.getInstance().getTime();
        man.create(entity);
        
        if(entity.chargeableServices != null)
        {
            int rowNumber = 0;
            for(ChargeableService serv : entity.chargeableServices)
            {
                OrderChargeableServiceAssignment osa = new OrderChargeableServiceAssignment();
                osa.chargeableService = new ChargeableService();
                osa.chargeableService.id = serv.id;
                osa.order = new Order();
                osa.order.id = entity.id;
                osa.rowNumber = rowNumber++;
                orderServiceAssignmentDao.create(osa);
            }
        }
        
        return entity.id;
    }
    
    private void fillChildEntities(Order order) throws SQLException
    {
        order.chargeableServices = new ArrayList();
        
        List<OrderChargeableServiceAssignment> osaList = orderServiceAssignmentDao.queryBuilder()
                .orderBy(OrderChargeableServiceAssignment.ROW_NUMBER, true)
                .where()
                .eq(OrderChargeableServiceAssignment.ORDER_ID, order.id).query();

        for(OrderChargeableServiceAssignment osa : osaList) {
            order.chargeableServices.add(chargeableServiceDao.queryForEq(ChargeableService.ID, osa.chargeableService.id).get(0));
        }
        
        if(order.paid)
        {
            order.payments = paymentDao.queryForEq(Payment.ORDER_ID, order.id);
            
            for(Payment payment : order.payments){
                chargeableServiceDao.refresh(payment.chargeableService);
            }
        }
    }
    
    public double calcOrderAmount(Order order, boolean ignoreDuplicate) throws SQLException
    {
        double amount = 0;
        
        if(order.chargeableServices != null)
        {
            if(ignoreDuplicate)
            {
                List<Long> serviceIds = order.chargeableServices.stream().map(m->m.id).collect(Collectors.toList());
                if(serviceIds.size() > 0){
                    amount = chargeableServiceDao.queryBuilder().where()
                            .in(ChargeableService.ID, serviceIds).query()
                            .stream().mapToDouble(m->m.price).sum();
                }
            } else {            
                for(ChargeableService serv : order.chargeableServices)
                {
                    chargeableServiceDao.refresh(serv);
                    amount += serv.price;
                }
            }
        }
        
        return amount;
    }

    @Override
    public FilteredResult<Order> read(List<FieldFilter> filters, long startIndex, long count) throws Exception
    {
        FilteredResult<Order> result = man.read(filters, startIndex, count);
        
        for(Order order : result.data) {
            fillChildEntities(order);            
        }
        
        return result;
    }

    @Override
    public void update(List<Order> entities) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
        //man.update(entities);
    }

    @Override
    public void delete(List<Order> entities) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
        //man.delete(entities);
    }

    @Override
    public long getCount() throws Exception {
        return man.getCount();
    }
    
    public void confirmPaymentForOrder(long orderId) throws SQLException, Exception
    {
        Order order = orderDao.queryForEq(Order.ID, orderId).get(0);
        
        if(order.paid)
            throw new Exception(String.format("Order [id: %d] already paid.", orderId));
            
        fillChildEntities(order);
        
        PaymentManager pm = PaymentManager.getInstance();
        
        Date currentTime = Calendar.getInstance().getTime();
        for(ChargeableService serv : order.chargeableServices)
        {
            Payment payment = new Payment();
            
            payment.amount = serv.price;
            payment.paymentTime = currentTime;
            payment.chargeableService = serv;
            payment.customerId = order.customerId;
            payment.paymentTime = order.creationDate;
            payment.startTime = new Date();
            payment.endTime = pm.calcEndDate(payment);
            payment.order = new Order();
            payment.order.id = order.id;
            
            pm.create(payment);
        }
        
        order.paid = true;
        
        orderDao.update(order);
        
        PaymentSystem.getOrderStatusHandler().processOrderCompletion(order);
    }
    
    public FilteredResult<OrderInfo> getCustomerOrdersInfo(long customerId, List<FieldFilter> filters, long startIndex, long count) throws SQLException, Exception
    {
        if(filters == null)
            filters = new ArrayList<>();
        
        FieldFilter filter = new FieldFilter();
        filter.field = Order.CUSTOMER_ID;
        filter.operation = FieldOperations.EQ;
        filter.value = customerId;
        filters.add(filter);
        
        FilteredResult<Order> orders = read(filters, startIndex, count);
        List<Long> customerIds = new ArrayList();
        customerIds.add(customerId);
        Customer customer = PaymentSystem.getProvider().getCustomers(customerIds).get(0);
        
        List<OrderInfo> ordersInfo = new ArrayList();
        
        for(Order order : orders.data)
        {
            OrderInfo orderInfo = new OrderInfo();
            ordersInfo.add(orderInfo);
            orderInfo.amount = order.amount;
            orderInfo.creationDate = order.creationDate;
            orderInfo.customer = customer;
            orderInfo.orderNumber = order.id;
            orderInfo.paid = order.paid;
            
            orderInfo.items = new ArrayList();
            
            if(order.paid) {
                for(Payment payment : order.payments)
                {
                    ChargeableService serv = payment.chargeableService;
                    OrderInfoItem item = new OrderInfoItem();
                    orderInfo.items.add(item);
                    item.name = serv.name;
                    item.description = serv.description;
                    item.price = serv.price;
                    item.type = serv.type;
                    if(serv.type == ChargeableServiceType.SUBSCRIPTION.getValue()){
                        item.startDate = payment.startTime;
                        item.endDate = payment.endTime;
                    }
                }
            }
            else
            {
                for(ChargeableService serv : order.chargeableServices)
                {
                    OrderInfoItem item = new OrderInfoItem();
                    orderInfo.items.add(item);
                    item.name = serv.name;
                    item.description = serv.description;
                    item.price = serv.price;
                }
            }
        }
        
        FilteredResult<OrderInfo> ordersInfoResult = new FilteredResult<>();
        ordersInfoResult.totalCounter = orders.totalCounter;
        ordersInfoResult.data = ordersInfo;
        
        return ordersInfoResult;
    }
    
    public Order getOrderById(long customerId, long orderId) throws Exception {
        Order requestedOrder = orderDao.queryBuilder()
                .where()
                .eq(Order.ID, orderId)
                .and().eq(Order.CUSTOMER_ID, customerId)
                .queryForFirst();
        if (requestedOrder == null) {
            throw new Exception("Order " + orderId + "was not found for customer");
        }
        else {
            return requestedOrder;
        }
    }
    
}
