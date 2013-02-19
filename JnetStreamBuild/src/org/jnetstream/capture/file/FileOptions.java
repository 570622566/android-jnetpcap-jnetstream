package org.jnetstream.capture.file;

import java.nio.channels.FileChannel.MapMode;
import java.util.Set;

import com.slytechs.utils.collection.IOIterator;

/**
 * Various options for the capture file. These options change the runtime
 * properties of the open capture file and affect performance and functionality
 * of the capture file API.
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 * 
 */
public interface FileOptions {

	/**
	 * Options for changing the memory model used by the implementation.
	 *
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 *
	 */
	public enum MemoryModel {

		/**
		 * The prefetch buffer used is byte array based. A regular java byte
		 * array is allocated to hold contents of the file as its prefetched
		 * into memory. The buffer size for this type of model should be around
		 * the page-size or cluster-size.
		 * 
		 * @see #DirectBuffer
		 * @see #MappedFile
		 */
		ByteArray,

		/**
		 * The prefetch buffer used is direct buffer allocation. This type of
		 * buffer holds its memory outside the java VM and allows maniche native
		 * operations on it. The file contents are prefetched very efficiently
		 * by the underlying OS kernel into the buffer. The buffer size for this
		 * type of model should be larger then {@link #ByteArray},
		 * the default is 512KB.
		 * 
		 * @see #ByteArray
		 * @see #MappedFile
		 */
		DirectBuffer,

		/**
		 * <P>
		 * The prefetch buffer uses memory mapped file buffers. Portions of the
		 * file are mapped by the kernel into physical memory using native OS
		 * functions and the data is directly accessed from the memory mapped
		 * buffers. This is the most efficient method as no data copies are done
		 * as the buffer addresses directly into the kernel buffer space. The
		 * buffer size for this type of model should be as large as possible.
		 * The default is 2MB and can get set as high as is acceptable to the
		 * user. Testing reveils no performance gain on buffers larger them
		 * 130MB, but this could be different on different machines. The reason
		 * for the large buffer size is that memory mapping a file is very time
		 * consuming, the larger the mapped buffer the less frequently it will
		 * have to be done. Memory mapping buffers lower then 512KB will have
		 * detrimental performance as compared to other memory models.
		 * </P>
		 * <P>
		 * <B>Note:</B> there are several outstanding issues with memomry
		 * mapped file buffers in current releases of java. The biggest issue is
		 * that once a buffer has been mapped, it can not be unmapped
		 * predictably from memory (<A
		 * HREF="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4724038">
		 * Sun Bug#4774038</A>. A file that has mapped memory buffer can not be
		 * truncated or enlarged. Therefore parts of the MutableFileCapture API
		 * fail when this mode was used on a file in this or any other session.
		 * Closing of the open file, does not unmap the memory mapped file
		 * buffers and they continue to exist and block functionality, even if
		 * there are no references to them from within the VM. Every attempt is
		 * made by this implementation to unmap the buffers by explicit calls to
		 * GC and cleaner Finalilzers, but there is no guarrantee that the
		 * buffers will be unmapped by these efforts. Therefore the user is
		 * warned to use the memory mapped buffers where appropriated, ie.
		 * read-only operations. The memory mapped model provides increadible
		 * level of performance and should be used where appropriate.
		 * </P>
		 * 
		 * <P>Also note that the mapping file to memory is very intensive and is 
		 * typically beneficial on very large files to pull large chunks of it into memory,
		 * but incases where a small file will be iterated over and over, it may be more
		 * beneficial to change the default model to MappedFile, take the penalty once for
		 * mapping the entire file into memory and then reap the benefits of accessing the
		 * file content very fast. Same goes for the DirectBuffer model.
		 * 
		 * @see #ByteArray
		 * @see #DirectBuffer
		 */
		MappedFile,
		

	}
	
	/**
	 * Changes the current memory model to the one specified. See each model
	 * for detailed description.
	 * 
	 * @param model
	 *   the model to change to
	 *   
	 * @see MemoryModel#ByteArray
	 * @see MemoryModel#DirectBuffer
	 * @see MemoryModel#MappedFile
	 */
	public void setMemoryModel(MemoryModel model);
	
