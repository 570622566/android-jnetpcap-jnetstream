/**
 * Copyright (C) 2007 Sly Technologies, Inc. This library is free software; you
 * can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version. This
 * library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.jnetstream.protocol;

import java.util.Set;

import org.jnetstream.packet.Header;
import org.jnetstream.packet.ProtocolFilterTarget;
import org.jnetstream.protocol.codec.Codec;
import org.jnetstream.protocol.codec.HeaderCodec;
import org.jnetstream.protocol.codec.Scanner.Scandec;

import com.slytechs.utils.memory.BitBuffer;
import com.slytechs.utils.namespace.Named;

/**
 * <P>
 * Defines access to a Protocol. Protocols are supplied to help deal with
 * disecting raw packet byte buffers, analyzing protocol specific structure and
 * state. Also verify protocol integrity and check for errors.
 * <P>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc
 * @since 0.2.4
 */
public interface ProtocolInfo<T extends Header> {

	public static final int NO_BIT_INDEX = 0;

	/**
	 * Gets a list of all the protocols this protocol references either inside a
	 * codec or a binding.
	 * 
	 * @return list of all the referenced protocols
	 */
	public Set<ProtocolInfo<? extends Header>> getProtocolReferences();

	/**
	 * Binding between protocols. Describes the conditions of how one protocol is
	 * bound to another. Typically in a packet, each protocol provides a header to
	 * describe its state. The headers are stacked one after the other. The
	 * previous protocol usually, but not neccessarily carries informaton about
	 * what next protocol should follow it. This information, sometimes in a form
	 * of a complex expression, is evaluated against a runtime version of a
	 * packet.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public interface Binding extends Scandec {

		/**
		 * List of protocols that this binding references and therefore is dependent
		 * on.
		 * 
		 * @return list of dependent protocols
		 */
		public Set<ProtocolInfo<? extends Header>> getProtocolReferences();

		/**
		 * Returns the sink protocol of this binding.
		 * 
		 * @return the sink protocol
		 */
		public ProtocolInfo<? extends Header> getSink();

		/**
		 * Returns the source protocol of this binding. Source protocol is the
		 * protocol to which this binding is attached to.
		 * 
		 * @return the source protocol
		 */
		public ProtocolInfo<? extends Header> getSource();

