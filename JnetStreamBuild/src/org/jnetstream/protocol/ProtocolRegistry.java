/**
 * Copyright (C) 2006 Mark Bednarczyk This library is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version. This library is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.jnetstream.protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jnetstream.capture.FileCapture;
import org.jnetstream.capture.FilePacket;
import org.jnetstream.packet.Header;
import org.jnetstream.packet.Packet;
import org.jnetstream.packet.PacketFactory;
import org.jnetstream.protocol.codec.HeaderCodec;
import org.jnetstream.protocol.codec.Scanner;

import com.slytechs.utils.factory.FactoryLoader;
import com.slytechs.utils.module.Initializer;

/**
 * <P>
 * Main protocol registry. The registry contains the master list of all known
 * protocols and their bindings. You can iterate through each of the known
 * protocols or iterate through all of the protocol bindings.
 * </P>
 * <P>
 * Protocol bindings define how each protocol is linked or bound to another. For
 * example IP protocol is bound to Ethernet frame using etherenet's protocol
 * field when the value of that field is 0x800 in hex. Any number of bindings
 * can be specified for any given protocol. In our example above ip protocol is
 * the <B>source</B> protocol (the one linking to) and Ethernet is the <B>sink</B>
 * protocol (the one being linked to.)
 * </P>
 * 
 * <PRE>
 * 
 * IPv4 = link Ethernet 0x800 HTTP = link TCP sport == 80 || dport == 80
 * 
 * </PRE>
 * 
 * <P>
 * The registry also contains a list of all of the known protocols. You can
 * retrieve them using the getProtocols() method. Not all the protocols may be
 * loaded into the registry for efficiency. But all the protocols are discovered
 * and Protocol object is created to represent, in somecases a place holder, for
 * all of the known protocols. You can use the Protocol.isLoaded() method to
 * find out if a protocol is actually loaded. Before any packets are dissected
 * (parsed), none of the protocols may be loaded. Protocols may be loaded on
 * demand only when needed. They may also be loaded upfront, this behaviour is
 * release dependent and may be altered via global user accessible properties.
 * </P>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 * @since 0.2.4
 */
public final class ProtocolRegistry {

	private final static Log logger = LogFactory.getLog(ProtocolRegistry.class);

	/**
	 * Packet factories for building specific type of packet
	 */
	private static Map<Object, Object> packetFactories =
	    new HashMap<Object, Object>();

	private static List<DefaultProtocolEntry> protocolList =
	    new ArrayList<DefaultProtocolEntry>();

	/**
	 * Repository of
	 */
	private static Map<Class<? extends Header>, ProtocolEntry> protocolsByClass =
	    new HashMap<Class<? extends Header>, ProtocolEntry>();

	private static Map<Protocol, DefaultProtocolEntry> protocolsByProto =
	    new HashMap<Protocol, DefaultProtocolEntry>();

	private static Scanner scanner;

	private static Map<Class<?>, Map<Integer, Protocol>> tr =
	    new HashMap<Class<?>, Map<Integer, Protocol>>();
	
	static {
		init();
	}

	/**
	 * @param protocol
	 * @return
	 */
	public static ProtocolEntry addProtocol(final Protocol protocol) {

		DefaultProtocolEntry e =
		    new DefaultProtocolEntry(protocol, protocolList.size() + 1);

		if (protocol.getClass().isEnum()) {
			fillInFromClassInfo(e, protocol);
		}

		protocolList.add(e);
		protocolsByProto.put(protocol, e);
		protocolsByClass.put(e.getProtocolClass(), e);

		return e;
	}

	/**
	 * @param class1
	 * @param proto
	 * @param value
	 */
	public static void addTranslation(
	    Class<? extends FileCapture<? extends FilePacket>> c, Protocol proto,
	    int value) {

//		System.out.printf("%s=>%d\n", proto.toString(), value);

		Map<Integer, Protocol> map = tr.get(c);
		if (map == null) {
			tr.put(c, map = new HashMap<Integer, Protocol>());
		}

		map.put(value, proto);
	}

