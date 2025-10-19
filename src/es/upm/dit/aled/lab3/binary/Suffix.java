package es.upm.dit.aled.lab3.binary;

import es.upm.dit.aled.lab3.FASTAReader;

/**
 * Represents a suffix in the FASTA data by storing its starting index. This
 * class is designed to be sorted by an external Comparator that uses the actual
 * sequence data.
 *
 * @author mmiguel, rgarciacarmona
 */
public class Suffix {
	public final int suffixIndex;

	/**
	 * Creates a new Suffix.
	 * 
	 * @param index The starting position of the suffix in the data array.
	 */
	public Suffix(int index) {
		suffixIndex = index;
	}
	
	public int getIndex() {
		return suffixIndex; 
	}

	/**
	 * Returns the byte stored at (suffixIndex + offset) from the provided
	 * FASTAReader's content, or null if the position is out of the reader's
	 * valid bytes. Returns a Byte object so callers can check for null.
	 *
	 * @param offset  offset inside the suffix (0-based)
	 * @param reader  the FASTAReader that provides the content and validBytes
	 * @return the byte at the requested position as a Byte, or null if out of bounds
	 */
	public Byte charAt(int offset, FASTAReader reader) {
		int pos = suffixIndex + offset;
		if (pos >= reader.getValidBytes())
			return null;
		return reader.getContent()[pos];
	}
}