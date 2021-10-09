package app;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.stream.javadsl.Sink;
import com.mongodb.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static akka.actor.typed.javadsl.Adapter.toClassic;

public class AuditMain {

    static MongoClientURI uri = new MongoClientURI("mongodb://atheer:atheer@cluster0-shard-00-00.f5yts.mongodb.net:27017,cluster0-shard-00-01.f5yts.mongodb.net:27017,cluster0-shard-00-02.f5yts.mongodb.net:27017/myFirstDatabase?ssl=true&replicaSet=atlas-91fqd5-shard-0&authSource=admin&retryWrites=true&w=majority");
    static MongoClient mongoClient = new MongoClient(uri);


    static DB database = mongoClient.getDB("logs");
    static DBCollection collection = database.getCollection("audit_logs");


    public static CompletionStage<String>  InsertToMongoDB(String value) {

        DBObject valueRecord = new BasicDBObject("record", value);
        collection.insert(valueRecord);

        return CompletableFuture.completedFuture("");

    }


    public static void main(String[] args)  {

        ActorSystem actorSystem = ActorSystem.create(Behaviors.empty(),"actorSystem");



        // [Source] Kafka

        ConsumerSettings<Integer, String> consumerSettings =
                ConsumerSettings.create(toClassic(actorSystem), new IntegerDeserializer(), new StringDeserializer())
                        .withBootstrapServers("localhost:9092")
                        .withGroupId("audit_consumer")
                        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
                        .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
                        .withProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "5000");



        // [Sink] MongoDB


        Consumer.plainSource(consumerSettings,Subscriptions.topics("operations_topic")).mapAsync(10, record -> InsertToMongoDB(record.value()))
                .to(Sink.foreach(
                        value -> {
                            System.out.println(value);
                        }

                )).run(actorSystem);










    }


}