	/**
	 * @param protocol
	 */
	private static void fillInFromClassInfo(DefaultProtocolEntry entry,
	    Protocol protocol) {
		Class<? extends Protocol> c = protocol.getClass();
		if (c.isEnum() == false) {
			return;
		}

		Enum<?> constant = null;
		for (Enum<?> e : (Enum[]) c.getEnumConstants()) {
			if (e == protocol) {
				constant = e;
			}
		}

		Package pkg = c.getPackage();
		String suite = c.getSimpleName();
		String name = constant.name();
		String headeri = pkg.getName() + "." + name;
		String headerc = pkg.getName() + "." + name + "Header";
		String headercdc = pkg.getName() + "." + name + "Codec";

		// System.out.printf("suite=%s,\n name=%s,\n headeri=%s,\n headerc=%s\n",
		// suite, name, headeri, headerc);

		entry.setSuite(suite);
		entry.setName(name);
		try {

			entry.setProtocolClass((Class<? extends Header>) Class.forName(headeri));
		} catch (Exception e) {
			logger.warn("missing header: " + headeri);
			logger.debug(e);
		}

		try {
			entry.setCodec((Class<HeaderCodec<? extends Header>>) Class
			    .forName(headercdc));

			HeaderCodec<? extends Header> codec = entry.getCodecClass().newInstance();
			entry.setCodec(codec);

		} catch (Exception e) {
			logger.warn("missing  codec: " + headercdc);
			logger.debug(e);
		}
	}

	/**
	 * @param <T>
	 * @param c
	 * @param defaults
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends PacketFactory<? extends Packet>> T getPacketFactory(
	    Class<T> c, String defaults) {

		final Object r = packetFactories.get(c);
		if (r == null) {
			final T f = new FactoryLoader<T>(logger, "stub", defaults).getFactory();
			packetFactories.put(c, f);

			return f;
		}

		return (T) r;
	}

	public static DefaultProtocolEntry getProtocolEntry(Protocol protocol) {
		DefaultProtocolEntry e = protocolsByProto.get(protocol);
		if (e == null) {
			e = (DefaultProtocolEntry) addProtocol(protocol);
		}

		return e;
	}

	/**
	 * Packet buffer scanner for headers
	 * 
	 * @return
	 */
	public static Scanner getScanner(String defaults) {
		if (scanner == null) {
			scanner =
			    new FactoryLoader<Scanner>(logger, "stub", defaults).getFactory();
		}

		return scanner;
	}

	/**
	 * Uses factory loader to initialize protocols and protocol translators
	 */
	public static void init() {

		/*
		 * Load DLT translations
		 */
		new FactoryLoader<Initializer>(logger,
		    "org.jnetstream.protocol.file.tranlations",
		    "org.jnetstream.protocol.file.FileTranslations").getFactory().init();

		new FactoryLoader<Initializer>(logger,
		    "org.jnetstream.protocol.initializer",
		    "org.jnetstream.protocol.ProtocolInitializer").getFactory().init();

	}

	/**
	 * @param binding
	 * @return
	 */
	public static ProtocolBinding loadProtocolBinding(ProtocolBinding binding) {

		Protocol source = binding.getSource();
		Protocol sink = binding.getSink();

		ProtocolEntry se = getProtocolEntry(sink);
		getProtocolEntry(source); // Make sure its loaded

		se.addBinding(binding);

		return binding;
	}

	/**
	 * @param c
	 * @return
	 */
	public static ProtocolEntry lookup(Class<? extends Header> c) {
		return protocolsByClass.get(c);
	}

	/**
	 * @param protocol
	 * @return
	 */
	public static ProtocolEntry lookup(Protocol protocol) {
		return protocolsByProto.get(protocol);
	}

	/**
	 * @param c
	 * @param raw
	 * @return
	 */
	public static Protocol translate(
	    Class<? extends FileCapture<? extends FilePacket>> c, int raw) {

		Map<Integer, Protocol> map = tr.get(c);
		if (map == null) {
			return null;
		}

		Protocol proto = map.get(raw);

		return proto;
	}

	/**
	 * @param c
	 * @param p
	 * @return
	 */
	public static int translate(
	    Class<? extends FileCapture<? extends FilePacket>> c, Protocol p) {

		Map<Integer, Protocol> map = tr.get(c);
		if (map == null) {
			return -1;
		}

		for (Entry<Integer, Protocol> e : map.entrySet()) {
			if (p == e.getValue()) {
				return e.getKey();
			}
		}

		return -1;
	}
}
