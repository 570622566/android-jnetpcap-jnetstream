package org.jnetstream.protocol;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Describes a characteristic of protocol. The protocol characteristic can also
 * be encoded as bits either set or cleared within an 32-bit unsigned integer.
 * The bits and their meanings are constant and reproducable. The protocol
 * registry can reverse the integer value into a charactericts set.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public interface Characteristic {

	/**
	 * A special utility method which can encode and decode a set of
	 * characteristics into a bitwise representation in a 32-bit unsigned integer.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public static class BitCodec {

		/**
		 * Decodes the unsigned uninteger into a set containing all of the various
		 * constants.
		 * 
		 * @param value
		 *          value with the bit encoded characteristics
		 * @return a set with the decoded constants
		 */
		public static Set<? extends Characteristic> valueOf(final int value) {

			final Set<Characteristic> set = new HashSet<Characteristic>();

			set.addAll(Frame.valueOf(value));
			set.addAll(Physical.valueOf(value));
			set.addAll(Kind.valueOf(value));

			return set;

		}

		/**
		 * Encodes characteristic contants into an unsigned 32-bit integer. Each bit
		 * represents a particular characteristic contants.
		 * 
		 * @param chars
		 *          a set of characteristic contants to encode into unsigned integer
		 * @return unsigned 32-bit integer with the constants encoded
		 */
		public static int valueOf(Set<Characteristic> chars) {
			int mask = 0;
			for (Characteristic c : chars) {
				mask |= c.getValue();
			}

			return mask;
		}
	}

	/**
	 * Describes frame characteristics of a protocol. Can be encoded into its
	 * 27-24 of a 32-bit unsigned integer.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public enum Frame implements Characteristic {
		/**
		 * Frame has CRC field at the end of the frame
		 */
		HasCrc(0x02000000),

		/**
		 * Frame contains 8 byte preamble at the front of the frame
		 */
		HasPreamble(0x01000000),

		/**
		 * No special frame characteristics present
		 */
		None(0x00000000), ;

		private final int value;

		private Frame(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}

		/**
		 * Returns an efficient EnumSet of all the frame characteristics that are
		 * encoded into the integer value.
		 * 
		 * @param value
		 *          integer value with bit encoded frame properties
		 * @return a set containing the decoded constants
		 */
		public static Set<? extends Characteristic> valueOf(int value) {
			final Set<Characteristic.Frame> set = EnumSet
			    .noneOf(Characteristic.Frame.class);

			for (Characteristic.Frame c : values()) {
				if ((c.value & value) != 0) {
					set.add(c);
				}
			}

			return set;
		}
	}

	/**
	 * Describes the physical characteristics of the network. Encoded into bits
	 * 30-27 of a 32-bit unsigned integer.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public enum Physical implements Characteristic {
		/**
		 * Ethernet
		 */
		Cdma(0x20000000),

		/**
		 * Higher-level protocols that have no physical medium
		 */
		None(0x00000000),

		/**
		 * Radio based physical networks
		 */
		Rf(0x40000000),

		/**
		 * Ring topology, Sonet, FDDI, Token-Ring
		 */
		Ring(0x08000000),

		/**
		 * Slip, Wan Links
		 */
		Serial(0x10000000),

		;

		private final int value;

		private Physical(int value) {
			this.value = value;
		}

		/**
		 * @return the value
		 */
		public final int getValue() {
			return this.value;
		}

		/**
		 * Returns an efficient EnumSet of all the physical characteristics that are
		 * encoded into the integer value.
		 * 
		 * @param value
		 *          integer value with bit encoded physical properties
		 * @return a set containing the decoded constants
		 */
		public static Set<? extends Characteristic> valueOf(int value) {
			final Set<Characteristic.Physical> set = EnumSet
			    .noneOf(Characteristic.Physical.class);

			for (Characteristic.Physical c : values()) {
				if ((c.value & value) != 0) {
					set.add(c);
				}
			}

			return set;
		}
	}

	/**
	 * Kind of protocol this is. A table of constants that define the kind of
	 * protocol as a characteristic. The last bit in the 32-bit unsigned integer
	 * determines has the kind of protocol. When set its a group protocol, if not
	 * set its a dissector.
	 * 
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 */
	public enum Kind implements Characteristic {

		/**
		 * The protocol is a dissector kind of a protocol and contains codecs for
		 * encoding and dissecting protocols.
		 */
		Dissector(0x0),

		/**
		 * The protocol is a protocol group which only contains binding information
		 * and produces no real header that is exported to the packet.
		 */
		Group(0x80000000);

		private final int value;

		private Kind(int value) {
			this.value = value;
		}

		/**
		 * @return the value
		 */
		public final int getValue() {
			return this.value;
		}

		/**
		 * Returns as set the kind characteristic of this protocol. Only a single
		 * characteristic of type kind can be assigned, therefore the returned set
		 * will always contain a single value.
		 * 
		 * @param value
		 *          integer value with bit encoded kind characteristic
		 * @return a set containing the decoded constants
		 */
		public static Set<? extends Characteristic> valueOf(int value) {
			final Set<Characteristic.Kind> set = EnumSet
			    .noneOf(Characteristic.Kind.class);

			if ((value & Group.value) == Group.value) {
				set.add(Group);
			}

			return set;
		}
	}

	public int getValue();
}