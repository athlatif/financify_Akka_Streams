package archive;

import akka.Done;
import akka.NotUsed;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.kafka.ConsumerSettings;
import akka.kafka.ProducerSettings;
import akka.kafka.javadsl.Producer;
import akka.stream.impl.Timers;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.typesafe.config.Config;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import static akka.actor.typed.javadsl.Adapter.toClassic;

public class test {

    /*
    - testing Akka Streams for the first time

     */
    public static void main(String[] args){




        /*

       // ===================== Test Quote Producer ========================

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



*/


        // Create Source

        //
        //Iterator<Integer> infinteRange = Stream.iterate(0,i->i+50).iterator();

       // Source<Integer, NotUsed> mysource = Source.range(1,10);
   /*     Source<Integer, NotUsed> mysource2 = Source.cycle(
                () -> {return RandomBids.iterator();}

        );


    */
/*
        Source<Integer, NotUsed> mysource3 = Source.fromIterator(
                () -> infinteRange
        ).throttle(1, Duration.ofSeconds(3));
        // Create Flow
        Flow<Integer, String, NotUsed> flow = Flow.of(Integer.class).map(
                value -> {
                    return  "The next value is " + value;
                }
        );

        // Create Sink
        Sink<String, CompletionStage<Done>> sink = Sink.foreach(
                value -> {
                    System.out.println(value);
                }

        );

        // create a graph
        RunnableGraph<NotUsed> graph = mysource3.via(flow).to(sink);

        // create an actor system
        // in akka streams you create an actor system without the need to create actors
        // and the behaivor of each actor is gonna be .empty()



        // run the graph

        graph.run(actorSystem);





*/


    }
}
