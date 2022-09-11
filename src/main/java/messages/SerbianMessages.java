package messages;

public class SerbianMessages implements Messages {

    @Override
    public String getInvoiceIsPaidMessage(String lastSubscriptionDay) {
        return "Faktura je uspešno plaćena. Poslednji dan pretplate je " + lastSubscriptionDay + ".";
    }
}
