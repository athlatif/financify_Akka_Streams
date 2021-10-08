package app;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.kafka.javadsl.Producer;
import akka.stream.javadsl.Sink;
import com.mongodb.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;


import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;



import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static akka.actor.typed.javadsl.Adapter.toClassic;

public class AuditMain {

    static MongoClientURI uri = new MongoClientURI("mongodb://atheer:atheer@cluster0-shard-00-00.f5yts.mongodb.net:27017,cluster0-shard-00-01.f5yts.mongodb.net:27017,cluster0-shard-00-02.f5yts.mongodb.net:27017/myFirstDatabase?ssl=true&replicaSet=atlas-91fqd5-shard-0&authSource=admin&retryWrites=true&w=majority");
    static MongoClient mongoClient = new MongoClient(uri);


    static DB database = mongoClient.getDB("logs");
    static DBCollection collection = database.getCollection("audit_logs");

    /*

    private static Number number;

    public static CompletionStage<String> messageStructure(Integer key, String value) { // .... }

        return CompletableFuture.completedFuture(value);
    }


     */

    //public static MongoClient client;
    //public static MongoDatabase db;
    //public static MongoCollection<Number> numbersColl;

    public static CompletionStage<String>  InsertToMongoDB(Integer key, String value) throws UnknownHostException {

        DBObject person = new BasicDBObject("record", value);
        collection.insert(person);

        return CompletableFuture.completedFuture("");

    }


    public static void main(String[] args) throws UnknownHostException {

        ActorSystem actorSystem = ActorSystem.create(Behaviors.empty(),"actorSystem");




        /*

        PojoCodecProvider codecProvider = PojoCodecProvider.builder().register(Number.class).build();
        CodecRegistry codecRegistry =
                CodecRegistries.fromProviders(codecProvider, new ValueCodecProvider());


         */

        //client = MongoClients.create("mongodb+srv://atheer:atheer>@cluster0.f5yts.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
        //db = client.getDatabase("logs");
        //numbersColl = db.getCollection("audit_logs", Number.class).withCodecRegistry(codecRegistry);


        // Source Kafka

        ConsumerSettings<Integer, String> consumerSettings =
                ConsumerSettings.create(toClassic(actorSystem), new IntegerDeserializer(), new StringDeserializer())
                        .withBootstrapServers("localhost:9092")
                        .withGroupId("audit_consumer8")
                        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
                        .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
                        .withProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "5000");



        // Sink

        /*
        Consumer.plainSource(consumerSettings, Subscriptions.topics("operations_topic")).mapAsync(10, record -> messageStructure(record.key(), record.value()))
                .map(value -> {number = new Number(value);
                System.out.println(String.format("%s inserting %s", value, number.toString()));
                return number;})
                .runWith(MongoSink.insertOne(numbersColl), actorSystem);

         */



        Consumer.plainSource(consumerSettings,Subscriptions.topics("operations_topic")).mapAsync(10, record -> InsertToMongoDB(record.key(), record.value()))

                .to(Sink.foreach(
                        value -> {
                            System.out.println(value);
                        }

                )).run(actorSystem);










    }


}
