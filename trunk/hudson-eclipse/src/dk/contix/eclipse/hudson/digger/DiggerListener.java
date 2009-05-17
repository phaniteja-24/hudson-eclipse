package dk.contix.eclipse.hudson.digger;

import java.util.Collection;

public interface DiggerListener {

	public void serversFound(Collection<HudsonServer> servers);
}
