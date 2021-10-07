package app;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.kafka.ProducerSettings;
import akka.kafka.javadsl.Producer;
import akka.stream.javadsl.Source;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Arrays;
import java.util.List;

import static akka.actor.typed.javadsl.Adapter.toClassic;

public class QuoteGenerator {

    public static void main(String[] args){

        // Create Actor System

        ActorSystem actorSystem = ActorSystem.create(Behaviors.empty(),"actorSystem");

        // Kafka Producer Settings

        ProducerSettings<String, String> producerSettings =
                ProducerSettings.create(toClassic(actorSystem),  new StringSerializer(), new StringSerializer())
                        .withBootstrapServers("localhost:9092");


        // Random Values
        List<Integer> RandomBids = Arrays.asList(new Integer[]{3000,64567,500,1000,20});

        // Create a Source [Random numbers], and a Sink [Kafka Producer]
        Source.cycle(() -> {return RandomBids.iterator();})
                .map(number -> number.toString())
                .map(value -> new ProducerRecord<String, String>("bids_topic", value))
                .runWith(Producer.plainSink(producerSettings), actorSystem);


    }
}
