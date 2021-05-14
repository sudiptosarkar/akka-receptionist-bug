package org.coreops.akka.bug.actors;

import akka.actor.InvalidActorNameException;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;

import java.util.Optional;

public class ProcessorParent {

  public static final ServiceKey<Command> serviceKey = ServiceKey.create(Command.class, "ProcessorParent");
  private final ActorContext<Command> context;
  private static final Gson gson = new GsonBuilder().create();

  public ProcessorParent(ActorContext<Command> context) {
    this.context = context;
  }

  /**
   * Creates {@link Behavior} for this actor.
   *
   * @return The credted Behavior
   */
  public static Behavior<Command> create() {

    return Behaviors.setup(
        context -> {
          context
              .getSystem()
              .receptionist()
              .tell(Receptionist.register(serviceKey, context.getSelf()));
          context.getLog().info("Path: {}", context.getSelf().path());
          return new ProcessorParent(context).behavior();
        });
  }

  /**
   * Gets the Behavior for this actor.
   */
  public Behavior<Command> behavior() {
    return Behaviors.receive(Command.class)
        .onMessage(ListingResponse.class, this::onListing)
        .onMessage(Msg.class, this::onReceive)
        .build();
  }

  private Behavior<Command> onListing(ListingResponse response) {
    String msgId = response.message.msgId;
    ServiceKey<Processor.Msg> tmpSkey = ServiceKey.create(Processor.Msg.class, Processor.SERVICE_KEY_PREFIX + msgId);
    context.getLog().info("[{}] {} - Received from Receptionist: {}", Thread.currentThread().getName(), context.getSelf().path(), gson.toJson(response.message));
    // Optional<ActorRef<Processor.Msg>> optionalActor = response.listing.getServiceInstances(tmpSkey).stream()
    //    .filter(s -> s.path().name().equalsIgnoreCase(msgId)).findFirst();
    //context.getLog().debug("Credit Actor Present: {}", optionalActor.isPresent());
    //try {
    //  ActorRef<Processor.Msg> actorRef = optionalActor.orElseGet(() -> context.spawn(Processor.create(msgId), msgId));
    //  actorRef.tell(new Processor.Msg(msgId, response.message.message));
    //} catch (InvalidActorNameException e) {
      // Do the lookup again.
    //  context.getLog().debug("Looking up again...");
    //  context.getSystem().receptionist()
    //      .tell(Receptionist.find(tmpSkey, context.messageAdapter(Receptionist.Listing.class, listing -> new ListingResponse(listing,
    //          response.message))));
    //}
    return Behaviors.same();
  }

  private Behavior<Command> onReceive(Msg message) {
    context.getLog().info("[{}] {} - Received: {}", Thread.currentThread().getName(), context.getSelf().path(), gson.toJson(message));
    ServiceKey<Processor.Msg> tmpSkey = ServiceKey.create(Processor.Msg.class, Processor.SERVICE_KEY_PREFIX + message.msgId);
    context.getSystem().receptionist()
        .tell(Receptionist.find(tmpSkey, context.messageAdapter(Receptionist.Listing.class, listing -> new ListingResponse(listing, message))));
    return Behaviors.same();
  }

  public interface Command {
  }

  @AllArgsConstructor
  public static class Msg implements Command {
    private final String msgId;
    private final String message;
  }

  @AllArgsConstructor
  private static class ListingResponse implements Command {
    private final Receptionist.Listing listing;
    private final Msg message;
  }
}