	/**
	 * Gets the current active memory model.
	 * 
	 * @return
	 *   memory model that is currently active
	 *   
	 * @see MemoryModel#ByteArray
	 * @see MemoryModel#DirectBuffer
	 * @see MemoryModel#MappedFile
	 */
	public MemoryModel getMemoryModel();

	/**
	 * Sets the memory mapping mode for the {@link MemoryModel#MappedFile} mode.
	 * If the model is not of the MappedFile type, illegalArgumentException is thrown.
	 * 
	 * @param mode
	 *   mode to set
	 * 
	 * @exception IllegalArgumentException
	 *   thrown if model is not of MappedFile
	 */
	public void setMemoryMapMode(MapMode mode) throws IllegalArgumentException;
	
	public MapMode getMemoryMapMode();
	
	/**
	 * Changes the size of the prefetch buffer for the current MemoryModel.
	 * The buffer size will have direct impact on the performance of the
	 * capture framework within jNetStream. Please note that appropriate
	 * buffer size is greatly different between each of the supported memory
	 * models. The buffer size can be set greater then the actual file size.
	 * This means that the entire file will be buffered at once. The buffer will
	 * be allocated exactly the size of the file and not over-allocated. So its safe
	 * to set huge buffers even on small files, as only the amount of buffer needed
	 * will be allocated.
	 * 
	 * @param size
	 *   new prefetch buffer size
	 * 
	 * @see #ByteArray
	 * @see #DirectBuffer
	 * @see #MappedFile
	 */
	public void setBufferSize(int size);
	
	/**
	 * Returns the current prefetch buffer size.
	 * 
	 * @return
	 *   size of the current buffer
	 */
	public int getBufferSize();

	/**
	 * Various properties that can be applied to capture files.
	 *
	 * @author Mark Bednarczyk
	 * @author Sly Technologies, Inc.
	 *
	 */
	public enum FileProperty {

		/**
		 * Changes the file size artificially for the API. The physical file
		 * size is unaffected, but the API is artificially tricked into thinking
		 * the file size is smaller then the actual. This will cause all
		 * operations on the file act using the new artificial filesize. The
		 * artificial filesize can not be set greater then the actual file size.
		 * An IllegalArgumentException will be thrown if you try to set a
		 * filesize greater then then actual or less then 0.
		 */
		// ArtificialFilesize,
		/**
		 * This will cause a shared FilePacket to be returned by the call to
		 * {@link IOIterator#next} and initialized as if this was a new packet.
		 * This is a more efficient way of iterating throught the capture file,
		 * but no new packet instances are created, the same packet instance is
		 * reused for each iteration and simply reinitialized. The use of
		 * SharedPacketModel avoids creation of the CapturePacket and thus
		 * saving few nanoseconds of the runtime allowing greater performance from
		 * the Capture framework (upto several million packets per second.) At
		 * these speeds even creation of a single Object has large impact on the
		 * overall performance.
		 */
		SharedPacketMode,
		
		/**
		 * Test/simulation mode. This mode only accesses the disk once to read in an
		 * initial buffer using normal algorithms and the current memory model, but from then on the buffer
		 * is reused for all operations as if it was reading from the disk. Normal
		 * position pointer is kept and updated and simulates going through a file, except
		 * the same set of packets is created and returned based on the same buffer. This
		 * model allows testing of fully in memory buffers. Depending on the size of the buffer
		 * a great many packet records can be cached this way and iterated through at full
		 * VM speeds without limitation of the disk IO. Note that {@DeprecatedPacketIterator#seek} methods
		 * use their own separate internal buffers for doing seek operation and will still properly
		 * seek and aquire proper position within the file, but the {@link Capture#hasNext} and
		 * {@link Capture#next} methods will continue to simulate using the cached buffer.
		 * The DeprecatedPacketIterator API should not be used as this will have unspecified effects on the file
		 * and may cause unexpected exceptions to be thrown. 
		 */
		SimulationMode
	};
	
	/**
	 * Sets a new property on the file.
	 * 
	 * @param property
	 *   property to set
	 */
	public void setFileProperty(FileProperty property);
	
	/**
	 * Gets the immutable set of currently set properties.
	 * 
	 * @return
	 *   returns active properties
	 */
	public Set<FileProperty> getFileProperties();

}