		/**
		 * Hit counter for this binding. Every time the binding resolves to a
		 * protocol match, the hit counter is incremented. Then every time the
		 * resolution fails, the hit counter is decremented, unless the counter is
		 * at zero, at which point its not decremented any further.
		 * <p>
		 * The hit counter is used to prioritize protocol bindings. The bindings are
		 * ordered according to their hit counter. This ensures that a binding that
		 * is used more often is at the top of the binding list and has a chance to
		 * be matched before any other.
		 * </p>
		 * 
		 * @return current hit counter which is never a negative value
		 */
		public int getHitCounter();
	}

	/**
	 * Defines a unique identification of a protocol. There are 2 forms of
	 * identification possible. The first is a unique string. The protocol name
	 * should include company or organization or suite name in a form of a java
	 * package and class naming conventions.
	 * <p>
	 * For example <em>protocols.lan.Ethernet</em> describes uniquely Ethernet 2
	 * protocol. <em>com.slytechs.protocols.jnetstream.server</em> uniqueuely
	 * identifies the custom protocol utilized by the jNetStream's RemoteServer
	 * communication. The string does not signify that there exists such a real
	 * package or class. It is simply means of unqiuely assigning, non conflicting
	 * names that carry the organization which is responsible for the protocol.
	 * </p>
	 * <p>
	 * The integer based ID is generated based on the above metioned unique string
	 * based id. If the string ID changes the numerical ID will change as well.
	 * The integer ID is a complex hash of the string, not the hash function
	 * provided by java's implementation of hash on string, but a more refined
	 * function that guarrantees uniqueness for each different string and also
	 * guarrantees that the id will always be the same for the same string on any
	 * platform.
	 * </p>
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public interface ID extends ProtocolFilterTarget {

		/**
		 * A java style ID as a string that uniquely identifies the protocol
		 * 
		 * @return id string
		 */
		public String getIdString();

		/**
		 * Overrides and implements a more efficient and stringent hash value. The
		 * hash value is guarranteed never to match a hash of another ID on the same
		 * java VM. For example, the implementations may (or may not) use static
		 * tables to store all of the registered IDs and use indexes into the tables
		 * as hash values. The hash value is not persinstant between different
		 * invocations of a java VM. It is used only as a fast runtime property of a
		 * protocol that can be used for comparison between protocols.
		 * 
		 * @return a unique hash value guarranteed to be unique on this java VM
		 */
		public int hashValue();

		public <T extends Header> ProtocolInfo<T> getProtocol();

		/**
		 * @return
		 */
		public String getName();

		/**
		 * Returns the type, as an instance of a class type, which is also a unique
		 * type for this protocol.
		 * 
		 * @return class file of this protocol
		 */
		public Class<? extends Header> getType();

		/**
		 * Returns a special bit index for core protocols. Bit indexes are used to
		 * significantly speedup the process of caching certain protocol information
		 * within a packet. Only the core protocols can provide a bit index. All
		 * other non-core protocols must return value of 0.
		 * 
		 * @return bit index if this is a core protocol, otherwise 0
		 */
		public int bitIndex();
	}

	/**
	 * Informational interface which provides information about the protocol. Info
	 * objects are usually defined in codes where each protocol collects all info
	 * objects from each codec and uses combined list of info objects to present
	 * information about the protocol.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public interface Info extends Named {
		public String getDescription();

	}

	/**
	 * Protocol registry factory methods. Protocol registry maintains a map of all
	 * known protocols and bindings.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public interface Registry {

		/**
		 * @param other
		 * @param ch
		 * @param const1
		 * @return
		 */
		public ProtocolInfo<? extends Header> newProtocol(ID id, Suite other,
		    Set<Characteristic> ch);

		/**
		 * @param dlt
		 * @return
		 */
		public ProtocolInfo<? extends Header> getProtocol(ID dlt);

		public <T extends Header> ProtocolInfo<T> getProtocol(Class<T> c);

		/**
     * @param name
     * @return
     */
    public ProtocolInfo<? extends Header> getProtocol(String name);

		/**
     * @param set
     * @return
     */
    public Set<ProtocolInfo<? extends Header>> getProtocols(Set<String> set);

	}

	/**
	 * Enum structure which defines all of the jNetStream system defined protocol
	 * suites. These protocol suites are guarranteed to exist and do not reflect
	 * any user defined protocol suites. New protocols that can not be placed
	 * under any of the predefined protocol suites, should be be placed under
	 * {@link ProtocolInfo.Suite#Other}.
	 */
	public enum Suite {

		/**
		 * Cisco Systems Corp protocols
		 */
		Cisco,

		/**
		 * Capture file format defintions. This suite contains defintions for all
		 * the known capture file formats. There are 3 things that are special about
		 * definitions in this suite:
		 * <UL>
		 * <LI> They are all bound to a constant name "CaptureFile"
		 * <LI> The naming convention is based on a fact that each definition is
		 * made up of two components. The file header (1 NPL definition) and a
		 * record header (a second NPL definition). The file header can be named
		 * anything, but must be linked to "CaptureFile". The record header must be
		 * of the same base name as the file header with a post fix of the string
		 * literal "_Record".
		 * <LI> The last special consideration for capture file defintions that
		 * either the File header or record header definitions must export the
		 * following properties (in NPL definition written in NPL language) which
		 * are then picked up by the jNetStream runtime environment and used for
		 * accessing packet data from capture files.
		 * <UL>
		 * <LI> property local packet.capture_linktype
		 * <LI> property local packet.capture_snaplen
		 * <LI> property local packet.capture_recordlen
		 * <LI> property local packet.capture_seconds
		 * <LI> property local packet.capture_nanos
		 * </UL>
		 * </UL>
		 * It does not matter the order in which these properties are exported from
		 * from exactly which header (file or record headers) as long as all these
		 * values are exported, runtime environment has enough information to
		 * process the file and decode all the headers within the packet (using the
		 * linktype property.)
		 */
		File,

		/**
		 * HP and old Dec protocols. HP purchased Dec.
		 */
		HP,

		/**
		 * IBM protocols, NetBEUI, etc..
		 */
		IBM,

		/**
		 * Intel Corp protocols
		 */
		Intel,

		/**
		 * ISO protocols
		 */
		ISO,

		/**
		 * Datalink Local Area Network level protocols.
		 */
		LAN,

		/**
		 * Datalink Metropolitan Area Network level protocols
		 */
		MAN,

		/**
		 * Microsoft protocols, CIFS, etc...
		 */
		Microsoft,

		/**
		 * Network OSI layer protocols such as IP and others.
		 */
		Network,

		/**
		 * Novell defined protocols, IPX, etc..
		 */
		Novell,

		/**
		 * Everything else not nice placed into a suite.
		 */
		Other,

		/**
		 * Storage Area Network protocols, Fiberchannel etc..
		 */

		SAN,

		/**
		 * Security releated, Radius, Kerberos, etc...
		 */
		Security,

		/**
		 * Telecom industry protocols for land and wireless standards
		 */
		SS7,

		/**
		 * Sun Microsystems protocols
		 */
		Sun,

		/**
		 * TCP/IP suite of protocols, mainly higher level upto application layer
		 */
		TCPIP,

		/**
		 * Voice over IP protocols and other realtime protocols, RTP, SIP, etc..
		 */
		VOIP,

		/**
		 * Datalink Wide Area Network level protocols
		 */
		WAN,

		/**
		 * Wireless protocols, 802.11a/b/g .
		 */
		Wireless, ;
	}

	/**
	 * Returns all available codecs for this protocol. All the available codecs
	 * have already been loaded into memory.
	 * 
	 * @return array of currently available codecs
	 * @see #isLoaded()
	 */
	public Codec[] getAvailableCodecs();

	/**
	 * Returns the current Codec that is responsible for Encoding/Decoding the
	 * packet's data buffer to a decomposed state accessible using the Packet
	 * public API. Each codec is assigned a priority which determines which one is
	 * actively used, if more than one codec exists for a protocol. You can check
	 * codec priority with <code>getPriority</code> and change it using
	 * <code>setPriority</code>
	 * 
	 * @return the currently assigned codec
	 * @see HeaderCodec#getPriority()
	 * @see HeaderCodec#setPriority(org.jnetstream.protocol.codec.HeaderCodec.Priority)
	 */
	public HeaderCodec<? extends Header> getCodec();

	/**
	 * Returns the name of this protocol. Protocol names are unique.
	 * 
	 * @return name of this protocol
	 * @see ID#getIdString()
	 */
	public String getName();

	/**
	 * Gets the ID, Identification object which uniquely identifies this protocol.
	 * Each protocol is assigned a unique ID object and a unique integer hash
	 * value. The ID structure can be used to uniquely identify a protocol.
	 * 
	 * @return ID structure that uniquely identifies this protocol
	 */
	public ID getID();

	/**
	 * Returns the protocol suite this protocol belongs to. Many protocol suites
	 * have been predefined for all standard and common protocols.
	 * 
	 * @see ProtocolInfo.Suite
	 * @return protocol suite of this protocol
	 */
	public Suite getSuite();

	/**
	 * Checks if any of the current protocol's codecs have been loaded. Each
	 * protocol can have a number of codecs assigned to it. The codecs are first
	 * assigned by a reference ID, but not neccessarily loaded into memory. When
	 * the codec is needed for decoding of a packet, and its not currently loaded,
	 * the protocol will request that the codec be loaded into memory. This may
	 * take a slight amount of time to do, but only needs to be done once. After
	 * the codec has been loaded it remains in memory and is available for
	 * immediate utilization.
	 * 
	 * @return true if any of the protocol's codecs has been loaded, otherwise
	 *         false.
	 * @see #pushCodec(HeaderCodec)
	 */
	public boolean isLoaded();

	/**
	 * Pushes codec on to a prioritised queue where the highest priority codec is
	 * used for actively decoding header content. Each codec is assigned a
	 * priority which determines which one of the codecs is currently active.
	 * 
	 * @param codec
	 *          codec to push on to the priority queue list
	 * @see HeaderCodec#getPriority()
	 * @see HeaderCodec#setPriority(org.jnetstream.protocol.codec.HeaderCodec.Priority)
	 */
	public void pushCodec(HeaderCodec<? extends Header> codec);

	/**
	 * Gets a set of special characteristics for this protocol. The set contains
	 * various types of characteristics such as Frame and Physical. Each class of
	 * characteristics is defined in its own enum structure which can be used to
	 * check against the set.
	 * 
	 * <pre>
	 * Protocol ethernet = // from some source
	 * Set&lt;Characteristics&gt; ch = ethernet.getCharacteristics();
	 * if (ch.contains(Frame.HasCRC)) {
	 *   // Do something
	 * } else if (ch.contains(Physical.Serial)) {
	 *   // Do something with a serial line based protocol
	 * }
	 * </pre>
	 * 
	 * @return a set containing any special characteristics that this protocol has
	 */
	public Set<Characteristic> getCharacteristics();

	/**
	 * Retrieves various characteristics for this protocol.
	 * 
	 * @return a 32-bit unsigned integer value whos bits are encoded with various
	 *         characteristics for this protocol
	 * @see #getCharacteristics() method for description of how decode
	 *      characteristic enum constants out of the integer representation
	 */
	public int getCharacteristicsValue();

	/**
	 * Forces the protocol to reprioritize its bindings. Each binding keeps a hit
	 * counter which is incremented when a binding succeeds and decremented when
	 * it fails. The hit counter is used to place bindings that are successful
	 * more often at the front of the list. This allows a more popular binding to
	 * be at the top of the list which is more efficient in most cases. The
	 * protocol automatically reprioritises the binding list according to binding
	 * prioritisation weight <code>setBindingWeight</code>.
	 * 
	 * @return true means that changes did occur as a result of the
	 *         prioritization, otherwise false
	 * @see #setBindingWeight(int)
	 */
	public boolean prioritiseBindings();

	/**
	 * Changes the prioritisation weight of bindings for this protocol. The lower
	 * the value, the more often the protocol will reprioritize the bindings
	 * according to their usage.
	 * 
	 * @see #prioritiseBindings()
	 * @param weight
	 *          A non negative value specifying the priority weight. The lower the
	 *          value the more often reprioritization algorithm is run. A value of
	 *          zero turns off prioritization completely.
	 * @return the previous weight value
	 */
	public int setBindingWeight(int weight);

	public void addBinding(ProtocolInfo.Binding binding);

	/**
	 * Gets all of the bindings current attached to this protocol
	 * @return array of bindinds
	 */
	public Binding[] getBindings();

	/**
	 * @return
	 */
	public Class<T> getType();

	/**
	 * Returns a special bit index for core protocols. Bit indexes are used to
	 * significantly speedup the process of caching certain protocol information
	 * within a packet. Only the core protocols can provide a bit index. All other
	 * non-core protocols must return value of 0.
	 * 
	 * @return bit index if this is a core protocol, otherwise 0
	 */
	public int getBitIndex();

	/**
	 * Method which checks if this protocol is actually a protocol group. Protocol
	 * groups do not have any codecs, only pure bindings. All CODEC related
	 * methods are disabled, therefore it is required to check first if this
	 * protocol is a normal or a group protocol.
	 * 
	 * @return true means that this is a protocol group, otherwise false
	 */
	public boolean isProtocolGroup();

	/**
   * @param bits
	 * @param offset TODO
   * @return
   */
  public int getLength(BitBuffer bits, int offset);
}
