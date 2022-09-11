package messages;

public class EnglishMessages implements Messages {

    @Override
    public String getInvoiceIsPaidMessage(String lastSubscriptionDay) {
        return "Invoice is paid successfully. The last day of subscription is " + lastSubscriptionDay + ".";
    }
}
