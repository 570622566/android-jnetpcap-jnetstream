package com.slytechs.utils.collection;

/**
 * Enum structure which reports the result of a seek or a skip using this
 * iterator.
 * 
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public enum SeekResult {
	
	/**
	 * Seek fullfilled the request accurately and iterator has been repositioned. 
	 */
	Fullfilled,
	
	/**
	 * Seek was not fulfilled because the seek operation did not find or was
	 * not able to fullfill the request. The operation is supported but the 
	 * criteria specified did not match and request was not fullfilled.
	 */
	NotFullfilled,

	/**
	 * Seek operation was fullfilled but with uncertainty. Since the underlying
	 * dataset being operated on can be quiet complex, it may be possible to
	 * fullfill the seek request and be uncertain if the request was properly
	 * fullfilled. For example looking for a record header of a record that contains
	 * the exact same header as its data, a record within a record. In such circumstances
	 * it may not be totally confident in the result.
	 */
	FullfilledWithUncertainty,
	
	/**
	 * Requested seek operation is not supported. For example if the iteration
	 * is of underterminate length, possibly infinate, then certain seek operations
	 * may not be able to fulfill their request.
	 */
	IndeterminateLength,
	;

}