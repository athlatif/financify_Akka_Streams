package app;

import akka.Done;
import akka.NotUsed;
import akka.actor.typed.ActorSystem;
import static akka.actor.typed.javadsl.Adapter.*;
import akka.actor.typed.javadsl.Behaviors;
import akka.kafka.CommitterSettings;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Committer;
import akka.kafka.javadsl.Consumer;
import akka.stream.javadsl.Keep;
import com.typesafe.config.Config;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;


public class TraderMain {

    public static void main(String[] args){


       ActorSystem<Void> actorSystem = ActorSystem.create(Behaviors.ignore(),"name");

       ConsumerSettings<Integer, String> consumerSettings =
                ConsumerSettings.create(toClassic(actorSystem), new IntegerDeserializer(), new StringDeserializer())
                        .withBootstrapServers("localhost:9092")
                        .withGroupId("trader1")
                        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
                        .withStopTimeout(Duration.ofSeconds(5));

        String topic = "foobar";


        Consumer.DrainingControl<Done> control = Consumer.sourceWithOffsetContext(consumerSettings, Subscriptions.topics(topic))
                .map(record -> {System.out.println(record.value());
                            return NotUsed.notUsed();
                        }
                )
                .toMat(Committer.sinkWithOffsetContext(CommitterSettings.create(toClassic(actorSystem))), Keep.both()) // (9)
                .mapMaterializedValue(Consumer::createDrainingControl)
                //.to(Sink.foreach(it -> System.out.println("Done with " + it)))
                .run(actorSystem);
        CompletionStage<Done> copyingFinished = control.drainAndShutdown(actorSystem.executionContext());
        copyingFinished.whenComplete((result, ex) -> System.out.println("stage 2: "+result+"\t"+ex)
        );

    }





}
