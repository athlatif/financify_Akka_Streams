package app;

import akka.Done;
import akka.NotUsed;
import akka.actor.typed.ActorSystem;
import static akka.actor.typed.javadsl.Adapter.*;
import akka.actor.typed.javadsl.Behaviors;
import akka.kafka.CommitterSettings;
import akka.kafka.ConsumerSettings;
import akka.kafka.ProducerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Committer;
import akka.kafka.javadsl.Consumer;
import akka.kafka.javadsl.Producer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import com.typesafe.config.Config;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;


public class TraderMain {

    public static Integer Current_Balance = 3000;


    public static CompletionStage<String>  TraderStratgy(Integer key, String value) { // .... }
        // #atMostOnce #atLeastOnce
        Integer casted_value = Integer.parseInt(value);
        if(!(casted_value > Current_Balance)){

            if(casted_value > (Current_Balance/2)){
                value = "Sell";
                Current_Balance = Current_Balance + casted_value;
            }else{
                value = "buy";
                Current_Balance = Current_Balance - casted_value;
            }

        } else {
            value = "ignore";
        }
        return CompletableFuture.completedFuture("Operation: " + value + " Current Balance: " + Integer.toString(Current_Balance));
    }

    public static void main(String[] args){



        ActorSystem actorSystem = ActorSystem.create(Behaviors.empty(),"actorSystem");

        ProducerSettings<String, String> producerSettings =
                ProducerSettings.create(toClassic(actorSystem),  new StringSerializer(), new StringSerializer())
                        .withBootstrapServers("localhost:9092");

       ConsumerSettings<Integer, String> consumerSettings =
                ConsumerSettings.create(toClassic(actorSystem), new IntegerDeserializer(), new StringDeserializer())
                        .withBootstrapServers("localhost:9092")
                        .withGroupId("trader_cosumer")
                        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
                        .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
                        .withProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "5000");


        String topic = "bids_topic";


        // #atLeastOnce

         Consumer.plainSource(consumerSettings,Subscriptions.topics(topic)).mapAsync(10, record -> TraderStratgy(record.key(), record.value()))
                 .map(value -> new ProducerRecord<String, String>("operations_topic", value))
                 .runWith(Producer.plainSink(producerSettings), actorSystem);



         // to print out
        /*

                        .to(Sink.foreach(
                value -> {
                    System.out.println(value);
                }

        )).run(actorSystem);
         */


    }





}
