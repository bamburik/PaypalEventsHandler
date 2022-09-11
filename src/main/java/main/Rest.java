package main;

import com.jayway.jsonpath.JsonPath;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import config.Props;
import messages.EnglishMessages;
import messages.Messages;
import messages.RussianMessages;
import messages.SerbianMessages;
import org.bson.Document;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Properties;

@RestController
public class Rest {

    private MongoClient mongoClient;
    private MongoCollection unpaidInvoices;
    private MongoCollection users;

    @PostConstruct
    public void postConstruct() {
        try (InputStream input = new FileInputStream(System.getProperty("propPath"))) {
            Properties prop = new Properties();
            prop.load(input);
            Props.setBotToken(prop.getProperty("bot.token"));
            Props.setMongoHost(prop.getProperty("mongo.host"));
            Props.setMongoPort(Integer.parseInt(prop.getProperty("mongo.port")));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        mongoClient = new MongoClient(Props.getMongoHost(), Props.getMongoPort());
    }

    @PostMapping("/invoice/paid")
    public void onPaymentRecieved(@RequestBody String newEmployee) {
        System.out.println(newEmployee);
        if (users == null) {
            users = mongoClient.getDatabase("myBotDB").getCollection("users");
        }
        if (unpaidInvoices == null) {
            unpaidInvoices = mongoClient.getDatabase("myBotDB").getCollection("unpaidInvoices");
        }
        String invoiceId = JsonPath.read(newEmployee, "$.resource.invoice.id");
        if (unpaidInvoices.count(new Document().append("invoiceLink", new Document("$regex", invoiceId))) != 0) {
            String chatId = ((Document)unpaidInvoices.find(new Document().append("invoiceLink", new Document("$regex", invoiceId))).first()).getString("chatId");
            String paymentPeriod = ((Document)unpaidInvoices.find(new Document().append("invoiceLink", new Document("$regex", invoiceId))).first()).getString("paymentPeriod");
            prolongSubscriptionFor(chatId, Integer.parseInt(paymentPeriod));
            unpaidInvoices.deleteOne(new Document().append("invoiceLink", new Document("$regex", invoiceId)));
            String lastDayOfSubscription = ((Document)users.find(new Document().append("chatId", chatId)).first()).getString("lastDayOfSubscription");
            Messages messages = getMessages(((Document)users.find(new Document().append("chatId", chatId)).first()).getString("lang"));
            TelegramBot bot = new TelegramBot(Props.getBotToken());
            String message = messages.getInvoiceIsPaidMessage(lastDayOfSubscription);
            SendMessage request = new SendMessage(chatId, message);
            bot.execute(request);
            bot.shutdown();
        }
    }

    private void prolongSubscriptionFor(String chatId, int months) {
        LocalDate currentLastDayOfSubscription = LocalDate.parse(((Document)users.find(new Document().append("chatId", chatId)).first()).getString("lastDayOfSubscription"));
        LocalDate newLastDayOfSubscription;
        if (currentLastDayOfSubscription.isAfter(LocalDate.now())) {
            newLastDayOfSubscription = currentLastDayOfSubscription.plusMonths(months);
        }
        else {
            newLastDayOfSubscription = LocalDate.now().plusMonths(months);
        }
        users.updateOne(new Document().append("chatId", chatId),
                new Document().append("$set", new Document("lastDayOfSubscription", newLastDayOfSubscription.toString())));
    }

    private static Messages getMessages(String lang) {
        switch (lang) {
            case "Srpski":
                return new SerbianMessages();
            case "English":
                return new EnglishMessages();
            case "Русский":
                return new RussianMessages();
            default:
                throw new IllegalArgumentException();
        }
    }
}
