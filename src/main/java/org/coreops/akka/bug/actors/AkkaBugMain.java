package org.coreops.akka.bug.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.Receptionist;
import lombok.AllArgsConstructor;

public class AkkaBugMain {

  private final ActorContext<Command> context;

  public AkkaBugMain(ActorContext<Command> context) {
    this.context = context;
  }

  /**
   * Creates {@link Behavior} for this actor.
   *
   * @return The created Behavior
   */
  public static Behavior<Command> create() {
    return Behaviors.setup(context -> {
      context.getLog().info("Path: {}", context.getSelf().path());
      return new AkkaBugMain(context).behavior();
    });
  }

  private Behavior<Command> behavior() {
    return Behaviors.receive(Command.class).onMessage(MainInit.class, notUsed -> onBegin()).build();
  }

  private Behavior<Command> onBegin() {
    ActorRef<ProcessorParent.Command> processorParent = context.spawn(ProcessorParent.create(), "processor-parent");
    context.getLog().info("Spawned CreditManager: {}", processorParent);
    processorParent.tell(new ProcessorParent.Msg("1", "First Message"));
    processorParent.tell(new ProcessorParent.Msg("2", "Second Message"));
    processorParent.tell(new ProcessorParent.Msg("3", "Third Message"));
    processorParent.tell(new ProcessorParent.Msg("4", "Fourth Message"));
    processorParent.tell(new ProcessorParent.Msg("5", "Fifth Message"));
    processorParent.tell(new ProcessorParent.Msg("6", "Sixth Message"));
    return Behaviors.same();
  }

  public interface Command {
  }

  /**
   * MainInit message class. Used to initialize this actor system's activities.
   */
  public static class MainInit implements Command {
  }

  @AllArgsConstructor
  public static class ListingResponse implements Command {
    private Receptionist.Listing listing;
    private ProcessorParent.Msg msg;
  }
}
