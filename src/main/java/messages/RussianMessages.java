package messages;

public class RussianMessages implements Messages {

    @Override
    public String getInvoiceIsPaidMessage(String lastSubscriptionDay) {
        return "Счет успешно оплачен. Ваш последний день подписки - " + lastSubscriptionDay + ".";
    }
}
