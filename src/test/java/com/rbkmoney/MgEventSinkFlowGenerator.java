package com.rbkmoney;


import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.machinegun.msgpack.Value;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MgEventSinkFlowGenerator {

    private static final String SOURCE_NS = "source_ns";
    private static final String PAYMENT_ID = "1";
    private static final String SHOP_ID = "SHOP_ID";
    private static final String TEST_MAIL_RU = "test@mail.ru";
    private static final String BIN = "666";

    public static List<SinkEvent> generateSuccessFlow(String sourceId) {
        List<SinkEvent> sinkEvents = new ArrayList<>();
        sinkEvents.add(createSinkEvent(createMessageCreateInvoice(sourceId)));
        sinkEvents.add(createSinkEvent(createMessagePaymentStared(sourceId)));
        sinkEvents.add(createSinkEvent(createMessageInvoiceCaptured(sourceId)));
        return sinkEvents;
    }

    private static SinkEvent createSinkEvent(MachineEvent machineEvent) {
        SinkEvent sinkEvent = new SinkEvent();
        sinkEvent.setEvent(machineEvent);
        return sinkEvent;
    }

    private static MachineEvent createMessageCreateInvoice(String sourceId) {
        InvoiceCreated invoiceCreated = createInvoiceCreate(sourceId);
        InvoiceChange invoiceChange = new InvoiceChange();
        invoiceChange.setInvoiceCreated(invoiceCreated);
        return createMachineEvent(invoiceChange, sourceId);
    }

    private static MachineEvent createMessageInvoiceCaptured(String sourceId) {
        InvoiceChange invoiceCaptured = createInvoiceCaptured();
        return createMachineEvent(invoiceCaptured, sourceId);
    }

    private static MachineEvent createMessagePaymentStared(String sourceId) {
        InvoiceChange invoiceCaptured = createPaymentStarted();
        return createMachineEvent(invoiceCaptured, sourceId);
    }

    private static MachineEvent createMachineEvent(InvoiceChange invoiceChange, String sourceId) {
        MachineEvent message = new MachineEvent();
        EventPayload payload = new EventPayload();
        ArrayList<InvoiceChange> invoiceChanges = new ArrayList<>();
        invoiceChanges.add(invoiceChange);
        payload.setInvoiceChanges(invoiceChanges);

        message.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        message.setEventId(1L);
        message.setSourceNs(SOURCE_NS);
        message.setSourceId(sourceId);

        ThriftSerializer<EventPayload> eventPayloadThriftSerializer = new ThriftSerializer<>();
        Value data = new Value();
        data.setBin(eventPayloadThriftSerializer.serialize("", payload));
        message.setData(data);
        return message;
    }

    private static InvoiceCreated createInvoiceCreate(String sourceId) {

        return new InvoiceCreated()
                .setInvoice(new com.rbkmoney.damsel.domain.Invoice()
                        .setId(sourceId)
                        .setOwnerId("owner_id")
                        .setShopId(SHOP_ID)
                        .setCreatedAt("2016-08-10T16:07:18Z")
                        .setStatus(InvoiceStatus.unpaid(new InvoiceUnpaid()))
                        .setDue("2016-08-10T16:07:23Z")
                        .setCost(new Cash(12L, new CurrencyRef("RUB")))
                        .setDetails(new InvoiceDetails("product"))
                        .setContext(new Content()
                                .setType("contentType")
                                .setData("test".getBytes())
                        )
                );
    }

    private static InvoiceChange createInvoiceCaptured() {
        InvoiceChange invoiceChange = new InvoiceChange();
        InvoicePaymentChangePayload payload = new InvoicePaymentChangePayload();
        InvoicePaymentStatusChanged invoicePaymentStatusChanged = new InvoicePaymentStatusChanged()
                .setStatus(InvoicePaymentStatus.captured(new InvoicePaymentCaptured()));
        payload.setInvoicePaymentStatusChanged(invoicePaymentStatusChanged);
        invoiceChange.setInvoicePaymentChange(new InvoicePaymentChange()
                .setId("1")
                .setPayload(payload));
        return invoiceChange;
    }

    private static InvoiceChange createPaymentStarted() {
        InvoicePaymentChangePayload invoicePaymentChangePayload = new InvoicePaymentChangePayload();
        invoicePaymentChangePayload.setInvoicePaymentStarted(
                new InvoicePaymentStarted()
                        .setPayment(new com.rbkmoney.damsel.domain.InvoicePayment()
                                .setCost(
                                        new Cash()
                                                .setAmount(123L)
                                                .setCurrency(new CurrencyRef()
                                                        .setSymbolicCode("RUB")))
                                .setCreatedAt("2016-08-10T16:07:18Z")
                                .setId(PAYMENT_ID)
                                .setStatus(InvoicePaymentStatus.processed(new InvoicePaymentProcessed()))
                                .setPayer(createCustomerPayer())
                                .setFlow(createFlow())));
        InvoiceChange invoiceChange = new InvoiceChange();
        invoiceChange.setInvoicePaymentChange(new InvoicePaymentChange()
                .setId(PAYMENT_ID)
                .setPayload(invoicePaymentChangePayload));
        return invoiceChange;
    }

    @NotNull
    private static InvoicePaymentFlow createFlow() {
        InvoicePaymentFlow flow = new InvoicePaymentFlow();
        InvoicePaymentFlowHold invoicePaymentFlowHold = new InvoicePaymentFlowHold();
        invoicePaymentFlowHold.setOnHoldExpiration(OnHoldExpiration.capture);
        invoicePaymentFlowHold.setHeldUntil("werwer");
        flow.setHold(invoicePaymentFlowHold);
        return flow;
    }

    private static ClientInfo createClientInfo() {
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setFingerprint("finger");
        clientInfo.setIpAddress("123.123.123.123");
        return clientInfo;
    }

    private static Payer createCustomerPayer() {
        Payer customer = Payer.customer(new CustomerPayer("custId", "1", "rec_paym_tool", createBankCard(), new ContactInfo()));
        customer.setPaymentResource(
                new PaymentResourcePayer()
                        .setResource(new DisposablePaymentResource()
                                .setClientInfo(createClientInfo())
                                .setPaymentTool(createBankCard()))
                        .setContactInfo(new ContactInfo()
                                .setEmail(TEST_MAIL_RU)));
        return customer;
    }

    private static PaymentTool createBankCard() {
        PaymentTool paymentTool = new PaymentTool();
        paymentTool.setBankCard(
                new BankCard()
                        .setToken("477bba133c182267fe5f086924abdc5db71f77bfc27f01f2843f2cdc69d89f05")
                        .setPaymentSystem(BankCardPaymentSystem.mastercard)
                        .setBin(BIN)
                        .setLastDigits("3333")
                        .setIssuerCountry(Residence.RUS)
        );
        return paymentTool;
    }

}
