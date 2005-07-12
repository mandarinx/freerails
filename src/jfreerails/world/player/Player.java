package jfreerails.world.player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import jfreerails.world.common.FreerailsSerializable;

/**
 * Represents a player within the game. The player model is such that a user can
 * start a client, create a new player on the server and start playing. They can
 * disconnect from the server, which may continue running with other players
 * still active. The server can then save the list of players and be stopped and
 * restarted again, the clients can then authenticate themselves to the server
 * and continue their sessions where they left off.
 * 
 * XXX the player is only authenticated when the connection is opened, and
 * subsequent exchanges are not authenticated.
 * 
 * TODO implement a more complete authentication system using certificates
 * rather than public keys.
 * 
 * @author rtuck99@users.sourceforge.net
 */
public class Player implements FreerailsSerializable {
	private static final long serialVersionUID = 1;

	/** A FreerailsPrincipal that is not a player. */
	private static class WorldPrincipal extends FreerailsPrincipal {
		private static final long serialVersionUID = 1;

		private final String m_name;

		public WorldPrincipal(String name) {
			m_name = name;
		}

		public String getName() {
			return m_name;
		}

		public String toString() {
			return m_name;
		}

		public int hashCode() {
			return m_name.hashCode();
		}

		public boolean equals(Object o) {
			if (!(o instanceof WorldPrincipal)) {
				return false;
			}

			return (m_name.equals(((WorldPrincipal) o).m_name));
		}
	}

	private FreerailsPrincipal principal;

	/**
	 * This Principal can be granted all permissions.
	 */
	public static final FreerailsPrincipal AUTHORITATIVE = new WorldPrincipal(
			"Authoritative Server");

	/**
	 * This Principal has no permissions.
	 */
	public static final FreerailsPrincipal NOBODY = new WorldPrincipal("Nobody");

	/**
	 * Name of the player.
	 */
	private final String name;

	/**
	 * Private data (eg private keys) that should not be serialized in normal
	 * use. Instead, when the client needs to save their session they should
	 * call saveSession().
	 */
	// private/* =mutable */transient PrivateData privateData;
	/**
	 * This is the clients public key.
	 */
	// private PublicKey publicKey;
	/**
	 * Used by the client to generate a player with a particular name.
	 */
	public Player(String name) {
		this.name = name;

		KeyPairGenerator kpg;

		/* generate our key pair */
		try {
			kpg = KeyPairGenerator.getInstance("DSA");
			kpg.initialize(1024);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Used by the server to generate a player with a particular name and public
	 * key.
	 * 
	 * @param publicKey
	 *            the client's public key. certificate.
	 */
	public Player(String name, int id) {
		this.name = name;
		// this.publicKey = publicKey;
		// privateData = new PrivateData();
		this.principal = new PlayerPrincipal(id, name);
	}

	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}

		if (!(o instanceof Player)) {
			return false;
		}

		// return (name.equals(((Player) o).name) && keysEqual);
		return (name.equals(((Player) o).name));
	}

	public int hashCode() {
		return name.hashCode();
	}

	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	/**
	 * TODO save this player's private data so that they can be re-connected to
	 * the server at a later point in time.
	 */
	public void saveSession(ObjectOutputStream out) throws IOException {
		// out.writeObject(privateData);
	}

	/**
	 * Called by the client to reconstitute the data from a saved game.
	 */
	public void loadSession(ObjectInputStream in) throws IOException {
		// try {
		// privateData = (PrivateData) in.readObject();
		// } catch (ClassNotFoundException e) {
		// throw new IOException("Couldn't find class:" + e);
		// }
	}

	public FreerailsPrincipal getPrincipal() {
		return principal;
	}
}