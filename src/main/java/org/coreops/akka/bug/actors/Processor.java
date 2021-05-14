package org.coreops.akka.bug.actors;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Processor {

  public static final String SERVICE_KEY_PREFIX = "/user/buggy/processor/";
  private final ActorContext<Msg> context;

  public static Behavior<Msg> create(String msgId) {

    ServiceKey<Msg> serviceKey = ServiceKey.create(Msg.class, Processor.SERVICE_KEY_PREFIX + msgId);
    return Behaviors.setup(
        context -> {
          context
              .getSystem()
              .receptionist()
              .tell(Receptionist.register(serviceKey, context.getSelf()));
          context.getLog().info("Path: {}", context.getSelf().path());
          return new Processor(context).behavior();
        });
  }

  /**
   * Gets the Behavior for this actor.
   */
  public Behavior<Msg> behavior() {
    return Behaviors.receive(Msg.class)
        .onMessage(Msg.class, this::onReceive)
        .build();
  }

  private Behavior<Msg> onReceive(Msg msg) {
    context.getLog().info("Received: " + msg);
    return Behaviors.same();
  }


  @AllArgsConstructor
  public static class Msg {
    private String msgId;
    private String message;
  }
}
