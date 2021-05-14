package org.coreops.akka.bug;

import akka.actor.typed.ActorSystem;
import akka.management.cluster.bootstrap.ClusterBootstrap;
import akka.management.javadsl.AkkaManagement;
import com.typesafe.config.ConfigFactory;
import org.coreops.akka.bug.actors.AkkaBugMain;

public class Main {

  public static final ActorSystem<AkkaBugMain.Command> system = ActorSystem.create(AkkaBugMain.create(), "akkaBug", ConfigFactory.load());

  public static void main(String[] args) {

    AkkaManagement.get(system).start();
    ClusterBootstrap.get(system).start();

    system.tell(new AkkaBugMain.MainInit());
  }
}
